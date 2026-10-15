import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Inverter {

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()){
                    System.err.println("Error: has recibido una linea vacia");
                    System.exit(1);
                } else if ("EXIT".equalsIgnoreCase(line.trim())){
                    System.err.println("Saliendo: has recibido la senal de exit");
                    System.exit(1);
                }

                String inverted = new StringBuilder(line).reverse().toString();
                System.out.println(inverted);
            }
        } catch (IOException e) {
            System.err.printf("Error de entradad salida: %s", e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        new Inverter().run();
    }
}