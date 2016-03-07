package evolveconference.safelive.utils;

import evolveconference.safelive.model.NursingHome;
import evolveconference.safelive.model.Staff;

/**
 * Created by andrei on 05/03/16.
 */
public interface GetStaffInfoCallback {

    void populateScreen(Staff staff, NursingHome nursingHome);
}
