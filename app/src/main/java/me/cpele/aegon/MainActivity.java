package me.cpele.aegon;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder;

import java.util.Timer;
import java.util.TimerTask;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MainActivity extends FragmentActivity {

    private static final long HALF_SEC = 500;

    private static final String KEY_RUNNING = "KEY_RUNNING";
    private static final String KEY_ETA = "KEY_ETA";

    private TextView mTimeTextView;
    private CountDownTimer mTimer;
    /**
     * Absolute time OF arrival: this is different from the time TO arrival
     **/
    private long mTimeOfArrival;
    private boolean mRunning;
    private Timer mStopwatch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTimeTextView = (TextView) findViewById(R.id.main_tv_time);

        if (savedInstanceState != null) {
            mTimeOfArrival = savedInstanceState.getLong(KEY_ETA, 0);
            mRunning = savedInstanceState.getBoolean(KEY_RUNNING, false);
        }

        updateEtaView();
        if (mRunning) startTimer();

        findViewById(R.id.main_tv_time).setOnClickListener(
                v ->
                        new HmsPickerBuilder().addHmsPickerDialogHandler(
                                (reference, isNegative, hours, minutes, seconds) -> {
                                    if (mTimer != null) mTimer.cancel();
                                    if (mStopwatch != null) mStopwatch.cancel();
                                    mTimeOfArrival = 1000 + System.currentTimeMillis()
                                            + HOURS.toMillis(hours)
                                            + MINUTES.toMillis(minutes)
                                            + SECONDS.toMillis(seconds);
                                    startTimer();
                                })
                                .setFragmentManager(getSupportFragmentManager())
                                .setStyleResId(R.style.BetterPickersDialogFragment)
                                .show());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(KEY_ETA, mTimeOfArrival);
        outState.putBoolean(KEY_RUNNING, mRunning);
    }

    private void startTimer() {

        long timeToArrival = mTimeOfArrival - System.currentTimeMillis();

        mRunning = true;
        mTimer = new CountDownTimer(timeToArrival, HALF_SEC) {

            @Override
            public void onTick(long millisUntilFinished) {
                updateEtaView();
            }

            @Override
            public void onFinish() {
                switchToStopWatch();
                if (mTimer != null) mTimer.cancel();
            }
        }.start();
    }

    private void switchToStopWatch() {
        mStopwatch = new Timer();
        mStopwatch.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(() -> updateStopwatchView());
            }
        }, 0, HALF_SEC);
    }

    private void updateStopwatchView() {
        long elapsedTime = System.currentTimeMillis() - mTimeOfArrival;
        long hour = MILLISECONDS.toHours(elapsedTime);
        long min = MILLISECONDS.toMinutes(elapsedTime) - HOURS.toMinutes(hour);
        long sec = MILLISECONDS.toSeconds(elapsedTime) - MINUTES.toSeconds(min) - HOURS.toSeconds(hour);
        mTimeTextView.setText(getString(R.string.main_time, hour, min, sec));
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
