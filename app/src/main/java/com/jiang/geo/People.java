package com.jiang.geo;

import java.util.HashMap;

public class People {

    private HashMap<String, Double> geo;
    private String location;
    private HashMap<String, Integer> volume;

    private String distanceTag;
    private float distanceValue;

    public String getDistanceTag() {
        return distanceTag;
    }

    public void setDistanceTag(String distanceTag) {
        this.distanceTag = distanceTag;
    }

    public float getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(float distanceValue) {
        this.distanceValue = distanceValue;
    }

    public People(HashMap<String, Double> geo, String location, HashMap<String, Integer> volume) {
        this.geo = geo;
        this.location = location;
        this.volume = volume;
    }

    public People() {

    }

    public HashMap<String, Double> getGeo() {
        return geo;
    }

    public void setGeo(HashMap<String, Double> geo) {
        this.geo = geo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public HashMap<String, Integer> getVolume() {
        return volume;
    }

    public void setVolume(HashMap<String, Integer> volume) {
        this.volume = volume;
    }
}