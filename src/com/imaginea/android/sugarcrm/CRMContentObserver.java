/*******************************************************************************
 * Copyright (c) 2013 Asha, Murli.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Asha, Murli - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : CRMContentObserver 
 * ******************************************************************************/
package com.imaginea.android.sugarcrm;

import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

/**
 * An asynchronous update interface for receiving notifications about CRMContent
 * information as the CRMContent is constructed.
 */
public class CRMContentObserver extends ContentObserver {

    /**
     * This method is called when information about an CRMContent which was
     * previously requested using an asynchronous interface becomes available.
     * 
     * @param handler
     *            the handler
     */
    public CRMContentObserver(Handler handler) {
        super(handler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.database.ContentObserver#onChange(boolean)
     */
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.d("CRMCO", "onChange called");
    }

}
