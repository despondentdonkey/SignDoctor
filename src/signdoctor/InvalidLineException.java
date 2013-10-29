package signdoctor;

public class InvalidLineException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidLineException() {
        super("Invalid line number. Must be 1 to 4.");
    }

}
