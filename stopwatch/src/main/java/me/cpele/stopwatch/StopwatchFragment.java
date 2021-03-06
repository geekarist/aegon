package me.cpele.stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class StopwatchFragment extends Fragment {

    private static final long HALF_SEC = 500;
    private static final String KEY_START_TIME = "START_TIME";
    private static final String KEY_TIME_OF_ARRIVAL = "TIME_OF_ARRIVAL";

    private Timer mStopwatch;

    private TextView mTimeTextView;

    private long mTimeOfArrival;
    private long mStartTime;
    private boolean mBackground = true;

    private OnTickListener mOnTickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStartTime = getArguments().getLong(KEY_START_TIME);
        mTimeOfArrival = getArguments().getLong(KEY_TIME_OF_ARRIVAL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stopwatch, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTimeTextView = view.findViewById(R.id.stopwatch_tv_time);
    }

    @Override
    public void onPause() {
        mBackground = true;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBackground = false;
    }

    @Override
    public void onDestroy() {
        tearDown();
        super.onDestroy();
    }

    public static StopwatchFragment newInstance(long startTime, long timeOfArrival) {
        StopwatchFragment fragment = new StopwatchFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_START_TIME, startTime);
        bundle.putLong(KEY_TIME_OF_ARRIVAL, timeOfArrival);
        fragment.setArguments(bundle);
        return fragment;
    }

    @SuppressWarnings("UnusedReturnValue")
    public StopwatchFragment setOnTickListener(OnTickListener onTickListener) {
        mOnTickListener = onTickListener;
        return this;
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
        mTimeTextView.setText(timeStr);
        mTimeTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));

        mOnTickListener.accept(mBackground, timeStr);
    }

    public void setup() {
        mStopwatch = new Timer();
        mStopwatch.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(() -> updateStopwatchView());
            }
        }, 0, HALF_SEC);
    }

    public void tearDown() {
        if (mStopwatch != null) mStopwatch.cancel();
        mStopwatch = null;
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnTickListener extends BiConsumer<Boolean, String> {
    }
}
