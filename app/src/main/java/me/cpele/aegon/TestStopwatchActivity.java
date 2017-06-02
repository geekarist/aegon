package me.cpele.aegon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

public class TestStopwatchActivity extends FragmentActivity {

    private NotificationManager mNotificationManager;
    private boolean mBackground = true;

    private static final int NOTIFICATION_ID = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_stopwatch);
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
