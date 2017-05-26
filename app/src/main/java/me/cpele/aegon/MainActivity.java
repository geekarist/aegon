package me.cpele.aegon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class MainActivity extends FragmentActivity implements TimerFragment.Listener {

    private static final int NOTIFICATION_ID = 0;
    public static final String KEY_TIME_OF_ARRIVAL = "TIME_OF_ARRIVAL";
    public static final String KEY_START_TIME = "START_TIME";
    private static final long HALF_SEC = 500;

    /**
     * Absolute time OF arrival: this is different from the time TO arrival
     **/
    private long mTimeOfArrival;
    private long mStartTime;

    private Timer mStopwatch;
    private NotificationManager mNotificationManager;
    private boolean mBackground = true;
    private TimerFragment mTimerFragment;
    private TextView mStopwatchTextView;

    // region Android lifecycle

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mNotificationManager = getSystemService(NotificationManager.class);

        if (savedInstanceState != null) {
            restoreStateFrom(savedInstanceState);
            startTimer();
        }
        else mTimerFragment.showTimePicker();

        mStopwatchTextView = (TextView) findViewById(R.id.main_tv_stopwatch);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        mBackground = false;
        cancelNotification();
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveStateTo(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        mBackground = true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        cancelNotification();
        mTimerFragment.cancel();
        if (mStopwatch != null) mStopwatch.cancel();
        super.onDestroy();
    }

    // endregion

    // region TimerFragment.Listener

    @Override
    public void over() {
        mStopwatch = new Timer();
        mStopwatch.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(() -> updateStopwatchView());
            }
        }, 0, HALF_SEC);
    }

    @Override
    public void cancel() {
        if (mStopwatch != null) mStopwatch.cancel();
    }

    @Override
    public void makeNotification(String timeStr) {

        Intent intent = new Intent(this, getClass());
        Notification notification = new Notification.Builder(this)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setSmallIcon(R.drawable.ic_hourglass_empty_black_24dp)
                .setContentTitle(getString(R.string.main_notification_text))
                .setContentText(getString(R.string.main_notification_sub_text))
                .setSubText(timeStr)
                .build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    // endregion

    private void startTimer() {

        mTimerFragment = TimerFragment.newInstance(mStartTime, mTimeOfArrival);
        mTimerFragment.start();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_fr_timer_container, mTimerFragment)
                .commit();
    }

    private void saveStateTo(@NonNull Bundle outState) {
        outState.putLong(KEY_TIME_OF_ARRIVAL, mTimeOfArrival);
        outState.putLong(KEY_START_TIME, mStartTime);
    }

    private void restoreStateFrom(@NonNull Bundle savedInstanceState) {
        mTimeOfArrival = savedInstanceState.getLong(KEY_TIME_OF_ARRIVAL);
        mStartTime = savedInstanceState.getLong(KEY_START_TIME);
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
        String timeStr = getString(R.string.main_time_vs_total, hour, min, sec, totalHour, totalMin, totalSec);
        mStopwatchTextView.setText(timeStr);
        mStopwatchTextView.setTextColor(getResources().getColor(R.color.bpDarker_red, null));
        if (mBackground) makeNotification(timeStr);
    }

    private void cancelNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}
