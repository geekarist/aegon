package me.cpele.aegon;

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

    private Timer mStopwatch;

    private TextView mTimeTextView;

    private long mTimeOfArrival;
    private long mStartTime;
    private boolean mBackground = true;

    private OnTickListener mOnTickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stopwatch, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTimeTextView = (TextView) view.findViewById(R.id.stopwatch_tv_time);
    }

    public static StopwatchFragment newInstance(long startTime) {
        StopwatchFragment fragment = new StopwatchFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_START_TIME, startTime);
        fragment.setArguments(bundle);
        return fragment;
    }

    public StopwatchFragment setOnTickListener(OnTickListener onTickListener) {
        mOnTickListener = onTickListener;
        return this;
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
        String timeStr = getString(R.string.main_time_vs_total, hour, min, sec, totalHour, totalMin, totalSec);
        mTimeTextView.setText(timeStr);
        mTimeTextView.setTextColor(getResources().getColor(R.color.bpDarker_red, null));

        mOnTickListener.accept(mBackground, timeStr);
    }

    public void play() {
        switchToStopWatch();
    }

    public void pause() {
        if (mStopwatch != null) mStopwatch.cancel();
        mStopwatch = null;
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnTickListener extends BiConsumer<Boolean, String> {
    }
}
