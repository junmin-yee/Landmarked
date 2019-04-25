package landmarked.landmarked.DataManipulation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import landmarked.landmarked.R;

public class addCustomLandmark extends AppCompatActivity
{
    private static final int inputType = 12290; //signed and decimal code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_landmark);
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
                    temp.setInputType(InputType.TYPE_NULL);
                    temp.setFocusable(false);
                    temp = findViewById(R.id.longitudeEntry);
                    temp.setInputType(InputType.TYPE_NULL);
                    temp.setFocusable(false);
                }
                else
                {
                    temp = findViewById(R.id.latitudeEntry);
                    temp.setInputType(inputType);
                    temp.setFocusable(true);
                    temp = findViewById(R.id.longitudeEntry);
                    temp.setInputType(inputType);
                    temp.setFocusable(true);
                }
        }
    }
}
