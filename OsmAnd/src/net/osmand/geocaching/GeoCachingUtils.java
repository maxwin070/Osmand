package net.osmand.geocaching;

import net.osmand.AndroidNetworkUtils;
import net.osmand.Location;
import net.osmand.plus.OsmandApplication;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by mwinnick on 4/10/2016.
 */
public class GeoCachingUtils {
    private static final String CONSUMER_KEY = "L7Q5XGkFtS335NRcUkxJ";
    private static final String API_ROOT = "http://opencaching.pl/okapi/";
    private static final String API_SEARCH_AND_RETRIEVE = "services/caches/shortcuts/search_and_retrieve";
    private static final String API_SEARCH_NEAREST = "services/caches/search/nearest";
    private static final String API_RETRIEVE_GEOCACHES = "services/caches/geocaches";
    private static final String FIELDS = "location|name|type|size2|terrain|difficulty|rating|recommendations";

    private static final String[] SIZES = new String[] {"none", "nano", "micro", "small", "regular", "large", "xlarge", "other"};
    private static final String[] TYPES = new String[] {"Traditional", "Multi", "Quiz", "Virtual", "Event"};

    private static Map<String,GeoCache> loadedCaches = new HashMap<>();

    private static void refreshLoadedCaches(List<GeoCache> geoCaches) {
        loadedCaches.clear();
        for (GeoCache i : geoCaches) loadedCaches.put(i.getCode(), i);
    }

    private static String generateCacheCode() {
        Random rand = new Random();
        int min = 0x1000;
        int max = 0xFFFF;
        int myRandomNumber = min + rand.nextInt(max - min);
        String result = "OP" + Integer.toHexString(myRandomNumber);
        while (loadedCaches.containsKey(result)) {
            myRandomNumber = rand.nextInt(max);
            result = "OP" + Integer.toHexString(myRandomNumber);
        }
        return result.toUpperCase();
    }

    public static String[] GetAvailableSizes() {
        return SIZES;
    }

    public static String[] GetAvailableTypes() {
        return TYPES;
    }

    public static GeoCache GetGeoCache(String cacheCode) {
        return loadedCaches.get(cacheCode);
    }

    public static boolean IsGeoCacheLoaded(String cacheCode) {
        return loadedCaches.containsKey(cacheCode);
    }

    public static String AddGeoCache(String name, String type, String size, double terrain_difficulty,
                                   double general_difficulty, double latitude, double longitude) {
        String newCacheCode = generateCacheCode();
        GeoCache geoCache = new GeoCache(newCacheCode, name, type, size, terrain_difficulty, general_difficulty,
                0, 0, latitude, longitude);
        loadedCaches.put(newCacheCode, geoCache);
        return newCacheCode;
    }

    public interface OnCachesLoadedListener {
        void onCachesLoaded(List<GeoCache> caches);
    }

    public static void GetNearestGeoCaches(Location currentLoc, double radius,
                                           final OsmandApplication ctx, final OnCachesLoadedListener listener) {
        String apiUrl = API_ROOT + API_SEARCH_AND_RETRIEVE;

        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("center",
                String.valueOf(currentLoc.getLatitude()) + "|" + String.valueOf(currentLoc.getLongitude()));
        searchParams.put("radius", String.valueOf(radius));

        Map<String, String> retrParams = new HashMap<>();
        retrParams.put("fields", FIELDS);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("consumer_key", CONSUMER_KEY);
        queryParams.put("search_method", API_SEARCH_NEAREST);
        queryParams.put("search_params", new JSONObject(searchParams).toString());
        queryParams.put("retr_method", API_RETRIEVE_GEOCACHES);
        queryParams.put("retr_params", new JSONObject(retrParams).toString());
        queryParams.put("wrap", "true");

        AndroidNetworkUtils.sendRequestAsync(ctx, apiUrl, queryParams, "", new AndroidNetworkUtils.OnRequestResultListener() {
            @Override
            public void onResult(String result) {
                List<GeoCache> cachesList = new ArrayList<>();
                try {
                    JSONObject cachesJson = new JSONObject(result).getJSONObject("results");
                    for(Iterator<String> iter = cachesJson.keys();iter.hasNext();) {
                        String cacheCode = iter.next();
                        JSONObject fieldsJson = cachesJson.getJSONObject(cacheCode);
                        String[] location = fieldsJson.getString("location").split("\\|");
                        double lat = Double.parseDouble(location[0]);
                        double lon = Double.parseDouble(location[1]);
                        cachesList.add(new GeoCache(cacheCode, fieldsJson.getString("name"),
                                fieldsJson.getString("type"), fieldsJson.getString("size2"),
                                fieldsJson.getDouble("terrain"), fieldsJson.getDouble("difficulty"),
                                fieldsJson.getDouble("rating"), fieldsJson.getDouble("recommendations"),
                                lat, lon));
                    }
                    refreshLoadedCaches(cachesList);
                } catch (Exception e) {
                    ctx.showToastMessage("Wystąpił błąd przy parsowaniu odpowiedzi z opencaching.pl!");
                    e.printStackTrace();
                } finally {
                    listener.onCachesLoaded(cachesList);
                }
            }
        });
    }
}
