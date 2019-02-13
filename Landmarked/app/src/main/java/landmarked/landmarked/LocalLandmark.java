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

    @NonNull
    String getM_name()
    {
        return m_name;
    }


    public LocalLandmark()
    {
        m_name = "none provided";
        m_latitude = "none provided";
        m_longitude = "none provided";
        m_elevation = 0.0f;
        m_wiki_info = "none provided";
    }
    public LocalLandmark(String name, String latitude, String longitude, float elevation, String wiki)
    {
        m_name = name;
        m_latitude = latitude;
        m_longitude = longitude;
        m_elevation = elevation;
        m_wiki_info = wiki;
    }
    public LocalLandmark(LocalLandmark landmarkArg)
    {
        m_name = landmarkArg.m_name;
        m_latitude = landmarkArg.m_latitude;
        m_longitude = landmarkArg.m_longitude;
        m_elevation = landmarkArg.m_elevation;
        m_wiki_info = landmarkArg.m_wiki_info;
    }

}
