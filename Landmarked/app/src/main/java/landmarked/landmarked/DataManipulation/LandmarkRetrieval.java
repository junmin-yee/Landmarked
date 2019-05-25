package landmarked.landmarked.DataManipulation;

import android.location.Location;
import android.util.Log;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.support.constraint.Constraints.TAG;

public class LandmarkRetrieval {
    private static final int EARTH_RADIUS = 6378137;
    private static final double BEARING_ERROR = 0.10;
    private static final double DIRECTION_SHIFT = 0.45;
    private static final int FIELD_OF_VIEW_DEGREE = 20;
    private static final int BIGGER_BBOX_OFFSET = 3;
    private static final String TAG = "LandmarkRetrieval";

    private SensorData mSensorData;
    private Location mCurrLocation;
    private Location mGeoCodeSWLocation;
    private Location mGeoCodeNELocation;
    private Location mGeoCodeLSLocation;
    private float mLeftField;
    private float mRightField;
    private List<CarmenFeature> mRevResults;
    private List<CarmenFeature> mFwdResults;
    private Set<CarmenFeature> mProximityResults;
    private Set<CarmenFeature> mBoundaryBoxResults;
    private boolean sensorInfoSet;

    private LandmarkFilter landmarkFilter;

    // These categories limit the search results (or, at least, heavily bias them)
    // Need more categories or perhaps have the user define the search???
    //public final String mLandmarkCategories = "lake, water, natural, historic site, historic, forest, woods, mountain, hill, stadium, arena, field"; // expanded list - perhaps unnecessary?
    public final String mLandmarkCategories[] = {"lake", "river", "natural", "historic", "tourism", "arena"}; // some basic categories... I've found that nearly all lakes, rivers, etc have the keyword "natural" and basically all else includes "historic" or "tourism"

    public LandmarkRetrieval() {

        mProximityResults = new HashSet<>();
        mBoundaryBoxResults = new HashSet<>();

        mGeoCodeNELocation = new Location("");
        mGeoCodeSWLocation = new Location("");

        sensorInfoSet = false;
    }

    // Set the sensor information
    public void SetSensorInformation(SensorData sensorData) {

        mSensorData = sensorData;
        mCurrLocation = mSensorData.getCurrentLocation();

        sensorInfoSet = true;
    }

    // Calculate line of sight based on sensor data
    private void CalculateMaxLineofSight()
    {
        // Variable for max line of sight distance in meters
        int losdistance = 0;
        // Get current pitch and roll and azimuth
        float pitch = mSensorData.getPitch();
        Log.d(TAG, "Current pitch is: " + pitch);
        float direction = mSensorData.getDirectionInDegrees();
        Log.d(TAG, "Current direction is: " + direction);

        double x = 0;
        double y = 0;

        // Check phone pitch
        if (pitch <= 0)
        {
            losdistance = 50000;
            // Calculate change in distance in Cartesian
            //x = losdistance * Math.sin(-Math.PI/2 - pitch) * Math.cos(roll);
            //y = losdistance * Math.sin(-Math.PI/2 - pitch) * Math.sin(roll);
        }
        else if (pitch > 0)
        {
            losdistance = 25000;
            // Calculate change in distance in Cartesian
            //x = losdistance * Math.sin(Math.PI/2 - pitch) * Math.cos(roll);
            //y = losdistance * Math.sin(Math.PI/2 - pitch) * Math.sin(roll);
        }
        x = losdistance*Math.sin(Math.toRadians(direction));
        y = losdistance*Math.cos(Math.toRadians(direction));

        // Set location in front of user
        mGeoCodeNELocation.setLatitude(mCurrLocation.getLatitude() + (180/Math.PI)*(y/EARTH_RADIUS));
        mGeoCodeNELocation.setLongitude(mCurrLocation.getLongitude() +
                (180/Math.PI)*(x/EARTH_RADIUS)/Math.cos((Math.PI/180)*mCurrLocation.getLatitude()));

        // Set location in front of user
        mGeoCodeLSLocation = new Location(mGeoCodeNELocation);

        // Set location behind user
        mGeoCodeSWLocation.setLatitude(mCurrLocation.getLatitude() - (180/Math.PI)*((y/2)/EARTH_RADIUS));
        mGeoCodeSWLocation.setLongitude(mCurrLocation.getLongitude() -
                (180/Math.PI)*((x/2)/EARTH_RADIUS)/Math.cos((Math.PI/180)*mCurrLocation.getLatitude()));
    }

    private void CalculateBoundaryBox()
    {
        // Initial setup
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
            Location temp = new Location(mGeoCodeNELocation);
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
            Location temp = new Location(mGeoCodeNELocation);
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
            Location temp = new Location(mGeoCodeNELocation);
            temp.setLatitude(mGeoCodeSWLocation.getLatitude());
            mGeoCodeNELocation.setLongitude(mGeoCodeSWLocation.getLongitude());
            mGeoCodeSWLocation = temp;
        }
        // Test if looking Southwest
        else if (testlat < 0 && testlong < 0)
        {
            // Swap location points
            Location temp = new Location(mGeoCodeNELocation);
            mGeoCodeNELocation = mGeoCodeSWLocation;
            mGeoCodeSWLocation = temp;
        }
        // Test if looking Southeast
        else if (testlat < 0 && testlong > 0)
        {
            Location temp = new Location(mGeoCodeNELocation);
            temp.setLongitude(mGeoCodeSWLocation.getLongitude());
            mGeoCodeNELocation.setLatitude(mGeoCodeSWLocation.getLatitude());
            mGeoCodeSWLocation = temp;
        }
        // Otherwise facing Northeast and values are good
    }

    /**
     * Calculates and sets the Left and Right FOV of the user.
     *
     * Bearing is set from 0 - 360 degrees based on azimuth.
     */
    private void CalculateFieldofView()
    {
        // Set current bearing
        mCurrLocation.setBearing(mCurrLocation.bearingTo(mGeoCodeLSLocation));

        // Set the left and right field of views.
        mLeftField = mCurrLocation.getBearing() - FIELD_OF_VIEW_DEGREE;
        mRightField = mCurrLocation.getBearing() + FIELD_OF_VIEW_DEGREE;

        // Adjust Field Of Views in case the left FOV is negative or the right FOV is above 360.
        if (mLeftField < 0)
            mLeftField = 360 - mLeftField;
        else if (mRightField > 360)
            mRightField = mRightField - 360;

    }

    /**Check Field of View
     * Requires that CalculateFieldofView has been called.
     *
     * mLeftField and mRightField are from 0-360 degrees. From North at 0 increasing clockwise.
     *
     * Don't think this works if FIELD_OF_VIEW_DEGREE >= 180.
     *
     * @param location
     * @return True if the location is within the FOV, False otherwise.
     */
    private boolean CheckFieldofView(CarmenFeature location)
    {
        float testBearing;
        boolean isWithinField = false;

        // Convert object to appropriate type
        Location location_to = new Location("");

        location_to.setLongitude(location.center().longitude());
        location_to.setLatitude(location.center().latitude());

        testBearing = mCurrLocation.bearingTo(location_to);

        /* testBearing values:
                East side goes from 0 degrees (at N) to 180 degrees (at S)
                West side goes from 0 degrees (at N) to -180 degrees (at S)
         */
        if (testBearing < 0) // if testBearing is negative, align it with the left/right fields.
            testBearing = 180 + (180 + testBearing); // convert to a 0-360 degree value.

        // If either left or right FOV crosses North
        if ((mLeftField + FIELD_OF_VIEW_DEGREE) > 360 || (mRightField - FIELD_OF_VIEW_DEGREE) < 0){
            if (testBearing >= mLeftField || testBearing <= mRightField)
                isWithinField = true;
        }
        // Test the bearing from current location to landmark result to determine if it's within the FOV.
        else if (testBearing >= mLeftField && testBearing <= mRightField)
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

    }

    // Proximity based forward geocode search on point generated in front of location.
    private void ProximityForwardGeocodeSearch(Location proximity_point, String category){

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
                .proximity(Point.fromLngLat(mGeoCodeLSLocation.getLongitude(), mGeoCodeLSLocation.getLatitude()))
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
        catch(java.lang.NullPointerException e){
            Log.d(TAG, "BoundaryBoxForwardGeocodeSearch: NullPointerException. Likely did not find any landmarks");
        }

    }

    // Collect nearby Features from Proximity Geocode Search
    public int LandmarkProximitySearch() {

        // If sensor info hasn't been set, return.
        if (!sensorInfoSet) {
            Log.w(TAG, "Sensor info not set");
            return -1;
        }

        ReverseGeocodeSearch();
        if(mRevResults != null) {
            // Sets the LSLocation
            CalculateMaxLineofSight();

            for (int iterator = 0; iterator < mLandmarkCategories.length; iterator++) {
                ProximityForwardGeocodeSearch(mGeoCodeLSLocation, mLandmarkCategories[iterator]);

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
            Log.w(TAG, "Sensor info not set");
            return -1;
        }

        // Calculate the Line of Sight in the direction the user is facing
        CalculateMaxLineofSight();

        // Calculate boundary box settings given current user location and Line of Sight
        CalculateBoundaryBox();

        // Calculate the field of view that the user is looking for
        CalculateFieldofView();

        // USE mGeoCodeSWLocation and mGeoCodeNELocation points to create the boundary box points for search
        Point SWPoint = Point.fromLngLat(mGeoCodeSWLocation.getLongitude(), mGeoCodeSWLocation.getLatitude());
        Point NEPoint = Point.fromLngLat(mGeoCodeNELocation.getLongitude(), mGeoCodeNELocation.getLatitude());

        // Perform BBox search on various categories. If results are returned, append them together.
        for (int iterator = 0; iterator < mLandmarkCategories.length; iterator++) {
            BoundaryBoxForwardGeocodeSearch(SWPoint, NEPoint, mLandmarkCategories[iterator]);

            if (mFwdResults != null) {
                mBoundaryBoxResults.addAll(mFwdResults);
            }
        }

        /* Basic Landmark Filtering
         * Remove if Landmark is outside the Field of View, is not a valid landmark classification, or is a street.
         * Never remove anything historical.
         */
        if (mBoundaryBoxResults != null)
            mBoundaryBoxResults.removeIf(n -> !LandmarkFilter.isHistorical(n) && (!LandmarkFilter.isValidPlaceType(n) || !CheckFieldofView(n) || !LandmarkFilter.isStreet(n)));

        // number of results returned
        return mBoundaryBoxResults.size();
    }

    public Set<CarmenFeature> getLandmarkProximitySearchResults() {

        // Return list of Carmen Features.
        return this.mProximityResults;
    }

    public Set<CarmenFeature> getLandmarkBoundaryBoxSearchResults() {

        // Return list of Carmen Features.
        return this.mBoundaryBoxResults;
    }

}
