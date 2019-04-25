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
import landmarked.landmarked.Database.CustomLocalLandmark;
import landmarked.landmarked.Database.CustomLocalLandmarkAccessorMethods;
import landmarked.landmarked.Database.CustomLocalLandmarkAccessorMethods_Impl;
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
        EditText temp = findViewById(R.id.landmarkName);
        String name = temp.getText().toString();
        temp = findViewById(R.id.latitudeEntry);
        String lat = temp.getText().toString();
        temp = findViewById(R.id.longitudeEntry);
        String lon = temp.getText().toString();
        temp = findViewById(R.id.elevationEntry);
        float elev = Float.parseFloat(temp.getText().toString());
        Date currDate = Calendar.getInstance().getTime();

        CustomLocalLandmark addition = new CustomLocalLandmark(name, lat, lon, elev, "",currDate);

        //CustomLocalLandmarkAccessorMethods_Impl add = new CustomLocalLandmarkAccessorMethods_Impl;
        CustomLocalLandmarkAccessorMethods_Impl add = new CustomLocalLandmarkAccessorMethods_Impl(AppDatabase.getM_DB_instance(this));
        add.insertCustomLandmarkStructure(addition);

        finish();
    }
}
