const vscode = require("vscode");

function activate(context) {

  // Create diagnostic collection for ENG language
  const diagnostics = vscode.languages.createDiagnosticCollection("eng");
  context.subscriptions.push(diagnostics);

  // Validate when file is opened
  vscode.workspace.onDidOpenTextDocument(doc => {
    if (doc.languageId === "eng") {
      validate(doc, diagnostics);
    }
  });

  // Validate when file is edited
  vscode.workspace.onDidChangeTextDocument(event => {
    if (event.document.languageId === "eng") {
      validate(event.document, diagnostics);
    }
  });
}

function validate(document, diagnostics) {

  const errors = [];
  const lines = document.getText().split("\n");

  const validCommands = [
    "SET",
    "ADD",
    "DIV",
    "PRINT",
    "TRY",
    "CATCH",
    "FINALLY",
    "ENDTRY"
  ];

  lines.forEach((line, index) => {

    const trimmed = line.trim();
    if (trimmed === "") return;

    const command = trimmed.split(/\s+/)[0];

    if (!validCommands.includes(command)) {
      const range = new vscode.Range(
        index,
        0,
        index,
        line.length
      );

      const diagnostic = new vscode.Diagnostic(
        range,
        `Unknown command: ${command}`,
        vscode.DiagnosticSeverity.Error
      );

      errors.push(diagnostic);
    }
  });

  diagnostics.set(document.uri, errors);
}

function deactivate() {}

module.exports = {
  activate,
  deactivate
};
