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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.cs743.uwmparkingfinder.HTTPManager.HttpManager;
import com.cs743.uwmparkingfinder.HTTPManager.RequestPackage;
import com.cs743.uwmparkingfinder.Parser.JSONParser;
import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Structures.Lot;
import com.cs743.uwmparkingfinder.Utility.UTILITY;

import java.util.List;

/****************************  Class Definitions  *****************************/

/**
 * UWM Welcome screen activity class
 */
public class MainActivity extends AppCompatActivity {
    /*************************  Class Static Variables  ***********************/

    /*************************
     * Class Member Variables
     ***********************/

    private TextView welcomeMsg_;                   // Welcome mesage
    private ListView listView_;                     // Main menu
    private ProgressDialog _p;

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
            p.setUri("http://ec2-54-152-4-103.compute-1.amazonaws.com/scripts.php");
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
                    preSave();
                } else {
                    // Unrecognized click
                    System.err.println("ERROR:  Unrecognized command " + itemValue + "detected!");
                }
            }
        });
    }

    private void preSave(){
        if(UTILITY.isOnline(getApplicationContext())){
            RequestPackage p = new RequestPackage();
            p.setMethod("GET");
            p.setUri(UTILITY.UBUNTU_SERVER_URL);
            p.setParam("query", "update");
            p.setParam("username", Session.getCurrentUser().getUsername());
            p.setParam("password", Session.getCurrentUser().getPassword());
            p.setParam("first", Session.getCurrentUser().getFirst());
            p.setParam("last", Session.getCurrentUser().getLast());
            p.setParam("phone", Session.getCurrentUser().getPhone());
            p.setParam("email", Session.getCurrentUser().getEmail());
            p.setParam("dist_price", String.valueOf(Session.getCurrentUser().getDistORprice()));
            p.setParam("covered", Session.getCurrentUser().isCovered() ? "true" : "false");
            p.setParam("handicap", Session.getCurrentUser().isHandicap() ? "true" : "false");
            p.setParam("electric", Session.getCurrentUser().isElectric() ? "true" : "false");
            Log.d("url: ", p.getEncodedParams());
            new Save().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, p);
        }else{
            //connection offline
        }
    }

    /************************  Class Private Interface  ***********************/

    /**
     * Called when the user selects the find parking menu item
     */
    private void processFindParkingSelection() {
        System.out.println("Processing find parking selection");

        // TODO:  Determine if should create a new preference or suggest based on past data
        // Need to create a new preference
        Intent intent = new Intent(this, SelectParkingOptionsActivity.class);
        startActivity(intent);
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
     * Save info before exiting
     */
    private class Save extends AsyncTask<RequestPackage,String,String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String content= HttpManager.getData(params[0]);
            return content==null? "fail" : "success";
        }
        @Override
        protected void onPostExecute(String s){
            UTILITY.controlProgressDialog(false, null, MainActivity.this.get_p(), null);
            startActivity(new Intent(MainActivity.this, SimpleLoginActivity.class));
        }
        @Override
        protected void onPreExecute() {
            MainActivity.this.set_p(UTILITY.controlProgressDialog(true, MainActivity.this, MainActivity.this.get_p(), "Saving....."));
        }
    }
}
