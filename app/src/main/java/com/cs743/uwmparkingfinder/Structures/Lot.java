package com.cs743.uwmparkingfinder.Structures;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbgrecojr on 11/5/15.
 */
public class Lot {

    private String name;
    private android.location.Location location;
    private int numSpaces;
    private double rate;
    private int maxTime;
    private List<String> keywords = new ArrayList<>();
    private List<Space> spaces = new ArrayList<>();
    private boolean covered;

    public boolean isCovered() {
        return covered;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public android.location.Location getLocation() {
        return location;
    }

    public void setLocation(android.location.Location location) {
        this.location = location;
    }

    public int getNumSpaces() {
        return numSpaces;
    }

    public void setNumSpaces(int numSpaces) {
        this.numSpaces = numSpaces;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<Space> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<Space> spaces) {
        this.spaces = spaces;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Name: " + this.getName() + "\n");
        sb.append("Location: " + "[" + this.getLocation().getLatitude() + ", " + this.getLocation().getLongitude() + "]\n");        sb.append("Number of Spaces: " + this.getNumSpaces() + "\n");
        sb.append("Rate: " + this.getRate() + "\n");
        sb.append("Max Time: " + this.getMaxTime() + "\n");
        sb.append("Keywords: " + this.getKeywords() + "\n");
        sb.append("Covered: " + this.isCovered() + "\n\n");
        for(int i = 0; i < this.getSpaces().size(); ++i){
            sb.append(this.getSpaces().get(i).toString() + "\n");
        }
        return sb.toString();
    }
}
