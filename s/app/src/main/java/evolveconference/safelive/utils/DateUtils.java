package evolveconference.safelive.utils;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class DateUtils {

    public static String getFormattedDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //2nd of march 2015
        int day = cal.get(Calendar.DATE);

        if (!((day > 10) && (day < 19)))
            switch (day % 10) {
                case 1:
                    return new SimpleDateFormat("d'st' MMM yyyy, hh'h'mm'm'").format(date);
                case 2:
                    return new SimpleDateFormat("d'nd' MMM yyyy, hh'h'mm'm'").format(date);
                case 3:
                    return new SimpleDateFormat("d'rd' MMM yyyy, hh'h'mm'm'").format(date);
                default:
                    return new SimpleDateFormat("d'th' MMM yyyy, hh'h'mm'm'").format(date);
            }
        return new SimpleDateFormat("d'th' MMM yyyy, hh'h'mm'm'").format(date);
    }
}
