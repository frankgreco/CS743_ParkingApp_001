/*******************************************************************************
 * File Name:  ParkingPreferences.java
 *
 * Description:
 * Structure used to hold parking preference parameters
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         11-Nov-2015 Eric Hitt          Original
 ******************************************************************************/
package com.cs743.uwmparkingfinder.Structures;

/****************************    Include Files    *****************************/

/****************************  Class Definitions  *****************************/

/**
 * Represents parking preferences
 */
public class ParkingPreferences
{
    /*************************  Class Static Variables  ***********************/
    /*public enum OPT_STRATEGY
    {
        OPT_COST,                           ///< Optimize based on cost
        OPT_DIST                            ///< Optimize based on distance
    }*/

    /*************************  Class Member Variables  ***********************/

    private String destination_;            ///< Destination building
    private int destTimeHour_;              ///< Destination time (hours part)
    private int destTimeMin_;               ///< Destination time (minute part)
    //private OPT_STRATEGY optimization_;     ///< Preferred optimization
    private int disORprice_;                ///< Cost or Distance Preference: 0=prefer closer / 100=prefer cheaper
    private boolean allowOutside_;          ///< True if outside parking ok
    private boolean handicapReq_;           ///< True if require handicap parking
    private boolean electricReq_;           ///< True if require electric parking

    /*************************  Class Public Interface  ***********************/

    /**
     * Default constructor
     */
    public ParkingPreferences()
    {
        destination_ = null;
        destTimeHour_ = 0;
        destTimeMin_ = 0;
        //optimization_ = OPT_STRATEGY.OPT_COST;
        disORprice_=50;
        allowOutside_ = true;
        handicapReq_ = false;
        electricReq_ = false;
    }

    /**
     * Constructor
     *
     * @param destination Overall destination building
     * @param hours Destination time (hours component)
     * @param min Destination time (minutes component)
     * @param optimization Optimization strategy
     * @param outsideOk True if it is ok to parking outside
     * @param handicapReq True if require handicap parking
     * @param electricReq True if require electric plug-in parking
     */
    public ParkingPreferences(String destination, int hours, int min,
                              int optimization, boolean outsideOk,
                              boolean handicapReq, boolean electricReq)
    {
        destination_ = destination;
        destTimeHour_ = hours;
        destTimeMin_ = min;
        //optimization_ = optimization;
        disORprice_=optimization;
        allowOutside_ = outsideOk;
        handicapReq_ = handicapReq;
        electricReq_ = electricReq;
    }

    /**
     * Assign a destination
     *
     * @param destination New destination
     *
     * @return true if able to update destination successfully, false otherwise
     */
    public boolean setDestination(String destination)
    {
        boolean success = true;

        if (destination == null)
        {
            success = false;
        }
        else
        {
            destination_ = destination;
        }

        return success;
    }

    /**
     * Assign a destination time
     *
     * @param hour Hours component
     * @param min Minutes component
     *
     * @return true if able to update time successfully, false otherwise
     */
    public boolean setDestinationTime(int hour, int min)
    {
        final int MAX_HOURS = 24;
        final int MAX_MINUTES = 60;
        boolean success = true;

        // Times are 0-based
        if (((hour < 0) || (hour >= MAX_HOURS)) ||
            ((min < 0) || (min >= MAX_MINUTES)))
        {
            success = false;
        }
        else
        {
            destTimeHour_ = hour;
            destTimeMin_ = min;
        }

        return success;
    }

    /**
     * Assign an optimization strategy
     *
     * @param optimization Optimization strategy
     *
     * @return true if able to update optimization strategy successfully, false otherwise
     */
    public boolean setOptimization(int optimization)
    {
        boolean success = true;
        disORprice_=optimization;
       /* if ((optimization != OPT_STRATEGY.OPT_COST) ||
            (optimization != OPT_STRATEGY.OPT_DIST))
        {
            success = false;
        }
        else
        {
            optimization_ = optimization;
        }*/
        return success;
    }

    /**
     * Assigns outside parking preference
     *
     * @param outsideOk True if ok to park outside, false otherwise
     *
     * @return true if able to update outside parking, false otherwise
     */
    public boolean setOutsideParking(boolean outsideOk)
    {
        boolean success = true;

        allowOutside_ = outsideOk;

        return success;
    }

    /**
     * Assigns handicap parking required preference
     *
     * @param handicapReq True if need handicap parking, false otherwise
     *
     * @return true if able to update handicap parking, false otherwise
     */
    public boolean setHandicapRequired(boolean handicapReq)
    {
        boolean success = true;

        handicapReq_ = handicapReq;

        return success;
    }

    /**
     * Assigns electric parking required preference
     *
     * @param electricReq True if need electric parking, false otherwise
     *
     * @return true if able to update electric parking, false otherwise
     */
    public boolean setElectricRequired(boolean electricReq)
    {
        boolean success = true;

        electricReq_ = electricReq;

        return success;
    }

    /**
     * Retrieve destination
     *
     * @return Destination
     */
    public String getDestination()
    {
        return destination_;
    }

    /**
     * Retrieve destination time (hour component)
     *
     * @return Hour component to destination time
     */
    public int getDestinationTimeHours()
    {
        return destTimeHour_;
    }

    /**
     * Retrieve destination time (minute component)
     *
     * @return Minute component to destination time
     */
    public int getDestinationTimeMinutes()
    {
        return destTimeMin_;
    }

    /**
     * Retrieves optimization strategy
     *
     * @return Optimization strategy
     */
    public int getOptimization()
    {
        return disORprice_;
    }

    /**
     * Retrieves outside parking preference
     *
     * @return Outside parking preference
     */
    public boolean getOutsideParking()
    {
        return allowOutside_;
    }

    /**
     * Retrieves handicap parking preference
     *
     * @return Handicap parking preference
     */
    public boolean getHandicapRequired()
    {
        return handicapReq_;
    }

    /**
     * Retrieves electric parking preference
     *
     * @return Electric parking preference
     */
    public boolean getElectricRequired()
    {
        return electricReq_;
    }

    /************************  Class Private Interface  ***********************/
}
