package landmarked.landmarked;

import android.Manifest;
<<<<<<< HEAD
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Room;
import android.content.Context;
=======
>>>>>>> aabc38a5784a349988424869d1c84781b79e1369
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import landmarked.landmarked.DataManipulation.CarmenFeatureHelper;
import landmarked.landmarked.DataManipulation.LandmarkRetrieval;
import landmarked.landmarked.DataManipulation.SensorData;
import landmarked.landmarked.Database.AppDatabase;
import landmarked.landmarked.Database.AzureConnectionClass;
import landmarked.landmarked.Database.CustomLandmark;
import landmarked.landmarked.Database.CustomLocalLandmark;
import landmarked.landmarked.Database.LocalLandmark;

public class LandmarkedMain extends AppCompatActivity {
    private static ReentrantLock m_thread_lock;
    public static final String ACTIVITY_MESSAGE = "Sending to Map";
    private static LandmarkedMain m_instance;//instance of the main activity that can be accessed through getter getInstance(), handy for things such as closing the app from another instance

    public TextView directionTV;
    public TextView locationTV;

    //DB instance
    AppDatabase db;
    public static LandmarkedMain main_instance;
    //Thread pool instance
    public static ExecutorService m_thread;
    GoogleAuthentication m_user;
    public static String m_username;
    public String m_conn_msg;
    public static AzureConnectionClass m_conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        m_user = new GoogleAuthentication();
        m_thread_lock = new ReentrantLock();
        ConnectivityManager conn = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conn.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected)
        {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                m_conn_msg = "connected to WIFI";
            }
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                m_conn_msg = "connected to mobile data";
            }
        }
        else
        {
            m_conn_msg = "Not connected to either mobile or WIFI";
        }

        m_thread = Executors.newSingleThreadExecutor();
        m_conn = new AzureConnectionClass();
        if(isConnected)
        {
            Intent ii = new Intent(this, GoogleAuthentication.class);
            startActivity(ii);
        }
        m_instance = this;
        //This method will use a singleton pattern to either return the already existing instance
        db = AppDatabase.getM_DB_instance(getApplicationContext());
        main_instance = this;
       // String acct_name = m_user.getUserEmailName();

        m_conn.Connect();
        //InsertAzure("sometest", "someTest", "SomeTest", 0.1F, "Sometest"); //INSERTAZURE IS A FUNCTION WITHIN LANDMARKEDMAIN

        //call this function to select from azure cloud landmark table

       // ArrayList<LocalLandmark> LocalLMS = getLandmarksAzure();

        //call this methods to get landmarks by user email
        //arg function returns string from google auth containing user email
        //ArrayList<LocalLandmark> user_specific_lands = getUserLandmarksFromAzure(m_user.getUserEmailName());

        //Calling method with dummy data:
        ArrayList<LocalLandmark> user_specific_lands = getUserLandmarksFromAzure("someemail@gmail.com");


        setContentView(R.layout.activity_get_sensor_data);
        String str = m_username;
       // text.setText("test");

        directionTV = findViewById(R.id.current_direction_text);
        locationTV = findViewById(R.id.current_location_text);
    }
    public void setUserName(String name)
    {
        m_username = name;
    }
    public static ReentrantLock get_thread_lock() {
        return m_thread_lock;
    }

    @Override
    protected void onStart() {
        super.onStart();
<<<<<<< HEAD

=======
>>>>>>> aabc38a5784a349988424869d1c84781b79e1369
        TextView text = findViewById(R.id.WelcomeText);
        if(GoogleAuthentication.getUserEmailName() == null)
        {
            text.setText("Not connected: sign out button -> close and restart app to sign in");
        }
        else
        {
            text.setText("Welcome back " + GoogleAuthentication.getUserEmailName());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

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
    private synchronized ArrayList<LocalLandmark> getUserLandmarksFromAzure(String email)
    {
       ArrayList<LocalLandmark> lst = new ArrayList<LocalLandmark>();

        Runnable runCommand = new Runnable() {

            @Override
            public void run() {
                ArrayList<LocalLandmark> temp = m_conn.getLandmarksByEmail(email);
                for(int x = 0; x < temp.size(); x++)
                {
                    lst.add(temp.get(x));
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
        m_thread.execute(insertStructure);

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


    /*public void seeCustomLandmarks(View v)
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
    }*/

    public static LandmarkedMain getInstance()//return instance of main activity
    {
        return m_instance;
    }

    public void GoogleSignOut(View v)
    {
        GoogleAuthentication m_auth = new GoogleAuthentication();
        m_auth.signOut();

    }

    public void loadingScreen(View v)
    {
        Intent load = new Intent(this, LoadingPage.class);
        startActivity(load);
    }
}
