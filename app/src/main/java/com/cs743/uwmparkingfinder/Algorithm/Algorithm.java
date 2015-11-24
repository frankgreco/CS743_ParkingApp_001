package com.cs743.uwmparkingfinder.Algorithm;

import android.location.Location;

import com.cs743.uwmparkingfinder.Session.Session;
import com.cs743.uwmparkingfinder.Structures.Building;
import com.cs743.uwmparkingfinder.Structures.Lot;
import com.cs743.uwmparkingfinder.Structures.ParkingPreferences;
import com.cs743.uwmparkingfinder.Structures.Space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Masaki Ikuta on 11/20/2015.
 * Updated by masaki Ikuta on 11/24/2015 1:47PM.
 */
public class Algorithm {

    // The average parking expense per hour in the campus.
    // At UWM, the parking fee varies from $1.00 to $2.00.
    // This should be pre-determined value for Recommender system
    private static double AVERAGE_PARKING_FEE_PER_HOUR = 1.50;

    // The average walking distance from a parking lot to a destination.
    // This should be pre-determined value for Recommender system
    // Note that it is about 500 meters from North Quadrant to EMS building.
    private static double AVERAGE_DISTANCE_LOT_TO_DEST = 500.00;

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
     * @param preferences user preferences
     *
     * @return List of recommended parking lots
     */
    public List<Lot> computeRecommendedLots(ParkingPreferences preferences)
    {
        System.out.println("Algorithm: Entry");

        // Declare the resulting list
        List<Lot> sortedLotList = sortedLotList_;
        sortedLotList.clear(); // Clear the list since we are re-generate the list

        // Get the current lot list
        List<Lot> currentLotList = Session.getCurrentLotList();
        System.out.println("Algorithm:    currentLotList = " + currentLotList.size());

        // Get the building list
        List<Building> currentBuildings = Session.getCurrentBuildings();
        System.out.println("Algorithm:    currentBuildings = " + currentBuildings.size());

        // Get the building user wants to go
        Building building = new Building();
        building.setName("");
        for(Building each_building : currentBuildings)
        {
            if(preferences.getDestination().equals(each_building.getName()))
            {
                building = each_building;
                break;
            }
        }
        System.out.println("Algorithm:    destination = " + building.getName());

        // Get user preferences

        //Since the backend and the EditPreferences Class populates the User class, that class should be used
        //to get the below values as opposed to the Parking preferences class.
        //
        //See my commented suggestions
        //
        //instead of preferences.getOptimization() use Session.getCurrentUser().getDistORprice();
        int distORprice = (int)Math.floor((double)preferences.getOptimization() / 100.0 * 9.0); // distORprice ranges from 0 to 9
        //instead of preferences.getOutsideParking() use Session.getCurrentUser().isCovered();
        boolean allowOutside = preferences.getOutsideParking();
        //instead of preferences.getHandicapRequired() use Session.getCurrentUser().isHandicap();
        boolean handicapReq = preferences.getHandicapRequired();
        //instead of preferences.getElectricRequired() use Session.getCurrentUser().isElectric();
        boolean electricReq = preferences.getElectricRequired();
        System.out.println("Algorithm:    distORprice = " + distORprice + ", allowOutside = " + allowOutside + ", handicapReq = " + handicapReq + ", electricReq = " + electricReq);

        // Just return the empty list if some variables are not valid.
        if(currentLotList.size() == 0 || currentBuildings.size() == 0 || building.getName().equals(""))
        {
            return sortedLotList;
        }

        // Declare a tree map for the sorting
        HashMap unsorted_map = new HashMap();
        ValueComparator bvc = new ValueComparator(unsorted_map);
        TreeMap sorted_map = new TreeMap(bvc);

        //
        // Compute x_prime for each parking lot
        //
        for (Lot lot : currentLotList)
        {
            double x = computeNormalizedDistance(building, lot);
            double y = getNormalizedParkingCost(lot);
            double theta = distORprice * 10;
            double x_prime = x * Math.cos(Math.toRadians(theta)) + y * Math.sin(Math.toRadians(theta));

            // Check space availability
            List<Space> spaces = lot.getSpaces();
            boolean isCovered = lot.isCovered();
            boolean isHandicapAvailable = false;
            boolean isElectricAvailable = false;
            for(Space each_space : spaces) {
                //System.out.println("Algorithm:        lot = " + lot.getName() + ", space = " + each_space.getNumber() + ", isAvailable = " + each_space.isAvailable() + ", isHandicap = " + each_space.isHandicap() + ", isElectric = " + each_space.isElectric());

                if(each_space.isAvailable() == true) {

                    // Check if it should be handicapped
                    if (isHandicapAvailable == false && each_space.isHandicap() == true)
                    {
                        isHandicapAvailable = true;
                    }

                    // Check if it's an electric car or not
                    if (isElectricAvailable == false && each_space.isElectric() == true)
                    {
                        isElectricAvailable = true;
                    }
                }
            }

            System.out.println("Algorithm:    lot_name = " + lot.getName() + ", distance = " + computeDistanceInMeter(building, lot) + ", fee = " + lot.getRate()
                    + ", x = " + x + ", y = " + y + ", theta = " + theta + ", x_prime = " + x_prime + ", isCovered() = " + lot.isCovered() + ", isHandicap = " + isHandicapAvailable + ", isElectric = " + isElectricAvailable);

            if(allowOutside == false && lot.isCovered() == false) { continue; }
            if (preferences.getHandicapRequired() == true && isHandicapAvailable == false) { continue; }
            if(preferences.getElectricRequired() == true && isElectricAvailable == false) { continue; }

            unsorted_map.put(lot, x_prime);
        }

        // Generate a sorted list
        sorted_map.putAll(unsorted_map);

        Iterator it = sorted_map.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("Algorithm:    sortedLotList[" + sortedLotList.size() + "] = " + (Lot)pair.getKey());
            sortedLotList.add((Lot)pair.getKey());
        }

        System.out.println("Algorithm: Exit");

        return sortedLotList;
    }

    private double getNormalizedParkingCost(Lot lot)
    {
        return lot.getRate() / AVERAGE_PARKING_FEE_PER_HOUR;
    }

    private double computeNormalizedDistance(Building building, Lot lot)
    {

        return computeDistanceInMeter(building, lot) / AVERAGE_DISTANCE_LOT_TO_DEST;
    }

    private double computeDistanceInMeter(Building building, Lot lot)
    {
        Location building_location = building.getLocation();
        Location lot_location = lot.getLocation();
        float[] result = new float[1];

        //System.out.println("Algorithm:         getActualDistance: " + building_location.getLatitude() + ", " + building_location.getLongitude() + ", " + lot_location.getLatitude() + ", " + lot_location.getLongitude());
        Location.distanceBetween(building_location.getLatitude(), building_location.getLongitude(), lot_location.getLatitude(), lot_location.getLongitude(), result);

        return result[0];
    }
}
