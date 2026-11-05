import java.util.Scanner;
import java.util.SortedMap;

public class Main {
    private static final int WAITING_SECOND = 5;
    public static void main(String[] args) throws InterruptedException{
        String threadName = Thread.currentThread().getName();
        Scanner sc = new Scanner(System.in);
        System.out.printf("%s: Pulsa intro para comenzar el cronómetro\n", threadName);

    }
}