package me.cpele.aegon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
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
    private static final String KEY_START_TIME = "KEY_START_TIME";
    public static final int NOTIFICATION_ID = 0;

    private TextView mTimeTextView;
    private CountDownTimer mTimer;
    /**
     * Absolute time OF arrival: this is different from the time TO arrival
     **/
    private long mTimeOfArrival;
    private boolean mRunning;
    private Timer mStopwatch;
    private long mStartTime;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTimeTextView = (TextView) findViewById(R.id.main_tv_time);

        mNotificationManager = getSystemService(NotificationManager.class);

        if (savedInstanceState != null) {
            mStartTime = savedInstanceState.getLong(KEY_START_TIME, 0);
            mTimeOfArrival = savedInstanceState.getLong(KEY_ETA, 0);
            mRunning = savedInstanceState.getBoolean(KEY_RUNNING, false);
        }

        updateEtaView();
        if (mRunning) startTimer();

        findViewById(R.id.main_tv_time).setOnClickListener(
                v -> new HmsPickerBuilder().addHmsPickerDialogHandler(
                        (reference, isNegative, hours, minutes, seconds) -> {
                            if (mTimer != null) mTimer.cancel();
                            if (mStopwatch != null) mStopwatch.cancel();
                            mTimeOfArrival = 1000 + System.currentTimeMillis()
                                    + HOURS.toMillis(hours)
                                    + MINUTES.toMillis(minutes)
                                    + SECONDS.toMillis(seconds);
                            mStartTime = System.currentTimeMillis();
                            startTimer();
                        })
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment)
                        .show());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(KEY_ETA, mTimeOfArrival);
        outState.putBoolean(KEY_RUNNING, mRunning);
        outState.putLong(KEY_START_TIME, mStartTime);
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
        long elapsedTime = System.currentTimeMillis() - mStartTime;
        long hour = MILLISECONDS.toHours(elapsedTime);
        long min = MILLISECONDS.toMinutes(elapsedTime) - HOURS.toMinutes(hour);
        long sec = MILLISECONDS.toSeconds(elapsedTime) - MINUTES.toSeconds(min) - HOURS.toSeconds(hour);
        long total = mTimeOfArrival - mStartTime;
        long totalHour = MILLISECONDS.toHours(total);
        long totalMin = MILLISECONDS.toMinutes(total) - HOURS.toMinutes(totalHour);
        long totalSec = MILLISECONDS.toSeconds(total) - HOURS.toSeconds(totalHour) - MINUTES.toSeconds(totalMin);
        mTimeTextView.setText(getString(R.string.main_time_vs_total, hour, min, sec, totalHour, totalMin, totalSec));
        mTimeTextView.setTextColor(getResources().getColor(R.color.bpDarker_red, null));
    }

    private void updateEtaView() {

        long hour = 0;
        long min = 0;
        long sec = 0;

        long totalHour = 0;
        long totalMin = 0;
        long totalSec = 0;

        if (mTimeOfArrival != 0) {

            long timeToArrival = mTimeOfArrival - System.currentTimeMillis();
            hour = MILLISECONDS.toHours(timeToArrival);
            min = MILLISECONDS.toMinutes(timeToArrival) - HOURS.toMinutes(hour);
            sec = MILLISECONDS.toSeconds(timeToArrival) - MINUTES.toSeconds(min) - HOURS.toSeconds(hour);

            long total = mTimeOfArrival - mStartTime;
            totalHour = MILLISECONDS.toHours(total);
            totalMin = MILLISECONDS.toMinutes(total) - HOURS.toMinutes(totalHour);
            totalSec = MILLISECONDS.toSeconds(total) - HOURS.toSeconds(totalHour) - MINUTES.toSeconds(totalMin);
        }

        mTimeTextView.setText(getString(R.string.main_time_vs_total, hour, min, sec, totalHour, totalMin, totalSec));
        mTimeTextView.setTextColor(getResources().getColor(R.color.bpblack, null));

        makeNotification(System.currentTimeMillis() - mStartTime);
    }

    @Override
    protected void onPause() {
        makeNotification(System.currentTimeMillis() - mStartTime);
        super.onPause();
    }

    @Override
    protected void onResume() {
        cancelNotification();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        cancelNotification();
        if (mTimer != null) mTimer.cancel();
        if (mStopwatch != null) mStopwatch.cancel();
        super.onDestroy();
    }

    private void cancelNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private void makeNotification(long elapsed) {

        Intent intent = new Intent(this, getClass());
        Notification notification = new Notification.Builder(this)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setSmallIcon(R.drawable.ic_hourglass_empty_black_24dp)
                .setContentTitle(getString(R.string.main_notification_text))
                .setContentText(getString(R.string.main_notification_sub_text))
                .setSubText("" + toHMS(elapsed))
                .build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private String toHMS(long time) {

        long hour = MILLISECONDS.toHours(time);
        long min = MILLISECONDS.toMinutes(time) - HOURS.toMinutes(hour);
        long sec = MILLISECONDS.toSeconds(time) - HOURS.toSeconds(hour) - MINUTES.toSeconds(min);
        return getString(R.string.main_time_hms, hour, min, sec);
    }
}
