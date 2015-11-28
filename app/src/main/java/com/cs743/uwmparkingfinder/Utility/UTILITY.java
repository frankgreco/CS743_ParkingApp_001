package com.cs743.uwmparkingfinder.Utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Structures.LogItem;
import com.cs743.uwmparkingfinder.UI.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by fbgrecojr on 11/21/15.
 */
public class UTILITY {

    public static final String UBUNTU_SERVER_URL ="http://ec2-54-152-4-103.compute-1.amazonaws.com/scripts.php";
    public static final int AVAILABLE = 0;
    public static final int ALL = 1;


    /**
     * Check to see whether there is an internet connection or not.
     * @return whether there is an internet connection
     */
    public static boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     *http://stackoverflow.com/questions/31590714/getcolorint-id-deprecated-on-android-6-0-marshmallow-api-23
     * @param context
     * @param id
     * @return
     */
    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    /**
     * Extract a subset of the current log.
     * @param hour the hour of the day in 24 hour format
     * @param minutes the minute of the hour
     * @param bufferInMinutes buffer for time
     * @return subset of the current log
     */
    public static List<LogItem> getCurrentLogWithinRange(int hour, int minutes, int bufferInMinutes){
        if(Session.getCurrentLog() == null) return null;
        List<LogItem> subset = new ArrayList<>();
        GregorianCalendar c;
        for(LogItem item : Session.getCurrentLog()){
            c = item.getTime();
            int dayMinutesLog = (60 * c.get(Calendar.HOUR_OF_DAY)) + c.get(Calendar.MINUTE);
            int dayMinutesParam = (60 * hour) + minutes;
            if((dayMinutesLog >= (dayMinutesParam - bufferInMinutes)) && (dayMinutesLog <= (dayMinutesParam + bufferInMinutes))){
                subset.add(item);
            }
        }
        return subset;
    }

    /**
     * Converts the lot name from the database to a user-friendly name
     *
     * Ex:  LOT_59001 --> Garland Hall
     *
     * @param lotNameDb Lot name from database
     *
     * @return Resource ID of user-friendly lot name
     */
    public static int convertDbLotNameToUINameID(String lotNameDb)
    {
        int uiNameID = 0;

        if (lotNameDb.equalsIgnoreCase("LOT_59001"))
        {
            uiNameID = R.string.LOT_59001;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59002"))
        {
            uiNameID = R.string.LOT_59002;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59006"))
        {
            uiNameID = R.string.LOT_59006;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59007"))
        {
            uiNameID = R.string.LOT_59007;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59008"))
        {
            uiNameID = R.string.LOT_59008;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59009"))
        {
            uiNameID = R.string.LOT_59009;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59010"))
        {
            uiNameID = R.string.LOT_59010;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59012"))
        {
            uiNameID = R.string.LOT_59012;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59013"))
        {
            uiNameID = R.string.LOT_59013;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59014"))
        {
            uiNameID = R.string.LOT_59014;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59015"))
        {
            uiNameID = R.string.LOT_59015;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59016"))
        {
            uiNameID = R.string.LOT_59016;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59018"))
        {
            uiNameID = R.string.LOT_59018;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59019"))
        {
            uiNameID = R.string.LOT_59019;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59020"))
        {
            uiNameID = R.string.LOT_59020;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59023_B1"))
        {
            uiNameID = R.string.LOT_59023_B1;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59023_B2"))
        {
            uiNameID = R.string.LOT_59023_B2;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59023_B3"))
        {
            uiNameID = R.string.LOT_59023_B3;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59024"))
        {
            uiNameID = R.string.LOT_59024;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59026"))
        {
            uiNameID = R.string.LOT_59026;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59033_1"))
        {
            uiNameID = R.string.LOT_59033_1;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59033_2"))
        {
            uiNameID = R.string.LOT_59033_2;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59033_3"))
        {
            uiNameID = R.string.LOT_59033_3;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59033_4"))
        {
            uiNameID = R.string.LOT_59033_4;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59034"))
        {
            uiNameID = R.string.LOT_59034;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59035"))
        {
            uiNameID = R.string.LOT_59035;
        }
        else if (lotNameDb.equalsIgnoreCase("LOT_59036"))
        {
            uiNameID = R.string.LOT_59036;
        }
        else
        {
            System.out.println("ERROR:  Unknown parking lot " + lotNameDb);
        }

        return uiNameID;
    }
}
