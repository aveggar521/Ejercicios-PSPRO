import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public void show() {
        List<String> textLines = Arrays.asList(
                "First line of text to analyze",
                "Concurrent process execution in Java PSP",
                "Counting vowels and consonants simultaneously"
        );

        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classPath = System.getProperty("java.class.path");

        ProcessBuilder pbVowels = new ProcessBuilder(javaBin, "-cp", classPath, "CharacterCounter", "vowels");
        pbVowels.redirectError(ProcessBuilder.Redirect.INHERIT);
        pbVowels.environment().put("DEBUG_MODE", "TRUE");

        File logsDir = new File("./logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }

        ProcessBuilder pbConsonants = new ProcessBuilder(javaBin, "-cp", classPath, "CharacterCounter", "consonants");
        pbConsonants.directory(logsDir);
        File logErrorFile = new File(logsDir, "consonant_err.log");
        pbConsonants.redirectError(logErrorFile);

        try {
            Process processVowels = pbVowels.start();
            Process processConsonants = pbConsonants.start();

            System.out.println("Vowels Process PID: " + processVowels.pid());
            System.out.println("Consonants Process PID: " + processConsonants.pid());

            PrintWriter writerVowels = new PrintWriter(processVowels.getOutputStream(), true);
            PrintWriter writerConsonants = new PrintWriter(processConsonants.getOutputStream(), true);

            for (String line : textLines) {
                writerVowels.println(line);
                writerConsonants.println(line);
            }

            writerVowels.close();
            writerConsonants.close();

            BufferedReader readerVowels = new BufferedReader(new InputStreamReader(processVowels.getInputStream()));
            String vowelResult = readerVowels.readLine();

            BufferedReader readerConsonants = new BufferedReader(new InputStreamReader(processConsonants.getInputStream()));
            String consonantResult = readerConsonants.readLine();

            int exitCodeVowels = processVowels.waitFor();
            int exitCodeConsonants = processConsonants.waitFor();

            System.out.println("Vowel Result: " + vowelResult);
            System.out.println("Consonant Result: " + consonantResult);

            if (exitCodeVowels == 0 && exitCodeConsonants == 0) {
                System.out.println("Both processes finished successfully.");
            } else {
                System.err.println("One or both processes failed.");
            }

        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Process interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        new Main().show();
    }
}