public class Main {

    public static void main(String[] args) {

        try {
            if (args.length == 0) {
                throw new LanguageException("Please provide a .eng file");
            }

            Interpreter interpreter = new Interpreter(); // object creation
            interpreter.execute(args[0]);

        } catch (LanguageException e) {
            System.out.println("Fatal Error: " + e.getMessage());

        } finally {
            System.out.println("Program execution finished.");
        }
    }
}
