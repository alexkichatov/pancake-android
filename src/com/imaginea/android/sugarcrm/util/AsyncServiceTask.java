/*******************************************************************************
 * Copyright (c) 2013 .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : AsyncServiceTask
 * Description : 
 *              AsyncServiceTask enables proper and easy use of the UI thread from a Service.
 * This is similar to AsyncTask in many respects but allows to be run from a
 * Service and uses the supplied Messenger class to communicate to a UI Thread.
 * If no Messenger is supplied by the subclasses, then no messages are sent to
 * the handler. This class allows to perform background operations in a Service
 * and publish results on the UI thread without having to manipulate threads
 * and/or handlers.
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class AsyncServiceTask.
 * 
 * @param <Params>
 *            the generic type
 * @param <Progress>
 *            the generic type
 * @param <Result>
 *            the generic type
 */
public abstract class AsyncServiceTask<Params, Progress, Result> {

    /** The m wake lock. */
    private static PowerManager.WakeLock mWakeLock = null;

    /** The Constant CORE_POOL_SIZE. */
    private static final int CORE_POOL_SIZE = 5;

    /** The Constant MAXIMUM_POOL_SIZE. */
    private static final int MAXIMUM_POOL_SIZE = 32;

    /** The Constant KEEP_ALIVE. */
    private static final int KEEP_ALIVE = 10;

    /** The Constant sWorkQueue. */
    private static final BlockingQueue<Runnable> sWorkQueue = new LinkedBlockingQueue<Runnable>(
            10);

    /** The Constant TAG. */
    private static final String TAG = "AsyncServiceTask";

    /** define our custom thread factory for creating threads for the pool. */
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(final Runnable r) {
            return new Thread(r, "AsyncServiceTask #"
                    + mCount.getAndIncrement());
        }
    };

    /** Thread pool for our Service threads. */
    private static final ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            sWorkQueue, sThreadFactory);

    /** The m worker. */
    private final WorkerRunnable<Params, Result> mWorker;

    /** The m future. */
    private final FutureTask<Result> mFuture;

    /** The m status. */
    private volatile Status mStatus = Status.PENDING;

    /**
     * Indicates the current status of the task. Each status will be set only
     * once during the lifetime of a task.
     */
    public enum Status {
        /**
         * Indicates that the task has not been executed yet.
         */
        PENDING,
        /**
         * Indicates that the task is running.
         */
        RUNNING,
        /**
         * Indicates that {@link AsyncServiceTask#onPostExecute} has finished.
         */
        FINISHED,
    }

    /**
     * Creates a new asynchronous task. This constructor must be invoked on the
     * UI thread.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     */
    public AsyncServiceTask(final Context context) {

        createWakeLock(context);
        // acquire the wake lock before running
        acquireWakeLock();

        mWorker = new WorkerRunnable<Params, Result>() {
            @Override
            public Result call() throws Exception {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                return doInBackground(mParams);
            }
        };

        mFuture = new FutureTask<Result>(mWorker) {
            @Override
            protected void done() {
                // Message message;
                Result result = null;

                try {
                    result = get();
                } catch (final InterruptedException e) {
                    android.util.Log.w(TAG, e);
                } catch (final ExecutionException e) {
                    throw new RuntimeException(
                            "An error occured while executing doInBackground()",
                            e.getCause());
                } catch (final CancellationException e) {
                    // send the cancellation message
                    onCancelled();
                    return;
                } catch (final Throwable t) {
                    throw new RuntimeException(
                            "An error occured while executing "
                                    + "doInBackground()", t);
                } finally {
                    // we are reference counted wake lock, release it once we
                    // are done with the task
                    releaseWakeLock();
                    finish(result);
                }

            }
        };
    }

    /**
     * Returns the current status of this task.
     * 
     * @return The current status.
     */
    public final Status getStatus() {
        return mStatus;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute} by the
     * caller of this task.
     * 
     * This method can call {@link #publishProgress} to publish updates on the
     * UI thread.
     * 
     * @param params
     *            The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    protected abstract Result doInBackground(Params... params);

    /**
     * Requires notification.
     * 
     * @return true, if successful
     */
    protected boolean requiresNotification() {
        return false;
    }

    /**
     * Run in background.
     * 
     * @param flag
     *            the flag
     */
    protected void runInBackground(final boolean flag) {

    }

    /**
     * Checks if is running in background.
     * 
     * @return true, if is running in background
     */
    public synchronized boolean isRunningInBackground() {
        return true;
    }

    /**
     * Runs on the UI thread after {@link #cancel(boolean)} is invoked.
     * 
     * @see #cancel(boolean)
     * @see #isCancelled()
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    protected void onCancelled() {
    }

    /**
     * Returns <tt>true</tt> if this task was cancelled before it completed
     * normally.
     * 
     * @return <tt>true</tt> if task was cancelled before it completed
     * @see #cancel(boolean)
     */
    public final boolean isCancelled() {
        return mFuture.isCancelled();
    }

    /**
     * Attempts to cancel execution of this task. This attempt will fail if the
     * task has already completed, already been cancelled, or could not be
     * cancelled for some other reason. If successful, and this task has not
     * started when <tt>cancel</tt> is called, this task should never run. If
     * the task has already started, then the <tt>mayInterruptIfRunning</tt>
     * parameter determines whether the thread executing this task should be
     * interrupted in an attempt to stop the task.
     * 
     * @param mayInterruptIfRunning
     *            <tt>true</tt> if the thread executing this task should be
     *            interrupted; otherwise, in-progress tasks are allowed to
     *            complete.
     * @return <tt>false</tt> if the task could not be cancelled, typically
     *         because it has already completed normally; <tt>true</tt>
     *         otherwise
     * @see #isCancelled()
     * @see #onCancelled()
     * @see #isCancelled()
     * @see #onCancelled()
     */
    public final boolean cancel(final boolean mayInterruptIfRunning) {
        return mFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * Waits if necessary for the computation to complete, and then retrieves
     * its result.
     * 
     * @return The computed result.
     * @throws InterruptedException
     *             the interrupted exception
     * @throws ExecutionException
     *             the execution exception
     */
    public final Result get() throws InterruptedException, ExecutionException {
        return mFuture.get();
    }

    /**
     * Waits if necessary for at most the given time for the computation to
     * complete, and then retrieves its result.
     * 
     * @param timeout
     *            Time to wait before cancelling the operation.
     * @param unit
     *            The time unit for the timeout.
     * @return The computed result.
     * @throws InterruptedException
     *             the interrupted exception
     * @throws ExecutionException
     *             the execution exception
     * @throws TimeoutException
     *             the timeout exception
     */
    public final Result get(final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return mFuture.get(timeout, unit);
    }

    /**
     * Executes the task with the specified parameters. The task returns itself
     * (this) so that the caller can keep a reference to it.
     * 
     * @param params
     *            The parameters of the task.
     * @return This instance of AsyncServiceTask.
     *         {@link AsyncServiceTask.Status#RUNNING} or
     *         {@link AsyncServiceTask.Status#FINISHED}.
     */
    public final AsyncServiceTask<Params, Progress, Result> execute(
            final Params... params) {
        if (mStatus != Status.PENDING) {
            switch (mStatus) {
            case RUNNING:
                throw new IllegalStateException("Cannot execute task:"
                        + " the task is already running.");
            case FINISHED:
                throw new IllegalStateException("Cannot execute task:"
                        + " the task has already been executed "
                        + "(a task can be executed only once)");
            default:
                break;
            }
        }

        mStatus = Status.RUNNING;

        mWorker.mParams = params;
        sExecutor.execute(mFuture);

        return this;
    }

    /**
     * finish.
     * 
     * @param result
     *            the result
     */
    private void finish(final Result result) {
        // onPostExecute(result);
        mStatus = Status.FINISHED;
    }

    /**
     * The Class WorkerRunnable.
     * 
     * @param <Params>
     *            the generic type
     * @param <Result>
     *            the generic type
     */
    private static abstract class WorkerRunnable<Params, Result> implements
            Callable<Result> {

        /** The m params. */
        Params[] mParams;
    }

    /**
     * Create a new wake lock if we haven't made one yet and disable ref-count.
     * 
     * @param context
     *            the context
     */
    private synchronized void createWakeLock(final Context context) {

        if (mWakeLock == null) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.d(TAG, "Creating WakeLock");
            }
            final PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            mWakeLock.setReferenceCounted(true);
        }
    }

    /**
     * It's okay to acquire multiple times as we are using it in
     * reference-counted mode.make sure to release it always so its counted down
     * and donot hold any wake locks when no tasks are running
     */
    private void acquireWakeLock() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.d(TAG, "Acquiring WakeLock");
        }
        mWakeLock.acquire();
    }

    /**
     * Don't release the wake lock if it hasn't been created and acquired.
     */
    private void releaseWakeLock() {

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.d(TAG, "Releaseing WakeLock");

            }
        }
    }
}
