package evolveconference.safelive.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.SafeLiveApplication;
import evolveconference.safelive.model.Patient;
import evolveconference.safelive.ui.activities.MainActivity;
import evolveconference.safelive.ui.view.CircleLayout;
import evolveconference.safelive.ui.view.CircleLayout.OnCenterClickListener;
import evolveconference.safelive.ui.view.CircleLayout.OnItemClickListener;
import evolveconference.safelive.ui.view.CircleLayout.OnItemSelectedListener;
import evolveconference.safelive.ui.view.CircleLayout.OnRotationFinishedListener;

public class CircleDashboardFragment extends Fragment implements OnItemSelectedListener,
        OnItemClickListener, OnRotationFinishedListener, OnCenterClickListener {

    private static final String PATIENT_ID = "id";
    @Bind(R.id.main_circle_layout) CircleLayout circleMenu;
    @Bind(R.id.main_selected_textView) TextView selectedTextView;

    private SafeLiveApplication app;
    private int id = -1;
    private Patient patient;

    public CircleDashboardFragment() {
    }

    public static CircleDashboardFragment newInstance(int id) {
        Bundle b = new Bundle();
        b.putInt(PATIENT_ID, id);
        CircleDashboardFragment f = new CircleDashboardFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            id = b.getInt(PATIENT_ID, -1);
        }
        app = (SafeLiveApplication) getActivity().getApplication();

        patient = app.findPatientById(id);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.dashboard_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_circle_dashboard, container, false);
        ButterKnife.bind(this, v);

        // Set listeners
        circleMenu.setOnItemSelectedListener(this);
        circleMenu.setOnItemClickListener(this);
        circleMenu.setOnRotationFinishedListener(this);
        circleMenu.setOnCenterClickListener(this);
        circleMenu.setSelect(0);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onItemSelected(View view, int position, String name) {
        selectedTextView.setText(name);
    }

    private int[] resource = new int[]{ R.drawable.blood_pressure_2, R.drawable.audio_2, R.drawable.respiratory_2,
            R.drawable.activity_2, R.drawable.oximetry_2, R.drawable.heart_status_2, R.drawable.temperature_2 };

    @Override
    public void onItemClick(View view, int position, String name) {
        selectedTextView.setText(name);
        circleMenu.setCenterImage(resource[position], randomColor(position));
    }

    private int randomColor(int i) {
        switch (i % 3) {
            case 0:
                return R.color.wheel_red;
            case 1:
                return R.color.wheel_green;
            case 2:
                return R.color.wheel_orange;
            default:
                return R.color.wheel_green;
        }
    }

    @Override
    public void onRotationFinished(View view, String name) {
        Animation animation = new RotateAnimation(0, 360, view.getWidth() / 2,
                view.getHeight() / 2);
        animation.setDuration(250);
        view.startAnimation(animation);
    }

    @Override
    public void onCenterClick() {
        ((MainActivity) getActivity()).showFragment(new StatisticsFragment());
    }

}
