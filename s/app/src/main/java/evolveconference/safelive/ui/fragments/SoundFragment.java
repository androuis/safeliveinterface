package evolveconference.safelive.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import evolveconference.safelive.R;
import evolveconference.safelive.SafeLiveApplication;
import evolveconference.safelive.callbacks.fragments.PatientHomepageCallback;
import evolveconference.safelive.ui.view.VisualizerView;
import evolveconference.safelive.utils.LimitedQueue;

/**
 * Created by andrei on 26/03/16.
 */
public class SoundFragment extends AbstractPatientFragment {

    private static final String TAG = SoundFragment.class.getSimpleName();

    public static DateFormat dateFormatter = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
    public static DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    @Bind(R.id.wave_form)
    VisualizerView graphView;
    @Bind(R.id.sound_date)
    TextView soundDate;
    @Bind(R.id.sound_time)
    TextView soundTime;

    private PatientHomepageCallback patientHomepageCallback;

    private LimitedQueue prevLimitedQueue = SafeLiveApplication.instance.getLimitedQueue();

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            populateGraph();
        }
    };

    public static SoundFragment newInstance(int residentId) {
        Bundle args = new Bundle();
        args.putInt(ARG_RESIDENT_ID, residentId);
        SoundFragment fragment = new SoundFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            patientHomepageCallback = (PatientHomepageCallback) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, "Starting activity must implement PatientHomepageCallback");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sound, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            populateGraph();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please allow recording")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.create().show();
        }
        soundDate.setText(dateFormatter.format(new Date()));
        soundTime.setText(timeFormatter.format(new Date()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacks(mRunnable);
    }

    private void populateGraph() {
        LimitedQueue limitedQueue = SafeLiveApplication.instance.getLimitedQueue();
        /*LineGraphSeries series = new LineGraphSeries(limitedQueue.getDataPoints());
        series.setColor(Color.GRAY);
        graphView.addSeries(series);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(limitedQueue.size());
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);*/
        if (limitedQueue != prevLimitedQueue) {
            graphView.updateVisualizer((byte[]) limitedQueue.get(limitedQueue.size() - 1));
        } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            for (Object b: limitedQueue) {
                try {
                    byteArrayOutputStream.write((byte[]) b);
                    graphView.updateVisualizer(byteArrayOutputStream.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        mHandler.postDelayed(mRunnable, 1000);
    }
}
