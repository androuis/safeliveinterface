package evolveconference.safelive.ui.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.dfapi.ApiException;
import evolveconference.safelive.dfapi.ApiInvoker;
import evolveconference.safelive.dfapi.BaseAsyncRequest;
import evolveconference.safelive.model.Anomaly;
import evolveconference.safelive.model.Reading;
import evolveconference.safelive.model.ReadingList;
import evolveconference.safelive.model.Resident;
import evolveconference.safelive.ui.view.Circle;
import evolveconference.safelive.ui.view.CircleAngleAnimation;
import evolveconference.safelive.utils.AppConstants;
import evolveconference.safelive.utils.ComponentUtils;
import evolveconference.safelive.utils.GetResidentInfo;
import evolveconference.safelive.utils.GetResidentInfoCallback;
import evolveconference.safelive.utils.PrefUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivitiesFragment extends Fragment implements GetResidentInfoCallback {
    private static final String ARG_RESIDENT_ID = "arg_resident_id";

    // Footsteps details
    private int mResidentId;
    @Bind(R.id.footsteps_value)
    TextView mFootstepsValue;
    @Bind(R.id.footsteps_value_afternoon)
    TextView mFootstepsValueAfternoon;
    @Bind(R.id.footsteps_value_evening)
    TextView mFootstepsValueEvening;
    @Bind(R.id.footsteps_value_midnight)
    TextView mFootstepsValueMidnight;
    @Bind(R.id.footsteps_value_morning)
    TextView mFootstepsValueMorning;
    @Bind(R.id.footsteps_value_night)
    TextView mFootstepsValueNight;
    @Bind(R.id.footsteps_value_noon)
    TextView mFootstepsValueNoon;
    @Bind(R.id.footsteps_progress_afternoon)
    ProgressBar mFootstepsProgressAfternoon;
    @Bind(R.id.footsteps_progress_evening)
    ProgressBar mFootstepsProgressEvening;
    @Bind(R.id.footsteps_progress_midnight)
    ProgressBar mFootstepsProgressMidnight;
    @Bind(R.id.footsteps_progress_morning)
    ProgressBar mFootstepsProgressMorning;
    @Bind(R.id.footsteps_progress_night)
    ProgressBar mFootstepsProgressNight;
    @Bind(R.id.footsteps_progress_noon)
    ProgressBar mFootstepsProgressNoon;
    // Resident details
    @Bind(R.id.resident_image)
    CircularImageView mResidentImage;
    @Bind(R.id.resident_name)
    TextView mResidentName;
    @Bind(R.id.resident_location)
    TextView mResidentLocation;
    @Bind(R.id.resident_activity_title)
    TextView mResidentActivityTitle;
    @Bind(R.id.resident_activity)
    TextView mResidentActivity;
    // Percent details
    @Bind(R.id.circle)
    Circle circle;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(getString(R.string.activity));
    }

    public static ActivitiesFragment newInstance(int param1) {
        ActivitiesFragment fragment = new ActivitiesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RESIDENT_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mResidentId = getArguments().getInt(ARG_RESIDENT_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CircleAngleAnimation animation = new CircleAngleAnimation(circle);
        animation.setDuration(1500);
        animation.setInterpolator(new DecelerateInterpolator());
        circle.startAnimation(animation);

        circle.setOnSeriesClickedListener(new Circle.OnSeriesClickedListener() {
            @Override
            public void onClick(Circle.Series series) {
                Toast.makeText(getActivity(), String.format("Series %s selected", series.getLabel()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        new GetResidentInfo(mResidentId, this).execute();
        new GetFootsteps().execute();
        initSeries();
    }

    private void initSeries() {
        circle.addSeries(new Circle.Series.SeriesBuilder().setValue(50).setLabel(getString(R.string.in_train))
                .setColor(getResources().getColor(R.color.series_1)).build());
        circle.addSeries(new Circle.Series.SeriesBuilder().setValue(80).setLabel(getString(R.string.in_car))
                .setColor(getResources().getColor(R.color.series_2)).build());
        circle.addSeries(new Circle.Series.SeriesBuilder().setValue(55).setLabel(getString(R.string.jumping))
                .setColor(getResources().getColor(R.color.series_3)).build());
        circle.addSeries(new Circle.Series.SeriesBuilder().setValue(100).setLabel(getString(R.string.running))
                .setColor(getResources().getColor(R.color.series_4)).build());
    }

    @Override
    public void populateScreen(Resident resident) {
        if (ComponentUtils.checkUIisOK(getActivity())) {
            new GetActivity().execute();
            Picasso.with(getActivity())
                    .load(resident.photo)
                    .resizeDimen(R.dimen.picture_size_small, R.dimen.picture_size_small)
                    .centerInside()
                    .placeholder(R.drawable.user_empty_placeholder)
                    .into(mResidentImage);
            mResidentLocation.setText(resident.location);
            mResidentName.setText(getString(R.string.first_and_last_names, resident.firstName, resident.lastName));
        }
    }

    private class GetActivity extends BaseAsyncRequest {
        private Reading reading;

        protected void doSetup() throws ApiException, JSONException {
            callerName = "GetActivity";

            serviceName = AppConstants.DB_SVC;
            endPoint = "readings";
            verb = "GET";

            queryParams = new LinkedHashMap<>();
            queryParams.put("filter", "(sensor%3D'ActivityRecognition')%20and%20(deviceid%3D(select%20serialnumber%20from%20existingdevices%20where%20serialnumber%3Ddeviceid%20and%20residentid%3D" + mResidentId + "))");
            queryParams.put("related", "existingdevices_by_deviceid");

            // need to include the API key and session token
            applicationApiKey = AppConstants.API_KEY;
            sessionToken = PrefUtil.getString(getActivity().getApplicationContext(), AppConstants.SESSION_TOKEN);
        }

        @Override
        protected void processResponse(String response) throws Exception {
            List<Reading> readingList = ((ReadingList) ApiInvoker.deserialize(response, "", ReadingList.class)).record;
            if (readingList != null) {
                reading = readingList.get(0);
            }
        }

        @Override
        protected void onCompletion(boolean success) {
            if (ComponentUtils.checkUIisOK(getActivity())) {
                if (success) {
                    mResidentActivityTitle.setText(getString(R.string.current_activity, mResidentName.getText().toString()));
                    mResidentActivity.setText("running");
                } else {
                    displayLoginIfNeeded(getActivity(), exception);
                }
            }
        }
    }

    private class GetFootsteps extends BaseAsyncRequest {
        private Reading reading;

        protected void doSetup() throws ApiException, JSONException {
            callerName = "GetFootsteps";

            serviceName = AppConstants.DB_SVC;
            endPoint = "readings";
            verb = "GET";

            queryParams = new LinkedHashMap<>();
            queryParams.put("filter", "(sensor%3D'Pedometer')%20and%20(deviceid%3D(select%20serialnumber%20from%20existingdevices%20where%20serialnumber%3Ddeviceid%20and%20residentid%3D" + mResidentId + "))");
            queryParams.put("limit", "1");
            queryParams.put("order", "readingtimestamp%20desc");
            queryParams.put("related", "existingdevices_by_deviceid");

            // need to include the API key and session token
            applicationApiKey = AppConstants.API_KEY;
            sessionToken = PrefUtil.getString(getActivity().getApplicationContext(), AppConstants.SESSION_TOKEN);
        }

        @Override
        protected void processResponse(String response) throws Exception {
            List<Reading> readingList = ((ReadingList) ApiInvoker.deserialize(response, "", ReadingList.class)).record;
            if (readingList != null) {
                reading = readingList.get(0);
            }
        }

        @Override
        protected void onCompletion(boolean success) {
            if (ComponentUtils.checkUIisOK(getActivity())) {
                if (success) {
                    populateScreen(reading);
                } else {
                    displayLoginIfNeeded(getActivity(), exception);
                }
            }
        }

        private void populateScreen(Reading reading) {
            int total = reading.getStepsTotal();
            mFootstepsValue.setText(String.valueOf(total));
            setFootsteps(mFootstepsValueAfternoon, mFootstepsProgressAfternoon, reading.getStepsAfternoon(), total);
            setFootsteps(mFootstepsValueEvening, mFootstepsProgressEvening, reading.getStepsEvening(), total);
            setFootsteps(mFootstepsValueMidnight, mFootstepsProgressMidnight, reading.getStepsMidnight(), total);
            setFootsteps(mFootstepsValueMorning, mFootstepsProgressMorning, reading.getStepsMorning(), total);
            setFootsteps(mFootstepsValueNight, mFootstepsProgressNight, reading.getStepsNight(), total);
            setFootsteps(mFootstepsValueNoon, mFootstepsProgressNoon, reading.getStepsNoon(), total);
        }

        private void setFootsteps(TextView footstepsValue, ProgressBar footstepsProgress, int footsteps, int footstepsTotal) {
            footstepsValue.setText(String.valueOf(footsteps));
            if (footsteps == 0) {
                footstepsTotal = footstepsProgress.getWidth();
                footsteps = 1;
            }
            footstepsProgress.setMax(footstepsTotal);
            footstepsProgress.setProgress(footsteps);
        }
    }
}
