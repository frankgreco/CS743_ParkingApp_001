/*******************************************************************************
 * File Name:  SelectedParkingLot.java
 *
 * Description:
 * Structure used to define a parking lot selected by the back-end algorithm
 *
 * Revision  Date        Author             Summary of Changes Made
 * --------  ----------- ------------------ ------------------------------------
 * 1         12-Nov-2015 Eric Hitt          Original
 ******************************************************************************/
package com.cs743.uwmparkingfinder.Structures;

/****************************    Include Files    *****************************/

/****************************  Class Definitions  *****************************/

import java.io.Serializable;

/**
 * Structure representing a selected parking lot as determined by the back-end
 * algorithm.  Also contains defines for each of the different parking lots.
 *
 * Implements the Serializable class so that it can be passed
 */
public class SelectedParkingLot implements Serializable
{
    /*************************  Class Static Variables  ***********************/

    /*************************  Class Member Variables  ***********************/

    private String parkingLotName_;             ///< Selected parking lot name
    private String reason_;                     ///< Reason why lot was chosen
    private String destination_;
    /*************************  Class Public Interface  ***********************/

    /**
     * Constructor
     * @param name Parking lot name
     * @param reason Reason for selecting parking lot
     */
    public SelectedParkingLot(String name, String reason, String destination)
    {
        parkingLotName_ = name;
        reason_ = reason;
        destination_=destination;
    }

    /**
     * Set parking lot name
     *
     * @param name Parking lot name
     *
     * @return true if operation was successful, false otherwise
     */
    public boolean setParkingLotName(String name)
    {
        boolean success = true;

        parkingLotName_ = name;

        return success;
    }

    /**
     * Set reason for selecting parking lot
     *
     * @param reason Reason for selecting parking lot
     *
     * @return true if operation was successful, false otherwise
     */
    public boolean setReason(String reason)
    {
        boolean success = true;

        reason_ = reason;

        return success;
    }

    public boolean setDestination(String building) {
        boolean success=true;
        destination_=building;
        return success;
    }

    /**
     * Get parking lot name
     *
     * @return parking lot name
     */
    public String getParkingLotName()
    {
        return parkingLotName_;
    }

    /**
     * Get reason for selecting parking lot
     *
     * @return reason for selecting parking lot
     */
    public String getReason()
    {
        return reason_;
    }

    public String getDestination() {return destination_;}

    /************************  Class Private Interface  ***********************/

    /**
     * Default constructor
     */
    private SelectedParkingLot()
    {
        parkingLotName_ = null;
        reason_ = null;
        destination_=null;
    }
}
