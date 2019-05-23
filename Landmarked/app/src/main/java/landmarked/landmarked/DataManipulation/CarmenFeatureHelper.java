package landmarked.landmarked.DataManipulation;

import android.util.Log;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;

import java.util.List;

public class CarmenFeatureHelper {

    private static final String TAG = "CarmenFeatureHelper";
    private double mLandmarkLatitude;
    private double mLandmarkLongitude;
    private String mLandmarkName;
    private String mLandmarkPlaceName;
    private List<String> mLandmarkPlaceType;
    private String mLandmarkWikiData;

    private double mLandmarkElevation;
    private boolean mHasElevation;

    public CarmenFeatureHelper(CarmenFeature carmenFeature) {

        try {
            mLandmarkLatitude = carmenFeature.center().latitude();
            mLandmarkLongitude = carmenFeature.center().longitude();
        }
        catch(NullPointerException e) {
            Log.e(TAG, "Exception caught in CarmenFeatureHelper: ", e);
            // Sets to invalid (impossible) Latitude and Longitude.
            mLandmarkLatitude = -100.0;
            mLandmarkLongitude = -100.0;
        }

        // Returns null if there is no wikidata entry within "properties"
        if (carmenFeature.properties().has("wikidata")) {
            mLandmarkWikiData = carmenFeature.properties().get("wikidata").getAsString();
        } else {
            mLandmarkWikiData = "";
        }

        // Checks if landmark has an associated altitude
        if ( carmenFeature.center().hasAltitude() ) {
            mLandmarkElevation = carmenFeature.center().altitude();
            mHasElevation = true;
        }
        else {
            mLandmarkElevation = 0; // throwaway
            mHasElevation = false;
        }

        // .text results in a simplified name such as "Klamath Falls"
        mLandmarkName = carmenFeature.text();

        // .placeName results in an expanded name: "Klamath Lake, 11100-12898 The Dalles-California Hwy, Chiloquin, Oregon, 97624, United States"
        // Appears to be the address, though there are more similar fields labelled "address"
        mLandmarkPlaceName = carmenFeature.placeName();

        // List of Strings that represent the type oc Carmen Feature it is. (e.g. Street, Address, POI, etc.)
        mLandmarkPlaceType = carmenFeature.placeType();
    }

    public double getLandmarkLatitude() {
        return mLandmarkLatitude;
    }

    public double getLandmarkLongitude() {
        return mLandmarkLongitude;
    }

    public String getLandmarkName() {
        return mLandmarkName;
    }

    public String getLandmarkPlaceName() {
        return mLandmarkPlaceName;
    }

    public List<String> getmLandmarkPlaceType() { return mLandmarkPlaceType; }

    public String getLandmarkWikiData() { return mLandmarkWikiData; }

    // Call checkElevationExists before trusting this value.
    public double getLandmarkElevation() {
        return mLandmarkElevation;
    }

    // Check this before trusting the mLandmarkElevation value.
    // returns true if it does, false otherwise.
    public boolean checkElevationExists() {
        return mHasElevation;
    }
}
