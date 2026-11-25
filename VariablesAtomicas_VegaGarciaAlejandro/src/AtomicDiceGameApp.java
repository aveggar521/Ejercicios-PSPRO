import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;

class AtomicScoreboard {
    private final AtomicIntegerArray counts = new AtomicIntegerArray(6);

    public void increment(int face) {
        counts.incrementAndGet(face - 1);
    }

    public int getCount(int face) {
        return counts.get(face - 1);
    }
}

class AtomicDiceRoller implements Runnable {
    private static final int ROLLS_PER_THREAD = 10000;
    private static final int DICE_FACES = 6;
    private final AtomicScoreboard scoreboard;

    public AtomicDiceRoller(AtomicScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public void run() {
        Random random = new Random();
        for (int i = 0; i < ROLLS_PER_THREAD; i++) {
            int diceFace = random.nextInt(DICE_FACES) + 1;
            scoreboard.increment(diceFace);
        }
    }
}

public class AtomicDiceGameApp {
    private static final int DICE_FACES = 6;

    public static void main(String[] args) {
        AtomicScoreboard scoreboard = new AtomicScoreboard();

        Thread roller1 = new Thread(new AtomicDiceRoller(scoreboard), "Dado-1");
        Thread roller2 = new Thread(new AtomicDiceRoller(scoreboard), "Dado-2");
        Thread roller3 = new Thread(new AtomicDiceRoller(scoreboard), "Dado-3");

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

        int totalSum = 0;
        StringBuilder totalExpression = new StringBuilder();

        for (int face = 1; face <= DICE_FACES; face++) {
            int count = scoreboard.getCount(face);
            System.out.printf("Número %d: %d veces%n", face, count);

            totalSum += count;
            if (face > 1) {
                totalExpression.append(" + ");
            }
            totalExpression.append(count);
        }

        System.out.printf("Total de veces: %s = %d%n", totalExpression.toString(), totalSum);
    }
}