package landmarked.landmarked;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/////////////////////////////////read me/////////////////////////////////
/////////////////////////////////////////////////////////////////////////
//This is the " script " that creates our DB locally. Mind the types and how they are different from our
//DB fields - we will need to test all input after it's recieved to make sure lengths aren't violated.
// Example, how do we represent a 1024 sized varchar with a string?

//Changes to the can be made here, but probably the best policy is to delete the DB and start fresh.
//IT IS VERY, VERY IMPORTANT TO UNDERSTAND THAT AN @Entity REPRESENTS A TABLE IN THIS IMPLEMENTATION.
//for more information: https://developer.android.com/training/data-storage/room/#java


@Entity
public class LocalLandmark {
    @PrimaryKey
    public int UserID;

    @ColumnInfo(name="Name")
    public String m_name;

    @ColumnInfo(name = "Latitude")
    public String m_lattitude;

    @ColumnInfo(name = "Longitude")
    public String m_longitude;

    @ColumnInfo(name = "Elevation")
    public float m_elevation;

    @ColumnInfo(name = "WikiInfo")
    public String m_wiki_info;
}
