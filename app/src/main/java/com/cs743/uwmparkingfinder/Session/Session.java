package com.cs743.uwmparkingfinder.Session;

import com.cs743.uwmparkingfinder.Structures.Building;
import com.cs743.uwmparkingfinder.Structures.LogItem;
import com.cs743.uwmparkingfinder.Structures.Lot;
import com.cs743.uwmparkingfinder.Structures.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by fbgrecojr on 11/14/15.
 */
public class Session {

    //holds the current List of Lot Objects. It can be accessed through the entire project by its static getter/setter
    private static List<Lot> currentLotList = new ArrayList<>();
    //holds the current List of Lot Objects. It can be accessed through the entire project by its static getter/setter
    private static List<Lot> allSpacesByLot = new ArrayList<>();
    //holds the current User Objects. It can be accessed through the entire project by its static getter/setter
    private static User currentUser = new User();
    //holds the current List of LogItem Objects. It can be accessed through the entire project by its static getter/setter
    private static List<LogItem> currentLog = new ArrayList<>();
    //holds the current List of Building Objects. It can be accessed through the entire project by its static getter/setter
    private static List<Building> currentBuildings = new ArrayList<>();

    public static List<Lot> getCurrentLotList() {
        return currentLotList;
    }

    public static void setCurrentLotList(List<Lot> currentLotList) {
        Session.currentLotList = currentLotList;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        Session.currentUser = currentUser;
    }

    public static List<LogItem> getCurrentLog() {
        return currentLog;
    }

    public static void setCurrentLog(List<LogItem> currentLog) {
        Session.currentLog = currentLog;
    }

    public static List<Building> getCurrentBuildings() {
        return currentBuildings;
    }

    public static void setCurrentBuildings(List<Building> currentBuildings) {
        Session.currentBuildings = currentBuildings;
    }

    public static List<Lot> getAllSpacesByLot() {
        return allSpacesByLot;
    }

    public static void setAllSpacesByLot(List<Lot> allSpacesByLot) {
        Session.allSpacesByLot = allSpacesByLot;
    }
}
