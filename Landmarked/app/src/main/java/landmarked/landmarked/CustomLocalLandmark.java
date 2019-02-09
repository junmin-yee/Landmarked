package landmarked.landmarked;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class CustomLocalLandmark implements CustomLocalLandmarkAccessorMethods {
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

    public String CustomLM_getName()
    {
        String name = " ";
        return name;
    }

    public String CustomLM_getLatitude()
    {
        String latitude = " ";
        return latitude;
    }

    public String CustomLM_getLongitude()
    {
        String longitude = " ";
        return longitude;
    }

    public float CustomLM_getElevation()
    {
        float elevation = 0.0f;
        return elevation;
    }

    public long CustomLM_getDateSaved()
    {
        long Date = 0x11111111;
        return Date;
    }
    public void insertCustomLandmarkPrimitive(String nameArg, String latArg, String longArg, float elevationArg,  Date dateArg)
    {

    }

    public void insertCustomLandmarkStructure(CustomLocalLandmark landmarkArg)
    {

    }

}