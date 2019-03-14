package landmarked.landmarked;

import android.Manifest;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import landmarked.landmarked.DataManipulation.CarmenFeatureHelper;
import landmarked.landmarked.DataManipulation.LandmarkRetrieval;
import landmarked.landmarked.DataManipulation.SensorData;
import landmarked.landmarked.Database.AppDatabase;
import landmarked.landmarked.Database.AzureConnectionClass;
import landmarked.landmarked.Database.CustomLandmark;
import landmarked.landmarked.Database.CustomLocalLandmark;
import landmarked.landmarked.Database.LocalLandmark;
import landmarked.landmarked.Database.LocalLandmarkPass;

public class LandmarkedMain extends AppCompatActivity {

    public static final String ACTIVITY_MESSAGE = "Sending to Map";
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static LandmarkedMain m_instance;//instance of the main activity that can be accessed through getter getInstance(), handy for things such as closing the app from another instance

    public TextView directionTV;
    public TextView locationTV;
    public Location currLocation;
    public float[] currOrientation = new float[3];
    public SensorData mSensorData;
    public LandmarkRetrieval mLandmarkRetrieval;
    //DB instance
    AppDatabase db;
    //Thread pool instance
    private ExecutorService m_thread;
    public ArrayList<LocalLandmark> landmarkGet = new ArrayList<>();
    GoogleSignInAccount m_user;
    public static AzureConnectionClass m_conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_thread = Executors.newSingleThreadExecutor();
        m_conn = new AzureConnectionClass();
        Intent ii = new Intent(this, GoogleAuthentication.class);
        startActivity(ii);
        m_instance = this;
        //This method will use a singleton pattern to either return the already existing instance
        db = db.getM_DB_instance(getApplicationContext());


        m_conn.Connect();
        //InsertAzure("sometest", "someTest", "SomeTest", 0.1F, "Sometest"); //INSERTAZURE IS A FUNCTION WITHIN LANDMARKEDMAIN

    //call this function to select from azure cloud landmark table
       // ArrayList<LocalLandmark> LocalLMS = getLandmarksAzure();
        setContentView(R.layout.activity_get_sensor_data);

        //Instantiate with this context
        mSensorData = new SensorData(this);

        //Instantiate with existing SensorData object
        mLandmarkRetrieval = new LandmarkRetrieval(mSensorData);

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


    public ArrayList<LocalLandmark> getLandmarksAzure()
    {
        ArrayList<LocalLandmark>lst = new ArrayList<LocalLandmark>();
        Runnable runCommand = new Runnable() {
            @Override
            public void run() {
                ArrayList<LocalLandmark> temp = m_conn.getLandmarks();
                for(int x = 0; x < temp.size(); x++)
                {
                    lst.add(x, temp.get(x));
                }
            }
        };
        m_thread.execute(runCommand);
        return lst;

    }

    public void InsertAzure(String name, String latitude, String longitude, float elevation, String wiki)
    {
        Runnable runCommand = new Runnable() {
            @Override
            public void run() {
               m_conn.Insert(name, latitude, longitude, elevation, wiki);
            }
        };
        m_thread.execute(runCommand);
    }

    //Insert local data by primitive type
    public void insertLocalLandmarkPrimitive(String name, String latitude, String longitude, float elevation, String wiki, Date date) {


        //no error checking, at this point it's assumed that the primitive data is correct
        //It's also assumed that an instance of the DB has been initialized
        //insert is called through an instance of the interface LocalLandmarkAccessorMethods

        //Make a LocalLandmark from passed in data
        LocalLandmark land = new LocalLandmark(name, latitude, longitude, elevation, wiki,date);

        //SQL operations are required to be on their own thread, if they aren't on their own thread they will crash the app for trying to run on the main thread.


        //Creating a Runnable action that will run when our thread calls execute on it.
        Runnable runCommand = new Runnable() {
            @Override
            public void run() {
                db.LocalLandmarkMethodsVar().insertLocalLandmarkStructure(land);
            }
        };
        //m thread is our single thread pool, we've built a Runnable, and now we call execute to run it on the thread pool. The way this is supposed to work is that
        //simultaneous sql operations will be queued and eventually all run on the same thread.
        //What we're trying to avoid is incomplete data result if , for example, an insert and a select * are done on different threads? who knows. This way, they execute one at a time,
        //FIFO, and we can guarantee asyncrhnous behavior AKA SQL calls will be performed in the order they are called.
        m_thread.execute(runCommand);

    }


    //This function will turn raw data into a CustomLocalLandmark and insert it
    public void insertCustomLandmarkPrimitive(String name, String latitude, String longitude, float elevation, String wiki, Date date )
    {
        CustomLocalLandmark land = new CustomLocalLandmark(name, latitude, longitude, elevation, wiki, date);
        Runnable insertStructure = new Runnable()
        {
            @Override
            //overriding required method Run()
            public void run()
            {
                db.CustomMethodsVar().insertCustomLandmarkStructure(land);
                //db instance call it's member variable for custom landmarks that's able to call insertCustom..... that's declared in interface CustomLocalLandmarkAccessorMethods
            }

        };

    }
    public List<CustomLocalLandmark> getAllCustomLocals()
    {
        List lst = new ArrayList<CustomLocalLandmark>();
        //creating an action to execute on our sql thread
        Runnable getData = new Runnable() {
            @Override
            //this function must be overridden each time a new thread is called
            public void run() {
                //this array will hold the contents resulting form the query select * from CustomLocalLandmark
                CustomLocalLandmark[] ray = db.CustomMethodsVar().getAll();
                //All results are now in ray, but they need to be in a container that i can return. So, i'll iterate the array and add them to the list i initialized at top of func
                for (int x = 0; x < ray.length; x++) {
                    lst.add(ray[x]);
                }
            }
        };
        m_thread.execute(getData);
            //will contain contents of getAll()
        return lst;

    }

    //We need to do some conversion to a customLocalLandmark arg
    public void insertCustomLandmarkStructure(CustomLocalLandmark LandmarkArg)
    {
        Runnable insertStructure = new Runnable()
        {
            @Override
            //overriding required method Run()
            public void run()
            {
                db.CustomMethodsVar().insertCustomLandmarkStructure(LandmarkArg);
                //db instance call it's member variable for custom landmarks that's able to call insertCustom..... that's declared in interface CustomLocalLandmarkAccessorMethods
            }

        };

    }


    public void insertLocalLandmarkStructureArg(LocalLandmark landmarkArg)
    {
        //all sql ops must be done on thread other than main thread
       Runnable insertStructure = new Runnable()
       {
           @Override
           //overriding required method Run()
           public void run()
           {
               db.LocalLandmarkMethodsVar().insertLocalLandmarkStructure(landmarkArg);
           }

       };
       //Execute our new Runnable with our thread pool
       m_thread.execute(insertStructure);
    }


    //This function will return all rows from local DB and return them in the form of a list
    public List<LocalLandmark> getLocalLandmarkData()
    {
        List lst = new ArrayList<LocalLandmark>();
        //creating an action to execute on our sql thread
        Runnable getData = new Runnable(){
            @Override
            //this function must be overridden each time a new thread is called
            public void run()            {
                //this array will hold the contents resulting form the query select * from LocalLandmark. it's only here to prove that data is being retrieved from the db
                LocalLandmark[] ray = db.LocalLandmarkMethodsVar().getAll();
                //All results are now in ray, but they need to be in a container that i can return. So, i'll iterate the array and add them to the list i initialized at top of func
                for(int x = 0; x < ray.length; x++)
                {
                    lst.add(ray[x]);
                }
            }
        };
        //Now that we've built a runnable operation, call execute on it from our sql thread.
        m_thread.execute(getData);
        //will contain contents of getAll()
        return lst;

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
    public void onDestroy()
    {
        super.onDestroy();

    }
    @Override
    public void onResume()
    {
        super.onResume();
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

            // Test Geocode Searching
            mLandmarkRetrieval.LandmarkProximitySearch(currLocation);
            //mLandmarkRetrieval.LandmarkBoundaryBoxSearch(currLocation);

            List<CarmenFeature> test1 = mLandmarkRetrieval.getLandmarkProximitySearchResults();
            //List<CarmenFeature> test1 = mLandmarkRetrieval.getLandmarkBoundaryBoxSearchResults();
            CarmenFeatureHelper test2;
            if (test1 != null && test1.size() > 0) {
                test2 = new CarmenFeatureHelper(test1.get(0));

                boolean test_elev = test2.checkElevationExists();
                double elev_result;
                if (test_elev)
                    elev_result = test2.getLandmarkElevation();
                else {
                    elev_result = mSensorData.getCurrentLocation().getAltitude();       // else return current altitude/elevation
                }
                double lat_result = test2.getLandmarkLatitude();
                double lon_result = test2.getLandmarkLongitude();
                String lan_name = test2.getLandmarkName();
                String lan_placename = test2.getLandmarkPlaceName();
                String lan_wikidata = test2.getLandmarkWikiData();

                //elev_result = 0; // throwaway

                landmarkGet.clear();
                for(int i = 0; i < test1.size(); i++)
                {
                    CarmenFeatureHelper retriever = new CarmenFeatureHelper(test1.get(i));

                    double lat = retriever.getLandmarkLatitude();
                    double lon = retriever.getLandmarkLongitude();
                    String name = retriever.getLandmarkName();
                    String placename = retriever.getLandmarkName();
                    String wikidata = retriever.getLandmarkWikiData();
                    Date lan_date = new Date();

                    landmarkGet.add(new LocalLandmark(placename, Double.toString(lat), Double.toString(lon), (float)elev_result, wikidata, lan_date));
                }

                // Example Usage of getCarmenFeatureFwdResults:
                //List<CarmenFeature> test1;
                //test1 =mLandmarkRetrieval.getCarmenFeatureFwdResults(); // returns a List<CarmenFeature> of variable size.
                //CarmenFeatureHelper test2;
                //test2 = new CarmenFeatureHelper(mFwdResults.get(0)); // gets only the first feature in the List object. Must iterate through for every CarmenFeature returned by getCarmenFeatureFwdResults.
            }
            else
                throw new NullPointerException("Landmark search test failed."); // temporary so the UI seems to be a bit more fluid. Otherwise it will display data but not have any landmarks.

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
        //finish();
    }


    public void seeCustomLandmarks(View v)
    {
        Intent customLand = new Intent(this, CustomLandmark.class);

        customLand.putParcelableArrayListExtra("sending_landmark", landmarkGet);

        startActivity(customLand);
    }

    public void seeHistoryPage(View v)
    {
        Intent hist = new Intent(this, LandmarkHistory.class);
        hist.putParcelableArrayListExtra("sending_history", landmarkGet);
        startActivity(hist);
    }

    public static LandmarkedMain getInstance()//return instance of main activity
    {
        return m_instance;
    }

    public void GoogleSignOut(View v)
    {
        GoogleAuthentication m_auth = new GoogleAuthentication();
        m_auth.signOut();

    }

}
