import DivisionOfThreads.DivisibleByTwoThread;
import DivisionOfThreads.NonDivisibleByTwoThread;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        DivisibleByTwoThread divisibleByTwoThread = new DivisibleByTwoThread();
        NonDivisibleByTwoThread nonDivisibleByTwoThread = new NonDivisibleByTwoThread();
        divisibleByTwoThread.start();
        divisibleByTwoThread.join();
        nonDivisibleByTwoThread.start();
        nonDivisibleByTwoThread.join();
    }

}