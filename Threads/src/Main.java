import Division.Division;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        Division division = new Division();
        Thread t1 = new Thread(() -> {
            for(int i=0;i<10;i++) {
                division.displayDivisibleByTwo(i);
            }
        });

        Thread t2 = new Thread(() -> {
            for(int i=0;i<10;i++) {
                division.displayNonDivisibleByTwo(i);
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

    }

}