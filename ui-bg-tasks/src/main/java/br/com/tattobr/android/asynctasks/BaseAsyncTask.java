package br.com.tattobr.android.asynctasks;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    public interface AsyncTaskListener<Result> {
        void onAsyncTaskCancelled();

        void onAsyncTaskComplete(Result result);

        void onAsyncTaskFail();

        void onAsyncTaskFinish();

        void onAsyncTaskStart();
    }

    public BaseAsyncTask(AsyncTaskListener<Result> listener) {
        setAsyncTaskListener(listener);
    }

    private WeakReference<AsyncTaskListener<Result>> mAsyncTaskListenerReference;

    public AsyncTaskListener<Result> getAsyncTaskListener() {
        return mAsyncTaskListenerReference != null ? mAsyncTaskListenerReference.get() : null;
    }

    public void setAsyncTaskListener(AsyncTaskListener<Result> mAsyncTaskListener) {
        this.mAsyncTaskListenerReference = new WeakReference<AsyncTaskListener<Result>>(mAsyncTaskListener);
    }

    public abstract boolean isSuccess(Result result);

    @Override
    protected void onPreExecute() {
        AsyncTaskListener<Result> asyncTaskListener = getAsyncTaskListener();
        if (asyncTaskListener != null) {
            asyncTaskListener.onAsyncTaskStart();
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        AsyncTaskListener<Result> asyncTaskListener = getAsyncTaskListener();
        if (asyncTaskListener != null) {
            if (isSuccess(result)) {
                asyncTaskListener.onAsyncTaskComplete(result);
            } else {
                asyncTaskListener.onAsyncTaskFail();
            }
            asyncTaskListener.onAsyncTaskFinish();
        }
    }

    @Override
    protected void onCancelled() {
        AsyncTaskListener<Result> asyncTaskListener = getAsyncTaskListener();

        if (asyncTaskListener != null) {
            asyncTaskListener.onAsyncTaskCancelled();
            asyncTaskListener.onAsyncTaskFinish();
        }
    }

    public boolean executeCompat(Params... params) {
        boolean executed = false;
        try {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                execute(params);
                executed = true;
            } else {
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
                executed = true;
            }
        } catch (Exception e) {

        }

        try {
            if (!executed) {
                execute(params);
            }
        } catch (Exception e) {

        }

        return executed;
    }
}