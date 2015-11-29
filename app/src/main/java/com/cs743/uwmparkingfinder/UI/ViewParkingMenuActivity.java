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
import com.cs743.uwmparkingfinder.Utility.UTILITY;

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

        Resources res = getResources();

        // Retrieve screen inputs
        listView_ = (ListView)findViewById(R.id.lotNameList);

        // Create the list of parking lots list view - reuse TOC text view layout
        List<Lot> lotList = Session.getCurrentLotList();
        int numLots = lotList.size();
        String[] lotNames = new String[numLots];
        for (int i = 0; i < numLots; i++)
        {
            lotNames[i] = res.getString(UTILITY.convertDbLotNameToUINameID(lotList.get(i).getName()));
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

                // Convert UI name to DB name
                String dbName = convertUILotNameToDBLotName(itemValue);

                System.out.println("Got item " +itemPosition + ", value " + itemValue + " (" + dbName + ")");

                SelectedParkingLot curLot = new SelectedParkingLot(dbName,"Selected by User","");
                Intent intent = new Intent(ViewParkingMenuActivity.this,MonitorParkingLotActivity.class);
                intent.putExtra(ViewParkingMenuActivity.PREFERENCES_INTENT_DATA,curLot);
                startActivity(intent);
            }
        });
    }

    /************************  Class Private Interface  ***********************/

    private String convertUILotNameToDBLotName(String uiName)
    {
        String dbLotName = "UNKNOWN";
        Resources res = getResources();

        if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59001)))
        {
            dbLotName = "LOT_59001";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59002)))
        {
            dbLotName = "LOT_59002";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59006)))
        {
            dbLotName = "LOT_59006";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59007)))
        {
            dbLotName = "LOT_59007";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59008)))
        {
            dbLotName = "LOT_59008";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59009)))
        {
            dbLotName = "LOT_59009";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59010)))
        {
            dbLotName = "LOT_59010";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59012)))
        {
            dbLotName = "LOT_59012";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59013)))
        {
            dbLotName = "LOT_59013";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59014)))
        {
            dbLotName = "LOT_59014";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59015)))
        {
            dbLotName = "LOT_59015";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59016)))
        {
            dbLotName = "LOT_59016";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59018)))
        {
            dbLotName = "LOT_59018";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59019)))
        {
            dbLotName = "LOT_59019";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59020)))
        {
            dbLotName = "LOT_59020";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59023_B1)))
        {
            dbLotName = "LOT_59023_B1";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59023_B2)))
        {
            dbLotName = "LOT_59023_B2";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59023_B3)))
        {
            dbLotName = "LOT_59023_B3";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59024)))
        {
            dbLotName = "LOT_59024";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59026)))
        {
            dbLotName = "LOT_59026";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59033_1)))
        {
            dbLotName = "LOT_59033_1";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59033_2)))
        {
            dbLotName = "LOT_59033_2";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59033_3)))
        {
            dbLotName = "LOT_59033_3";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59033_4)))
        {
            dbLotName = "LOT_59033_4";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59034)))
        {
            dbLotName = "LOT_59034";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59035)))
        {
            dbLotName = "LOT_59035";
        }
        else if (uiName.equalsIgnoreCase(res.getString(R.string.LOT_59036)))
        {
            dbLotName = "LOT_59036";
        }
        else
        {
            System.out.println("ERROR:  Unknown parking lot " + uiName);
        }

        return dbLotName;
    }
}
