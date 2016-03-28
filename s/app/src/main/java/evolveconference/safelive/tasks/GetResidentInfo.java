package evolveconference.safelive.tasks;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

import evolveconference.safelive.SafeLiveApplication;
import evolveconference.safelive.callbacks.tasks.GetResidentInfoCallback;
import evolveconference.safelive.dfapi.ApiException;
import evolveconference.safelive.dfapi.ApiInvoker;
import evolveconference.safelive.dfapi.BaseAsyncRequest;
import evolveconference.safelive.model.Resident;
import evolveconference.safelive.model.ResidentList;
import evolveconference.safelive.utils.AppConstants;
import evolveconference.safelive.utils.PrefUtil;

/**
 * Created by andrei on 05/03/16.
 */
public class GetResidentInfo extends BaseAsyncRequest {
    private final GetResidentInfoCallback getResidentInfoCallback;
    private int mResidentId;
    private Resident mResident;

    public GetResidentInfo(int residentId, GetResidentInfoCallback getResidentInfoCallback) {
        this.mResidentId = residentId;
        this.getResidentInfoCallback = getResidentInfoCallback;
    }

    @Override
    protected void doSetup() throws ApiException, JSONException {
        callerName = "GetResidentInfo";

        serviceName = AppConstants.DB_SVC;
        endPoint = "resident";
        verb = "GET";

        // filter to only select the contacts in this group
        queryParams = new HashMap<>();
        queryParams.put("filter", "resid=" + mResidentId);

        // need to include the API key and session token
        applicationApiKey = AppConstants.API_KEY;
        sessionToken = PrefUtil.getString(SafeLiveApplication.instance.getApplicationContext(), AppConstants.SESSION_TOKEN);
    }

    @Override
    protected void processResponse(String response) throws Exception {
        List<Resident> residentList = ((ResidentList) ApiInvoker.deserialize(response, "", ResidentList.class)).record;
        if (residentList != null) {
            mResident = residentList.get(0);
        }
    }

    @Override
    protected void onCompletion(boolean success) {
        if (!isCancelled()) {
            if (success) {
                getResidentInfoCallback.populateScreen(mResident);
            } else {
                displayLoginIfNeeded(SafeLiveApplication.instance.getApplicationContext(), exception);
            }
        }
    }
}
