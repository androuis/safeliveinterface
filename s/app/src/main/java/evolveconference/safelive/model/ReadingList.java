package evolveconference.safelive.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jjoe64.graphview.series.DataPoint;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import evolveconference.safelive.SafeLiveApplication;

/**
 * Created by abacalu on 2/21/2016.
 */
public class ReadingList {
    @JsonProperty("resource")
    public List<Reading> record = new ArrayList<>();

    public DataPoint[] getDataPoints() throws ParseException {
        Iterator<Reading> iterator = record.iterator();
        Reading previous = null;
        while (iterator.hasNext()) {
            if (previous == null) {
                previous = iterator.next();
            } else {
                Reading current = iterator.next();
                if (current.timestamp.equals(previous.timestamp)) {
                    iterator.remove();
                } else {
                    previous = current;
                }
            }
        }
        DataPoint[] result = new DataPoint[record.size()];
        int x = 0;
        for (int i = 0; i < record.size(); i++) {
            if (i == 0) {
                result[i] = new DataPoint(x, Double.valueOf(record.get(i).value));
            } else {
                x += (int) (SafeLiveApplication.anomalyTimestampFormatter.parse(record.get(i).timestamp).getTime() -
                                    SafeLiveApplication.anomalyTimestampFormatter.parse(record.get(i - 1).timestamp).getTime()) / 1000;
                result[i] = new DataPoint(x, Double.valueOf(record.get(i).value));
            }
        }
        return result;
    }
}
