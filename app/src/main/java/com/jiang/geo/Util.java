package com.jiang.geo;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class Util {

    private static final double EARTH_RADIUS = 6378137.0;

    private static final String TAG = "FileUtil";

    public static final String LOCAL = "Test";

    public static final String LOCAL_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator;

    /**
     * THE loation of the document
     */
    public static final String REC_PATH = LOCAL_PATH + LOCAL + File.separator;


    /**
     * automatically create the directory in SD CARD
     */
    static {
        File dirRootFile = new File(LOCAL_PATH);
        if (!dirRootFile.exists()) {
            dirRootFile.mkdirs();
        }
        File recFile = new File(REC_PATH);
        if (!recFile.exists()) {
            recFile.mkdirs();
        }
    }

    /**
     * find if there is any spaces for storage	 *
     *
     * @return
     */
    public static boolean isExitSDCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private static boolean hasFile(String fileName) {
        File f = createFile(fileName);
        return null != f && f.exists();
    }

    public static File createFile(String fileName) {

        File myCaptureFile = new File(REC_PATH + fileName);
        if (myCaptureFile.exists()) {
            myCaptureFile.delete();
        }
        try {
            myCaptureFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myCaptureFile;
    }

    public static String getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return convertTo2(s / 1000);
    }

    public static float getDistanceValue(double longitude1, double latitude1,
                                         double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return Float.parseFloat(String.format("%.2f", s / 1000));
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    private static String convertTo2(double distance) {


        String distance1 = String.format("%.2f", distance);
        return distance1 + "Km";

    }

}


