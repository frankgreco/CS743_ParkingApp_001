package com.cs743.uwmparkingfinder.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.cs743.uwmparkingfinder.HTTPManager.HttpManager;
import com.cs743.uwmparkingfinder.HTTPManager.RequestPackage;
import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Utility.UTILITY;

/**
 * Update Preferences Activity
 *
 * NOTE: Backend is not updated in this Activity as local use is used. Upon logoff, backend is updated.
 */
public class Preferences extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, DialogInterface.OnClickListener{

    private TextView _topInfo;
    private android.text.Spanned _html;
    private Switch _covered, _handicap, _electric;
    private Button _edit;
    private SeekBar _seekBar;
    private EditText _first, _last, _username, _phone, _email, _password;
    private ProgressDialog _p;
    private String username;

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
        _seekBar.setOnSeekBarChangeListener(this);
        _covered.setOnCheckedChangeListener(this);
        _handicap.setOnCheckedChangeListener(this);
        _electric.setOnCheckedChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //do nothing
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //do nothing
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Session.getCurrentUser().setDistORprice(seekBar.getProgress());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.switch1:
                Session.getCurrentUser().setCovered(isChecked);
                break;

            case R.id.switch2:
                Session.getCurrentUser().setHandicap(isChecked);
                break;

            case R.id.switch3:
                Session.getCurrentUser().setElectric(isChecked);
                break;
        }
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

                //Set UI element Properties
                _first.setText(Session.getCurrentUser().getFirst());
                _last.setText(Session.getCurrentUser().getLast());
                _username.setText(Session.getCurrentUser().getUsername());
                _phone.setText(Session.getCurrentUser().getPhone());
                _email.setText(Session.getCurrentUser().getEmail());
                _password.setText(Session.getCurrentUser().getPassword());

                // set dialog message
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Preferences.this);
                alertDialogBuilder
                        .setView(promptsView)
                        .setCancelable(false)
                        .setPositiveButton("Save", this)
                        .setNegativeButton("Go Back", this)
                        .show();

                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case AlertDialog.BUTTON_POSITIVE:
                //error checking
                if (!validateEntries()) {
                    //do something
                }else{
                    Session.getCurrentUser().setFirst(_first.getText().toString());
                    Session.getCurrentUser().setLast(_last.getText().toString());
                    Session.getCurrentUser().setUsername(_username.getText().toString());
                    Session.getCurrentUser().setPhone(_phone.getText().toString());
                    Session.getCurrentUser().setEmail(_email.getText().toString());
                    Session.getCurrentUser().setPassword(_password.getText().toString());

                    //Update log on backend if username changes
                    if(!username.equals(Session.getCurrentUser().getUsername())){
                        preBackend();
                    }

                    //update UI
                    updateUserInfo();
                }
                break;
        }
    }

    private void preBackend(){
        if(UTILITY.isOnline(getApplicationContext())){
            RequestPackage p = new RequestPackage();
            p.setMethod("GET");
            p.setUri(UTILITY.UBUNTU_SERVER_URL);
            p.setParam("query", "change");
            p.setParam("old", username);
            p.setParam("new", Session.getCurrentUser().getUsername());
            new UpdateLogUponUsernameChange().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, p);
        }else{
            //connection offline
        }
    }

    private boolean validateEntries(){
        return validateBasicText(new String[]{_username.getText().toString(), _password.getText().toString(),
                _last.getText().toString(), _first.getText().toString()}) && validatePhoneNumber(_phone.getText().toString())
                && isValidEmailAddress(_email.getText().toString());
    }

    private boolean validateBasicText(String[] string){
        for(String item : string){
            if(item.equals("") || containsSpace(item) || item.length() < 1){
                return false;
            }
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

    public ProgressDialog get_p() {
        return _p;
    }

    public void set_p(ProgressDialog _p) {
        this._p = _p;
    }

    private class UpdateLogUponUsernameChange extends AsyncTask<RequestPackage,String,String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String content= HttpManager.getData(params[0]);
            return content==null? "fail" : "success";
        }
        @Override
        protected void onPostExecute(String s){
            UTILITY.controlProgressDialog(false, null, Preferences.this.get_p(), null);
        }
        @Override
        protected void onPreExecute() {
            Preferences.this.set_p(UTILITY.controlProgressDialog(true, Preferences.this, Preferences.this.get_p(), "Saving Information..."));
        }
    }
}
