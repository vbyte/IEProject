package com.jiang.geo;

import java.util.HashMap;
import java.util.Objects;

public class Constructions {

    private HashMap<String, Object> noise;
    private HashMap<String, Object> location_1;
    private String street_address;
    private String type;
    private String distance;
    private String image;
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

    public Constructions(HashMap<String, Object> noise, HashMap<String, Object> location_1, String street_address, String type, String distance, String image) {
        this.noise = noise;
        this.location_1 = location_1;
        this.street_address = street_address;
        this.type = type;
        this.distance = distance;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Constructions() {

    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public HashMap<String, Object> getNoise() {
        return noise;
    }

    public void setNoise(HashMap<String, Object> noise) {
        this.noise = noise;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public HashMap<String, Object> getLocation_1() {
        return location_1;
    }

    public void setLocation_1(HashMap<String, Object> location_1) {
        this.location_1 = location_1;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Constructions that = (Constructions) o;
        return Objects.equals(noise, that.noise) &&
                Objects.equals(location_1, that.location_1) &&
                Objects.equals(street_address, that.street_address) &&
                Objects.equals(type, that.type) &&
                Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {

        return Objects.hash(noise, location_1, street_address, type, image);
    }
}