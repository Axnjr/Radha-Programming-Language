import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Optional;

class FunctionObject 
{
    public List<String> params;
    public String body;

    public FunctionObject(List<String> params, String body) {
        this.params = params;
        this.body = body;
    }

    public String getCode() {
        return this.body;
    }

    public List<String> getParamList() {
        return this.params;
    }
}

public class RadhaProgrammingLanguage extends BuiltInFunctions_RadhaProgrammingLanguage {

    private String CODE;
    private final String LETTERS = "^[a-zA-Z]*$";
    // private final String NUMBERS = "[0-9]";
    private final List<String> KEYWORDS = Arrays.asList(
        "for", "in", "if", "is", "else", "def", "and", "or", "brk:",
        "con:", "ret:", "True", "False", "None", "not"
    );
    private Map<String, Object> VARIABLES; // hash map to store variables.
    private Map<String, FunctionObject> FUNCTIONS; // hash map to store user defined functions.
    private Stack<String> CURRENTLY_RUNNING_FUNCTION_STACK; // stack to hold currently executing function.

    private ScriptEngine engine;

    public RadhaProgrammingLanguage(String code) throws Exception {

        super();

        VARIABLES = new HashMap<>();
        FUNCTIONS = new HashMap<>();
        CURRENTLY_RUNNING_FUNCTION_STACK = new Stack<>();

        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("JavaScript");

        this.print(code);

        String[] tempCode = code.split("\\n\\s+");

        for (int i = 0; i < tempCode.length; i++) {
            // this.print(tempCode[i]);
            Boolean t = this.isValid(tempCode[i]);
            this.print("ANS:  "+String.valueOf(t));
            if (!t) {
                throw new RuntimeException(
                    "[RADHA ERROR]: Parenthesis don't match at line " + i +
                    " you might have forgotten a closing or opening bracket !!"
                );
            }
        }

        this.CODE = code.replace(" ", "");
        this.MainProgramThread(0, this.CODE);
    }

    private boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for (char l : s.toCharArray()) {
            if (l == '(' || l == '{' || l == '[') {
                stack.push(l);  // Push the opening brackets onto the stack
            } else if (l == ')' && !stack.isEmpty() && stack.peek() == '(') {
                stack.pop();    // Pop if a matching opening bracket is found
            } else if (l == '}' && !stack.isEmpty() && stack.peek() == '{') {
                stack.pop();
            } else if (l == ']' && !stack.isEmpty() && stack.peek() == '[') {
                stack.pop();
            } else {
                return false;   // If no matching opening bracket, it's invalid
            }
        }
        this.print(stack.empty() ? "YES" : "NO");
        return stack.isEmpty(); // Valid if stack is empty
    }

    private boolean CheckIfString(String str) {
        int l = str.length() - 1;
        return (str.charAt(0) == '"' || str.charAt(0) == '\'') && (str.charAt(l) == '"' || str.charAt(l) == '\'');
    }

    private Object[] LoopTillGivenCharacter(
        int pointer, 
        char tillCharacter, 
        Optional<String> optionalScript, 
        Optional<String> optionalErrorMessage, 
        Optional<Boolean> optionalCondition
    ) throws Exception 
    {
        StringBuilder returnValue = new StringBuilder();

        Boolean cond = optionalCondition.orElse(false);
        // If optionalScript is not provided, use the entire code
        String script = optionalScript.orElse(this.CODE);
        String error = optionalErrorMessage.orElse(null);
    
        // Loop through the script until the specified character is found
        while (true) 
        {
            if (pointer >= script.length()) {
                break;
            }
    
            if (script.charAt(pointer) == tillCharacter) {
                if (cond) {
                    returnValue.append(script.charAt(pointer));
                }
                break;
            }
    
            if (pointer > this.CODE.length()) {
                if (error == null) {
                    break;
                } 
                else {
                    throw new Exception(error);
                }
            }
    
            returnValue.append(script.charAt(pointer));
            pointer++;
        }
    
        return new Object[]{pointer, returnValue.toString()};
    }

    private void MainProgramThread(int index, String script) throws Exception {

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

            if (c.charAt(pointer) == '[') // assignment of variables
            { 
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

                // variable name cannot be the same as the name of keywords.
                if (KEYWORDS.contains(varName.toString())) 
                { 
                    throw new RuntimeException(
                        "Syntax Error: invalid syntax" + 
                        varName + 
                        " is a reserved keyword it cannot be used !!"
                    );
                }

                pointer++; // move ahead

                if (c.substring(pointer, pointer + 2).equals("[[")) // array is declared
                { 
                    pointer++;
                    varValue.append(ArrayDeclaration(pointer, c));
                    VARIABLES.put(varName.toString(), varValue.toString());
                } 

                else if (c.substring(pointer, pointer + 2).equals("{{")) // dictionary / object is declared.
                { 
                    pointer++;
                    while (true) {
                        varValue.append(c.charAt(pointer));
                        if (c.charAt(pointer) == '}') {
                            break;
                        }
                        pointer++;
                    }

                    // ------------------------- JSON PARSE TO BE IMPLEMETED ------------------------------ //

                    // VARIABLES.put(varName.toString(), parseToMap(varValue.toString()));

                    // ------------------------- JSON PARSE TO BE IMPLEMETED ------------------------------ //

                } 

                else // integer or string is declared
                { 
                    while (true) {
                        if (c.charAt(pointer) == ']') {
                            break;
                        }
                        varValue.append(c.charAt(pointer));
                        pointer++;
                    }

                    if (VARIABLES.containsKey(varValue.charAt(0)) && varValue.charAt(1) == '[') {
                        // varValue = new StringBuilder(ReturnPropertyOfObject(varValue.toString()));
                    }

                    if (builtInFunctions.contains(varValue.toString().split("\\(")[0].replaceAll("[{}]", ""))) 
                    {
                        String fn = varValue.toString().split("\\(")[0].replaceAll("[{}]", "");
                        String pr = varValue.toString().split("\\(")[1].replaceAll("[{}]", "");
                        varValue = new StringBuilder(ExecuteFunction(fn, pr));
                    } 

                    else {
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

            if (k.equals("ret:"))  // return statement.
            { 
                if (!CURRENTLY_RUNNING_FUNCTION_STACK.isEmpty()) 
                {
                    String fnName = CURRENTLY_RUNNING_FUNCTION_STACK.peek();
                    Object[] temp = LoopTillGivenCharacter(
                        pointer+4,
                        ':', 
                        Optional.of(c),
                        Optional.empty(),
                        Optional.empty()
                    );

                    String ret_val = ProcessData((String) temp[1]);
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
        String[] parts = varValue.split("[");
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

    private void HandleDefinedFunctions(int pointer, String script) throws Exception {

        String c = script;

        Object[] temp;

        temp = LoopTillGivenCharacter(
            pointer,
            '(', 
            Optional.of(script), 
            Optional.empty(), 
            Optional.empty()
        );

        String fnName = (String) temp[1];
        pointer = (int) temp[0] + 1;

        temp = LoopTillGivenCharacter(
            pointer,
            ')', 
            Optional.of(script), 
            Optional.empty(), 
            Optional.empty()
        );

        String paramValues = (String) temp[1];
        pointer = (int) temp[0] + 1;

        // Check if function is a built-in function and execute it
        if (builtInFunctions.contains(fnName)) {
            ExecuteFunction(fnName, paramValues);
        }

        // // Check if function is neither built-in nor user-defined
        if (!builtInFunctions.contains(fnName) && !userDefinedFunctions.contains(fnName)) {
            throw new Exception("Name \"" + fnName + "\" is not defined!");
        }

        // If function is user-defined, execute the corresponding script
        if (userDefinedFunctions.contains(fnName)) {
            String functionScript = FUNCTIONS.get(fnName).getCode();
            List<String> params = FUNCTIONS.get(fnName).getParamList();
            String[] paramValuesArray = paramValues.split(",");

            // Check if the number of arguments matches the number of parameters
            if (paramValuesArray.length != params.size()) {
                throw new Exception(
                    "\""                      + 
                    fnName                    + 
                    "\" takes "               + 
                    params.size()             + 
                    " arguments but got "     + 
                    paramValuesArray.length
                );
            }

            for (int i = 0; i < params.size(); i++) {
                VARIABLES.put(params.get(i), paramValuesArray[i]);
            }

            // Add the function to the currently running function stack
            CURRENTLY_RUNNING_FUNCTION_STACK.push(fnName + "(" + paramValues + ")");

            // Execute the main program thread for the user-defined function
            MainProgramThread(0, functionScript);
        }

        // Continue the main program thread execution
        MainProgramThread(pointer, c);

    }

    private void ForLoopInterpreter(int pointer, String c) throws Exception 
    {
        StringBuilder loop = new StringBuilder();
        String loop_script = "";

        // Extract loop condition until a newline character is encountered
        while (c.charAt(pointer) != '\n') {
            loop.append(c.charAt(pointer));
            pointer++;
        }

        String[] loopParts = loop.toString().split("in");
        if (loopParts.length <= 1) {
            throw new Exception("Syntax Error: \"in\" missing.");
        }

        Object[] temp = this.LoopTillGivenCharacter(
            pointer, 
            ':', 
            Optional.of(c), 
            Optional.of("Syntax Error : Loop end \":\" not found ."), 
            Optional.empty()
        );

        loop_script = (String) temp[1];
        pointer = (int) temp[0];

        String loopVar = loopParts[0].replaceAll("[\\[\\]|]", "");
        String loopCond = loopParts[1];

        // Handle variable having an array
        if (loopCond.contains("[") && LETTERS.matches(loopCond)) {
            String tempVar = loopCond.replaceAll("[\\[\\]|]", "");
            if (!VARIABLES.containsKey(tempVar)) {
                throw new Exception("NameError: name \"" + tempVar + "\" is not defined.");
            }
            VARIABLES.put(loopVar, "0");
            ExecuteDirectForLoop(loopVar, VARIABLES.get(tempVar).toString(), loop_script);
        }

        // Handle array directly given
        else if (loopCond.contains("[[")) {
            VARIABLES.put(loopVar, "0");
            String loopArray = ArrayDeclaration(loopCond.charAt(2), loopCond);
            ExecuteDirectForLoop(loopVar, loopArray, loop_script);
        }

        // Handle range tuple given
        else if (loopCond.contains("(")) 
        {
            loopCond = loopCond.replaceAll("[()]", "");
            String[] loopRange = loopCond.split(",");

            for (int i = 0; i < loopRange.length; i++) {
                loopRange[i] = ProcessData(loopRange[i]);
            }

            VARIABLES.put(loopVar, loopRange[0]);
            ExecuteRangeForLoop(
                Integer.parseInt(loopRange[0]), 
                Integer.parseInt(loopRange[1]), 
                Integer.parseInt(loopRange[1]), 
                loopVar, 
                loop_script
            );
        }

        MainProgramThread(pointer, c);
    }

    private void ExecuteDirectForLoop(String loopVar, String arr, String loop_script) throws Exception {
        for(int i = 0; i < arr.length(); i++){ 
            this.VARIABLES.put(loopVar, i);
            this.MainProgramThread(0,loop_script);
        }
    }

    private void ExecuteRangeForLoop(int start, int end, int inc, String varValue, String loop_script) throws Exception{
        // if( end == null ){ end = start ; start = 0 } 
        // (10) in such range start must be 0 and end will be 10 therfore ..
        // if(inc == null){ inc = 1 } 
        // incrementing factor is also optional parameter .
        for(int i = start; i < end; i = i + inc){
            this.VARIABLES.put(varValue, i+1); 
            this.MainProgramThread(0, loop_script);
        }
    }

    private void IfInterpreter(int pointer, String c) throws Exception {
        String if_script, word = "", ans;
        List<String> t = new ArrayList<>();
        Object[] temp;
        
        temp = this.LoopTillGivenCharacter(
            pointer,
            '\n',
            Optional.of(c),
            Optional.empty(),
            Optional.empty()
        ); // temprary variable
       
        String CONDITION = (String) temp[1];
        pointer = (int) temp[0]; 

        for (int i = 0; i < CONDITION.length(); i++) {
            String currentChar = String.valueOf(CONDITION.charAt(i));
            
            if (this.LETTERS.matches(currentChar)) {
                word += currentChar; // Append the matching character to the word
            } 
            else {
                t.add(word);         // Push the accumulated word to the stack (or list)
                t.add(currentChar);  // Push the current non-matching character to the stack (or list)
                word = "";           // Reset the word for the next accumulation
            }
            if (i == CONDITION.length() - 1) {
                t.add(word);         // Ensure the last accumulated word is pushed to the stack (or list)
            }
        }

        List<String> CONDITION_ARR = new ArrayList<>();

        for (String str : t) {
            if (str.matches(".*\\S.*")) { // Equivalent to /\S/.test(str) in JS
                CONDITION_ARR.add(str);
            }
        }

        for(String i : CONDITION_ARR){
            if(this.LETTERS.matches(i)){
                i = this.ProcessData(i);
            } 
        }

        ans = engine.eval(String.join("", CONDITION_ARR)).toString();

        temp = this.LoopTillGivenCharacter(
            pointer, 
            '?', 
            Optional.of(c), 
            Optional.of("Syntax Error : if statement never ended missing '?' . "),
            Optional.of(true)
        );

        pointer = ((int) temp[0]) + 2 ; 
        if_script = (String) temp[1]  ;

        if(Boolean.valueOf(ans)){ 
            this.MainProgramThread(0, if_script);
        }
        this.MainProgramThread(pointer, c);
    }

    private void RegisterUserDefinedFunctions(int pointer) {
        // Add logic to register user-defined functions
    }

    private String ProcessData(String varValue) throws Exception {

        // If variable is assigned another variable
        if (VARIABLES.containsKey(varValue)) {
            varValue = (String) VARIABLES.get(varValue);

        } 
        else if (varValue.startsWith("{")) {
            
            HandleDefinedFunctions(1, varValue);

            if (isValid(varValue)) {
                varValue = (String) VARIABLES.get(varValue.replaceAll("[{}]", ""));
            } else {
                varValue = (String) VARIABLES.get(varValue.replaceAll("[{}]", "") + ")");
            }

        } 
        else if (varValue.contains("((")) {
            // Handle conditional logic
            varValue = engine.eval(varValue.replaceAll("[()]", "")).toString();
        } 
        else if (varValue.contains("+")) {
            varValue = HandleAddition(varValue); // Handle addition
        } 
        else if (varValue.contains("-")) {
            varValue = HandleSubtraction(varValue); // Handle subtraction
        } 
        else if (varValue.contains("*")) {
            varValue = HandleMultiplication(varValue); // Handle multiplication
        } 
        else if (varValue.contains("/")) {
            varValue = HandleDivision(varValue); // Handle division
        } 
        else if (varValue.contains("%")) {
            varValue = HandleMod(varValue); // Handle modulo
        } 
        else if (varValue.contains("[")) {
            // varValue = ReturnElementAtIndexOfArray(varValue); // Handle array indexing
        } 
        else if (!CheckIfString(varValue) && !isNumeric(varValue) && !KEYWORDS.contains(varValue)) {
            throw new RuntimeException("NameError : name \"" + varValue + "\" is not defined.");
        }

        return varValue;
    }

    private Boolean isNumeric(String d){
        try {
            Integer.parseInt(d);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String HandleAddition(String d) throws Exception{ 
        String[] data = d.split("+");
        int int_sum = 0;
        String str_sum = "";

        for(String ch: data){ ch = this.ProcessData(ch); }
        for(int i = 0 ; i < data.length ; i++ ){
            // for adding strings 
            if(this.CheckIfString(data[i])){ 
                str_sum += data[i].replace("\"", "").replace("'", "");
            } 

            if(!this.CheckIfString(data[i])){  int_sum += Integer.parseInt(data[i]); } // for adding numbers
        }

        if(int_sum==0){ return str_sum; } // two strings were passed . BUG FOUND <--
        if(str_sum==""){ return int_sum+""; } // two integers were passed
        else{  throw new RuntimeException("TypeError: can only concatenate str (not 'int') to str"); } // one string and one integer were passed which cannot be added in python.
    }

    private String HandleSubtraction(String d) throws Exception{
        String[] data = d.split("-");

        // if it is a program string (quotes prresent) then it can't be subtracted .
        if( this.CheckIfString(data[0]) ){ 
            throw new RuntimeException(
                "TypeError: unsupported operand type(s) for -:" +
                this.type(data[0]) + 
                " and " +
                this.type(data[1])
            ); 
        }

        data[0] = this.ProcessData(data[0]) ; int ans = Integer.parseInt(data[0]);

        for(int i = 1 ; i < data.length ; i++){
            if( this.CheckIfString(data[i]) ){  // strings can't be subtracted .
                throw new RuntimeException(
                    "TypeError: unsupported operand type(s) for -:" +
                    this.type(data[i]) + 
                    " and " +
                    this.type(data[i + 1])
                ); 
            }
            data[i] = this.ProcessData(data[i]);
            ans -= Integer.parseInt(data[i]);
        }
        return ans+"";
    }

    private String HandleMultiplication(String d) throws Exception{
        String[] data = d.split("*");
        int ans = 1;

        for(int i = 0 ; i < data.length ; i++)
        {
            if( this.CheckIfString(data[i]) ){  // strings can't be multiplied .
                throw new RuntimeException(
                    "TypeError: can't multiply sequence by non-int of type" + 
                    this.type(data[i])
                ); 
            }

            data[i] = this.ProcessData(data[i]);
            ans = ans * Integer.parseInt(data[i]);
        }

        return ans+"";
    }

    private String HandleDivision(String d) throws Exception{
        String[] data = d.split("/");

        if( this.CheckIfString(data[0]) || this.CheckIfString(data[1]) ){  // strings can't be divided
            throw new RuntimeException(
                "TypeError: can't divide sequence by non-int of type" + 
                this.type(data[0])
            ); 
        }

        data[0] = this.ProcessData(data[0]);
        data[1] = this.ProcessData(data[1]);
        return engine.eval(data[0] + "/" + data[1]).toString();
    }

    private String HandleMod(String d) throws Exception{
        String[] data = d.split("+");

        if( this.CheckIfString(data[0]) || this.CheckIfString(data[1]) ){  // strings can't be divided
            throw new RuntimeException(
                "TypeError: can't mod sequence by non-int of type" + 
                this.type(data[0])
            ); 
        }

        data[0] = this.ProcessData(data[0]);
        data[1] = this.ProcessData(data[1]);
        return engine.eval(data[0] + "%" + data[1]).toString();
    }

    private Map<String, Object> parseToMap(String data) {
        // Logic to convert a string into a Map
        return new HashMap<>();
    }

    private String ExecuteFunction(String functionName, String params) throws Exception {
        params = params.replace(")", "");
        String[] temp = params.split(",");
        String ans;

        if(temp.length>1){
            for(int i = 0; i < temp.length; i++){  
                temp[i] = this.ProcessData(temp[i]);  
            }
            params = temp+"" ;
        }
        else{  
            params = this.ProcessData(params); 
        }

        for(int i = 0; i < this.builtInFunctions.size() ; i++){
            if(this.builtInFunctions.get(i) == functionName){ 
                ans = this.FunctionCaller(i, params);
                return ans;
            }
        }

        return null;
    }
}
