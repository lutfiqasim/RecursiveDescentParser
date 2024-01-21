//Lexical analyser(Scanner)

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyser {
    // Token types
    //Names
    public static final String NUMBER = "NUMBER";
    public static final String MODULE = "MODULE";
    public static final String CONST = "CONST";
    public static final String VAR = "VAR";
    public static final String PROCEDURE = "PROCEDURE";
    public static final String BEGIN = "BEGIN";
    public static final String END = "END";
    public static final String IF = "IF";
    public static final String THEN = "THEN";
    public static final String ELSE = "ELSE";
    public static final String ELSIF = "ELSEIF";
    public static final String WHILE = "WHILE";
    public static final String DO = "DO";
    public static final String LOOP = "LOOP";
    public static final String UNTIL = "UNTIL";
    public static final String EXIT = "EXIT";
    public static final String CALL = "CALL";
    public static final String READINT = "READINT";
    public static final String READREAL = "READREAL";
    public static final String READCHAR = "READCHAR";
    public static final String READLN = "READLN";
    public static final String WRITEINT = "WRITEINT";
    public static final String WRITEREAL = "WRITEREAL";
    public static final String WRITECHAR = "WRITECHAR";
    public static final String WRITELN = "WRITELN";
    public static final String SEMICOLON = ";";
    public static final String COMMA = ",";
    public static final String OPEN_PAREN = "(";
    public static final String CLOSE_PAREN = ")";
    public static final String ASSIGNMENT = "ASSIGNMENT";
    public static final String DATATYPE = ":";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String MULTIPLY = "*";
    public static final String DIVIDE = "/";
    public static final String MOD = "MOD";
    public static final String DIV = "DIV";
    public static final String EQUAL = "=";
    public static final String NOT_EQUAL = "|=";
    public static final String LESS_THAN = "<";
    public static final String LESS_THAN_OR_EQUAL = "<=";
    public static final String GREATER_THAN = ">";
    public static final String GREATER_THAN_OR_EQUAL = ">=";

    public static final String IDENTIFIER = "IDENTIFIER";
    public static final String INTEGER_LITERAL = "INTEGER";
    public static final String REAL_LITERAL = "REAL";
    public static final String CHAR_LITERAL = "CHAR";

    private String fileData;
    private int currentLine;
    public int currentPosition;

    public LexicalAnalyser(String fileData) {
        this.fileData = fileData.strip();
        currentPosition = 0;
        currentLine = 1;
    }

    public Token getNextToken() throws Exception {
        if (currentPosition >= fileData.length()) {
            return new Token("EOF", "", fileData.length(), currentLine);
        }
        updateLine();
        skipWhitespaces();
        Character currentChar = fileData.charAt(currentPosition);
//        System.out.println("curr char: '" + currentChar + "'");
        switch (currentChar) {
            case ';':
                ++currentPosition;
//                updateLine();
                return new Token(SEMICOLON, SEMICOLON, currentPosition - 1, currentLine);
            case ',':
                ++currentPosition;
                return new Token(COMMA, COMMA, currentPosition - 1, currentLine);
            case '(':
                ++currentPosition;
                return new Token(OPEN_PAREN, OPEN_PAREN, currentPosition - 1, currentLine);
            case ')':
                ++currentPosition;
                return new Token(CLOSE_PAREN, CLOSE_PAREN, currentPosition - 1, currentLine);
            case ':':
                // Check for ':=' assignment
                if (currentPosition + 1 < fileData.length() && fileData.charAt(currentPosition + 1) == '=') {
                    currentPosition += 2;
                    return new Token(ASSIGNMENT, ":=", currentPosition - 2, currentLine);
                } else {
                    // Handle single ':'
                    currentPosition++;
                    return new Token(DATATYPE, DATATYPE, currentPosition - 1, currentLine);
                }
            case '+':
                ++currentPosition;
                return new Token(PLUS, PLUS, currentPosition - 1, currentLine);
            case '-':
                ++currentPosition;
                return new Token(MINUS, MINUS, currentPosition - 1, currentLine);
            case '*':
                ++currentPosition;
//                updateLine();
                return new Token(MULTIPLY, MULTIPLY, currentPosition - 1, currentLine);
            case '/':
                ++currentPosition;
//                updateLine();
                return new Token(DIVIDE, DIVIDE, currentPosition - 1, currentLine);
            case '=':
                ++currentPosition;
//                updateLine();
                return new Token(EQUAL, EQUAL, currentPosition - 1, currentLine);
            case '|':
                if (currentPosition + 1 < fileData.length() && fileData.charAt(currentPosition + 1) == '=') {
                    currentPosition += 2;
//                    updateLine();
                    return new Token(NOT_EQUAL, "|=", currentPosition - 1, currentLine);
                } else {
                    throw new Exception("Unexpected token");
                }
            case '>':
                if (currentPosition + 1 < fileData.length() && fileData.charAt(currentPosition + 1) == '=') {
                    currentPosition += 2;
//                    updateLine();
                    return new Token(GREATER_THAN_OR_EQUAL, GREATER_THAN_OR_EQUAL, currentPosition - 2, currentLine);
                }
                if (currentPosition + 1 < fileData.length()) {
                    currentPosition++;
//                    updateLine();
                    return new Token(GREATER_THAN, GREATER_THAN, currentPosition - 1, currentLine);
                }
                throw new Exception("Illegal format");
            case '<':
                if (currentPosition + 1 < fileData.length() && fileData.charAt(currentPosition + 1) == '=') {
                    currentPosition += 2;
//                    updateLine();
                    return new Token(LESS_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, currentPosition - 2, currentLine);
                }
                if (currentPosition + 1 < fileData.length()) {
                    currentPosition++;
//                    updateLine();
                    return new Token(LESS_THAN, LESS_THAN, currentPosition - 1, currentLine);
                }
                throw new Exception("Illegal format");
                //expected it to be used with chars ass
            case '\'':
                currentPosition++;
//                updateLine();
                return new Token("Apostrophe", "\'", currentPosition - 1, currentLine);
            case '.':
                currentPosition++;
                return new Token("DOT", ".", currentPosition - 1, currentLine);
            default:
                if (isLetter(currentChar)) {
                    StringBuilder currentExp = new StringBuilder("");
                    while (isLetter(currentChar) || isDigit(currentChar)) {
                        currentExp.append(currentChar);
                        if (currentPosition + 1 >= fileData.length()) {
                            currentPosition += 1;
                            break;
                        } else
                            currentChar = fileData.charAt(++currentPosition);

                    }
//                    updateLine();
                    isValidIdentifier(currentExp.toString());
                    return new Token(checkType(currentExp.toString()), currentExp.toString(), currentPosition - currentExp.length(), currentLine);
                }
                if (isDigit(currentChar)) {
                    StringBuilder currentExp = new StringBuilder("");
                    while (isDigit(currentChar) || currentChar.equals(".")) {
                        currentExp.append(currentChar);
                        currentChar = fileData.charAt(++currentPosition);
//                        updateLine();
                    }
//                    System.out.println("HERE2" + currentExp.toString());
                    isValidNumber(currentExp.toString());
                    return new Token(NUMBER, currentExp.toString(), currentPosition, currentLine);
                }

        }
        // If no match is found, raise an error or handle accordingly
        throw new Exception("Lexical-Error: Invalid token got: '" + currentChar + "', at line: " + currentLine);

    }

    private void skipWhitespaces() {
        while (currentPosition < fileData.length() && Character.isWhitespace(fileData.charAt(currentPosition))) {
            currentPosition++;
            updateLine();
        }
    }

    private String checkType(String currentexp) {
        if (currentexp == ":=") {
            return ASSIGNMENT;
        }
        //check if its key Word
        for (String keyword : getAllKeywords()) {
            if (match(keyword, currentexp)) {
                return keyword;
            }
        }
        return IDENTIFIER;
    }

    private boolean match(String keyWord, String currentWord) {
        Pattern p = Pattern.compile("^" + keyWord.toLowerCase());
        Matcher m = p.matcher(currentWord.toLowerCase());
        return m.find();
    }

    private String[] getAllKeywords() {
        return new String[]{MODULE, CONST, VAR, PROCEDURE, BEGIN, END, IF, THEN, ELSE, ELSIF, WHILE, DO, LOOP, UNTIL,
                EXIT, CALL, READINT, READREAL, READCHAR, READLN, WRITEINT, WRITEREAL, WRITECHAR,
                WRITELN, INTEGER_LITERAL, REAL_LITERAL, CHAR_LITERAL};
    }

    private boolean isLetter(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private boolean isValidNumber(String n) throws Exception {
        try {
            // Try to parse the string as a double
            double number = Double.parseDouble(n);

            if (Double.isFinite(number)) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            //not valid number
            throw new Error("Wrong number of format", currentLine);
        }
    }

    private void isValidIdentifier(String n) throws Exception {
        if (Character.isDigit(n.charAt(0))) {
            System.out.println("identifiers: " + n);
            throw new Error("Lexical Error wrong name format", currentLine);
        }
    }

    public int getLine(int index) {
        int line = 1;
        for (int i = 0; i < index; i++) {
            if (fileData.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

    private void updateLine() {
        char currentChar = fileData.charAt(currentPosition);
        if (currentChar == '\n') {
            currentLine++;
        }
    }
}

