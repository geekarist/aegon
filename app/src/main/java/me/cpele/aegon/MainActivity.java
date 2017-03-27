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
import static java.util.concurrent.TimeUnit.SECONDS;

public class MainActivity extends Activity {

    private static final long ONE_SEC = 1000;

    private static final String KEY_INITIAL_HOUR = "KEY_INITIAL_HOUR";
    private static final String KEY_INITIAL_MIN = "KEY_INITIAL_MIN";
    private static final String KEY_INITIAL_SEC = "KEY_INITIAL_SEC";
    public static final String KEY_RUNNING = "KEY_RUNNING";

    private long mInitialCountDown;
    private TextView mTimeTextView;
    private CountDownTimer mTimer;
    private long mEta;
    private boolean mRunning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTimeTextView = (TextView) findViewById(R.id.main_tv_time);

        int initialHour = 0;
        int initialMin = 0;
        int initialSec = 0;
        if (savedInstanceState != null) {
            initialHour = savedInstanceState.getInt(KEY_INITIAL_HOUR, 0);
            initialMin = savedInstanceState.getInt(KEY_INITIAL_MIN, 0);
            initialSec = savedInstanceState.getInt(KEY_INITIAL_SEC, 0);
            mRunning = savedInstanceState.getBoolean(KEY_RUNNING, false);
        }

        initTime(initialHour, initialMin, initialSec);
        if (mRunning) startTimer();

        findViewById(R.id.main_tv_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (mTimer != null) mTimer.cancel();
                        initTime(hourOfDay, minute, 0);
                        startTimer();
                    }
                }, 0, 0, true).show();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int initialHour = (int) MILLISECONDS.toHours(mEta);
        int initialMin = (int) (MILLISECONDS.toMinutes(mEta) - HOURS.toMinutes(initialHour));
        int initialSec = (int) (MILLISECONDS.toSeconds(mEta) - MINUTES.toSeconds(initialMin) - HOURS.toSeconds(initialHour));

        outState.putInt(KEY_INITIAL_HOUR, initialHour);
        outState.putInt(KEY_INITIAL_MIN, initialMin);
        outState.putInt(KEY_INITIAL_SEC, initialSec);
        outState.putBoolean(KEY_RUNNING, mRunning);
    }

    private void startTimer() {

        mRunning = true;
        mTimer = new CountDownTimer(mInitialCountDown, ONE_SEC) {

            @Override
            public void onTick(long millisUntilFinished) {
                updateEtaView(millisUntilFinished);
                mEta = millisUntilFinished;
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    private void updateEtaView(long eta) {

        long hour = MILLISECONDS.toHours(eta);
        long min = MILLISECONDS.toMinutes(eta) - HOURS.toMinutes(hour);
        long sec = MILLISECONDS.toSeconds(eta) - MINUTES.toSeconds(min) - HOURS.toSeconds(hour);

        mTimeTextView.setText(getString(R.string.main_time, hour, min, sec));
    }

    private void initTime(int hour, int min, int sec) {

        mInitialCountDown = HOURS.toMillis(hour) + MINUTES.toMillis(min) + SECONDS.toMillis(sec);

        mTimeTextView.setText(getString(R.string.main_time, hour, min, sec));
    }
}
