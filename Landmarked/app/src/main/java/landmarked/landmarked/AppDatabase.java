package landmarked.landmarked;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {LocalLandmark.class, CustomLocalLandmark.class,
        CustomLandmarkPhoto.class, CustomLandmarkNotes.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    //Interface methods found in AccessorMethods.java
    public abstract AccessorMethods methods();
}
