import java.io.*;

public class Main {
    public void show() {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classPath = System.getProperty("java.class.path");

        ProcessBuilder pb = new ProcessBuilder(javaBin, "-cp", classPath, "Inverter");
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            Process process = pb.start();

            PrintWriter writer = new PrintWriter(process.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String[] testString = {
                    "Hola mundo",
                    "Baldomero el mejor",
                    "123456789",
                    "Programacion de servicios y procesos"
            };

            int index = 0;
            boolean processRunning = true;

            while (index < testString.length && processRunning) {
                String input = testString[index];
                writer.println(input);

                if (input.trim().isEmpty() || "exit".equalsIgnoreCase(input.trim())) {
                    processRunning = false;
                } else {
                    String invertedOutput = reader.readLine();
                    System.out.printf("Original: %s -> Invertido: %s\n", input, invertedOutput);
                }
                index++;
            }

            writer.println("EXIT");

            int exitCode = process.waitFor();

            if (exitCode == 1) {
                System.out.println("Verificacion completada. Proceso terminado con codigo 1");
            } else {
                System.out.printf("Verificacion fallida, %s", exitCode);
            }
        } catch (IOException e) {
            System.err.printf("Error de entrada salida: %s", e.getMessage());
        } catch (InterruptedException e) {
            System.err.printf("Proceso interrumpido: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    public static void main(String[] args) {
        new Main().show();
    }
}