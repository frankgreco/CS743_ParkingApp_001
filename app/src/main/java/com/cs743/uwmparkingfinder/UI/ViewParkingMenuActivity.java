/*******************************************************************************
 * File Name:  ViewParkingMenuActivity.java
 *
 * Description:
 * Handles the view available parking lots screen activity.
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         04-Nov-2015 Eric Hitt          Original
 * 2         22-Nov-2015 Eric Hitt          Use lot names from database
 ******************************************************************************/
package com.cs743.uwmparkingfinder.UI;

/****************************    Include Files    *****************************/
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Structures.Lot;
import com.cs743.uwmparkingfinder.Structures.SelectedParkingLot;

import java.util.List;

/****************************  Class Definitions  *****************************/

/**
 * View parking lots screen activity class
 */
public class ViewParkingMenuActivity extends AppCompatActivity
{
    /*************************  Class Static Variables  ***********************/
    public static final String PREFERENCES_INTENT_DATA = "preferenceData";
    /*************************  Class Member Variables  ***********************/

    private ListView listView_;             // Used to display parking lot names

    /*************************  Class Public Interface  ***********************/

    /**
     * Creates the view parking lots page.
     *
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_parking_menu);

        // Retrieve screen inputs
        listView_ = (ListView)findViewById(R.id.lotNameList);

        // Create the list of parking lots list view - reuse TOC text view layout
        List<Lot> lotList = Session.getCurrentLotList();
        int numLots = lotList.size();
        String[] lotNames = new String[numLots];
        for (int i = 0; i < numLots; i++)
        {
            lotNames[i] = lotList.get(i).getName();
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_toclistview, lotNames);
        listView_.setAdapter(adapter);

        // ListView item click listener (based on androidexample.com)
        listView_.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int itemPosition = position;        // ListView clicked item index
                String itemValue = (String) listView_.getItemAtPosition(position);

                System.out.println("Got item " +itemPosition + ", value " + itemValue);

                SelectedParkingLot curLot=new SelectedParkingLot(itemValue,"Selected by User");
                Intent intent=new Intent(ViewParkingMenuActivity.this,MonitorParkingLotActivity.class);
                intent.putExtra(ViewParkingMenuActivity.PREFERENCES_INTENT_DATA,curLot);
                startActivity(intent);
            }
        });
    }

    /************************  Class Private Interface  ***********************/
}
