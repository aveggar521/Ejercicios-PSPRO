import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Dictory {
    public void  show() {
        int totalInstancias = 10;
        List<Process> procesos = new ArrayList<>();

        System.out.println("Generando todas las palabras para el diccionario...");

        for (int i = 1; i <= totalInstancias; i++) {
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", "language.jar", String.valueOf(i));
            try {
                Process p = pb.start();
                procesos.add(p);
            } catch (IOException e) {
                System.err.println("Error al intentar ejecutar el subproceso language.jar: " + e.getMessage());
                return;
            }
        }

        List<String> todasLasPalabras = new ArrayList<>();
        boolean error = false;

        for (int i = 0; i < procesos.size(); i++) {
            Process p = procesos.get(i);

            try {
                int exitCode = p.waitFor();

                if (exitCode != 0) {
                    error = true;
                    try (BufferedReader errReader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                        String lineaError;
                        System.err.println("\n[Error en la instancia " + (i + 1) + "]:");
                        while ((lineaError = errReader.readLine()) != null) {
                            System.err.println(lineaError);
                        }
                    }
                    break;
                } else {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                        String palabra;
                        while ((palabra = reader.readLine()) != null) {
                            if (!palabra.trim().isEmpty()) {
                                todasLasPalabras.add(palabra.trim());
                            }
                        }
                    }
                }

            } catch (InterruptedException e) {
                System.err.println("El proceso principal fue interrumpido: " + e.getMessage());
                Thread.currentThread().interrupt();
                return;
            } catch (IOException e) {
                System.err.println("Error de E/S leyendo los datos del subproceso: " + e.getMessage());
                return;
            }
        }

        if (error) {
            System.err.println("\nProceso cancelado debido a errores en la generación de palabras.");
            return;
        }

        Set<String> palabrasUnicas = new LinkedHashSet<>(todasLasPalabras);

        System.out.println("\n--- DICCIONARIO GENERADO (SIN REPETICIONES) ---");
        for (String palabra : palabrasUnicas) {
            System.out.println(palabra);
        }
    }
    public static void main(String[] args) {
        new Dictory().show();
    }
}
