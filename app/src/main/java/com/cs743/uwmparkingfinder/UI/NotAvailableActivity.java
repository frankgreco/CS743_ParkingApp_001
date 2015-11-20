/*******************************************************************************
 * File Name:  NotAvailableActivity.java
 *
 * Description:
 * Handles the not available screen activity.
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         08-Nov-2015 Eric Hitt          Original
 ******************************************************************************/
package com.cs743.uwmparkingfinder.UI;

/****************************    Include Files    *****************************/
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/****************************  Class Definitions  *****************************/

/**
 * Feature not available screen activity class
 */
public class NotAvailableActivity extends AppCompatActivity
{

    /*************************  Class Static Variables  ***********************/

    /*************************  Class Member Variables  ***********************/

    /*************************  Class Public Interface  ***********************/

    /**
     * Creates the screen not available page.
     *
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_available);

        // Screen text all captured in the related XML file.
    }
}
