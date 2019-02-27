package landmarked.landmarked;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import landmarked.landmarked.Database.LocalLandmarkPass;

public class LandmarkSelected extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_selected);

        Intent i = getIntent();
        LocalLandmarkPass my_land = (LocalLandmarkPass)i.getParcelableExtra("passing_landmark");

        TextView LandmarkInfo = findViewById(R.id.LandmarkInfo);

        LandmarkInfo.setTextSize(32);
        LandmarkInfo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        LandmarkInfo.setText("Name: " + my_land.getName() +"\nLat: " + my_land.getLatitiude() +"\nLong: " + my_land.getLongitude() + "\nElv: " + my_land.getElevation());
    }
}
