package landmarked.landmarked.DataManipulation;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import landmarked.landmarked.Database.AppDatabase;
import landmarked.landmarked.Database.AzureConnectionClass;
import landmarked.landmarked.Database.CustomLocalLandmark;
import landmarked.landmarked.Database.CustomLocalLandmarkAccessorMethods;
import landmarked.landmarked.Database.CustomLocalLandmarkAccessorMethods;
import landmarked.landmarked.LandmarkedMain;
import landmarked.landmarked.R;

public class addCustomLandmark extends AppCompatActivity
{
    private static final int inputType = 12290; //signed and decimal code
    private static final int inputEmpty = 131073;
    SensorData mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_custom_landmark);

        TextView temp = findViewById(R.id.landmarkName);
        int inputEnd = temp.getInputType();

        int rest = inputEnd;

        mData = LandmarkedMain.getInstance().mSensorData;
    }

    public void onCheckClicked(View v)
    {
        TextView temp;
        boolean currResult = ((CheckBox)v).isChecked();
        switch (v.getId())
        {
            case R.id.currLocationBox:
                if(currResult)
                {
                    Location l = mData.getCurrentLocation();
                    temp = findViewById(R.id.latitudeEntry);
                    if(l != null)
                    {
                        temp.setText(Double.toString(l.getLatitude()));
                    }
                    else
                    {
                        temp.setText("0");
                    }
                    temp.setFocusable(false);
                    temp = findViewById(R.id.longitudeEntry);
                    if(l != null)
                    {
                        temp.setText(Double.toString(l.getLongitude()));
                    }
                    else
                    {
                        temp.setText("0");
                    }
                    temp.setFocusable(false);

                }
                else
                {
                    temp = findViewById(R.id.latitudeEntry);
                    temp.setText("");
                    temp.setFocusableInTouchMode(true);
                    temp = findViewById(R.id.longitudeEntry);
                    temp.setText("");
                    temp.setFocusableInTouchMode(true);
                }
                break;
            case R.id.elevationBox:
                if(currResult)
                {
                    Location l = mData.getCurrentLocation();
                    temp = findViewById(R.id.elevationEntry);
                    if(l != null)
                    {
                        temp.setText(Double.toString(l.getAltitude()));
                    }
                    else
                    {
                        temp.setText("0");
                    }
                    temp.setFocusable(false);
                }
                else
                {
                    temp = findViewById(R.id.elevationEntry);
                    temp.setText("");
                    temp.setFocusableInTouchMode(true);
                }
                break;
        }
    }

    public void onSaveCustom(View v)
    {
        try
        {
            float rangeVal;
            EditText temp = findViewById(R.id.landmarkName);
            String name = temp.getText().toString();
            if (name.length() == 0)
            {
                throw new Exception("No name was entered");
            }
            else if(name.length() > 255)
            {
                throw new Exception("Name can only contain 255 characters at most.\nCurrent amount: " + name.length());
            }


            temp = findViewById(R.id.latitudeEntry);
            String lat = temp.getText().toString();
            if(lat.length() == 0)
            {
                throw new Exception("No latitude was entered");
            }
            else if(lat.length() > 20)
            {
                lat = lat.substring(0,20);
            }
            else if(lat.equals("-"))
            {
                throw new Exception("No value given to be negative");
            }
            rangeVal = Float.valueOf(lat);
            if (rangeVal > 90 || rangeVal < -90)
            {
                throw new Exception("Latitude is not between -90 and 90");
            }

            temp = findViewById(R.id.longitudeEntry);
            String lon = temp.getText().toString();
            if(lon.length() == 0)
            {
                throw new Exception("No longitude was entered");
            }
            else if(lon.length() > 20)
            {
                lon = lon.substring(0,20);
            }
            else if(lon.equals("-"))
            {
                throw new Exception("No value given to be negative");
            }
            rangeVal = Float.valueOf(lon);
            if (rangeVal > 180 || rangeVal < -180)
            {
                throw new Exception("Longitude is not between -180 and 180");
            }

            temp = findViewById(R.id.elevationEntry);
            String eleVal = temp.getText().toString();
            if(eleVal.length() == 0)
            {
                throw new Exception("No elevation was entered");
            }
            else if(lat.equals("-"))
            {
                throw new Exception("No value given to be negative");
            }
            float elev = Float.parseFloat(eleVal);
            if (elev > 8848 || elev < -420)
            {
                throw new Exception("Elevation cannot reasonably be within these bounds");
            }
            //Date currDate = Calendar.getInstance().getTime();
            temp = findViewById(R.id.notesEntry);
            String note = temp.getText().toString();
            if(note.length() > 1023)
            {
                throw new Exception("Notes can only contain 1023 characters at most.\nCurrent amount: " + note.length());
            }

            //CustomLocalLandmark addition = new CustomLocalLandmark(name, lat, lon, elev, "",currDate);

            //CustomLocalLandmarkAccessorMethods_Impl add = new CustomLocalLandmarkAccessorMethods_Impl;
        /*CustomLocalLandmarkAccessorMethods_Impl add = new CustomLocalLandmarkAccessorMethods_Impl(AppDatabase.getM_DB_instance(this));
        add.insertCustomLandmarkStructure(addition);*/

            //LandmarkedMain main =  LandmarkedMain.getInstance();
            //main.insertCustomLandmarkStructure(addition);

            AzureConnectionClass thePowerOfTheAzure = AzureConnectionClass.getAzureInstance();
            //thePowerOfTheAzure.Connect();
            thePowerOfTheAzure.InsertCustomLandmark(name, lat, lon, elev, "");
            thePowerOfTheAzure.insertNote(note);
            //thePowerOfTheAzure.Disconnect();

            finish();
        }
        catch(Exception e)
        {
            Context c = getApplicationContext();
            Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onCancelCustom(View v)
    {
        finish();
    }
}
