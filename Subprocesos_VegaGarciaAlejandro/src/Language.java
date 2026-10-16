import java.util.Random;

public class Language {
    public void show(String[] args) {
        if (args.length == 0) {
            System.err.println("Uso correcto: java -jar language.jar <numero_palabras>");
            System.exit(1);
        }

        int numPalabras = 0;
        try {
            numPalabras = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Error: El argumento debe ser un número entero válido.");
            System.exit(1);
        }

        if (numPalabras <= 0) {
            System.err.println("Error: El número de palabras debe ser mayor que 0.");
            System.exit(1);
        }

        Random random = new Random();
        String abecedario = "abcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < numPalabras; i++) {
            int longitud = random.nextInt(7) + 2; // (0 a 6) + 2 = [2, 8]
            StringBuilder palabra = new StringBuilder();

            for (int j = 0; j < longitud; j++) {
                char letra = abecedario.charAt(random.nextInt(abecedario.length()));
                palabra.append(letra);
            }

            System.out.println(palabra.toString());
        }

        System.exit(0);
    }
    public static void main(String[] args) {
        new Language().show(args);
    }
}