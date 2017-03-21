package me.cpele.aegon;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initTime(0, 0);

        findViewById(R.id.main_tv_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        initTime(hourOfDay, minute);
                    }
                }, 0, 0, true).show();
            }
        });
    }

    private void initTime(int hour, int min) {
        TextView timeTextView = (TextView) findViewById(R.id.main_tv_time);

        int sec = 0;
        timeTextView.setText(getString(R.string.main_time, hour, min, sec));
    }
}
