package landmarked.landmarked;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;

public class DisplayMap extends AppCompatActivity {
    private MapView demoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);


        Intent obtained = getIntent();
        double[] arr = obtained.getDoubleArrayExtra(LandmarkedMain.ACTIVITY_MESSAGE);

        Mapbox.getInstance(this, "pk.eyJ1IjoicmVkZ3JlZWQ0IiwiYSI6ImNqb2k3NXNpNjAyMGEzcXBhbThoeXBtOGcifQ.AG9JmnzPQKHuSxazOvrk3g");

        MapboxMapOptions options = new MapboxMapOptions()
                .styleUrl(Style.OUTDOORS)
                .camera(new CameraPosition.Builder()
                        .target(new LatLng(arr[0], arr[1]))
                                .zoom(17)
                                .build());

        demoView = new MapView(this, options);
        setContentView(demoView);
    }

    @Override
    protected void  onStart()
    {
        super.onStart();
    }
}
