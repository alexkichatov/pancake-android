/*******************************************************************************
 * Copyright (c)
 *   {DATE} 27/08/2013
 *   {INITIAL COPYRIGHT OWNER} Asha , Muralidaran
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors::
 *                  Asha, Muralidaran- initial API and implementation and/or initial documentation
 *   Project Name : SugarCrm Pancake
 *   FileName : BatchOpeartion 
 *   Description : 
                handles execution of batch mOperations on SugarCRM provider.
 ******************************************************************************/

package com.imaginea.android.sugarcrm.sync;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;

/**
 * The Class BatchOperation.
 */
public class BatchOperation {

    /** The m resolver. */
    private final ContentResolver mResolver;

    /** The m operations. */
    ArrayList<ContentProviderOperation> mOperations;// List for storing the
                                                    // batch mOperations

    /** The log tag. */
    private final String LOG_TAG = BatchOperation.class.getSimpleName();

    /**
     * Constructor for BatchOperation.
     * 
     * @param resolver
     *            a {@link android.content.ContentResolver} object.
     */
    public BatchOperation(ContentResolver resolver) {
        mResolver = resolver;
        mOperations = new ArrayList<ContentProviderOperation>();
    }

    /**
     * Size.
     * 
     * @return the int
     */
    public int size() {
        return mOperations.size();
    }

    /**
     * add
     * 
     * @param cpo
     *            a {@link android.content.ContentProviderOperation} object.
     */
    public void add(ContentProviderOperation cpo) {
        mOperations.add(cpo);
    }

    /**
     * Execute. : Apply the mOperations to the content provider
     */
    public void execute() {
        if (mOperations.size() == 0) {
            Log.v(LOG_TAG, "No Batch Operations found to execute");
            return;
        }

        try {
            Log.v(LOG_TAG, " Batch Operations Size:" + mOperations.size());
            final ContentProviderResult[] result = mResolver.applyBatch(
                    SugarCRMProvider.AUTHORITY, mOperations);
            Log.v(LOG_TAG, " result.length" + result.length);
        } catch (final OperationApplicationException e1) {
            Log.e(LOG_TAG, "storing Module data failed", e1);
        } catch (final RemoteException e2) {
            Log.e(LOG_TAG, "storing Module data failed", e2);
        }
        mOperations.clear();
    }
}
