package com.example.maplocationinfirebase.model;

public class MyLocation {

    private String lat;
    private String lon;

    public MyLocation(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        if(lat.length() > 0){
            return Double.parseDouble(lat);
        }
        return 0;
    }

    public double getLon() {
        if(lon.length() > 0){
            return Double.parseDouble(lon);
        }
        return 0;
    }
}