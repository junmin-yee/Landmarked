package landmarked.landmarked.DataManipulation;


import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import java.util.HashSet;
import java.util.Set;

public class LandmarkFilter {

    /**placeTypeFilter
     * Extremely basic filter which removes anything that most certainly isn't a Landmark using the PlaceType parameters.
     *
     * See https://docs.mapbox.com/api/search/#data-types for different PlaceTypes.
     *
     * Crater Lake, for example, is a place.
     * Parks, Historical sites, etc. are Points of Interest (poi).
     * Upper Klamath Lake, for example, is an address. As are all streets and such.
     *
     * In short: What a mess.
      */
    public static boolean validPlaceType(CarmenFeature feature){
        // If the CarmenFeature is one of the following, it will not be filtered.
        return (feature.placeType().contains("poi") || feature.placeType().contains("address") || feature.placeType().contains("place"));
    }


    public static boolean validPropertyType(CarmenFeature feature){
        try {
            // if feature has the "accuracy" property, it is likely because it is a street. Check if it is a street.
            if (feature.properties().has("accuracy")) {
                return !(feature.properties().get("accuracy").getAsString().equals("street"));
            } else {
                return true; // Inconclusive. Feature does not have the "accuracy" property, therefore is may or may not be a valid result.
            }
        }
        catch (NullPointerException e){
            return true; // Also inconclusive.
        }
    }

    /**whitelistFilter
     *
     * @param filteredLandmarks
     * @return
     */
    public Set<CarmenFeature> whitelistFilter(Set<CarmenFeature> filteredLandmarks) {

        Set<CarmenFeature> filter = new HashSet<>();

        // filter by whitelist of objects
        filteredLandmarks.retainAll(filter);

        return filteredLandmarks;
    }
}
