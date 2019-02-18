package landmarked.landmarked;

import android.location.Location;
import android.util.Log;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;

public class LandmarkRetrieval {

    private SensorData mSensorData;
    private Location mCurrLocation;
    List<CarmenFeature> mResults;

    public LandmarkRetrieval() {

    }

    public LandmarkRetrieval(SensorData sensorData) {
        mSensorData = sensorData;

    }

    private void buildGeocodeSearch(final Location location) {
        mCurrLocation = location;

        // Sets Access Token
        // Constructs query based on current location.
        // Maximum limit of results for reverse geocoding is 5.
        // Filters the search to "Points of Interest - Landmark" in TYPE_POI_LANDMARK
        //      This MAY not be exactly what we want - I'm unsure of everything this entails.
        //      Delete the "_LANDMARK" to get all POI.
        // Build simply constructs the query, must be at end.
        MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoicmVkZ3JlZWQ0IiwiYSI6ImNqb2k3NXNpNjAyMGEzcXBhbThoeXBtOGcifQ.AG9JmnzPQKHuSxazOvrk3g")
                .query(Point.fromLngLat(mCurrLocation.getLongitude(), mCurrLocation.getLatitude()))
                //.proximity(Point)      // Useful for setting a bias of results toward a specific point - Calculate point in front of user?
                .limit(5)
                .geocodingTypes(GeocodingCriteria.TYPE_POI_LANDMARK)
                .build();

        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                mResults = response.body().features();

                if (mResults.size() > 0) {

                    // Log the location of response.
                    Log.d(TAG, "onResponse: " + mResults.size() + " results at " + location.toString());

                } else {

                    // No results were found.
                    Log.d(TAG, "onResponse: No result found");

                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                // Failed to send request
                throwable.printStackTrace();
            }
        });
    }

    // Collect nearby Features from Reverse Geocode Search
    public void FilterFeatures(Location location) {

        buildGeocodeSearch(location);

        // Use mResults and a set of defined-properties for the user to display. Filter GeoJSON if necessary.
        // mResults

    }

    public List<CarmenFeature> getCarmenFeatureResult() {

        // Return list of Carmen Features.
        return mResults;
    }

}
