package me.cpele.aegon;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

public class TestTimerActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_timer);

        FragmentManager manager = getSupportFragmentManager();
        final TimerFragment timerFragment = (TimerFragment) manager.findFragmentById(R.id.test_timer_fr_timer);

        Context context = getApplicationContext();
        timerFragment.setOnTickListener(bg -> {
            if (bg) Toast.makeText(context, "Make a notification", Toast.LENGTH_SHORT).show();
        }).setOnEndListener(() -> Toast.makeText(context, "It's over", Toast.LENGTH_SHORT).show());

        findViewById(R.id.test_timer_bt_cancel).setOnClickListener(v -> timerFragment.cancel());
    }
}
