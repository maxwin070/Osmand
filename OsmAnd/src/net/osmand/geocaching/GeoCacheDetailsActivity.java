package net.osmand.geocaching;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import net.osmand.StringMatcher;
import net.osmand.plus.R;
import net.osmand.plus.activities.OsmandActionBarActivity;

public class GeoCacheDetailsActivity extends OsmandActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String cacheCode = i.getStringExtra("cacheCode");

        setContentView(R.layout.activity_geo_cache_details);
        setTitle(String.format("Skrzynka: %s", cacheCode));
        setupHomeButton();

        GeoCache geoCache = GeoCachingUtils.GetGeoCache(cacheCode);

        // basic string fields
        TextView CacheCodeText = (TextView)findViewById(R.id.CacheCodeText);
        CacheCodeText.setText(cacheCode);
        TextView CacheNameText = (TextView)findViewById(R.id.CacheNameText);
        CacheNameText.setText(geoCache.getName());
        TextView CacheTypeText = (TextView)findViewById(R.id.CacheTypeText);
        CacheTypeText.setText(geoCache.getType());
        TextView CacheSizeText = (TextView)findViewById(R.id.CacheSizeText);
        CacheSizeText.setText(geoCache.getSize());
        TextView CacheLocationText = (TextView)findViewById(R.id.CacheLocationText);
        CacheLocationText.setText(geoCache.GetFormattedLocation());

        // cache difficulties
        TextView CacheGeneralDiffText = (TextView)findViewById(R.id.CacheGeneralDiffText);
        CacheGeneralDiffText.setText(String.format("%.2f / 5", geoCache.getGeneral_difficulty()));
        TextView CacheTerrainDiffText = (TextView)findViewById(R.id.CacheTerrainDiffText);
        CacheTerrainDiffText.setText(String.format("%.2f / 5", geoCache.getTerrain_difficulty()));

        // recommendations and rating
        TextView CacheRecommText = (TextView)findViewById(R.id.CacheRecommText);
        CacheRecommText.setText(String.format("%.0f", geoCache.getRecommendations()));
        TextView CacheRatingText = (TextView)findViewById(R.id.CacheRatingText);
        if (geoCache.getRating() > 0) {
            CacheRatingText.setText(String.format("%.2f", geoCache.getRating()));
            CacheRatingText.setTextColor(geoCache.GetRatingColor());
        }
        else {
            CacheRatingText.setText("-");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
