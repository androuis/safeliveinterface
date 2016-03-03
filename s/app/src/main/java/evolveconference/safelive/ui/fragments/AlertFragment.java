package evolveconference.safelive.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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

    private SafeLiveApplication app;
    private GetAnomaliesAndResidents getAnomaliesAndResidents;
    private SparseArray<List<Pair<Anomaly, Resident>>> anomalyMap;
    private LinearLayout alertsHolder;
    private LinearLayout warningsHolder;
    private LayoutInflater inflater;
    private TextView alertsCount;
    private TextView warningsCount;
    private TextView alertDetails;
    private TextView warningDetails;

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
    public void onPause() {
        super.onPause();
        if (getAnomaliesAndResidents != null) {
            getAnomaliesAndResidents.cancel(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_alert, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
    }

    public void setAlert(View v) {


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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.details_alert:
                alertsHolder.setVisibility(alertsHolder.getVisibility() == View.VISIBLE ?
                        View.GONE : View.VISIBLE);
                alertDetails.setCompoundDrawablesWithIntrinsicBounds(alertsHolder.getVisibility() == View.VISIBLE ?
                        android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float, 0, 0, 0);
                break;
            case R.id.details_warning:
                warningsHolder.setVisibility(warningsHolder.getVisibility() == View.VISIBLE ?
                        View.GONE : View.VISIBLE);
                warningDetails.setCompoundDrawablesWithIntrinsicBounds(warningsHolder.getVisibility() == View.VISIBLE ?
                        android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float, 0, 0, 0);
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
                sb.append(anomaly.id).append(",");
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
        anomaliesCount.setText(String.valueOf(anomalyMap.get(anomaliesPosition).size()));
        for (Pair<Anomaly, Resident> anomalyPair : anomalyMap.get(anomaliesPosition)) {
            View anomaly = inflater.inflate(R.layout.anomaly_list_item, anomaliesHolder, false);
            ((TextView) anomaly.findViewById(R.id.resident_name))
                    .setText(String.format("%s %s", anomalyPair.second.firstName, anomalyPair.second.lastName));
            ((TextView) anomaly.findViewById(R.id.anomaly_type)).setText(anomalyPair.first.anomaly);
            Date date = anomalyTimestampFormat.parse(anomalyPair.first.timestamp);
            ((TextView) anomaly.findViewById(R.id.anomaly_time)).setText(
                    DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
            Glide.with(getActivity())
                    .load(anomalyPair.second.photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .into((ImageView) anomaly.findViewById(R.id.resident_image));
            anomaliesHolder.addView(anomaly);
        }
    }
}
