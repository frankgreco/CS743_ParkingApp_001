/*******************************************************************************
 * File Name:  ParkingRequest.java
 *
 * Description:
 * Structure used to hold parking request parameters
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         11-Nov-2015 Eric Hitt          Original
 * 2         24-Nov-2015 Eric Hitt          Renamed to ParkingRequest
 ******************************************************************************/
package com.cs743.uwmparkingfinder.Structures;

/****************************    Include Files    *****************************/

/****************************  Class Definitions  *****************************/

/**
 * Represents parking request packet
 */
public class ParkingRequest
{
    /*************************  Class Static Variables  ***********************/

    /*************************  Class Member Variables  ***********************/

    private String destination_;            ///< Destination building
    private int disORprice_;                ///< Cost or Distance Preference: 0=prefer closer / 100=prefer cheaper
    private boolean allowOutside_;          ///< True if outside parking ok
    private boolean handicapReq_;           ///< True if require handicap parking
    private boolean electricReq_;           ///< True if require electric parking

    /*************************  Class Public Interface  ***********************/

    /**
     * Constructor
     *
     * @param destination Overall destination building
     * @param optimization Optimization strategy
     * @param outsideOk True if it is ok to parking outside
     * @param handicapReq True if require handicap parking
     * @param electricReq True if require electric plug-in parking
     */
    public ParkingRequest(String destination,
                         int optimization, boolean outsideOk,
                         boolean handicapReq, boolean electricReq)
    {
        destination_ = destination;
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
     * Assign an optimization strategy
     *
     * @param optimization Optimization strategy
     *
     * @return true if able to update optimization strategy successfully, false otherwise
     */
    public boolean setOptimization(int optimization)
    {
        boolean success = true;

        disORprice_ = optimization;

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

    /**
     * Default constructor
     */
    private ParkingRequest()
    {
        destination_ = null;
        disORprice_=50;
        allowOutside_ = true;
        handicapReq_ = false;
        electricReq_ = false;
    }
}
