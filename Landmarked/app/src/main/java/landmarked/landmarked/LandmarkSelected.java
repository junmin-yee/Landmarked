package landmarked.landmarked;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import landmarked.landmarked.Database.LocalLandmarkPass;

public class LandmarkSelected extends AppCompatActivity {
    static LandmarkedMain m_instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_instance = m_instance.getInstance();
        setContentView(R.layout.activity_landmark_selected);

        Intent i = getIntent();
        LocalLandmarkPass my_land = i.getParcelableExtra("passing_landmark");

        TextView LandmarkInfo = findViewById(R.id.LandmarkInfo);

        LandmarkInfo.setTextSize(32);
        LandmarkInfo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        String[] attributesArr = getResources().getStringArray(R.array.ShortenedLocData);
        String conCatStr = attributesArr[0] + ": " + my_land.getName() + "\n" + attributesArr[1] + ": " + my_land.getLatitiude() +
                "\n" + attributesArr[2] + ": " + my_land.getLongitude() + "\n" + attributesArr[3] + ": " + my_land.getElevation();
        m_instance.InsertAzure(my_land.getName(), my_land.getLatitiude(), my_land.getLongitude(),my_land.getElevation(), "" );
        LandmarkInfo.setText(conCatStr);
    }
}
