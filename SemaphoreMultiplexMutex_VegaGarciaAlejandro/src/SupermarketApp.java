public class SupermarketApp {
    public static void main(String[] args) {
        int numberOfRegisters = 4;
        int numberOfCustomers = 50;

        CheckoutArea checkoutArea = new CheckoutArea(numberOfRegisters);
        Thread[] customers = new Thread[numberOfCustomers];

        for (int i = 0; i < numberOfCustomers; i++) {
            customers[i] = new Thread(new Customer(i + 1, checkoutArea), "Cliente-" + (i + 1));
            customers[i].start();
        }

        for (int i = 0; i < numberOfCustomers; i++) {
            try {
                customers[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Todos los clientes han completado su paso por caja.");
    }
}
