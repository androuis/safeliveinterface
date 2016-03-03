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

import evolveconference.safelive.R;
import evolveconference.safelive.SafeLiveApplication;
import evolveconference.safelive.model.Profile;
import evolveconference.safelive.ui.adapters.ExpandableListAdapter;
import evolveconference.safelive.ui.adapters.ExpandableListAdapter.Item;
import evolveconference.safelive.utils.Generator;

public class HomeFakeFragment extends Fragment {

    private SafeLiveApplication app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (SafeLiveApplication) getActivity().getApplication();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.home_title);
    }

    private RecyclerView recyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_expandable, container, false);

        recyclerview = (RecyclerView) v.findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        List<Item> data = new ArrayList<>();

        Profile profile = app.getProfile();

        //TODO this info should be fetched from a database or any other place
        data.add(new Item(ExpandableListAdapter.PROFILE,
                profile.name, profile.location, profile.role, R.drawable.face3));

        data.add(new Item(ExpandableListAdapter.HEADER,
                "ALERT", "5 Alerts (2 high risk residents, 3 regular residents)"));

        data.add(new Item(ExpandableListAdapter.ALERT,
                "Nicolas Janssens has anomalous Heart activity", "5 alerts in last 14 minutes",
                R.drawable.face10, Generator.randomRisk()));

        data.add(new Item(ExpandableListAdapter.ALERT,
                "Thomas Peeters had a poor audio quality", "Surrounding noise constant high since 8 minutes",
                R.drawable.face4, Generator.randomRisk()));

        data.add(new Item(ExpandableListAdapter.ALERT,
                "Nicolas Janssens has a high temperature", "Mr. Mertens temperature passed to 39 degrees Celsius at 8h15 PM",
                R.drawable.face8, Generator.randomRisk()));

        data.add(new Item(ExpandableListAdapter.HEADER,
                "WARNING", "5 warnings (2 high risk patients)"));

        data.add(new Item(ExpandableListAdapter.WARNING,
                "Nicolas Janssens", "Heart rate higher than usual",
                R.drawable.face10, Generator.randomRisk(), true));

        data.add(new Item(ExpandableListAdapter.WARNING,
                "Sarah Willems", "Anomalous high Sedentary activity",
                R.drawable.face9, Generator.randomRisk(), false));

        data.add(new Item(ExpandableListAdapter.WARNING,
                "Marie Mertens", "Fall detected in the last 5 minutes",
                R.drawable.face2, Generator.randomRisk(), true));

        recyclerview.setAdapter(new ExpandableListAdapter(data));

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
