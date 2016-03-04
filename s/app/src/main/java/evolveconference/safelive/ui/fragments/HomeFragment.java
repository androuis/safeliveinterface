package evolveconference.safelive.ui.fragments;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;

import java.util.HashMap;

import evolveconference.safelive.R;
import evolveconference.safelive.dfapi.ApiException;
import evolveconference.safelive.dfapi.ApiInvoker;
import evolveconference.safelive.dfapi.BaseAsyncRequest;
import evolveconference.safelive.model.Staff;
import evolveconference.safelive.model.StaffList;
import evolveconference.safelive.utils.AppConstants;
import evolveconference.safelive.utils.ComponentUtils;
import evolveconference.safelive.utils.PrefUtil;

public class HomeFragment extends AlertFragment {

    private static final String KEY_STAFF_ID = "staff_id";
    private static final String KEY_STAFF_OBJECT = "staff_object";
    private Staff staff;
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
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.home_title);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getStaffInfo != null) {
            getStaffInfo.cancel(true);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments().containsKey(KEY_STAFF_ID)) {
            getStaffInfo = new GetStaffInfo(getArguments().getInt(KEY_STAFF_ID));
            getStaffInfo.execute();
        } else {
            staff = getArguments().getParcelable(KEY_STAFF_OBJECT);
            populateScreen();
        }
    }

    private class GetStaffInfo extends BaseAsyncRequest {
        private int staffId;

        public GetStaffInfo(int staffId) {
            this.staffId = staffId;
        }

        @Override
        protected void doSetup() throws ApiException, JSONException {
            callerName = "GetStaffInfo";

            serviceName = AppConstants.DB_SVC;
            endPoint = "staff";
            verb = "GET";

            // filter to only select the contacts in this group
            queryParams = new HashMap<>();
            queryParams.put("filter", "staffid=" + staffId);

            // need to include the API key and session token
            applicationApiKey = AppConstants.API_KEY;
            sessionToken = PrefUtil.getString(getActivity().getApplicationContext(), AppConstants.SESSION_TOKEN);
        }

        @Override
        protected void processResponse(String response) throws ApiException, JSONException {
            staff = ((StaffList) ApiInvoker.deserialize(response, "", StaffList.class)).record.get(0);
        }

        @Override
        protected void onCompletion(boolean success) {
            if (!isCancelled()) {
                if (success) {
                    populateScreen();
                } else {
                    displayLoginIfNeeded(getActivity(), exception);
                }
            }
        }
    }

    private void populateScreen() {
        if (ComponentUtils.checkUIisOK(this)) {
            headerCardView.setVisibility(View.VISIBLE);
            Glide.with(getActivity())
                    .load(staff.photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .into(profileImage);
            username.setText(getString(R.string.first_and_last_names, staff.firstName, staff.lastName));
            role.setText(staff.position);
        }
    }
}
