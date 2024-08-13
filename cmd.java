class FunctionObject {
    public String name;
    public List<String> params;
    public String body;

    public FunctionObject(String name, List<String> params, String body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }
}

public class Main {
    public static void main(String[] args) {
        // Example usage of the interpreter
        String code = "[x]=10\nprint('Hello, World!')\ndef myFunc(a) { print(a) }\n";
        RadhaProgrammingLanguage interpreter = new RadhaProgrammingLanguage(code);
    }
}