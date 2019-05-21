package landmarked.landmarked.Database;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.Calendar;
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

    // Get an instance of the connection
    public static Connection ConnectionGetInstance()
    {
        return mConnection;
    }

    // Get an instance of the azure database
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
            //Load the JDBC driver, this was one of the things that was missing. Without loading the driver, Android does not know where to find it.
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
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

    // Inserts a user into the database
    public void InsertUsername(String email, String fname, String lname)
    {
        try
        {



           String query = "IF NOT EXISTS ( SELECT 1 FROM AppUser WHERE Email = '"+email+"')"
                + "BEGIN "
                +   "INSERT INTO AppUser (FirstName,LastName, Email) Values ('"+fname+"', '"+lname+"', '"+email+"')"
                + "END";

            Statement m_query = mConnection.createStatement();
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

    // Inserts a landmark into the database
    public void Insert(String name, String latitude, String longitude, float elevation, String wiki)
    {
        try
        {
            m_username = m_main.get_m_username();
            String query = "INSERT INTO dbo.Landmark (LandmarkName, LandmarkLat, LandmarkLong, LandmarkEle, LandmarkWikiInfo) VALUES ('"+name+"', '"+latitude+"', '"+longitude+"', "+0.00+", '"+wiki+"')";
            Statement m_query = mConnection.createStatement();
            m_query.executeUpdate(query);
            query = "INSERT Into dbo.UserLandmark (UserID, LandmarkID) "
            +  " SELECT UserID, LandmarkID "
            +   " FROM dbo.AppUser, dbo.Landmark "
            +   " WHERE AppUser.Email = '"+m_username+"' AND Landmark.LandmarkName = '"+name+"'";
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

    public void InsertCustomLandmark(String name, String latitude, String longitude, float elevation, String notes)
    {
        try
        {
            m_username = m_main.get_m_username();
            PreparedStatement stmt = mConnection.prepareStatement("INSERT INTO dbo.CustomLandmark (CustomLandmarkName, CustomLandmarkLat, CustomLandmarkLong, CustomLandmarkEle, DateSaved, UserID) "
                    + " VALUES "
                    + " (?, ?, ?, ?, ?, "
                    + " (SELECT UserID "+" FROM dbo.AppUser WHERE Email = '"+m_username+"'))");
            Date currentDate = Calendar.getInstance().getTime();
            java.sql.Timestamp currDate = new Timestamp(System.currentTimeMillis());
            stmt.setString(1, name);
            stmt.setString(2, latitude);
            stmt.setString(3,longitude);
            stmt.setFloat(4, elevation);
            stmt.setTimestamp(5, currDate);
            stmt.executeQuery();

            //insertNote(notes);
            /*String query;
            query = "INSERT INTO dbo.CustomLandmark (CustomLandmarkName, CustomLandmarkLat, CustomLandmarkLong, CustomLandmarkEle, DateSaved, UserID) "
            + " VALUES "
            + " (?, ?, ?, ?, ?, "
            + " (SELECT UserID "+" FROM dbo.AppUser WHERE Email = '"+m_username+"'))";
            Statement m_query = mConnection.createStatement();
            m_query.executeUpdate(query);*/
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

    public void insertNote(String note)
    {
        int id = getUserID();
        if(id > 0)
        {
            try
            {
                PreparedStatement stmt = mConnection.prepareStatement("SELECT TOP 1 CustomLandmarkID FROM CustomLandmark WHERE UserID = ? ORDER BY CustomLandmarkID DESC");
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                //int customID = rs.getInt("CustomLandmarkID");
                int customID = rs.getInt("CustomLandmarkID");
                //java.sql.Timestamp currDate = new Timestamp(System.currentTimeMillis());
                stmt = mConnection.prepareStatement("INSERT INTO Note (CustomLandmarkID, NoteTitle, NoteText, NoteLastEdited)"
                        + " VALUES(?, 'Title', ?, GETDATE()) ");
                stmt.setInt(1, customID);
                stmt.setString(2, note);
                stmt.executeQuery();
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
    }

    public int getUserID()
    {
        int ID = -1;
        try {
            m_username = m_main.get_m_username();
            String query = "SELECT UserID "
                    + "FROM dbo.AppUser "
                    + "WHERE AppUser.Email = '"+m_username+"'";
            Statement m_query = mConnection.createStatement();
            ResultSet  result = m_query.executeQuery(query);

                if(result.next())
                {
                    ID = (int) result.getInt("UserID");
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

        return ID;

    }
    // Gets a list of landmarks in the database
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

    // Query to find a list of landmarks by email
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

    // Disconnects from the database
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
