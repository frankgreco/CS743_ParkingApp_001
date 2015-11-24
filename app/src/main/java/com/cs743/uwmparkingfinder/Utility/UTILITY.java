package com.cs743.uwmparkingfinder.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by fbgrecojr on 11/21/15.
 */
public class UTILITY {

    public static final String UBUNTU_SERVER_URL ="http://ec2-54-152-4-103.compute-1.amazonaws.com/scripts.php";


    /**
     * Check to see whether there is an internet connection or not.
     * @return whether there is an internet connection
     */
    public static boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
