import java.util.ArrayList;

public class BuiltInFunctions_RadhaProgrammingLanguage {
    
    public ArrayList<String> builtInFunctions;
    public ArrayList<String> userDefinedFunctions;

    public BuiltInFunctions_RadhaProgrammingLanguage() {
        builtInFunctions = new ArrayList<>();
        builtInFunctions.add("print");
        builtInFunctions.add("capitalize");
        builtInFunctions.add("casefold");
        builtInFunctions.add("type");
        userDefinedFunctions = new ArrayList<>(); // empty array to store user defined functions
    }

    public void print(String data) {
        if (data instanceof String) {
            data = data.replaceAll(",", " "); // we don't display comma's in between variable's to be printed hence they are separated by whitespace
            System.out.println(data.replace("'", "").replace("\"", ""));
        } else {
            System.out.println(data);
        }
    }

    public String capitalize(String data) {
        return data.toUpperCase();
    }

    public String casefold(String data) {
        return data.toLowerCase();
    }

    public String type(String data) {
        try {
            Integer.parseInt(data);
            return "<class 'int'>";
        } catch (NumberFormatException e) {
            return "<class 'str'>";
        }
    }

    public String FunctionCaller(String fnName, String params) {
        // print("FUNCTION CALLER: "+params);
        switch (fnName) {
            case "print":
                print(params);
                return "";
            case "capitalize":
                return capitalize(params);
            case "casefold":
                return casefold(params);
            case "type":
                return type(params);
        }
        return "Function not defined !!";
    }
}
