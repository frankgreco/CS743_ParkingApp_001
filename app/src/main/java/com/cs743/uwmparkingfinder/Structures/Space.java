package com.cs743.uwmparkingfinder.Structures;


/**
 * Created by fbgrecojr on 11/5/15.
 */
public class Space {

    private int number;
    private String lot;
    private boolean available;
    private String expired;
    private boolean handicap;
    private boolean electric;

    public Space(int number, String lot, boolean available, String expired, boolean handicap, boolean electric){
        this.number = number;
        this.lot = lot;
        this.available = available;
        this.expired = expired;
        this.handicap = handicap;
        this.electric = electric;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public boolean isHandicap() {
        return handicap;
    }

    public void setHandicap(boolean handicap) {
        this.handicap = handicap;
    }

    public boolean isElectric() {
        return electric;
    }

    public void setElectric(boolean electric) {
        this.electric = electric;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Space#" + this.getNumber() + "\n");
        sb.append("Available: " + this.isAvailable() + "\n");
        sb.append("Expired: " + this.getExpired() + "\n");
        sb.append("Handicap: " + this.isHandicap() + "\n");
        sb.append("Electric: " + this.isElectric() + "\n");
        return sb.toString();
    }
}
