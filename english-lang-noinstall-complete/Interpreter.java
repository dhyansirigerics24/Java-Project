import java.nio.file.*;
import java.util.*;

public class Interpreter {

    private Context context;

    public Interpreter() {
        context = new Context(); // constructor + object
    }

    public void execute(String fileName) throws LanguageException {

        if (!fileName.endsWith(".eng")) {
            throw new LanguageException("Invalid file type");
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            Parser.parse(lines, context);

        } catch (Exception e) {
            throw new LanguageException(e.getMessage());
        }
    }
}
