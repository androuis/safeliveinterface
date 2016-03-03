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

public class HomeFragment extends AlertFragment {

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.home_title);
    }

}
