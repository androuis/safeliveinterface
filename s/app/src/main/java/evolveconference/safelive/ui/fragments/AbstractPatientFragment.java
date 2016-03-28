package evolveconference.safelive.ui.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.model.Resident;
import evolveconference.safelive.utils.ComponentUtils;
import evolveconference.safelive.tasks.GetResidentInfo;
import evolveconference.safelive.callbacks.tasks.GetResidentInfoCallback;

public abstract class AbstractPatientFragment extends Fragment implements GetResidentInfoCallback {
    public static final String ARG_RESIDENT_ID = "arg_resident_id";

    protected int mResidentId;

    // Resident details
    @Bind(R.id.resident_image)
    CircularImageView mResidentImage;
    @Bind(R.id.resident_name)
    TextView mResidentName;
    @Bind(R.id.resident_location)
    TextView mResidentLocation;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(getString(R.string.sound));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mResidentId = getArguments().getInt(ARG_RESIDENT_ID);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        new GetResidentInfo(mResidentId, this).execute();
    }

    @Override
    public void populateScreen(Resident resident) {
        if (ComponentUtils.checkUIisOK(getActivity())) {
            if (!TextUtils.isEmpty(resident.photo)) {
                Picasso.with(getActivity())
                        .load(resident.photo)
                        .resizeDimen(R.dimen.picture_size_small, R.dimen.picture_size_small)
                        .centerInside()
                        .placeholder(R.drawable.user_empty_placeholder)
                        .into(mResidentImage);
            }
            mResidentLocation.setText(resident.location);
            mResidentName.setText(getString(R.string.first_and_last_names, resident.firstName, resident.lastName));
        }
    }

}
