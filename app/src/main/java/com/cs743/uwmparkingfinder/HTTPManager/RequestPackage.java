package com.cs743.uwmparkingfinder.HTTPManager;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fbgrecojr on 11/5/15.
 */
public class RequestPackage {

    private String uri;
    private String method = "GET";
    private Map<String, String> params = new HashMap<>();

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setParam(String k, String v){
        this.params.put(k,v);
    }

    /**
     * Takes all of the parameters that will be passed and correctly formats them
     * @return Returns the formated string that will be appended to the end of the url
     */
    public String getEncodedParams(){
        StringBuilder sb = new StringBuilder();
        for(String key : params.keySet()){
            String value = null;
            try{
                value = URLEncoder.encode(params.get(key), "UTF-8");
            }catch (Exception e){
                e.printStackTrace();
            }
            if(sb.length() > 0){
                sb.append("&");
            }
            sb.append(key + "=" + value);
        }
        return sb.toString();
    }
}
