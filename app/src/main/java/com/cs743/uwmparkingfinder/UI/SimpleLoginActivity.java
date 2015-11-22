package com.cs743.uwmparkingfinder.UI;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cs743.uwmparkingfinder.HTTPManager.HttpManager;
import com.cs743.uwmparkingfinder.HTTPManager.RequestPackage;
import com.cs743.uwmparkingfinder.HTTPManager.UTILITY;
import com.cs743.uwmparkingfinder.Parser.JSONParser;
import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Structures.Building;
import com.cs743.uwmparkingfinder.Structures.Lot;
import com.cs743.uwmparkingfinder.Structures.User;

import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SimpleLoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText userName;
    private EditText password;
    private Button login;
    private TextView title;

    //create a Progress Dialog to be used throughout Activity
    private ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_login);

        userName = (EditText) findViewById(R.id.userName);
        userName.setHint("Username");
        userName.requestFocus();

        password = (EditText) findViewById(R.id.password);
        password.setHint("Password");

        login = (Button) findViewById(R.id.login);
        title = (TextView) findViewById(R.id.title);

        title.setTextSize(24);

        login.setOnClickListener(this);
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.login:
                if(getUserName().getText().toString().equals("") || getUserName().getText().toString().equals(" ")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SimpleLoginActivity.this);
                    alertDialogBuilder.setMessage("Enter Your User Name");
                    alertDialogBuilder.setPositiveButton("Got it", null);
                    alertDialogBuilder.create().show();
                }else{
                    //webservice
                    if (isOnline()) {
                        RequestPackage p = new RequestPackage();
                        p.setMethod("GET");
                        p.setUri(UTILITY.UBUNTU_SERVER_URL);
                        p.setParam("query", "user");
                        p.setParam("username", getUserName().getText().toString());
                        new WebserviceCallOne().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, p);
                    } else {
                        Toast.makeText(getApplicationContext(), "you are not connected to the internet", Toast.LENGTH_LONG).show();
                    }
                }
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

    public EditText getPassword() {
        return password;
    }

    public EditText getUserName() {
        return userName;
    }

    public void getOtherInfoFromWebservice(){
        if (isOnline()) {
            RequestPackage p = new RequestPackage();
            p.setMethod("GET");
            p.setUri(UTILITY.UBUNTU_SERVER_URL);
            p.setParam("query", "buildings");
            new WebserviceCallTwo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, p);
        } else {
            Toast.makeText(getApplicationContext(), "you are not connected to the internet", Toast.LENGTH_LONG).show();
        }
    }

    private class WebserviceCallOne extends AsyncTask<RequestPackage, String, User> {
        @Override
        protected User doInBackground(RequestPackage... params) {

            String content = HttpManager.getData(params[0]);

            return JSONParser.parseUserFeed(content);
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }

        @Override
        protected void onPostExecute(User s) {

            Session.setCurrentUser(s);

            //if null, error stacktrace will print to the log. This is expected!!
            if(Session.getCurrentUser() == null){ //username was incorrect
                SimpleLoginActivity.controlProgressDialog(false, null, SimpleLoginActivity.this.getP(), null);
                Toast.makeText(getApplicationContext(), "That username does not exist", Toast.LENGTH_LONG).show();
            }else{ //check password
                if(getPassword().getText().toString().equals(s.getPassword())){ //passwords match
                    getOtherInfoFromWebservice();
                    //start intent
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    SimpleLoginActivity.controlProgressDialog(false, null, SimpleLoginActivity.this.getP(), null);
                }else{
                    SimpleLoginActivity.controlProgressDialog(false, null, SimpleLoginActivity.this.getP(), null);
                    Toast.makeText(getApplicationContext(), "password is incorrect", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            SimpleLoginActivity.this.setP(SimpleLoginActivity.controlProgressDialog(true, SimpleLoginActivity.this, p, "Getting Information..."));
        }
    }

    private class WebserviceCallTwo extends AsyncTask<RequestPackage, String, List<Building>> {
        @Override
        protected List<Building> doInBackground(RequestPackage... params) {

            String content = HttpManager.getData(params[0]);

            return JSONParser.parseBuildingFeed(content);
        }

        @Override
        protected void onProgressUpdate(String... values) {
        }

        @Override
        protected void onPostExecute(List<Building> s) {
            Session.setCurrentBuildings(s);
        }

        @Override
        protected void onPreExecute() {
        }
    }


}
