package evolveconference.safelive.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.SafeLiveApplication;
import evolveconference.safelive.model.Patient;
import evolveconference.safelive.ui.adapters.PatientDetailAdapter;
import evolveconference.safelive.ui.adapters.PatientDetailAdapter.Item;

public class DetailPatientFragment extends Fragment {

    private static final String PATIENT_ID = "id";
    @Bind(R.id.recyclerview) RecyclerView recyclerview;

    private SafeLiveApplication app;
    private int id = -1;
    private Patient patient;

    public DetailPatientFragment() {
    }

    public static DetailPatientFragment newInstance(int id) {
        Bundle b = new Bundle();
        b.putInt(PATIENT_ID, id);
        DetailPatientFragment f = new DetailPatientFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            id = b.getInt(PATIENT_ID, -1);
        }
        app = (SafeLiveApplication) getActivity().getApplication();

        patient = app.findPatientById(id);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.dashboard_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_patient, container, false);
        ButterKnife.bind(this, v);
        setAlert(v);
        return v;
    }

    public void setAlert(View v) {
        recyclerview = (RecyclerView) v.findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        List<Item> data = new ArrayList<>();

        data.add(new PatientDetailAdapter.Item(PatientDetailAdapter.PROFILE,
                patient.name, "", patient.photoId, patient.risk));

        data.add(new PatientDetailAdapter.Item(PatientDetailAdapter.HEADER,
                "ALERTS history", ""));

        data.add(new PatientDetailAdapter.Item(PatientDetailAdapter.ALERT,
                "2 heart rate alerts at 17h15, 18h00", ""));

        data.add(new PatientDetailAdapter.Item(PatientDetailAdapter.ALERT,
                "3 sleep alerts at 20h15, 22h00, 23h30.", ""));

        data.add(new PatientDetailAdapter.Item(PatientDetailAdapter.HEADER,
                "WARNING history", ""));

        data.add(new PatientDetailAdapter.Item(PatientDetailAdapter.WARNING,
                "3 activity warnings at 12h, 16h, 18h.", ""));

        data.add(new PatientDetailAdapter.Item(PatientDetailAdapter.HEADER,
                "STATISTICS", ""));

        data.add(new PatientDetailAdapter.Item(PatientDetailAdapter.MENU, "STATISTICS", ""));

        recyclerview.setAdapter(new PatientDetailAdapter(data, getActivity()));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
