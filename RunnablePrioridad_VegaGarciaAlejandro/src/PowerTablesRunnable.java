public class PowerTablesRunnable implements Runnable {
    private final int baseNumber;

    public PowerTablesRunnable(int baseNumber) {
        this.baseNumber = baseNumber;
    }

    @Override
    public void run() {
        try {
            for (int exponent = 0; exponent <= 10; exponent++) {
                long result = (long) Math.pow(baseNumber, exponent);
                System.out.printf("%s: %d ^ %d = %d\n",
                        Thread.currentThread().getName(), baseNumber, exponent, result);
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            PowerTablesRunnable task = new PowerTablesRunnable(i);
            Thread thread = new Thread(task, "Thread-" + i);

            if (i % 2 == 0) {
                thread.setPriority(Thread.MAX_PRIORITY);
            } else {
                thread.setPriority(Thread.MIN_PRIORITY);
            }

            thread.start();
        }
    }
}