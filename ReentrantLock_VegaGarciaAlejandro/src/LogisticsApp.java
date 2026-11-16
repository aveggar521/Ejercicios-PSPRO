import java.util.Arrays;
import java.util.List;

public class LogisticsApp {
    public static void main(String[] args) {
        List<String> initialTrucks = Arrays.asList("Camion-1", "Camion-2", "Camion-3");
        List<String> initialBays = Arrays.asList("Bahia-A", "Bahia-B");

        Central central = new Central(initialTrucks, initialBays);

        Thread terminal1 = new Thread(new TerminalLogistica("Terminal-Norte", central));
        Thread terminal2 = new Thread(new TerminalLogistica("Terminal-Sur", central));
        Thread terminal3 = new Thread(new TerminalLogistica("Terminal-Este", central));
        Thread terminal4 = new Thread(new TerminalLogistica("Terminal-Oeste", central));

        terminal1.start();
        terminal2.start();
        terminal3.start();
        terminal4.start();

        try {
            terminal1.join();
            terminal2.join();
            terminal3.join();
            terminal4.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Sistema logístico cerrado correctamente.");
    }
}
