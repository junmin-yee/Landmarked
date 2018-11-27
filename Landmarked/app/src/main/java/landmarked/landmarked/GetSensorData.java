package landmarked.landmarked;

import android.Manifest;
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

public class GetSensorData extends AppCompatActivity implements SensorEventListener {

    public static final String ACTIVITY_MESSAGE = "Sending to Map";
    public static final long LOCATION_REFRESH_TIME_IN_MS = 1000;
    public static final float LOCATION_REFRESH_DISTANCE = 0;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    private LocationManager mLocationManager;
    Location currlocation;
    public TextView directionTV;
    public TextView locationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_sensor_data);

        directionTV = findViewById(R.id.current_direction_text);
        locationTV = findViewById(R.id.current_location_text);

        //Get sensor data from phone
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
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

        //Get location data from phone
        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        try {
<<<<<<< HEAD
            currlocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (currlocation != null) {
                locationTV.setText("Latitude: " + currlocation.getLatitude() + "\nLongitude: " +
                        currlocation.getLongitude() + "\nElevation: " + currlocation.getAltitude());
            }
=======
>>>>>>> parent of 80810b7... Merge branch 'master' of https://github.com/Redgreed4/Landmarked
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_REFRESH_TIME_IN_MS, LOCATION_REFRESH_DISTANCE, mLocationListener);
        }
        catch (SecurityException e) {
            locationTV.setText("Location not found");
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

    protected void OnResume() {
        super.onResume();
    }

    protected void OnPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    float[] mGravity;
    float[] mGeomagnetic;
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            mGeomagnetic = event.values;
        }
        if (mGravity != null && mGeomagnetic != null){
            float R[] = new float[9];
            float I[] = new float[9];
            boolean passed = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (passed) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                directionTV.setText("Azimuth (z): " + orientation[0] + "\nPitch (x): " +
                    orientation[1] + "\nRoll (y): " + orientation[2]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currlocation = location;
            locationTV.setText("Latitude: " + currlocation.getLatitude() + "\nLongitude: " +
                    currlocation.getLongitude() + "\nElevation: " + currlocation.getAltitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    public void showMap(View v)
    {
        Intent i = new Intent(this, DisplayMap.class);

        double[] arr = new double[2];

        arr[0] = currlocation.getLatitude();
        arr[1] = currlocation.getLongitude();

        i.putExtra(ACTIVITY_MESSAGE, arr);

        startActivity(i);
    }
}
