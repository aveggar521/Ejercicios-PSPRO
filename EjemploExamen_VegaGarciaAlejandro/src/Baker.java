import java.util.Random;
import java.util.Scanner;

public class Baker {
public void show(String[] args) {
    if (args.length != 4) {
        System.err.println("Error: Se requieren 4 argumentos (min, max, cantidad de panes, tipo).");
        System.exit(1);
    }

    int minSize = 0;
    int maxSize = 0;
    int amountBread = 0;

    try {
        minSize = Integer.parseInt(args[0]);
        maxSize = Integer.parseInt(args[1]);
        amountBread = Integer.parseInt(args[2]);
    } catch (NumberFormatException e) {
        System.err.printf("Error: Parametros numericos no validos: %s\n", e.getMessage());
        System.exit(1);
    }

    String breadType = args[3].toUpperCase();

    if (minSize > maxSize || amountBread <= 0 || (!breadType.equals("REDONDO") && !breadType.equals("BARRA"))) {
        System.err.println("Error: Los parametros introducidos no son correctos. (tamaño minimo > tamaño maximo, cantidad de panes <= 0 y tipo de pan [BARRA o REDONDO)");
        System.exit(1);
    }

    Random random = new Random();

    for (int i = 0; i < amountBread; i++) {
        int randomSize = random.nextInt(minSize, maxSize + 1);
        System.out.printf("%s-%d\n", breadType, randomSize);
    }

    System.exit(0);
}
    public static void main(String[] args){
        new Baker().show(args);
    }
}




