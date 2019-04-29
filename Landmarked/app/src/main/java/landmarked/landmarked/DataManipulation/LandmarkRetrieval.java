package landmarked.landmarked.DataManipulation;

import android.location.Location;
import android.util.Log;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import landmarked.landmarked.LandmarkedMain;
import landmarked.landmarked.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;

public class LandmarkRetrieval {
    final int EARTH_RADIUS = 6378137;
    final double BEARING_ERROR = 0.01;
    final double DIRECTION_SHIFT = 0.15;
    final int FIELD_OF_VIEW_DEGREE = 3;

    private SensorData mSensorData;
    private Location mCurrLocation;
    private Location mGeoCodeSWLocation;
    private Location mGeoCodeNELocation;
    private float mLeftField;
    private float mRightField;
    private List<CarmenFeature> mRevResults;
    private List<CarmenFeature> mFwdResults;
    private Set<CarmenFeature> mProximityResults;
    private Set<CarmenFeature> mBoundaryBoxResults;
    private boolean sensorInfoSet;

    // These categories limit the search results (or, at least, heavily bias them)
    // Need more categories or perhaps have the user define the search???
    //public final String mLandmarkCategories = "lake, water, natural, historic site, historic, forest, woods, mountain, hill, stadium, arena, field"; // expanded list - perhaps unnecessary?
    public final String mLandmarkCategories[] = {"lake", "river", "natural", "historic", "tourism", "arena"}; // some basic categories... I've found that nearly all lakes, rivers, etc have the keyword "natural" and basically all else includes "historic" or "tourism"

    public LandmarkRetrieval() {

        mProximityResults = new HashSet<>();
        mBoundaryBoxResults = new HashSet<>();

        sensorInfoSet = false;
    }

    // Set the sensor information
    public void SetSensorInformation(SensorData sensorData) {

        mSensorData = sensorData;
        mCurrLocation = mSensorData.getCurrentLocation();

        sensorInfoSet = true;
    }

    // Calculate line of sight based on sensor data
    private Location CalculateMaxLineofSight()
    {
        // Variable for max line of sight distance in meters
        int losdistance = 0;
        // Get current pitch and roll
        float azimuth = mSensorData.getCurrentOrientation()[0];
        float pitch = mSensorData.getCurrentOrientation()[1];
        float roll = mSensorData.getCurrentOrientation()[2];

        // Check phone pitch
        if (pitch < -Math.PI/4)
            losdistance = 10000;
        else if (pitch > -Math.PI/4)// && pitch < 0)
            losdistance = 5000;

        // Calculate change in distance in Cartesian
        double x = losdistance*Math.sin(pitch)*Math.cos(roll);
        double y = losdistance*Math.sin(pitch)*Math.sin(roll);
        //double z = losdistance*Math.cos(pitch);

        // Create new location of distance away
        Location max = new Location("Provider");
        max.setLatitude(mCurrLocation.getLatitude() + (180/Math.PI)*(y/EARTH_RADIUS));
        max.setLongitude(mCurrLocation.getLongitude() +
                (180/Math.PI)*(x/EARTH_RADIUS)/Math.cos(mCurrLocation.getLatitude()));

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
        if (testlat >= 0 && Math.abs(testlong) < BEARING_ERROR)
        {
            // Shift values in each direction
            mGeoCodeSWLocation.setLongitude(mGeoCodeSWLocation.getLongitude() - DIRECTION_SHIFT);
            mGeoCodeNELocation.setLongitude(mGeoCodeNELocation.getLongitude() + DIRECTION_SHIFT);
        }
        // Test if looking directly South within error
        else if (testlat <= 0 && Math.abs(testlong) < BEARING_ERROR)
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
        else if (testlong <= 0 && Math.abs(testlat) < BEARING_ERROR)
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
        else if (testlong >= 0 && Math.abs(testlat) < BEARING_ERROR)
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

    private void CalculateFieldofView()
    {
        Location search = CalculateMaxLineofSight();

        // Set current bearing
        mCurrLocation.setBearing(mCurrLocation.bearingTo(search));

        mLeftField = mCurrLocation.getBearing() - FIELD_OF_VIEW_DEGREE;
        mRightField = mCurrLocation.getBearing() + FIELD_OF_VIEW_DEGREE;
    }

    private boolean CheckFieldofView(Location location)
    {
        float testBearing;
        boolean isWithinField = false;

        testBearing = mCurrLocation.bearingTo(location);

        if (testBearing >= mLeftField && testBearing <= mRightField)
            isWithinField = true;

        return isWithinField;
    }

    private void ReverseGeocodeSearch() {

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
                .limit(5)
                .geocodingTypes(GeocodingCriteria.TYPE_PLACE)
                .build();

        // Synchronous execution of the mapbox api request. Blocks GUI thread. Display loading screen.
        try {
            mRevResults = reverseGeocode.executeCall().body().features();

            if (mRevResults.size() > 0) {

                // Log the location of response.
                Log.d(TAG, "ReverseGeocodeSearch: " + mRevResults.size() + " results at " + mCurrLocation.toString());

            } else {

                // No results were found.
                Log.d(TAG, "ReverseGeocodeSearch: No result found");

            }
        }
        catch(java.io.IOException e){
            Log.d(TAG, "ReverseGeocodeSearch: java.io.IOException");
        }

        /*
        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                mRevResults = response.body().features();

                if (mRevResults.size() > 0) {

                    // Log the location of response.
                    Log.d(TAG, "ReverseGeocodeSearch: " + mRevResults.size() + " results at " + mCurrLocation.toString());

                } else {

                    // No results were found.
                    Log.d(TAG, "ReverseGeocodeSearch: No result found");

                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                // Failed to send request
                throwable.printStackTrace();
            }
        });*/
    }

    // Proximity based forward geocode search on point generated in front of location.
    private void ProximityForwardGeocodeSearch(Location proximity_point, String category){

        Location proximity_search = CalculateMaxLineofSight();
        // USE mGeoCodeSWLocation and mGeoCodeNELocation points to create

        // Searches for a basic type of landmark - only works because we base results around a proximity point.
        String query_string = category + " near " + mRevResults.get(0).placeName(); //+ mCurrLocation.getLongitude() + ", " + mCurrLocation.getLatitude();

        // Sets Access Token
        // Constructs query based on search criteria defined in "query_string".
        // Proximity to point - currently at current user location.
        // Maximum limit of results for forward geocoding is 10.
        // Build simply constructs the query, must be at end.
        MapboxGeocoding forwardGeocode = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoicmVkZ3JlZWQ0IiwiYSI6ImNqb2k3NXNpNjAyMGEzcXBhbThoeXBtOGcifQ.AG9JmnzPQKHuSxazOvrk3g")
                .query(query_string)
                .proximity(Point.fromLngLat(proximity_search.getLongitude(), proximity_search.getLatitude()))
                .limit(10)
                .build();

        // Synchronous execution of the mapbox api request. Blocks GUI thread. Display loading screen.
        try {
            mFwdResults = forwardGeocode.executeCall().body().features();

            if (mFwdResults.size() > 0) {

                // Log the location of response.
                Log.d(TAG, "ProximityForwardGeocodeSearch: " + mFwdResults.size() + " results at " + mCurrLocation.toString());

            } else {

                // No results were found.
                Log.d(TAG, "ProximityForwardGeocodeSearch: No result found");

            }
        }
        catch(java.io.IOException e){
            Log.d(TAG, "ProximityForwardGeocodeSearch: java.io.IOException");
        }

        /*
        forwardGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                mFwdResults = response.body().features();

                if (mFwdResults.size() > 0) {

                    // Log the location of response.
                    Log.d(TAG, "ProximityForwardGeocodeSearch: " + mFwdResults.size() + " results at " + mCurrLocation.toString());

                } else {

                    // No results were found.
                    Log.d(TAG, "ProximityForwardGeocodeSearch: No result found");

                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                // Failed to send request
                throwable.printStackTrace();
            }
        });*/
    }

    // Boundary Box Forward Geocode search based on BB in front of user.
    private void BoundaryBoxForwardGeocodeSearch(Point Southwest, Point Northeast, String category){

        // Hardcoded "lake" for testing purposes - must set up a better way. One potential solution (but very inefficient) would be to have separate queries for each type of landmark.
        String query_string = category; // search by category. left as separate local variable for experimenting.

        // Sets Access Token
        // Constructs query based on search criteria defined in "query_string".
        // Proximity to point - currently at current user location.
        // Maximum limit of results for forward geocoding is 10.
        // Build simply constructs the query, must be at end.
        MapboxGeocoding forwardGeocode = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoicmVkZ3JlZWQ0IiwiYSI6ImNqb2k3NXNpNjAyMGEzcXBhbThoeXBtOGcifQ.AG9JmnzPQKHuSxazOvrk3g")
                .query(query_string)
                .bbox(Southwest, Northeast)
                .proximity(Point.fromLngLat(mCurrLocation.getLongitude(), mCurrLocation.getLatitude()))
                .limit(10)
                .build();

        // Synchronous execution of the mapbox api request. Blocks GUI thread. Display loading screen.
        try {
            mFwdResults = forwardGeocode.executeCall().body().features();

            if (mFwdResults.size() > 0) {

                // Log the location of response.
                Log.d(TAG, "BoundaryBoxForwardGeocodeSearch: " + mFwdResults.size() + " results at " + mCurrLocation.toString());

            } else {

                // No results were found.
                Log.d(TAG, "BoundaryBoxForwardGeocodeSearch: No result found");

            }
        }
        catch(java.io.IOException e){
            Log.d(TAG, "BoundaryBoxForwardGeocodeSearch: java.io.IOException");
        }

    /*
        forwardGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                mFwdResults = response.body().features();

                if (mFwdResults.size() > 0) {

                    // Log the location of response.
                    Log.d(TAG, "BoundaryBoxForwardGeocodeSearch: " + mFwdResults.size() + " results at " + mCurrLocation.toString());

                } else {

                    // No results were found.
                    Log.d(TAG, "BoundaryBoxForwardGeocodeSearch: No result found");

                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                // Failed to send request
                throwable.printStackTrace();
            }
        });*/
    }

    // Collect nearby Features from Proximity Geocode Search
    public int LandmarkProximitySearch() {

        // If sensor info hasn't been set, return.
        if (!sensorInfoSet) {
            return -1;
        }

        ReverseGeocodeSearch();
        if(mRevResults != null) {
            Location proximityPoint = CalculateMaxLineofSight();

            for (int iterator = 0; iterator < mLandmarkCategories.length; iterator++) {
                ProximityForwardGeocodeSearch(proximityPoint, mLandmarkCategories[iterator]);

                if (mFwdResults != null) {
                    mProximityResults.addAll(mFwdResults);
                }
            }
        }

        // number of results returned
        return mProximityResults.size();
    }

    // Collect nearby Features from Boundary Box Geocode Search
    public int LandmarkBoundaryBoxSearch() {

        // If sensor info hasn't been set, return.
        if (!sensorInfoSet) {
            return -1;
        }

        // Calculate boundary box settings given current user location and
        CalculateBoundaryBox();

        // Calculate the field of view that the user is looking for
        //CalculateFieldofView();

        // USE mGeoCodeSWLocation and mGeoCodeNELocation points to create the boundary box points for search
        Point SWPoint = Point.fromLngLat(mGeoCodeSWLocation.getLongitude(), mGeoCodeSWLocation.getLatitude());
        Point NEPoint = Point.fromLngLat(mGeoCodeNELocation.getLongitude(), mGeoCodeNELocation.getLatitude());

        for (int iterator = 0; iterator < mLandmarkCategories.length; iterator++) {
            BoundaryBoxForwardGeocodeSearch(SWPoint, NEPoint, mLandmarkCategories[iterator]);

            if (mFwdResults != null) {
                mBoundaryBoxResults.addAll(mFwdResults);
            }
        }

        // number of results returned
        return mBoundaryBoxResults.size();
    } 

    public Set<CarmenFeature> getLandmarkProximitySearchResults() {

        // Return list of Carmen Features.
        Set<CarmenFeature> retResults = this.mProximityResults;
        return retResults;
    }

    public Set<CarmenFeature> getLandmarkBoundaryBoxSearchResults() {

        // Return list of Carmen Features.
        Set<CarmenFeature> retResults = this.mBoundaryBoxResults;
        return retResults;
    }

}
