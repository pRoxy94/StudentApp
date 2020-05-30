package it.uniba.di.sms.laricchia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AlarmActivity extends AppCompatActivity {

    private Button btnSetAlarm;
    private EditText editHours, editMin;
    private Toolbar tlbAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        editHours = (EditText) findViewById(R.id.editHour);
        editMin = (EditText) findViewById(R.id.editMin);
        btnSetAlarm = (Button) findViewById(R.id.btnSetAlarm);

        tlbAlarm = (Toolbar) findViewById(R.id.tlbAlarm);
        tlbAlarm.setTitle(R.string.strAlarm);
        tlbAlarm.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(tlbAlarm);

        tlbAlarm.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Button Set Alarm
        btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Recupero dati dalle edit text
                int hours = Integer.parseInt(editHours.getText().toString());
                int minutes = Integer.parseInt(editMin.getText().toString());
                //Create alarm
                String setMessageAlarm = getString(R.string.strTxtAlarm);
                createAlarm(setMessageAlarm, hours, minutes);
               //show hours and minutes with 2 digits (Example: 23:02 instead of 23:2)
                String hours2digits = String.format("%02d", hours);
                String minutes2digits = String.format("%02d", minutes);
                //Show toast message
                String text = getString(R.string.setAlarm,hours2digits, minutes2digits);
                Toast.makeText(AlarmActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    //Create Alarm
    public void createAlarm(String msg, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
            .putExtra(AlarmClock.EXTRA_MESSAGE, msg)
            .putExtra(AlarmClock.EXTRA_HOUR, hour)
            .putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        if(intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }
}
