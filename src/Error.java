public class Error extends RuntimeException {
    public Error(String message, int index) {
        super(message+", at line: "+index);
    }
}