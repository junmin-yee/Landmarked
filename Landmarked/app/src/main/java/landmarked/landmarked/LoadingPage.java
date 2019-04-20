package landmarked.landmarked;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import landmarked.landmarked.DataManipulation.CarmenFeatureHelper;
import landmarked.landmarked.DataManipulation.LandmarkRetrieval;
import landmarked.landmarked.DataManipulation.SensorData;
import landmarked.landmarked.Database.LocalLandmark;

public class LoadingPage extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public SensorData mSensorData;
    public LandmarkRetrieval mLandmarkRetrieval;
    public Location currLocation;
    public float[] currOrientation = new float[3];
    public ArrayList<LocalLandmark> landmarkGet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        checkLocationPermission();

        //Instantiate with this context
        mSensorData = new SensorData(this);

        //Instantiate with existing SensorData object
        mLandmarkRetrieval = new LandmarkRetrieval();

        //Register listeners
        mSensorData.registerOrientationSensors();
        mSensorData.registerLocationSensor();

        currOrientation = mSensorData.getCurrentOrientation();

        try
        {
            currLocation = mSensorData.getCurrentLocation();

            mLandmarkRetrieval.SetSensorInformation(mSensorData);
            mLandmarkRetrieval.LandmarkProximitySearch();

            Set<CarmenFeature> retrievedLandmarks = mLandmarkRetrieval.getLandmarkProximitySearchResults();

            Iterator<CarmenFeature> retLanIterator = retrievedLandmarks.iterator();

            if(retrievedLandmarks.size() > 0)
            {
                landmarkGet.clear();
                while(retLanIterator.hasNext())
                {
                    CarmenFeatureHelper retriever = new CarmenFeatureHelper(retLanIterator.next());

                    double lat = retriever.getLandmarkLatitude();
                    double lon = retriever.getLandmarkLongitude();
                    String name = retriever.getLandmarkName();
                    String placename = retriever.getLandmarkName();
                    String wikidata = retriever.getLandmarkWikiData();
                    Date lan_date = new Date();

                    boolean test_elev = retriever.checkElevationExists();
                    double elev_result;

                    if (test_elev)
                        elev_result = retriever.getLandmarkElevation();
                    else {
                        elev_result = mSensorData.getCurrentLocation().getAltitude();       // else return current altitude/elevation
                    }

                    landmarkGet.add(new LocalLandmark(placename, Double.toString(lat), Double.toString(lon), (float)elev_result, wikidata, lan_date));
                }
            }
            else
                throw new NullPointerException("Landmark search test failed."); // temporary so the UI seems to be a bit more fluid. Otherwise it will display data but not have any landmarks.
        }
        catch (SecurityException | NullPointerException e)
        {}
        finish();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        //Unregister listeners
        mSensorData.unregisterOrientationSensors();
        mSensorData.unregisterLocationSensor();

        Intent result = new Intent(this, LandmarkHistory.class);
        result.putParcelableArrayListExtra("sending_history", landmarkGet);
        startActivity(result);
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {

            }
            else
            {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
        else
        {
            // Permission has already been granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
