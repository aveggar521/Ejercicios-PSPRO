import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Central {
    private final List<String> availableTrucks;
    private final List<String> availableBays;
    private final ReentrantLock lock;

    public Central(List<String> initialTrucks, List<String> initialBays) {
        this.availableTrucks = new ArrayList<>(initialTrucks);
        this.availableBays = new ArrayList<>(initialBays);
        this.lock = new ReentrantLock();
    }

    public boolean tryTransport(String terminalName) {
        lock.lock();
        try {
            if (availableTrucks.isEmpty() || availableBays.isEmpty()) {
                return false;
            }

            String truck = availableTrucks.remove(0);
            String bay = availableBays.remove(0);
            System.out.printf("[%s] Asignados recursos para transporte -> Camión: %s, Bahía: %s\n", terminalName, truck, bay);

            lock.unlock();

            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            lock.lock();
            availableTrucks.add(truck);
            availableBays.add(bay);
            System.out.printf("[%s] Transporte finalizado. Recursos liberados -> Camión: %s, Bahía: %s\n", terminalName, truck, bay);
            return true;

        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void repairTruck(String terminalName) {
        lock.lock();
        String truck = null;
        try {
            if (!availableTrucks.isEmpty()) {
                truck = availableTrucks.remove(0);
                System.out.printf("[%s] Camión %s retirado para reparación.\n", terminalName, truck);
            }
        } finally {
            lock.unlock();
        }

        if (truck != null) {
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            lock.lock();
            try {
                availableTrucks.add(truck);
                System.out.printf("[%s] Reparación finalizada. Camión %s devuelto a disponibles.\n", terminalName, truck);
            } finally {
                lock.unlock();
            }
        } else {
            System.out.printf("[%s] No hay camiones disponibles para reparar en este momento.\n", terminalName);
        }
    }

    public void performBayInventory(String terminalName) {
        lock.lock();
        String bay = null;
        try {
            if (!availableBays.isEmpty()) {
                bay = availableBays.remove(0);
                System.out.printf("[%s] Bahía %s retirada para inventario.\n", terminalName, bay);
            }
        } finally {
            lock.unlock();
        }

        if (bay != null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            lock.lock();
            try {
                availableBays.add(bay);
                System.out.printf("[%s] Inventario finalizado. Bahía %s devuelta a disponibles.\n", terminalName, bay);
            } finally {
                lock.unlock();
            }
        } else {
            System.out.printf("[%s] No hay bahías disponibles para inventario en este momento.\n", terminalName);
        }
    }
}