import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CharacterCounter {

    public void run(String[] args) {
        if (args.length != 1) {
            System.err.println("Error: Invalid mode");
            System.exit(1);
        }

        String mode = args[0].toLowerCase();
        if (!mode.equals("vowels") && !mode.equals("consonants")) {
            System.err.println("Error: Invalid mode");
            System.exit(1);
        }

        String debugEnv = System.getenv("DEBUG_MODE");
        boolean isDebug = "TRUE".equalsIgnoreCase(debugEnv);
        long processId = ProcessHandle.current().pid();

        int totalCount = 0;
        String vowelsList = "aeiouAEIOU";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int lineCount = 0;

                for (int i = 0; i < line.length(); i++) {
                    char ch = line.charAt(i);
                    if (Character.isLetter(ch)) {
                        boolean isVowel = vowelsList.indexOf(ch) != -1;
                        if (mode.equals("vowels") && isVowel) {
                            lineCount++;
                        } else if (mode.equals("consonants") && !isVowel) {
                            lineCount++;
                        }
                    }
                }

                totalCount += lineCount;

                if (isDebug) {
                    if (mode.equals("vowels")) {
                        System.err.println("[PID: " + processId + "] Line parsed. Found " + lineCount + " vocals");
                    } else {
                        System.err.println("[PID: " + processId + "] Line parsed. Found " + lineCount + " consonants");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
            System.exit(1);
        }

        if (mode.equals("vowels")) {
            System.out.println("Total vowels: " + totalCount);
        } else {
            System.out.println("Total consonants: " + totalCount);
        }

        System.exit(0);
    }

    public static void main(String[] args) {
        new CharacterCounter().run(args);
    }
}