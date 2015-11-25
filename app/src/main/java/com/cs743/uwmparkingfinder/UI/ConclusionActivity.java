/*******************************************************************************
 * File Name:  ConclusionActivity.java
 *
 * Description:
 * Handles the reached your destination (conclusion) screen activity.
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         25-Nov-2015 Eric Hitt          Original
 ******************************************************************************/
package com.cs743.uwmparkingfinder.UI;

/****************************    Include Files    *****************************/
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/****************************  Class Definitions  *****************************/

/**
 * Conclusion screen activity class
 */
public class ConclusionActivity extends AppCompatActivity
{
    /*************************  Class Static Variables  ***********************/

    /*************************  Class Member Variables  ***********************/

    private TextView reachedDestLabel_;             ///< Reached destination label
    private TextView thankYouFillerLabel_;          ///< Blank space...
    private TextView thankYouLabel_;                ///< Thank you message
    private Button conclusionButton_;               ///< Conclusion button

    /*************************  Class Public Interface  ***********************/

    /**
     * Creates the conclusion screen.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conclusion);

        // Retrieve screen inputs
        reachedDestLabel_ = (TextView)findViewById(R.id.reachedDestLabel);
        thankYouFillerLabel_ = (TextView)findViewById(R.id.thankYouFillerLabel);
        thankYouLabel_ = (TextView)findViewById(R.id.thankYouLabel);
        conclusionButton_ = (Button)findViewById(R.id.conclusionButton);
    }

    /**
     * Called when the user presses the conclusion button
     *
     * @param view Current view
     */
    public void conclusionButtonPressed(View view)
    {
        // Return to the main activity page
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /************************  Class Private Interface  ***********************/
}
