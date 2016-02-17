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
import evolveconference.safelive.ui.adapters.LevelAdapter;

public class SettingsFragment extends Fragment {


    @Bind(R.id.level1) RecyclerView recyclerView1;
    @Bind(R.id.level2) RecyclerView recyclerView2;

    private List<Patient> patients1;
    private List<Patient> patients2;

    private RecyclerView.Adapter adapter1;
    private RecyclerView.Adapter adapter2;
    private LinearLayoutManager layoutManager1;
    private LinearLayoutManager layoutManager2;


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.settings_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupPatients();
    }

    private void setupPatients() {
        recyclerView1.setHasFixedSize(true);
        layoutManager1 = new LinearLayoutManager(getActivity());
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView1.setLayoutManager(layoutManager1);
        initializeData();
        adapter1 = new LevelAdapter(patients1, getActivity());
        recyclerView1.setAdapter(adapter1);

        recyclerView2.setHasFixedSize(true);
        layoutManager2 = new LinearLayoutManager(getActivity());
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView2.setLayoutManager(layoutManager2);
        adapter2 = new LevelAdapter(patients2, getActivity());
        recyclerView2.setAdapter(adapter2);
    }


    private void initializeData() {
        // Sublist just for mock
        patients1 = new ArrayList<>();
        patients1.addAll(((SafeLiveApplication) getActivity().getApplication()).getContactLevel1());

        // Sublist just for mock
        patients2 = new ArrayList<>();
        patients2.addAll(((SafeLiveApplication) getActivity().getApplication()).getContactLevel2());
    }

}
