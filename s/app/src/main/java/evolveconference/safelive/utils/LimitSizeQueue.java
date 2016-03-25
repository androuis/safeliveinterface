package evolveconference.safelive.utils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by abacalu on 3/25/2016.
 */
public class LimitSizeQueue<E> extends LinkedList<E> {
    private int mSize;

    public LimitSizeQueue(int size) {
        mSize = size;
    }

    @Override
    public boolean add(E o) {
        super.add(o);
        while (size() > mSize) { super.remove(); }
        return true;
    }
}
