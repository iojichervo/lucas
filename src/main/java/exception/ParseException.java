package exception;

public class ParseException extends Exception {
    
    public static final long serialVersionUID = 1L;

    public ParseException(String s) {
        super(s);
    }
}
