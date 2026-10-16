import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public void show(){
        String osName = System.getProperty("os.name").toLowerCase();
        System.out.println(osName);

        String command[];

            if (osName.contains("win")) {
                command = new String[] {"cmd.exe", "/c", "dir /a"};
            } else {
                command = new String[] {"sh", "-c", "ls -la"};
            }

            try {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(command);

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
                    String linea;
                    System.out.printf("Comando ejecutado: ", command);
                    while ((linea = reader.readLine()) != null){
                        System.out.println(linea);
                    }
                }
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.out.println("[Info]: El comando finalizo con un codigo de error: " + exitCode);
                }

            } catch (IOException e) {
                System.err.println("[Info]: Error de entrada/salida");
            } catch (InterruptedException e) {
                System.err.println("[Info]: El proceso fue interrumpido");
                Thread.currentThread().interrupt();
            }
    }
    public static void main(String[] args) {
        new Main().show();
    }
}