package landmarked.landmarked.Database;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Vector;

import landmarked.landmarked.DataManipulation.addCustomLandmark;
import landmarked.landmarked.GUI.CustomSelected;
import landmarked.landmarked.LandmarkedMain;
import landmarked.landmarked.R;

public class CustomLandmark extends AppCompatActivity {

    private static final String TAG = "CustomLandmark";
    public TextView landmarkInfo;
    public LinearLayout LandmarkList;
    public TextView tempView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*SharedPreferences themePref = this.getSharedPreferences("LandmarkedPreferences", MODE_PRIVATE);
        int myTheme = themePref.getInt("themePreferences", R.style.AppTheme);
        setTheme(myTheme);*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_landmark);

        //get user ID that correlates to the database
        landmarkInfo = findViewById(R.id.LandmarkAttributes);

        landmarkInfo.setText("Custom Landmarks");

        LandmarkList = findViewById(R.id.landmarkList);

        try {
            Vector<CustomLocalLandmark> lst = LandmarkedMain.getInstance().getCustomLandmarksFromAzure();
            for(int i = 0; i < lst.size(); i++)
            {
                elementWrapper(lst.get(i));
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception caught in onCreate: ", e);
        }

        /*ArrayList<LocalLandmark> recievedLandmark = i.getParcelableArrayListExtra("sending_landmark");

        for(int v = 0; v < recievedLandmark.size(); v++)
        {
            AddElement(recievedLandmark.get(v).getName(), recievedLandmark.get(v).getLatitude(), recievedLandmark.get(v).getLongitude(), recievedLandmark.get(v).getElevation());
        }*/

            /*for (CarmenFeature feat : landmarkList) {
                CarmenFeatureHelper landmarkHelper = new CarmenFeatureHelper(feat);
                String name = landmarkHelper.getmLandmarkName();
                String latitude = Double.toString(landmarkHelper.getmLandmarkLatitude());
                String longitude = Double.toString(landmarkHelper.getmLandmarkLongitude());
                float elevation = (float) landmarkHelper.getmLandmarkElevation();

                AddElement(name, latitude, longitude, elevation);
            }*/

        //AddElement("temp", "34.5634", "-54.4464", (float)1000.65);
        //AddElement("Shasta(fake)", "23.565", "-46.54", (float)2365.76784);
    }

    void AddElement(String name, String latitude, String longitude, float elevation, String notes)
    {
        /***********************************************
         * Dynamically adds elements into the UI given a name, latitude,
         * longitude, and elevation
         *
         * Will add MediaWiki info and other attributes as we need them
         * (Will likely still not affect the UI but needs to be passed through)
        ************************************************/
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(landmarkInfo.getLayoutParams());

        CustomLandmarkLayout vert = new CustomLandmarkLayout(this, name, latitude, longitude, elevation, notes);
        vert.setOnClickListener(listener);
        vert.setOrientation(LinearLayout.HORIZONTAL);

        vert.setBackgroundColor(Color.LTGRAY);
        vert.setPadding(20,20,20,20);

        ImageView img = new ImageView(this);
        img.setBackgroundColor(Color.WHITE);

        ViewGroup.LayoutParams imgSize = new ViewGroup.LayoutParams(200, 200);
        //custom pictures would ideally go in here
        //Uses default pictures for now
        img.setImageResource(R.drawable.logo3);
        img.setLayoutParams(imgSize);
        vert.addView(img);

        tempView = new TextView(this);
        String[] attributesArr = getResources().getStringArray(R.array.ShortenedLocData);
        String conCatStr = attributesArr[0] + ": " + name + "\n" + attributesArr[1] + ": " + latitude +
                "\n" + attributesArr[2] + ": " + longitude + "\n" + attributesArr[3] + ": " + elevation;

        tempView.setText(conCatStr); //concats strings together ¯\_(ツ)_/¯ Will probably fix later
        //Addendum: Also, should add multiple textboxes in the future
        tempView.setTextColor(Color.BLACK);
        tempView.setPadding(10,0,0,0);
        tempView.setLayoutParams(params);
        tempView.setTextAppearance(R.style.ListElemText); //set text appearance
        vert.addView(tempView);

        LandmarkList.addView(vert);// add element to the list
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LandmarkClickListener(v);
        }
    };

    private void LandmarkClickListener(View v)
    {
        /*******************************************************
         * Sends info to another page which changes depending on the data recieved
         * Will not change as elements are addeed
        *******************************************************/
        CustomLandmarkLayout pushed = (CustomLandmarkLayout) v; //gets LandmarkLayout element
        //Should not have any other object using this listener
        CustomLandmarkPass passing = pushed.GetLandmark(); //gets the landmark

        Intent i = new Intent(this, CustomSelected.class);
        i.putExtra("passing_custom", passing); //passes the landmark into Intent

        startActivity(i);
    }

    public void addCustom(View v)
    {
        Intent customAdd = new Intent(this, addCustomLandmark.class);
        startActivity(customAdd);
    }

    /*@Override
    protected void onResume() {
        super.onResume();

        LandmarkedMain main = LandmarkedMain.getInstance();

        List<CustomLocalLandmark> val = main.getAllCustomLocals();

        for (int i = 0; i < val.size(); i++)
        {
            CustomLocalLandmark res = val.get(i);
            AddElement(res.getName(), res.m_latitude, res.m_longitude, res.m_elevation);
        }
        /*CustomLocalLandmarkAccessorMethods_Impl grab = new CustomLocalLandmarkAccessorMethods_Impl(AppDatabase.getM_DB_instance(this));
        CustomLocalLandmark[] arr = grab.getAll();

        for(int i = 0; i < arr.length; i++)
        {
            CustomLocalLandmark res = arr[i];
            AddElement(res.getName(), res.m_latitude, res.m_longitude, res.m_elevation);
        }
    }*/

    public void elementWrapper(CustomLocalLandmark custom)
    {
        AddElement(custom.getName(), custom.getLatitude(), custom.getLongitude(), custom.getElevation(), custom.getWiki());
    }
}