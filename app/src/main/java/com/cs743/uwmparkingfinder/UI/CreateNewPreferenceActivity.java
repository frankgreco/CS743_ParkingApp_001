/*******************************************************************************
 * File Name:  CreateNewPreferenceActivity.java
 *
 * Description:
 * Handles the create a new preference screen activity.
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         08-Nov-2015 Eric Hitt          Original
 * 2         21-Nov-2015 Eric Hitt          Can pass null data to recommend
 *                                          parking activity
 * 3         22-Nov-2015 Eric Hitt          Use building names from database
 ******************************************************************************/
package com.cs743.uwmparkingfinder.UI;

/****************************    Include Files    *****************************/
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cs743.uwmparkingfinder.Algorithm.Algorithm;
import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Structures.Building;
import com.cs743.uwmparkingfinder.Structures.Lot;
import com.cs743.uwmparkingfinder.Structures.ParkingPreferences;
import com.cs743.uwmparkingfinder.Structures.SelectedParkingLot;

import java.util.List;

/****************************  Class Definitions  *****************************/

/**
 * Create a new preference screen activity class
 */
public class CreateNewPreferenceActivity extends AppCompatActivity
{
    /*************************  Class Static Variables  ***********************/

    /*************************  Class Member Variables  ***********************/

    private TextView whereToLabel_;             ///< Where to label
    private Spinner whereToSpinner_;            ///< Where to spinner
    private TextView whatTimeLabel_;            ///< What time label
    private TimePicker whatTimePicker_;         ///< What time picker
    private TextView prefLabelCloser_;          ///< Closer parking preferences label
    private TextView prefLabelCheaper_;         ///< Cheaper parking preferences label
    private SeekBar disORpriceBar_;             ///< Cost or Distance Preference: 0=prefer closer / 100=prefer cheaper
    private TextView prefOutsideLabel_;         ///< Outside preference label
    private Switch prefOutsideSwitch_;          ///< Outside parking switch
    private TextView handicapLabel_;            ///< Need handicap parking label
    private Switch handicapSwitch_;             ///< Need handicap parking switch
    private TextView electricLabel_;            ///< Need electric parking label
    private Switch electricSwitch_;             ///< Need electric parking switch
    private Button findParkingButton_;          ///< Find parking button

    /*************************  Class Public Interface  ***********************/

    /**
     * Creates the create new preference page.
     *
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_preference);

        // Retrieve screen inputs
        whereToLabel_ = (TextView)findViewById(R.id.whereToLabel);
        whereToSpinner_ = (Spinner)findViewById(R.id.whereToList);
        whatTimeLabel_ = (TextView)findViewById(R.id.whatTimeLabel);
        whatTimePicker_ = (TimePicker)findViewById(R.id.whatTimePicker);
        prefLabelCloser_ = (TextView)findViewById(R.id.prefLabelCloser);
        prefLabelCheaper_ = (TextView) findViewById(R.id.prefLabelCheaper);
        disORpriceBar_ = (SeekBar) findViewById(R.id.costDistBar);
        prefOutsideLabel_ = (TextView)findViewById(R.id.badWeatherLabel);
        prefOutsideSwitch_= (Switch)findViewById(R.id.outsideSwitch);
        handicapLabel_ = (TextView)findViewById(R.id.disableParkLabel);
        handicapSwitch_ = (Switch)findViewById(R.id.disableParkSwitch);
        electricLabel_ = (TextView)findViewById(R.id.electricParkLabel);
        electricSwitch_ = (Switch)findViewById(R.id.electricParkSwitch);
        findParkingButton_ = (Button)findViewById(R.id.findParkingButton);

        // Populate the Where To? spinner - reuse TOC text view layout
        List<Building> buildingList = Session.getCurrentBuildings();
        int numBuildings = buildingList.size();
        String[] buildingNames = new String[numBuildings];
        for (int i = 0; i < numBuildings; i++)
        {
            buildingNames[i] = buildingList.get(i).getName();
        }

        ArrayAdapter whereToAdapter = new ArrayAdapter<String>(this,
                                                               R.layout.activity_toclistview,
                                                               buildingNames);

        // Specify dropdown layout
        whereToAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        // Apply adapter to spinner
        whereToSpinner_.setAdapter(whereToAdapter);
    }

    /**
     * Called when user presses the Find Parking button
     *
     * @param view Current view
     */
    public void findParking(View view)
    {
        //
        // Step 1:  Retrieve inputs from activity
        //

        // Read destination spinner
        String destination = whereToSpinner_.getSelectedItem().toString();

        // Read destination time
        int destHour = whatTimePicker_.getHour();
        int destMin = whatTimePicker_.getMinute();

        // Read optimization preference value
        int disORprice = disORpriceBar_.getProgress();

        // Read outdoor preference
        boolean outsideAllowed = prefOutsideSwitch_.isChecked();

        // Read disabled parking value
        boolean disableParkNeeded = handicapSwitch_.isChecked();

        // Read electric car value
        boolean electricParkNeeded = electricSwitch_.isChecked();

        // Package up preferences
        ParkingPreferences preferences = new ParkingPreferences(destination, destHour, destMin,
                                                                disORprice, outsideAllowed,
                                                                disableParkNeeded,
                                                                electricParkNeeded);

        //
        // Step 2:  Have algorithm determine desired parking lot
        //
        SelectedParkingLot selectedLot = findParkingLot(preferences);

        // At this point, it is possible that selectedLot is null.  This will be checked as
        // part of the RecommendParkingActivity.
        // Prepare activity showing selected parking lot and reason (pass selectedLot data)
        // Pass preference data to the recommend parking activity and start activity
        Intent intent = new Intent(this, RecommendParkingActivity.class);
        intent.putExtra(RecommendParkingActivity.PREFERENCES_INTENT_DATA, selectedLot);
        startActivity(intent);
    }

    /************************  Class Private Interface  ***********************/

    /**
     * Request to have system determine ideal parking lot based on preferences
     *
     * @param preferences Parking lot preferences
     *
     * @return Selected parking lot, or null if operation failed (or no lots found)
     */
    private SelectedParkingLot findParkingLot(ParkingPreferences preferences)
    {
        // FOR DEBUGGING PURPOSES ONLY!
        System.out.println("Preference data retrieved:");
        System.out.println("  Destination:  " + preferences.getDestination());
        System.out.println("  Time:  " + preferences.getDestinationTimeHours() + ":" +
                           preferences.getDestinationTimeMinutes());
        System.out.println("  Optimization:  " + preferences.getOptimization());
        System.out.println("  Outside OK:  " + preferences.getOutsideParking());
        System.out.println("  Handicap Needed:  " + preferences.getHandicapRequired());
        System.out.println("  Electric Needed:  " + preferences.getElectricRequired());

        // Compute recommended parking lot
        Resources res = getResources();
        Algorithm algorithm = Algorithm.getInstance();
        List<Lot> sortedLots = algorithm.computeRecommendedLots(preferences);
        if (sortedLots.size() == 0)
        {
            // No lots were found
            return null;
        }
        else
        {
            String reason = res.getString(R.string.LOT_REASON_DIST);
            if (disORpriceBar_.getProgress() > 50)
            {
                reason = res.getString(R.string.LOT_REASON_COST);
            }

            return new SelectedParkingLot(sortedLots.get(0).getName(), reason);
        }
    }
}
