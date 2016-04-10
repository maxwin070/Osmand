package net.osmand.geocaching;

import net.osmand.AndroidNetworkUtils;
import net.osmand.Location;
import net.osmand.plus.OsmandApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by mwinnick on 4/10/2016.
 */
public class GeoCachingUtils {
    private static final String CONSUMER_KEY = "L7Q5XGkFtS335NRcUkxJ";
    private static final String API_ROOT = "http://opencaching.pl/okapi/";
    private static final String API_SEARCH_AND_RETRIEVE = "services/caches/shortcuts/search_and_retrieve";
    private static final String API_SEARCH_NEAREST = "services/caches/search/nearest";
    private static final String API_RETRIEVE_GEOCACHES = "services/caches/geocaches";

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
        retrParams.put("fields", "location");

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
                        cachesList.add(new GeoCache(cacheCode, lat, lon));
                    }
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
