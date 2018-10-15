package com.jiang.geo;

public class ConstructionsAdapter {
    private String address;
    private Float distance;
    private String noiselevel;


    public ConstructionsAdapter(String address, Float distance, String noiselevel) {
        this.address = address;
        this.distance = distance;
        this.noiselevel = noiselevel;
    }

    public String getNoiselevel() {
        return noiselevel;
    }

    public void setNoiselevel(String noiselevel) {
        this.noiselevel = noiselevel;
    }

    public ConstructionsAdapter() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }


}
