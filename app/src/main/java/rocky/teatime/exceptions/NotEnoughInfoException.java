package rocky.teatime.exceptions;

/**
 * An exception class to be thrown when the user has not provided enough information when adding or
 * editing a tea.
 * @author Rocky Petkov
 * @version Final
 */
public class NotEnoughInfoException extends Exception {

    /**
     * A pretty standard exception constructor. Simply passes the message up to the parent classes'
     * constructor
     * @param message Message we wish to display with the exception
     */
    public NotEnoughInfoException(String message) {
        super(message);
    }
}
