import java.util.Random;

public class TerminalLogistica implements Runnable {
    private final String name;
    private final Central central;

    public TerminalLogistica(String name, Central central) {
        this.name = name;
        this.central = central;
    }

    @Override
    public void run() {
        Random random = new Random();

        for (int i = 1; i <= 10; i++) {
            int operationType = random.nextInt(3);

            if (operationType == 0) {
                System.out.printf("[%s] Operación %d/10: Intentando realizar transporte...\n", name, i);
                boolean success = central.tryTransport(name);
                if (!success) {
                    System.out.printf("[%s] Transporte fallido por falta de recursos. Esperando...\n", name);
                    try {
                        int waitTime = random.nextInt(401) + 100;
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } else if (operationType == 1) {
                System.out.printf("[%s] Operación %d/10: Solicitando reparación de camión...\n", name, i);
                central.repairTruck(name);
            } else {
                System.out.printf("[%s] Operación %d/10: Solicitando inventario de bahía...\n", name, i);
                central.performBayInventory(name);
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.printf("[%s] Todas las operaciones de la terminal han finalizado\n", name);
    }
}