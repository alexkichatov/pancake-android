/*******************************************************************************
 * Copyright (c) 2013 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *          chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : SugarService 
 * Description : 
 *              SugarService, follows the APIDemos pattern of command handling example of a
 * Service
 ******************************************************************************/

package com.imaginea.android.sugarcrm.services;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.imaginea.android.sugarcrm.util.AsyncServiceTask;
import com.imaginea.android.sugarcrm.util.CRMCustomLogFormatter;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * SugarService, follows the APIDemos pattern of command handling example of a
 * Service.
 */
public class SugarService extends Service {

    /** file handler for logging the sync/copy requests. */
    private static FileHandler fileHandler;

    /** The m task map. */
    @SuppressLint("UseSparseArrays")
    @SuppressWarnings("rawtypes")
    private static Map<Integer, AsyncServiceTask> mTaskMap = new HashMap<Integer, AsyncServiceTask>();

    /** The m service looper. */
    private volatile Looper mServiceLooper;

    /** The m service handler. */
    private volatile ServiceHandler mServiceHandler;

    /** The Constant ONE_MINUTE. */
    public static final int ONE_MINUTE = 60 * 1000;

    /** The m recent start id. */
    private static int mRecentStartId;

    /** The m messenger. */
    private static Messenger mMessenger;

    /** The Constant ACTION_START. */
    public static final String ACTION_START = "com.imaginea.action.ACTION_START";

    /** The Constant TAG. */
    private static final String TAG = SugarService.class.getSimpleName();

    /** {@inheritDoc} */
    @Override
    public void onCreate() {

        Log.i(TAG, "OnCreate: ");

        initLogger();

        /**
         * Start up the thread running the service. Note that we create a
         * separate thread because the service normally runs in the process's
         * main thread, which we don't want to block.
         */
        final HandlerThread thread = new HandlerThread(TAG);
        thread.start();

        /**
         * temporary hack to stop the service, If you call
         * Service.stopSelfResult(startId) function with the most-recently
         * received start ID before you have called it for previously received
         * IDs, the service will be immediately stopped anyway. If you may end
         * up processing IDs out of order (such as by dispatching them on
         * separate threads), then you are responsible for stopping them in the
         * same order you received them
         */
        final Thread serviceStopThread = new Thread(serviceStopperRunnable);
        serviceStopThread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

    }

    /**
     * Once the user gets his job done, this thread after a minute checks to see
     * if any tasks are running and calls stop on service If a new startId comes
     * in, then the taskSize will be greater than 1 as we assign the startId to
     * recentStartId after we create anew task. If before adding the task, the
     * stop executes, then the stopSelfResult will return false, if a new
     * startId is started. Worst case a new service is created
     */
    private final Runnable serviceStopperRunnable = new Runnable() {

        @Override
        public void run() {
            while (true) {
                try {
                    SystemClock.sleep(ONE_MINUTE);

                    if (mTaskMap.size() == 0) {
                        final boolean stopped = stopSelfResult(mRecentStartId);

                        if (stopped) {
                            break;
                        }
                    }
                } catch (final Exception e) {
                    Log.i(TAG, "Exception found" + e);
                }
            }
        }
    };

    /**
     * isRunning returns if the transaction is running or not.
     * 
     * @param transactionId
     *            a long.
     * @return a boolean.
     */
    public static synchronized boolean isRunning(final int transactionId) {

        final AsyncServiceTask<?, ?, ?> task = mTaskMap.get(transactionId);

        if (task == null) {
            return false;
        }
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.d(TAG, "Task Status:" + task.getStatus().name());
        }
        if (task.getStatus() == AsyncServiceTask.Status.FINISHED) {
            mTaskMap.remove(transactionId);
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.d(TAG, "task removed");
            }
            return false;
        }
        return true;
    }

    /**
     * The Class ServiceHandler.
     */
    private final class ServiceHandler extends Handler {

        /**
         * Instantiates a new service handler.
         * 
         * @param looper
         *            the looper
         */
        public ServiceHandler(final Looper looper) {
            super(looper);
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(final Message msg) {
            final Intent intent = (Intent) msg.obj;
            switch (msg.what) {
            case Util.GET:
                final EntryListServiceTask entryListServiceTask = new EntryListServiceTask(
                        getBaseContext(), intent);
                mTaskMap.put(Util.getId(), entryListServiceTask);
                entryListServiceTask.execute();
                break;
            case Util.UPDATE:
                UpdateServiceTask updateServiceTask = new UpdateServiceTask(
                        getBaseContext(), intent);
                mTaskMap.put(Util.getId(), updateServiceTask);
                updateServiceTask.execute();
                break;
            case Util.DELETE:
                updateServiceTask = new UpdateServiceTask(getBaseContext(),
                        intent);
                mTaskMap.put(Util.getId(), updateServiceTask);
                updateServiceTask.execute();
                break;
            case Util.INSERT:
                updateServiceTask = new UpdateServiceTask(getBaseContext(),
                        intent);
                mTaskMap.put(Util.getId(), updateServiceTask);
                updateServiceTask.execute();
                break;
            default:

                break;
            }
            mRecentStartId = msg.arg1;
        }
    };

    /**
     * isCancelled.
     * 
     * @param transactionId
     *            a long.
     * @return a boolean.
     */
    public static synchronized boolean isCancelled(final int transactionId) {
        final AsyncServiceTask<?, ?, ?> task = mTaskMap.get(transactionId);
        if (task != null) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.d(TAG,
                        "isCancelled:Task is cancelled:" + task.isCancelled());
            }
            return task.isCancelled();
        }
        return false;
    }

    /**
     * This is the old onStart method that will be called on the pre-2.0 //
     * platform. On 2.0 or later we override onStartCommand() so this // method
     * will not be called.
     * 
     * @param intent
     *            the intent
     * @param startId
     *            the start id
     */
    @Override
    public void onStart(final Intent intent, final int startId) {
        super.onStart(intent, startId);
        handleStart(intent, startId);
    }

    /** {@inheritDoc} */
    @Override
    public int onStartCommand(final Intent intent, final int flags,
            final int startId) {
        handleStart(intent, startId);
        return START_NOT_STICKY;
    }

    /**
     * common method to handle intents for pre2.0 and post 2.0 devices
     * 
     * @param intent
     *            the intent
     * @param startId
     *            the start id
     */
    void handleStart(final Intent intent, final int startId) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "onStart: " + System.currentTimeMillis());
        }
        // set the service to foreground so that we do not get killed while we
        // this is sort of deprecated in Android 2.0

        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Starting #" + startId + ": " + intent.getExtras());
        }
        final Message msg = mServiceHandler.obtainMessage();
        final Bundle extras = intent.getExtras();

        final int command = extras.getInt(Util.COMMAND);

        msg.what = command;
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Sending: " + msg);
        }
    }

    /**
     * initLogger.
     */
    private void initLogger() {
        try {
            final File storeLog = new File(
                    Environment.getExternalStorageDirectory(), "SugarCRM"
                            + File.separatorChar + "Cache");

            if (!storeLog.exists()) {
                storeLog.mkdirs();

            }
            final File logFile = new File(storeLog, "CRMLog.txt");
            final String name = logFile.getAbsolutePath();

            fileHandler = new FileHandler(name, 10 * 1024, 1, false);
            fileHandler.setFormatter(new CRMCustomLogFormatter());

            Logger.getLogger("CRM").addHandler(fileHandler);
        } catch (final IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * closeLogger.
     */
    private void closeLogger() {
        if (fileHandler != null) {
            fileHandler.close();
        }
    }

    /**
     * logStatus.
     * 
     * @param str
     *            a {@link java.lang.String} object.
     */
    public static void logStatus(final String str) {
        if (fileHandler != null) {
            final Logger logger = Logger.getLogger("CRM");
            logger.log(Level.INFO, str);
        }
    }

    /**
     * logError.
     * 
     * @param str
     *            a {@link java.lang.String} object.
     */
    public static void logError(final String str) {
        if (fileHandler != null) {
            final Logger logger = Logger.getLogger("CRM");
            logger.log(Level.SEVERE, str);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {

        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "onDestroy");
        }
        closeLogger();
        super.onDestroy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    /**
     * We only register one Messenger as only one Activity is connected to us at
     * given time.modify to add to a list if we need multiple activity support
     * -?? this will never happen in current android architecture; will happen
     * if single screen holds two activities ???
     * 
     * @param messenger
     *            a {@link android.os.Messenger} object.
     */
    public static void registerMessenger(final Messenger messenger) {

        mMessenger = messenger;
    }

    /**
     * keeping the messenger in case we go ahead with multiple listeners
     * Activities should unregister in onPause and register in onResume so that
     * they continue to receive messages specific to them, further filtering can
     * be done while sending messages so that unwanted messages are not sent.
     * 
     * @param messenger
     *            a {@link android.os.Messenger} object.
     */
    public static void unregisterMessenger(final Messenger messenger) {
        mMessenger = null;
    }

    /**
     * messages will be sent to the activity or any component that is currently
     * registered with this service made static so can directly call this to
     * display the status of the.
     * 
     * @param what
     *            a int.
     * @param obj
     *            a {@link java.lang.Object} object.
     */
    public static synchronized void sendMessage(final int what, final Object obj) {

        if (mMessenger == null) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Messenger is null ");
            }
            return;
        }
        try {
            if (mMessenger != null) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG,
                            "Sending Message using Messenger"
                                    + mMessenger.toString());
                }
                final Message message = Message.obtain();
                message.what = what;
                message.obj = obj;
                mMessenger.send(message);
            }
        } catch (final RemoteException e) {

            Log.e(TAG, e.getMessage(), e);
        }
    }
}
