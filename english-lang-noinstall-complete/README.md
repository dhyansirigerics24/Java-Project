# English Language Syntax Interpreter

A lightweight Java-based interpreter for a custom, English-like scripting language. This project demonstrates language parsing, execution contexts, and exception handling mechanisms (Try-Catch-Finally) using pure Java.

## 📂 Project Structure

- **Main.java**: The entry point of the application. Reads the `.eng` file and initiates the interpreter.
- **Interpreter.java**: Coordinates the parsing and execution flow.
- **Parser.java**: parses the script line by line and handles control flow structures like `TRY-CATCH`.
- **Executor.java**: Executes individual commands (SET, ADD, DIV, PRINT).
- **Context.java**: Manages the state and variable storage (Symbol Table).
- **programs/**: Contains example scripts (e.g., `demo.eng`).

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)** installed on your machine.
- No external libraries or installation required (No-Install).

### Compilation

Navigate to the project directory and compile all Java files:

```bash
javac *.java
```

### Running a Script

Run the `Main` class passing the path to your script file as an argument:

```bash
java Main programs/demo.eng
```

## 📖 Syntax Guide

The language supports basic variable manipulation and error handling.

### Variables & Arithmetic

*   **SET**: Assign a value to a variable.
    ```text
    SET x = 10
    ```
*   **ADD**: Add the value of one variable to another.
    ```text
    ADD x y  (x = x + y)
    ```
    *Note: The second operand must be a variable name.*
*   **DIV**: Divide a variable by another.
    ```text
    DIV x y  (x = x / y)
    ```
*   **PRINT**: Print the name and value of a variable.
    ```text
    PRINT x
    ```

### Error Handling

The language supports `TRY`, `CATCH`, `FINALLY`, and `ENDTRY` blocks.

- **TRY**: Code within this block is executed. If a runtime error (like division by zero) occurs, execution jumps to the CATCH block.
- **CATCH**: Executed if an error occurs in the TRY block.
- **FINALLY**: Always executed after TRY (or CATCH), regardless of errors.

**Example (`programs/demo.eng`):**

```text
SET x = 10
SET y = 0

TRY
    DIV x y    (Division by Zero -> Triggers Error)
    PRINT x
CATCH
    PRINT ERROR
FINALLY
    PRINT DONE
ENDTRY

PRINT x
```

## ⚠️ Notes

- The `CATCH` and `FINALLY` blocks in this implementation currently simulate execution by printing hardcoded status messages ("Error handled", "Finally executed") rather than executing the specific lines inside them.
- Variable lookups default to `0` if the variable is not defined.

## 🤝 Contributing

Feel free to fork this project and submit pull requests. You can extend the `Executor` to add more math operations or improve the `Parser` to support nested blocks!
