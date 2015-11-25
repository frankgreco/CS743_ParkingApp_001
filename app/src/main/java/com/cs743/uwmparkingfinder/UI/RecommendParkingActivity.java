/*******************************************************************************
 * File Name:  RecommendParkingActivity.java
 *
 * Description:
 * Handles the recommend a parking lot screen activity.
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         12-Nov-2015 Eric Hitt          Original
 * 2         21-Nov-2015 Eric Hitt          Improved user response handling
 ******************************************************************************/
package com.cs743.uwmparkingfinder.UI;

/****************************    Include Files    *****************************/
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cs743.uwmparkingfinder.Algorithm.Algorithm;
import com.cs743.uwmparkingfinder.Structures.Lot;
import com.cs743.uwmparkingfinder.Structures.SelectedParkingLot;
import com.cs743.uwmparkingfinder.Utility.UTILITY;

import java.util.List;

/****************************  Class Definitions  *****************************/

/**
 * Create a new recommend parking lot screen activity class
 */
public class RecommendParkingActivity extends AppCompatActivity
{
    /*************************  Class Static Variables  ***********************/

    /// Used for passing in parking lot preference data from other activities
    public static final String PREFERENCES_INTENT_DATA = "preferenceData";

    /*************************  Class Member Variables  ***********************/

    private TextView recommendationHeader_;         ///< Header label
    private TextView recommendationBody_;           ///< Where to place recommended lot
    private TextView reasonHeader_;                 ///< Reason header
    private TextView reasonBody_;                   ///< Where to place reason
    private TextView confirmLabel_;                 ///< Lot selection confirmation label
    private Button confirmButtonYes_;               ///< Yes button
    private Button confirmButtonNo_;                ///< No button

    private SelectedParkingLot currLotSelection_;   ///< Current parking lot selection
    private int selectedLotOption_;                 ///< Which lot in list was selected

    /*************************  Class Public Interface  ***********************/

    /**
     * Creates the recommend parking page.
     *
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_parking);

        // Retrieve screen inputs
        recommendationHeader_ = (TextView)findViewById(R.id.parkingRecommendationHeader);
        recommendationBody_ = (TextView)findViewById(R.id.parkingRecommendationName);
        reasonHeader_ = (TextView)findViewById(R.id.parkingRecommendationReasonHeader);
        reasonBody_ = (TextView)findViewById(R.id.parkingRecommendationReason);
        confirmLabel_ = (TextView)findViewById(R.id.parkingRecommendationConfirmLabel);
        confirmButtonYes_ = (Button)findViewById(R.id.parkingRecommendationYesButton);
        confirmButtonNo_ = (Button)findViewById(R.id.parkingRecommendationNoButton);

        Resources res = getResources();

        // Retrieve intent
        Intent intent = getIntent();
        currLotSelection_ = (SelectedParkingLot)intent.getSerializableExtra(PREFERENCES_INTENT_DATA);

        // Always start with 1st choice
        selectedLotOption_ = 0;

        if (currLotSelection_ == null)
        {
            // No lot was found

            // Set apology notice
            recommendationHeader_.setText(res.getString(R.string.LOT_REC_HEADER_NOTFOUND));

            // Set recommended parking lot text (blank)
            recommendationBody_.setText("");

            // Set reason header (reason)
            reasonHeader_.setText(res.getString(R.string.LOT_REASON_NONE));

            // Set reason for selecting parking lot (blank)
            reasonBody_.setText("");

            // Set confirmation label
            confirmLabel_.setText(res.getString(R.string.LOT_REC_CONFIRM_REFINESEARCH));
        }
        else
        {
            // At least 1 lot was found
            displayRecommendedLot();
        }
    }

    /**
     * Called when the user presses the Yes button
     *
     * @param view Current view
     */
    public void recommendParkingYes(View view)
    {
        // Behavior dependent on whether or not a recommendation was made
        if (currLotSelection_ == null)
        {
            // Return to preference activity (previous activity)
            finish();
        }
        else
        {
            // Go to the monitor parking screen
            // Prepare activity showing selected parking lot and reason (pass selectedLot data)
            // Pass preference data to the recommend parking activity and start activity
            Intent intent = new Intent(this, MonitorParkingLotActivity.class);
            intent.putExtra(RecommendParkingActivity.PREFERENCES_INTENT_DATA, currLotSelection_);
            startActivity(intent);
        }
    }

    /**
     * Called when the user presses the No button
     *
     * @param view Current view
     */
    public void recommendParkingNo(View view)
    {
        // Behavior dependent on whether or not a recommendation was made
        if (currLotSelection_ == null)
        {
            // Return to main menu
            // TODO:  Should I be creating a new Intent?
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else
        {
            // Get next best option from algorithm
            // Move on to next parking lot option
            Resources res = getResources();
            selectedLotOption_++;
            List<Lot> lotOptions = Algorithm.getInstance().getLotList();
            if (lotOptions.size() == selectedLotOption_)
            {
                // Out of options!
                // Set apology notice
                recommendationHeader_.setText(res.getString(R.string.LOT_REC_HEADER_OUTOFCHOICES));

                // Set recommended parking lot text (blank)
                recommendationBody_.setText("");

                // Set reason header (reason)
                reasonHeader_.setText(res.getString(R.string.LOT_REASON_NONE));

                // Set reason for selecting parking lot (blank)
                reasonBody_.setText("");

                // Set confirmation label
                confirmLabel_.setText(res.getString(R.string.LOT_REC_CONFIRM_REFINESEARCH));

                // Clear out the selected parking lot structure so that yes returns to preferences,
                // and no takes user to main menu
                currLotSelection_ = null;
            }
            else
            {
                // Provide next best suggestion
                // Save name
                Lot nextLot = lotOptions.get(selectedLotOption_);
                currLotSelection_.setParkingLotName(nextLot.getName());

                // Reason:  Next best choice
                currLotSelection_.setReason(res.getString(R.string.LOT_REASON_NEXT));

                // Refresh screen
                displayRecommendedLot();
            }
        }
    }

    /************************  Class Private Interface  ***********************/

    /**
     * Display a recommended parking lot
     */
    private void displayRecommendedLot()
    {
        Resources res = getResources();

        // Set recommendation header text
        recommendationHeader_.setText(res.getString(R.string.LOT_REC_HEADER_FOUND));

        // Set recommended parking lot text
        String uiLotName = res.getString(UTILITY.convertDbLotNameToUINameID(currLotSelection_.getParkingLotName()));
        recommendationBody_.setText(uiLotName);

        // Set reason header
        reasonHeader_.setText(res.getString(R.string.LOT_REC_REASON_REASON));

        // Set reason for selecting parking lot
        reasonBody_.setText(currLotSelection_.getReason());

        // Set confirmation label
        confirmLabel_.setText(res.getString(R.string.LOT_REC_CONFIRM_OK));
    }
}
