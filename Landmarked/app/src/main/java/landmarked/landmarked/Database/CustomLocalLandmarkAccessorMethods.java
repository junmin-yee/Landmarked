package landmarked.landmarked.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;


@Dao
public interface CustomLocalLandmarkAccessorMethods {

    //format:
    //Query
    //Function prototype for the function that will call the query
    @Query("Select * From CustomLocalLandmark")
    public CustomLocalLandmark[] getAll();

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
