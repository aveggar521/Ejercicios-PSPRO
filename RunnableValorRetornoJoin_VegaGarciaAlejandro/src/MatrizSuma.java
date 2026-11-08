import java.util.Random;

public class MatrizSuma implements Runnable {
    private final int[] row;
    private final int rowIndex;
    private int rowSum;

    public MatrizSuma(int[] row, int rowIndex) {
        this.row = row;
        this.rowIndex = rowIndex;
        this.rowSum = 0;
    }

    @Override
    public void run() {
        try {
            for (int value : row) {
                rowSum += value;
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getRowSum() {
        return rowSum;
    }

    public static void main(String[] args) {
        int rows = 5;
        int cols = 5;
        int[][] matrix = new int[rows][cols];
        Random random = new Random();
        int sequentialSum = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(10);
                sequentialSum += matrix[i][j];
            }
        }

        System.out.println("Matriz generada (5x5):");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println("Suma secuencial (comprobación): " + sequentialSum);
        System.out.println("--------------------------------------------------");

        MatrizSuma[] tasks = new MatrizSuma[rows];
        Thread[] threads = new Thread[rows];

        for (int i = 0; i < rows; i++) {
            tasks[i] = new MatrizSuma(matrix[i], i);
            threads[i] = new Thread(tasks[i], "Sumador-Fila-" + i);
            threads[i].start();
        }

        System.out.println("Sumadores iniciados");

        for (int i = 0; i < rows; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        int totalThreadsSum = 0;
        for (int i = 0; i < rows; i++) {
            int partialSum = tasks[i].getRowSum();
            System.out.printf("Suma de la fila %d: %d\n", i, partialSum);
            totalThreadsSum += partialSum;
        }

        System.out.println("--------------------------------------------------");
        System.out.println("Suma total obtenida por los hilos: " + totalThreadsSum);

        if (totalThreadsSum == sequentialSum) {
            System.out.println("La suma de los hilos coincide con la secuencial.");
        } else {
            System.out.println("Error: Las sumas no coinciden.");
        }
    }
}