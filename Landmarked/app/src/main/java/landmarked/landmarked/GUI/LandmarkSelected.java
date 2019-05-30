package landmarked.landmarked.GUI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import landmarked.landmarked.Database.CustomLandmark;
import landmarked.landmarked.Database.CustomLandmarkPass;
import landmarked.landmarked.Database.LocalLandmarkPass;
import landmarked.landmarked.LandmarkedMain;
import landmarked.landmarked.R;

public class LandmarkSelected extends AppCompatActivity {
    static LandmarkedMain m_instance;
    ImageView directionArrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_instance = m_instance.getInstance();
        setContentView(R.layout.activity_landmark_selected);

        Intent i = getIntent();

        LocalLandmarkPass my_land = i.getParcelableExtra("passing_landmark");

        TextView LandmarkInfo = findViewById(R.id.textView2);

        LandmarkInfo.setTextSize(32);
        LandmarkInfo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        String[] attributesArr = getResources().getStringArray(R.array.ShortenedLocData);
        String conCatStr = attributesArr[0] + ": " + my_land.getName() + "\n" + attributesArr[1] + ": " + my_land.getLatitiude() +
                "\n" + attributesArr[2] + ": " + my_land.getLongitude() + "\n" + attributesArr[3] + ": " + my_land.getElevation();

        directionArrow = findViewById(R.id.arrowImage);
        directionArrow.setRotation(30);

        TextView distanceInfo = findViewById(R.id.directionInfo);
        distanceInfo.setText("Insert distance information here:");

        //This is the call to insert into azure cloud
        m_instance.InsertAzure(my_land.getName(), my_land.getLatitiude(), my_land.getLongitude(),my_land.getElevation(), "" );
        LandmarkInfo.setText(conCatStr);
    }
}

