# English Language Syntax Interpreter

A lightweight Java-based interpreter for a custom, English-like scripting language. This project demonstrates language parsing, execution contexts, typed variables, and Object-Oriented features using pure Java.

## 📂 Project Structure

- **Main.java**: Entry point. Reads `.eng` files and starts the interpreter.
- **Interpreter.java**: Coordinates parsing and execution.
- **Parser.java**: Custom tokenizer and parser. Handles control flow (`IF`, `WHILE`, `TRY`) and command dispatch.
- **Executor.java**: Executes commands (`SET`, `ADD`, `PRINT`, `NEW`) and handles logic.
- **Context.java**: Manages the symbol table (variables and class definitions).
- **Value.java**: Represents typed values (`int`, `float`, `string`, `boolean`, `Object`).
- **ClassDefinition.java**: Stores class structures (fields).
- **programs/**: Contains example scripts (e.g., `demo.eng`).

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)** installed.
- No external libraries required.

### Compilation

```bash
javac *.java
```

### Running a Script

```bash
java Main programs/demo.eng
```

## 📖 Syntax Guide

**Important:** All statements must end with a semicolon `;`.

### 1. Variables & Types
You must declare variables with a type (`int`, `float`, `string`, `boolean`, `char`, `double`).

```text
int x = 10;
string name = "Hello";
boolean isValid = true;
```

### 2. Math & Logic
*   **ADD**, **DIV**: Modify the first variable in place.
    ```text
    ADD x 5;      // x = x + 5
    DIV y 2;      // y = y / 2
    ADD s " World"; // String concatenation
    ```
*   **PRINT**: output the value of a variable or literal.
    ```text
    PRINT x;
    PRINT "Done";
    ```

### 3. Classes and Objects
Define classes with fields and instantiate them using `NEW`.

```text
CLASS Person
    string name;
    int age;
ENDCLASS

Person p = NEW Person;
SET p.name = "Alice";
PRINT p.name;
```

### 4. Control Flow
Standard structures (`IF`, `WHILE`) are supported. Semicolons are required for statements *inside* the blocks, but not for the headers.

```text
IF x > 10
    PRINT "High";
ELSE
    PRINT "Low";
ENDIF

WHILE count > 0
    ADD count -1;
ENDWHILE
```

### 5. Error Handling
`TRY`, `CATCH`, `FINALLY` blocks capture runtime errors (e.g., division by zero).

```text
TRY
    DIV x 0;
CATCH
    PRINT "Error caught";
FINALLY
    PRINT "Cleanup";
ENDTRY
```
