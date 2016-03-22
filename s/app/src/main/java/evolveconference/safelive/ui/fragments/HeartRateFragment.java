package evolveconference.safelive.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.Arrays;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.dfapi.ApiException;
import evolveconference.safelive.dfapi.ApiInvoker;
import evolveconference.safelive.dfapi.BaseAsyncRequest;
import evolveconference.safelive.model.ReadingList;
import evolveconference.safelive.model.Resident;
import evolveconference.safelive.model.ResidentList;
import evolveconference.safelive.utils.AppConstants;
import evolveconference.safelive.utils.ComponentUtils;
import evolveconference.safelive.utils.PrefUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HeartRateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeartRateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mReadingId;
    private int mResidentId;

    @Bind(R.id.graph1)
    GraphView mGraphView1;
    @Bind(R.id.graph2)
    GraphView mGraphView2;
    @Bind(R.id.resident_image)
    CircularImageView mProfileImage;
    @Bind(R.id.resident_name)
    TextView mProfileName;
    @Bind(R.id.resident_location)
    TextView mProfileLocation;

    public HeartRateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HeartRateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HeartRateFragment newInstance(int param1, int param2) {
        HeartRateFragment fragment = new HeartRateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mReadingId = getArguments().getInt(ARG_PARAM1);
            mResidentId = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_heart_rate, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.heart_rate_title);
        ButterKnife.bind(this, view);
        new GetResident().execute();
        new GetHeartRate(15, mGraphView1).execute();
        new GetHeartRate(5, mGraphView2).execute();
    }

    private class GetResident extends BaseAsyncRequest {
        private Resident mResident;

        @Override
        protected void doSetup() throws ApiException, JSONException {
            callerName = "GetPatientInfo";

            serviceName = AppConstants.DB_SVC;
            endPoint = "resident";
            verb = "GET";
            queryParams = new HashMap<>();
            queryParams.put("ids", String.valueOf(mResidentId));

            // need to include the API key and session token
            applicationApiKey = AppConstants.API_KEY;
            sessionToken = PrefUtil.getString(getActivity().getApplicationContext(), AppConstants.SESSION_TOKEN);
        }

        @Override
        protected void processResponse(String response) throws Exception {
            ResidentList residentList = ((ResidentList) ApiInvoker.deserialize(response, "", ResidentList.class));
            if (residentList.record.size() > 0) {
                mResident = residentList.record.get(0);
            }
        }

        @Override
        protected void onCompletion(boolean success) {
            if (ComponentUtils.checkUIisOK(getActivity())) {
                if (success) {
                    Picasso.with(getActivity())
                            .load(mResident.photo)
                            .error(android.R.drawable.ic_menu_myplaces)
                            .placeholder(android.R.drawable.ic_menu_myplaces)
                            .fit()
                            .into(mProfileImage);
                    mProfileName.setText(getString(R.string.first_and_last_names, mResident.firstName, mResident.lastName));
                    mProfileLocation.setText(mResident.location);
                } else {
                    displayLoginIfNeeded(getActivity(), exception);
                }
            }
        }
    }

    private class GetHeartRate extends BaseAsyncRequest {

        private int mMinutes;
        private DataPoint[] mDataPoints;
        private GraphView mGraphView;

        public GetHeartRate(int minutes, GraphView graphView) {
            this.mMinutes = minutes;
            mGraphView = graphView;
        }

        @Override
        protected void onPreExecute() {
            mGraphView.getViewport().setXAxisBoundsManual(true);
            mGraphView.getViewport().setYAxisBoundsManual(true);
            mGraphView.getViewport().setMaxY(90);
            mGraphView.getViewport().setMinY(70);
            mGraphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
            mGraphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
            mGraphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (!isValueX) {
                        return value == 75 ? "MIN" : value == 85 ? "MAX" : "   ";
                    }
                    return "   ";
                }
            });
        }

        protected void doSetup() throws ApiException, JSONException {
            callerName = "GetHeartRate";

            serviceName = AppConstants.DB_SVC;
            endPoint = "readings";
            verb = "GET";
            queryParams = new HashMap<>();
            queryParams.put("filter", "(deviceid%3D(select%20deviceid%20from%20readings%20where%20readingid%3D'"
                    + mReadingId + "'))%20and%20(readingtimestamp%20%3E%3D%20((select%20readingtimestamp%20from%20readings%20where%20readingid%3D'"
                    + mReadingId + "')%20%2B%20INTERVAL%20'-"
                    + mMinutes + "%20minutes'))%20and%20(readingtimestamp%20%3C%3D%20((select%20readingtimestamp%20from%20readings%20where%20readingid%3D'"
                    + mReadingId + "')%20%2B%20INTERVAL%20'" + mMinutes + "%20minutes'))");
            queryParams.put("related", "readingtimestamp%2C%20readingvalue");

            // need to include the API key and session token
            applicationApiKey = AppConstants.API_KEY;
            sessionToken = PrefUtil.getString(getActivity().getApplicationContext(), AppConstants.SESSION_TOKEN);
        }

        @Override
        protected void processResponse(String response) throws Exception {
            mDataPoints = ((ReadingList) ApiInvoker.deserialize(response, "", ReadingList.class)).getDataPoints();
        }

        @Override
        protected void onCompletion(boolean success) {
            if (getActivity() != null) {
                if (success) {
                    mGraphView.getViewport().setMaxX(mDataPoints[mDataPoints.length / 4 - 1].getX());
                    mGraphView.addSeries(new LineGraphSeries(Arrays.copyOfRange(mDataPoints, 0, mDataPoints.length / 4)));
                } else {
                    displayLoginIfNeeded(getActivity(), exception);
                }
            }
        }
    }
}
