package landmarked.landmarked.DataManipulation;


import android.util.Log;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import java.util.HashSet;
import java.util.Set;

public class LandmarkFilter {

    private static final String TAG = "LandmarkedFilter";

    /**isValidPlaceType
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
    public static boolean isValidPlaceType(CarmenFeature feature){
        // If the CarmenFeature is one of the following, it will not be filtered.
        return (feature.placeType().contains("poi") || feature.placeType().contains("address") || feature.placeType().contains("place"));
    }

    /**isStreet
     * Checks if feature is a street.
     *
     * @param feature Carmen Feature being tested
     * @return True if valid
     */
    public static boolean isStreet(CarmenFeature feature){
        try {
            // if feature has the "accuracy" property, it is likely because it is a street. Check if it is a street.
            if (feature.properties().has("accuracy")) {
                return feature.properties().get("accuracy").getAsString().equals("street");
            }
        }
        catch (NullPointerException e){
            Log.d(TAG, "validPropertyType no 'accuracy' property found by has()", e);
        }

        return false; // inconclusive. If no properties member "accuracy" exists, feature is not a street.
    }

    /**isHistorical
     * Checks if the feature is related to history in any way.
     *
     * @param feature Carmen Feature being tested
     * @return True if valid
     */
    public static boolean isHistorical(CarmenFeature feature){
        String catString;

        try {
            // if feature has the category property.
            if (feature.properties().has("category")){
                catString = feature.properties().get("category").getAsString();

                // If the feature is any identified history-related landmark, return true.
                return catString.contains("history") || catString.contains("historical") || catString.contains("historical site");
            }
        }
        catch (NullPointerException e) {
            Log.d(TAG, "isHistorical no 'category' property found by has()", e);
        }

        // Default case.
        return false;
    }

    /**isLodging
     * Checks if the feature is a hotel, motel, or a place of lodging.
     *
     * @param feature Carmen Feature being tested.
     * @return True if valid
     */
    public static boolean isLodging(CarmenFeature feature){
        String catString;

        try {
            // if feature has the category property.
            if (feature.properties().has("category")){
                catString = feature.properties().get("category").getAsString();

                // If the feature is any identified history-related landmark, return true.
                return catString.contains("hotel") || catString.contains("motel") || catString.contains("lodging");
            }
        }
        catch (NullPointerException e) {
            Log.d(TAG, "isLodging no 'category' property found by has()", e);
        }

        // Default case.
        return false;
    }

    /**isNatural
     * Checks if the feature is natural.
     *
     * @param feature Carmen Feature being tested.
     * @return True if valid
     */
    public static boolean isNatural(CarmenFeature feature){
        String catString;

        try {
            // if feature has the category property.
            if (feature.properties().has("category")){
                catString = feature.properties().get("category").getAsString();

                // If the feature is any identified history-related landmark, return true.
                return catString.contains("natural");
            }
        }
        catch (NullPointerException e) {
            Log.d(TAG, "isNatural no 'category' property found by has()", e);
        }

        // Default case.
        return false;
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
