package evolveconference.safelive.ui.fragments;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import evolveconference.safelive.R;
import evolveconference.safelive.model.NursingHome;
import evolveconference.safelive.model.Staff;
import evolveconference.safelive.utils.ComponentUtils;
import evolveconference.safelive.utils.GetStaffInfo;
import evolveconference.safelive.utils.GetStaffInfoCallback;

public class HomeFragment extends AlertFragment implements GetStaffInfoCallback {

    private static final String KEY_STAFF_ID = "staff_id";
    private static final String KEY_STAFF_OBJECT = "staff_object";
    private Staff staff;
    private NursingHome nursingHome;
    private GetStaffInfo getStaffInfo;

    public static HomeFragment newInstance(Staff staff) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_STAFF_OBJECT, staff);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    public static HomeFragment newInstance(int staffId) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_STAFF_ID, staffId);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.home_title);
        if (getArguments().containsKey(KEY_STAFF_ID)) {
            getStaffInfo = new GetStaffInfo(getArguments().getInt(KEY_STAFF_ID, 0), this);
            getStaffInfo.execute();
        } else {
            staff = getArguments().getParcelable(KEY_STAFF_OBJECT);
            populateScreen(staff, nursingHome);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getStaffInfo != null) {
            getStaffInfo.cancel(true);
        }
    }

    public void populateScreen(Staff staff, NursingHome nursingHome) {
        if (ComponentUtils.checkUIisOK(this)) {
            headerCardView.setVisibility(View.VISIBLE);
            this.staff = staff;
            this.nursingHome = nursingHome;
            Picasso.with(getActivity())
                    .load(this.staff.photo)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .placeholder(android.R.drawable.ic_menu_myplaces)
                    .into(profileImage);
            username.setText(getString(R.string.first_and_last_names, this.staff.firstName, this.staff.lastName));
            role.setText(getString(R.string.role_at, this.staff.position));
            //address.setText(this.nursingHome.nursinghomeaddress);
        }
    }
}
