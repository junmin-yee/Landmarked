package landmarked.landmarked.Database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {LocalLandmark.class, CustomLocalLandmark.class,
        CustomLandmarkPhoto.class, CustomLandmarkNotes.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {


    private static AppDatabase m_DB_instance; //Singleton instance of DB;

    public abstract LocalLandmarkAccessorMethods LocalLandmarkMethodsVar(); //Interface methods found in LocalLandmarkAccessorMethods.java
    public abstract CustomLocalLandmarkAccessorMethods CustomMethodsVar(); //Interface methods found in CustomLocalLandmarkAccessorMethods.java

    private static Context m_context;



    public static AppDatabase getM_DB_instance(final Context context) // if hte db already exists, we'll return the instance. otherwise create the db
    {
        if (m_DB_instance == null)
        {
            synchronized (LocalLandmarkAccessorMethods.class)
            {
                if (m_DB_instance == null)
                {
                    m_DB_instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "Landmarks")
                            .fallbackToDestructiveMigration()
                            .build(); // CREATE DB
                }
                return m_DB_instance;
            }
        }
        return m_DB_instance;
    }

}
