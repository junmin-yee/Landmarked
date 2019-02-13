package landmarked.landmarked;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

        //landmarkInfo.setText("Hello");

        LandmarkList = findViewById(R.id.landmarkList);

        AddElement(32.543, 125.4465, 304);
        AddElement(43.567, 10.43434, 10000323);
        AddElement(213.65, 233453, -2);
        AddElement(213.65, 233453, -2);
        AddElement(213.65, 233453, -2);
        AddElement(213.65, 233453, -2);
        AddElement(213.65, 233453, -2);
        AddElement(213.65, 233453, -2);
        AddElement(213.65, 233453, -2);
        AddElement(213.65, 233453, -2);
        AddElement(213.65, 233453, -2);
        AddElement(213.65, 233453, -2);
    }

    void AddElement(double latitude, double longitude, double elevation)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(landmarkInfo.getLayoutParams());

        LinearLayout vert = new LinearLayout(this);
        vert.setOrientation(LinearLayout.HORIZONTAL);

        vert.setBackgroundColor(Color.LTGRAY);
        vert.setPadding(20,20,20,20);

        ImageView img = new ImageView(this);
        img.setBackgroundColor(Color.WHITE);

        ViewGroup.LayoutParams imgSize = new ViewGroup.LayoutParams(200, 200);
        img.setImageResource(R.drawable.logo3);
        img.setLayoutParams(imgSize);
        vert.addView(img);

        tempView = new TextView(this);
        tempView.setText("Lat: " + latitude +"\nLong: " + longitude + "\nElv: " + elevation);
        tempView.setTextColor(Color.BLACK);
        tempView.setPadding(10,0,0,0);
        tempView.setLayoutParams(params);
        tempView.setTextAppearance(R.style.ListElemText);
        vert.addView(tempView);

        LandmarkList.addView(vert);
    }

}