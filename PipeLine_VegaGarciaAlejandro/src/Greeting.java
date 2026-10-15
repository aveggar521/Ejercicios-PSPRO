import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Random;

public class Greeting {
    public void show() {
        String[] greetings = {
                "Hola, %s",
                "Buenas, %s",
                "Saludos, %s",
                "Hello, %s",
                "Bienvenido, %s"
        };

        Random random = new Random();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            String name;
            while ((name = reader.readLine()) != null) {
                if (!name.trim().isEmpty()) {
                    int randomIndex = random.nextInt(greetings.length);
                    System.out.println(String.format(greetings[randomIndex], name.trim()));
                }
            }
        } catch (IOException e) {
            System.err.printf("Error de entrada salida: %s", e.getMessage());
            System.exit(1);
        }
    }
    public static void main(String[] args) {
        new Greeting().show();
    }
}
