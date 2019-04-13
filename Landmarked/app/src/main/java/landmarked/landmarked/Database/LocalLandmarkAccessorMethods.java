package landmarked.landmarked.Database;

import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Dao;


//This is the method where we can write code for populating or getting data from the local DB. This is also where the code will reside
//that allows the local DB to connect with the Azure DB. I've left it empty except a testing method for now because i'm not completely
//sure what methods will be needed.


// SELECT * FROM ... is just a demo i used that i thought might be useful , we can add query.

// It's important to note that in this context, LocalLandmark is a table. Technically it's an @Entity,
//But it's important we realize that @Entity represents a table.

//For more information on accessing data in the DB:
//https://developer.android.com/training/data-storage/room/accessing-data
@Dao
public interface LocalLandmarkAccessorMethods {



    @Query("SELECT * FROM LocalLandmark")
    LocalLandmark[] getAll();


    @Query("SELECT Name FROM LocalLandmark")
    String getLocalLandmarkName();

    @Query("SELECT Latitude FROM LocalLandmark")
    String getLatitude();

    @Query("SELECT Longitude FROM LocalLandmark")
    String getLongitude();

    @Query("SELECT Elevation FROM LocalLandmark")
    float getElevation();

    @Query("SELECT WikiInfo FROM LocalLandmark")
    String getWiki();



    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertLocalLandmarkStructure(LocalLandmark LandmarkArg);




}
