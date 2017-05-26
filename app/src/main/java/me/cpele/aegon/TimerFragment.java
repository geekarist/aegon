package me.cpele.aegon;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    }

    @Override
    public void onPause() {
        super.onPause();

        mBackground = true;
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
                mListener.onTimerEnd();
                if (mTimer != null) mTimer.cancel();
            }
        }.start();
    }

    public void cancel() {
        if (mTimer != null) mTimer.cancel();
    }

    interface Listener {

        void onTimerEnd();

        void onTimerReset();

        void onTimerTick(String timeStr);
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
        mTimeTextView.setOnClickListener(v -> showTimePicker());
    }

    void showTimePicker() {
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

        String timeStr = getString(R.string.main_time_vs_total, hour, min, sec, totalHour, totalMin, totalSec);
        mTimeTextView.setText(timeStr);
        mTimeTextView.setTextColor(getResources().getColor(R.color.bpblack, null));

        if (mBackground) mListener.onTimerTick(timeStr);
    }
}
