package landmarked.landmarked.GUI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import landmarked.landmarked.Database.AzureConnectionClass;
import landmarked.landmarked.Database.LandmarkLayout;
import landmarked.landmarked.Database.LocalLandmark;
import landmarked.landmarked.LandmarkedMain;
import landmarked.landmarked.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;


public class LandmarkHistory extends AppCompatActivity {

    private static final String TAG = "LandmarkHistory";
    static LandmarkedMain m_instance;
    public TextView landmarkInfo;
    public LinearLayout LandmarkList;
    public TextView tempView;
    static String m_username;
    public static ExecutorService m_thread;
    public static AzureConnectionClass m_conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_instance = m_instance.getInstance();
        m_username = m_instance.get_m_username();
        m_thread = m_instance.getThreadPoolInstance();
        m_conn = m_instance.get_azure_instance();

        setContentView(R.layout.activity_landmark_history);

        landmarkInfo = findViewById(R.id.LandmarkAttributes);

        LandmarkList = findViewById(R.id.LandmarkInfo);

        try {
            Vector<LocalLandmark> lst = m_instance.getUserLandmarksFromAzure();
            int x = 10;
            for (int v = 0; v < lst.size(); v++) {
                AddElement(lst.get(v).getName(), lst.get(v).getLatitude(), lst.get(v).getLongitude(), lst.get(v).getElevation());
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception caught in onCreate: ", e);
        }

        Intent i = getIntent();

        //ArrayList<LocalLandmark> recievedLandmark = i.getParcelableArrayListExtra("sending_history");
        // ArrayList<LocalLandmark> recievedLandmark = m_instance.getUserLandmarksFromAzure(m_instance.get_m_username());
    }

    void AddElement(String name, String latitude, String longitude, float elevation) {
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
        vert.setPadding(20, 20, 20, 20);

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
        tempView.setPadding(10, 0, 0, 0);
        tempView.setLayoutParams(params);
        tempView.setTextAppearance(R.style.ListElemText); //set text appearance
        vert.addView(tempView);

        LandmarkList.addView(vert);// add element to the list
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        { }
    };
}