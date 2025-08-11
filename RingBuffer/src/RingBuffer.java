import java.util.Arrays;

public class RingBuffer<E> {

    private final E[] data;
    private int readerIndex=0;
    private int writerIndex=-1;
    private final int capacity;



    @SuppressWarnings("unchecked")
    public RingBuffer(int capacity) {
        this.capacity = capacity;
        this.data = (E[]) new Object[this.capacity];
    }

    public void offer(E element)
    {
        boolean isFull = (writerIndex - readerIndex) +1 == capacity;
        int nextWriterIndex=0;
        if(!isFull)
        {
            nextWriterIndex = writerIndex + 1;
            data[nextWriterIndex%capacity] = element;
            writerIndex++;
        }
    }


    public E poll()
    {
        boolean isFull = writerIndex<readerIndex;
        if(!isFull)
        {
            E nextValue = data[readerIndex%capacity];
           readerIndex++;
           return nextValue;
        }

        return null;
    }


    public String toString() {
        return Arrays.toString(data);
    }
}