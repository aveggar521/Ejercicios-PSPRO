import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Washer implements Runnable {
    private final DishRack rack;

    public Washer(DishRack rack) {
        this.rack = rack;
    }

    @Override
    public void run() {
        Random random = new Random();
        int counter = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        try {
            while (!Thread.currentThread().isInterrupted()) {
                Dish dish = new Dish(counter++);
                int washTime = random.nextInt(5) + 4;
                Thread.sleep(washTime * 1000L);

                rack.putClean(dish);
                String time = LocalTime.now().format(formatter);
                System.out.printf("[%s] [%s] friega plato con número de serie %d\n",
                        time, Thread.currentThread().getName(), dish.getSerialNumber());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}