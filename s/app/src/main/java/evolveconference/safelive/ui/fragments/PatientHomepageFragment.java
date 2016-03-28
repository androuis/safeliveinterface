package evolveconference.safelive.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.Bind;
import evolveconference.safelive.R;
import evolveconference.safelive.callbacks.fragments.PatientHomepageCallback;

/**
 * Created by andrei on 26/03/16.
 */
public class PatientHomepageFragment extends AbstractPatientFragment implements View.OnClickListener {

    private static final String TAG = PatientHomepageFragment.class.getSimpleName();

    @Bind(R.id.location)
    LinearLayout linearLayoutLocation;
    @Bind(R.id.heart_rate)
    LinearLayout linearLayoutHeartRate;
    @Bind(R.id.activity)
    LinearLayout linearLayoutActivity;
    @Bind(R.id.sound)
    LinearLayout linearLayoutSound;

    private PatientHomepageCallback patientHomepageCallback;

    public static PatientHomepageFragment newInstance(int residentId) {
        Bundle args = new Bundle();
        args.putInt(ARG_RESIDENT_ID, residentId);
        PatientHomepageFragment fragment = new PatientHomepageFragment();
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
        return inflater.inflate(R.layout.fragment_patient_homepage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayoutLocation.setOnClickListener(this);
        linearLayoutHeartRate.setOnClickListener(this);
        linearLayoutActivity.setOnClickListener(this);
        linearLayoutSound.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getActivity().getString(R.string.sensors));
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        switch (v.getId()) {
            case R.id.location:
                fragment = LocationFragment.newInstance(mResidentId);
                break;
            case R.id.heart_rate:
                fragment = HeartRateFragment.newInstance(mResidentId, 2);
                break;
            case R.id.activity:
                fragment = ActivitiesFragment.newInstance(mResidentId);
                break;
            case R.id.sound:
                fragment = SoundFragment.newInstance(mResidentId);
                break;
            default:
                fragment = ActivitiesFragment.newInstance(mResidentId);
        }
        patientHomepageCallback.onCallback(fragment, true);
    }
}
