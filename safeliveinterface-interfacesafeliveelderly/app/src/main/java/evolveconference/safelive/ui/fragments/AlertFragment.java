package evolveconference.safelive.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import evolveconference.safelive.R;
import evolveconference.safelive.SafeLiveApplication;
import evolveconference.safelive.ui.adapters.ExpandableListAdapter;
import evolveconference.safelive.ui.adapters.ExpandableListAdapter.Item;

public class AlertFragment extends Fragment {

    private SafeLiveApplication app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (SafeLiveApplication) getActivity().getApplication();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.alert_title);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_alert, container, false);

        setAlert(v);
        return v;
    }

    private RecyclerView recyclerview;

    public void setAlert(View v) {
        recyclerview = (RecyclerView) v.findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        List<Item> data = new ArrayList<>();

        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHART));

        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER,
                "ALERTS history for HEART RATE", "5 Alerts (2 high risk patients)"));

        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HISTORY,
                "Thomas Peeters  had a FALL", "3 Alerts in the last 15 minutes",
                new Date()));

        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HISTORY,
                "Mr. Peeters has been RUNNING excesively", "Running from 7h15 PM to 7h25 PM",
                new Date()));

        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HISTORY,
                "Mr. Peeters had high HEART RATE", "100 bmp during 15 minutes", new Date()));

        recyclerview.setAdapter(new ExpandableListAdapter(data));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
