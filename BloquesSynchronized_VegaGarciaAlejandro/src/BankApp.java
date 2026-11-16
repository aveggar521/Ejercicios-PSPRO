public class BankApp {
    public static void main(String[] args) {
        Account accountA = new Account(1, 1000.0);
        Account accountB = new Account(2, 1000.0);

        double initialTotal = accountA.getBalance() + accountB.getBalance();
        System.out.printf("Saldo inicial total del sistema (A + B): %.2f€\n", initialTotal);

        Bank bank = new Bank(accountA, accountB);

        Thread cashier1 = new Thread(new Cashier("Cajero-1", bank));
        Thread cashier2 = new Thread(new Cashier("Cajero-2", bank));
        Thread cashier3 = new Thread(new Cashier("Cajero-3", bank));

        cashier1.start();
        cashier2.start();
        cashier3.start();

        try {
            cashier1.join();
            cashier2.join();
            cashier3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Todas las operaciones han finalizado.");
        System.out.printf("Saldo final Cuenta A: %.2f€\n", accountA.getBalance());
        System.out.printf("Saldo final Cuenta B: %.2f€\n", accountB.getBalance());

        double finalTotal = accountA.getBalance() + accountB.getBalance();
        System.out.printf("Saldo final total del sistema: %.2f€\n", finalTotal);

        if (initialTotal == finalTotal) {
            System.out.println("Comprobación exitosa. La invariante de suma cero se mantiene intacta.");
        } else {
            System.out.println("Error: Descuadre en el sistema bancario.");
        }
    }
}