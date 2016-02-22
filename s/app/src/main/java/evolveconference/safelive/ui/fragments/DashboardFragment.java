package evolveconference.safelive.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;

public class DashboardFragment extends Fragment {

    // Dashboard actions
    @Bind(R.id.da_heart_status) TextView daHeartStatus;
    @Bind(R.id.da_oximetry) TextView daOximetry;
    @Bind(R.id.da_activity) TextView daActivity;
    @Bind(R.id.da_blood) TextView daBlood;
    @Bind(R.id.da_temperature) TextView daTemperature;
    @Bind(R.id.da_heart_risks) TextView daHeartRisks;
    @Bind(R.id.da_respiratory) TextView daRespiratory;
    @Bind(R.id.da_diabetes) TextView daDiabetes;
    @Bind(R.id.da_sleep) TextView daSleep;
    @Bind(R.id.da_audio) TextView daAudio;
    @Bind(R.id.da_incident) TextView daIncident;

    public DashboardFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.dashboard_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupDashboard();
    }

    private void setupDashboard(){
        daHeartStatus.setOnClickListener(onDashboardClickListener);
        daOximetry.setOnClickListener(onDashboardClickListener);
        daActivity.setOnClickListener(onDashboardClickListener);
        daBlood.setOnClickListener(onDashboardClickListener);
        daTemperature.setOnClickListener(onDashboardClickListener);
        daHeartRisks.setOnClickListener(onDashboardClickListener);
        daRespiratory.setOnClickListener(onDashboardClickListener);
        daDiabetes.setOnClickListener(onDashboardClickListener);
        daSleep.setOnClickListener(onDashboardClickListener);
        daAudio.setOnClickListener(onDashboardClickListener);
        daIncident.setOnClickListener(onDashboardClickListener);
    }

    private OnClickListener onDashboardClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), "DASHBOARD TBI", Toast.LENGTH_SHORT).show();
        }
    };
}
