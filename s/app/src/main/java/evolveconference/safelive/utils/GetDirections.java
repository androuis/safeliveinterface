package evolveconference.safelive.utils;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Map;

import evolveconference.safelive.ui.fragments.LocationFragment;

/**
 * Created by abacalu on 2/18/2016.
 */
public class GetDirections extends AsyncTask<Map<String, LatLng>, Object, ArrayList> {
    public static final String USER_CURRENT_START = "user_current_start";
    public static final String USER_CURRENT_END = "user_current_end";
    public static final String DIRECTIONS_MODE = "directions_mode";
    private LocationFragment locationFragment;
    private Exception exception;
    private ProgressDialog progressDialog;

    public GetDirections(LocationFragment locationFragment)
    {
        super();
        this.locationFragment = locationFragment;
    }

    public void onPreExecute()
    {
        progressDialog = new ProgressDialog(locationFragment.getActivity());
        progressDialog.setMessage("Calculating directions");
        progressDialog.show();
    }

    @Override
    public void onPostExecute(ArrayList result)
    {
        if (!isCancelled()) {
            progressDialog.dismiss();
            if (exception == null)
            {
                locationFragment.handleGetDirectionsResult(result);
            }
            else
            {
                processException();
            }
        }
    }

    @Override
    protected ArrayList doInBackground(Map<String, LatLng>... params)
    {
        Map<String, LatLng> paramMap = params[0];
        try
        {
            LatLng fromPosition = paramMap.get(USER_CURRENT_START);
            LatLng toPosition = paramMap.get(USER_CURRENT_END);
            GMapV2Direction md = new GMapV2Direction();
            Document doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);
            ArrayList directionPoints = md.getDirection(doc);
            return directionPoints;
        }
        catch (Exception e)
        {
            exception = e;
            return null;
        }
    }

    private void processException()
    {
        Toast.makeText(locationFragment.getActivity(), "No data", Toast.LENGTH_LONG).show();
    }
}
