import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Cronometro implements Runnable {

    @Override
    public void run() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String currentTime = LocalTime.now().format(formatter);
                System.out.printf("[%s] %s\n", Thread.currentThread().getName(), currentTime);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.printf("[%s] Me han parado\n", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        }
    }
    public static void main(String[] args) {
        System.out.println("Pulsa Intro para comenzar el cronómetro.");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            reader.readLine();
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }

        Thread chronometerThread = new Thread(new Cronometro(), "Cronometro-Thread");
        chronometerThread.start();

        System.out.printf("[%s] Cronómetro en ejecución. Esperando 5 segundos.\n",
                Thread.currentThread().getName());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        chronometerThread.interrupt();

        try {
            chronometerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.printf("[%s] Cronometro terminado, hasta luego\n",
                Thread.currentThread().getName());
    }
}