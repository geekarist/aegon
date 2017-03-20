package me.cpele.aegon;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initTime();
    }

    private void initTime() {
        TextView timeTextView = (TextView) findViewById(R.id.main_tv_time);

        int hour = 0, min = 0, sec = 0;
        timeTextView.setText(getString(R.string.main_time, hour, min, sec));
    }
}
