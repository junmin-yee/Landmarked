package landmarked.landmarked.Database;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AzureConnectionClass {
    private static Connection mConnection;

    public AzureConnectionClass()
    {
        mConnection = null;
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
