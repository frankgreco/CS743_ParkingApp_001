package com.cs743.uwmparkingfinder.Structures;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by fbgrecojr on 11/7/15.
 */
public class LogItem {

    private GregorianCalendar time;
    private String keyword;
    private int length;
    private String lotName;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public GregorianCalendar getTime() {
        return time;
    }

    public void setTime(GregorianCalendar time) {
        this.time = time;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLotName() {
        return lotName;
    }

    public void setLotName(String lotName) {
        this.lotName = lotName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("username: " + this.getUserName() + "\n");
        sb.append("time: " + this.getTime() + "\n");
        sb.append("lot: " + this.getLotName() + "\n");
        sb.append("keyword: " + this.getKeyword() + "\n");
        sb.append("length " + this.getLength() + "\n");
        return sb.toString();
    }
}
