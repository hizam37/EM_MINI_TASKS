package DivisionOfThreads;


public class DivisibleByTwoThread extends Thread {
    public void run() {
        for(int i = 1; i <= 5; ++i) {
            if (i % 2 == 0) {
                System.out.println("DivisibleByTwoThread is running\n" + i + " is divisible by 2");
            }
        }

    }
}
