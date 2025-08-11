import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        RingBuffer<String> circle = new RingBuffer<>(4);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        executorService.execute(() -> {
            circle.offer("A");
            circle.offer("B");
            circle.offer("C");
            circle.offer("D");
        });
        executorService.execute(() -> {
            System.out.println(circle.poll());
            System.out.println(circle.poll());
            System.out.println(circle.poll());
            System.out.println(circle.poll());
        });

        executorService.shutdown();
    }

}
