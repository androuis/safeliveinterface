package evolveconference.safelive.utils;

import org.json.JSONException;

import java.util.HashMap;

import evolveconference.safelive.SafeLiveApplication;
import evolveconference.safelive.dfapi.ApiException;
import evolveconference.safelive.dfapi.ApiInvoker;
import evolveconference.safelive.dfapi.BaseAsyncRequest;
import evolveconference.safelive.model.NursingHome;
import evolveconference.safelive.model.NursingHomeList;
import evolveconference.safelive.model.Staff;
import evolveconference.safelive.model.StaffList;

/**
 * Created by andrei on 05/03/16.
 */
public class GetStaffInfo extends BaseAsyncRequest {
    private final GetStaffInfoCallback getStaffInfoCallback;
    private int staffId;
    private Staff staff;
    private NursingHome nursingHome;

    public GetStaffInfo(int staffId, GetStaffInfoCallback getStaffInfoCallback) {
        this.staffId = staffId;
        this.getStaffInfoCallback = getStaffInfoCallback;
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
        sessionToken = PrefUtil.getString(SafeLiveApplication.instance.getApplicationContext(), AppConstants.SESSION_TOKEN);
    }

    @Override
    protected void processResponse(String response) throws Exception {
        staff = ((StaffList) ApiInvoker.deserialize(response, "", StaffList.class)).record.get(0);
        /*doSetup2(staff.nursinghomeid);
        processResponse2(call());*/
    }

    private void processResponse2(String response) throws ApiException {
        NursingHomeList nursingHomeList = ((NursingHomeList) ApiInvoker.deserialize(response, "", NursingHomeList.class));
        if (nursingHomeList.record.size() > 0) {
            nursingHome = ((NursingHomeList) ApiInvoker.deserialize(response, "", NursingHomeList.class)).record.get(0);
        }
    }

    private void doSetup2(int nursinghomeid) {
        callerName = "GetStaffInfo";

        serviceName = AppConstants.DB_SVC;
        endPoint = "nursinghomes";
        verb = "GET";

        // filter to only select the contacts in this group
        queryParams = new HashMap<>();
        queryParams.put("filter", "nursinghomeid=" + nursinghomeid);

        // need to include the API key and session token
        applicationApiKey = AppConstants.API_KEY;
        sessionToken = PrefUtil.getString(SafeLiveApplication.instance.getApplicationContext(), AppConstants.SESSION_TOKEN);
    }

    @Override
    protected void onCompletion(boolean success) {
        if (!isCancelled()) {
            if (success) {
                getStaffInfoCallback.populateScreen(staff, nursingHome);
            } else {
                displayLoginIfNeeded(SafeLiveApplication.instance.getApplicationContext(), exception);
            }
        }
    }
}
