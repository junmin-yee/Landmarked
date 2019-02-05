package landmarked.landmarked;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LandmarkedMain extends AppCompatActivity {

    public static final String ACTIVITY_MESSAGE = "Sending to Map";
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public TextView directionTV;
    public TextView locationTV;
    public Location currLocation;
    public float[] currOrientation = new float[3];
    public SensorData mSensorData;
    AppDatabase db;

    public GoogleAuthentication mAuth;
    //This is where it starts, when the app launches, this is called
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //calling a STATIC METHOD on the DB containing class.
        //This method will use a singleton pattern to either return the already existing instance
        //or create a new one. We pass in this as a context.
        db.getM_DB_instance(this);

        Intent ii = new Intent(this, GoogleAuthentication.class);
        startActivity(ii);

        setContentView(R.layout.activity_get_sensor_data);

        //Instantiate with this context
        mSensorData = new SensorData(this);

        directionTV = findViewById(R.id.current_direction_text);
        locationTV = findViewById(R.id.current_location_text);
    }

    @Override
    protected void onStart() {
        super.onStart();


        checkLocationPermission();

        //Register listeners
        mSensorData.registerOrientationSensors();
        mSensorData.registerLocationSensor();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Unregister listeners
        mSensorData.unregisterOrientationSensors();
        mSensorData.unregisterLocationSensor();
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            }
        } else {
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

    public void testSensors(View v)
    {
        currOrientation = mSensorData.getCurrentOrientation();
        directionTV.setText("Azimuth (z): " + currOrientation[0] + "\nPitch (x): " +
                currOrientation[1] + "\nRoll (y): " + currOrientation[2]);

        try{
            currLocation = mSensorData.getCurrentLocation();//gets called in showMap as well, consider changing?
            locationTV.setText("Latitude: " + currLocation.getLatitude() + "\nLongitude: " +
                    currLocation.getLongitude() + "\nElevation: " + currLocation.getAltitude());
        }
        catch (SecurityException | NullPointerException e){
            locationTV.setText("Location not found");
        }
    }

    public void showMap(View v)
    {
        currLocation = mSensorData.getCurrentLocation();//gets called in testSensors as well, consider changing?
        Intent i = new Intent(this, DisplayMap.class);

        double[] arr = new double[2];

        arr[0] = currLocation.getLatitude();
        arr[1] = currLocation.getLongitude();

        i.putExtra(ACTIVITY_MESSAGE, arr);

        startActivity(i);
        finish();
    }

    public void seeCustomLandmarks(View v)
    {
        /*Intent customLand = new Intent(this, CustomLandmark.class);

        startActivity(customLand);*/
    }
}
