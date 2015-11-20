package com.cs743.uwmparkingfinder.HTTPManager;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fbgrecojr on 11/4/15.
 */
public class HttpManager {

    /**
     * Uses HttpURLConnection to make a connection to the database and returns a JSON formatted string
     * @param p RequestPackage containing all pertinent information to connect
     * @return a JSON formatted String
     */
    public static String getData(RequestPackage p){

        BufferedReader reader = null;
        String uri = p.getUri();
        if(p.getMethod().equals("GET")){
            uri+="?" + p.getEncodedParams();
        }

        try{
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(p.getMethod());

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;

            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }

            return sb.toString();

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally{
            if(reader != null){
                try{
                    reader.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Uses HttpURLConnection and authentication to make a connection to the database and returns a JSON formatted string
     * @param p RequestPackage containing all pertinent information to connect
     * @param userName the username
     * @param password the password
     * @return a JSON formatted String
     */
    public static String getData(RequestPackage p, String userName, String password){

        BufferedReader reader = null;
        HttpURLConnection con = null;
        String uri = p.getUri();
        if(p.getMethod().equals("GET")){
            uri+="?" + p.getEncodedParams();
        }

        byte[] loginBytes = (userName + ":" + password).getBytes();
        StringBuilder loginBuilding = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));

        try{
            URL url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(p.getMethod());

            con.addRequestProperty("Authorization", loginBuilding.toString());

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;

            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }

            return sb.toString();

        }catch (Exception e) {
            e.printStackTrace();
            try{
                int status = con.getResponseCode();
                return String.valueOf("Error: " + status);
            }catch(Exception x){
                x.printStackTrace();
            }
            return null;
        }finally{
            if(reader != null){
                try{
                    reader.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
