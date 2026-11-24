import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclistStageApp {
    private static final int CYCLIST_COUNT = 10;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final CyclicBarrier gasStationStartBarrier;
    private final CyclicBarrier innBarrier;
    private final CyclicBarrier gasStationEndBarrier;

    public CyclistStageApp() {
        this.gasStationStartBarrier = new CyclicBarrier(CYCLIST_COUNT, () -> {
            String timestamp = LocalTime.now().format(TIME_FORMATTER);
            String threadName = Thread.currentThread().getName();
            System.out.printf("[%s] (%s) Comienza la etapa%n", timestamp, threadName);
        });

        this.innBarrier = new CyclicBarrier(CYCLIST_COUNT, () -> {
            String timestamp = LocalTime.now().format(TIME_FORMATTER);
            String threadName = Thread.currentThread().getName();
            System.out.printf("[%s] (%s) De vuelta a casa%n", timestamp, threadName);
        });

        this.gasStationEndBarrier = new CyclicBarrier(CYCLIST_COUNT, () -> {
            String timestamp = LocalTime.now().format(TIME_FORMATTER);
            String threadName = Thread.currentThread().getName();
            System.out.printf("[%s] (%s) Etapa finalizada%n", timestamp, threadName);
        });
    }

    private void log(String cyclistName, String message) {
        String timestamp = LocalTime.now().format(TIME_FORMATTER);
        System.out.printf("[%s] %s: %s%n", timestamp, cyclistName, message);
    }

    private void sleepRandom(int minSeconds, int maxSeconds) throws InterruptedException {
        Random random = new Random();
        int seconds = random.nextInt(maxSeconds - minSeconds + 1) + minSeconds;
        Thread.sleep(seconds * 1000L);
    }

    static class CyclistTask implements Runnable {
        private final String cyclistName;
        private final CyclistStageApp stage;

        public CyclistTask(String cyclistName, CyclistStageApp stage) {
            this.cyclistName = cyclistName;
            this.stage = stage;
        }

        @Override
        public void run() {
            try {
                stage.log(cyclistName, "Sale de su casa hacia la gasolinera.");
                stage.sleepRandom(1, 3);
                stage.log(cyclistName, "Llega a la gasolinera.");
                stage.gasStationStartBarrier.await();

                stage.sleepRandom(5, 10);
                stage.log(cyclistName, "Llega a la venta.");
                stage.innBarrier.await();

                stage.sleepRandom(5, 10);
                stage.log(cyclistName, "Llega de vuelta a la gasolinera.");
                stage.gasStationEndBarrier.await();

                stage.sleepRandom(1, 3);
                stage.log(cyclistName, "Ya en casa");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (BrokenBarrierException e) {
                stage.log(cyclistName, "Barrera rota durante la etapa.");
            }
        }
    }

    public static void main(String[] args) {
        CyclistStageApp stage = new CyclistStageApp();
        Thread[] cyclistThreads = new Thread[CYCLIST_COUNT];

        for (int i = 0; i < CYCLIST_COUNT; i++) {
            String name = "Ciclista-" + (i + 1);
            cyclistThreads[i] = new Thread(new CyclistTask(name, stage), name);
            cyclistThreads[i].start();
        }

        for (Thread thread : cyclistThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Todos los ciclistas han finalizado la jornada.");
    }
}