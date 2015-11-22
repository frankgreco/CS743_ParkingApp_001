/*******************************************************************************
 * File Name:  MainActivity.java
 *
 * Description:
 * Handles the UWM Welcome screen activity.
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         04-Nov-2015 Eric Hitt          Original
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
import android.widget.TextView;

/****************************  Class Definitions  *****************************/

/**
 * UWM Welcome screen activity class
 */
public class MainActivity extends AppCompatActivity
{
    /*************************  Class Static Variables  ***********************/

    /*************************  Class Member Variables  ***********************/

    private TextView welcomeMsg_;                   // Welcome mesage
    private ListView listView_;                     // Main menu

    /*************************  Class Public Interface  ***********************/

    /**
     * Creates the welcome screen.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve screen inputs
        welcomeMsg_ = (TextView)findViewById(R.id.welcomeText);
        listView_ = (ListView)findViewById(R.id.tableOfContents);

        // Create the table of contents list view
        Resources res = getResources();
        String[] tocList = res.getStringArray(R.array.tableOfContents);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_toclistview,
                                                        tocList);
        listView_.setAdapter(adapter);

        // ListView item click listener (based on androidexample.com)
        listView_.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int itemPosition = position;        // ListView clicked item index
                String itemValue = (String) listView_.getItemAtPosition(position);
                Resources res = getResources();

                if (itemValue.equalsIgnoreCase(res.getString(R.string.tocFindParking)))
                {
                    // Pressed the find parking menu item
                    processFindParkingSelection();
                }
                else if (itemValue.equalsIgnoreCase(res.getString(R.string.tocViewParking)))
                {
                    // Pressed the view parking menu item
                    processViewParkingSelection();
                }
                else if (itemValue.equalsIgnoreCase(res.getString(R.string.tocEditPrefs)))
                {
                    // Pressed the edit preferences menu item
                    processEditPreferencesSelection();
                }
                else if (itemValue.equalsIgnoreCase(res.getString(R.string.tocExit)))
                {
                    // Exit the application
                    System.exit(0);
                }
                else
                {
                    // Unrecognized click
                    System.err.println("ERROR:  Unrecognized command " + itemValue + "detected!");
                }
            }
        });
    }

    /************************  Class Private Interface  ***********************/

    /**
     * Called when the user selects the find parking menu item
     */
    private void processFindParkingSelection()
    {
        System.out.println("Processing find parking selection");

        // TODO:  Determine if should create a new preference or suggest based on past data
        // Need to create a new preference
        Intent intent = new Intent(this, CreateNewPreferenceActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the user selects the view parking menu item
     */
    private void processViewParkingSelection()
    {
        System.out.println("Processing view parking selection");

        // Create a new Intent for the ViewParkingMenuActivity
        Intent intent = new Intent(this, ViewParkingMenuActivity.class);

        // Go to the view parking menu screen
        startActivity(intent);
    }

    /**
     * Called when the user selects the edit preferences menu item
     */
    private void processEditPreferencesSelection()
    {
        System.out.println("Processing edit preferences selection");

        // Go to the edit preferences page
        Intent intent = new Intent(this, EditPreferencesActivity.class);
        startActivity(intent);
    }
}
