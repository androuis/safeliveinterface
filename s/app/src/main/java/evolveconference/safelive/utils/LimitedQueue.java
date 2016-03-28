package evolveconference.safelive.utils;

import java.util.LinkedList;

/**
 * Created by andrei on 26/03/16.
 */
public class LimitedQueue<E> extends LinkedList<E> {

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
            remove();
        }
        return super.add(object);
    }
}
