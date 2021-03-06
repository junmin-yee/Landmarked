package landmarked.landmarked.DataManipulation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

public class SensorData {

    private static final long LOCATION_REFRESH_TIME_IN_MS = 1000;
    private static final float LOCATION_REFRESH_DISTANCE = 0;
    private static final int DEGREES_IN_CIRCLE = 360;
    private static final String TAG = "SensorData";

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
        mLocationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);

        //Find sensors
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void registerOrientationSensors() {
        //Register sensors
        mSensorManager.registerListener(mSensorEventListener, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorEventListener, magnetometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterOrientationSensors() {
        //Unregister sensors
        mSensorManager.unregisterListener(mSensorEventListener, accelerometer);
        mSensorManager.unregisterListener(mSensorEventListener, magnetometer);
    }

    public void registerLocationSensor() {
        try {
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_REFRESH_TIME_IN_MS, LOCATION_REFRESH_DISTANCE,
                    mLocationListener);
        }
        catch (SecurityException e) {
            Log.e(TAG, "Exception caught in registerLocationSensor: ", e);
        }
    }

    public void unregisterLocationSensor() {
        //Stop location call back, DRAINS BATTERY if line below is commented
        mLocationManager.removeUpdates(mLocationListener);
    }

    public float[] getCurrentOrientation(){
        //Get sensor data from phone
        return mOrientation;
    }

    public float getDirectionInDegrees()
    {
        return ((float)Math.toDegrees(mOrientation[0]) + DEGREES_IN_CIRCLE) % DEGREES_IN_CIRCLE;
    }

    public float getAzimuth()
    {
        return mOrientation[0];
    }

    public float getPitch()
    {
        return mOrientation[1];
    }

    public float getRoll()
    {
        return mOrientation[2];
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
                //float remap[] = new float[9];
                boolean passed = SensorManager.getRotationMatrix(R, null, mGravity, mGeomagnetic);
                if (passed) {
                    //Calculate orientation
                    //SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Z, remap);
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
