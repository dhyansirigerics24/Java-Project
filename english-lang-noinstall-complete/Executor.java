public class Executor {

    public static void setValue(String[] t, Context c) throws LanguageException {
        if (t.length != 4)
            throw new LanguageException("Invalid SET syntax");

        c.setVariable(t[1], Integer.parseInt(t[3]));
    }

    public static void addValue(String[] t, Context c) {
        int result = c.getVariable(t[1]) + c.getVariable(t[2]);
        c.setVariable(t[1], result);
    }

    public static void divideValue(String[] t, Context c) throws LanguageException {
        int divisor = c.getVariable(t[2]);
        if (divisor == 0)
            throw new LanguageException("Division by zero");

        c.setVariable(t[1], c.getVariable(t[1]) / divisor);
    }

    public static void printValue(String[] t, Context c) {
        System.out.println(t[1] + " = " + c.getVariable(t[1]));
    }
}
