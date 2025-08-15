import java.util.Arrays;

public class RingBuffer<E> {

    private final E[] data;
    private int readerIndex=0;
    private int writerIndex=-1;
    private final int capacity;
    private int size=0;
    private boolean closed;


    @SuppressWarnings("unchecked")
    public RingBuffer(int capacity) {
        this.capacity = capacity;
        this.data = (E[]) new Object[this.capacity];
    }

    public void offer(E element)
    {
        if (closed) {
            throw new IllegalStateException("Buffer is closed");
        }
        boolean isFull =  (writerIndex + 1) % capacity == writerIndex;
        int nextWriterIndex;
        if(!isFull)
        {
            nextWriterIndex = writerIndex + 1;
            data[nextWriterIndex%capacity] = element;
            writerIndex++;
            size++;
        }
    }


    public E poll()
    {
        boolean isFull = writerIndex==readerIndex;
        if(!isFull)
        {
            E nextValue = data[readerIndex%capacity];
            readerIndex++;
            return nextValue;
        }
        size--;
        return null;
    }

    public boolean isEmpty()
    {
        return size==0;
    }

    public void clean()
    {
        writerIndex=-1;
        readerIndex=0;
    }

    public boolean close()
    {
        return closed=true;
    }

    public String toString() {
        return Arrays.toString(data);
    }
}