package evolveconference.safelive.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by andrei on 28/02/16.
 */
public class ComponentUtils {

    public static boolean checkUIisOK(Fragment fragment) {
        return fragment != null && checkUIisOK(fragment.getActivity()) && fragment.getView() != null;
    }

    public static boolean checkUIisOK(FragmentActivity activity) {
        return activity != null && !activity.isFinishing();
    }
}
