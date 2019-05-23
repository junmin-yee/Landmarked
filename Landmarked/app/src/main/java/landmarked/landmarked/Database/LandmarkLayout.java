package landmarked.landmarked.Database;

import android.content.Context;
import android.widget.LinearLayout;

public class LandmarkLayout extends LinearLayout //Needs to be updated to take LocalLandmarks instead
{
    private LocalLandmarkPass m_landmark;

    // Sets up the layout for a landmark
    public LandmarkLayout(Context c, String name, String lat, String longitude, float elev)
    {
        super(c);
        m_landmark = new LocalLandmarkPass(name, lat, longitude, elev);
    }

    public LandmarkLayout(Context c, LocalLandmarkPass landmarkPass)
    {
        super(c);
        m_landmark = landmarkPass;
    }

    public LocalLandmarkPass GetLandmark(){return m_landmark;}
}

class CustomLandmarkLayout extends LinearLayout
{
    private CustomLandmarkPass m_custom;

    public CustomLandmarkLayout(Context c, String name, String lat, String longitude, float elev, String notes)
    {
        super(c);
        m_custom = new CustomLandmarkPass(new CustomLocalLandmark(name, lat, longitude, elev, notes, null));
    }

    public CustomLandmarkPass GetLandmark(){return m_custom;}
}