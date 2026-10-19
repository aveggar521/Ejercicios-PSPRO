import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Manager {
    public void show() {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classPath = System.getProperty("java.class.path");

        ProcessBuilder pbBaker1 = new ProcessBuilder(javaBin, "-cp", classPath, "Baker", "10", "100", "2", "REDONDO");
        ProcessBuilder pbBaker2 = new ProcessBuilder(javaBin, "-cp", classPath, "Baker", "50", "120", "4");

        pbBaker1.redirectError(ProcessBuilder.Redirect.INHERIT);
        pbBaker2.redirectError(ProcessBuilder.Redirect.INHERIT);

        List<String> bakedBreads = new ArrayList<>();

        try {
            Process processBaker1 = pbBaker1.start();
            Process processBaker2 = pbBaker2.start();

            System.out.println( "Horneando todo el pan...");

            try (BufferedReader reader1 = new BufferedReader(new InputStreamReader(processBaker1.getInputStream()))) {
                String line;
                while ((line = reader1.readLine()) != null) {
                    bakedBreads.add(line);
                }
            }
            try (BufferedReader reader2 = new BufferedReader(new InputStreamReader(processBaker2.getInputStream()))) {
                String line;
                while ((line = reader2.readLine()) != null) {
                    bakedBreads.add(line);
                }
            }

            int exitCode1 = processBaker1.waitFor();
            int exitCode2 = processBaker2.waitFor();


            if (exitCode1 != 0 || exitCode2 != 0) {
                System.err.printf("Error durante el horneado. Codigos de salida: Panadero1=%d, Panadero2=%d\n", exitCode1, exitCode2);
                return;
            }

            ProcessBuilder pbAssistant = new ProcessBuilder(javaBin, "-cp", classPath, "Assistant");
            pbAssistant.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process processAssistant = pbAssistant.start();

            try (PrintWriter writer = new PrintWriter(processAssistant.getOutputStream(), true)) {
                for (String bread : bakedBreads) {
                    writer.println(bread);
                }
            }

            try (BufferedReader readerAssistant = new BufferedReader(new InputStreamReader(processAssistant.getInputStream()))) {
                String line;
                while ((line = readerAssistant.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int assistantExitCode = processAssistant.waitFor();

            if (assistantExitCode == 0) {
                System.out.println("Tienda preparada...");
            } else {
                System.err.println("Error en el dependiente");
            }

        } catch (IOException e) {
            System.err.printf("Error de E/S en la gestion de la panaderia: %s\n", e.getMessage());
        } catch (InterruptedException e) {
            System.err.printf("La ejecucion del gerente fue interrumpida: %s\n", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    public static void main(String[] args) {
        new Manager().show();
    }
}
