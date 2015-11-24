package com.cs743.uwmparkingfinder.Algorithm;

import com.cs743.uwmparkingfinder.Structures.Lot;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by 305007877 on 11/24/2015.
 */
public class ValueComparator implements Comparator<Lot> {

    Map<Lot, Double> map;

    public ValueComparator(Map<Lot, Double> base) {
        this.map = base;
    }

    public int compare(Lot a, Lot b) {
        if (map.get(a) >= map.get(b)) {
            return 1;
        } else {
            return -1;
        } // returning 0 would merge keys
    }
}
