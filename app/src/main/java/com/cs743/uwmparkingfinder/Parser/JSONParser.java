package com.cs743.uwmparkingfinder.Parser;

import com.cs743.uwmparkingfinder.Structures.Building;
import com.cs743.uwmparkingfinder.Structures.LogItem;
import com.cs743.uwmparkingfinder.Structures.Lot;
import com.cs743.uwmparkingfinder.Structures.Space;
import com.cs743.uwmparkingfinder.Structures.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by fbgrecojr on 11/5/15.
 */
public class JSONParser {

    /**
     * Turns a string of JSON formatted content and fills a typed List of Lot Objects
     * @param content the JSON string to parse
     * @return a List of Lot Objects
     */
    public static List<List<Lot>> parseLotFeed(String content){

        ArrayList<List<Lot>> toReturn = new ArrayList<>();
        List<Lot> lotListAvailable;
        List<Lot> lotListAll;
        Lot curLot;

        try{
            JSONArray arr = new JSONArray(content);

            lotListAvailable = new ArrayList<>();
            lotListAll = new ArrayList<>();

            for(int i = 0; i < arr.length(); ++i){
                JSONObject obj = arr.getJSONObject(i);

                boolean available = obj.getString("available").equals("1") ? true : false;
                String curLotName = obj.getString("lotName");
                boolean exists = false;
                int index;

                if(available){

                    for(index = 0; index < lotListAvailable.size(); ++index) {
                        if (lotListAvailable.get(index).getName().equals(curLotName)) {
                            exists = true;
                            break;
                        }
                    }

                    if(exists){ //only add to lotListAvailable if space is available
                        lotListAvailable.get(index).getSpaces().add(new Space(obj.getInt("spaceNumber"), lotListAvailable.get(index).getName(), obj.getString("available").equals("1") ? true : false, obj.getString("expired"), obj.getBoolean("handicap"), obj.getBoolean("electric")));
                    }else{
                        curLot = new Lot();
                        android.location.Location curLoc = new android.location.Location("");
                        curLot.setName(obj.getString("lotName"));
                        curLoc.setLatitude(obj.getDouble("latitude"));
                        curLoc.setLongitude(obj.getDouble("longitude"));
                        curLot.setLocation(curLoc);
                        curLot.setNumSpaces(obj.getInt("numSpaces"));
                        curLot.setRate(obj.getDouble("rate"));
                        curLot.setMaxTime(obj.getInt("maxTime"));
                        String keywords = obj.getString("keywords");
                        List<String> keys = Arrays.asList(keywords.split("\\s*,\\s*"));
                        curLot.setKeywords(keys);
                        curLot.getSpaces().add(new Space(obj.getInt("spaceNumber"), curLot.getName(), obj.getString("available").equals("1") ? true : false, obj.getString("expired"), obj.getBoolean("handicap"), obj.getBoolean("electric")));
                        curLot.setCovered(obj.getBoolean("covered"));

                        lotListAvailable.add(curLot);

                    }

                }

                //add all spaces to lotListAll
                curLot = new Lot();
                exists = false;
                for(index = 0; index < lotListAll.size(); ++index) {
                    if (lotListAll.get(index).getName().equals(curLotName)) {
                        exists = true;
                        break;
                    }
                }

                if(exists){
                    lotListAll.get(index).getSpaces().add(new Space(obj.getInt("spaceNumber"), lotListAll.get(index).getName(), obj.getString("available").equals("1") ? true : false, obj.getString("expired"), obj.getBoolean("handicap"), obj.getBoolean("electric")));
                }else{
                    curLot = new Lot();
                    android.location.Location curLoc = new android.location.Location("");
                    curLot.setName(obj.getString("lotName"));
                    curLoc.setLatitude(obj.getDouble("latitude"));
                    curLoc.setLongitude(obj.getDouble("longitude"));
                    curLot.setLocation(curLoc);
                    curLot.setNumSpaces(obj.getInt("numSpaces"));
                    curLot.setRate(obj.getDouble("rate"));
                    curLot.setMaxTime(obj.getInt("maxTime"));
                    String keywords = obj.getString("keywords");
                    List<String> keys = Arrays.asList(keywords.split("\\s*,\\s*"));
                    curLot.setKeywords(keys);
                    curLot.getSpaces().add(new Space(obj.getInt("spaceNumber"), curLot.getName(), obj.getString("available").equals("1") ? true : false, obj.getString("expired"), obj.getBoolean("handicap"), obj.getBoolean("electric")));
                    curLot.setCovered(obj.getBoolean("covered"));

                    lotListAll.add(curLot);

                }

            }

            toReturn.add(lotListAvailable);
            toReturn.add(lotListAll);

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return toReturn;
    }

    /**
     * Turns a string of JSON formatted content and fills a typed Lot
     * @param content the JSON string to parse
     * @return Lot Object containing all spaces for a specified lot
     */
    public static Lot parseSpacesByLot(String content){

        Lot lot = new Lot();

        if(content.equals("no rows returned")){
            return lot;
        }

        try{
            JSONArray arr = new JSONArray(content);

            for(int i = 0; i < arr.length(); ++i){
                JSONObject obj = arr.getJSONObject(i);

                //get first item to populate
                if(i == 0){ //set lot variables as well as first space
                    android.location.Location curLoc = new android.location.Location("");
                    lot.setName(obj.getString("lotName"));
                    curLoc.setLatitude(obj.getDouble("latitude"));
                    curLoc.setLongitude(obj.getDouble("longitude"));
                    lot.setLocation(curLoc);
                    lot.setNumSpaces(obj.getInt("numSpaces"));
                    lot.setRate(obj.getDouble("rate"));
                    lot.setMaxTime(obj.getInt("maxTime"));
                    String keywords = obj.getString("keywords");
                    List<String> keys = Arrays.asList(keywords.split("\\s*,\\s*"));
                    lot.setKeywords(keys);
                    lot.getSpaces().add(new Space(obj.getInt("spaceNumber"), lot.getName(), obj.getString("available").equals("1") ? true : false, obj.getString("expired"), obj.getBoolean("handicap"), obj.getBoolean("electric")));
                    lot.setCovered(obj.getBoolean("covered"));
                }else{ //populate other spaces
                    lot.getSpaces().add(new Space(obj.getInt("spaceNumber"), lot.getName(), obj.getString("available").equals("1") ? true : false, obj.getString("expired"), obj.getBoolean("handicap"), obj.getBoolean("electric")));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return lot;
    }

    /**
     * Turns a string of JSON formatted content and fills a typed User Object
     * @param content the JSON string to parse
     * @return a User objects
     */
    public static User parseUserFeed(String content){

        User user;

        try {
            user = new User();
            JSONArray arr = new JSONArray(content);

            for (int i = 0; i < arr.length(); ++i) {
                JSONObject obj = arr.getJSONObject(i);

                user.setUsername(obj.getString("username"));
                user.setPassword(obj.getString("password"));
                user.setFirst(obj.getString("first_name"));
                user.setLast(obj.getString("last_name"));
                user.setEmail(obj.getString("email"));
                user.setPhone(obj.getString("phone"));
                user.setCovered(obj.getBoolean("covered"));
                user.setHandicap(obj.getBoolean("handicap"));
                user.setElectric(obj.getBoolean("electric"));
                user.setDistORprice(obj.getInt("dist_price"));
            }
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return user;
    }

    /**
     * Turns a string of JSON formatted content and fills a typed List of user User Objects
     * @param content the JSON string to parse
     * @return a List of User Objects
     */
    public static List<LogItem> parseLogFeed(String content){

        List<LogItem> logList = new ArrayList<>();

        try{
            JSONArray arr = new JSONArray(content);

            for(int i = 0; i < arr.length(); ++i){
                JSONObject obj = arr.getJSONObject(i);
                LogItem curItem = new LogItem();

                curItem.setUserName(obj.getString("username"));
                curItem.setKeyword(obj.getString("keyword"));
                curItem.setLength(obj.getInt("length"));
                curItem.setLotName(obj.getString("lotName"));

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try{
                    Date date = format.parse(obj.getString("time")); // mysql datetime format
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    curItem.setTime(calendar);
                }catch (ParseException e){
                    e.printStackTrace();
                    return null;
                }

                logList.add(curItem);
            }
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return logList;
    }

    /**
     * Turns a string of JSON formatted content and fills a typed List of Building Objects
     * @param content the JSON string to parse
     * @return a List of Building Objects
     */
    public static List<Building> parseBuildingFeed(String content){

        List<Building> buildings = new ArrayList<>();

        try{
            JSONArray arr = new JSONArray(content);

            for(int i = 0; i < arr.length(); ++i){
                JSONObject obj = arr.getJSONObject(i);
                Building curItem = new Building();
                android.location.Location curLoc = new android.location.Location("");

                curItem.setName(obj.getString("name"));
                curLoc.setLatitude(obj.getDouble("latitude"));
                curLoc.setLongitude(obj.getDouble("longitude"));
                curItem.setLocation(curLoc);

                buildings.add(curItem);
            }
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return buildings;
    }
}
