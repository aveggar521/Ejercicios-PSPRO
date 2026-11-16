import java.util.Random;

public class Cashier implements Runnable {
    private final String name;
    private final Bank bank;

    public Cashier(String name, Bank bank) {
        this.name = name;
        this.bank = bank;
    }

    @Override
    public void run() {
        Random random = new Random();

        for (int i = 0; i < 15; i++) {
            int operationType = random.nextInt(3);
            int accountId = random.nextBoolean() ? 1 : 2;
            double amount = random.nextInt(90) + 10;

            if (operationType == 0) {
                bank.depositOperation(name, accountId, amount);
            } else if (operationType == 1) {
                bank.withdrawOperation(name, accountId, amount);
            } else {
                int targetId = (accountId == 1) ? 2 : 1;
                bank.transferOperation(name, accountId, targetId, amount);
            }

            try {
                Thread.sleep(random.nextInt(100) + 50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.printf("[%s] Turno de cajero finalizado.\n", name);
    }
}
