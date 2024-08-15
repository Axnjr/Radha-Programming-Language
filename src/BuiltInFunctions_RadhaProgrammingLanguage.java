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

    public String FunctionCaller(int index, String params) {
        switch (index) {
            case 0:
                print(params);
                return "";
            case 1:
                return capitalize(params);
            case 2:
                return casefold(params);
            case 3:
                return type(params);
        }
        return "Function not defined !!";
    }
}
