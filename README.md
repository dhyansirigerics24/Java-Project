# ENG Language Project

Welcome to the **ENG Language** project! This repository contains a complete ecosystem for a custom, English-like scripting language, including a Java-based interpreter and developer tooling (VS Code extension).

## 📂 Repository Structure

*   **`english-lang-noinstall-complete/`**: The Core Interpreter. Contains the Java source code to parse and execute `.eng` scripts.
*   **`eng-language-syntax/`**: Developer Tools. A Visual Studio Code extension providing syntax highlighting for `.eng` files.

---

## ☕ 1. The Interpreter

Located in `english-lang-noinstall-complete/`, this is a lightweight, no-install interpreter written in Java.

### Key Features
*   **Simple Syntax**: Write code using English words like `SET`, `ADD`, `DIV`, `PRINT`.
*   **Error Handling**: Robust `TRY-CATCH-FINALLY` blocks.
*   **Zero Dependencies**: Runs on any machine with standard Java installed.

### Quick Start
1.  Navigate to the interpreter directory:
    ```bash
    cd english-lang-noinstall-complete
    ```
2.  Compile the source:
    ```bash
    javac *.java
    ```
3.  Run the demo script:
    ```bash
    java Main programs/demo.eng
    ```

*(See the `english-lang-noinstall-complete/README.md` for detailed documentation on syntax and usage.)*

---

## 🎨 2. VS Code Extension

Located in `eng-language-syntax/`, this extension enhances the development experience for `.eng` files.

### Features
*   **Syntax Highlighting**: Colorizes keywords (`SET`, `TRY`, `CATCH`), variables, and numbers for better readability.
*   **File Recognition**: Automatically detects `.eng` files.

### Installation
To test or use this extension locally:
1.  Open the `eng-language-syntax` folder in VS Code.
2.  Press **F5** to open a new "Extension Development Host" window with the extension loaded.
3.  Open any `.eng` file to see the highlighting in action!

---

## 📝 Example Code

Here is a snippet of valid `ENG` code:

```text
SET x = 10
SET y = 5

TRY
    DIV x y
    PRINT x
CATCH
    PRINT ERROR
FINALLY
    PRINT DONE
ENDTRY
```

## 📄 License
[Include your License here, e.g., MIT]
