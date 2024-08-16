import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws Exception 
    {
        if (args.length != 1) {
            System.err.println("Usage: radha <filename>.radha");
            return;
        }

        String filename = args[0];
        String content = "";

        try {
            content = String.join("\n", Files.readAllLines(Paths.get(filename)));
        } 
        catch (IOException e) {
            // System.err.println("Error reading the file: " + e.getMessage());
            // e.printStackTrace();
            throw new RadhaError("File to be executed not found !");
        }

        new RadhaProgrammingLanguage(content);
    }
}