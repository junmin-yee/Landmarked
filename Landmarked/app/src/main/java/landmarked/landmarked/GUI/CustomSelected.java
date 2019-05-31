package landmarked.landmarked.GUI;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import landmarked.landmarked.Database.CustomLandmarkPass;
import landmarked.landmarked.LandmarkedMain;
import landmarked.landmarked.R;

public class CustomSelected extends AppCompatActivity
{
    static LandmarkedMain m_instance;
    ImageView directionArrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_instance = m_instance.getInstance();
        setContentView(R.layout.activity_landmark_selected);

        Intent i = getIntent();

        CustomLandmarkPass my_land = i.getParcelableExtra("passing_custom");

        TextView LandmarkInfo = findViewById(R.id.textView2);

        LandmarkInfo.setTextSize(32);
        LandmarkInfo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        String[] attributesArr = getResources().getStringArray(R.array.ShortenedLocData);
        String conCatStr = attributesArr[0] + ": " + my_land.getName() + "\n" + attributesArr[1] + ": " + my_land.getLatitiude() +
                "\n" + attributesArr[2] + ": " + my_land.getLongitude();

        //This is the call to insert into azure cloud
        //m_instance.InsertAzure(my_land.getName(), my_land.getLatitiude(), my_land.getLongitude(),my_land.getElevation(), "" );

        directionArrow = findViewById(R.id.arrowImage);
        //directionArrow.setRotation(90); //math to set the rotation of the arrow should go here

        //float rotAmount = LandmarkedMain.getInstance().mSensorData.getCurrentLocation().distanceTo(new Location(my_land.getLatitiude(), my_land.getLongitude()));
        Location l = new Location("");
        l.setLatitude(Double.parseDouble(my_land.getLatitiude()));
        l.setLongitude(Double.parseDouble(my_land.getLongitude()));

        TextView distanceInfo = findViewById(R.id.directionInfo);
        distanceInfo.setText("Insert distance information here:");
        //distanceInfo.setText(Float.toString(m_instance.mSensorData.getCurrentLocation().distanceTo(l)));
        try {
            distanceInfo.setText(Float.toString(m_instance.mSensorData.getCurrentLocation().distanceTo(l) / 1000) + " km to custom landmark");
        }
        catch(Exception e)
        {
            distanceInfo.setVisibility(View.GONE);
        }

        //directionArrow.setRotation(m_instance.mSensorData.getCurrentLocation().bearingTo(l));
        directionArrow.setVisibility(View.GONE);

        LandmarkInfo.setText(conCatStr);

        LandmarkInfo = findViewById(R.id.noteInfo);
        LandmarkInfo.setText("Notes:\n\n");
        if(my_land.getNotes() == null)
        {
            LandmarkInfo.append("{Note field left blank}");
        }
        else
        {
            LandmarkInfo.append(my_land.getNotes());
        }
    }
}
