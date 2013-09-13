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
                Helper class for storing data in the sugarcrm content providers..
 ******************************************************************************/

package com.imaginea.android.sugarcrm.sync;

import java.util.Map;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.imaginea.android.sugarcrm.rest.SugarBean;
import com.imaginea.android.sugarcrm.util.ContentUtils;

/**
 * The Class SugarCRMOperations.
 */
public class SugarCRMOperations {

    private String mModuleName;

    private String mRelatedModuleName;

    private final ContentValues mValues;

    private ContentProviderOperation.Builder mBuilder;

    private final BatchOperation mBatchOperation;

    private boolean mYield;

    private long mRawId;

    private static final String TAG = "SugarCRMOperations";

    /**
     * Returns an instance of SugarCRMOperations instance for adding new module
     * item to the sugar crm provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param accountName
     *            the username of the current login
     * @return instance of ContactOperations
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param sBean
     *            a {@link com.imaginea.android.sugarcrm.rest.SugarBean} object.
     * @param batchOperation
     *            a {@link com.imaginea.android.sugarcrm.sync.BatchOperation}
     *            object.
     */
    public static SugarCRMOperations createNewModuleItem(final Context context,
            final String moduleName, final String accountName,
            final BatchOperation batchOperation) {
        return new SugarCRMOperations(context, moduleName, batchOperation);
    }

    /**
     * Returns an instance of SugarCRMOperations instance for adding new module
     * item to the sugar crm provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param accountName
     *            the username of the current login
     * @return instance of ContactOperations
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param relationModuleName
     *            a {@link java.lang.String} object.
     * @param rawId
     *            a long.
     * @param sBean
     *            a {@link com.imaginea.android.sugarcrm.rest.SugarBean} object.
     * @param relatedBean
     *            a {@link com.imaginea.android.sugarcrm.rest.SugarBean} object.
     * @param batchOperation
     *            a {@link com.imaginea.android.sugarcrm.sync.BatchOperation}
     *            object.
     */
    public static SugarCRMOperations createNewRelatedModuleItem(
            final Context context, final String moduleName,
            final String relationModuleName, final long rawId,
            final SugarBean sBean, final BatchOperation batchOperation) {
        return new SugarCRMOperations(context, moduleName, relationModuleName,
                rawId, batchOperation);
    }

    /**
     * Returns an instance of SugarCRMOperations for updating existing module
     * item in the sugarcrm provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param rawId
     *            the unique Id of the existing rawId
     * @return instance of ContactOperations
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param sBean
     *            a {@link com.imaginea.android.sugarcrm.rest.SugarBean} object.
     * @param batchOperation
     *            a {@link com.imaginea.android.sugarcrm.sync.BatchOperation}
     *            object.
     */
    public static SugarCRMOperations updateExistingModuleItem(
            final Context context, final String moduleName,
            final SugarBean sBean, final long rawId,
            final BatchOperation batchOperation) {
        return new SugarCRMOperations(context, moduleName, rawId,
                batchOperation);
    }

    /**
     * Constructor for SugarCRMOperations.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param batchOperation
     *            a {@link com.imaginea.android.sugarcrm.sync.BatchOperation}
     *            object.
     */
    public SugarCRMOperations(final Context context,
            final BatchOperation batchOperation) {
        mValues = new ContentValues();
        mModuleName = "";
        mYield = true;
        mBatchOperation = batchOperation;
    }

    /**
     * Constructor for SugarCRMOperations.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param relationModuleName
     *            a {@link java.lang.String} object.
     * @param accountName
     *            a {@link java.lang.String} object.
     * @param batchOperation
     *            a {@link com.imaginea.android.sugarcrm.sync.BatchOperation}
     *            object.
     */
    public SugarCRMOperations(final Context context, final String moduleName,
            final String relationModuleName, final BatchOperation batchOperation) {
        this(context, batchOperation);
        mModuleName = moduleName;
        mRelatedModuleName = relationModuleName;
    }

    /**
     * 
     * Constructor for SugarCRMOperations.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param accountName
     *            a {@link java.lang.String} object.
     * @param batchOperation
     *            a {@link com.imaginea.android.sugarcrm.sync.BatchOperation}
     *            object.
     */
    public SugarCRMOperations(final Context context, final String moduleName,
            final BatchOperation batchOperation) {
        this(context, batchOperation);
        mModuleName = moduleName;

    }

    /**
     * Constructor for SugarCRMOperations.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param sBean
     *            a {@link com.imaginea.android.sugarcrm.rest.SugarBean} object.
     * @param rawId
     *            a long.
     * @param batchOperation
     *            a {@link com.imaginea.android.sugarcrm.sync.BatchOperation}
     *            object.
     */
    public SugarCRMOperations(final Context context, final String moduleName,
            final long rawId, final BatchOperation batchOperation) {
        this(context, batchOperation);
        mModuleName = moduleName;
        mRawId = rawId;
    }

    /**
     * Constructor for SugarCRMOperations.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param relationModuleName
     *            a {@link java.lang.String} object.
     * @param sBean
     *            a {@link com.imaginea.android.sugarcrm.rest.SugarBean} object.
     * @param rawId
     *            a long.
     * @param batchOperation
     *            a {@link com.imaginea.android.sugarcrm.sync.BatchOperation}
     *            object.
     */
    public SugarCRMOperations(final Context context, final String moduleName,
            final String relationModuleName, final long rawId,
            final BatchOperation batchOperation) {
        this(context, batchOperation);
        mModuleName = moduleName;
        mRelatedModuleName = relationModuleName;
        mRawId = rawId;
    }

    /**
     * 
     * addSugarBean
     * 
     * @param sBean
     *            a {@link com.imaginea.android.sugarcrm.rest.SugarBean} object.
     * @return a {@link com.imaginea.android.sugarcrm.sync.SugarCRMOperations}
     *         object.
     */
    public SugarCRMOperations addSugarBean(final SugarBean sBean) {
        final Map<String, String> map = sBean.getEntryList();
        for (final String fieldName : map.keySet()) {
            final String fieldValue = map.get(fieldName);
            if (!TextUtils.isEmpty(fieldValue)) {
                mValues.put(fieldName, fieldValue);
            }
        }
        if (mValues.size() > 0) {
            addInsertOp();
        }
        return this;
    }

    /**
     * 
     * addRelatedSugarBean
     * 
     * @param sBean
     *            a {@link com.imaginea.android.sugarcrm.rest.SugarBean} object.
     * @param relatedBean
     *            a {@link com.imaginea.android.sugarcrm.rest.SugarBean} object.
     * @return a {@link com.imaginea.android.sugarcrm.sync.SugarCRMOperations}
     *         object.
     */
    public SugarCRMOperations addRelatedSugarBean(final SugarBean sBean,
            final SugarBean relatedBean) {
        final Map<String, String> map = relatedBean.getEntryList();
        for (final String fieldName : map.keySet()) {
            final String fieldValue = map.get(fieldName);
            if (!TextUtils.isEmpty(fieldValue)) {
                mValues.put(fieldName, fieldValue);
            }
        }
        if (mValues.size() > 0) {
            addRelatedInsertOp();
        }
        return this;
    }

    /**
     * updateSugarBean
     * 
     * @param sBean
     *            a {@link com.imaginea.android.sugarcrm.rest.SugarBean} object.
     * @param uri
     *            a {@link android.net.Uri} object.
     * @return a {@link com.imaginea.android.sugarcrm.sync.SugarCRMOperations}
     *         object.
     */
    public SugarCRMOperations updateSugarBean(final SugarBean sBean,
            final Uri uri) {
        final Map<String, String> map = sBean.getEntryList();
        for (final String fieldName : map.keySet()) {
            final String fieldValue = map.get(fieldName);
            if (!TextUtils.isEmpty(fieldValue)) {
                mValues.put(fieldName, fieldValue);
            }
        }
        if (mValues.size() > 0) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "updateSugarBean: uri - " + uri);
            }
            addUpdateOp(uri);
        }
        return this;
    }

    /**
     * Adds an insert operation into the batch.
     */
    private void addInsertOp() {

        final Uri contentUri = ContentUtils.getModuleUri(mModuleName);

        mBuilder = newInsertCpo(contentUri, mYield);
        mBuilder.withValues(mValues);
        mYield = false;
        mBatchOperation.add(mBuilder.build());
    }

    /**
     * Adds an insert operation into the batch
     */
    private void addRelatedInsertOp() {
        final Uri contentUri = ContentUtils.getModuleUri(mModuleName);
        final Uri relatedUri = Uri.withAppendedPath(
                ContentUris.withAppendedId(contentUri, mRawId),
                mRelatedModuleName);
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "addRelatedInsertOp: relatedUri - " + relatedUri);
        }
        mBuilder = newInsertCpo(relatedUri, mYield);
        mBuilder.withValues(mValues);

        mYield = false;
        mBatchOperation.add(mBuilder.build());
    }

    /**
     * Adds an update operation into the batch
     */
    private void addUpdateOp(final Uri uri) {
        mBuilder = newUpdateCpo(uri, mYield).withValues(mValues);
        mYield = false;
        mBatchOperation.add(mBuilder.build());
    }

    /**
     * 
     * newInsertCpo
     * 
     * @param uri
     *            a {@link android.net.Uri} object.
     * @param yield
     *            a boolean.
     * @return a {@link android.content.ContentProviderOperation.Builder}
     *         object.
     */
    public static ContentProviderOperation.Builder newInsertCpo(final Uri uri,
            final boolean yield) {
        return ContentProviderOperation.newInsert(uri).withYieldAllowed(yield);
    }

    /**
     * 
     * newUpdateCpo
     * 
     * @param uri
     *            a {@link android.net.Uri} object.
     * @param yield
     *            a boolean.
     * @return a {@link android.content.ContentProviderOperation.Builder}
     *         object.
     */
    public static ContentProviderOperation.Builder newUpdateCpo(final Uri uri,
            final boolean yield) {
        return ContentProviderOperation.newUpdate(uri).withYieldAllowed(yield);
    }

    /**
     * 
     * newDeleteCpo
     * 
     * @param uri
     *            a {@link android.net.Uri} object.
     * @param yield
     *            a boolean.
     * @return a {@link android.content.ContentProviderOperation.Builder}
     *         object.
     */
    public static ContentProviderOperation.Builder newDeleteCpo(final Uri uri,
            final boolean yield) {
        return ContentProviderOperation.newDelete(uri).withYieldAllowed(yield);

    }
}
