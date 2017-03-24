package me.cpele.aegon;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    public static final long ONE_SEC = 1000;

    private long mInitialCountDown;
    private TextView mTimeTextView;
    private CountDownTimer mTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTimeTextView = (TextView) findViewById(R.id.main_tv_time);

        initTime(0, 0);

        findViewById(R.id.main_tv_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (mTimer != null) mTimer.cancel();
                        initTime(hourOfDay, minute);
                        startTimer();
                    }
                }, 0, 0, true).show();
            }
        });
    }

    private void startTimer() {

        mTimer = new CountDownTimer(mInitialCountDown, ONE_SEC) {

            @Override
            public void onTick(long millisUntilFinished) {
                updateEtaView(millisUntilFinished);
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    private void updateEtaView(long eta) {

        long hour = TimeUnit.MILLISECONDS.toHours(eta);
        long min = TimeUnit.MILLISECONDS.toMinutes(eta) - TimeUnit.HOURS.toMinutes(hour);
        long sec = TimeUnit.MILLISECONDS.toSeconds(eta) - TimeUnit.MINUTES.toSeconds(min) - TimeUnit.HOURS.toSeconds(hour);

        mTimeTextView.setText(getString(R.string.main_time, hour, min, sec));
    }

    private void initTime(int hour, int min) {

        mInitialCountDown = TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(min);

        int sec = 0;
        mTimeTextView.setText(getString(R.string.main_time, hour, min, sec));
    }
}
