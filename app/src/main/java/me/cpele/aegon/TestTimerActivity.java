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

        final TimerFragment timerFragment = findTimerFragment();
        findViewById(R.id.test_timer_bt_cancel).setOnClickListener(v -> timerFragment.cancel());
    }

    @Override
    public void onTimerEnd() {
        Toast.makeText(this, "It's over", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimerReset() {
        Toast.makeText(this, "Reset", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimerProgress(String timeStr) {
        Toast.makeText(this, "Make a notification", Toast.LENGTH_SHORT).show();
    }

    private TimerFragment findTimerFragment() {
        TimerFragment fragment = (TimerFragment) getSupportFragmentManager().findFragmentByTag("TIMER");
        if (fragment == null) {
            fragment = (TimerFragment) getSupportFragmentManager().findFragmentById(R.id.test_timer_fr_timer);
        }
        return fragment;
    }
}
