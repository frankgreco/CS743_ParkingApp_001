/*******************************************************************************
 * File Name:  MonitorParkingSpotStatusActivity.java
 *
 * Description:
 * Handles the monitor parking spot status screen activity.
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         13-Nov-2015 Eric Hitt          Original
 ******************************************************************************/
package com.cs743.uwmparkingfinder.UI;

/****************************    Include Files    *****************************/
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs743.uwmparkingfinder.GPSLocation.LocationTracker;
import com.cs743.uwmparkingfinder.GPSLocation.ProviderLocationTracker;
import com.cs743.uwmparkingfinder.HTTPManager.HttpManager;
import com.cs743.uwmparkingfinder.HTTPManager.RequestPackage;
import com.cs743.uwmparkingfinder.Parser.JSONParser;
import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Structures.Lot;
import com.cs743.uwmparkingfinder.Structures.SelectedParkingLot;
import com.cs743.uwmparkingfinder.Utility.UTILITY;
import com.cs743.uwmparkingfinder.Structures.Space;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/****************************  Class Definitions  *****************************/

/**
 * Create a new monitor parking spot status screen activity class
 */
public class MonitorParkingSpotStatusActivity extends AppCompatActivity implements View.OnClickListener
{
    /*************************  Class Static Variables  ***********************/

    /// Polling timer interval (milliseconds)
    public static final int POLL_TIMER_INTERVAL_MSEC = 1000;

    /*************************  Class Member Variables  ***********************/
    private ProviderLocationTracker tracker;
    //private TextView selectedLotNameLabel_;         ///< Parking lot name label
    private ListView parkingSpotStatusList_;        ///< List of parking spots
    private SelectedParkingLot selectedLot_;        ///< Selected parking lot
    private Timer pollTimer_;                   ///< Poll timer
    private SpacesAdapter adapter;
    private static boolean executeOnce;
    private Button refresh;
    private ProgressDialog _p;

    /*************************  Class Public Interface  ***********************/

    /**
     * Creates the monitor parking spot status page.
     *
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_parking_spot_status);

        executeOnce = true;

        //initialize tracker
        tracker = new ProviderLocationTracker(MonitorParkingSpotStatusActivity.this, ProviderLocationTracker.ProviderType.GPS);

        // Retrieve screen inputs
        //selectedLotNameLabel_ = (TextView)findViewById(R.id.selectedLotNameLabel);
        parkingSpotStatusList_ = (ListView)findViewById(R.id.parkingSpotStatusList);

        //button
        refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(this);

        // Retrieve intent
        Intent intent = getIntent();
        selectedLot_ =
                (SelectedParkingLot)intent.getSerializableExtra(RecommendParkingActivity.PREFERENCES_INTENT_DATA);

        getSupportActionBar().setTitle(getResources().getString(UTILITY.convertDbLotNameToUINameID(selectedLot_.getParkingLotName())));

        finishOnCreate();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.refresh:
                MonitorParkingSpotStatusActivity.executeOnce = true;
                finishOnCreate();
            break;
        }
    }

    private void finishOnCreate() {
        if(UTILITY.isOnline(getApplicationContext())){
            RequestPackage p = new RequestPackage();
            p.setMethod("GET");
            p.setUri(UTILITY.UBUNTU_SERVER_URL);
            p.setParam("query", "available");
            new RefreshSpaces().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, p);
        }else{
            //connection offline
        }
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
    }

    /* used https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView to help
        get the custom arrayadapter working*/
    private class SpacesAdapter extends ArrayAdapter<Space> {

        public SpacesAdapter (Context context, int layoutResourceId, Space[] spaces) {
            super(context,layoutResourceId,spaces);
        }

        @Override
        public View getView(int position,View convertView,ViewGroup parent) {
            //if the current view is null, create a new one.
            if(convertView==null){
                convertView= LayoutInflater.from(getContext()).inflate(R.layout.activity_spotlistview,parent,false);
            }

            //fill spot's number and availability
            if (getItem(position)!=null) {
                //get the TextView objects
                TextView number = (TextView) convertView.findViewById(R.id.spotNumber);
                //TextView available = (TextView) convertView.findViewById(R.id.spotAvailable);
                ImageView handicap = (ImageView) convertView.findViewById(R.id.handicap);
                handicap.setVisibility(View.INVISIBLE);
                ImageView electric = (ImageView) convertView.findViewById(R.id.electric);
                electric.setVisibility(View.INVISIBLE);

                //set the name and availability of the space
                number.setText("Space: " + Integer.toString(getItem(position).getNumber()));
                if(getItem(position).isElectric()) electric.setVisibility(View.VISIBLE);
                if(getItem(position).isHandicap()) handicap.setVisibility(View.VISIBLE);
                //available.setText(getItem(position).isAvailable() ? "Available" : "Unavailable");

                //set row background color to green=available and red=unavailable
                if (getItem(position).isAvailable()) {
                    convertView.setBackgroundColor(UTILITY.getColor(this.getContext(), R.color.green));
                } else {
                    convertView.setBackgroundColor(UTILITY.getColor(this.getContext(), R.color.red));
                }
            }
            return convertView;
        }
    }

    private class RefreshSpaces extends AsyncTask<RequestPackage, String, List<List<Lot>>> {
        @Override
        protected List<List<Lot>> doInBackground(RequestPackage... params) {

            String content = HttpManager.getData(params[0]);

            return JSONParser.parseLotFeed(content);
        }

        @Override
        protected void onPreExecute() {
            if(MonitorParkingSpotStatusActivity.executeOnce){
                MonitorParkingSpotStatusActivity.this.set_p(UTILITY.controlProgressDialog(true, MonitorParkingSpotStatusActivity.this, MonitorParkingSpotStatusActivity.this.get_p(), "Updating Availability"));
            }
        }

        @Override
        protected void onPostExecute(List<List<Lot>> s) {

            if(s != null){
                Session.setCurrentLotList(s.get(UTILITY.AVAILABLE));
                Session.setAllSpacesByLot(s.get(UTILITY.ALL));
            }

            if(MonitorParkingSpotStatusActivity.executeOnce){
                finishOnCreate();
            }
        }

        private void finishOnCreate(){
            MonitorParkingSpotStatusActivity.executeOnce = false;
            //get list of spaces in the selected lot
            //find index of spaces
            List<Space> spaces = updateSpaces();

            if (spaces.size() > 0) {

                //create and set custom array adapter
                Space[] toPass = new Space[spaces.size()];
                adapter = new SpacesAdapter(getApplicationContext(), R.layout.activity_spotlistview, spaces.toArray(toPass));
                parkingSpotStatusList_.setAdapter(adapter);

            } else {
                Toast.makeText(getApplicationContext(), "No parking spots found for " + selectedLot_.getParkingLotName(), Toast.LENGTH_LONG).show();
                System.out.println("ERROR: No parking spots found for " + selectedLot_.getParkingLotName());
            }

            if(PackageManager.PERMISSION_GRANTED != checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1340);
            }else{
                tracker.start(new LocationTracker.LocationUpdateListener() {
                    @Override
                    public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {

                        NumberFormat formatter = new DecimalFormat("#0.00000");
                        //LOG LOCATION UPDATES TO THE CONSOLE FOR DEBUGGING/REFERENCE
                        Log.i("LOCATION UPDATED", tracker.hasLocation() ? ("old: [" + oldLoc.getLatitude() + ", " + oldLoc.getLongitude() + "]") : "no previous location");
                        Log.i("LOCATION UPDATED", "new: [" + newLoc.getLatitude() + ", " + newLoc.getLongitude() + "]\n");

                        List<Space> updatedSpaces = new ArrayList<>();
                        //get updated information from backend - STORED IN Session.getCurrentLotList();
                        if (UTILITY.isOnline(getApplicationContext())) {
                            //get list of spaces in the selected lot
                            //this should be a call to the backend, so it will need the if online logic...eventually
                            updatedSpaces = updateSpaces();
                        } else {
                            Toast.makeText(getApplicationContext(), "you are not connected to the internet", Toast.LENGTH_LONG).show();
                        }
                        //clear the current spots from the list
                        //adapter.clear();
                        if (updateSpaces().size() > 0) {
                            Space[] toPass = new Space[updatedSpaces.size()];
                            adapter = new SpacesAdapter(getApplicationContext(), R.layout.activity_spotlistview, updatedSpaces.toArray(toPass));
                            parkingSpotStatusList_.setAdapter(adapter);
                        } else {
                            //I feel like we shoul put a warning...maybe if we have time!
                        }
                    }
                });
            }

            if(MonitorParkingSpotStatusActivity.this.get_p().isShowing()){
                MonitorParkingSpotStatusActivity.this.set_p(UTILITY.controlProgressDialog(false, MonitorParkingSpotStatusActivity.this, MonitorParkingSpotStatusActivity.this.get_p(), null));
            }
        }

        private List<Space> updateSpaces() {
            List<Space> spaces = new ArrayList<>();
            int i;
            for(i = 0; i< Session.getAllSpacesByLot().size(); ++i){
                String lot_name = Session.getAllSpacesByLot().get(i).getName();
                if(lot_name.equalsIgnoreCase(selectedLot_.getParkingLotName())){
                    spaces = Session.getAllSpacesByLot().get(i).getSpaces();
                    break;
                }
            }
            return spaces;
        }
    }

    public ProgressDialog get_p() {
        return _p;
    }

    public void set_p(ProgressDialog _p) {
        this._p = _p;
    }
}
