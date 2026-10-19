import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Assistant {
    public void show() {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("-", - 1);
                     if (parts.length == 2) {
                         String breadType = parts[0];
                         int size = Integer.parseInt(parts[1]);
                         System.out.printf("Pan %s de %d cm colocado en tienda.",breadType, size);
                     }
                }
            }
        } catch (IOException e) {
            System.err.printf("Erro de entrada salida: %s", e.getMessage());
        }
    }
    public static void main(String[] args) {
        new Assistant().show();
    }
}
