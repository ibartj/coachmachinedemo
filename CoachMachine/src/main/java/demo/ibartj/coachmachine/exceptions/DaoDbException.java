package demo.ibartj.coachmachine.exceptions;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class DaoDbException extends RuntimeException {
    public DaoDbException(String message) {
        super(message);
    }

    public DaoDbException(String message, Throwable cause) {
        super(message, cause);
    }
}
