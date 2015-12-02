package com.cs743.uwmparkingfinder.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cs743.uwmparkingfinder.HTTPManager.HttpManager;
import com.cs743.uwmparkingfinder.HTTPManager.RequestPackage;
import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Utility.UTILITY;

/**
 * Update Preferences Activity
 *
 * NOTE: Backend is not updated in this Activity as local use is used. Upon logoff, backend is updated.
 */
public class Preferences extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnShowListener{

    private TextView _topInfo;
    private android.text.Spanned _html;
    private Switch _covered, _handicap, _electric;
    private Button _edit, _save;
    private SeekBar _seekBar;
    private EditText _first, _last, _username, _phone, _email, _password;
    private TextInputLayout _firstLayout, _lastLayout, _usernameLayout, _phoneLayout, _emailLayout, _passwordLayout;
    private String username;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        //Programmatically set UI elements
        _seekBar = (SeekBar) findViewById(R.id.costDistBarEdit);
        _edit = (Button) findViewById(R.id.edit);
        _covered = (Switch) findViewById(R.id.switch1);
        _handicap = (Switch) findViewById(R.id.switch2);
        _electric = (Switch) findViewById(R.id.switch3);
        _electric.setChecked(Session.getCurrentUser().isElectric());
        _topInfo = (TextView) findViewById(R.id.top_info);

        //Set UI element Properties
        _seekBar.setMax(9);
        _seekBar.incrementProgressBy(1);
        _seekBar.setProgress(Session.getCurrentUser().getDistORprice());
        _covered.setChecked(Session.getCurrentUser().isCovered());
        _handicap.setChecked(Session.getCurrentUser().isHandicap());
        updateUserInfo();

        //Register OnClickListeners
        _edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit:

                //Setup Layout stuff
                LayoutInflater li = LayoutInflater.from(Preferences.this);
                View promptsView = li.inflate(R.layout.edit_user_dialog, null);

                //get username in case of username change
                username = Session.getCurrentUser().getUsername();

                //Programmatically set UI elements
                _first = (EditText) promptsView.findViewById(R.id.first);
                _last = (EditText) promptsView.findViewById(R.id.last);
                _username = (EditText) promptsView.findViewById(R.id.username);
                _phone = (EditText) promptsView.findViewById(R.id.phone);
                _email = (EditText) promptsView.findViewById(R.id.email);
                _password = (EditText) promptsView.findViewById(R.id.password);
                _firstLayout = (TextInputLayout) promptsView.findViewById(R.id.text_input_first);
                _lastLayout = (TextInputLayout) promptsView.findViewById(R.id.text_input_last);
                _usernameLayout = (TextInputLayout) promptsView.findViewById(R.id.text_input_username);
                _phoneLayout = (TextInputLayout) promptsView.findViewById(R.id.text_input_phone);
                _emailLayout = (TextInputLayout) promptsView.findViewById(R.id.text_input_email);
                _passwordLayout = (TextInputLayout) promptsView.findViewById(R.id.text_input_password);

                //Set UI element Properties
                _first.setText(Session.getCurrentUser().getFirst());
                _last.setText(Session.getCurrentUser().getLast());
                _username.setText(Session.getCurrentUser().getUsername());
                _phone.setText(Session.getCurrentUser().getPhone());
                _email.setText(Session.getCurrentUser().getEmail());
                _password.setText(Session.getCurrentUser().getPassword());
                _firstLayout.setErrorEnabled(true);
                _lastLayout.setErrorEnabled(true);
                _usernameLayout.setErrorEnabled(true);
                _phoneLayout.setErrorEnabled(true);
                _emailLayout.setErrorEnabled(true);
                _passwordLayout.setErrorEnabled(true);

                // set dialog message
                alertDialogBuilder = new AlertDialog.Builder(Preferences.this);
                alertDialogBuilder
                        .setView(promptsView)
                        .setCancelable(false)
                        .setPositiveButton("Save", null)
                        .setNegativeButton("Go Back", null);

                alertDialog = alertDialogBuilder.create();
                alertDialog.setOnShowListener(this);
                alertDialog.show();

                break;

            case AlertDialog.BUTTON_POSITIVE:
                if(validateEntries()){

                    alertDialog.dismiss();

                    //Update log on backend if username changes
                    if(!username.equals(_username.getText().toString())){
                        saveDialogLocal();
                        executeWebserviceCallDialogUsernameChange();
                    }else if(hasPreferencesChangedDialog()){ //username hasn't changed but other info might have
                        saveDialogLocal();
                        executeWebserviceCallDialogUsernameSame();
                    }

                    //update UI
                    updateUserInfo();
                }
                break;
        }
    }

    private void saveDialogLocal() {
        Session.getCurrentUser().setFirst(_first.getText().toString());
        Session.getCurrentUser().setLast(_last.getText().toString());
        Session.getCurrentUser().setUsername(_username.getText().toString());
        Session.getCurrentUser().setPhone(_phone.getText().toString());
        Session.getCurrentUser().setEmail(_email.getText().toString());
        Session.getCurrentUser().setPassword(_password.getText().toString());
    }

    private void saveActivityLocal(){
        Session.getCurrentUser().setDistORprice(_seekBar.getProgress());
        Session.getCurrentUser().setCovered(_covered.isChecked());
        Session.getCurrentUser().setHandicap(_handicap.isChecked());
        Session.getCurrentUser().setElectric(_electric.isChecked());
    }


    private void executeWebserviceCallDialogUsernameChange(){
        if(UTILITY.isOnline(getApplicationContext())){
            RequestPackage p = new RequestPackage();
            p.setMethod("GET");
            p.setUri(UTILITY.UBUNTU_SERVER_URL);
            p.setParam("query", "change_username");
            p.setParam("old", username);
            p.setParam("new", Session.getCurrentUser().getUsername());
            p.setParam("password", Session.getCurrentUser().getPassword());
            p.setParam("first", Session.getCurrentUser().getFirst());
            p.setParam("last", Session.getCurrentUser().getLast());
            p.setParam("phone", Session.getCurrentUser().getPhone());
            p.setParam("email", Session.getCurrentUser().getEmail());
            new Save().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, p);
        }else{
            //connection offline
        }
    }

    private void executeWebserviceCallDialogUsernameSame(){
        if(UTILITY.isOnline(getApplicationContext())){
            RequestPackage p = new RequestPackage();
            p.setMethod("GET");
            p.setUri(UTILITY.UBUNTU_SERVER_URL);
            p.setParam("query", "update_dialog");
            p.setParam("username", Session.getCurrentUser().getUsername());
            p.setParam("password", Session.getCurrentUser().getPassword());
            p.setParam("first", Session.getCurrentUser().getFirst());
            p.setParam("last", Session.getCurrentUser().getLast());
            p.setParam("phone", Session.getCurrentUser().getPhone());
            p.setParam("email", Session.getCurrentUser().getEmail());
            Log.d("url: ", p.getEncodedParams());
            new Save().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, p);
        }else{
            //connection offline
        }
    }

    private void executeWebserviceCallActivity(){
        if(UTILITY.isOnline(getApplicationContext())){
            RequestPackage p = new RequestPackage();
            p.setMethod("GET");
            p.setUri(UTILITY.UBUNTU_SERVER_URL);
            p.setParam("query", "update_activity");
            p.setParam("username", Session.getCurrentUser().getUsername());
            p.setParam("dist_price", String.valueOf(Session.getCurrentUser().getDistORprice()));
            p.setParam("covered", Session.getCurrentUser().isCovered() ? "true" : "false");
            p.setParam("electric", Session.getCurrentUser().isElectric() ? "true" : "false");
            p.setParam("handicap", Session.getCurrentUser().isHandicap() ? "true" : "false");
            Log.d("url: ", p.getEncodedParams());
            new Save().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, p);
        }else{
            //connection offline
        }
    }

    private boolean hasPreferencesChangedDialog(){
        if(!_first.getText().toString().equalsIgnoreCase(Session.getCurrentUser().getFirst())) return true;
        if(!_last.getText().toString().equalsIgnoreCase(Session.getCurrentUser().getLast())) return true;
        if(!_phone.getText().toString().equalsIgnoreCase(Session.getCurrentUser().getPhone())) return true;
        if(!_email.getText().toString().equalsIgnoreCase(Session.getCurrentUser().getEmail())) return true;
        if(!_password.getText().toString().equalsIgnoreCase(Session.getCurrentUser().getPassword())) return true;
        return false;
    }

    private boolean hasPreferencesChangedActivity(){
        if(_seekBar.getProgress() != Session.getCurrentUser().getDistORprice()) return true;
        if(_covered.isChecked() != Session.getCurrentUser().isCovered()) return true;
        if(_handicap.isChecked() != Session.getCurrentUser().isHandicap()) return true;
        if(_electric.isChecked() != Session.getCurrentUser().isElectric()) return true;
        return false;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        _save = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        _save.setId(AlertDialog.BUTTON_POSITIVE);
        _save.setOnClickListener(this);
    }

    private boolean validateEntries(){
        boolean toReturn = true;
        if(!validateBasicText(_first.getText().toString())){
            _firstLayout.setError("check syntax");
            toReturn = false;
        }
        if(!validateBasicText(_last.getText().toString())){
            _lastLayout.setError("check syntax");
            toReturn = false;
        }
        if(!validateBasicText(_username.getText().toString())){
            _usernameLayout.setError("check syntax");
            toReturn = false;
        }
        if(!validatePhoneNumber(_phone.getText().toString())){
            _phoneLayout.setError("check syntax");
            toReturn = false;
        }
        if(!isValidEmailAddress(_email.getText().toString())){
            _emailLayout.setError("check syntax");
            toReturn = false;
        }
        if(!validateBasicText(_password.getText().toString())){
            _passwordLayout.setError("check syntax");
            toReturn = false;
        }
        return toReturn;
    }

    private boolean validateBasicText(String string){
        if(string.equals("") || containsSpace(string) || string.length() < 1){
            return false;
        }
        return true;
    }

    private boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;

    }

    private boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private boolean containsSpace(String input){
        for(int i = 0; i < input.length(); ++i){
            char c = input.charAt(i);
            if(c == ' ') return true;
        }
        return false;
    }

    private void updateUserInfo(){
        _html = Html.fromHtml("<b>Name </b>" + Session.getCurrentUser().getFirst() + " " + Session.getCurrentUser().getLast() + "<br /><br /><b>Username </b>" + Session.getCurrentUser().getUsername() + "<br /><br /><b>Phone </b>" + Session.getCurrentUser().getPhone() + "<br /><br /" +
                "><b>Email </b>" + Session.getCurrentUser().getEmail());
        _topInfo.setText(_html);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(hasPreferencesChangedActivity()){
            saveActivityLocal();
            executeWebserviceCallActivity();
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
    }
}