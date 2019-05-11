package landmarked.landmarked.DataManipulation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import landmarked.landmarked.Database.AppDatabase;
import landmarked.landmarked.Database.AzureConnectionClass;
import landmarked.landmarked.Database.CustomLocalLandmark;
import landmarked.landmarked.Database.CustomLocalLandmarkAccessorMethods;
import landmarked.landmarked.Database.CustomLocalLandmarkAccessorMethods_Impl;
import landmarked.landmarked.LandmarkedMain;
import landmarked.landmarked.R;

public class addCustomLandmark extends AppCompatActivity
{
    private static final int inputType = 12290; //signed and decimal code
    private static final int inputEmpty = 131073;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_custom_landmark);

        TextView temp = findViewById(R.id.landmarkName);
        int inputEnd = temp.getInputType();

        int rest = inputEnd;
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
                    temp = findViewById(R.id.latitudeEntry);
                    temp.setFocusable(false);
                    temp = findViewById(R.id.longitudeEntry);
                    temp.setFocusable(false);
                }
                else
                {
                    temp = findViewById(R.id.latitudeEntry);
                    temp.setFocusableInTouchMode(true);
                    temp = findViewById(R.id.longitudeEntry);
                    temp.setFocusableInTouchMode(true);
                }
                break;
            case R.id.elevationBox:
                if(currResult)
                {
                    temp = findViewById(R.id.elevationEntry);
                    temp.setText("");
                    temp.setFocusable(false);
                }
                else
                {
                    temp = findViewById(R.id.elevationEntry);
                    temp.setFocusableInTouchMode(true);
                }
                break;
        }
    }

    public void onSaveCustom(View v)
    {
        int errorVar = 0;
        float rangeVal;
        EditText temp = findViewById(R.id.landmarkName);
        String name = temp.getText().toString();
        if(name.length() == 0)
        {
            errorVar += 1;
        }
        temp = findViewById(R.id.latitudeEntry);
        String lat = temp.getText().toString();
        rangeVal = Float.valueOf(lat);
        if(rangeVal > 90 || rangeVal < -90)
        {
            errorVar += 2;
        }
        temp = findViewById(R.id.longitudeEntry);
        String lon = temp.getText().toString();
        rangeVal = Float.valueOf(lon);
        if(rangeVal > 180 || rangeVal < -180)
        {
            errorVar += 4;
        }
        temp = findViewById(R.id.elevationEntry);
        float elev = Float.parseFloat(temp.getText().toString());
        if(elev > 8848 || elev < -420)
        {
            errorVar += 16;
        }
        Date currDate = Calendar.getInstance().getTime();

        //CustomLocalLandmark addition = new CustomLocalLandmark(name, lat, lon, elev, "",currDate);

        //CustomLocalLandmarkAccessorMethods_Impl add = new CustomLocalLandmarkAccessorMethods_Impl;
        /*CustomLocalLandmarkAccessorMethods_Impl add = new CustomLocalLandmarkAccessorMethods_Impl(AppDatabase.getM_DB_instance(this));
        add.insertCustomLandmarkStructure(addition);*/

        //LandmarkedMain main =  LandmarkedMain.getInstance();
        //main.insertCustomLandmarkStructure(addition);

        AzureConnectionClass thePowerOfTheAzure = AzureConnectionClass.getAzureInstance();
        thePowerOfTheAzure.Connect();
        thePowerOfTheAzure.InsertCustomLandmark(name, lat, lon, elev, "");
        thePowerOfTheAzure.Disconnect();

        finish();
    }

    public void onCancelCustom(View v)
    {
        finish();
    }
}
