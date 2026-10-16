import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public void show() {
        String osName = System.getProperty("os.name").toLowerCase();
        System.out.println("Sistema operativo: " + osName);

        String[] command;

        if (osName.contains("win")) {
            command = new String[] {"cmd.exe", "/c", "dir /a"};
        } else {
            command = new String[] {"ls -la"};
        }

        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    System.out.println(linea);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("[Info]: El comando finalizó con un código de error: " + exitCode);
            }

        } catch (IOException e) {
            System.err.println("[Info]: Error de entrada/salida: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("[Info]: El proceso fue interrumpido");
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        new Main().show();
    }
}