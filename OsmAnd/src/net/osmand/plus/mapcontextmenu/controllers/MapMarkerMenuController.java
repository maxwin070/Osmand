package net.osmand.plus.mapcontextmenu.controllers;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import net.osmand.data.PointDescription;
import net.osmand.geocaching.GeoCache;
import net.osmand.geocaching.GeoCacheDetailsActivity;
import net.osmand.geocaching.GeoCachingUtils;
import net.osmand.plus.MapMarkersHelper;
import net.osmand.plus.MapMarkersHelper.MapMarker;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.helpers.MapMarkerDialogHelper;
import net.osmand.plus.mapcontextmenu.MenuBuilder;
import net.osmand.plus.mapcontextmenu.MenuController;
import net.osmand.util.Algorithms;

import static android.support.v4.app.ActivityCompat.startActivity;

public class MapMarkerMenuController extends MenuController {

	private MapMarker mapMarker;

	public MapMarkerMenuController(final OsmandApplication app, MapActivity mapActivity, PointDescription pointDescription, MapMarker mapMarker) {
		super(new MenuBuilder(app), pointDescription, mapActivity);
		this.mapMarker = mapMarker;
		final MapMarkersHelper markersHelper = app.getMapMarkersHelper();
		leftTitleButtonController = new TitleButtonController() {
			@Override
			public void buttonPressed() {
				markersHelper.removeMapMarker(getMapMarker().index);
				markersHelper.addMapMarkerHistory(getMapMarker());
				getMapActivity().getContextMenu().close();
			}
		};
		leftTitleButtonController.caption = getMapActivity().getString(R.string.shared_string_remove);
		leftTitleButtonController.leftIconId = R.drawable.ic_action_delete_dark;

		final String cacheCode = getMapMarker().getOnlyName();
		if (GeoCachingUtils.IsGeoCacheLoaded(cacheCode)) {
			rightTitleButtonController = new TitleButtonController() {
				@Override
				public void buttonPressed() {
					getMapActivity().getContextMenu().close();
					Intent myIntent = new Intent(app, GeoCacheDetailsActivity.class);
					myIntent.putExtra("cacheCode", cacheCode);
					startActivity(getMapActivity(), myIntent, null);
			}
			};
			rightTitleButtonController.caption = String.format("Poka%c szczeg%c%cy", (char)0x017c, (char)0x00f3, (char)0x0142);
			rightTitleButtonController.leftIconId = R.drawable.ic_action_additional_option;
		}
	}

	@Override
	protected void setObject(Object object) {
		if (object instanceof MapMarker) {
			this.mapMarker = (MapMarker) object;
		}
	}

	public MapMarker getMapMarker() {
		return mapMarker;
	}

	@Override
	protected int getSupportedMenuStatesPortrait() {
		return MenuState.HEADER_ONLY | MenuState.HALF_SCREEN;
	}

	@Override
	public boolean needTypeStr() {
		return !Algorithms.isEmpty(getNameStr());
	}

	@Override
	public boolean displayDistanceDirection() {
		return true;
	}

	@Override
	public Drawable getLeftIcon() {
		return MapMarkerDialogHelper.getMapMarkerIcon(getMapActivity().getMyApplication(), mapMarker.colorIndex);
	}

	@Override
	public String getTypeStr() {
		return mapMarker.getPointDescription(getMapActivity()).getTypeName();
	}

	@Override
	public boolean needStreetName() {
		return !needTypeStr();
	}
}