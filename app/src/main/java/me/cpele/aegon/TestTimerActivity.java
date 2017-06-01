package me.cpele.aegon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

public class TestTimerActivity extends FragmentActivity {

    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_timer);

        mNotificationManager = getSystemService(NotificationManager.class);

        FragmentManager manager = getSupportFragmentManager();
        final TimerFragment timerFragment = (TimerFragment) manager.findFragmentById(R.id.test_timer_fr_timer);

        cancelNotification();

        Context context = getApplicationContext();
        timerFragment
                .setOnTickListener((bg, status) -> {
                    if (bg) makeNotification(status);
                    else cancelNotification();
                })
                .setOnEndListener(() ->
                        Toast.makeText(context, "It's over", Toast.LENGTH_SHORT).show());

        findViewById(R.id.test_timer_bt_cancel).setOnClickListener(v -> timerFragment.cancel());
    }

    private void cancelNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
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
}
