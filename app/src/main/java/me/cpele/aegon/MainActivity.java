package me.cpele.aegon;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class MainActivity extends Activity {

    private static final long ONE_SEC = 1000;

    private static final String KEY_RUNNING = "KEY_RUNNING";
    private static final String KEY_ETA = "KEY_ETA";

    private TextView mTimeTextView;
    private CountDownTimer mTimer;
    /** Absolute time OF arrival: this is different from the time TO arrival **/
    private long mTimeOfArrival;
    private boolean mRunning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTimeTextView = (TextView) findViewById(R.id.main_tv_time);

        if (savedInstanceState != null) {
            mTimeOfArrival = savedInstanceState.getInt(KEY_ETA, 0);
            mRunning = savedInstanceState.getBoolean(KEY_RUNNING, false);
        }

        updateEtaView();
        if (mRunning) startTimer();

        findViewById(R.id.main_tv_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (mTimer != null) mTimer.cancel();
                        mTimeOfArrival = System.currentTimeMillis() + HOURS.toMillis(hourOfDay) + MINUTES.toMillis(minute);
                        startTimer();
                    }
                }, 0, 0, true).show();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(KEY_ETA, mTimeOfArrival);
        outState.putBoolean(KEY_RUNNING, mRunning);
    }

    private void startTimer() {

        long timeToArrival = System.currentTimeMillis() - mTimeOfArrival;

        mRunning = true;
        mTimer = new CountDownTimer(timeToArrival, ONE_SEC) {

            @Override
            public void onTick(long millisUntilFinished) {
                updateEtaView();
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    private void updateEtaView() {

        long hour = 0;
        long min = 0;
        long sec = 0;
        if (mTimeOfArrival != 0) {
            long timeToArrival = mTimeOfArrival - System.currentTimeMillis();
            hour = MILLISECONDS.toHours(timeToArrival);
            min = MILLISECONDS.toMinutes(timeToArrival) - HOURS.toMinutes(hour);
            sec = MILLISECONDS.toSeconds(timeToArrival) - MINUTES.toSeconds(min) - HOURS.toSeconds(hour);
        }

        mTimeTextView.setText(getString(R.string.main_time, hour, min, sec));
    }
}
