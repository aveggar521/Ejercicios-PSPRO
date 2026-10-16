import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class GreetPeople {
    public void show() {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classPath = System.getProperty("java.class.path");

        ProcessBuilder pbNames = new ProcessBuilder(javaBin, "-cp", classPath, "Names","8");
        ProcessBuilder pbGreeting = new ProcessBuilder(javaBin, "-cp", classPath, "Greeting");

        pbNames.redirectError(ProcessBuilder.Redirect.INHERIT);
        pbGreeting.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            List<Process> pipeline = ProcessBuilder.startPipeline(Arrays.asList(pbNames, pbGreeting));

            Process lastProcess = pipeline.get(pipeline.size() - 1);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(lastProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            for (Process process : pipeline) {
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.err.printf("Error: un proceso ha finalizado con codigo de error: %d\n", exitCode);
                }
            }
        } catch (IOException e) {
            System.err.printf("Error de entrada salida: %s", e.getMessage());
        } catch (InterruptedException e) {
            System.err.printf("La ejecucion ha sido interrumpida: %s\n", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    public static void main(String[] args) {
        new GreetPeople().show();
    }
}
