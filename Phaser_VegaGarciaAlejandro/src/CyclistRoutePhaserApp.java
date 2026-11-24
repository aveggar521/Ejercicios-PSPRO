import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;

public class CyclistRoutePhaserApp {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int REGULAR_CYCLISTS = 4;

    private final Phaser phaser = new Phaser(1);
    private final AtomicBoolean leftGasStation = new AtomicBoolean(false);
    private final AtomicBoolean finishedRoute = new AtomicBoolean(false);

    private void log(String message) {
        System.out.printf("[%s] %s%n", LocalTime.now().format(TIME_FORMATTER), message);
    }

    private void sleepRandom(int minSeconds, int maxSeconds) throws InterruptedException {
        Random random = new Random();
        int seconds = random.nextInt(maxSeconds - minSeconds + 1) + minSeconds;
        Thread.sleep(seconds * 1000L);
    }

    public void runRegularCyclist(String name) {
        phaser.register();
        try {
            log(name + " sale de casa hacia la gasolinera.");
            sleepRandom(1, 3);
            log(name + " llega a la gasolinera.");
            phaser.arriveAndAwaitAdvance();

            log(name + " sale de la gasolinera hacia la venta.");
            sleepRandom(1, 3);
            log(name + " llega a la venta.");
            phaser.arriveAndAwaitAdvance();

            log(name + " sale de la venta hacia el destino final.");
            sleepRandom(1, 3);
            log(name + " llega a la gasolinera de vuelta / fin de ruta y se va a casa.");
            phaser.arriveAndDeregister();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            phaser.arriveAndDeregister();
        }
    }

    public void runNoWaitAfterGasStationCyclist(String name) {
        phaser.register();
        try {
            log(name + " sale de casa hacia la gasolinera.");
            sleepRandom(1, 3);
            log(name + " llega a la gasolinera.");
            phaser.arriveAndAwaitAdvance();

            log(name + " sale de la gasolinera y ya no esperará a nadie más.");
            phaser.arriveAndDeregister();

            sleepRandom(1, 4);
            log(name + " pasa por la venta a su ritmo y sigue hacia su casa.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            phaser.arriveAndDeregister();
        }
    }

    public void runLateToGasStationCyclist(String name) {
        try {
            sleepRandom(5, 7);
            log(name + " se levanta tarde y sale de casa.");

            if (leftGasStation.get()) {
                log(name + " llega tarde: el grupo ya salió de la gasolinera (ni lo esperaban), así que va directo a la venta donde lo esperarán.");
                int registeredPhase = phaser.register();
                if (registeredPhase < 2) {
                    phaser.arriveAndAwaitAdvance();
                }
                log(name + " llega a la venta y se reúne con el grupo.");
                phaser.arriveAndAwaitAdvance();

                log(name + " sale de la venta con el grupo hacia el tramo final.");
                sleepRandom(1, 3);
                log(name + " termina la ruta y se va a casa.");
                phaser.arriveAndDeregister();
            } else {
                phaser.register();
                log(name + " consigue llegar a tiempo a la gasolinera.");
                phaser.arriveAndAwaitAdvance();
                log(name + " llega a la venta.");
                phaser.arriveAndAwaitAdvance();
                log(name + " termina la ruta y se va a casa.");
                phaser.arriveAndDeregister();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void runTooLateCyclist(String name) {
        try {
            sleepRandom(8, 11);
            log(name + " intenta unirse a la ruta.");

            if (finishedRoute.get() || phaser.isTerminated()) {
                log(name + " llega demasiado tarde: los demás ya han llegado a la gasolinera y se han ido para casa.");
                return;
            }

            phaser.register();
            log(name + " logra engancharse al grupo.");
            phaser.arriveAndDeregister();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void coordinateRoute() {
        log("Coordinación de la ruta iniciada.");

        phaser.arriveAndAwaitAdvance();
        leftGasStation.set(true);
        log("Fase 1 completada: el grupo sale de la gasolinera.");

        phaser.arriveAndAwaitAdvance();
        log("Fase 2 completada: el grupo sale de la venta.");

        phaser.arriveAndAwaitAdvance();
        finishedRoute.set(true);
        phaser.arriveAndDeregister();
        log("Fase 3 completada: todos han terminado y se han ido para casa.");
    }

    public static void main(String[] args) {
        CyclistRoutePhaserApp app = new CyclistRoutePhaserApp();

        Thread coordinatorThread = new Thread(app::coordinateRoute, "Coordinador");
        coordinatorThread.start();

        Thread[] regularThreads = new Thread[REGULAR_CYCLISTS];
        for (int i = 0; i < REGULAR_CYCLISTS; i++) {
            String name = "Ciclista-" + (i + 1);
            regularThreads[i] = new Thread(() -> app.runRegularCyclist(name), name);
            regularThreads[i].start();
        }

        Thread noWaitCyclist = new Thread(() -> app.runNoWaitAfterGasStationCyclist("Ciclista-SinEspera"), "Ciclista-SinEspera");
        Thread lateGasCyclist = new Thread(() -> app.runLateToGasStationCyclist("Ciclista-TardeGasolinera"), "Ciclista-TardeGasolinera");
        Thread tooLateCyclist = new Thread(() -> app.runTooLateCyclist("Ciclista-MuyTarde"), "Ciclista-MuyTarde");

        noWaitCyclist.start();
        lateGasCyclist.start();
        tooLateCyclist.start();

        try {
            coordinatorThread.join();
            for (Thread t : regularThreads) {
                t.join();
            }
            noWaitCyclist.join();
            lateGasCyclist.join();
            tooLateCyclist.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Simulación de la ruta ciclista finalizada.");
    }
}