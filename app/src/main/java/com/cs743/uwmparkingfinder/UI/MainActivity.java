/*******************************************************************************
 * File Name:  MainActivity.java
 *
 * Description:
 * Handles the UWM Welcome screen activity.
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         04-Nov-2015 Eric Hitt          Original
 ******************************************************************************/
package com.cs743.uwmparkingfinder.UI;

/****************************    Include Files    *****************************/
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs743.uwmparkingfinder.Algorithm.Algorithm;
import com.cs743.uwmparkingfinder.HTTPManager.HttpManager;
import com.cs743.uwmparkingfinder.HTTPManager.RequestPackage;
import com.cs743.uwmparkingfinder.Parser.JSONParser;
import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Structures.LogItem;
import com.cs743.uwmparkingfinder.Structures.Lot;
import com.cs743.uwmparkingfinder.Utility.UTILITY;
import com.cs743.uwmparkingfinder.Structures.ParkingRequest;
import com.cs743.uwmparkingfinder.Structures.SelectedParkingLot;
import com.cs743.uwmparkingfinder.Structures.User;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/****************************  Class Definitions  *****************************/

/**
 * UWM Welcome screen activity class
 */
public class MainActivity extends AppCompatActivity implements DialogInterface.OnClickListener {
    /*************************  Class Static Variables  ***********************/

    /*************************
     * Class Member Variables
     ***********************/

    private TextView welcomeMsg_;                   // Welcome mesage
    private ListView listView_;                     // Main menu
    private ProgressDialog _p;
    private int LOGS_MIN_THRESHOLD=3;               // minimum number of logs required to try and guess destination
    private int LOGS_MAX_TIME_DIFF=5;               // Number of months from which to consider log entries for guessing destination

    /*************************  Class Public Interface  ***********************/

    /**
     * Creates the welcome screen.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve screen inputs
        welcomeMsg_ = (TextView) findViewById(R.id.welcomeText);
        listView_ = (ListView) findViewById(R.id.tableOfContents);

        // Create the table of contents list view
        Resources res = getResources();
        String[] tocList = res.getStringArray(R.array.tableOfContents);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_toclistview,
                tocList);
        listView_.setAdapter(adapter);


        // Make web service call to get current lot listing
        if (UTILITY.isOnline(getApplicationContext())) {
            //*****FOR EACH CALL TO THE WEBSERVICE, THIS OVERHEAD MUST BE DONE*****
            //1. Create a RequestPackage Object that will hold all of the information
            RequestPackage p = new RequestPackage();
            //2. Set the Request type
            p.setMethod("GET");
            //3. Set the URL
            p.setUri(UTILITY.UBUNTU_SERVER_URL);
            //4. Set all of the parameters
            p.setParam("query", "available");
            //5. Make a call to a private class extending AsyncTask which will run off of the main thread.
            new WebserviceCallOne().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, p);
        } else {
            Toast.makeText(getApplicationContext(), "you are not connected to the internet", Toast.LENGTH_LONG).show();
        }

        // ListView item click listener (based on androidexample.com)
        listView_.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;        // ListView clicked item index
                String itemValue = (String) listView_.getItemAtPosition(position);
                Resources res = getResources();

                if (itemValue.equalsIgnoreCase(res.getString(R.string.tocFindParking))) {
                    // Pressed the find parking menu item
                    processFindParkingSelection();
                } else if (itemValue.equalsIgnoreCase(res.getString(R.string.tocViewParking))) {
                    // Pressed the view parking menu item
                    processViewParkingSelection();
                } else if (itemValue.equalsIgnoreCase(res.getString(R.string.tocEditPrefs))) {
                    // Pressed the edit preferences menu item
                    processEditPreferencesSelection();
                } else if (itemValue.equalsIgnoreCase(res.getString(R.string.tocExit))) {
                    // Exit the application
                    confirmLogOff();
                } else {
                    // Unrecognized click
                    System.err.println("ERROR:  Unrecognized command " + itemValue + "detected!");
                }
            }
        });
    }

    private void confirmLogOff(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage("Are you sure you want to leave?");
        alertDialogBuilder.setPositiveButton("Log off", this);
        alertDialogBuilder.setNegativeButton("Go Back", null);
        alertDialogBuilder.create().show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case AlertDialog.BUTTON_POSITIVE:
                startActivity(new Intent(MainActivity.this, SimpleLoginActivity.class));
            break;
        }
    }

    /************************  Class Private Interface  ***********************/

    /**
     * Called when the user selects the find parking menu item
     */
    private void processFindParkingSelection() {
        System.out.println("Processing find parking selection");

       if (UTILITY.isOnline(getApplicationContext())){
            //determine current time using UTC as time zone
            Calendar cal=GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
            //get logs from database and determine destination
            RequestPackage p = new RequestPackage();
            p.setMethod("GET");
            p.setUri(UTILITY.UBUNTU_SERVER_URL);
            p.setParam("query", "log");
            User user = Session.getCurrentUser();
            p.setParam("username", user.getUsername());
            p.setParam("day",String.valueOf(getDayOfWeekIndex(cal.get(Calendar.DAY_OF_WEEK))));
            new getLogItemServiceCall(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, p);
        } else {
            Toast.makeText(getApplicationContext(), "you are not connected to the internet", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Returns the index of the day of week that matches the MySQL indexes for the days of the week.
     * @param day the index of the day of week in a GregorianCalendar
     * @return an integer representing the day of week using MySQL indexing for days of week.
     */
    public int getDayOfWeekIndex(int day){
        switch(day){
            case 2:
                return 0;
            case 3:
                return 1;
            case 4:
                return 2;
            case 5:
                return 3;
            case 6:
                return 4;
            case 7:
                return 5;
            case 1:
                return 6;
            default:
                return -1;
        }
    }

    /**
     * Called when the user selects the view parking menu item
     */
    private void processViewParkingSelection() {
        System.out.println("Processing view parking selection");

        // Create a new Intent for the ViewParkingMenuActivity
        Intent intent = new Intent(this, ViewParkingMenuActivity.class);

        // Go to the view parking menu screen
        startActivity(intent);
    }

    /**
     * Called when the user selects the edit preferences menu item
     */
    private void processEditPreferencesSelection() {
        System.out.println("Processing edit preferences selection");

        // Go to the edit preferences page
        //Intent intent = new Intent(this, EditPreferencesActivity.class);
        //startActivity(intent);

        startActivity(new Intent(this, Preferences.class));
    }

    public ProgressDialog get_p() {
        return _p;
    }

    public void set_p(ProgressDialog _p) {
        this._p = _p;
    }

    @Override
    public void onBackPressed() {
        //log off
        confirmLogOff();
    }

    /**
     * Webservice call class, used to get current lots available
     */
    private class WebserviceCallOne extends AsyncTask<RequestPackage, String, List<List<Lot>>> {
        @Override
        protected List<List<Lot>> doInBackground(RequestPackage... params) {

            String content = HttpManager.getData(params[0]);

            return JSONParser.parseLotFeed(content);
        }

        @Override
        protected void onPostExecute(List<List<Lot>> s) {
            if (s != null) {
                if (s != null) {
                    Session.setCurrentLotList(s.get(UTILITY.AVAILABLE));
                    Session.setAllSpacesByLot(s.get(UTILITY.ALL));
                }
            } else {
                System.out.println("No rows available!!!");
            }
        }
    }

    /**
     * Gets the list of log entries form the database and uses the list to determine a destination
     */
    private class getLogItemServiceCall extends AsyncTask<RequestPackage,String,List<LogItem>> {
        private Activity activity;
        private String destBuilding;

        getLogItemServiceCall(Activity act) {
            activity=act;
            destBuilding=null;
        }

        @Override
        protected List<LogItem> doInBackground(RequestPackage... params) {
            String content = HttpManager.getData(params[0]);
            return JSONParser.parseLogFeed(content);
        }
        @Override
        protected void onPostExecute(List<LogItem> logs) {
            if (logs !=null) {
                //set currentLog list
                Session.setCurrentLog(logs);

                //see if there is a destination in these log items
                destBuilding=getLikelyDestination(logs);

                //if no destination can be determined, go to SelectParkingOptionsActivity
                if(destBuilding==null) {
                    doNoDestinationFound();
                } else {
                    //confirm destination with user
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder
                            .setTitle("Destination")
                            .setMessage("You have previously gone to "+destBuilding+" around this time. Are you going to "+destBuilding+"?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //if guess is right, get a recommendation using the guessed destination
                                    doGuessedRight();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //if the user is not going to the guessed destination, let user select destination
                                    doNoDestinationFound();
                                }
                            })
                            .show();
                }
            } else {
                //if there are no logs, let the user select a destination
                doNoDestinationFound();
            }
        }

        /**
         * Open a new SelectParkingOptionsActivity so the user can select a destination
         */
        private void doNoDestinationFound() {
            Intent intent = new Intent(activity, SelectParkingOptionsActivity.class);
            startActivity(intent);
        }

        /**
         * Uses the destBuilding to get a recommended parking spot and starts a new RecommendParkingActivity.
         * If destBuilding is null, a new SelectParkingOptionsActivity is started.
         */
        private void doGuessedRight() {
           if(destBuilding!=null) {
               String destination = destBuilding;

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
               Intent intent = new Intent(activity, RecommendParkingActivity.class);
               intent.putExtra(RecommendParkingActivity.PREFERENCES_INTENT_DATA, selectedLot);
               startActivity(intent);
           } else {
               doNoDestinationFound();
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

        /**
         * Determines the building the user has gone to the most around this time of day. Ties are broken
         * arbitrarily.
         * @param logs the log entries that occurr on the current day of teh week.
         * @return A String that is either null or the destination determined from the log entries
         */
        private String getLikelyDestination(List<LogItem> logs) {
            String strBuilding=null;

            //want to only guess if there are enough log entries to show a pattern
            if (logs.size()>LOGS_MIN_THRESHOLD) {
                HashMap<String, Integer> buildings = new HashMap<>();
                int maxNum = 0;
                String destination = "";

                Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
                int curMonths = cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH);

                //get subset of logs with times within 30 minutes of the current time
                List<LogItem> inRange = UTILITY.getCurrentLogWithinRange(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 30);

                for (LogItem item : inRange) {
                    //get the time this log occurred
                    GregorianCalendar time = item.getTime();
                    int logMonths = time.get(Calendar.YEAR) * 12 + time.get(Calendar.MONTH);

                    //only consider logs that occurred in the last few months
                    if (curMonths - logMonths < LOGS_MAX_TIME_DIFF) {
                        //get destination associated with this log entry
                        String word = item.getKeyword();

                        //if the map already contains this building, increment number of occurrences
                        //TODO: remove entries in database that were for testing purposes only, e.g., the ones with HELLO as keyword or test as log
                        if (!word.equalsIgnoreCase("HELLO") && !word.contains(",")) {
                            if (buildings.containsKey(word)) {
                                Integer oldVal = buildings.get(word);
                                Integer newVal = oldVal + 1;
                                buildings.put(word, newVal);

                                //if this building has occurred the most, set it as the best guess
                                if (newVal > maxNum) {
                                    maxNum = newVal;
                                    destination = word;
                                }
                            } else {
                                //otherwise, add building to the map with count of one
                                buildings.put(word, 1);
                                if (1 > maxNum) {
                                    maxNum = 1;
                                    destination = word;
                                }
                            }
                        }
                    }
                }

                //if at least one building occurrs more than once
                if (maxNum>1) {
                    strBuilding=destination;
                }
            }
            return strBuilding;
        }
    }
}
