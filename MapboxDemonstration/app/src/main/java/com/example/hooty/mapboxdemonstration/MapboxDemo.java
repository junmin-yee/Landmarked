package com.example.hooty.mapboxdemonstration;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;


import java.util.Map;

public class MapboxDemo extends AppCompatActivity {
    private MapView demoView;


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //We call Mapbox.getInstance() to get an instance of a map using this (the mapView member of this class), and our access token.
        //Once we've gotten an instance of Mapbox, assuming or key is valid, we can start initializing different map elements.
        Mapbox.getInstance(this, "pk.eyJ1IjoicmVkZ3JlZWQ0IiwiYSI6ImNqb2k3NXNpNjAyMGEzcXBhbThoeXBtOGcifQ.AG9JmnzPQKHuSxazOvrk3g");

        //***********************************************************************************************************************************************
        //DYNAMICALLY CREATING A MAPVIEW - vs loading data from resource (below)
        //Presumably we won't be new'ing an instance of MapboxMapOptions till we have the coordinates we want. There are many style tags here, i used defaults.
        //The important thing to note is the .target tag that allows you to target the exact coordinates you want.
        MapboxMapOptions options = new MapboxMapOptions()
                .styleUrl(Style.OUTDOORS)
                .camera(new CameraPosition.Builder()
                    .target(new LatLng(42.2110349, -121.72552199999998))//magic number coords, presumabley we will use variables
                        .zoom(12)
                        .build());

        demoView = new MapView(this, options);//initialize mapView with pointer to self and newly initialized map options above
        setContentView(demoView);//notice here we're changing the view based on whatever new mapView gets passed in
        //*************************************************************************************************************************************************






        //**************************************************************************************************************************************
        //LOADING MAP DATA FROM A RESOURCE!
        //This isn't probably going to be tall that useful to us since it's loading a resource from activity_mapbox_demo.xml. We're going to be doing this on the fly, not loading
        //predefined resources. Still, this is how you do it. Be sure to check out the xaml file to see what i'm talking about. I've commented out the line since i'm not creating
        //objects this way .

        //demoView = (MapView) findViewById(R.id.demoView);
        //setContentView(R.layout.activity_mapbox_demo); //notice here the actual app window is loading a map from a resource
        //************************************************************************************************************************************************





        demoView.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        demoView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        demoView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        demoView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        demoView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        demoView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        demoView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        demoView.onSaveInstanceState(outState);
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();
}
