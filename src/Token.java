public class Token {
    private String type;
    private String value;
    private int startIndex;
    private int lineNumber;

    public Token(String type, String value, int startIndex, int lineNumber) {
        this.type = type;
        this.value = value;
        this.startIndex = startIndex;
        this.lineNumber = lineNumber;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", startIndex=" + startIndex +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
