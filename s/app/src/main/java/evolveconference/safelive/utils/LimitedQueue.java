package evolveconference.safelive.utils;

import com.jjoe64.graphview.series.DataPoint;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by andrei on 26/03/16.
 */
public class LimitedQueue<E> extends CopyOnWriteArrayList<E> implements Serializable {

    private int mLimit;

    public LimitedQueue(int limit) {
        mLimit = limit;
    }

    public LimitedQueue(LimitedQueue limitedQueue) {
        super(limitedQueue);
    }

    @Override
    public boolean add(E object) {
        if (size() + 1 > mLimit) {
            remove(0);
        }
        return super.add(object);
    }

    public DataPoint[] getDataPoints() {
        DataPoint[] dataPoints = new DataPoint[size()];
        for (int i = 0; i < size(); i++) {
            dataPoints[i] = new DataPoint(i, (Float) get(i));
        }
        return dataPoints;
    }
}
