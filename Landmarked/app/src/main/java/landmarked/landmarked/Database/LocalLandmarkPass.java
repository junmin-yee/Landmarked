package landmarked.landmarked.Database;

import android.os.Parcel;
import android.os.Parcelable;

public class LocalLandmarkPass implements Parcelable
{
    private String m_name;
    private String m_latitude;
    private String m_longitude;
    private float m_elevation;
    //no wiki info or otherwise required yet

    public LocalLandmarkPass()
    {
        m_name = "";
        m_latitude = "0";
        m_longitude = "0";
        m_elevation = 0;
    }

    public LocalLandmarkPass(LocalLandmark landmark)
    {
        m_name = landmark.getName();
        m_latitude = landmark.getLatitude();
        m_longitude = landmark.getLongitude();
        m_elevation = landmark.getElevation();
    }

    public LocalLandmarkPass(String name, String lat, String longitude, float elev)
    {
        m_name = name;
        m_latitude = lat;
        m_longitude = longitude;
        m_elevation = elev;
    }

    private LocalLandmarkPass(Parcel in)
    {
        m_name = in.readString();
        m_latitude = in.readString();
        m_longitude = in.readString();
        m_elevation = in.readFloat();
    }

    public String getName(){return m_name;}
    public String getLatitiude(){return m_latitude;}
    public String getLongitude(){return m_longitude;}
    public float getElevation(){return  m_elevation;}

    public void setName(String name){m_name = name;}
    public void setLatitiude(String lat){m_latitude = lat;}
    public void setLongitude(String longitude){m_longitude = longitude;}
    public void setElevation(float elev){m_elevation = elev;}

    public int describeContents()
    {return 0;}

    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(m_name);
        out.writeString(m_latitude);
        out.writeString(m_longitude);
        out.writeFloat(m_elevation);
    }

    public static final Creator<LocalLandmarkPass> CREATOR = new Creator<LocalLandmarkPass>()
    {
        public LocalLandmarkPass createFromParcel(Parcel in)
        {
            return new LocalLandmarkPass(in);
        }

        public LocalLandmarkPass[] newArray(int size)
        {
            return new LocalLandmarkPass[size];
        }
    };
}

