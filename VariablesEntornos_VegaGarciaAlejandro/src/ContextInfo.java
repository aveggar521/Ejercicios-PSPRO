import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Locale;

public class ContextInfo {
    public void show() {
    String customId = System.getenv("CUSTOM_ID");
    System.out.printf("CUSTOM_ID: %s\n", customId);

    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

    String pwdCommand = isWindows ? "cmd.exe /c cd" : "pwd";
    executeSystemCommand(pwdCommand);

    String lsCommand = isWindows ? "cmd.exe /c dir" : "ls";
    executeSystemCommand(lsCommand);

    }

    private void executeSystemCommand(String command) {
        try {
            String[] cmdArray = command.split(" ");
            ProcessBuilder pb = new ProcessBuilder(cmdArray);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            process.waitFor();
        } catch (IOException e) {
            System.err.printf("Error de entrada salida: %s", e.getMessage());
        } catch (InterruptedException e) {
            System.err.printf("Proceso interrumpido: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }

    }

    public static void main(String[] args) {
        new ContextInfo().show();
    }
}
