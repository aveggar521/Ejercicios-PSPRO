import java.util.Random;

class Scoreboard {
    private final int[] counts = new int[6];

    public synchronized void increment(int face) {
        counts[face - 1]++;
    }

    public int[] getCounts() {
        return counts;
    }
}

class DiceRoller implements Runnable {
    private final Scoreboard scoreboard;

    public DiceRoller(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public void run() {
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            int diceFace = random.nextInt(6) + 1;
            scoreboard.increment(diceFace);
        }
    }
}

public class DiceGameApp {
    public static void main(String[] args) {
        Scoreboard scoreboard = new Scoreboard();

        Thread roller1 = new Thread(new DiceRoller(scoreboard), "Dado-1");
        Thread roller2 = new Thread(new DiceRoller(scoreboard), "Dado-2");
        Thread roller3 = new Thread(new DiceRoller(scoreboard), "Dado-3");

        roller1.start();
        roller2.start();
        roller3.start();

        try {
            roller1.join();
            roller2.join();
            roller3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int[] finalCounts = scoreboard.getCounts();
        int totalSum = 0;
        StringBuilder totalExpression = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int faceNumber = i + 1;
            int count = finalCounts[i];
            System.out.printf("Número %d: %d veces\n", faceNumber, count);

            totalSum += count;
            if (i > 0) {
                totalExpression.append(" + ");
            }
            totalExpression.append(count);
        }

        System.out.printf("Total de veces: %s = %d\n", totalExpression.toString(), totalSum);
    }
}