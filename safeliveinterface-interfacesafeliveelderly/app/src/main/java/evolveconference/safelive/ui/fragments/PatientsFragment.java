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
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.SafeLiveApplication;
import evolveconference.safelive.model.Patient;
import evolveconference.safelive.ui.adapters.ExpandablePatientsAdapter;
import evolveconference.safelive.ui.adapters.ExpandablePatientsAdapter.Item;

public class PatientsFragment extends Fragment {

    @Bind(R.id.high_risk_patients_recycler_view) RecyclerView highRiskRecycler;
    @Bind(R.id.regular_patients_recycler_view) RecyclerView regularPatientsRecycler;

    private LinearLayoutManager layoutManager;
    private LinearLayoutManager layoutManager1;
    private SafeLiveApplication app;

    public PatientsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (SafeLiveApplication) getActivity().getApplication();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.patients_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_patients, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupPatients();
    }

    private void setupPatients() {
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        highRiskRecycler.setLayoutManager(layoutManager);
        highRiskRecycler.setHasFixedSize(true);

        layoutManager1 = new LinearLayoutManager(getActivity());
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        regularPatientsRecycler.setLayoutManager(layoutManager1);
        regularPatientsRecycler.setHasFixedSize(true);

        initializeData();
    }


    private void initializeData() {

        Random r = new Random();

        List<Item> items = new ArrayList<>();

        List<Patient> patients = app.getPatients();

        List<Patient> highRisk = patients.subList(0, 2);
        for (Patient p : highRisk) {
            items.add(new ExpandablePatientsAdapter.Item(p.id, ExpandablePatientsAdapter.PATIENT_CARD,
                    p.name, "", p.risk, p.photoId, r.nextBoolean(), r.nextBoolean()));
        }
        highRiskRecycler.setAdapter(new ExpandablePatientsAdapter(items, getActivity()));


        List<Patient> regularPatients = patients.subList(2, patients.size() - 1);
        List<Item> regular = new ArrayList<>();
        for (Patient p : regularPatients) {
            regular.add(new ExpandablePatientsAdapter.Item(p.id, ExpandablePatientsAdapter.PATIENT_CARD,
                    p.name, "", p.risk, p.photoId, r.nextBoolean(), r.nextBoolean()));
        }
        regularPatientsRecycler.setAdapter(new ExpandablePatientsAdapter(regular, getActivity()));

    }
}
