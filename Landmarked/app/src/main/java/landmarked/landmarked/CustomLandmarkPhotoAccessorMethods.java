package landmarked.landmarked;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

@Dao
public interface CustomLandmarkPhotoAccessorMethods {
    @Query("Select PhotoID From CustomLandmarkPhoto")
    int CustomLM_getPhotoID();

    @Query("SELECT CustomLandmarkID FROM CustomLandmarkPhoto")
    int CustomLM_getCMLandmarkID();

    @Query("SELECT FileName FROM CustomLandmarkPhoto")
    String CustomLM_getFileName();

    @Query("SELECT DateTaken FROM CustomLandmarkPhoto")
    long CustomLM_getDateTaken();

    @Query("SELECT FilePath FROM CustomLandmarkPhoto")
    String CustomLM_getFilePath();
}
