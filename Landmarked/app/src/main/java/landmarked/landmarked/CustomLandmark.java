package landmarked.landmarked;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

class LandmarkLayout extends LinearLayout
{
    private LocalLandmarkPass m_landmark;

    LandmarkLayout(Context c, String name, String lat, String longitude, float elev)
    {
        super(c);
        m_landmark = new LocalLandmarkPass(name, lat, longitude, elev);
    }

    public LocalLandmarkPass GetLandmark(){return m_landmark;}
}

public class CustomLandmark extends AppCompatActivity {

    public TextView landmarkInfo;
    public LinearLayout LandmarkList;
    public TextView tempView;
    public SensorData mSensorData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_landmark);

        //get user ID that correlates to the database
        landmarkInfo = findViewById(R.id.LandmarkAttributes);

        landmarkInfo.setText("Custom Landmarks");

        LandmarkList = findViewById(R.id.landmarkList);

        /*mSensorData = new SensorData(this);
        mSensorData.registerOrientationSensors();
        mSensorData.registerLocationSensor();

        LandmarkRetrieval grabLandmark = new LandmarkRetrieval(mSensorData);

        Location loc = mSensorData.getCurrentLocation();
        if(loc != null) {
            grabLandmark.LandmarkSearch(loc, null);

            List<CarmenFeature> landmarkList = grabLandmark.getCarmenFeatureFwdResults();

            for (CarmenFeature feat : landmarkList) {
                CarmenFeatureHelper landmarkHelper = new CarmenFeatureHelper(feat);
                String name = landmarkHelper.getmLandmarkName();
                String latitude = Double.toString(landmarkHelper.getmLandmarkLatitude());
                String longitude = Double.toString(landmarkHelper.getmLandmarkLongitude());
                float elevation = (float) landmarkHelper.getmLandmarkElevation();

                AddElement(name, latitude, longitude, elevation);
            }
        }
        */


        AddElement("temp", "34.5634", "-54.4464", (float)1000.65);
        AddElement("Shasta(fake)", "23.565", "-46.54", (float)2365.76784);
    }

    void AddElement(String name, String latitude, String longitude, float elevation)
    {
        /***********************************************
         * Dynamically adds elements into the UI given a name, latitude,
         * longitude, and elevation
         *
         * Will add MediaWiki info and other attributes as we need them
         * (Will likely still not affect the UI but needs to be passed through)
        ************************************************/
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(landmarkInfo.getLayoutParams());

        LandmarkLayout vert = new LandmarkLayout(this, name, latitude, longitude, elevation);
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
        tempView.setText("Name: " + name  +"\nLat: " + latitude +"\nLong: " + longitude + "\nElv: " + elevation); //concats strings together ¯\_(ツ)_/¯ Will probably fix later
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
        LandmarkLayout pushed = (LandmarkLayout) v; //gets LandmarkLayout element
        //Should not have any other object using this listener
        LocalLandmarkPass passing = pushed.GetLandmark(); //gets the landmark

        Intent i = new Intent(this, LandmarkSelected.class);
        i.putExtra("passing_landmark", passing); //passes the landmark into Intent

        startActivity(i);
    }


}