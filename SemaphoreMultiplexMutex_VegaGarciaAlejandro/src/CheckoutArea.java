import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class CheckoutArea {
    private final Semaphore queueSemaphore;
    private final Semaphore mutexSemaphore;
    private final boolean[] availableRegisters;
    private final DateTimeFormatter timeFormatter;

    public CheckoutArea(int numberOfRegisters) {
        this.queueSemaphore = new Semaphore(numberOfRegisters, true);
        this.mutexSemaphore = new Semaphore(1, true);
        this.availableRegisters = new boolean[numberOfRegisters];
        for (int i = 0; i < numberOfRegisters; i++) {
            this.availableRegisters[i] = true;
        }
        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    }

    public void processCustomer(int customerId, String arrivalTime) throws InterruptedException {
        queueSemaphore.acquire();
        String accessTime = LocalTime.now().format(timeFormatter);

        int registerNumber = acquireRegister();
        String startServiceTime = LocalTime.now().format(timeFormatter);

        Random random = new Random();
        int checkoutSeconds = random.nextInt(4) + 1;
        Thread.sleep(checkoutSeconds * 1000L);

        String exitTime = LocalTime.now().format(timeFormatter);
        releaseRegister(registerNumber);
        queueSemaphore.release();

        System.out.printf("Cliente %02d | Llegada: %s | Acceso zona cajas: %s | Inicio en Caja %d: %s | Salida: %s%n",
                customerId, arrivalTime, accessTime, registerNumber, startServiceTime, exitTime);
    }

    private int acquireRegister() throws InterruptedException {
        mutexSemaphore.acquire();
        try {
            for (int i = 0; i < availableRegisters.length; i++) {
                if (availableRegisters[i]) {
                    availableRegisters[i] = false;
                    return i + 1;
                }
            }
            return -1;
        } finally {
            mutexSemaphore.release();
        }
    }

    private void releaseRegister(int registerNumber) throws InterruptedException {
        mutexSemaphore.acquire();
        try {
            availableRegisters[registerNumber - 1] = true;
        } finally {
            mutexSemaphore.release();
        }
    }
}