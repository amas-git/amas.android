package s.a.m.a.cs;

/**
 * Created by amas on 4/12/16.
 */
public class CircularBuffer<T> {
    T[] buffer = null;
    int head = 0; // the index of the first element
    int tail = 0; // the index of the very last element, this element could not be used
    int size = 0; // current element number

    public CircularBuffer(int size) {
        buffer = (T[])new Object[size+1];
    }

    /**
     * Put the elment to the buffer tail
     * @param elem
     */
    public void put(T elem) {
        T droped = null;

        int index = tail % buffer.length;
        droped = buffer[index];
        buffer[index] = elem;
        tail = (tail + 1) % buffer.length;

        if(tail == head) {
            head = (head+1) % buffer.length;
        }

        if(size < buffer.length - 1) {
            size+=1;
        } else {
            onDropElement(droped);
        }
    }

    /**
     * Called when the element droped
     * @param droped
     */
    public void onDropElement(T droped) {

    }

    /**
     * Get the nth element
     * @param nth
     * @return
     */
    public T get(int nth) {
        int index = (head+nth) % buffer.length;
        if(index == tail) {
            throw new IndexOutOfBoundsException("CircularBuffer get " + nth +" is invalidate position!");
        }
        return buffer[index];
    }

    /**
     * Get the first element
     * @return
     */
    public T first() {
        return buffer[head];
    }

    /**
     * The buffer is full?
     * @return
     */
    public boolean isFull() {
        return buffer.length-1 <= size;
    }

    @Override
    public String toString() {
        return dump();
    }

    protected String dump() {
        StringBuilder sb = new StringBuilder();

        for(int i=0; i<size; ++i) {
            String elem = "";
            if(i == tail) {
                elem = "*";
            } else if(i == head) {
                elem += "[ " + buffer[i] +" ]" ;
            } else {
                elem += "" + buffer[i];
            }

            sb.append(String.format("%10s, ", elem));
        }
        return String.format(" (head: %d tail: %d full: %b) %s", head, tail, isFull(), sb.toString());
    }

    public static void TEST() {
        CircularBuffer<String> b = new CircularBuffer<String>(4);
        for(int i=0; i<20; ++i) {
            b.put(""+i);
            System.out.print(""+b+"\n");
        }
    }

    public CircularBuffer<T> copy() {
        CircularBuffer<T> copy = new CircularBuffer<>(buffer.length);
        for(int i=0; i<size;++i) {
            copy.put(get(i));
        }
        return copy;
    }

    public int size() {
        return size;
    }
}
