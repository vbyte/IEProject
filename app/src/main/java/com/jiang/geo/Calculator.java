package com.jiang.geo;

public class Calculator {

    public static float dbstart = 0; //initiall record

    public static float dblast = dbstart; //the last value

    public static void setDbCount(float dbValue) {
        dbstart = dblast + (dbValue - dblast) * 0.2f; // with two demical number
        dblast = dbstart;
    }
}

