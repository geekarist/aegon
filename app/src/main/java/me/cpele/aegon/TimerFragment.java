package me.cpele.aegon;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TimerFragment extends Fragment {

    private static final long HALF_SEC = 500;
    public static final String KEY_TIME_OF_ARRIVAL = "TIME_OF_ARRIVAL";
    public static final String KEY_START_TIME = "START_TIME";

    private long mTimeOfArrival;
    private long mStartTime;

    private Listener mListener;
    private CountDownTimer mTimer;
    private boolean mBackground;
    private TextView mTimeTextView;

    private Context mApp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = getActivity().getApplicationContext();

        if (savedInstanceState != null) {
            restoreStateFrom(savedInstanceState);
            start();
        } else showPicker();
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();
        mTimer = null;
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveStateTo(outState);
        super.onSaveInstanceState(outState);
    }

    private void saveStateTo(@NonNull Bundle outState) {
        outState.putLong(KEY_TIME_OF_ARRIVAL, mTimeOfArrival);
        outState.putLong(KEY_START_TIME, mStartTime);
    }

    private void restoreStateFrom(@NonNull Bundle savedInstanceState) {
        mTimeOfArrival = savedInstanceState.getLong(KEY_TIME_OF_ARRIVAL);
        mStartTime = savedInstanceState.getLong(KEY_START_TIME);
    }

    static TimerFragment newInstance(long startTime, long timeOfArrival) {
        TimerFragment timerFragment = new TimerFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_START_TIME, startTime);
        bundle.putLong(KEY_TIME_OF_ARRIVAL, timeOfArrival);
        timerFragment.setArguments(bundle);
        return timerFragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        mBackground = false;
        Log.d(getClass().getSimpleName(), "Resuming");
    }

    @Override
    public void onPause() {
        super.onPause();

        mBackground = true;
        Log.d(getClass().getSimpleName(), "Pausing");
    }

    public void start() {

        long timeToArrival = mTimeOfArrival - System.currentTimeMillis();

        mTimer = new CountDownTimer(timeToArrival, HALF_SEC) {

            @Override
            public void onTick(long millisUntilFinished) {
                updateEtaView();
            }

            @Override
            public void onFinish() {
                if (mListener != null) mListener.onTimerEnd();
                if (mTimer != null) mTimer.cancel();
            }
        }.start();
    }

    public void cancel() {
        if (mTimer != null) mTimer.cancel();
    }

    public static TimerFragment find(FragmentManager manager, int id) {
        TimerFragment fragment = (TimerFragment) manager.findFragmentByTag("TIMER");
        if (fragment == null) {
            fragment = (TimerFragment) manager.findFragmentById(id);
        }
        return fragment;
    }

    interface Listener {

        void onTimerEnd();

        void onTimerReset();

        void onTimerProgress(String timeStr);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTimeTextView = (TextView) view.findViewById(R.id.main_tv_time);
        mTimeTextView.setOnClickListener(v -> showPicker());
    }

    void showPicker() {
        new HmsPickerBuilder().addHmsPickerDialogHandler(
                (reference, isNegative, hours, minutes, seconds) -> {
                    if (mTimer != null) mTimer.cancel();
                    mListener.onTimerReset();
                    mTimeOfArrival = 1000 + System.currentTimeMillis()
                            + HOURS.toMillis(hours)
                            + MINUTES.toMillis(minutes)
                            + SECONDS.toMillis(seconds);
                    mStartTime = System.currentTimeMillis();
                    start();
                })
                .setFragmentManager(getActivity().getSupportFragmentManager())
                .setStyleResId(R.style.BetterPickersDialogFragment)
                .show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Listener) mListener = (Listener) context;
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    private void updateEtaView() {

        long hour = 0;
        long min = 0;
        long sec = 0;

        long totalHour = 0;
        long totalMin = 0;
        long totalSec = 0;

        if (mTimeOfArrival != 0) {

            long timeToArrival = mTimeOfArrival - System.currentTimeMillis();
            hour = MILLISECONDS.toHours(timeToArrival);
            min = MILLISECONDS.toMinutes(timeToArrival) - HOURS.toMinutes(hour);
            sec = MILLISECONDS.toSeconds(timeToArrival) - MINUTES.toSeconds(min) - HOURS.toSeconds(hour);

            long total = mTimeOfArrival - mStartTime;
            totalHour = MILLISECONDS.toHours(total);
            totalMin = MILLISECONDS.toMinutes(total) - HOURS.toMinutes(totalHour);
            totalSec = MILLISECONDS.toSeconds(total) - HOURS.toSeconds(totalHour) - MINUTES.toSeconds(totalMin);
        }

        // Has to be the app context to be able to get this when paused
        String timeStr = mApp.getString(R.string.main_time_vs_total, hour, min, sec, totalHour, totalMin, totalSec);
        mTimeTextView.setText(timeStr);
        mTimeTextView.setTextColor(mApp.getColor(R.color.bpblack));

        if (mBackground) {
            if (mListener != null) mListener.onTimerProgress(timeStr);
            Log.d(getClass().getSimpleName(), "In background");
        } else {
            Log.d(getClass().getSimpleName(), "In foreground");
        }
    }
}
