package landmarked.landmarked.Database;

import android.content.Context;
import android.widget.LinearLayout;

public class LandmarkLayout extends LinearLayout //Needs to be updated to take LocalLandmarks instead
{
    private LocalLandmarkPass m_landmark;

    public LandmarkLayout(Context c, String name, String lat, String longitude, float elev)
    {
        super(c);
        m_landmark = new LocalLandmarkPass(name, lat, longitude, elev);
    }

    public LocalLandmarkPass GetLandmark(){return m_landmark;}
}
