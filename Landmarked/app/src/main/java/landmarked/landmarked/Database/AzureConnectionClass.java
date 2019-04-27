package landmarked.landmarked.Database;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.Date;

import landmarked.landmarked.LandmarkedMain;


public class AzureConnectionClass {
    private static Connection mConnection;
    private static AzureConnectionClass m_instance;
    private static String m_username;
    static LandmarkedMain m_main;
    public AzureConnectionClass()
    {
        m_main = LandmarkedMain.getInstance();
        m_username = m_main.get_m_username();
        mConnection = null;
        m_instance = this;
    }

    public static Connection ConnectionGetInstance()
    {
        return mConnection;
    }

    public static AzureConnectionClass getAzureInstance()
    {
        return m_instance;
    }
    public Connection Connect()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mConnection = null;

        String ConnectionURL = null;
        // Connect to database

        try
        {

            Class.forName("net.sourceforge.jtds.jdbc.Driver");//Load the JDBC driver, this was one of the things that was missing. Without loading the driver, Android does not know where to find it.
            ConnectionURL = String.format("jdbc:jtds:sqlserver://landmarks.database.windows.net:1433;databaseName=Landmarked;user=landmarked.admin@landmarks;password=ReallyIntricatePassword18.;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;");
            mConnection = DriverManager.getConnection(ConnectionURL);//connect
        }
        catch (SQLException se)
        {
            Log.e("Error 1","Failed to connect to SQL Database");
        }
        catch (Exception e)
        {
            Log.e("Error 2","Something went wrong");
        }
        return mConnection;
    }
    public void Insert(String name, String latitude, String longitude, float elevation, String wiki)
    {
        try
        {

        //    String query = "INSERT INTO dbo.Landmark (LandmarkName, LandmarkLat, LandmarkLong, LandmarkEle, LandmarkWikiInfo) VALUES ('"+name+"', '"+latitude+"', '"+longitude+"', '"+0.00+"', '"+wiki+"')";
            Statement m_query = mConnection.createStatement();
        //    m_query.executeUpdate(query);
         String   query = "INSERT Into dbo.UserLandmark VALUES (UserID, LandmarkID)"
            +  "SELECT UserID, LandmarkID"
            +   "FROM User, Landmark"
            +   "WHERE User.name = '"+m_username+"', Landmark.name = '"+name+"'";
            m_query.executeUpdate(query);

        }
        catch (SQLException se)
        {
            Log.e("Error 1","Failed to connect to SQL Database");
        }
        catch (Exception e)
        {
            Log.e("Error 2","Something went wrong");
        }
    }


    public ArrayList<LocalLandmark> getLandmarks()
    {
        ArrayList lst = new ArrayList();
        try
        {
            String query = "SELECT * FROM dbo.Landmark";
            Statement m_query = mConnection.createStatement();
            ResultSet Landmarks = m_query.executeQuery(query);
            while (Landmarks.next())
            {
                String name = Landmarks.getString("LandmarkName");
                String latitude = Landmarks.getString("LandmarkLat");
                String longitude = Landmarks.getString("LandmarkLong");
                float elevation = Landmarks.getFloat("LandmarkEle");
                String wiki = Landmarks.getString("LandmarkWikiInfo");
                LocalLandmark temp = new LocalLandmark(name, latitude,longitude,elevation,wiki,new Date());
                lst.add(temp);
            }

        }
        catch (SQLException se)
        {
            Log.e("Error 1","Failed to connect to SQL Database");
        }
        catch (Exception e)
        {
            Log.e("Error 2","Something went wrong");
        }



        return lst;
    }

    public ArrayList<LocalLandmark> getLandmarksByEmail(String Email)
    {
        ArrayList lst = new ArrayList();
        try
        {
            String query = "SELECT * " +
                    "FROM dbo.Landmark " +
                    "JOIN dbo.UserLandmark ON dbo.Landmark.LandmarkID = UserLandmark.LandmarkID " +
                    "JOIN AppUser ON AppUser.UserID=UserLandmark.UserID " +
                    "WHERE dbo.AppUser.UserID = dbo.UserLandmark.UserID AND dbo.AppUser.Email = '"+Email+"';";
            Statement m_query = mConnection.createStatement();
            ResultSet Landmarks = m_query.executeQuery(query);
            while (Landmarks.next())
            {
                String name = Landmarks.getString("LandmarkName");
                String latitude = Landmarks.getString("LandmarkLat");
                String longitude = Landmarks.getString("LandmarkLong");
                float elevation = Landmarks.getFloat("LandmarkEle");
                String wiki = Landmarks.getString("LandmarkWikiInfo");
                LocalLandmark temp = new LocalLandmark(name, latitude,longitude,elevation,wiki,new Date());
                lst.add(temp);
            }

        }
        catch (SQLException se)
        {
            Log.e("Error 1","Failed to connect to SQL Database");
        }
        catch (Exception e)
        {
            Log.e("Error 2","Something went wrong");
        }



        return lst;
    }
    public void Disconnect()
    {
        try {
            mConnection.close();
        }
        catch (SQLException se)
        {
            Log.e("Error 1","Connection not open");
        }
    }
}
