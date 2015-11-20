/*******************************************************************************
 * File Name:  MonitorParkingSpotStatusActivity.java
 *
 * Description:
 * Handles the monitor parking spot status screen activity.
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         13-Nov-2015 Eric Hitt          Original
 ******************************************************************************/
package com.cs743.uwmparkingfinder.UI;

/****************************    Include Files    *****************************/
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.cs743.uwmparkingfinder.Structures.SelectedParkingLot;

import java.util.Timer;
import java.util.TimerTask;

/****************************  Class Definitions  *****************************/

/**
 * Create a new monitor parking spot status screen activity class
 */
public class MonitorParkingSpotStatusActivity extends AppCompatActivity
{
    /*************************  Class Static Variables  ***********************/

    /// Polling timer interval (milliseconds)
    public static final int POLL_TIMER_INTERVAL_MSEC = 1000;

    /*************************  Class Member Variables  ***********************/

    private TextView selectedLotNameLabel_;         ///< Parking lot name label
    private ListView parkingSpotStatusList_;        ///< List of parking spots

    private SelectedParkingLot selectedLot_;        ///< Selected parking lot
    private Timer pollTimer_;                   ///< Poll timer

    /*************************  Class Public Interface  ***********************/

    /**
     * Creates the monitor parking spot status page.
     *
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_parking_spot_status);

        // Retrieve screen inputs
        selectedLotNameLabel_ = (TextView)findViewById(R.id.selectedLotNameLabel);
        parkingSpotStatusList_ = (ListView)findViewById(R.id.parkingSpotStatusList);

        // Retrieve intent
        Intent intent = getIntent();
        selectedLot_ =
                (SelectedParkingLot)intent.getSerializableExtra(RecommendParkingActivity.PREFERENCES_INTENT_DATA);

        // Set parking space name
        selectedLotNameLabel_.setText("Lot:  " + selectedLot_.getParkingLotName());

        // TODO:  Populate list view, get parking lot status (poll timer?)
    }

    /**
     * Called when the window needs to be updated.
     *
     * @param hasFocus True if have focus, false otherwise
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        // Refresh screen
        refreshScreen();
    }

    /**
     * Called when user leaves activity.
     */
    @Override
    public void onPause()
    {
        super.onPause();

        // Stop timer
        pollTimer_.cancel();

        System.out.println("Monitor Parking Spots Poll Timer Canceled");
    }

    /**
     * Called when user re-enters activity
     */
    @Override
    public void onResume()
    {
        super.onResume();

        // Configure timer
        pollTimer_ = new Timer();
        pollTimer_.schedule(new MonitorParkingSpotsPollTask(), 0, POLL_TIMER_INTERVAL_MSEC);
    }

    /************************  Class Private Interface  ***********************/

    /**
     * Retrieves parking spot status.
     */
    private void getParkingSpotStatus()
    {
        // TODO:  IMPLEMENT FUNCTION
    }

    /**
     * Updates screen.
     */
    private void refreshScreen()
    {
        // Get parking spot status
        getParkingSpotStatus();

        // TODO:  Update list view
    }

    /**
     * Processes poll timer event.
     *
     * Called by the poll timer thread.
     */
    private void processPollTimerEvent()
    {
        // TODO:  Should only perform timer processing if moved sufficient distance?
        // Screen changes cannot be done on the timer thread.  Need to be done on main thread.  See
        // http://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Get updated parking spot status
                getParkingSpotStatus();

                // Update screen
                refreshScreen();
            }
        });
    }

    /**
     * Poll timer task class
     */
    class MonitorParkingSpotsPollTask extends TimerTask
    {
        /**
         * Poll timer timeout handler
         */
        @Override
        public void run()
        {
            processPollTimerEvent();
        }
    }
}
