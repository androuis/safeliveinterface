package evolveconference.safelive.ui.fragments;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.dfapi.ApiException;
import evolveconference.safelive.dfapi.ApiInvoker;
import evolveconference.safelive.dfapi.BaseAsyncRequest;
import evolveconference.safelive.model.Resident;
import evolveconference.safelive.model.ResidentList;
import evolveconference.safelive.ui.adapters.GridPatientsAdapter;
import evolveconference.safelive.ui.adapters.GridPatientsAdapter.Item;
import evolveconference.safelive.utils.AppConstants;
import evolveconference.safelive.utils.ComponentUtils;
import evolveconference.safelive.utils.PrefUtil;

public class PatientsFragment extends Fragment {

    @Bind(R.id.grid_patients_recycler_view)
    RecyclerView mPatientsRecycler;
    private GridPatientsAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private Map<String, List<Item>> mHashMap = new LinkedHashMap<>();
    private Random mRandom = new Random();


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.patients_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grid_patients, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        setupPatients();
        initializeData();
    }

    private void setupPatients() {
        // todo update to grid adapter
        mGridLayoutManager = new GridLayoutManager(getContext(), getContext().getResources().getInteger(R.integer.grid_item_span_count));
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)) {
                    case GridPatientsAdapter.TYPE_HEADER:
                        return 3;
                    case GridPatientsAdapter.TYPE_PATIENT_CARD:
                        return 1;
                    default:
                        return -1;
                }
            }
        });
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAdapter = new GridPatientsAdapter(mHashMap, getContext());
        mPatientsRecycler.setHasFixedSize(true);
        mPatientsRecycler.setLayoutManager(mGridLayoutManager);
        mPatientsRecycler.setAdapter(mAdapter);
    }

    private void initializeData() {
        new GetResidents().execute();
    }


    private class GetResidents extends BaseAsyncRequest {

        protected void doSetup() throws ApiException, JSONException {
            callerName = "GetResidents";

            serviceName = AppConstants.DB_SVC;
            endPoint = "resident";
            verb = "GET";

            // need to include the API key and session token
            applicationApiKey = AppConstants.API_KEY;
            sessionToken = PrefUtil.getString(getActivity().getApplicationContext(), AppConstants.SESSION_TOKEN);
        }

        @Override
        protected void processResponse(String response) throws Exception {
            List<Resident> residentList = ((ResidentList) ApiInvoker.deserialize(response, "", ResidentList.class)).record;
            if (residentList != null) {
                List<Item> highRisk = new ArrayList<>();
                List<Item> regularRisk = new ArrayList<>();
                mHashMap.clear();
                for (Resident resident : residentList) {
                    if (resident.riskClass == Resident.RISK_HIGH) {
                        highRisk.add(new Item(resident.id, resident.firstName, resident.lastName, resident.photo, mRandom.nextInt(10), mRandom.nextInt(10)));
                    } else {
                        regularRisk.add(new Item(resident.id, resident.firstName, resident.lastName, resident.photo, mRandom.nextInt(10), mRandom.nextInt(10)));
                    }
                }
                mHashMap.put(getString(R.string.high_risk_text), highRisk);
                mHashMap.put(getString(R.string.regular_risk_text), regularRisk);
            }

        }

        @Override
        protected void onCompletion(boolean success) {
            if (ComponentUtils.checkUIisOK(getActivity())) {
                if (success) {
                    mAdapter.swapData(mHashMap);
                } else {
                    displayLoginIfNeeded(getActivity(), exception);
                }
            }
        }
    }

    /*@Subscribe(sticky = true, threadMode = ThreadMode.MainThread)
    public void onPatientsEvent(Events.PatientsEvent event) {
        List<Item> highRisk = new ArrayList<>();
        List<Item> regularRisk = new ArrayList<>();
        Random r = new Random();
        List<PatientNew> patientsList = event.getData();
        if (patientsList != null) {
            mHashMap.clear();
            for (PatientNew patientNew : patientsList) {
                if (patientNew.getRiskclass() != 0) {
                    highRisk.add(new Item(patientNew.getId(), GridPatientsAdapter.TYPE_PATIENT_CARD,
                            patientNew.getFirstName(), patientNew.getLastName(), Generator.randomAlertWarningRisk(), patientNew.getPicture(), r.nextBoolean(), r.nextBoolean()));
                } else {
                    regularRisk.add(new Item(patientNew.getId(), GridPatientsAdapter.TYPE_PATIENT_CARD,
                            patientNew.getFirstName(), patientNew.getLastName(), Generator.randomAlertWarningRisk(), patientNew.getPicture(), r.nextBoolean(), r.nextBoolean()));
                }
            }
            mHashMap.put(getString(R.string.high_risk_text), highRisk);
            mHashMap.put(getString(R.string.regular_risk_text), regularRisk);
            mAdapter.swapData(mHashMap);
        }
    }


    private void initializeData() {

        Random r = new Random();
        mHashMap = new LinkedHashMap<>();
        //TODO update with real data
        List<Item> items = new ArrayList<>();
        List<Item> items2 = new ArrayList<>();
//        items.add(new Item(0, GridPatientsAdapter.TYPE_HEADER,
//                getString(R.string.high_risk_text), null, 0));
        List<Patient> patients = app.getPatients();

        List<Patient> highRisk = patients.subList(0, 3);
        for (Patient p : highRisk) {
            items.add(new Item(p.id, GridPatientsAdapter.TYPE_PATIENT_CARD,
                    p.name, p.lastname, p.risk, null, r.nextBoolean(), r.nextBoolean()));
        }
//        List<Patient> regularRisk = patients.subList(3, patients.size() - 1);
//        just mock data
        List<Patient> regularRisk = patients;
        for (Patient p : regularRisk) {
            items2.add(new Item(p.id, GridPatientsAdapter.TYPE_PATIENT_CARD,
                    p.name, p.lastname, p.risk, null, r.nextBoolean(), r.nextBoolean()));
        }

        mHashMap.put(getString(R.string.high_risk_text), items);
        mHashMap.put(getString(R.string.regular_risk_text), items2);
        mAdapter = new GridPatientsAdapter(null, mHashMap, getContext());
        mPatientsRecycler.setAdapter(mAdapter);

    }*/

    /**
     * Nested class for recycleview items styling
     */
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int mSpanCount;
        private int mSpacing;
        private boolean isIncludeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing) {
            this.mSpanCount = spanCount;
            this.mSpacing = spacing;
            this.isIncludeEdge = true;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % mSpanCount; // item column

            if (isIncludeEdge) {
                outRect.left = mSpacing - column * mSpacing / mSpanCount; // mSpacing - column * ((1f / mSpanCount) * mSpacing)
                outRect.right = (column + 1) * mSpacing / mSpanCount; // (column + 1) * ((1f / mSpanCount) * mSpacing)

                if (position < mSpanCount) { // top edge
                    outRect.top = mSpacing;
                }
                outRect.bottom = mSpacing; // item bottom
            } else {
                outRect.left = column * mSpacing / mSpanCount; // column * ((1f / mSpanCount) * mSpacing)
                outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount; // mSpacing - (column + 1) * ((1f /    mSpanCount) * mSpacing)
                if (position >= mSpanCount) {
                    outRect.top = mSpacing; // item top
                }
            }
        }
    }
}
