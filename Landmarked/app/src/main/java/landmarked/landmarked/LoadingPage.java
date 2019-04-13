package landmarked.landmarked;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import landmarked.landmarked.DataManipulation.CarmenFeatureHelper;
import landmarked.landmarked.Database.LocalLandmark;

public class LoadingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        LandmarkedMain ref = LandmarkedMain.getInstance();
        ref.currOrientation = ref.mSensorData.getCurrentOrientation();

        try
        {
            ref.currLocation = ref.mSensorData.getCurrentLocation();
            ref.mLandmarkRetrieval.LandmarkProximitySearch(ref.currLocation);

            Set<CarmenFeature> retrievedLandmarks = ref.mLandmarkRetrieval.getLandmarkProximitySearchResults();

            Iterator<CarmenFeature> retLanIterator = retrievedLandmarks.iterator();

            if(retrievedLandmarks.size() > 0)
            {
                ref.landmarkGet.clear();
                while(retLanIterator.hasNext())
                {
                    CarmenFeatureHelper retriever = new CarmenFeatureHelper(retLanIterator.next());

                    double lat = retriever.getLandmarkLatitude();
                    double lon = retriever.getLandmarkLongitude();
                    String name = retriever.getLandmarkName();
                    String placename = retriever.getLandmarkName();
                    String wikidata = retriever.getLandmarkWikiData();
                    Date lan_date = new Date();

                    boolean test_elev = retriever.checkElevationExists();
                    double elev_result;

                    if (test_elev)
                        elev_result = retriever.getLandmarkElevation();
                    else {
                        elev_result = ref.mSensorData.getCurrentLocation().getAltitude();       // else return current altitude/elevation
                    }

                    ref.landmarkGet.add(new LocalLandmark(placename, Double.toString(lat), Double.toString(lon), (float)elev_result, wikidata, lan_date));
                }
            }
            else
                throw new NullPointerException("Landmark search test failed."); // temporary so the UI seems to be a bit more fluid. Otherwise it will display data but not have any landmarks.
        }
        catch (SecurityException | NullPointerException e)
        {}
        finish();
    }
}
