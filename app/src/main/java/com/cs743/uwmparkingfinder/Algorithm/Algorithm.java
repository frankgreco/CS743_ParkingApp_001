package com.cs743.uwmparkingfinder.Algorithm;

import android.location.Location;

import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Structures.Building;
import com.cs743.uwmparkingfinder.Structures.Lot;
import com.cs743.uwmparkingfinder.Structures.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Masaki Ikuta on 11/20/2015.
 * Updated by masaki Ikuta on 11/22/2015 5:04PM.
 */
public class Algorithm {

    private double AVERAGE_PARKING_FEE_PER_HOUR = 1.50; // FIXME temporarily declared
    private double AVERAGE_DISTANCE = 100.00;           // FIXME temporarily declared

    private List<Lot> sortedLotList_;                   ///< Sorted lots from algorithm

    private static Algorithm sInstance_ = null;         ///< Class instance

    /**
     * Default (private) constructor
     */
    private Algorithm()
    {
        sortedLotList_ = new ArrayList<Lot>();
    }

    /**
     * Retrieve the instance of the class.
     *
     * Creates a new instance if one has not yet been created.
     *
     * @return Singleton instance of the class
     */
    public static Algorithm getInstance()
    {
        if (sInstance_ == null)
        {
            sInstance_ = new Algorithm();
        }

        return sInstance_;
    }

    /**
     * Returns the list of computed lots
     *
     * @return list of computed parking lots
     */
    public List<Lot> getLotList()
    {
        return sortedLotList_;
    }

    /**
     * Determines a set of recommended parking lots.
     *
     * @param destination Destination building
     *
     * @return List of recommended parking lots
     */
    public List<Lot> computeRecommendedLots(String destination)
    {
        System.out.println("DEBUG: Algorithm: Entry");

        // Declare the resulting list
        List<Lot> sortedLotList = sortedLotList_;

        // Get the current lot list
        List<Lot> currentLotList = Session.getCurrentLotList();
        if(currentLotList.size() == 0)
        {
            System.out.println("DEBUG: Algorithm: currentLotList is zero.");
            return sortedLotList;
        }

        // Get the building list
        List<Building> currentBuildings = Session.getCurrentBuildings(); //FIXME Need to get the current building
        if(currentBuildings.size() == 0)
        {
            System.out.println("DEBUG: Algorithm: currentBuildings is zero.");
            return sortedLotList;
        }

        // Get the building user wants to go
        Building building = currentBuildings.get(0);
        for(Building each_building : currentBuildings)
        {
            if(building.toString().equals(each_building.toString()))
            {
                building = each_building;
                break;
            }
        }

        if(building.getName().equals(""))
        {
            System.out.println("DEBUG: Algorithm: the destination cannot be found.");
            return sortedLotList;
        }

        TreeMap<Double, Lot> tmap = new TreeMap<Double, Lot>();
        //TODO: distORprice preference is in both parkingPreferences class and in the User class, so which should be used?
        int distORprice = Session.getCurrentUser().getDistORprice();

        //
        // Compute x_prime for each parking lot
        //
        for (Lot lot : currentLotList)
        {
            double x = getNormalizedDistance(building, lot);
            double y = getNormalizedParkingCost(lot);
            double theta = (distORprice - 1) * 10;
            double x_prime = x * Math.cos(theta) + y * Math.sin(theta);

            tmap.put(x_prime, lot);
        }

        Set set = tmap.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext())
        {
            Map.Entry mentry = (Map.Entry)iterator.next();
            System.out.print("DEBUG: Algorithm: key is: " + mentry.getKey() + " & Value is: ");
            System.out.print(mentry.getValue());
        }

        set = tmap.entrySet();
        iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            sortedLotList.add((Lot) mentry.getValue());
        }

        System.out.println("DEBUG: Algorithm: Exit");

        return sortedLotList;
    }

    private double getNormalizedParkingCost(Lot lot)
    {
        // AVERAGE_PARKING_FEE_PER_HOUR: the average parking expense per hour in the campus which is probably $1.50. This should be pre-determined value for Recommender system

        return lot.getRate() / AVERAGE_PARKING_FEE_PER_HOUR;
    }

    private double getNormalizedDistance(Building building, Lot lot)
    {
        // AVERAGE_DISTANCE: the average walking distance from a parking lot to a class. This should be pre-determined value for Recommender system

        return getActualDistance(building, lot) / AVERAGE_DISTANCE;
    }

    private double getActualDistance(Building building, Lot lot)
    {
        Location building_location = building.getLocation();
        Location lot_location = lot.getLocation();
        float[] result = new float[1];

        Location.distanceBetween(building_location.getLatitude(), building_location.getLongitude(), lot_location.getLatitude(), lot_location.getLongitude(), result);

        return result[0];
    }
}