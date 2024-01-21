import java.io.BufferedReader;
import java.io.FileReader;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

public class Main {
    public static void main(String[] args) {
        System.out.println("Running\n");
        Parser parser = null;
        try {
            String filePath = "project_code.txt";
            String sourceCode = readFile(filePath);
            LexicalAnalyser lexer = new LexicalAnalyser(sourceCode);
            parser = new Parser(lexer);
            parser.parseModuleDecal();
            System.out.println
                    ("____________________________________________________________________________" +
                            "\nParsing successful!" +
                            "\n____________________________________________________________________________");
        } catch (Exception e) {
            System.err.println(e.getMessage());
//            e.printStackTrace();
            System.exit(1);
        }
        //System.out.println("Generated tokens:\n");
        //for (Token t : parser.generatedToken) {
        // if (!t.getType().equals("EOF"))
        //       System.out.println(t);
        // }
    }

    private static String readFile(String filePath) throws Exception {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString().strip();
    }

//    private void testScanner() {
//        String modula2Code = "module myprogram;" +
//                "  const" +
//                "    max=100;" +
//                "  var" +
//                "    num:integer;" +
//                "\tx:real;" +
//                "  procedure compute;" +
//                "    var" +
//                "\t  n:integer;" +
//                "\tbegin" +
//                "\t  n:=10;" +
//                "\t  writeint(n);" +
//                "\tend compute;" +
//                "\t" +
//                "  begin" +
//                "    readint(num);" +
//                "\treadreal(x);" +
//                "\tif num<=max then" +
//                "\t  num:=num+5" +
//                "\telse" +
//                "\t  num :=num-5" +
//                "\tend;" +
//                "\twriteint(num);" +
//                "\tcall compute;" +
//                "\twritereal(x);" +
//                "\texit;" +
//                "  end myprogram";
//        // Create an instance of the lexer
//        LexicalAnalyser lexer = new LexicalAnalyser(modula2Code);
//        int x = 0;
//        // Tokenize and print each token
//        try {
//            Token token;
//            do {
//                x++;
//                token = lexer.getNextToken();
//                if (!token.getType().equals("EOF")) {
//                    System.out.println("Type: " + token.getType() + ", Lexeme: " + token.getValue());
//                }
//            } while (!token.getType().equals("EOF"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}