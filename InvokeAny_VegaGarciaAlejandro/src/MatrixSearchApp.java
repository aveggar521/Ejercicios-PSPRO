import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MatrixSearchApp {
    private static final int ROWS = 5;
    private static final int COLS = 5;
    private static final int MIN_MATRIX_VALUE = 1;
    private static final int MAX_MATRIX_VALUE = 10;
    private static final int MIN_TARGET_VALUE = 1;
    private static final int MAX_TARGET_VALUE = 20;

    static class Position {
        private final int row;
        private final int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

    private static int[][] generateMatrix(Random random) {
        int[][] matrix = new int[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                matrix[row][col] = random.nextInt(MAX_MATRIX_VALUE - MIN_MATRIX_VALUE + 1) + MIN_MATRIX_VALUE;
            }
        }
        return matrix;
    }

    private static void printMatrix(int[][] matrix) {
        System.out.println("Matriz generada (5x5):");
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.printf("%3d ", value);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Random random = new Random();
        int[][] matrix = generateMatrix(random);
        int targetNumber = random.nextInt(MAX_TARGET_VALUE - MIN_TARGET_VALUE + 1) + MIN_TARGET_VALUE;

        printMatrix(matrix);
        System.out.printf("Número a buscar: %d%n", targetNumber);

        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(processors);
        CompletionService<Position> completionService = new ExecutorCompletionService<>(executor);

        for (int row = 0; row < ROWS; row++) {
            final int rowIndex = row;
            Callable<Position> rowSearchTask = () -> {
                Random taskRandom = new Random();
                for (int col = 0; col < COLS; col++) {
                    if (Thread.currentThread().isInterrupted()) {
                        return null;
                    }
                    if (matrix[rowIndex][col] == targetNumber) {
                        return new Position(rowIndex, col);
                    }
                    int delayMillis = taskRandom.nextInt(501) + 500;
                    Thread.sleep(delayMillis);
                }
                return null;
            };
            completionService.submit(rowSearchTask);
        }

        Position foundPosition = null;
        try {
            for (int i = 0; i < ROWS; i++) {
                Future<Position> completedFuture = completionService.take();
                Position position = completedFuture.get();
                if (position != null) {
                    foundPosition = position;
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.printf("Error durante la búsqueda: %s%n", e.getMessage());
        } finally {
            executor.shutdownNow();
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (foundPosition != null) {
            System.out.printf("Número %d encontrado en la posición (fila %d, columna %d).%n",
                    targetNumber, foundPosition.getRow(), foundPosition.getCol());
        } else {
            System.out.printf("El número %d no se encuentra en la matriz.%n", targetNumber);
        }
    }
}