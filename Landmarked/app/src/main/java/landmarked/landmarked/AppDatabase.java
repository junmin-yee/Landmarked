package landmarked.landmarked;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {LocalLandmark.class, CustomLocalLandmark.class,
        CustomLandmarkPhoto.class, CustomLandmarkNotes.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {


    private static AppDatabase m_DB_instance; //Singleton instance of DB;

    public abstract AccessorMethods methods(); //Interface methods found in AccessorMethods.java

    private static Context m_context;



    public static AppDatabase getM_DB_instance(Context context) // if hte db already exists, we'll return the instance. otherwise create the db
    {
        if(m_DB_instance == null)
        {
            m_DB_instance  = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "Landmarks").build(); // CREATE DB
        }
        return m_DB_instance;
    }
}
