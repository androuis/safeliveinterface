package evolveconference.safelive.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.SafeLiveApplication;
import evolveconference.safelive.dfapi.ApiException;
import evolveconference.safelive.dfapi.ApiInvoker;
import evolveconference.safelive.dfapi.BaseAsyncRequest;
import evolveconference.safelive.model.Anomaly;
import evolveconference.safelive.model.AnomalyList;
import evolveconference.safelive.model.Resident;
import evolveconference.safelive.model.ResidentList;
import evolveconference.safelive.ui.adapters.ExpandableListAdapter;
import evolveconference.safelive.ui.adapters.ExpandableListAdapter.Item;
import evolveconference.safelive.utils.AppConstants;
import evolveconference.safelive.utils.PrefUtil;

public class AlertFragment extends Fragment implements View.OnClickListener {

    private static final int KEY_TIMESTAMP = 111;
    private GetAnomaliesAndResidents getAnomaliesAndResidents;
    private SparseArray<List<Pair<Anomaly, Resident>>> anomalyMap;
    private LinearLayout alertsHolder;
    private LinearLayout warningsHolder;
    private LayoutInflater inflater;
    private TextView alertsCount;
    private TextView warningsCount;
    private TextView alertDetails;
    private TextView warningDetails;
    //header
    @Bind(R.id.header_card_view)
    CardView headerCardView;
    @Bind(R.id.profile_image)
    CircularImageView profileImage;
    @Bind(R.id.username)
    TextView username;
    @Bind(R.id.address)
    TextView address;
    @Bind(R.id.role)
    TextView role;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_alert, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.alert_title);
        alertsHolder = (LinearLayout) view.findViewById(R.id.holder_alerts);
        warningsHolder = (LinearLayout) view.findViewById(R.id.holder_warnings);
        alertsCount = (TextView) view.findViewById(R.id.count_alert);
        warningsCount = (TextView) view.findViewById(R.id.count_warning);
        alertDetails = (TextView) view.findViewById(R.id.details_alert);
        alertDetails.setOnClickListener(this);
        warningDetails = (TextView) view.findViewById(R.id.details_warning);
        warningDetails.setOnClickListener(this);
        getAnomaliesAndResidents = new GetAnomaliesAndResidents();
        getAnomaliesAndResidents.execute();
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getAnomaliesAndResidents != null) {
            getAnomaliesAndResidents.cancel(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getTag(R.id.TAG_TIMESTAMP) != null) {
            Fragment newFragment = HeartRateFragment.newInstance((String) v.getTag(KEY_TIMESTAMP), "");
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(newFragment, getClass().getSimpleName());
            ft.commit();
            fm.executePendingTransactions();
        }
        switch (v.getId()) {
            case R.id.details_alert:
                alertsHolder.setVisibility(alertsHolder.getVisibility() == View.VISIBLE ?
                        View.GONE : View.VISIBLE);
                alertDetails.setCompoundDrawablesWithIntrinsicBounds(alertsHolder.getVisibility() == View.VISIBLE ?
                        R.drawable.ic_keyboard_arrow_up : R.drawable.ic_keyboard_arrow_down, 0, 0, 0);
                break;
            case R.id.details_warning:
                warningsHolder.setVisibility(warningsHolder.getVisibility() == View.VISIBLE ?
                        View.GONE : View.VISIBLE);
                warningDetails.setCompoundDrawablesWithIntrinsicBounds(warningsHolder.getVisibility() == View.VISIBLE ?
                        R.drawable.ic_keyboard_arrow_up : R.drawable.ic_keyboard_arrow_down, 0, 0, 0);
                break;
        }
    }

    private class GetAnomaliesAndResidents extends BaseAsyncRequest {
        private AnomalyList anomalyList;

        protected void doSetup() throws ApiException, JSONException {
            callerName = "GetPatientInfo";

            serviceName = AppConstants.DB_SVC;
            endPoint = "anomalies";
            verb = "GET";

            // need to include the API key and session token
            applicationApiKey = AppConstants.API_KEY;
            sessionToken = PrefUtil.getString(getActivity().getApplicationContext(), AppConstants.SESSION_TOKEN);
        }

        @Override
        protected void processResponse(String response) throws Exception {
            anomalyList = ((AnomalyList) ApiInvoker.deserialize(response, "", AnomalyList.class));
            doSetup2();
            processResponse2(call());
        }

        private void doSetup2() {
            //TODO: refactor this to only retrieve info for the residents that have an id in the anomalylist :)
            callerName = "GetPatientInfo";

            serviceName = AppConstants.DB_SVC;
            endPoint = "resident";
            verb = "GET";

            queryParams = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            for (Anomaly anomaly : anomalyList.record) {
                sb.append(anomaly.residentId).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            queryParams.put("ids", sb.toString());

            // need to include the API key and session token
            applicationApiKey = AppConstants.API_KEY;
            sessionToken = PrefUtil.getString(getActivity().getApplicationContext(), AppConstants.SESSION_TOKEN);
        }

        private void processResponse2(String response) throws ApiException {
            ResidentList residentList = ((ResidentList) ApiInvoker.deserialize(response, "", ResidentList.class));
            anomalyMap = new SparseArray<>();
            for (Anomaly anomaly : anomalyList.record) {
                for (Resident resident : residentList.record) {
                    if (anomaly.residentId == resident.id) {
                        List<Pair<Anomaly, Resident>> values = anomalyMap.get(anomaly.eventType);
                        if (values != null) {
                            values.add(Pair.create(anomaly, resident));
                        } else {
                            values = new ArrayList<>();
                            values.add(Pair.create(anomaly, resident));
                            anomalyMap.put(anomaly.eventType, values);
                        }
                    }
                }
            }
        }

        @Override
        protected void onCompletion(boolean success) {
            if (success) {
                try {
                    populateScreen();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void populateScreen() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        populateAnomalies(alertsCount, alertsHolder, 0, format);
        populateAnomalies(warningsCount, warningsHolder, 1, format);
    }

    private void populateAnomalies(TextView anomaliesCount, LinearLayout anomaliesHolder, int anomaliesPosition, DateFormat anomalyTimestampFormat) throws ParseException {
        List<Pair<Anomaly, Resident>> anomalies = anomalyMap.get(anomaliesPosition);
        if (anomalies != null) {
            anomaliesCount.setText(String.valueOf(anomalies.size()));
            for (Pair<Anomaly, Resident> anomalyPair : anomalies) {
                View anomaly = inflater.inflate(R.layout.anomaly_list_item, anomaliesHolder, false);
                ((TextView) anomaly.findViewById(R.id.resident_name))
                        .setText(String.format("%s %s", anomalyPair.second.firstName, anomalyPair.second.lastName));
                ((TextView) anomaly.findViewById(R.id.anomaly_type)).setText(anomalyPair.first.anomaly);
                Date date = anomalyTimestampFormat.parse(anomalyPair.first.timestamp);
                ((TextView) anomaly.findViewById(R.id.anomaly_time)).setText(
                        DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
                Picasso.with(getActivity())
                        .load(anomalyPair.second.photo)
                        .error(android.R.drawable.ic_menu_myplaces)
                        .placeholder(android.R.drawable.ic_menu_myplaces)
                        .fit()
                        .into((ImageView) anomaly.findViewById(R.id.resident_image));
                anomaliesHolder.addView(anomaly);
                anomaly.setTag(R.id.TAG_TIMESTAMP, date.getTime());
                anomaly.setOnClickListener(this);
            }
        }
    }
}
