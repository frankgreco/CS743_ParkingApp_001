package com.cs743.uwmparkingfinder.Structures;

/**
 * Created by fbgrecojr on 11/7/15.
 */
public class Building {

    private String name;
    private android.location.Location location;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: " + this.getName() + "\n");
        sb.append("Location: " + "[" + this.getLocation().getLatitude() + ", " + this.getLocation().getLongitude() + "]\n");
        return sb.toString();
    }
}
