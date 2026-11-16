import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Dryer implements Runnable {
    private final DishRack rack;

    public Dryer(DishRack rack) {
        this.rack = rack;
    }

    @Override
    public void run() {
        Random random = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        try {
            while (!Thread.currentThread().isInterrupted()) {
                Dish dish = rack.takeClean();
                int dryTime = random.nextInt(3) + 1;
                Thread.sleep(dryTime * 1000L);

                rack.putDry(dish);
                String time = LocalTime.now().format(formatter);
                System.out.printf("[%s] [%s] seca plato con número de serie %d\n",
                        time, Thread.currentThread().getName(), dish.getSerialNumber());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
