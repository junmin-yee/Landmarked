package landmarked.landmarked;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import java.util.Date;


@Dao
public interface CustomLocalLandmarkAccessorMethods {

    @Query("Select Name From CustomLocalLandmark")
    String CustomLM_getName();

    @Query("SELECT Latitude FROM CustomLocalLandmark")
    String CustomLM_getLatitude();

    @Query("SELECT Longitude FROM CustomLocalLandmark")
    String CustomLM_getLongitude();

    @Query("SELECT Elevation FROM CustomLocalLandmark")
    float CustomLM_getElevation();

    @Query("SELECT DateSaved FROM CustomLocalLandmark")
    long CustomLM_getDateSaved();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCustomLandmarkStructure(CustomLocalLandmark LandmarkArg);


}
