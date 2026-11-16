import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Rack {
    private int dirtyDishes;
    private final int maxCapacity;
    private final Lock lock;
    private final Condition washCondition;
    private final Condition dryCondition;

    public Rack(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.dirtyDishes = 0;
        this.lock = new ReentrantLock();
        this.washCondition = lock.newCondition();
        this.dryCondition = lock.newCondition();
    }

    public void washDish(String washerName) throws InterruptedException {
        lock.lock();
        try {
            while (dirtyDishes >= maxCapacity) {
                washCondition.await();
            }
            dirtyDishes++;
            System.out.printf("[%s] Lava un plato. Platos sucios acumulados: %d\n", washerName, dirtyDishes);
            dryCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    public void dryDish(String dryerName) throws InterruptedException {
        lock.lock();
        try {
            while (dirtyDishes <= 0) {
                dryCondition.signal();
                dryCondition.await();
            }
            dirtyDishes--;
            System.out.printf("[%s] Seca un plato. Platos sucios restantes: %d\n", dryerName, dirtyDishes);
            washCondition.signal();
        } finally {
            lock.unlock();
        }
    }
}

class Washer implements Runnable {
    private final String name;
    private final Rack rack;

    public Washer(String name, Rack rack) {
        this.name = name;
        this.rack = rack;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                rack.washDish(name);
                Thread.sleep(300);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Dryer implements Runnable {
    private final String name;
    private final Rack rack;

    public Dryer(String name, Rack rack) {
        this.name = name;
        this.rack = rack;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                rack.dryDish(name);
                Thread.sleep(400);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Rack rack = new Rack(5);

        Thread washer1 = new Thread(new Washer("Lavador-1", rack));
        Thread dryer1 = new Thread(new Dryer("Secador-1", rack));

        washer1.start();
        dryer1.start();

        try {
            washer1.join();
            dryer1.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Todos los platos han sido lavados y secados con éxito");
    }
}