package landmarked.landmarked;

import android.Manifest;

import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import landmarked.landmarked.DataManipulation.CarmenFeatureHelper;
import landmarked.landmarked.DataManipulation.LandmarkRetrieval;
import landmarked.landmarked.DataManipulation.SensorData;
import landmarked.landmarked.Database.AppDatabase;
import landmarked.landmarked.Database.AzureConnectionClass;
import landmarked.landmarked.Database.CustomLandmark;
import landmarked.landmarked.Database.CustomLocalLandmark;
import landmarked.landmarked.Database.LocalLandmark;
import landmarked.landmarked.GUI.GoogleAuthentication;
import landmarked.landmarked.GUI.LandmarkHistory;
import landmarked.landmarked.GUI.LoadingPage;
import landmarked.landmarked.GUI.RetrievedLandmarks;

public class LandmarkedMain extends AppCompatActivity {
    private static ReentrantLock m_thread_lock;
    public static final String ACTIVITY_MESSAGE = "Sending to Map";
    private static LandmarkedMain m_instance;//instance of the main activity that can be accessed through getter getInstance(), handy for things such as closing the app from another instance
    private static GoogleSignInAccount m_acct;
    public TextView directionTV;
    public TextView locationTV;
    CountDownLatch locationPermissionLock = new CountDownLatch(1);

    //DB instance
    AppDatabase db;

    //Thread pool instance
    public static ExecutorService m_thread;
    GoogleAuthentication m_user;
    public static  String m_username;
    public String m_conn_msg;
    public static AzureConnectionClass m_conn;

    // getLandmarkData stuff. Needed for searching landmarks and sensor stuff.
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    // Sensor data variables
    public SensorData mSensorData;
    public LandmarkRetrieval mLandmarkRetrieval;
    public Location currLocation;
    public float[] currOrientation = new float[3];
    public ArrayList<LocalLandmark> landmarkGet = new ArrayList<>();

    public ProgressDialog dialog;

    private static final String TAG = "LandmarkedMain";
    //public Vector<LocalLandmark> m_list = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        m_user = new GoogleAuthentication();
        ConnectivityManager conn = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conn.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected)
        {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                m_conn_msg = "connected to WIFI";
                Log.i(TAG, m_conn_msg);
            }
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                m_conn_msg = "connected to mobile data";
                Log.i(TAG, m_conn_msg);
            }
        }
        else
        {
            m_conn_msg = "Not connected to either mobile or WIFI";
            Log.i(TAG, m_conn_msg);
        }

        m_thread = Executors.newSingleThreadExecutor();
        m_conn = new AzureConnectionClass();

        if(isConnected)
        {
            Intent ii = new Intent(this, GoogleAuthentication.class);
            startActivity(ii);
        }

        //This method will use a singleton pattern to either return the already existing instance
        db = AppDatabase.getM_DB_instance(getApplicationContext());
        m_instance = this;

        m_conn.Connect();
        //InsertAzure("sometest", "someTest", "SomeTest", 0.1F, "Sometest"); //INSERTAZURE IS A FUNCTION WITHIN LANDMARKEDMAIN

        //call this function to select from azure cloud landmark table

       // ArrayList<LocalLandmark> LocalLMS = getLandmarksAzure();

        //call this methods to get landmarks by user email
        //arg function returns string from google auth containing user email
        //ArrayList<LocalLandmark> user_specific_lands = getUserLandmarksFromAzure(m_user.getUserEmailName());

        //Calling method with dummy data:
//        ArrayList<LocalLandmark> user_specific_lands = getUserLandmarksFromAzure();

        //InsertUserToAzure();
        setContentView(R.layout.activity_get_sensor_data);
        
       // text.setText("test");

        //directionTV = findViewById(R.id.current_direction_text);
        //locationTV = findViewById(R.id.current_location_text);

        AnimationDrawable anim;

        ImageView animView = (ImageView) findViewById(R.id.imageView);
        animView.setBackgroundResource(R.drawable.animation);
        anim = (AnimationDrawable) animView.getBackground();
        anim.start();
    }

    // Setter/Getter for Google Authentication Username
    public static AzureConnectionClass get_azure_instance(){ return m_conn; }

    public void setUserName(String name)
    {
        m_username = name;
    }
    public static String get_m_username()
    {
        return m_username;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Checks if app has permission to use location.
        checkLocationPermission();

        /*try {
            locationPermissionLock.await();
        }
        catch (InterruptedException e)
        {

        }*/

        //Instantiate with this context
        mSensorData = new SensorData(this);

        //Instantiate with existing SensorData object
        mLandmarkRetrieval = new LandmarkRetrieval();

        //Register listeners
        mSensorData.registerOrientationSensors();
        mSensorData.registerLocationSensor();

        currOrientation = mSensorData.getCurrentOrientation();

        // Create ProgressDialog for landmark searching
        dialog = new ProgressDialog(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Unregister listeners
        mSensorData.unregisterOrientationSensors();
        mSensorData.unregisterLocationSensor();
    }

    // Creates a threadpool if one has not yet been created
    public static ExecutorService getThreadPoolInstance()
    {
        if(m_thread == null)
        {
            m_thread = Executors.newSingleThreadExecutor();
        }
        return m_thread;
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
    public void InsertUserToAzure()
    {
        Runnable runCommand = new Runnable() {

            @Override
            public void run()
            {
                m_conn.InsertUsername(m_username, m_acct.getGivenName(), m_acct.getFamilyName());
            }

        };
        m_thread.execute(runCommand);
    }

    // Gets all landmarks from Azure given specific username

    public List<LocalLandmark> getUserLandmarksFromAzure()throws InterruptedException
    {
        CountDownLatch threadLatch = new CountDownLatch(1);
       ArrayList<LocalLandmark> lst = new ArrayList<LocalLandmark>();
       ArrayList<String> names = new ArrayList<String>();


        Runnable runCommand = new Runnable() {
            @Override
            public void run() {
                   ArrayList<LocalLandmark> temp = m_conn.getLandmarksByEmail(m_username);
                   for(int x = 0; x < temp.size(); x++)
                   {

                       if(!names.contains(temp.get(x).getName()))
                       {
                           lst.add(temp.get(x));
                           names.add(temp.get(x).getName());
                       }
                   }
                threadLatch.countDown();
            }
        };

        m_thread.execute(runCommand);
        try {
            threadLatch.await();
        }
        catch(Exception e)
        {
            Log.w(TAG, "getUserLandmarksFromAzure CountDownLatch await failed", e);
        }
        return lst;
    }

    public Vector<CustomLocalLandmark> getCustomLandmarksFromAzure() throws InterruptedException
    {
        CountDownLatch threadLatch = new CountDownLatch(1);
        Vector<CustomLocalLandmark> lst = new Vector<CustomLocalLandmark>();

        Runnable runCommand = new Runnable() {
            @Override
            public void run() {
                ArrayList<CustomLocalLandmark> temp = m_conn.getCustomLandmarksByUser();
                for(int i = 0; i < temp.size(); i++)
                {
                    lst.add(temp.get(i));
                }
                threadLatch.countDown();
            }
        };
        m_thread.execute(runCommand);
        try
        {
            threadLatch.await();
        }
        catch (Exception e)
        {
            Log.w(TAG, "getCustomLandmarksFromAzure CountDownLatch await failed", e);
        }
        return lst;
    }

    // Inserts a landmark into Azure.
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

    // Gets all custom landmarks from the local SQLite database
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
        m_thread.execute(insertStructure);
    }

    // Inserts a landmark into the local SQLite database
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

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    /*public void showMap(View v)
    {
        currLocation = mSensorData.getCurrentLocation();//gets called in testSensors as well, consider changing?
        Intent i = new Intent(this, DisplayMap.class);

        double[] arr = new double[2];

        arr[0] = currLocation.getLatitude();
        arr[1] = currLocation.getLongitude();

        i.putExtra(ACTIVITY_MESSAGE, arr);

        startActivity(i);
        //finish();
    }*/

    // Show list of custom landmarks
    public void seeCustomLandmarks(View v)
    {
        Intent customLand = new Intent(this, CustomLandmark.class);

        //customLand.putParcelableArrayListExtra("sending_landmark", landmarkGet);
        startActivity(customLand);
    }

    // Show list of landmarks in history
    public void seeHistoryPage(View v)
    {
        Intent hist = new Intent(this, RetrievedLandmarks.class);
        //hist.putParcelableArrayListExtra("sending_history", landmarkGet);
        startActivity(hist);
    }

    // Gets current instance of the main activity
    public static LandmarkedMain getInstance()//return instance of main activity
    {
        return m_instance;
    }

    // Signs a user out on click
    public void GoogleSignOut(View v)
    {
        GoogleAuthentication m_auth = new GoogleAuthentication();
        m_auth.signOut();
    }

    // Gets landmark data from mapbox given sensor data
    public void getLandmarkData(View v){

        showProgressDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try
                {
                    currLocation = mSensorData.getCurrentLocation();
                    Log.d(TAG, "Current latitude: " + currLocation.getLatitude() + ", " +
                            "Current longitude: " + currLocation.getLongitude());

                    // Set sensor information as current
                    mLandmarkRetrieval.SetSensorInformation(mSensorData);

                    // Search for landmarks
                    //mLandmarkRetrieval.LandmarkProximitySearch();
                    mLandmarkRetrieval.LandmarkBoundaryBoxSearch();

                    // Get the search results
                    //Set<CarmenFeature> retrievedLandmarks = mLandmarkRetrieval.getLandmarkProximitySearchResults();
                    Set<CarmenFeature> retrievedLandmarks = mLandmarkRetrieval.getLandmarkBoundaryBoxSearchResults();

                    if(retrievedLandmarks.size() > 0)
                    {
                        // Clear landmark results already within GUI.
                        landmarkGet.clear();

                        // Set iterator for list of landmarks.
                        Iterator<CarmenFeature> retLanIterator = retrievedLandmarks.iterator();

                        // Iterate through list of landmarks and add them to GUI.
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
                                // Sets to current elevation converted to feet from meters.
                                elev_result = mSensorData.getCurrentLocation().getAltitude() * 3.28084;       // else return current altitude/elevation
                            }

                            // Add landmarks to GUI
                            landmarkGet.add(new LocalLandmark(placename, Double.toString(lat), Double.toString(lon), (float)elev_result, wikidata, lan_date));
                            //m_conn.Insert(placename, Double.toString(lat), Double.toString(lon), (float)elev_result, wikidata);
                        }

                        // Finish loading page activity
                        //finish(); // We don't want this in our main activity!!!
                    }
                    else {
                        Log.e(TAG, "Landmark search test failed");
                        throw new NullPointerException("Landmark search test failed."); // temporary so the UI seems to be a bit more fluid. Otherwise it will display data but not have any landmarks.
                    }
                }
                catch (SecurityException | NullPointerException e)
                {
                    Log.e(TAG, "Exception caught in getLandmarkData: ", e);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        // Dismiss the... dialog. Refers to the progress dialog
                        dialog.dismiss();

                        if (landmarkGet.size() > 0) {

                            // Show landmark history page (Shows the results returned from landmark search)
                            Intent result = new Intent(m_instance, RetrievedLandmarks.class);
                            result.putParcelableArrayListExtra("sending_history", landmarkGet);
                            startActivity(result);
                        }
                        else
                        {
                            Context c = getApplicationContext();
                            Toast.makeText(c, "No landmarks were found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    // Shows a progress dialog as a wait
    public void showProgressDialog() {

        // Create progress dialog box while app is searching for landmarks
        dialog.setMessage("Searching for Landmarks...");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
    }

    // Checks whether the location permission is given by user
    public void checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {
                Log.d(TAG, "Showing request permission rationale");
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
            locationPermissionLock.countDown();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG, "Location permission granted");
                }
                else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.w(TAG, "Location permission denied");
                }
            }
        }
        locationPermissionLock.countDown();
    }

    @Override protected void onPause()
    {
        super.onPause();
    }

    @Override protected void onResume()
    {
        super.onResume();
        TextView text = findViewById(R.id.WelcomeText);
        m_acct = GoogleAuthentication.getUser();

        if(m_acct != null)
            m_username = m_acct.getEmail();

        if(m_username == null)
        {
            text.setText("Not connected: sign in failed");
            Log.i(TAG, "Notc connected: sign in failed");
        }
        else
        {
            text.setText("Welcome back " + m_acct.getEmail());
            //insertUserToAzure will make a query that attempts to insert the username. However, if the user is already in the DB, they will not be added again.
            InsertUserToAzure();
          //  Intent i = new Intent(this, LandmarkHistory.class);
            //startActivity(i);
        }
    }
    public void LandmarkHist(View v) {
        Intent i = new Intent(this, LandmarkHistory.class);

        startActivity(i);
    }
}
