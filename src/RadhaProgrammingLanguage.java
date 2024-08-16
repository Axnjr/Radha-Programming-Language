import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Optional;

class FunctionObject {
    public String[] params;
    public String body;

    public FunctionObject(String[] params, String body) {
        this.params = params;
        this.body = body;
    }
}

public class RadhaProgrammingLanguage extends BuiltInFunctions_RadhaProgrammingLanguage {

    private String CODE;
    private final String LETTERS = "^[a-zA-Z]*$";
    // private final String NUMBERS = "[0-9]";
    private final List<String> KEYWORDS = Arrays.asList(
            "for", "in", "if", "is", "else", "def", "and", "or", "brk:",
            "con:", "ret:", "True", "False", "None", "not");
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

        String[] tempCode = code.split("\\n\\s+");

        for (int i = 0; i < tempCode.length; i++) {
            Boolean t = this.isValid(tempCode[i]);
            if (!t) {
                throw new RadhaError(
                    "[RADHA ERROR]: Parenthesis don't match at line " + i +
                    " you might have forgotten a closing or opening bracket !!"
                );
            }
        }

        this.CODE = code.replace(" ", "");
        this.MainProgramThread(0, this.CODE);
    }

    public boolean isValid(String s) 
    {
        Stack<Character> stack = new Stack<>();
        Map<Character, Character> mapping = new HashMap<>();

        mapping.put(')', '(');
        mapping.put('}', '{');
        mapping.put(']', '[');

        for (char c : s.toCharArray()) {
            if (mapping.containsValue(c)) {
                stack.push(c);
            } else if (mapping.containsKey(c)) {
                if (stack.isEmpty() || mapping.get(c) != stack.pop()) {
                    return false;
                }
            }
        }
        
        return stack.isEmpty();
    }

    private boolean CheckIfString(String str) {
        int l = str.length() - 1;
        return 
            (str.charAt(0) == '"' || str.charAt(0) == '\'') 
                                  && 
            (str.charAt(l) == '"' || str.charAt(l) == '\'');
    }

    private Object[] LoopTillGivenCharacter(
            int pointer,
            char tillCharacter,
            Optional<String> optionalScript,
            Optional<String> optionalErrorMessage,
            Optional<Boolean> optionalCondition
    ) throws Exception {

        StringBuilder returnValue = new StringBuilder();
        // optional condition specifies wheather "till" character should also be
        // included in the return result string .
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
                } else {
                    throw new Exception(error);
                }
            }

            returnValue.append(script.charAt(pointer));
            pointer++;
        }

        // print(pointer+returnValue.toString()+"\n\n\n"+script.charAt(pointer));

        return new Object[] { pointer, returnValue.toString() };
    }

    private void MainProgramThread(int index, String script) throws Exception 
    {
        String c = script != null ? script : this.CODE;
        int scriptSize = c.length();

        for (int pointer = index; pointer < c.length(); pointer++) 
        {
            if (Character.isWhitespace(c.charAt(pointer))) {
                pointer++; // skipping white spaces
            }

            if (pointer < scriptSize && c.charAt(pointer) == '#') { // it's a comment
                while (pointer < c.length()) {
                    if (c.charAt(pointer) == '\n') {
                        break;
                    }
                    pointer++;
                }
            }

            // assignment of variables
            if (pointer < scriptSize && c.charAt(pointer) == '[') 
            {
                if (c.charAt(pointer + 1) == ']') {
                    break; // in case of empty declaration [] like this we must break else loop
                }

                pointer++;
                StringBuilder varName = new StringBuilder();
                StringBuilder varValue = new StringBuilder();

                // for variable name
                while (true) { 
                    if (c.charAt(pointer) == '=') {
                        break;
                    }
                    varName.append(c.charAt(pointer));
                    pointer++;
                }

                // variable name cannot be the same as the name of keywords.
                if (KEYWORDS.contains(varName.toString())) {
                    throw new RadhaError(
                        "Syntax Error: invalid syntax" +
                        varName +
                        " is a reserved keyword it cannot be used !!"
                    );
                }

                pointer++; // move ahead

                // array is declared
                if (pointer < scriptSize && c.substring(pointer, pointer + 2).equals("[[")) {
                    pointer++;
                    varValue.append(ArrayDeclaration(pointer, c));
                    VARIABLES.put(varName.toString(), varValue.toString());
                }

                // dictionary / object is declared.
                else if (pointer < scriptSize && c.substring(pointer, pointer + 2).equals("{{")) 
                {
                    pointer++;

                    while (true) {
                        varValue.append(c.charAt(pointer));
                        if (c.charAt(pointer) == '}') {
                            break;
                        }
                        pointer++;
                    }

                    // print("DICTIONARY :::::: "+varValue.toString());
                    List<Object> objList = new ArrayList<>();

                    for (String i : varValue.toString().replaceAll("[{}]", "").split(",")) {
                        String[] temp = i.split(":");
                        objList.add(new Object[] { temp[0], temp[1] });
                    }

                    VARIABLES.put(varName.toString(), objList);
                }

                // integer or string is declared
                else {
                    while (true) {
                        if (c.charAt(pointer) == ']') {
                            break;
                        }
                        varValue.append(c.charAt(pointer));
                        pointer++;
                    }

                    if (builtInFunctions.contains(varValue.toString().split("\\(")[0].replaceAll("[{}]", ""))) {
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

            if (pointer < scriptSize && c.charAt(pointer) == '{') {
                // print(c);
                HandleDefinedFunctions(pointer + 1, c);
                break;
            }

            if (pointer + 2 <= scriptSize && c.substring(pointer, pointer + 2).equals("for")) {
                ForLoopInterpreter(pointer + 2, c);
                break;
            }

            if (pointer + 4 <= c.length() && c.substring(pointer, pointer + 4).equals("ret:")) // return statement.
            {
                if (!CURRENTLY_RUNNING_FUNCTION_STACK.isEmpty()) 
                {
                    String fnName = CURRENTLY_RUNNING_FUNCTION_STACK.peek();
                    Object[] temp = LoopTillGivenCharacter(
                            pointer + 4,
                            '\n',
                            Optional.of(c),
                            Optional.empty(),
                            Optional.empty()
                    );
                    String ret_val = ProcessData((String) temp[1]);

                    VARIABLES.put(fnName, ret_val);
                    CURRENTLY_RUNNING_FUNCTION_STACK.pop();
                }
            }

            if (pointer + 2 <= c.length() && c.substring(pointer, pointer + 2).equals("if")) {
                IfInterpreter(pointer + 2, c);
                break;
            }

            if (pointer + 3 <= c.length() && c.substring(pointer, pointer + 3).equals("def")) {
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

    @SuppressWarnings({ "unchecked" })
    private String ReturnElementAtIndexOfArray(String varValue) throws Exception 
    {
        String[] parts = varValue.split("\\[");
        String result = "";
        // print("ARRAY ELE: "+parts[0]+" / "+parts[1]);

        if (VARIABLES.containsKey(parts[0])) 
        {
            Object temp = VARIABLES.get(parts[0]); // array or object whose element is required
            String ref = parts[1].replace("]", "");

            if (temp instanceof String) {
                int index = Integer.parseInt(ref); // index of element to be searched
                result = String.valueOf(temp.toString().split(",")[index]);
            } 
            
            else if (temp instanceof List) {
                result = ((List<Object[]>) temp)
                    .stream()
                    .filter(obj -> ref.equals(obj[0]))
                    .findFirst()
                    .map(obj -> obj[1].toString()) // Convert the value to a string
                    .orElse(null)
                ;
            }
        } 
        else {
            throw new RadhaError("Variable " + parts[0] + " is not defined !");
        }

        return result;
    }

    private void HandleDefinedFunctions(int pointer, String script) throws Exception 
    {
        String c = script;
        Object[] temp;

        temp = LoopTillGivenCharacter(
                pointer,
                '(',
                Optional.of(script),
                Optional.empty(),
                Optional.empty());

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
        pointer = (int) temp[0];

        // Check if function is a built-in function and execute it
        if (builtInFunctions.contains(fnName)) {
            ExecuteFunction(fnName, paramValues);
        }

        // // Check if function is neither built-in nor user-defined
        if (!builtInFunctions.contains(fnName) && !userDefinedFunctions.contains(fnName)) {
            throw new Exception("Name \"" + fnName + "\" is not defined!");
        }

        // If function is user-defined, execute the corresponding script
        if (userDefinedFunctions.contains(fnName)) 
        {
            String functionScript = this.FUNCTIONS.get(fnName).body;
            String[] params = this.FUNCTIONS.get(fnName).params;
            String[] paramValuesArray = paramValues.split(",");

            // Check if the number of arguments matches the number of parameters
            if (paramValuesArray.length != params.length) {
                throw new RadhaError(
                    "\"" +
                    fnName +
                    "\" takes " +
                    params.length +
                    " arguments but got " +
                    paramValuesArray.length
                );
            }

            for (int i = 0; i < params.length; i++) {
                VARIABLES.put(params[i], paramValuesArray[i]);
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
        if (loopCond.contains("[") && LETTERS.matches(loopCond)) 
        {
            String tempVar = loopCond.replaceAll("[\\[\\]|]", "");

            if (!VARIABLES.containsKey(tempVar)) {
                throw new Exception("NameError: name \"" + tempVar + "\" is not defined.");
            }

            VARIABLES.put(loopVar, "0");
            ExecuteDirectForLoop(loopVar, VARIABLES.get(tempVar).toString(), loop_script);
        }

        // Handle array directly given
        else if (loopCond.contains("[[")) 
        {
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
        for (int i = 0; i < arr.length(); i++) {
            this.VARIABLES.put(loopVar, i);
            this.MainProgramThread(0, loop_script);
        }
    }

    private void ExecuteRangeForLoop(
        int start, int end, int inc, String varValue, String loop_script) throws Exception {
        // if( end == null ){ end = start ; start = 0 }
        // (10) in such range start must be 0 and end will be 10 therfore ..
        // if(inc == null){ inc = 1 }
        // incrementing factor is also optional parameter .
        for (int i = start; i < end; i = i + inc) {
            this.VARIABLES.put(varValue, i + 1);
            this.MainProgramThread(0, loop_script);
        }
    }

    private void IfInterpreter(int pointer, String c) throws Exception 
    {
        String if_script, word = "", ans;
        List<String> t = new ArrayList<>();
        Object[] temp;

        // temprary variable
        temp = this.LoopTillGivenCharacter(
                pointer,
                '\n',
                Optional.of(c),
                Optional.empty(),
                Optional.empty()
        ); 

        String CONDITION = (String) temp[1];
        pointer = (int) temp[0];

        CONDITION = CONDITION
            .replaceAll("or", "||")
            .replaceAll("and", "&&")
            .replaceAll("is", "===")
            .replaceAll("True", "true")
            .replaceAll("False", "false")
        ;

        for (int i = 0; i < CONDITION.length(); i++) 
        {
            String currentChar = String.valueOf(CONDITION.charAt(i));

            if (currentChar.matches(this.LETTERS)) {
                word += currentChar; // Append the matching character to the word
            } 
            else {
                t.add(word); // Push the accumulated word to the stack (or list)
                t.add(currentChar); // Push the current non-matching character to the stack (or list)
                word = ""; // Reset the word for the next accumulation
            }
            if (i == CONDITION.length() - 1) {
                t.add(word); // Ensure the last accumulated word is pushed to the stack (or list)
            }
        }

        String[] CONDITION_ARR = new String[t.size()];

        for (int i = 0; i < t.size(); i++) {
            // look for variables and replace them with thier values
            if (t.get(i).matches(".*\\S.*") && t.get(i).matches(this.LETTERS)) {
                // print(t.get(i));
                CONDITION_ARR[i] = this.ProcessData(t.get(i));
            } else {
                CONDITION_ARR[i] = t.get(i);
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

        pointer = ((int) temp[0]) + 2;
        if_script = (String) temp[1];

        if (Boolean.valueOf(ans)) {
            this.MainProgramThread(0, if_script);
        }

        this.MainProgramThread(pointer, c);
    }

    private void RegisterUserDefinedFunctions(int pointer) throws Exception 
    {
        String c = this.CODE, fnName = "", params = "", fn_code = "";
        Object[] temp;

        // temp[0] has the modified pointer and temp[1] has the value after looping .
        // looping to get fnName (function name)
        temp = this.LoopTillGivenCharacter(
            pointer,
            '(',
            Optional.of(c),
            Optional.empty(),
            Optional.empty()
        );

        fnName = (String) temp[1];
        // update pointer by moving ahead
        pointer = (int) temp[0] + 1;
        // print("REGISTERING FUNC: "+fnName);

        // looping to get params (function paramaters)
        temp = this.LoopTillGivenCharacter(
            pointer,
            ')',
            Optional.of(c),
            Optional.empty(),
            Optional.empty()
        );

        params = (String) temp[1];
        // update pointer by moving ahead
        pointer = (int) temp[0] + 1;

        // looping to get fnCode (function code/script)
        temp = this.LoopTillGivenCharacter(
            pointer,
            ';',
            Optional.of(c),
            Optional.of("Syntax Error : Function never ended ';' not found ."),
            Optional.empty()
        );

        // adding a semi colon bcuz else it would give error in case of user defined function .
        fn_code = (String) temp[1] + ":";
        // update pointer by moving ahead
        pointer = (int) temp[0] + 1;

        FunctionObject funcObj = new FunctionObject(params.split(","), fn_code);

        this.userDefinedFunctions.add(fnName);
        this.FUNCTIONS.put(fnName, funcObj);
        this.MainProgramThread(pointer + 1, c);
    }

    private String ProcessData(String varValue) throws Exception 
    {
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
            varValue = ReturnElementAtIndexOfArray(varValue); // Handle array indexing
        } 
        else if (!CheckIfString(varValue) && !isNumeric(varValue) && !KEYWORDS.contains(varValue)) {
            throw new RadhaError("Name \"" + varValue + "\" is not defined.");
        }

        return varValue == null ? "" : varValue;
    }

    private Boolean isNumeric(String d) {
        try {
            Integer.parseInt(d);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String HandleAddition(String d) throws Exception 
    {
        String[] data = d.split("\\+");
        String str_sum = "";
        int int_sum = 0;

        for (int i = 0; i < data.length; i++) {
            data[i] = ProcessData(data[i]);
        }

        for (int i = 0; i < data.length; i++) {
            // for adding strings
            if (this.CheckIfString(data[i])) {
                str_sum += data[i].replace("\"", "").replace("'", "");
            }

            if (!this.CheckIfString(data[i])) {
                int_sum += Integer.parseInt(data[i]);
            } // for adding numbers
        }

        // two strings were passed . BUG FOUND <--
        if (int_sum == 0) {
            return str_sum;
        } 
        // two integers were passed
        if (str_sum == "") {
            return int_sum + "";
        } 
        // one string and one integer were passed which cannot be added in python.
        else {
            throw new RadhaError("TypeError: can only concatenate str (not 'int') to str");
        } 
    }

    private String HandleSubtraction(String d) throws Exception 
    {
        String[] data = d.split("\\-");

        // if it is a program string (quotes prresent) then it can't be subtracted .
        if (this.CheckIfString(data[0])) {
            throw new RuntimeException(
                    "TypeError: unsupported operand type(s) for -:" +
                            this.type(data[0]) +
                            " and " +
                            this.type(data[1]));
        }

        data[0] = this.ProcessData(data[0]);
        int ans = Integer.parseInt(data[0]);

        for (int i = 1; i < data.length; i++) 
        {
            if (this.CheckIfString(data[i])) { // strings can't be subtracted .
                throw new RadhaError(
                    "TypeError: unsupported operand type(s) for -:" +
                    this.type(data[i]) +
                    " and " +
                    this.type(data[i + 1])
                );
            }

            data[i] = this.ProcessData(data[i]);
            ans -= Integer.parseInt(data[i]);
        }

        return ans + "";
    }

    private String HandleMultiplication(String d) throws Exception 
    {
        String[] data = d.split("\\*");
        int ans = 1;

        for (int i = 0; i < data.length; i++) 
        {
            if (this.CheckIfString(data[i])) { // strings can't be multiplied .
                throw new RadhaError(
                    "TypeError: can't multiply sequence by non-int of type" +
                    this.type(data[i])
                );
            }

            data[i] = this.ProcessData(data[i]);
            ans = ans * Integer.parseInt(data[i]);
        }

        return ans + "";
    }

    private String HandleDivision(String d) throws Exception 
    {
        String[] data = d.split("/");

        if (this.CheckIfString(data[0]) || this.CheckIfString(data[1])) { // strings can't be divided
            throw new RuntimeException(
                    "TypeError: can't divide sequence by non-int of type" +
                            this.type(data[0]));
        }

        data[0] = this.ProcessData(data[0]);
        data[1] = this.ProcessData(data[1]);

        return engine.eval(data[0] + "/" + data[1]).toString();
    }

    private String HandleMod(String d) throws Exception 
    {
        String[] data = d.split("+");

        if (this.CheckIfString(data[0]) || this.CheckIfString(data[1])) { // strings can't be divided
            throw new RadhaError(
                "TypeError: can't mod sequence by non-int of type" +
                this.type(data[0]));
        }

        data[0] = this.ProcessData(data[0]);
        data[1] = this.ProcessData(data[1]);

        return engine.eval(data[0] + "%" + data[1]).toString();
    }

    private String ExecuteFunction(String functionName, String params) throws Exception 
    {
        params = params.replace(")", "");
        String[] temp = params.split(",");
        String ans;

        if (temp.length > 1) {
            for (int i = 0; i < temp.length; i++) {
                temp[i] = this.ProcessData(temp[i]);
            }
            params = String.join("", temp);
        } 
        else {
            params = this.ProcessData(params);
        }

        for (int i = 0; i < this.builtInFunctions.size(); i++) {
            if (this.builtInFunctions.contains(functionName)) {
                ans = this.FunctionCaller(functionName, params);
                return ans;
            }
        }

        return null;
    }
    
}