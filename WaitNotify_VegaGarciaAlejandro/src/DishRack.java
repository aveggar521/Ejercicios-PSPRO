import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DishRack {
    private final Queue<Dish> cleanRack;
    private final Queue<Dish> dryRack;
    private final int maxCapacity;
    private final Lock lock;
    private final Condition cleanCondition;
    private final Condition dryCondition;

    public DishRack(int maxCapacity) {
        this.cleanRack = new LinkedList<>();
        this.dryRack = new LinkedList<>();
        this.maxCapacity = maxCapacity;
        this.lock = new ReentrantLock();
        this.cleanCondition = lock.newCondition();
        this.dryCondition = lock.newCondition();
    }

    public void putClean(Dish dish) throws InterruptedException {
        lock.lock();
        try {
            while (cleanRack.size() >= maxCapacity) {
                cleanCondition.await();
            }
            cleanRack.add(dish);
            dryCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    public Dish takeClean() throws InterruptedException {
        lock.lock();
        try {
            while (cleanRack.isEmpty()) {
                cleanCondition.signal();
                cleanCondition.await();
            }
            Dish dish = cleanRack.poll();
            cleanCondition.signal();
            return dish;
        } finally {
            lock.unlock();
        }
    }

    public void putDry(Dish dish) throws InterruptedException {
        lock.lock();
        try {
            while (dryRack.size() >= maxCapacity) {
                dryCondition.await();
            }
            dryRack.add(dish);
        } finally {
            lock.unlock();
        }
    }

    public Dish takeDry() throws InterruptedException {
        lock.lock();
        try {
            while (dryRack.isEmpty()) {
                dryCondition.await();
            }
            Dish dish = dryRack.poll();
            dryCondition.signal();
            return dish;
        } finally {
            lock.unlock();
        }
    }
}