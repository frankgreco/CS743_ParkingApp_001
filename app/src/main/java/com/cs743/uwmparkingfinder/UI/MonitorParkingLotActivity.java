/*******************************************************************************
 * File Name:  MonitorParkingActivity.java
 *
 * Description:
 * Handles the monitor parking lot screen activity.
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         13-Nov-2015 Eric Hitt          Original
 ******************************************************************************/
package com.cs743.uwmparkingfinder.UI;

/****************************    Include Files    *****************************/
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.cs743.uwmparkingfinder.Algorithm.Algorithm;
import com.cs743.uwmparkingfinder.GPSLocation.LocationTracker;
import com.cs743.uwmparkingfinder.GPSLocation.ProviderLocationTracker;
import com.cs743.uwmparkingfinder.GPSLocation.*;
import com.cs743.uwmparkingfinder.HTTPManager.HttpManager;
import com.cs743.uwmparkingfinder.HTTPManager.RequestPackage;
import com.cs743.uwmparkingfinder.Parser.JSONParser;
import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Structures.Lot;
import com.cs743.uwmparkingfinder.Structures.ParkingRequest;
import com.cs743.uwmparkingfinder.Structures.SelectedParkingLot;
import com.cs743.uwmparkingfinder.Structures.User;
import com.cs743.uwmparkingfinder.Utility.UTILITY;

/****************************  Class Definitions  *****************************/

/**
 * Create a new monitor parking lot screen activity class
 */
public class MonitorParkingLotActivity extends AppCompatActivity
{
    /*************************  Class Static Variables  ***********************/
    public static final String PREFERENCES_INTENT_DATA = "preferenceData";
    public static final int PARKING_LOT_OPACITY = 100;  ///< Opacity for overlays

    /// Polling timer interval (milliseconds)
    public static final int POLL_TIMER_INTERVAL_MSEC = 1000;

    //location
    ProviderLocationTracker tracker;

    /*************************  Class Member Variables  ***********************/

    private TextView selectedLotNameLabel_;     ///< Selected lot name label
    private TextView spaceRemainingLabel_;      ///< Space remaining label
    private TextView spaceRemainingCount_;      ///< Displays number of spots
    private ImageView monitorParkingMap_;       ///< Displays map of parking lot
    private View unavailableOverlay_;           ///< Unavailable parking spot overlay
    private View availableOverlay_;             ///< Available parking spot overlay
    private Button parkingSpotStatusButton_;    ///< Click button to see ind. spot status

    private SelectedParkingLot selectedLot_;    ///< Selected parking lot
    private int totalSpots_;                    ///< Total number of parking spots available
    private int spotsRemaining_;                ///< Number of parking spots remaining

    //private Timer pollTimer_;                   ///< Poll timer

    private static boolean executeOnce;

    /*************************  Class Public Interface  ***********************/

    /**
     * Creates the monitor parking lot page.
     *
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_parking_lot);

        executeOnce=true;

        //initialize tracker
        tracker = new ProviderLocationTracker(MonitorParkingLotActivity.this, ProviderLocationTracker.ProviderType.GPS);

        // Retrieve screen inputs
        selectedLotNameLabel_ = (TextView)findViewById(R.id.selectedLotNameLabel);
        spaceRemainingLabel_ = (TextView)findViewById(R.id.spaceRemainingLabel);
        spaceRemainingCount_ = (TextView)findViewById(R.id.spaceRemainingCount);
        monitorParkingMap_ = (ImageView)findViewById(R.id.monitorParkingMap);
        unavailableOverlay_ = findViewById(R.id.unavailableOverlay);
        availableOverlay_ = findViewById(R.id.availableOverlay);
        parkingSpotStatusButton_ = (Button)findViewById(R.id.viewIndividualParkingButton);

        // Retrieve intent
        Intent intent = getIntent();
        selectedLot_ =
                (SelectedParkingLot)intent.getSerializableExtra(RecommendParkingActivity.PREFERENCES_INTENT_DATA);

        // Initialize other member variables (default values)
        totalSpots_ = 0;
        spotsRemaining_ = 0;

        // Set parking lot name label
        String uiLotName = getResources().getString(UTILITY.convertDbLotNameToUINameID(selectedLot_.getParkingLotName()));
        selectedLotNameLabel_.setText("Lot:  " + uiLotName);


        // Using SelectedParkingLot data, determine the number of spaces available and in total
        getParkingSpotCount();

        // Determine which parking lot image should be shown on this screen and display
        findAndDisplayParkingLotImage();

        finishOnCreate();
    }


    /**
     * Called when the window needs to be updated.
     *
     * @param hasFocus True if have focus, false otherwise
     */
   /* @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        // Get parking spot status
        getParkingSpotStatus();

        // Refresh screen
        refreshScreen();
    }
*/

    /**
     * Called when user leaves activity.
     */
    @Override
    public void onPause()
    {
        super.onPause();

        // Stop timer
        //pollTimer_.cancel();
        tracker.stop();
        System.out.println("Tracker canceled");
    }
    /**
     * Called when user re-enters activity
     */
   @Override
    public void onResume()
    {
        super.onResume();
        tracker.start();
        // Configure timer
       // pollTimer_ = new Timer();
        //pollTimer_.schedule(new MonitorLotPollTask(), 0, POLL_TIMER_INTERVAL_MSEC);
    }

    /**
     * Called when the user presses the view parking lot status button
     *
     * @param view Current view
     */
    public void viewParkingSpotStatusButton(View view)
    {
        // Open the monitor parking spot activity, passing in parking preference
        Intent intent = new Intent(this, MonitorParkingSpotStatusActivity.class);
        intent.putExtra(RecommendParkingActivity.PREFERENCES_INTENT_DATA, selectedLot_);
        startActivity(intent);
    }

    /************************  Class Private Interface  ***********************/

    /**
     * Makes a call to the back-end server to obtain the total number of parking
     * spots in the lot, and the number of parking spots available.
     *
     * Updates member variable totalSpots_
     */
    private void getParkingSpotCount()
    {
        for(int i=0;i<Session.getCurrentLotList().size();i++) {
            Lot lot=Session.getCurrentLotList().get(i);
            if(lot.getName().equalsIgnoreCase(selectedLot_.getParkingLotName())) {
                totalSpots_=lot.getNumSpaces();
                break;
            }
        }
    }

    /**
     * Makes a call to the back-end server to obtain the number of parking spots
     * available in the lot.
     *
     * Updates member variable spotsRemaining_
     */
    private void getParkingSpotStatus()
    {
        for(int i=0;i<Session.getCurrentLotList().size();i++) {
            Lot lot=Session.getCurrentLotList().get(i);
            if(lot.getName().equalsIgnoreCase(selectedLot_.getParkingLotName())) {
                spotsRemaining_=lot.getSpaces().size();
                break;
            }
        }
    }

    private void finishOnCreate() {
        if (UTILITY.isOnline(getApplicationContext())) {
            RequestPackage p = new RequestPackage();
            p.setMethod("GET");
            p.setUri(UTILITY.UBUNTU_SERVER_URL);
            p.setParam("query", "available");
            new WebserviceCallOne().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, p);
        } else {
            Toast.makeText(getApplicationContext(), "you are not connected to the internet", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Determine what parking lot image should be shown on the screen.
     *
     * Configures activity to display that image once found
     */
    private void findAndDisplayParkingLotImage()
    {
        Resources res = getResources();
        int lotMapImage = R.drawable.lot_unavailable;
        String lotName = res.getString(UTILITY.convertDbLotNameToUINameID(selectedLot_.getParkingLotName()));

        if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59001)))
        {
            lotMapImage = R.drawable.lot_59001;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59002)))
        {
            lotMapImage = R.drawable.lot_59002;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59006)))
        {
            lotMapImage = R.drawable.lot_59006;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59007)))
        {
            lotMapImage = R.drawable.lot_59007;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59008)))
        {
            lotMapImage = R.drawable.lot_59008;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59009)))
        {
            lotMapImage = R.drawable.lot_59009;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59010)))
        {
            lotMapImage = R.drawable.lot_59010;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59012)))
        {
            lotMapImage = R.drawable.lot_59012;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59013)))
        {
            lotMapImage = R.drawable.lot_59013;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59014)))
        {
            lotMapImage = R.drawable.lot_59014;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59015)))
        {
            lotMapImage = R.drawable.lot_59015;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59016)))
        {
            lotMapImage = R.drawable.lot_59016;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59018)))
        {
            lotMapImage = R.drawable.lot_59018;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59019)))
        {
            lotMapImage = R.drawable.lot_59019;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59020)))
        {
            lotMapImage = R.drawable.lot_59020;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59023_B1)))
        {
            lotMapImage = R.drawable.lot_59023_b1;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59023_B2)))
        {
            lotMapImage = R.drawable.lot_59023_b2;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59023_B3)))
        {
            lotMapImage = R.drawable.lot_59023_b3;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59024)))
        {
            lotMapImage = R.drawable.lot_59024;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59026)))
        {
            lotMapImage = R.drawable.lot_59026;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59033_1)))
        {
            lotMapImage = R.drawable.lot_59033_1;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59033_2)))
        {
            lotMapImage = R.drawable.lot_59033_2;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59033_3)))
        {
            lotMapImage = R.drawable.lot_59033_3;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59033_4)))
        {
            lotMapImage = R.drawable.lot_59033_4;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59034)))
        {
            lotMapImage = R.drawable.lot_59034;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59035)))
        {
            lotMapImage = R.drawable.lot_59035;
        }
        else if (lotName.equalsIgnoreCase(res.getString(R.string.LOT_59036)))
        {
            lotMapImage = R.drawable.lot_59036;
        }
        else
        {
            System.out.println("ERROR:  Unknown parking lot " + lotName);
        }

        // Set image
        monitorParkingMap_.setImageResource(lotMapImage);
    }

    /**
     * Update screen.  This includes both the progress bar and spots
     * remaining count.
     */
    private void refreshScreen()
    {
        //
        // Step 1:  Update spot remaining count
        //
        spaceRemainingCount_.setText(Integer.toString(spotsRemaining_));

        //
        // Step 2:  Update parking lot progress bar images
        //

        // Note:  Overlay with transparency inspired by a stack overflow question:
        // http://stackoverflow.com/questions/5211912/android-overlay-a-picture-jpg-with-transparency
        // That example showed how to overlay a transparent image on another.  This design extends
        // that idea by adding an additional overlay, and dynamically change the height of these
        // overlays based on external information (i.e., parking lot availability).

        // Compute required heights for available and unavailable overlays
        // Unavailable overlay height based on percentage of parking spots remaining
        int unavailLayoutHeight =
                (int)(monitorParkingMap_.getMeasuredHeight() * ((float)(totalSpots_ - spotsRemaining_) / (float)totalSpots_));
        int availLayoutHeight = (monitorParkingMap_.getMeasuredHeight() - unavailLayoutHeight);

        // Configure unavailable parking spot overlay
        unavailableOverlay_.setBackgroundColor((PARKING_LOT_OPACITY * 0x10000000) | 0x00FF0000);
        FrameLayout.LayoutParams unavailParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, // Width
                                             unavailLayoutHeight);                  // Height
        unavailParams.gravity = Gravity.BOTTOM;
        unavailableOverlay_.setLayoutParams(unavailParams);


        /** TEST DEBUG CODE */
        /*
        System.out.println("Image Height:  " + monitorParkingMap_.getMeasuredHeight());
        System.out.println("Image Width:  " + monitorParkingMap_.getMeasuredWidth());
        System.out.println("Image Top:  " + monitorParkingMap_.getTop());
        System.out.println("Image Bottom:  " + monitorParkingMap_.getBottom());

        System.out.println("Unavailable Overlay Height:  " + unavailableOverlay_.getMeasuredHeight());
        System.out.println("Unavailable Overlay Width:  " + unavailableOverlay_.getMeasuredWidth());
        System.out.println("Unavailable Overlay Top:  " + unavailableOverlay_.getTop());
        System.out.println("Unavailable Overlay Bottom:  " + unavailableOverlay_.getBottom());
        */
        /** END TEST DEBUG CODE */

        // Configure available parking spot overlay
        availableOverlay_.setBackgroundColor((PARKING_LOT_OPACITY * 0x10000000) | 0x0000FF00);
        FrameLayout.LayoutParams availParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, // Width
                                             availLayoutHeight);                    // Height
        availParams.gravity = Gravity.TOP;
        availableOverlay_.setLayoutParams(availParams);

        // Update views
        unavailableOverlay_.invalidate();
        availableOverlay_.invalidate();
    }

    /**
     * Processes poll timer event.
     *
     * Called by the poll timer thread.
     */
   /* private void processPollTimerEvent()
    {
        // TODO:  Should only perform timer processing if moved sufficient distance?
        // TODO:  What should happen when user is "close" to lot?
        // TODO:  Need to transition to a conclusion activity screen when arrived at lot.
        // Screen changes cannot be done on the timer thread.  Need to be done on main thread.  See
        // http://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Get updated parking spot status
                getParkingSpotStatus();

                // Update screen
                refreshScreen();
            }
        });
    }*/

    /**
     * Poll timer task class
     */
   // class MonitorLotPollTask extends TimerTask
    //{
        /**
         * Poll timer timeout handler
         */
     /*   @Override
        public void run()
        {
            processPollTimerEvent();
        }
    }*/

    private Lot getLot() {
        for (int i=0;i<Session.getCurrentLotList().size();i++){
            Lot lot = Session.getCurrentLotList().get(i);
            if(lot.getName().equalsIgnoreCase(selectedLot_.getParkingLotName())) {
                return lot;
            }
        }
        return null;
    }

    private void updateAvailability() {
        if (UTILITY.isOnline(getApplicationContext())) {
            RequestPackage p = new RequestPackage();
            p.setMethod("GET");
            p.setUri(UTILITY.UBUNTU_SERVER_URL);
            p.setParam("query", "available");
            new WebserviceCallOne().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, p);
        } else {
            Toast.makeText(getApplicationContext(), "you are not connected to the internet", Toast.LENGTH_LONG).show();
        }
    }

    private class WebserviceCallOne extends AsyncTask<RequestPackage, String, List<List<Lot>>> {
        @Override
        protected List<List<Lot>> doInBackground(RequestPackage... params) {

            String content = HttpManager.getData(params[0]);

            return JSONParser.parseLotFeed(content);
        }

        @Override
        protected void onProgressUpdate(String... values) {
        }

        @Override
        protected void onPostExecute(List<List<Lot>> s) {

            if(s != null){
                Session.setCurrentLotList(s.get(UTILITY.AVAILABLE));
                Session.setAllSpacesByLot(s.get(UTILITY.ALL));
            }

            getParkingSpotStatus();

            if(spotsRemaining_>0) {
                refreshScreen();;
            } else {
                tracker.stop();
                String destination = selectedLot_.getDestination();

                //get the current user and the user's preferences
                User curUser = Session.getCurrentUser();
                int disORprice = curUser.getDistORprice();
                boolean outsideAllowed = curUser.isCovered();
                boolean disableParkNeeded = curUser.isHandicap();
                boolean electricParkNeeded = curUser.isElectric();

                //create a new ParkingRequest with the user's preferences
                ParkingRequest request = new ParkingRequest(destination, disORprice, outsideAllowed,
                        disableParkNeeded, electricParkNeeded);

                //use the algorithm to rank the parking spots
                SelectedParkingLot selectedLot = findParkingLot(request);

                //create a new RecommendParkingActivity
                Intent intent = new Intent(MonitorParkingLotActivity.this, RecommendParkingActivity.class);
                intent.putExtra(MonitorParkingLotActivity.PREFERENCES_INTENT_DATA, selectedLot);
                startActivity(intent);
            }

            if(MonitorParkingLotActivity.executeOnce) {
               finishOnCreate();
            }
        }

        private void finishOnCreate() {
            MonitorParkingLotActivity.executeOnce=false;

            //ideally call this method when the user initiates parking
            if(PackageManager.PERMISSION_GRANTED != checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1340);
            }else{
                tracker.start(new LocationTracker.LocationUpdateListener() {
                    @Override
                    public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
                        NumberFormat formatter = new DecimalFormat("#0.00000");
                        //LOG LOCATION UPDATES TO THE CONSOLE FOR DEBUGGING/REFERENCE
                        Log.i("LOCATION UPDATED", tracker.hasLocation() ? ("old: [" + oldLoc.getLatitude() + ", " + oldLoc.getLongitude() + "]") : "no previous location");
                        Log.i("LOCATION UPDATED", "new: [" + newLoc.getLatitude() + ", " + newLoc.getLongitude() + "]\n");

                        Lot l = getLot();

                        //if within 50ft of destination, go to conclusion activity
                        Location curLoc = new Location("");
                        curLoc.setLatitude(newLoc.getLatitude());
                        curLoc.setLongitude(newLoc.getLongitude());
                        if (l != null && curLoc.distanceTo(l.getLocation()) < .015) {
                            tracker.stop();
                            startActivity(new Intent(MonitorParkingLotActivity.this, ConclusionActivity.class));
                        }

                        //get updated information from backend - STORED IN Session.getCurrentLotList();
                        updateAvailability();

                        //DO OTHER STUFF EVERY SO OFTEN
                        //CODE GOES HERE


                    }
                });
            }
        }

        /**
         * Uses the user's preferences to get a recommendation using Algorithm
         * @param request The object holding the user's parking preferences
         * @return A SelectedParkingLot object representing the recommended parking lot
         */
        private SelectedParkingLot findParkingLot(ParkingRequest request)
        {
            // Compute recommended parking lot
            Resources res = getResources();
            Algorithm algorithm = Algorithm.getInstance();
            List<Lot> sortedLots = algorithm.computeRecommendedLots(request);
            if (sortedLots.size() == 0)
            {
                // No lots were found
                return null;
            }
            else
            {
                String reason = res.getString(R.string.LOT_REASON_DIST);
                if (Session.getCurrentUser().getDistORprice() > 50)
                {
                    reason = res.getString(R.string.LOT_REASON_COST);
                }

                return new SelectedParkingLot(sortedLots.get(0).getName(), reason, request.getDestination());
            }
        }

    }

}
