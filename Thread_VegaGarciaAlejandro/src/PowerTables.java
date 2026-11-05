public class PowerTables implements Runnable {
    private final int baseNumber;

    public PowerTables(int baseNumber) {
        this.baseNumber = baseNumber;
    }

    @Override
    public void run() {
        try {
            for (int exponent = 0; exponent <= 10; exponent++) {
                long result = (long) Math.pow(baseNumber, exponent);
                System.out.printf("[Hilo %s] %d ^ %d = %d\n",
                        Thread.currentThread().getName(), baseNumber, exponent, result);
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            System.err.printf("El hilo para el numero %d fue interrumpido: %s\n", baseNumber, e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        System.out.println("Iniciando la generacion concurrente de tablas de potencias...\n");

        for (int i = 1; i <= 10; i++) {
            PowerTables task = new PowerTables(i);
            Thread thread = new Thread(task, "Thread-" + i);
            thread.start();
        }

        System.out.println("Todas las tareas de potencias han sido lanzadas.\n");
    }
}