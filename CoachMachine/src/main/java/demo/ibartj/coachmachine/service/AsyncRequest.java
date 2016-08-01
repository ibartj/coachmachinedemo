package demo.ibartj.coachmachine.service;

import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * @author Jan Bartovský
 * @version %I%, %G%
 */
public abstract class AsyncRequest<T> extends AsyncTask<Void, String, T> {
    protected ResultCallback<T> callback;
    private ProgressDialog progressDialog;
    private Exception exception;

    public void fire(ResultCallback<T> callback) {
        this.callback = callback;
        this.execute();
    }

    @SuppressWarnings("unused")
    public void fire(ResultCallback<T> callback, ProgressDialog progressDialog) {
        this.callback = callback;
        this.progressDialog = progressDialog;
        this.execute();
    }

    @SuppressWarnings("unused")
    public T failure(Exception exception) {
        this.exception = exception;
        this.cancel(false);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.progressDialog != null) {
            progressDialog.show();
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (progressDialog != null && values.length == 1) {
            progressDialog.setMessage(values[0]);
        }
    }

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);
        if (this.progressDialog != null) {
            progressDialog.dismiss();
        }
        this.callback.onSuccess(t);
    }

    @Override
    protected void onCancelled(T t) {
        super.onCancelled(t);
        this.callback.onFailure(this.exception);
    }
}
