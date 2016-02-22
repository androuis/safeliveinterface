package evolveconference.safelive.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pkmmte.view.CircularImageView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.dfapi.ApiException;
import evolveconference.safelive.dfapi.ApiInvoker;
import evolveconference.safelive.dfapi.BaseAsyncRequest;
import evolveconference.safelive.model.Reading;
import evolveconference.safelive.model.ReadingList;
import evolveconference.safelive.model.Resident;
import evolveconference.safelive.model.ResidentList;
import evolveconference.safelive.utils.AppConstants;
import evolveconference.safelive.utils.GMapV2Direction;
import evolveconference.safelive.utils.GetDirectionsAsyncTask;
import evolveconference.safelive.utils.PrefUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment implements OnMapReadyCallback {

    @Bind(R.id.profile_image)
    CircularImageView profileImage;
    @Bind(R.id.profile_name)
    TextView profileName;
    @Bind(R.id.profile_location)
    TextView profileLocation;

    private static final String PATIENT_ID = "id";
    private Resident user;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private List<LatLng> coordinates;

    public static LocationFragment newInstance(int id) {
        Bundle b = new Bundle();
        b.putInt(PATIENT_ID, id);
        LocationFragment f = new LocationFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int patientId = getArguments().getInt(PATIENT_ID, -1);
            if (patientId != -1) {
                new GetPatientInfo(patientId).execute();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        ButterKnife.bind(this, view);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (user != null) {
            populateScreen();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        new GetCoordinates("FDGGDAA12435G", "Location").execute();
    }

    private void addMarkers(LatLng startPoint, LatLng endPoint) {
        if (map != null) {
            map.addMarker(new MarkerOptions().position(startPoint)
                    .title("Start Point")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            map.addMarker(new MarkerOptions().position(endPoint)
                    .title("End Point")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    public void findDirections(LatLng startPoint, LatLng endPOint) {
        Map<String, LatLng> map = new HashMap<String, LatLng>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_START, startPoint);
        map.put(GetDirectionsAsyncTask.USER_CURRENT_END, endPOint);

        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
        asyncTask.execute(map);
    }

    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
        PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.BLACK);
        for (int i = 0; i < directionPoints.size(); i++) {
            rectLine.add(directionPoints.get(i));
        }
        map.addPolyline(rectLine);
    }

    private class GetCoordinates extends BaseAsyncRequest {
        private String sensorType;
        private String deviceId;
        private Exception exception;

        public GetCoordinates(String deviceId, String sensorType) {
            this.deviceId = deviceId;
            this.sensorType = sensorType;
        }

        @Override
        protected void doSetup() throws ApiException, JSONException {
            callerName = "GetCoordinates";

            serviceName = AppConstants.DB_SVC;
            endPoint = "readings";
            verb = "GET";

            // filter to only select the contacts in this group
            queryParams = new HashMap<>();
            queryParams.put("filter", "deviceId=" + deviceId);
            queryParams.put("filter", "sensor=" + sensorType);

            // need to include the API key and session token
            applicationApiKey = AppConstants.API_KEY;
            sessionToken = PrefUtil.getString(getActivity().getApplicationContext(), AppConstants.SESSION_TOKEN);
        }

        @Override
        protected void processResponse(String response) throws ApiException, JSONException {
            ReadingList readingList = ((ReadingList) ApiInvoker.deserialize(response, "", ReadingList.class));
            coordinates = new ArrayList<>(readingList.record.size());
            for (Reading reading: readingList.record) {
                coordinates.add(new LatLng(reading.getLat(), reading.getLong()));
            }
        }

        @Override
        protected void onCompletion(boolean success) {
            if (success) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates.get(0), 16));
                addMarkers(coordinates.get(0), coordinates.get(coordinates.size() - 1));
                findDirections(coordinates.get(0), coordinates.get(coordinates.size() - 1));
            } else {
                displayLoginIfNeeded(getActivity(), exception);
            }
        }

        @Override
        protected void onError(Exception e) {
            super.onError(e);
            this.exception = e;
        }
    }

    private class GetPatientInfo extends BaseAsyncRequest {
        private int patientId;
        private Exception exception;

        public GetPatientInfo(int patientId) {
            this.patientId = patientId;
        }

        @Override
        protected void doSetup() throws ApiException, JSONException {
            callerName = "GetPatientInfo";

            serviceName = AppConstants.DB_SVC;
            endPoint = "resident";
            verb = "GET";

            // filter to only select the contacts in this group
            queryParams = new HashMap<>();
            queryParams.put("filter", "resId=" + patientId);

            // need to include the API key and session token
            applicationApiKey = AppConstants.API_KEY;
            sessionToken = PrefUtil.getString(getActivity().getApplicationContext(), AppConstants.SESSION_TOKEN);
        }

        @Override
        protected void processResponse(String response) throws ApiException, JSONException {
            user = ((ResidentList) ApiInvoker.deserialize(response, "", ResidentList.class)).record.get(0);
        }

        @Override
        protected void onCompletion(boolean success) {
            if (success) {
                populateScreen();
            } else {
                displayLoginIfNeeded(getActivity(), exception);
            }
        }

        @Override
        protected void onError(Exception e) {
            super.onError(e);
            this.exception = e;
        }
    }

    private void populateScreen() {
        if (checkUIisOK()) {
            Glide.with(getActivity())
                    .load(user.photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .into(profileImage);
            profileName.setText(user.firstName + " " + user.lastName);
            profileLocation.setText(user.location);
        }
    }

    private boolean checkUIisOK() {
        return getActivity() != null && !getActivity().isFinishing() && getView() != null;
    }

}
