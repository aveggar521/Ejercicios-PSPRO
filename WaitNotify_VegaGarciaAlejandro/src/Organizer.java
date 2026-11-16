import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Organizer implements Runnable {
    private final DishRack rack;

    public Organizer(DishRack rack) {
        this.rack = rack;
    }

    @Override
    public void run() {
        Random random = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        try {
            while (!Thread.currentThread().isInterrupted()) {
                Dish dish = rack.takeDry();
                int organizeTime = random.nextInt(2) + 1;
                Thread.sleep(organizeTime * 1000L);

                String time = LocalTime.now().format(formatter);
                System.out.printf("[%s] [%s] organiza en alacena plato con número de serie %d\n",
                        time, Thread.currentThread().getName(), dish.getSerialNumber());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
