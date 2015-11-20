/*******************************************************************************
 * File Name:  RecommendParkingActivity.java
 *
 * Description:
 * Handles the recommend a parking lot screen activity.
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         12-Nov-2015 Eric Hitt          Original
 ******************************************************************************/
package com.cs743.uwmparkingfinder.UI;

/****************************    Include Files    *****************************/
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cs743.uwmparkingfinder.Structures.SelectedParkingLot;

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

        // Retrieve intent
        Intent intent = getIntent();
        currLotSelection_ = (SelectedParkingLot)intent.getSerializableExtra(PREFERENCES_INTENT_DATA);

        // Set recommendation header text
        recommendationHeader_.setText("I recommend you park at");

        // Set recommended parking lot text
        recommendationBody_.setText(currLotSelection_.getParkingLotName());

        // Set reason header
        reasonHeader_.setText("Reason:");

        // Set reason for selecting parking lot
        reasonBody_.setText(currLotSelection_.getReason());

        // Set confirmation label
        confirmLabel_.setText("Is this recommendation OK?");
    }

    /**
     * Called when the user presses the Yes button
     *
     * @param view Current view
     */
    public void recommendParkingYes(View view)
    {
        // Go to the monitor parking screen
        // Prepare activity showing selected parking lot and reason (pass selectedLot data)
        // Pass preference data to the recommend parking activity and start activity
        Intent intent = new Intent(this, MonitorParkingLotActivity.class);
        intent.putExtra(RecommendParkingActivity.PREFERENCES_INTENT_DATA, currLotSelection_);
        startActivity(intent);
    }

    /**
     * Called when the user presses the No button
     *
     * @param view Current view
     */
    public void recommendParkingNo(View view)
    {
        // TODO:  IMPLEMENT FUNCTION
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setCancelable(false);
        ad.setMessage("No button tapped");
        ad.setButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        ad.show();
    }


    /************************  Class Private Interface  ***********************/
}
