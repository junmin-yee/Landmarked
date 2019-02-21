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
    final int EARTH_RADIUS = 6378137;

    private SensorData mSensorData;
    private Location mCurrLocation;
    List<CarmenFeature> mRevResults;
    List<CarmenFeature> mFwdResults;

    // These categories limit the search results (or, at least, heavily bias them)
    // Need more categories or perhaps have the user define the search???
    public final String mLandmarkCategories = "lake, water, natural, historic site, historic, forest, woods, mountain, hill, stadium, arena, field";

    public LandmarkRetrieval() {

    }

    public LandmarkRetrieval(SensorData sensorData) {
        mSensorData = sensorData;

    }
    
        // Calculate line of sight based on sensor data
    private Location CalculateMaxLineofSight()
    {
        // Variable for max line of sight distance in meters
        int losdistance = 0;
        // Get current pitch and roll
        float pitch = mSensorData.getCurrentOrientation()[1];
        float roll = mSensorData.getCurrentOrientation()[2];

        // Check phone pitch
        if (pitch < -Math.PI/4)
            losdistance = 5000;
        else if (pitch > -Math.PI/4 && pitch < 0)
            losdistance = 2000;

        // Calculate change in distance in Cartesian
        double x = losdistance*Math.sin(pitch)*Math.cos(roll);
        double y = losdistance*Math.sin(pitch)*Math.sin(roll);
        //double z = losdistance*Math.cos(pitch);

        // Create new loaction of distance away
        Location max = new Location("Provider");
        max.setLatitude(mCurrLocation.getLatitude() + (180/Math.PI)*(y/6378137));
        max.setLongitude(mCurrLocation.getLongitude() + (180/Math.PI)*(x/6378137)/Math.cos(mCurrLocation.getLatitude()));

        return max;
    }

    private void ReverseGeocodeSearch(final Location location) {
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
                //.query(Point.fromLngLat(-122.139053, 41.021809))
                .query(Point.fromLngLat(mCurrLocation.getLongitude(), mCurrLocation.getLatitude()))
                //.proximity(Point)      // Useful for setting a bias of results toward a specific point - Calculate point in front of user?
                .limit(5)
                .geocodingTypes(GeocodingCriteria.TYPE_PLACE)
                .build();

        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                mRevResults = response.body().features();

                if (mRevResults.size() > 0) {

                    // Log the location of response.
                    Log.d(TAG, "onResponse: " + mRevResults.size() + " results at " + location.toString());

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

    private void ForwardGeocodeSearch(Location location){
        mCurrLocation = location;

        ReverseGeocodeSearch(location);

        // Hardcoded "lake" for testing purposes - must set up a better way. One potential solution (but very inefficient) would be to have separate queries for each type of landmark.
        String query_string = "lake near " + mRevResults.get(0).placeName();//+ mCurrLocation.getLongitude() + ", " + mCurrLocation.getLatitude();

        // Sets Access Token
        // Constructs query based on search criteria defined in "query_string".
        // Proximity to point - currently at current user location.
        // Maximum limit of results for forward geocoding is 10.
        // Build simply constructs the query, must be at end.
        MapboxGeocoding forwardGeocode = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoicmVkZ3JlZWQ0IiwiYSI6ImNqb2k3NXNpNjAyMGEzcXBhbThoeXBtOGcifQ.AG9JmnzPQKHuSxazOvrk3g")
                .query(query_string)
                .proximity(Point.fromLngLat(mCurrLocation.getLongitude(), mCurrLocation.getLatitude()))      // Useful for setting a bias of results toward a specific point - Calculate point in front of user?
                .limit(10)
                .build();

        forwardGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                mFwdResults = response.body().features();

                if (mFwdResults.size() > 0) {

                    // Log the location of response.
                    Log.d(TAG, "onResponse: " + mFwdResults.size() + " results at " + location.toString());

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
    public void LandmarkSearch(Location location, List<String> categories) {

        //ReverseGeocodeSearch(location);
        ForwardGeocodeSearch(location);

        // Use mResults and a set of defined-properties for the user to display. Filter GeoJSON if necessary.
        // mResults


        // A non-exhaustive list of categories is: lake, water, natural, historic site, historic, forest, woods
        //mResults.get(0).properties().get("category");


    }

    public List<CarmenFeature> getCarmenFeatureResult() {

        // Return list of Carmen Features.
        return mFwdResults;
    }

}
