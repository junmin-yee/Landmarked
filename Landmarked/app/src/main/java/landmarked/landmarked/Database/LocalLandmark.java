package landmarked.landmarked.Database;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/////////////////////////////////read me/////////////////////////////////
/////////////////////////////////////////////////////////////////////////
//This is the " script " that creates our DB locally. Mind the types and how they are different from our
//DB fields - we will need to test all input after it's recieved to make sure lengths aren't violated.
// Example, how do we represent a 1024 sized varchar with a string?

//Changes to the can be made here, but probably the best policy is to delete the DB and start fresh.
//IT IS VERY, VERY IMPORTANT TO UNDERSTAND THAT AN @Entity REPRESENTS A TABLE IN THIS IMPLEMENTATION.
//for more information: https://developer.android.com/training/data-storage/room/#java


@Entity
public class LocalLandmark implements Parcelable {
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

    @ColumnInfo(name="DateSaved")
    public Date m_date_saved;

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
    public LocalLandmark(String name, String latitude, String longitude, float elevation, String wiki, Date date)
    {
        m_name = name;
        m_latitude = latitude;
        m_longitude = longitude;
        m_elevation = elevation;
        m_wiki_info = wiki;
        m_date_saved = date;
    }

    //copy ctor
    public LocalLandmark(LocalLandmark landmarkArg)
    {
        m_name = landmarkArg.m_name;
        m_latitude = landmarkArg.m_latitude;
        m_longitude = landmarkArg.m_longitude;
        m_elevation = landmarkArg.m_elevation;
        m_wiki_info = landmarkArg.m_wiki_info;
        m_date_saved = landmarkArg.m_date_saved;
    }

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

    public int describeContents()
    {
        return 0;
    }

    public  void writeToParcel(Parcel output, int flags)
    {
        output.writeString(m_name);
        output.writeString(m_latitude);
        output.writeString(m_longitude);
        output.writeFloat(m_elevation);
        output.writeString(m_wiki_info);
        output.writeLong(m_date_saved.getTime());
    }

    public static final Creator<LocalLandmark> CREATOR = new Creator<LocalLandmark>() {
        @Override
        public LocalLandmark createFromParcel(Parcel input) {
            return new LocalLandmark(input);
        }

        @Override
        public LocalLandmark[] newArray(int size) {
            return new LocalLandmark[size];
        }
    };

    private LocalLandmark(Parcel input)
    {
        m_name = input.readString();
        m_latitude = input.readString();
        m_longitude = input.readString();
        m_elevation = input.readFloat();
        m_wiki_info = input.readString();
        m_date_saved = new Date(input.readLong());
    }
}
