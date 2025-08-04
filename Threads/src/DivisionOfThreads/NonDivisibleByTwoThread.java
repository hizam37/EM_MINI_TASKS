package DivisionOfThreads;

public class NonDivisibleByTwoThread extends Thread {
    public void run() {
        for(int i = 1; i <= 5; ++i) {
            if (i % 2 != 0) {
                System.out.println("NonDivisibleByTwoThread is running\n" + i + " is not divisible by 2");
            }
        }

    }
}
