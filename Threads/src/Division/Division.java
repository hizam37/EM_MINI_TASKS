package Division;

import java.util.List;
import java.util.stream.IntStream;

public class Division {

    public synchronized void displayDivisibleByTwo(int number)
    {
        IntStream.rangeClosed(1, number).filter(i -> i % 2 == 0).mapToObj(i -> "Numbers that are divisible by two are " + i).forEach(System.out::println);
    }

    public synchronized void displayNonDivisibleByTwo(int number)
    {
        IntStream.rangeClosed(1, number).filter(i -> i % 2 != 0).mapToObj(i -> "Numbers that are not divisible by two are " + i).forEach(System.out::println);
    }


}
