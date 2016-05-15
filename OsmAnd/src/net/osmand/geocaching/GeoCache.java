package net.osmand.geocaching;

import android.graphics.Color;

/**
 * Created by mwinnick on 4/10/2016.
 */
public class GeoCache {
    private String code;
    private String name;
    private String type;
    private String size;
    private double terrain_difficulty;
    private double general_difficulty;
    // rating color: < 2 red, (2,3) yellow, > 3 green
    private double rating;
    private double recommendations;
    private double latitude;
    private double longitude;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public double getTerrain_difficulty() {
        return terrain_difficulty;
    }

    public double getGeneral_difficulty() {
        return general_difficulty;
    }

    public double getRating() {
        return rating;
    }

    public double getRecommendations() {
        return recommendations;
    }

    public String getCode() {
        return code;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public GeoCache(String code, String name, String type, String size, double terrain_difficulty,
                    double general_difficulty, double rating, double recommendations, double latitude,
                    double longitude) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.size = size;
        this.terrain_difficulty = terrain_difficulty;
        this.general_difficulty = general_difficulty;
        this.rating = rating;
        this.recommendations = recommendations;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String GetFormattedLocation() {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            latSeconds = Math.abs(latSeconds % 3600);
            int latMinutes = latSeconds / 60;
            latSeconds %= 60;

            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            longSeconds = Math.abs(longSeconds % 3600);
            int longMinutes = longSeconds / 60;
            longSeconds %= 60;
            String latDegree = latDegrees >= 0 ? "N" : "S";
            String lonDegrees = latDegrees >= 0 ? "E" : "W";
            char degreeSymbol = 0x00B0;

            return String.format("%d%c%d'%d\"%s %d%c%d'%d\"%s",
                    Math.abs(latDegrees), degreeSymbol, latMinutes, latSeconds, latDegree,
                    Math.abs(longDegrees), degreeSymbol, longMinutes, longSeconds, lonDegrees);
        } catch (Exception e) {
            return ""+ String.format("%8.5f", latitude) + "  "
                    + String.format("%8.5f", longitude) ;
        }
    }

    public int GetRatingColor() {
        if (rating < 2)
            return Color.RED;
        if (rating < 3)
            return Color.YELLOW;
        return Color.GREEN;
    }
}
