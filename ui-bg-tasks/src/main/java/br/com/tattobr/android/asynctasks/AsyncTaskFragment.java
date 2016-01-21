package br.com.tattobr.android.asynctasks;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class AsyncTaskFragment<Result> extends Fragment {
    public interface AsyncTaskFragmentListener {
        BaseAsyncTask.AsyncTaskListener getAsyncTaskListener(AsyncTaskFragment instance);
    }

    private AsyncTaskFragmentListener mAsyncTaskFragmentListener;
    private BaseAsyncTask.AsyncTaskListener<Result> mAsyncTaskListener;
    private Result mAsyncTaskResult;
    private BaseAsyncTask mAsyncTask;

    //flags
    private boolean mStartDispatch;
    private boolean mStartDispatched;
    private boolean mFinishDispatch;
    private boolean mFinishDispatched;
    private boolean mFailDispatch;
    private boolean mFailDispatched;
    private boolean mCompleteDispatch;
    private boolean mCompleteDispatched;
    private boolean mCancelledDispatch;
    private boolean mCancelledDispatched;
    private boolean mPaused;

    public abstract BaseAsyncTask startAsyncTask();

    public abstract boolean isValidContext(Context context);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof AsyncTaskFragmentListener) || !isValidContext(context)) {
            throw new IllegalStateException("Context must implement fragment's callbacks.");
        }

        mAsyncTaskFragmentListener = (AsyncTaskFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mAsyncTaskFragmentListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        mPaused = false;

        dispatchAsyncTaskStart();
        dispatchAsyncTaskFail();
        dispatchAsyncTaskComplete(mAsyncTaskResult);
        dispatchAsyncTaskCancelled();
        dispatchAsyncTaskFinish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if (mAsyncTask == null) {
            mAsyncTask = startAsyncTask();
        }
    }

    public BaseAsyncTask.AsyncTaskListener<Result> getAsyncTaskListener() {
        if (mAsyncTaskListener == null) {
            mAsyncTaskListener = new BaseAsyncTask.AsyncTaskListener<Result>() {
                @Override
                public void onAsyncTaskCancelled() {
                    mCancelledDispatch = true;
                    dispatchAsyncTaskCancelled();
                }

                @Override
                public void onAsyncTaskComplete(Result result) {
                    mCompleteDispatch = true;
                    mAsyncTaskResult = result;
                    dispatchAsyncTaskComplete(result);
                }

                @Override
                public void onAsyncTaskFail() {
                    mFailDispatch = true;
                    dispatchAsyncTaskFail();
                }

                @Override
                public void onAsyncTaskFinish() {
                    mFinishDispatch = true;
                    dispatchAsyncTaskFinish();
                }

                @Override
                public void onAsyncTaskStart() {
                    mStartDispatch = true;
                    dispatchAsyncTaskStart();
                }
            };
        }
        return mAsyncTaskListener;
    }

    public void cancelTask() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(false);
            mAsyncTask = null;
        }
    }

    private void dispatchAsyncTaskCancelled() {
        if (mAsyncTaskFragmentListener != null && mCancelledDispatch && !mCancelledDispatched && !mPaused) {
            mCancelledDispatched = true;
            mAsyncTaskFragmentListener.getAsyncTaskListener(this).onAsyncTaskCancelled();
        }
    }

    private void dispatchAsyncTaskComplete(Result result) {
        if (mAsyncTaskFragmentListener != null && mCompleteDispatch && !mCompleteDispatched && !mPaused) {
            mCompleteDispatched = true;
            mAsyncTaskFragmentListener.getAsyncTaskListener(this).onAsyncTaskComplete(result);
        }
    }

    private void dispatchAsyncTaskFail() {
        if (mAsyncTaskFragmentListener != null && mFailDispatch && !mFailDispatched && !mPaused) {
            mFailDispatched = true;
            mAsyncTaskFragmentListener.getAsyncTaskListener(this).onAsyncTaskFail();
        }
    }

    private void dispatchAsyncTaskFinish() {
        if (mAsyncTaskFragmentListener != null && mFinishDispatch && !mFinishDispatched && !mPaused) {
            mFinishDispatched = true;
            mAsyncTaskFragmentListener.getAsyncTaskListener(this).onAsyncTaskFinish();
        }
    }

    private void dispatchAsyncTaskStart() {
        if (mAsyncTaskFragmentListener != null && mStartDispatch && !mStartDispatched && !mPaused) {
            mStartDispatched = true;
            mAsyncTaskFragmentListener.getAsyncTaskListener(this).onAsyncTaskStart();
        }
    }
}