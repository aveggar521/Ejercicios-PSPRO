import java.util.concurrent.Semaphore;

public class MarcoPolo {
    private static final int TOTAL_ROUNDS = 10;
    private final Semaphore marcoSemaphore = new Semaphore(1);
    private final Semaphore poloSemaphore = new Semaphore(0);

    public void shoutMarco() {
        try {
            for (int i = 1; i <= TOTAL_ROUNDS; i++) {
                marcoSemaphore.acquire();
                System.out.printf("Ronda %d -> [Marco]: Marco%n", i);
                poloSemaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shoutPolo() {
        try {
            for (int i = 1; i <= TOTAL_ROUNDS; i++) {
                poloSemaphore.acquire();
                System.out.printf("Ronda %d -> [Polo]: Polo%n", i);
                marcoSemaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        MarcoPolo game = new MarcoPolo();

        Thread marcoThread = new Thread(game::shoutMarco, "Hilo-Marco");
        Thread poloThread = new Thread(game::shoutPolo, "Hilo-Polo");

        marcoThread.start();
        poloThread.start();

        try {
            marcoThread.join();
            poloThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Juego Marco / Polo finalizado.");
    }
}