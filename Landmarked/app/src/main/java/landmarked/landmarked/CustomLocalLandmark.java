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

    public CustomLocalLandmark(String name, String latitude, String longitude, float elevation, String wiki, Date date)
    {
        m_name = name;
        m_latitude = latitude;
        m_longitude = longitude;
        m_elevation =elevation;
        m_wiki_info = wiki;
        m_datesaved = date;
    }
}