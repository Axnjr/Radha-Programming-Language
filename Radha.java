import java.util.*;

class RadhaProgrammingLanguage extends BuiltInFunctions_RadhaProgrammingLanguage {
    private String CODE;
    private final String LETTERS = "^[a-zA-Z]*$";
    private final String NUMBERS = "[0-9]";
    private final List<String> KEYWORDS = Arrays.asList("for", "in", "if", "is", "else", "def", "and", "or", "brk:", "con:", "ret:", "True", "False", "None", "not");
    private Map<String, Object> VARIABLES; // hash map to store variables.
    private Map<String, FunctionObject> FUNCTIONS; // hash map to store user defined functions.
    private Stack<String> CURRENTLY_RUNNING_FUNCTION_STACK; // stack to hold currently executing function.

    public RadhaProgrammingLanguage(String code) {
        super();
        VARIABLES = new HashMap<>();
        FUNCTIONS = new HashMap<>();
        CURRENTLY_RUNNING_FUNCTION_STACK = new Stack<>();

        String[] tempCode = code.split("\n\\s");

        for (int i = 0; i < tempCode.length; i++) {
            if (!isValid(tempCode[i])) {
                throw new RuntimeException("Parenthesis don't match at line " + i + " you might have forgotten a closing or opening bracket !!");
            }
        }

        this.CODE = code.replace(" ", "");
        MainProgramThread(0, this.CODE);
    }

    private boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for (char l : s.toCharArray()) {
            int index = "({[]})".indexOf(l);
            if (index > -1) {
                if (!stack.isEmpty() && stack.peek() == "({[})]".charAt(index)) {
                    stack.pop();
                } else {
                    stack.push(l);
                }
            }
        }
        return stack.isEmpty();
    }

    private boolean CheckIfString(String str) {
        int l = str.length() - 1;
        return (str.charAt(0) == '"' || str.charAt(0) == '\'') && (str.charAt(l) == '"' || str.charAt(l) == '\'');
    }

    private void MainProgramThread(int index, String script) {
        String c = script != null ? script : this.CODE;

        for (int pointer = index; pointer < c.length(); pointer++) {

            if (Character.isWhitespace(c.charAt(pointer))) {
                pointer++; // skipping white spaces
            }

            if (c.charAt(pointer) == '#') { // it's a comment
                while (pointer < c.length()) {
                    if (c.charAt(pointer) == '\n') {
                        break;
                    }
                    pointer++;
                }
            }

            if (c.charAt(pointer) == '[') { // assignment of variables
                if (c.charAt(pointer + 1) == ']') {
                    break; // in case of empty declaration [] like this we must break else loop
                }
                pointer++;
                StringBuilder varName = new StringBuilder();
                StringBuilder varValue = new StringBuilder();

                while (true) { // for variable name
                    if (c.charAt(pointer) == '=') {
                        break;
                    }
                    varName.append(c.charAt(pointer));
                    pointer++;
                }

                if (KEYWORDS.contains(varName.toString())) { // variable name cannot be the same as the name of keywords.
                    throw new RuntimeException("Syntax Error: invalid syntax \"" + varName + "\" is a reserved keyword it cannot be used !!");
                }
                pointer++; // move ahead

                if (c.substring(pointer, pointer + 2).equals("[[")) { // array is declared
                    pointer++;
                    varValue.append(ArrayDeclaration(pointer, c));
                    VARIABLES.put(varName.toString(), varValue.toString());
                } else if (c.substring(pointer, pointer + 2).equals("{{")) {  // dictionary / object is declared.
                    pointer++;
                    while (true) {
                        varValue.append(c.charAt(pointer));
                        if (c.charAt(pointer) == '}') {
                            break;
                        }
                        pointer++;
                    }
                    VARIABLES.put(varName.toString(), parseToMap(varValue.toString()));
                } else { // integer or string is declared
                    while (true) {
                        if (c.charAt(pointer) == ']') {
                            break;
                        }
                        varValue.append(c.charAt(pointer));
                        pointer++;
                    }

                    if (VARIABLES.containsKey(varValue.charAt(0)) && varValue.charAt(1) == '[') {
                        varValue = new StringBuilder(ReturnPropertyOfObject(varValue.toString()));
                    }

                    if (builtInFunctions.contains(varValue.toString().split("\\(")[0].replaceAll("[{}]", ""))) {
                        String fn = varValue.toString().split("\\(")[0].replaceAll("[{}]", "");
                        String pr = varValue.toString().split("\\(")[1].replaceAll("[{}]", "");
                        varValue = new StringBuilder(ExecuteFunction(fn, pr));
                    } else {
                        varValue = new StringBuilder(ProcessData(varValue.toString()));
                    }
                    VARIABLES.put(varName.toString(), varValue.toString());
                }
            }
            if (c.charAt(pointer) == '{') {
                HandleDefinedFunctions(pointer + 1, c);
                break;
            }

            if (c.substring(pointer, pointer + 3).equals("for")) {
                ForLoopInterpreter(pointer + 3, c);
                break;
            }

            String k = c.substring(pointer, pointer + 4);
            if (k.equals("ret:")) { // return statement.
                if (!CURRENTLY_RUNNING_FUNCTION_STACK.isEmpty()) {
                    String fnName = CURRENTLY_RUNNING_FUNCTION_STACK.peek();
                    String ret_val = ProcessData(LoopTillGivenCharacter(pointer + 4, ":", c)[1]);
                    VARIABLES.put(fnName, ret_val);
                    CURRENTLY_RUNNING_FUNCTION_STACK.pop();
                }
            }

            if (c.substring(pointer, pointer + 2).equals("if")) {
                IfInterpreter(pointer + 2, c);
                break;
            }

            if (c.substring(pointer, pointer + 3).equals("def")) {
                RegisterUserDefinedFunctions(pointer + 3);
                break;
            }
        }
    }

    private String ArrayDeclaration(int pointer, String c) {
        StringBuilder varValue = new StringBuilder();
        pointer++;
        while (!c.substring(pointer, pointer + 2).equals("]]")) {
            varValue.append(c.charAt(pointer));
            if (pointer > this.CODE.length()) {
                break;
            }
            pointer++;
        }
        return "[" + varValue.toString() + "]";
    }

    private String ReturnElementAtIndexOfArray(String varValue) {
        String[] parts = varValue.split("\\[");
        if (VARIABLES.containsKey(parts[0])) {
            List<Object> temp = (List<Object>) VARIABLES.get(parts[0]); // array whose element is required
            int index = Integer.parseInt(parts[1].replace("]", "")); // index of element to be searched
            if (!(temp instanceof List)) {
                throw new RuntimeException("TypeError: \"" + type(temp.toString()) + "\" object is not subscriptable.");
            }
            if (index >= temp.size()) {
                throw new RuntimeException("IndexError: list index out of range");
            }
            return temp.get(index).toString();
        }
        return null;
    }

    private String ReturnPropertyOfObject(String varValue) {
        String[] parts = varValue.split("\\[");
        if (VARIABLES.containsKey(parts[0])) {
            Map<String, Object> temp = (Map<String, Object>) VARIABLES.get(parts[0]); // dictionary/object
            String key = parts[1].replace("]", "");
            if (!(temp instanceof Map)) {
                throw new RuntimeException("TypeError: \"" + type(temp.toString()) + "\" object is not subscriptable.");
            }
            if (!temp.containsKey(key)) {
                throw new RuntimeException("KeyError: '" + key + "'");
            }
            return temp.get(key).toString();
        }
        return null;
    }

    private void HandleDefinedFunctions(int pointer, String c) {
        // You need to add code here to handle custom-defined functions
        // This will require you to keep track of function definitions and invoke them later
    }

    private void ForLoopInterpreter(int pointer, String c) {
        // Add logic to handle for loops here
    }

    private void IfInterpreter(int pointer, String c) {
        // Add logic to handle if statements here
    }

    private void RegisterUserDefinedFunctions(int pointer) {
        // Add logic to register user-defined functions
    }

    private String LoopTillGivenCharacter(int index, String character, String c) {
        StringBuilder result = new StringBuilder();
        while (index < c.length()) {
            if (String.valueOf(c.charAt(index)).equals(character)) {
                break;
            }
            result.append(c.charAt(index));
            index++;
        }
        return result.toString();
    }

    private String ProcessData(String data) {
        // Process data (e.g., handling strings, integers, variables, etc.)
        return data;
    }

    private Map<String, Object> parseToMap(String data) {
        // Logic to convert a string into a Map
        return new HashMap<>();
    }

    private String ExecuteFunction(String functionName, String params) {
        int index = builtInFunctions.indexOf(functionName);
        if (index != -1) {
            return FunctionCaller(index, params).toString();
        }
        return null;
    }
}