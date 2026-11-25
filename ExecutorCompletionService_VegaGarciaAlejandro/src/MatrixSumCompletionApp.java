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

public class MatrixSumCompletionApp {
    private static final int ROWS = 5;
    private static final int COLS = 5;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 10;

    static class RowSumResult {
        private final int rowIndex;
        private final int sum;

        public RowSumResult(int rowIndex, int sum) {
            this.rowIndex = rowIndex;
            this.sum = sum;
        }

        public int getRowIndex() {
            return rowIndex;
        }

        public int getSum() {
            return sum;
        }
    }

    private static int[][] generateMatrix(Random random) {
        int[][] matrix = new int[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                matrix[row][col] = random.nextInt(MAX_VALUE - MIN_VALUE + 1) + MIN_VALUE;
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
        printMatrix(matrix);

        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(processors);
        CompletionService<RowSumResult> completionService = new ExecutorCompletionService<>(executor);

        for (int row = 0; row < ROWS; row++) {
            final int rowIndex = row;
            Callable<RowSumResult> rowTask = () -> {
                Random taskRandom = new Random();
                int rowSum = 0;
                for (int col = 0; col < COLS; col++) {
                    rowSum += matrix[rowIndex][col];
                    Thread.sleep(taskRandom.nextInt(150) + 50);
                }
                return new RowSumResult(rowIndex + 1, rowSum);
            };
            completionService.submit(rowTask);
        }

        List<Integer> partialSums = new ArrayList<>();
        try {
            for (int i = 0; i < ROWS; i++) {
                Future<RowSumResult> completedFuture = completionService.take();
                RowSumResult result = completedFuture.get();
                partialSums.add(result.getSum());
                System.out.printf("Suma parcial obtenida -> Fila %d: %d%n", result.getRowIndex(), result.getSum());
            }

            Callable<Integer> totalSumTask = () -> {
                int total = 0;
                for (int partial : partialSums) {
                    total += partial;
                }
                return total;
            };

            Future<Integer> totalFuture = executor.submit(totalSumTask);
            int totalSum = totalFuture.get();

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);

            System.out.printf("Suma total de la matriz: %d%n", totalSum);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        } catch (ExecutionException e) {
            System.err.printf("Error durante la ejecución de una tarea: %s%n", e.getMessage());
            executor.shutdownNow();
        }
    }
}