package mytunes.bll.exeption;

public class BLLexception extends Exception {

    public BLLexception(String message) {
        super(message);
    }

    public BLLexception(String message, Exception ex) {
        super(message, ex);
    }
}