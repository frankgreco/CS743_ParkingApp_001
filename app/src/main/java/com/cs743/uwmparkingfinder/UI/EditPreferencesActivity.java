package com.cs743.uwmparkingfinder.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.cs743.uwmparkingfinder.HTTPManager.HttpManager;
import com.cs743.uwmparkingfinder.HTTPManager.RequestPackage;
import com.cs743.uwmparkingfinder.HTTPManager.UTILITY;
import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Structures.User;

/**
 * Created by joannasandretto on 11/20/15.
 */
public class EditPreferencesActivity extends AppCompatActivity implements View.OnClickListener{
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
    private Button savePreferences;
    private final String TRUE="true";
    private final String FALSE="false";
    private final String FAIL="Failed";
    private final String SUCCESS="Succeeded";
    private final String GET="GET";
    private final String DIST_PRICE="dist_price";
    private final String COVERED="covered";
    //create a Progress Dialog to be used throughout Activity
    private ProgressDialog p;

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
        savePreferences=(Button) findViewById(R.id.savePreferencesButton);

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
        handicapSwitch_.setChecked(curUser.isHandicap());
        electricSwitch_.setChecked(curUser.isElectric());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.savePreferencesButton:
                savePreferences(v);
            break;
        }
    }

    /**
     * Check to see whether there is an internet connection or not.
     * @return whether there is an internet connection
     */
    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private static ProgressDialog controlProgressDialog(boolean show, Context context, ProgressDialog p, String message){
        if(show){
            p = new ProgressDialog(context);
            p.setMessage(message);
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }
        else{
            p.dismiss();
        }

        return p;
    }


    public ProgressDialog getP() {
        return p;
    }

    public void setP(ProgressDialog p) {
        this.p = p;
    }

    /**
     * Saves the user's preferences to the database.
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

        //Update server
        if (isOnline()) {
            RequestPackage p=new RequestPackage();
            p.setMethod(GET);
            p.setParam("query","preferences");
            p.setUri(UTILITY.UBUNTU_SERVER_URL);
            p.setParam(DIST_PRICE, "" + costDist);
            p.setParam(COVERED,outside?TRUE : FALSE);
            p.setParam("disabled",handicap?TRUE:FALSE);
            p.setParam("electric",electric?TRUE:FALSE);
            new savePreferencesToDatabase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,p);
        }else{
            Toast.makeText(this, "you are not connected to the internet", Toast.LENGTH_LONG).show();
        }
        //Update local
        User toUpdate = Session.getCurrentUser();
        toUpdate.setUsername(uName);
        toUpdate.setPassword(pWord);
        toUpdate.setFirst(fName);
        toUpdate.setLast(lName);
        toUpdate.setPhone(pNum);
        toUpdate.setEmail(eAddress);
        toUpdate.setDistORprice(costDist);
        toUpdate.setHandicap(handicap);
        toUpdate.setCovered(outside);
        toUpdate.setElectric(electric);
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
        protected void onPostExecute(String s){
            EditPreferencesActivity.controlProgressDialog(false, null, EditPreferencesActivity.this.getP(), null);
        }

        @Override
        protected void onPreExecute(){
            EditPreferencesActivity.this.setP(EditPreferencesActivity.controlProgressDialog(true, EditPreferencesActivity.this, p, "Saving Information..."));
        }
    }
}
