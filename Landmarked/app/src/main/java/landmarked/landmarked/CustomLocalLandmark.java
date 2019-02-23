package landmarked.landmarked;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class CustomLocalLandmark {
    @PrimaryKey
    public int CustomLandmarkID;

    @ColumnInfo(name="Name")
    public String m_name;

    @ColumnInfo(name = "Latitude")
    public String m_latitude;

    @ColumnInfo(name = "Longitude")
    public String m_longitude;

    @ColumnInfo(name = "Elevation")
    public float m_elevation;

    @ColumnInfo(name = "DateSaved")
    public Date m_datesaved;

    @ColumnInfo(name="Wiki")
    public String m_wiki_info;

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
    public final Date getDate() { return m_datesaved; }


    public CustomLocalLandmark()
    {
        m_name = "none provided";
        m_latitude = "none provided";
        m_longitude = "none provided";
        m_elevation = 0.0f;
        m_wiki_info = "none provided";
        m_datesaved = null;

    }


    public CustomLocalLandmark(String name, String latitude, String longitude, float elevation, String wiki, Date date)
    {
        m_name = name;
        m_latitude = latitude;
        m_longitude = longitude;
        m_elevation =elevation;
        m_wiki_info = wiki;
        m_datesaved = date;
    }

    public CustomLocalLandmark(CustomLocalLandmark landmarkArg)
    {
        m_name = landmarkArg.m_name;
        m_latitude = landmarkArg.m_latitude;
        m_longitude = landmarkArg.m_longitude;
        m_elevation = landmarkArg.m_elevation;
        m_wiki_info = landmarkArg.m_wiki_info;
        m_datesaved = landmarkArg.m_datesaved;
    }
}