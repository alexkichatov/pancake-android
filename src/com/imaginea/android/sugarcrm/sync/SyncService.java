/*******************************************************************************
 * Copyright (c)
 * {DATE} 27/08/2013
 * {INITIAL COPYRIGHT OWNER} Asha , Muralidaran
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors::
 *                  Asha, Muralidaran- initial API and implementation and/or initial documentation
 * Project Name : SugarCrm Pancake
 * Description : 
 *                  Service to handle Account sync. This is invoked with an intent with action
 * ACTION_AUTHENTICATOR_INTENT. It instantiates the syncadapter and returns its
 * IBinder
 ******************************************************************************/

package com.imaginea.android.sugarcrm.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * The Class SyncService.
 */
public class SyncService extends Service {

    /** The Constant sSyncAdapterLock. */
    private static final Object sSyncAdapterLock = new Object();

    /** The s sync adapter. */
    private static SyncAdapter sSyncAdapter = null;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
