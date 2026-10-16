import java.util.Random;

public class Names {
    public void show(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: debes de proporcionar un numero de nombres a generar");
            System.exit(1);
        }

        int count = 0;
        try {
            count = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.printf("Error: formato numerico no valido en los argumentos: %s\n", e.getMessage());
            System.exit(1);
        }

        String[] avaibleNames = {
                "Baldomero", "Alejandro", "Pablo", "Pedro", "Eva", "Lucia",
                "Maria", "Daniel", "Sofia"
        };

        Random random = new Random();

        for (int i = 0; i < count; i++) {
            int randomIndex = random.nextInt(avaibleNames.length);
            System.out.println(avaibleNames[randomIndex]);
        }
    }
    public static void main(String[] args) {
        new Names().show(args);
    }
}