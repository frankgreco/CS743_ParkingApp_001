package com.cs743.uwmparkingfinder.UI;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import com.cs743.uwmparkingfinder.HTTPManager.HttpManager;
import com.cs743.uwmparkingfinder.HTTPManager.RequestPackage;
import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Structures.User;

/**
 * Created by joannasandretto on 11/20/15.
 */
public class EditPreferencesActivity extends AppCompatActivity {
    /*************************  Class Static Variables  ***********************/

    /*************************  Class Member Variables  ***********************/
    private EditText emailAdress_;              ///< email address preferences
    private EditText username_;                 ///< username text box
    private EditText password_;                 ///< password text box
    private EditText firstname_;                ///< firstname text box
    private EditText lastname_;                 ///< lastname text box
    private EditText phone_;                    ///< phone number text box
    private SeekBar disORpriceBar_;             ///< Cost or Distance Preference: 0=prefer closer / 100=prefer cheaper
    private Switch prefOutsideSwitch_;          ///< Outside parking switch
    private Switch handicapSwitch_;             ///< Need handicap parking switch
    private Switch electricSwitch_;             ///< Need electric parking switch
    private final String TRUE="true";
    private final String FALSE="false";
    private final String FAIL="Failed";
    private final String SUCCESS="Succeeded";
    private final String GET="GET";
    private final String DIST_PRICE="dist_price";
    private final String COVERED="covered";
    private final String DB_ADDRESS ="http://ec2-54-152-4-103.compute-1.amazonaws.com/scripts.php";
    //TODO: where should we put the uri for the database?

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_edit_preferences);

        //get the UI elements
        username_=(EditText) findViewById(R.id.usernameTextBox);
        password_=(EditText) findViewById(R.id.passwordTextBox);
        firstname_=(EditText) findViewById(R.id.firstNameTextBox);
        lastname_=(EditText) findViewById(R.id.lastNameTextBox);
        phone_=(EditText) findViewById(R.id.phoneTextBox);
        emailAdress_=(EditText) findViewById(R.id.emailEditTextBox);
        disORpriceBar_=(SeekBar) findViewById(R.id.costDistBarEdit);
        prefOutsideSwitch_=(Switch) findViewById(R.id.outsideSwitchEdit);
        handicapSwitch_=(Switch) findViewById(R.id.disableParkSwitchEdit);
        electricSwitch_=(Switch) findViewById(R.id.electricParkSwitchEdit);

        //set the UI elements to the user's current settings
        User curUser= Session.getCurrentUser();
        username_.setText(curUser.getUsername());
        password_.setText(curUser.getEmail());
        firstname_.setText(curUser.getFirst());
        lastname_.setText(curUser.getLast());
        phone_.setText(curUser.getPhone());
        emailAdress_.setText(curUser.getEmail());
        disORpriceBar_.setProgress(curUser.getDistORprice());
        prefOutsideSwitch_.setChecked(curUser.isCovered());
        //TODO:Add electric and handicapped preferences
        //handicapSwitch_.setChecked(curUser.getDisability());
        //electricSwitch_.setChecked(curUser.getElectric());
    }

    /**
     * Saves the user's preferences to the database.
     * @param view
     */
    public void savePreferences(View view) {
        String uName=username_.getText().toString();
        String pWord=password_.getText().toString();
        String fName=firstname_.getText().toString();
        String lName=lastname_.getText().toString();
        String pNum=phone_.getText().toString();
        String eAddress=emailAdress_.getText().toString();
        int costDist=disORpriceBar_.getProgress();
        boolean handicap=handicapSwitch_.isChecked();
        boolean outside=prefOutsideSwitch_.isChecked();
        boolean electric=electricSwitch_.isChecked();

        //TODO: finish Implementing save to database
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            RequestPackage p=new RequestPackage();
            p.setMethod(GET);
            p.setUri(DB_ADDRESS);
            p.setParam(DIST_PRICE, "" + costDist);
            p.setParam(COVERED,outside?TRUE:FALSE);
            //p.setParam("disabled",handicap?TRUE:FALSE);
            //p.setParam("electric",electric?TRUE:FALSE);
            //p.setParam("query","Whatever the query is named");
            //new savePreferencesToDatabase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,p);
        }
    }

    private class savePreferencesToDatabase extends AsyncTask<RequestPackage,String,String>{
        @Override
        protected String doInBackground(RequestPackage... params) {
            String content= HttpManager.getData(params[0]);
            return content==null? FAIL : SUCCESS;
        }

        @Override
        protected void onProgressUpdate(String... values) {}

        @Override
        protected void onPostExecute(String s){}

        @Override
        protected void onPreExecute(){}
    }
}
