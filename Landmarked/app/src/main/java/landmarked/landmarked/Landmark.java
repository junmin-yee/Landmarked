package landmarked.landmarked;

public class Landmark {
    private String m_name;
    private String m_latitude;
    private String m_longitude;
    private float m_elevation;
    private String m_wiki;

    public Landmark()
    {
        m_name = "default value";
        m_latitude =  "default value";
        m_longitude = "default value";
        m_elevation = 0.0f;
        m_wiki = "default value";

    }
    public String getName()
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
        return m_wiki;
    }
    public void setName(String name)
    {
        m_name = name;
    }
    public void setLatitude(String latitude)
    {
        m_latitude = latitude;
    }
    public void setLongitude(String longitude)
    {
        m_longitude = longitude;
    }
    public void setElevation(float elevation)
    {
        m_elevation = elevation;
    }
    public void setWiki(String wiki)
    {
        m_wiki = wiki;
    }
}
