/*******************************************************************************
 * File Name:  SelectParkingOptionsActivity.java
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
 * 4         24-Nov-2015 Eric Hitt          Renamed from CreateNewPreferenceActivity
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
import android.widget.Spinner;
import android.widget.TextView;

import com.cs743.uwmparkingfinder.Algorithm.Algorithm;
import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Structures.Building;
import com.cs743.uwmparkingfinder.Structures.Lot;
import com.cs743.uwmparkingfinder.Structures.ParkingRequest;
import com.cs743.uwmparkingfinder.Structures.SelectedParkingLot;

import java.util.List;

/****************************  Class Definitions  *****************************/

/**
 * Create a new select parking options activity class
 */
public class SelectParkingOptionsActivity extends AppCompatActivity
{
    /*************************  Class Static Variables  ***********************/

    /*************************  Class Member Variables  ***********************/

    private TextView whereToLabel_;             ///< Where to label
    private Spinner whereToSpinner_;            ///< Where to spinner
    private Button findParkingButton_;          ///< Find parking button

    /*************************  Class Public Interface  ***********************/

    /**
     * Creates the select parking options page.
     *
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_parking_options);

        // Retrieve screen inputs
        whereToLabel_ = (TextView)findViewById(R.id.whereToLabel);
        whereToSpinner_ = (Spinner)findViewById(R.id.whereToList);
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

        // Get optimization preference value
        int disORprice = Session.getCurrentUser().getDistORprice();

        // Get outdoor preference
        boolean outsideAllowed = Session.getCurrentUser().isCovered();

        // Get disabled parking value
        boolean disableParkNeeded = Session.getCurrentUser().isHandicap();

        // Get electric car value
        boolean electricParkNeeded = Session.getCurrentUser().isElectric();

        // Package up parking request
        ParkingRequest request = new ParkingRequest(destination,
                                                    disORprice, outsideAllowed,
                                                    disableParkNeeded,
                                                    electricParkNeeded);

        //
        // Step 2:  Have algorithm determine desired parking lot
        //
        SelectedParkingLot selectedLot = findParkingLot(request);

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
     * @param request Parking lot preferences
     *
     * @return Selected parking lot, or null if operation failed (or no lots found)
     */
    private SelectedParkingLot findParkingLot(ParkingRequest request)
    {
        // FOR DEBUGGING PURPOSES ONLY!
        System.out.println("Preference data retrieved:");
        System.out.println("  Destination:  " + request.getDestination());
        System.out.println("  Optimization:  " + request.getOptimization());
        System.out.println("  Outside OK:  " + request.getOutsideParking());
        System.out.println("  Handicap Needed:  " + request.getHandicapRequired());
        System.out.println("  Electric Needed:  " + request.getElectricRequired());

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

            return new SelectedParkingLot(sortedLots.get(0).getName(), reason);
        }
    }
}
