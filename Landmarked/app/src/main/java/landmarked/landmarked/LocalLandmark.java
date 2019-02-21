package landmarked.landmarked;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

/////////////////////////////////read me/////////////////////////////////
/////////////////////////////////////////////////////////////////////////
//This is the " script " that creates our DB locally. Mind the types and how they are different from our
//DB fields - we will need to test all input after it's recieved to make sure lengths aren't violated.
// Example, how do we represent a 1024 sized varchar with a string?

//Changes to the can be made here, but probably the best policy is to delete the DB and start fresh.
//IT IS VERY, VERY IMPORTANT TO UNDERSTAND THAT AN @Entity REPRESENTS A TABLE IN THIS IMPLEMENTATION.
//for more information: https://developer.android.com/training/data-storage/room/#java


@Entity
public class LocalLandmark  {



    @PrimaryKey
    public int LandmarkID;

    @ColumnInfo(name="Name")
    public String m_name;

    @ColumnInfo(name = "Latitude")
    public String m_latitude;

    @ColumnInfo(name = "Longitude")
    public String m_longitude;

    @ColumnInfo(name = "Elevation")
    public float m_elevation;

    @ColumnInfo(name = "WikiInfo")
    public String m_wiki_info;

    //default ctor
    public LocalLandmark()
    {
        m_name = "none provided";
        m_latitude = "none provided";
        m_longitude = "none provided";
        m_elevation = 0.0f;
        m_wiki_info = "none provided";

    }

    //primitive arg ctor
    public LocalLandmark(String name, String latitude, String longitude, float elevation, String wiki)
    {
        m_name = name;
        m_latitude = latitude;
        m_longitude = longitude;
        m_elevation = elevation;
        m_wiki_info = wiki;

    }


    //copy ctor
    public LocalLandmark(LocalLandmark landmarkArg)
    {
        m_name = landmarkArg.m_name;
        m_latitude = landmarkArg.m_latitude;
        m_longitude = landmarkArg.m_longitude;
        m_elevation = landmarkArg.m_elevation;
        m_wiki_info = landmarkArg.m_wiki_info;

    }

    //going to use a singleton pattern toinitialize a single thread for sql operations to share


    //Returns the thread, should be called after initThread ONLY. i seperated this form initThread to save the overhead
    //of checking each time if backgroundThread is null, but this could easily be changed if it's too confusing to
    //maintain two seperate functions


    public final String getName()
    {
        return m_name;
    }
    public final String getLatitude()
    {
        return m_latitude;
    }
    public final String getLongitude()
    {
        return m_longitude;
    }
    public final float getElevation()
    {
        return m_elevation;
    }
    public final String getWiki()
    {
        return m_wiki_info;
    }


}
