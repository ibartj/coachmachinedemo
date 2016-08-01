package demo.ibartj.coachmachine.dao.util;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class DbValueFormatException extends Exception {
    @SuppressWarnings("unused")
    public DbValueFormatException() {
        super();
    }

    public DbValueFormatException(Exception ex) {
        super(ex);
    }
}
