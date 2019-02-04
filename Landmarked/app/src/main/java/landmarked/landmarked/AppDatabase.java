package landmarked.landmarked;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {LocalLandmark.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    //Interface methods found in AccessorMethods.java
    public abstract AccessorMethods methods();
}
