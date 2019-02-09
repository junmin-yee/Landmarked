package landmarked.landmarked;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

/////////////////////////////////read me/////////////////////////////////
/////////////////////////////////////////////////////////////////////////
//This is the " script " that creates our DB locally. Mind the types and how they are different from our
//DB fields - we will need to test all input after it's recieved to make sure lengths aren't violated.
// Example, how do we represent a 1024 sized varchar with a string?

//Changes to the can be made here, but probably the best policy is to delete the DB and start fresh.
//IT IS VERY, VERY IMPORTANT TO UNDERSTAND THAT AN @Entity REPRESENTS A TABLE IN THIS IMPLEMENTATION.
//for more information: https://developer.android.com/training/data-storage/room/#java


@Entity
public class LocalLandmark implements LocalLandmarkAccessorMethods {
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


    public List<LocalLandmark> getAll()
    {
        List<LocalLandmark> temp = new ArrayList<>();
        return temp;
    }

    public String getLocalLandmarkName()
    {
        String name = " ";
        return name;
    }
    public String getLatitude()
    {
        String latt = " ";
        return latt;
    }

    public String getLongitude()
    {
        String longitude = " " ;
        return longitude;
    }

    public float getElevation()
    {
        float elevation = 0.0f;
        return elevation;
    }

    public String getWiki()
    {
        String wiki_information = " ";
        return wiki_information;
    }

//why do i have problems when i try and create an insert function that takes numerous primitive type arguments?


    public void insertLocalLandmarkStructure(LocalLandmark landmarkArg)
    {

    }
}
