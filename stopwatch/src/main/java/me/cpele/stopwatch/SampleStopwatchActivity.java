package me.cpele.stopwatch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.ViewFlipper;

public class SampleStopwatchActivity extends FragmentActivity {

    private NotificationManager mNotificationManager;

    private static final int NOTIFICATION_ID = 0;
    private StopwatchFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sample_stopwatch);

        mNotificationManager = getSystemService(NotificationManager.class);

        mFragment = findOrCreateFragment();

        mFragment.setOnTickListener((background, status) -> {
            if (background) makeNotification(status);
        });

        setupFlipping();
    }

    private void setupFlipping() {

        ViewFlipper flipper = findViewById(R.id.test_stopwatch_view_flipper);

        findViewById(R.id.test_stopwatch_bt_setup).setOnClickListener((view) -> {
            mFragment.setup();
            flipper.showNext();
        });

        findViewById(R.id.test_stopwatch_bt_teardown).setOnClickListener((view) -> {
            mFragment.tearDown();
            flipper.showNext();
        });
    }

    private StopwatchFragment findOrCreateFragment() {

        StopwatchFragment fragment = (StopwatchFragment) getSupportFragmentManager()
                .findFragmentById(R.id.test_stopwatch_fragment);

        if (fragment == null) {

            fragment = StopwatchFragment.newInstance(
                    System.currentTimeMillis() - 60_000,
                    System.currentTimeMillis() - 10_000);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.test_stopwatch_fragment, fragment)
                    .commit();
        }
        return fragment;
    }

    @Override
    protected void onStart() {
        super.onStart();

        cancelNotification();
    }

    @Override
    protected void onDestroy() {
        cancelNotification();
        super.onDestroy();
    }

    private void makeNotification(String timeStr) {

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

    private void cancelNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}
