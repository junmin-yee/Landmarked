package landmarked.landmarked.Database;

import android.os.Parcel;

public class CustomLandmarkPass extends LocalLandmarkPass
{
    private String m_notes;

    public CustomLandmarkPass(CustomLocalLandmark c)
    {
        super(c.getName(), c.getLatitude(), c.getLongitude(), c.getElevation());
        m_notes = c.getWiki();
    }

    private CustomLandmarkPass(Parcel in)
    {
        setName(in.readString());
        setLatitiude(in.readString());
        setLongitude(in.readString());
        setElevation(in.readFloat());
        m_notes = in.readString();
    }

    public String getNotes(){ return m_notes; }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(m_notes);
    }

    public static final Creator<CustomLandmarkPass> CREATOR = new Creator<CustomLandmarkPass>()
    {
        public CustomLandmarkPass createFromParcel(Parcel in)
        {
            return new CustomLandmarkPass(in);
        }

        public CustomLandmarkPass[] newArray(int size)
        {
            return new CustomLandmarkPass[size];
        }
    };
}
