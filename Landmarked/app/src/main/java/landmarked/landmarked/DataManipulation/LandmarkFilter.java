package landmarked.landmarked.DataManipulation;


import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import java.util.HashSet;
import java.util.Set;

public class LandmarkFilter {

    public Set<CarmenFeature> whitelistFilter(Set<CarmenFeature> filteredLandmarks) {

        Set<CarmenFeature> filter = new HashSet<>();

        // filter by whitelist of objects

        filteredLandmarks.retainAll(filter);



        return filteredLandmarks;
    }


}
