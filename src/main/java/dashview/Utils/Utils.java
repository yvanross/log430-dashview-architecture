package dashview.Utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/** Utilitaire  */
public class Utils {
    
    /** read file and returen a string
     * @return String containing the file content
     */
    public static String readFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        // Path path = Paths.get(filePath);
        
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}