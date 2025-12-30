import java.util.*;

public class Executor {

    public static void executeAssignment(String[] parts, Context c) throws LanguageException {
        // Syntax: 
        // 1. SET var = value; (Old way, we can keep it but restrict to existing vars)
        // 2. TYPE var = value; (New declaration)
        // 3. var.field = value; (Object field assignment)
        
        // Let's decide how Parser handles this. 
        // Parser is likely sending tokenized lines. 
        // Simplest: 
        // Check if parts[0] is a Type (int, float, etc.) -> Declaration
        // Check if parts[0] is "SET" -> Legacy/Reassignment
        // Check if parts[0] contains "." -> Object member assignment (handled in SET path for now perhaps?)
        
        // But the previous parser uses switch on first token.
        // We will assume Parser calls different methods or we handle strictly here.
        // Based on plan, we have `declareVariable`.
    }

    // Helper for primitives
    public static void declareVariable(String type, String name, String valueStr, Context c) throws LanguageException {
        if (c.hasVariable(name)) {
            throw new LanguageException("Variable '" + name + "' already declared");
        }
        
        Value val = parseValue(valueStr, type, c);
        c.setVariable(name, val);
    }
    
    // For manual SET or updates
    public static void setValue(String name, String valueStr, Context c) throws LanguageException {
        // Handle object field: p.name
        if (name.contains(".")) {
            String[] split = name.split("\\.");
            String objName = split[0];
            String fieldName = split[1];
            
            if (!c.hasVariable(objName)) throw new LanguageException("Object '" + objName + "' not found");
            Value objVal = c.getVariable(objName);
            if (objVal.getValue() instanceof Map) {
               @SuppressWarnings("unchecked")
               Map<String, Value> fields = (Map<String, Value>) objVal.getValue();
               if (!fields.containsKey(fieldName)) throw new LanguageException("Field '" + fieldName + "' not found on " + objName);
               
               // We need to know the type from the ClassDefinition? 
               // Or simpler: just check current value type? 
               // For proper typing, we should look up the class.
               // But here, let's just parse value based on existing field type.
               String expectedType = fields.get(fieldName).getType();
               Value newVal = parseValue(valueStr, expectedType, c);
               fields.put(fieldName, newVal);
            } else {
               throw new LanguageException(objName + " is not an object");
            }
            return;
        }

        if (!c.hasVariable(name)) {
             throw new LanguageException("Variable '" + name + "' not declared. Use type to declare first.");
        }
        Value current = c.getVariable(name);
        Value newVal = parseValue(valueStr, current.getType(), c);
        c.setVariable(name, newVal);
    }

    public static void addValue(String[] t, Context c) throws LanguageException {
        // ADD target operand (Legacy)
        // ADD target op1 op2 (New)
        
        String targetName = t[1];
        if (!c.hasVariable(targetName)) throw new LanguageException("Variable " + targetName + " not found");
        Value target = c.getVariable(targetName);
        
        Value v1, v2;
        
        if (t.length >= 4) {
             // ADD x x,5 -> t=["ADD", "x", "x", "5"] (comma handled by tokenizer)
             v1 = getValue(t[2], c);
             v2 = getValue(t[3], c);
        } else {
             // Legacy: ADD x 5 -> x = x + 5
             v1 = target; // Use current value as first operand
             v2 = getValue(t[2], c);
        }

        if (target.getType().equals("int") && v1.getType().equals("int") && v2.getType().equals("int")) {
            target.setValue((int)v1.getValue() + (int)v2.getValue());
        } else if ((target.getType().equals("float") || target.getType().equals("int")) && 
                   (isNumber(v1) && isNumber(v2))) {
            float val1 = Float.parseFloat(v1.getValue().toString());
            float val2 = Float.parseFloat(v2.getValue().toString());
            
            if (target.getType().equals("int")) {
                 target.setValue((int)(val1 + val2));
            } else {
                 target.setValue(val1 + val2);
            }
        } else if (target.getType().equals("string")) {
            target.setValue(v1.getValue().toString() + v2.getValue().toString());
        } else {
             throw new LanguageException("Cannot ADD types in compatible way");
        }
    }
    
    private static boolean isNumber(Value v) {
        return v.getType().equals("int") || v.getType().equals("float");
    }

    public static void divideValue(String[] t, Context c) throws LanguageException {
        Value target = c.getVariable(t[1]);
        Value divisor = getValue(t[2], c);
        
        double d = Double.parseDouble(divisor.getValue().toString());
        if (d == 0) throw new LanguageException("Division by zero");
        
        double n = Double.parseDouble(target.getValue().toString());
        double result = n / d;
        
        if (target.getType().equals("int")) {
            target.setValue((int)result);
        } else {
            target.setValue(result);
        }
    }

    public static void printValue(String[] t, Context c) throws LanguageException {
        if (t.length < 2) return;
        String key = t[1];
        if (c.hasVariable(key)) {
            System.out.println(c.getVariable(key).getValue());
        } else if (key.contains(".")) {
             Value v = getValue(key, c);
             System.out.println(v.getValue());
        } else {
            // Literal or String
             if (key.startsWith("\"") && key.endsWith("\"")) {
                 System.out.println(key.substring(1, key.length()-1));
             } else {
                 System.out.println(key);
             }
        }
    }

    public static boolean checkCondition(String[] t, Context c) throws LanguageException {
        Value v1 = getValue(t[1], c);
        Value v2 = getValue(t[3], c);
        String op = t[2];
        
        // Assume numeric comparison for now
        double d1 = Double.parseDouble(v1.getValue().toString());
        double d2 = Double.parseDouble(v2.getValue().toString());

        switch (op) {
            case "==": return d1 == d2;
            case ">": return d1 > d2;
            case "<": return d1 < d2;
            case ">=": return d1 >= d2;
            case "<=": return d1 <= d2;
            case "!=": return d1 != d2;
            default: return false;
        }
    }
    
    // NEW for instantiation
    public static void instantiateObject(String type, String varName, String className, Context c) throws LanguageException {
         // NEW Person p -> not quite syntax "Person p = NEW Person"
         // Logic checks
         if (c.hasVariable(varName)) throw new LanguageException("Variable " + varName + " already defined");
         if (!c.hasClass(className)) throw new LanguageException("Class " + className + " not defined");
         
         ClassDefinition def = c.getClass(className);
         
         // Create object structure: Map<String, Value>
         Map<String, Value> fields = new HashMap<>();
         for (Map.Entry<String, String> entry : def.getFields().entrySet()) {
             // Initialize fields with defaults
             String fName = entry.getKey();
             String fType = entry.getValue();
             fields.put(fName, getDefaultValue(fType));
         }
         
         Value objVal = new Value(fields, className); // Type is ClassName
         c.setVariable(varName, objVal);
    }

    private static Value parseValue(String valStr, String type, Context c) throws LanguageException {
        // Handle "NEW ClassName" ? No, that's done via instantiation command usually.
        // If type is "int" ...
         try {
            if (valStr.startsWith("\"") && type.equals("string")) {
                return new Value(valStr.substring(1, valStr.length()-1), "string");
            }
            if (type.equals("int")) return new Value(Integer.parseInt(valStr), "int");
            if (type.equals("float")) return new Value(Float.parseFloat(valStr), "float");
            if (type.equals("double")) return new Value(Double.parseDouble(valStr), "double");
            if (type.equals("boolean")) return new Value(Boolean.parseBoolean(valStr), "boolean");
            
            // Checks for variable references in RHS
            if (c.hasVariable(valStr)) {
                Value other = c.getVariable(valStr);
                if (!other.getType().equals(type)) throw new LanguageException("Type mismatch: " + type + " vs " + other.getType());
                return new Value(other.getValue(), type); // Copy value?
            }
            
            throw new LanguageException("Invalid value " + valStr + " for type " + type);
        } catch (NumberFormatException e) {
             throw new LanguageException("Invalid number format for " + valStr);
        }
    }

    private static Value getValue(String s, Context c) throws LanguageException {
        if (c.hasVariable(s)) return c.getVariable(s);
        
        // Handle object.field
        if (s.contains(".")) {
             String[] parts = s.split("\\.");
             String obj = parts[0];
             String field = parts[1];
             if (c.hasVariable(obj)) {
                 Value val = c.getVariable(obj);
                 if (val.getValue() instanceof Map) {
                     @SuppressWarnings("unchecked")
                     Map<String, Value> map = (Map<String, Value>) val.getValue();
                     if (map.containsKey(field)) return map.get(field);
                 }
             }
        }
        
        // Try literals if not found
        if (s.matches("-?\\d+")) return new Value(Integer.parseInt(s), "int");
        if (s.matches("-?\\d*\\.\\d+")) return new Value(Float.parseFloat(s), "float");
        if (s.equals("true") || s.equals("false")) return new Value(Boolean.parseBoolean(s), "boolean");
        if (s.startsWith("\"")) return new Value(s.substring(1, s.length()-1), "string");
        
        throw new LanguageException("Unknown variable or value: " + s);
    }
    
    private static Value getDefaultValue(String type) {
        switch(type) {
            case "int": return new Value(0, "int");
            case "float": return new Value(0.0f, "float");
            case "string": return new Value("", "string");
            case "boolean": return new Value(false, "boolean");
            default: return new Value(null, type);
        }
    }
}
