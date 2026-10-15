import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    public void show() {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classPath = System.getProperty("java.class.path");

        ProcessBuilder pb = new ProcessBuilder(javaBin, "-cp", classPath, "ContextInfo");

        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        String targetDirPath = isWindows ? "C:\\Temp" : "/tmp";

        File targetDir = new File(targetDirPath);

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        pb.directory(targetDir);

        String expectedCustomId = String.valueOf(System.currentTimeMillis());
        Map<String, String> env = pb.environment();
        env.put("CUSTOM_ID", expectedCustomId);

        try {
            Process process = pb.start();

            String capturedCustomId = null;
            String capturedWorkingDir = null;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean isFirstLine = true;
                boolean isSecondLine = false;

                while ((line = reader.readLine()) != null) {
                    System.out.printf("Subproceso de salida: %s", line);

                    if (isFirstLine) {
                        if (line.startsWith("CUSTOM_ID: ")) {
                            capturedCustomId = line.replace("CUSTOM_ID:", "").trim();
                        }
                        isFirstLine = false;
                        isSecondLine = true;
                    } else if (isSecondLine) {
                        capturedWorkingDir = line.trim();
                        isSecondLine = false;
                    }
                }
            }

            int exitCode = process.waitFor();

            System.out.println("--- Verificacion ---");
            if (expectedCustomId.equals(capturedCustomId)) {
                System.out.printf("Verificacion de CUSTOM_ID con exito. Inyectado: %s, capturado: %s\n", expectedCustomId, capturedCustomId);
            } else {
                System.err.printf("La verificacion fallo. Se esperaba: %s, pero se capturo: %s\n", expectedCustomId, capturedCustomId);
            }

            String expectedPath = targetDir.getCanonicalPath();
            if (capturedWorkingDir != null && new File(capturedWorkingDir).getCanonicalPath().equalsIgnoreCase(expectedPath)) {
                System.out.printf("Directorio verificado con exito. Establecido: %s, capturado: %s\n", expectedPath, capturedWorkingDir);
            } else {
                System.err.printf("Verificacion del directorio fallido. Se esperaba: %s, pero se capturo: %s\n", expectedPath, capturedWorkingDir);
            }


        } catch (IOException e) {
            System.err.printf("Error de entrada salida: %s", e.getMessage());
        } catch (InterruptedException e) {
            System.err.printf("El proceso fue interrumpido: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }

    }
    public static void main(String[] args) {
        new Main().show();
    }
}