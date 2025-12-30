import java.util.*;

public class Parser {


    public static void parse(List<String> lines, Context context) throws LanguageException {
        // Preprocess: Remove comments
        List<String> validLines = removeComments(lines);

        // First Pass: Register Classes
        for (int i = 0; i < validLines.size(); i++) {
            String line = validLines.get(i).trim();
            if (line.isEmpty()) continue;
            
            if (line.startsWith("CLASS")) {
                i = handleClassDefinition(validLines, i, context);
            }
        }

        // Second Pass: Execute Code
        for (int i = 0; i < validLines.size(); i++) {
            String line = validLines.get(i).trim();
            if (line.isEmpty()) continue;
            
            // Skip Class Definitions in execution pass
            if (line.startsWith("CLASS")) {
                i = skipBlock(validLines, i);
                continue;
            }

            if (line.startsWith("TRY")) {
                i = handleTryBlock(validLines, i, context);
            } else if (line.startsWith("IF")) {
                i = handleIfBlock(validLines, i, context);
            } else if (line.startsWith("WHILE")) {
                i = handleWhileBlock(validLines, i, context);
            } else {
                // Enforce Semicolon for Statements
                if (!line.endsWith(";")) {
                    throw new LanguageException("Missing semicolon at end of line: " + line);
                }
                String cleanLine = line.substring(0, line.length() - 1).trim();
                executeLine(cleanLine, context);
            }
        }
    }

    private static List<String> removeComments(List<String> lines) {
        List<String> cleaned = new ArrayList<>();
        boolean inBlockComment = false;

        for (String line : lines) {
            StringBuilder validContent = new StringBuilder();
            for (int i = 0; i < line.length(); i++) {
                if (inBlockComment) {
                    if (i + 1 < line.length() && line.charAt(i) == '*' && line.charAt(i + 1) == '/') {
                        inBlockComment = false;
                        i++; // Skip /
                    }
                } else {
                    if (i + 1 < line.length() && line.charAt(i) == '/' && line.charAt(i + 1) == '*') {
                        inBlockComment = true;
                        i++; // Skip *
                    } else if (i + 1 < line.length() && line.charAt(i) == '/' && line.charAt(i + 1) == '/') {
                        break; // Ignore rest of line
                    } else {
                        validContent.append(line.charAt(i));
                    }
                }
            }
            // Add if not empty (maintain line count correlation? No, loops use size(), logic relies on structure)
            // But if we remove empty lines here, line numbers might get confusing if we ever tracked them.
            // For now, simpler to just add what remains.
            cleaned.add(validContent.toString());
        }
        return cleaned;
    }
    
    // Parses and registers a class
    private static int handleClassDefinition(List<String> lines, int index, Context context) throws LanguageException {
        String header = lines.get(index).trim(); // CLASS Person
        String[] parts = tokenize(header);
        if (parts.length < 2) throw new LanguageException("Invalid Class Definition");
        String className = parts[1];
        
        ClassDefinition def = new ClassDefinition(className);
        
        int i = index + 1;
        while (i < lines.size() && !lines.get(i).trim().equals("ENDCLASS")) {
            String line = lines.get(i).trim();
            if (!line.isEmpty()) {
                 // Parse Fields: string name;
                 if (!line.endsWith(";")) throw new LanguageException("Missing semicolon in class definition: " + line);
                 String clean = line.substring(0, line.length()-1).trim();
                 String[] fParts = tokenize(clean);
                 if (fParts.length >= 2) {
                     String type = fParts[0];
                     String name = fParts[1];
                     def.addField(name, type);
                 }
            }
            i++;
        }
        context.defineClass(className, def);
        return i;
    }

    private static void executeLine(String line, Context context) throws LanguageException {
        String[] t = tokenize(line);
        if (t.length == 0) return;
        String cmd = t[0];
        
        // Check for Types
        if (isType(cmd)) {
            // int x = 10
            // string s = "hello"
            // Person p = NEW Person
            if (t.length < 4 || !t[2].equals("=")) throw new LanguageException("Invalid declaration syntax");
            
            if (t[3].equals("NEW")) {
                // Object Instantiation: Person p = NEW Person
                // t[0]=Person t[1]=p t[2]=eq t[3]=NEW t[4]=Person
                if (t.length < 5) throw new LanguageException("Invalid instantiation syntax");
                Executor.instantiateObject(t[0], t[1], t[4], context);
            } else {
                // Primitive Declaration
                String valStr = t[3]; 
                Executor.declareVariable(t[0], t[1], valStr, context);
            }
            return;
        }

        switch (cmd) {
            case "SET": 
                // SET x = 10
                 String valStr = t[3]; // Assumes simple assignment
                 Executor.setValue(t[1], valStr, context); 
                 break;
            case "ADD": Executor.addValue(t, context); break;
            case "DIV": Executor.divideValue(t, context); break;
            case "PRINT": Executor.printValue(t, context); break;
            default: 
                 if (context.hasClass(cmd)) {
                     // Class Declaration: Person p = NEW ...
                     if (t.length >= 5 && t[2].equals("=") && t[3].equals("NEW")) {
                         Executor.instantiateObject(cmd, t[1], t[4], context);
                         return;
                     }
                 }
                 throw new LanguageException("Unknown command: " + cmd);
        }
    }
    
    // Custom tokenizer that respects quoted strings AND commas
    private static String[] tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                sb.append(c);
            } else if ((Character.isWhitespace(c) || c == ',') && !inQuotes) {
                if (sb.length() > 0) {
                    tokens.add(sb.toString());
                    sb.setLength(0);
                }
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }
    
    private static boolean isType(String s) {
        return s.equals("int") || s.equals("float") || s.equals("double") || 
               s.equals("boolean") || s.equals("char") || s.equals("string");
    }

    private static int handleTryBlock(List<String> lines, int index, Context context) throws LanguageException {
        int i = index + 1;
        boolean errorOccurred = false;

        // Execute TRY block
        try {
            while (i < lines.size() && !lines.get(i).trim().equals("CATCH")) {
                String line = lines.get(i).trim();
                
                if (!errorOccurred) {
                     if (line.startsWith("IF")) {
                         i = handleIfBlock(lines, i, context);
                     } else if (line.startsWith("WHILE")) {
                         i = handleWhileBlock(lines, i, context);
                     } else if (!line.isEmpty()) {
                         if (!line.endsWith(";")) throw new LanguageException("Missing semicolon: " + line);
                         executeLine(line.substring(0, line.length()-1).trim(), context);
                     }
                }
                
                if (!line.startsWith("IF") && !line.startsWith("WHILE")) {
                    i++;
                }
            }
        } catch (Exception e) { 
             errorOccurred = true;
             while(i < lines.size() && !lines.get(i).trim().equals("CATCH")) i++;
        }

        // Handle CATCH
        if (i < lines.size() && lines.get(i).trim().equals("CATCH")) {
            i++; 
            while (i < lines.size() && !lines.get(i).trim().equals("FINALLY")) {
                if (errorOccurred) {
                    String line = lines.get(i).trim();
                    if (!line.isEmpty()) {
                        if (!line.endsWith(";")) throw new LanguageException("Missing semicolon in CATCH");
                        try {
                           executeLine(line.substring(0, line.length()-1).trim(), context);
                        } catch(Exception ignored) {}
                    }
                }
                i++;
            }
        }
        
        // Handle FINALLY
        if (i < lines.size() && lines.get(i).trim().equals("FINALLY")) {
            i++;
            while (i < lines.size() && !lines.get(i).trim().equals("ENDTRY")) {
                String line = lines.get(i).trim();
                 if (!line.isEmpty()) {
                        if (!line.endsWith(";")) throw new LanguageException("Missing semicolon in FINALLY");
                        try {
                           executeLine(line.substring(0, line.length()-1).trim(), context);
                        } catch(Exception ignored) {}
                 }
                i++;
            }
        }

        return i; 
    }

    private static int handleIfBlock(List<String> lines, int index, Context context) throws LanguageException {
        String conditionLine = lines.get(index).trim(); // IF x > 10 (no semicolon usually)
        String[] parts = tokenize(conditionLine);
        boolean condition = Executor.checkCondition(parts, context);
        
        int i = index + 1;
        boolean executing = condition;

        while (i < lines.size() && !lines.get(i).trim().equals("ENDIF")) {
            String line = lines.get(i).trim();
            
            if (line.equals("ELSE")) {
                executing = !condition; 
                i++;
                continue;
            }

            if (executing) {
                if (line.startsWith("IF")) {
                    i = handleIfBlock(lines, i, context);
                } else if (line.startsWith("WHILE")) {
                    i = handleWhileBlock(lines, i, context);
                } else if (!line.isEmpty()) {
                     if (!line.endsWith(";")) throw new LanguageException("Missing semicolon: " + line);
                     executeLine(line.substring(0, line.length()-1).trim(), context);
                }
            } else {
                if (line.startsWith("IF") || line.startsWith("WHILE")) {
                     i = skipBlock(lines, i);
                }
            }
            
            if (!line.startsWith("IF") && !line.startsWith("WHILE") && !line.equals("ELSE")) {
               i++;
            }
        }
        return i;
    }
    
    private static int handleWhileBlock(List<String> lines, int index, Context context) throws LanguageException {
        int startIndex = index;
        String conditionLine = lines.get(startIndex).trim();
        String[] parts = tokenize(conditionLine);
        
        while (Executor.checkCondition(parts, context)) {
            int i = startIndex + 1;
            while (i < lines.size() && !lines.get(i).trim().equals("ENDWHILE")) {
                 String line = lines.get(i).trim();
                 if (line.isEmpty()) { i++; continue; }
                 
                 if (line.startsWith("IF")) {
                     i = handleIfBlock(lines, i, context);
                 } else if (line.startsWith("WHILE")) {
                     i = handleWhileBlock(lines, i, context); 
                 } else {
                     if (!line.endsWith(";")) throw new LanguageException("Missing semicolon: " + line);
                     executeLine(line.substring(0, line.length()-1).trim(), context);
                 }
                 
                 if (!line.startsWith("IF") && !line.startsWith("WHILE")) {
                     i++;
                 }
            }
        }

        return skipBlock(lines, startIndex);
    }
    
    private static int skipBlock(List<String> lines, int index) {
        int i = index + 1;
        int depth = 1;
        String startKey = lines.get(index).trim().split("\\s+")[0];
        String endKey = "END" + startKey; // ENDIF, ENDWHILE, ENDCLASS
        
        if (startKey.equals("TRY")) endKey = "ENDTRY"; // Exception to rule if needed, but safe
        
        while (i < lines.size() && depth > 0) {
            String line = lines.get(i).trim();
            if (line.startsWith(startKey)) depth++;
            if (line.equals(endKey)) depth--;
            i++;
        }
        return i - 1;
    }
}
