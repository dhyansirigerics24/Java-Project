import java.util.*;

public class ClassDefinition {
    private String name;
    private Map<String, String> fields; // name -> type
    // For now, methods are just starting lines or block references? 
    // Or we keep it simple: methods aren't stored here for the first pass, 
    // but we can parse them. Let's assume methods are stored as a list of lines for now.
    // However, the parser handles execution. 
    // Let's store variables structure for instantiation.
    
    public ClassDefinition(String name) {
        this.name = name;
        this.fields = new HashMap<>();
    }

    public void addField(String name, String type) {
        fields.put(name, type);
    }
    
    public Map<String, String> getFields() {
        return fields;
    }

    public String getName() {
        return name;
    }
}
