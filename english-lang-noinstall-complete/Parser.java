import java.util.*;

public class Parser {

    public static void parse(List<String> lines, Context context) throws LanguageException {

        for (int i = 0; i < lines.size(); i++) {

            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            if (line.equals("TRY")) {
                i = handleTryBlock(lines, i, context);
            } else {
                executeLine(line, context);
            }
        }
    }

    private static void executeLine(String line, Context context) throws LanguageException {

        String[] t = line.split("\\s+");

        switch (t[0]) {
            case "SET": Executor.setValue(t, context); break;
            case "ADD": Executor.addValue(t, context); break;
            case "DIV": Executor.divideValue(t, context); break;
            case "PRINT": Executor.printValue(t, context); break;
            default: throw new LanguageException("Unknown command: " + t[0]);
        }
    }

    private static int handleTryBlock(List<String> lines, int index, Context context) {

        int i = index + 1;
        boolean errorOccurred = false;

        // TRY
        try {
            while (!lines.get(i).trim().equals("CATCH")) {
                executeLine(lines.get(i), context);
                i++;
            }
        } catch (Exception e) {
            errorOccurred = true;
        }

        // CATCH
        i++; // skip CATCH
        if (errorOccurred) {
            while (!lines.get(i).trim().equals("FINALLY")) {
                System.out.println("Error handled");
                i++;
            }
        } else {
            while (!lines.get(i).trim().equals("FINALLY")) i++;
        }

        // FINALLY
        i++; // skip FINALLY
        while (!lines.get(i).trim().equals("ENDTRY")) {
            System.out.println("Finally executed");
            i++;
        }

        return i;
    }
}
