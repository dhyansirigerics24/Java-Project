import java.util.HashMap;
import java.util.Map;

public class Context {

    private HashMap<String, Value> variables;
    private HashMap<String, ClassDefinition> classDefinitions;

    public Context() {
        variables = new HashMap<>();
        classDefinitions = new HashMap<>();
    }

    public void setVariable(String key, Value value) {
        variables.put(key, value);
    }

    public Value getVariable(String key) {
        return variables.get(key);
    }

    public boolean hasVariable(String key) {
        return variables.containsKey(key);
    }
    
    public void defineClass(String name, ClassDefinition def) {
        classDefinitions.put(name, def);
    }
    
    public ClassDefinition getClass(String name) {
        return classDefinitions.get(name);
    }
    
    public boolean hasClass(String name) {
        return classDefinitions.containsKey(name);
    }
}
