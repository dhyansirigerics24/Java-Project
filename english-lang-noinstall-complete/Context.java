import java.util.HashMap;

public class Context {

    private HashMap<String, Integer> variables;

    public Context() {
        variables = new HashMap<>();
    }

    public void setVariable(String key, int value) {
        variables.put(key, value);
    }

    public int getVariable(String key) {
        return variables.getOrDefault(key, 0);
    }
}
