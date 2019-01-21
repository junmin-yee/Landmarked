package landmarked.landmarked;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class SensorData {

    public static final long LOCATION_REFRESH_TIME_IN_MS = 1000;
    public static final float LOCATION_REFRESH_DISTANCE = 0;

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float mOrientation[] = new float[3];
    private LocationManager mLocationManager;
    private Location mLocation;
    Context mContext;

    public SensorData(){

    }

    public SensorData(Context context){
        this.mContext = context;
        //Set up manager services
        mLocationManager = (LocationManager)mContext.getSystemService(mContext.LOCATION_SERVICE);
        mSensorManager = (SensorManager)mContext.getSystemService(mContext.SENSOR_SERVICE);

        //Find sensors
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //Register sensors
        mSensorManager.registerListener(mSensorEventListener, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorEventListener, magnetometer,
                SensorManager.SENSOR_DELAY_NORMAL);

        try {
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_REFRESH_TIME_IN_MS, LOCATION_REFRESH_DISTANCE,
                    mLocationListener);
        }
        catch (SecurityException e) {
            throw e;
        }
    }

    public float[] getCurrentOrientation(){
        //Get sensor data from phone
        return mOrientation;
    }

    public Location getCurrentLocation() {
        //Get location data from phone
        return mLocation;
    }

    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
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
                //Funky math stuff
                float R[] = new float[9];
                float I[] = new float[9];
                boolean passed = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (passed) {
                    //Calculate orientation
                    SensorManager.getOrientation(R, mOrientation);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;

            //Stop location call back, DRAINS BATTERY if line below is commented
            //mLocationManager.removeUpdates(this);
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
}
