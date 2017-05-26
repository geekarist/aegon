package me.cpele.aegon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class TestTimerActivity extends FragmentActivity implements TimerFragment.Listener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_timer);

        TimerFragment timerFragment = (TimerFragment) getSupportFragmentManager().findFragmentById(R.id.test_timer_fr_timer);
        timerFragment.showTimePicker();
    }

    @Override
    public void over() {
        Toast.makeText(this, "It's over", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancel() {
        Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void makeNotification(String timeStr) {
        Toast.makeText(this, "Make a notification", Toast.LENGTH_SHORT).show();
    }
}
