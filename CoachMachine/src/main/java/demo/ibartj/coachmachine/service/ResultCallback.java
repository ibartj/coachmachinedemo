package demo.ibartj.coachmachine.service;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public abstract class ResultCallback<T> {
    public ResultCallback() {
    }

    public abstract void onSuccess(T object);

    public void onFailure(Exception ex) {
    }

    @SuppressWarnings("unused")
    public void onFailure(Exception ex, T object) {
        onFailure(ex);
    }
}
