package landmarked.landmarked;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class CustomLandmark extends AppCompatActivity {

    public TextView landmarkInfo;
    public LinearLayout LandmarkList;
    public TextView tempView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_landmark);

        //get user ID that correlates to the database
        landmarkInfo = findViewById(R.id.textView2);

        landmarkInfo.setText("Hello");

        LandmarkList = findViewById(R.id.landmarkList);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(landmarkInfo.getLayoutParams());
        //LandmarkList.setOrientation(LinearLayout.VERTICAL);

        LinearLayout vert = new LinearLayout(this);
        vert.setOrientation(LinearLayout.HORIZONTAL);
        vert.setBackgroundColor(Color.GREEN);
        vert.setPadding(20,20,0,20);

        ImageView img = new ImageView(this);
        img.setBackgroundColor(Color.BLACK);
        img.setPadding(25,25,25,25);
        img.setMinimumWidth(300);
        img.setMinimumHeight(300);
        img.setMaxWidth(300);
        img.setMaxHeight(300);
        img.setImageResource(R.mipmap.logo3);
        vert.addView(img);

        tempView = new TextView(this);
        tempView.setText("Hello");
        tempView.setLayoutParams(params);
        tempView.setTextAppearance(R.style.ListElemText);
        vert.addView(tempView);

        LandmarkList.addView(vert);

        tempView = new TextView(this);
        tempView.setText("Grizzledor");
        tempView.setLayoutParams(params);
        tempView.setTextAppearance(R.style.ListElemText);
        LandmarkList.addView(tempView);
    }
}
