import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GiftFundApp {
    private static final int TARGET_AMOUNT = 10;
    private static final int STUDENT_COUNT = 5;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final CountDownLatch latch = new CountDownLatch(TARGET_AMOUNT);
    private final Lock lock = new ReentrantLock();
    private int totalAmount = 0;

    public void contribute(String studentName, int amount) {
        lock.lock();
        try {
            totalAmount += amount;
            int currentTotal = totalAmount;
            String timestamp = LocalTime.now().format(TIME_FORMATTER);
            System.out.printf("[%s] %s aporta %d € (Total recaudado: %d €)%n",
                    timestamp, studentName, amount, currentTotal);

            for (int i = 0; i < amount; i++) {
                latch.countDown();
            }
        } finally {
            lock.unlock();
        }
    }

    public void waitAndBuyGift(String brotherName) {
        String startTime = LocalTime.now().format(TIME_FORMATTER);
        System.out.printf("[%s] %s: Estoy esperando a que se recauden los %d € necesarios para comprar el regalo.%n",
                startTime, brotherName, TARGET_AMOUNT);

        try {
            latch.await();
            String buyTime = LocalTime.now().format(TIME_FORMATTER);
            System.out.printf("[%s] %s: ¡Ya se han recaudado al menos %d € (Total: %d €)! Me dirijo hacia la tienda para comprar el regalo.%n",
                    buyTime, brotherName, TARGET_AMOUNT, getTotalAmount());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private int getTotalAmount() {
        lock.lock();
        try {
            return totalAmount;
        } finally {
            lock.unlock();
        }
    }

    static class StudentTask implements Runnable {
        private final String studentName;
        private final GiftFundApp fund;

        public StudentTask(String studentName, GiftFundApp fund) {
            this.studentName = studentName;
            this.fund = fund;
        }

        @Override
        public void run() {
            Random random = new Random();
            int delaySeconds = random.nextInt(8) + 3;
            int amount = random.nextInt(4) + 2;

            try {
                Thread.sleep(delaySeconds * 1000L);
                fund.contribute(studentName, amount);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        GiftFundApp fund = new GiftFundApp();

        Thread brotherThread = new Thread(() -> fund.waitAndBuyGift("Hermano mayor"), "Hilo-Hermano");
        brotherThread.start();

        Thread[] studentThreads = new Thread[STUDENT_COUNT];
        for (int i = 0; i < STUDENT_COUNT; i++) {
            String name = "Alumno-" + (i + 1);
            studentThreads[i] = new Thread(new StudentTask(name, fund), name);
            studentThreads[i].start();
        }

        try {
            brotherThread.join();
            for (Thread studentThread : studentThreads) {
                studentThread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}