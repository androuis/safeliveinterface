package evolveconference.safelive.ui.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.Bind;
import evolveconference.safelive.R;
import evolveconference.safelive.SafeLiveApplication;
import evolveconference.safelive.callbacks.fragments.PatientHomepageCallback;
import evolveconference.safelive.tasks.RecordIntentService;
import evolveconference.safelive.ui.view.VisualizerView;
import evolveconference.safelive.utils.LimitedQueue;

/**
 * Created by andrei on 26/03/16.
 */
public class SoundFragment extends AbstractPatientFragment {

    private static final String TAG = SoundFragment.class.getSimpleName();

    @Bind(R.id.wave_form)
    VisualizerView visualizerView;

    private PatientHomepageCallback patientHomepageCallback;

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
        LimitedQueue limitedQueue = SafeLiveApplication.instance.getLimitedQueue();
        visualizerView.updateVisualizer((Float[]) limitedQueue.toArray(new Float[limitedQueue.size()]));
    }
}
