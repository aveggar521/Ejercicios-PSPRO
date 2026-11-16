public class BirthdayApp {
    public static void main(String[] args) {
        DishRack rack = new DishRack(5);

        Thread washer = new Thread(new Washer(rack), "fregador");
        Thread dryer = new Thread(new Dryer(rack), "secador");
        Thread organizer = new Thread(new Organizer(rack), "organizador");

        washer.start();
        dryer.start();
        organizer.start();

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        washer.interrupt();
        dryer.interrupt();
        organizer.interrupt();

        try {
            washer.join();
            dryer.join();
            organizer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Cumpleaños feliz");
    }
}
