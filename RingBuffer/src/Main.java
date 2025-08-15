import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        RingBuffer<Integer> cb = new RingBuffer<>(4);
        executorService.execute(() -> {

            cb.offer(1);
            cb.offer(2);
            cb.offer(3);
            cb.offer(4);
            cb.offer(5);

            System.out.println(cb.isEmpty());
        });


        executorService.execute(() -> {
            System.out.println(cb.poll());
            System.out.println(cb.poll());
            System.out.println(cb.poll());
            System.out.println(cb.poll());

        });
        executorService.shutdown();
    }
}