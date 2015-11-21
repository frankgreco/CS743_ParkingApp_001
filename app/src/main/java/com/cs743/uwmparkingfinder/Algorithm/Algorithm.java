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
 */
public class Algorithm {

    private double AVERAGE_PARKING_FEE_PER_HOUR = 1.50; // FIXME temporarily declared
    private double AVERAGE_DISTANCE = 100.00;           // FIXME temporarily declared

    public List<Lot> getSortedLotList(String destination)
    {
        System.out.println("DEBUG: Algorithm: Entry");

        // Declare the resulting list
        List<Lot> sortedLotList = new ArrayList<Lot>();

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
        Building building = new Building();
        for(Building each_building : currentBuildings)
        {
            if(building.toString().equals(building.toString()))
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