package landmarked.landmarked;

import android.location.Location;

import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
//import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.geometry.LatLng;

public class LandmarkRetrieval {

    private SensorData mSensorData;
    private Location mCurrLocation;
    private FeatureCollection mFeatureCollection;
    private LatLng mLatLng;

    public LandmarkRetrieval() {

    }

    public LandmarkRetrieval(SensorData sensorData) {
        mSensorData = sensorData;

    }

    private void makeGeocodeSearch(final LatLng latLng) {

        // In progress
    }

    // Collect nearby Features from Reverse Geocode Search
    public void FilterFeatures(Location location) {
        mCurrLocation = location;

        // Construct basic custom landmark "point" feature for testing
        /*Feature feature = Feature.fromGeometry(Point.fromLngLat(mCurrLocation.getLongitude(), mCurrLocation.getLatitude()));
        //System.out.println(feature.toJson());*/

        // Make a Latlng object using a location object.
        mLatLng = new LatLng(mCurrLocation);
        makeGeocodeSearch(mLatLng);

    }

    public FeatureCollection getFeatureCollection() {

        // Return list of Carmen Features.
        return mFeatureCollection;
    }

}
