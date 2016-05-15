package net.osmand.geocaching;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import net.osmand.data.LatLon;
import net.osmand.data.PointDescription;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.views.DoubleTapScaleDetector;

/**
 * Created by mwinnick on 5/15/2016.
 */
public class NewGeoCacheDialog {

    public static void Open(final MapActivity ctx, final double latitude, final double longitude) {
        LayoutInflater li = LayoutInflater.from(ctx);
        View dialogView = li.inflate(R.layout.dialog_add_geocache, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
        dialogBuilder.setView(dialogView);

        // Name
        final EditText CacheNameEditText = (EditText)dialogView.findViewById(R.id.CacheNameEditText);

        // Type
        final AutoCompleteTextView CacheTypeTextSpinner = (AutoCompleteTextView)dialogView.findViewById(R.id.CacheTypeTextSpinner);
        ArrayAdapter<String> typeDataAdapter = new ArrayAdapter<String>
                (ctx, android.R.layout.select_dialog_item, GeoCachingUtils.GetAvailableTypes());
        CacheTypeTextSpinner.setThreshold(1);
        CacheTypeTextSpinner.setAdapter(typeDataAdapter);

        // Size
        final Spinner CacheSizeSpinner = (Spinner)dialogView.findViewById(R.id.CacheSizeSpinner);
        ArrayAdapter<String> sizeDataAdapter = new ArrayAdapter<String>(ctx,
                android.R.layout.simple_spinner_item, GeoCachingUtils.GetAvailableSizes());
        sizeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CacheSizeSpinner.setAdapter(sizeDataAdapter);

        // Difficulties
        final EditText CacheGeneralDiffEditText = (EditText)dialogView.findViewById(R.id.CacheGeneralDiffEdiText);
        final EditText CacheTerrainDiffEditText = (EditText)dialogView.findViewById(R.id.CacheTerrainDiffEditText);

        dialogBuilder.setTitle("Podaj dane skrzynki:")
                .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String generalDiff = CacheGeneralDiffEditText.getText().toString();
                String terrainDiff = CacheTerrainDiffEditText.getText().toString();
                if (generalDiff.equals("") || terrainDiff.equals("") ||
                        Double.valueOf(generalDiff) < 1 || Double.valueOf(generalDiff) > 5 ||
                        Double.valueOf(terrainDiff) < 1 || Double.valueOf(terrainDiff) > 5) {
                    ctx.getMyApplication()
                            .showToastMessage("Trudnosc skrzynki musi zawierac sie w przedziale 1-5");
                    return;
                }

                String name = CacheNameEditText.getText().toString();
                String type = CacheTypeTextSpinner.getText().toString();
                String size = CacheSizeSpinner.getSelectedItem().toString();

                String cacheCode = GeoCachingUtils.AddGeoCache(name, type, size, Double.valueOf(terrainDiff),
                        Double.valueOf(generalDiff), latitude, longitude);

                ctx.getMapActions().addMapMarker(latitude, longitude,
                        new PointDescription(PointDescription.POINT_TYPE_FAVORITE, cacheCode));

                dialog.dismiss();

                ctx.getMyApplication()
                        .showToastMessage(String.format("Skrzynka o id %s zostala dodana!", cacheCode));

                ctx.getContextMenu().close();
            }
        });
    }
}
