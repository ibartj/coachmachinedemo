package demo.ibartj.coachmachine.exceptions;

import com.letsgood.synergykitsdkandroid.resources.SynergykitError;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public class SynergyKitException extends RuntimeException {
    private SynergykitError error;

    public SynergyKitException(SynergykitError error) {
        super(error.getMessage());
        this.error = error;
    }

    public SynergykitError getError() {
        return error;
    }
}
