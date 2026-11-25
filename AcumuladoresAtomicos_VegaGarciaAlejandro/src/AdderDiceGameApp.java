import java.util.Random;
import java.util.concurrent.atomic.LongAdder;

class AdderScoreboard {
    private static final int DICE_FACES = 6;
    private final LongAdder[] counters;

    public AdderScoreboard() {
        this.counters = new LongAdder[DICE_FACES];
        for (int i = 0; i < DICE_FACES; i++) {
            this.counters[i] = new LongAdder();
        }
    }

    public void increment(int face) {
        counters[face - 1].increment();
    }

    public long getCount(int face) {
        return counters[face - 1].sum();
    }
}

class AdderDiceRoller implements Runnable {
    private static final int ROLLS_PER_THREAD = 10000;
    private static final int DICE_FACES = 6;
    private final AdderScoreboard scoreboard;

    public AdderDiceRoller(AdderScoreboard scoreboard) {
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

public class AdderDiceGameApp {
    private static final int DICE_FACES = 6;

    public static void main(String[] args) {
        AdderScoreboard scoreboard = new AdderScoreboard();

        Thread roller1 = new Thread(new AdderDiceRoller(scoreboard), "Dado-1");
        Thread roller2 = new Thread(new AdderDiceRoller(scoreboard), "Dado-2");
        Thread roller3 = new Thread(new AdderDiceRoller(scoreboard), "Dado-3");

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

        long totalSum = 0;
        StringBuilder totalExpression = new StringBuilder();

        for (int face = 1; face <= DICE_FACES; face++) {
            long count = scoreboard.getCount(face);
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