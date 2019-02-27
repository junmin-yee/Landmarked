package landmarked.landmarked.DataManipulation;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;

public class CarmenFeatureHelper {

    private double mLandmarkLatitude;
    private double mLandmarkLongitude;
    private String mLandmarkName;
    private String mLandmarkPlaceName;
    private double mLandmarkElevation;

    private boolean mHasElevation;

    public CarmenFeatureHelper(CarmenFeature carmenFeature) {

        mLandmarkLatitude = carmenFeature.center().latitude();
        mLandmarkLongitude = carmenFeature.center().longitude();

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
        // Appears to be the address, though there are more similar fields laballed "address"
        mLandmarkPlaceName = carmenFeature.placeName();
    }

    public double getmLandmarkLatitude() {
        return mLandmarkLatitude;
    }

    public double getmLandmarkLongitude() {
        return mLandmarkLongitude;
    }

    public String getmLandmarkName() {
        return mLandmarkName;
    }

    public String getmLandmarkPlaceName() {
        return mLandmarkPlaceName;
    }

    // Call checkElevationExists before trusting this value.
    public double getmLandmarkElevation() {
        return mLandmarkElevation;
    }

    // Check this before trusting the mLandmarkElevation value.
    // returns true if it does, false otherwise.
    public boolean checkElevationExists() {
        return mHasElevation;
    }
}
