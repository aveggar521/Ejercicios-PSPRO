import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PowerTablesCachedExecutorApp {
    private static final int MIN_BASE = 1;
    private static final int MAX_BASE = 10;
    private static final int MAX_EXPONENT = 10;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("mm:ss:SSS");

    private static class PowerTableTask implements Runnable {
        private final int baseNumber;
        private final Set<String> usedThreads;

        public PowerTableTask(int baseNumber, Set<String> usedThreads) {
            this.baseNumber = baseNumber;
            this.usedThreads = usedThreads;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            usedThreads.add(threadName);

            try {
                for (int exponent = 0; exponent <= MAX_EXPONENT; exponent++) {
                    long result = (long) Math.pow(baseNumber, exponent);
                    String timestamp = LocalTime.now().format(TIME_FORMATTER);
                    System.out.printf("[%s] (%s) %d ^ %d = %d%n",
                            timestamp, threadName, baseNumber, exponent, result);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        AtomicInteger threadCounter = new AtomicInteger(1);
        Set<String> usedThreads = ConcurrentHashMap.newKeySet();

        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable, "Cached-Worker-" + threadCounter.getAndIncrement());
            return thread;
        };

        ExecutorService executor = Executors.newCachedThreadPool(threadFactory);

        for (int base = MIN_BASE; base <= MAX_BASE; base++) {
            executor.submit(new PowerTableTask(base, usedThreads));
        }

        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.printf("Número máximo de hilos usados por el CachedThreadPool: %d%n", usedThreads.size());
    }
}