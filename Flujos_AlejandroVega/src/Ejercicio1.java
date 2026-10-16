import java.io.*;
import java.util.Scanner;

//Alejandro Vega
public class Main {
    private static final String CENTINELA = "CENTINELA FIN";

    private void procesoPadre(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce la cadena de texto a filtrar: ");
        String cadenaFiltrar =  sc.nextLine();

        ProcessBuilder pb = new ProcessBuilder("java", "Main", cadenaFiltrar);

        try {
            Process subproceso = pb.start();

            PrintWriter escribirSubproceso =  new PrintWriter(subproceso.getOutputStream(), true);
            BufferedReader lectorSubproceso = new BufferedReader(
                    new InputStreamReader(subproceso.getInputStream())
            );

            String[] lineasAEnviar = {
                    "Primera línea de prueba con Java",
                    "Segunda línea de prueba",
                    "Esta línea contiene la palabra Java de nuevo",
                    "Línea aleatoria sin coincidencia",
                    "Última línea de código en Java"
            };

            System.out.println("\n--- Enviando datos al subproceso filtro ---");
            for (String linea : lineasAEnviar) {
                escribirSubproceso.println(linea);
            }

            escribirSubproceso.println(CENTINELA);

            System.out.println("\n--- Líneas filtradas devueltas por el subproceso ---");
            String lineaFiltrada;
            while ((lineaFiltrada = lectorSubproceso.readLine()) != null) {
                System.out.println(lineaFiltrada);
            }

            int exitCode = subproceso.waitFor();
            if (exitCode != 0) {
                System.err.println("[Info]: El subproceso finalizó con código de error: " + exitCode);
            }

        } catch (IOException e) {
            System.err.println("[Info]: Error de comunicación entre procesos: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("[Info]: El proceso fue interrumpido");
            Thread.currentThread().interrupt();
        }
        }
    }
    public static void main(String[] args) {
}
