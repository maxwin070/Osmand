package net.osmand.geocaching;

import net.osmand.Location;

/**
 * Created by mwinnick on 4/10/2016.
 */
public class GeoCache {
    private String code;
    private String name;
    private String type;
    private String status;

    private double latitude;
    private double longtitude;

    public String getCode() {
        return code;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public GeoCache(String code, double latitude, double longtitude) {
        this.code = code;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }
}
