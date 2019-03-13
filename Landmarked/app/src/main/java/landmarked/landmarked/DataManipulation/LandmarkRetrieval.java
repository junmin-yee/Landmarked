package landmarked.landmarked.DataManipulation;

import android.location.Location;
import android.util.Log;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;

public class LandmarkRetrieval {
    final int EARTH_RADIUS = 6378137;
    final double BEARING_ERROR = 0.01;
    final double DIRECTION_SHIFT = 0.05;

    private SensorData mSensorData;
    private Location mCurrLocation;
    private Location mGeoCodeSWLocation;
    private Location mGeoCodeNELocation;
    public List<CarmenFeature> mRevResults;
    public List<CarmenFeature> mFwdResults;
    public List<CarmenFeature> mRetResults;

    // These categories limit the search results (or, at least, heavily bias them)
    // Need more categories or perhaps have the user define the search???
    //public final String mLandmarkCategories = "lake, water, natural, historic site, historic, forest, woods, mountain, hill, stadium, arena, field"; // expanded list - perhaps unnecessary?
    public final String mLandmarkCategories[] = {"natural", "historic", "tourism", "arena"}; // some basic categories... I've found that nearly all lakes, rivers, etc have the keyword "natural" and basically all else includes "historic" or "tourism"

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

        // Create new location of distance away
        Location max = new Location("Provider");
        max.setLatitude(mCurrLocation.getLatitude() + (180/Math.PI)*(y/EARTH_RADIUS));
        max.setLongitude(mCurrLocation.getLongitude() + (180/Math.PI)*(x/EARTH_RADIUS)/Math.cos(mCurrLocation.getLatitude()));

        return max;
    }

    private void CalculateBoundaryBox()
    {
        // Initial setup
        mGeoCodeSWLocation = mCurrLocation;
        mGeoCodeNELocation = CalculateMaxLineofSight();
        Location temp; // For swapping value use

        double testlat = mGeoCodeNELocation.getLatitude() - mGeoCodeSWLocation.getLatitude();
        double testlong = mGeoCodeNELocation.getLongitude() - mGeoCodeSWLocation.getLongitude();
        // Test if looking directly North within error
        if (testlat > 0 && Math.abs(testlong) < BEARING_ERROR)
        {
            // Shift values in each direction
            mGeoCodeSWLocation.setLongitude(mGeoCodeSWLocation.getLongitude() - DIRECTION_SHIFT);
            mGeoCodeNELocation.setLongitude(mGeoCodeNELocation.getLongitude() + DIRECTION_SHIFT);
        }
        // Test if looking directly South within error
        else if (testlat < 0 && Math.abs(testlong) < BEARING_ERROR)
        {
            // Swap location points
            temp = mGeoCodeNELocation;
            mGeoCodeNELocation = mGeoCodeSWLocation;
            mGeoCodeSWLocation = temp;

            // Shift values in each direction
            mGeoCodeSWLocation.setLongitude(mGeoCodeSWLocation.getLongitude() - DIRECTION_SHIFT);
            mGeoCodeNELocation.setLongitude(mGeoCodeNELocation.getLongitude() + DIRECTION_SHIFT);
        }
        // Test if looking directly West within error
        else if (testlong < 0 && Math.abs(testlat) < BEARING_ERROR)
        {
            // Swap location points
            temp = mGeoCodeNELocation;
            mGeoCodeNELocation = mGeoCodeSWLocation;
            mGeoCodeSWLocation = temp;

            // Shift values in each direction
            mGeoCodeSWLocation.setLatitude(mGeoCodeSWLocation.getLatitude() - DIRECTION_SHIFT);
            mGeoCodeNELocation.setLatitude(mGeoCodeNELocation.getLatitude() + DIRECTION_SHIFT);
        }
        // Test if looking directly East
        else if (testlong > 0 && Math.abs(testlat) < BEARING_ERROR)
        {
            // Shift values in each direction
            mGeoCodeSWLocation.setLatitude(mGeoCodeSWLocation.getLatitude() - DIRECTION_SHIFT);
            mGeoCodeNELocation.setLatitude(mGeoCodeNELocation.getLatitude() + DIRECTION_SHIFT);
        }
        // Test if looking Northwest
        else if (testlat > 0 && testlong < 0)
        {
            temp = mGeoCodeNELocation;
            temp.setLatitude(mGeoCodeSWLocation.getLatitude());
            mGeoCodeNELocation.setLongitude(mGeoCodeSWLocation.getLongitude());
            mGeoCodeSWLocation = temp;
        }
        // Test if looking Southwest
        else if (testlat < 0 && testlong < 0)
        {
            // Swap location points
            temp = mGeoCodeNELocation;
            mGeoCodeNELocation = mGeoCodeSWLocation;
            mGeoCodeSWLocation = temp;
        }
        // Test if looking Southeast
        else if (testlat < 0 && testlong > 0)
        {
            temp = mGeoCodeNELocation;
            temp.setLongitude(mGeoCodeSWLocation.getLongitude());
            mGeoCodeNELocation.setLatitude(mGeoCodeSWLocation.getLatitude());
            mGeoCodeSWLocation = temp;
        }
        // Otherwise facing Northeast and values are good
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

    private void ProximityForwardGeocodeSearch(Location location, String category){
        mCurrLocation = location;

        Location proximity_search = CalculateMaxLineofSight(); // NEEDS TO GET CHANGED TO USE BOUNDARY BOX
        // USE mGeoCodeSWLocation and mGeoCodeNELocation points to create

        // Hardcoded "lake" for testing purposes - must set up a better way. One potential solution (but very inefficient) would be to have separate queries for each type of landmark.
        String query_string = category + " near " + mRevResults.get(0).placeName(); //+ mCurrLocation.getLongitude() + ", " + mCurrLocation.getLatitude();

        // Sets Access Token
        // Constructs query based on search criteria defined in "query_string".
        // Proximity to point - currently at current user location.
        // Maximum limit of results for forward geocoding is 10.
        // Build simply constructs the query, must be at end.
        MapboxGeocoding forwardGeocode = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoicmVkZ3JlZWQ0IiwiYSI6ImNqb2k3NXNpNjAyMGEzcXBhbThoeXBtOGcifQ.AG9JmnzPQKHuSxazOvrk3g")
                .query(query_string)
                .proximity(Point.fromLngLat(proximity_search.getLongitude(), proximity_search.getLatitude()))      // Useful for setting a bias of results toward a specific point - Calculate point in front of user?
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
    public void LandmarkProximitySearch(Location location) {

        ReverseGeocodeSearch(location);
        if(mRevResults != null) {
            for (int iterator = 0; iterator < mLandmarkCategories.length; iterator++) {
                ProximityForwardGeocodeSearch(location, mLandmarkCategories[iterator]);
                if (mFwdResults != null) {
                    if (mRetResults == null) {
                        mRetResults = mFwdResults;
                    }
                    mRetResults.removeAll(mFwdResults); // remove any objects existing in mFwdResults from mRetResults.
                    mRetResults.addAll(mFwdResults); // add all objects in mFwdResults to mRetResults.
                }
            }
        }
    }

    public List<CarmenFeature> getLandmarkSearchResults() {

        // Return list of Carmen Features.
        List<CarmenFeature> retResults = this.mRetResults;
        return retResults;
    }

}
