import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Customer implements Runnable {
    private final int customerId;
    private final CheckoutArea checkoutArea;
    private final DateTimeFormatter timeFormatter;

    public Customer(int customerId, CheckoutArea checkoutArea) {
        this.customerId = customerId;
        this.checkoutArea = checkoutArea;
        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    }

    @Override
    public void run() {
        try {
            String arrivalTime = LocalTime.now().format(timeFormatter);
            Random random = new Random();
            int shoppingSeconds = random.nextInt(3) + 1;
            Thread.sleep(shoppingSeconds * 1000L);

            checkoutArea.processCustomer(customerId, arrivalTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}