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
                Class for managing sugar crm sync related mOperations. should be capable of
 *              updating the SyncStats in SyncResult object.
 *              @Syncs Modules
 *              @Syncs AclAccess data
 *              @Syncs Module Data and Relationship Data
 * 
 ******************************************************************************/

package com.imaginea.android.sugarcrm.sync;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.R;
import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLActions;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLRoles;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Contacts;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ModuleColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Modules;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Sync;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.SyncColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Users;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.rest.SugarBean;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.Module;
import com.imaginea.android.sugarcrm.util.RelationshipStatus;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * The Class SugarSyncManager.
 */
public final class SugarSyncManager {

    /** The m database helper. */
    private static DatabaseHelper mDatabaseHelper;

    /** The m selection. */
    private static String mSelection = SugarCRMContent.SUGAR_BEAN_ID + "=?";

    /** The m bean id field. */
    private static String mBeanIdField = Contacts.BEAN_ID;

    /** The m query. */
    private static String mQuery = "";

    // for every module, linkName to field Array is retrieved from db cache and
    // then cleared
    private static Map<String, List<String>> mLinkNameToFieldsArray = new HashMap<String, List<String>>();

    /**
     * Instantiates a new sugar sync manager.
     */
    private SugarSyncManager() {

    }

    /**
     * make the date formatter static as we are synchronized even though its not
     * thread-safe
     */
    private static DateFormat mDateFormat;

    private static final String LOG_TAG = SugarSyncManager.class
            .getSimpleName();

    /**
     * Synchronize raw contacts
     * 
     * @param context
     *            The context of Authenticator Activity
     * @param account
     *            The username for the account
     * @param sessionId
     *            The session Id associated with sugarcrm session
     * @param moduleName
     *            The name of the module to sync
     * @param syncResult
     *            a {@link android.content.SyncResult} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static synchronized void syncModulesData(Context context,
            String account, int syncType, String sessionId, String moduleName,
            SyncResult syncResult) throws SugarCrmException {
        long rawId = 0;
        final ContentResolver resolver = context.getContentResolver();
        final BatchOperation batchOperation = new BatchOperation(resolver);
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper(context);
        }
        int offset = 0;
        final String deleted = "";
        final SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        // TODO use a constant and remove this as we start from the login screen
        final String url = pref.getString(Util.PREF_REST_URL,
                context.getString(R.string.defaultUrl));

        final String[] projections = ContentUtils
                .getModuleProjections(moduleName);
        final String orderBy = ContentUtils.getModuleSortOrder(moduleName);
        setLinkNameToFieldsArray(context, moduleName);

        // TODO - Fetching based on dates
        if (mDateFormat == null) {
            mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        }

        final Date startDate = getSyncStartDate(context, syncType, moduleName);

        final Date endDate = getSyncEndDate(context, syncType, moduleName);

        final int maxResults = 1000;

        final String maxRecordsPerSession = pref.getString(
                Util.PREF_FETCH_RECORDS_SIZE, "2000");
        int downloadRecordsSizePerRequest = -1;

        if (!maxRecordsPerSession.equals("ALL")) {
            downloadRecordsSizePerRequest = Integer
                    .parseInt(maxRecordsPerSession);
        }

        mQuery = moduleName + "." + ModuleFields.DATE_MODIFIED + ">'"
                + mDateFormat.format(startDate) + "' AND " + moduleName + "."
                + ModuleFields.DATE_MODIFIED + "<='"
                + mDateFormat.format(endDate) + "'";

        while (true) {
            if (projections == null || projections.length == 0) {
                break;
            }

            final SugarBean[] sBeans = Rest.getEntryList(url, sessionId,
                    moduleName, mQuery, orderBy, "" + offset, projections,
                    mLinkNameToFieldsArray, "" + maxResults, deleted);
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "fetching " + offset + "to "
                        + (offset + maxResults));
            }
            if (sBeans == null || sBeans.length == 0) {
                break;
            }
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "In Syncmanager");
            }

            for (final SugarBean sBean : sBeans) {

                final String beandIdValue = sBean.getFieldValue(mBeanIdField);
                // Check to see if the contact needs to be inserted or updated
                rawId = lookupRawId(resolver, moduleName, beandIdValue);
                if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                    Log.v(LOG_TAG, "beanId/rawid:" + beandIdValue + "/" + rawId);
                }

                final String name = TextUtils.isEmpty(sBean
                        .getFieldValue(ModuleFields.NAME)) ? sBean
                        .getFieldValue(ModuleFields.FIRST_NAME) : sBean
                        .getFieldValue(ModuleFields.NAME);

                if (rawId != 0) {
                    if (!sBean.getFieldValue(ModuleFields.DELETED).equals(
                            Util.DELETED_ITEM)) {
                        Log.d(LOG_TAG, "updating... " + moduleName + ": "
                                + rawId + ") " + name);
                        updateModuleItem(context, resolver, moduleName, sBean,
                                rawId, batchOperation);
                    } else {
                        // delete module item - never delete the item here, just
                        // update the deleted
                        // flag
                        Log.d(LOG_TAG, "deleting... " + moduleName + ": "
                                + rawId + ") " + name);
                        deleteModuleItem(rawId, moduleName, batchOperation);
                    }
                } else {
                    // add new moduleItem
                    if (!sBean.getFieldValue(ModuleFields.DELETED).equals(
                            Util.DELETED_ITEM)) {
                        Log.d(LOG_TAG, "inserting... " + moduleName + ": "
                                + " " + name);
                        addModuleItem(context, account, sBean, moduleName,
                                batchOperation);
                    }
                }
                // syncRelationships(context, account, sessionId, moduleName,
                // sBean,

                // A sync adapter should batch operations on multiple contacts,
                // because it will make a dramatic performance difference.
                if (batchOperation.size() >= 50) {
                    batchOperation.execute();
                }
            }
            batchOperation.execute();
            offset = offset + maxResults;
            for (final SugarBean sBean : sBeans) {
                syncRelationshipsData(context, account, sessionId, moduleName,
                        sBean, batchOperation);
                // A sync adapter should batch operations on multiple contacts,
                // because it will make a dramatic performance difference.
                if (batchOperation.size() >= 50) {
                    batchOperation.execute();
                }
            }
            batchOperation.execute();

            if ((downloadRecordsSizePerRequest != -1)
                    && (offset >= downloadRecordsSizePerRequest)) {
                break;
            }

        }
        mLinkNameToFieldsArray.clear();
        mDatabaseHelper.close();
        mDatabaseHelper = null;

        updateLastSyncTime(context, syncType, moduleName, endDate.getTime());

    }

    /**
     * updateLastSyncTime
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param syncType
     *            integer syncType
     * @param module
     *            a {@link java.lang.String} object.
     * @param endTime
     *            long time in milliseconds
     * 
     * 
     */
    private static void updateLastSyncTime(Context context, int syncType,
            String module, long endTime) {

        if (Util.SYNC_MODULE_DATA == syncType) {
            final String selection = ModuleColumns.MODULE_NAME + "='" + module
                    + "'";
            final ContentValues values = new ContentValues();
            values.put(ModuleColumns.LAST_SYNC_TIME, Long.toString(endTime));
            context.getContentResolver().update(Modules.CONTENT_URI, values,
                    selection, null);
        } else {
            boolean update = false;
            String selection = ModuleColumns.MODULE_NAME + "='" + module + "'";
            final Cursor cursor = context.getContentResolver().query(
                    Modules.CONTENT_URI, Modules.DETAILS_PROJECTION, selection,
                    null, null);
            if ((null != cursor) && (cursor.getCount() > 0)) {
                cursor.moveToFirst();
                // read the last sync time and check whether it is intial sync
                // or not
                // if lastSyncTime is null then update the last sync time
                final String lastSyncTime = cursor.getString(cursor
                        .getColumnIndex(ModuleColumns.LAST_SYNC_TIME));
                if (lastSyncTime == null) {
                    update = true;
                } else {
                    final long lastTime = Long.parseLong(lastSyncTime);
                    if (endTime > lastTime) {
                        update = true;
                    }
                }
                cursor.close();
            }
            if (update) {
                selection = ModuleColumns.MODULE_NAME + "='" + module + "'";
                final ContentValues values = new ContentValues();
                values.put(ModuleColumns.LAST_SYNC_TIME, Long.toString(endTime));
                context.getContentResolver().update(Modules.CONTENT_URI,
                        values, selection, null);
            }
        }
    }

    /**
     * getSyncEndDate
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param syncType
     *            integer syncType
     * @param module
     *            a {@link java.lang.String} object.
     * @return Date a {@link java.util.Date} object
     * 
     */
    private static Date getSyncEndDate(Context context, int syncType,
            String module) {

        final SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);

        final Date endDate = new Date();
        endDate.setTime(System.currentTimeMillis());

        if (Util.SYNC_MODULES_DATA == syncType) {
            final long time = pref.getLong(Util.PREF_SYNC_END_TIME,
                    endDate.getTime());
            endDate.setTime(time);
        } else {
            final String selection = ModuleColumns.MODULE_NAME + "='" + module
                    + "'";
            final Cursor cursor = context.getContentResolver().query(
                    Modules.CONTENT_URI, Modules.DETAILS_PROJECTION, selection,
                    null, null);
            if ((null != cursor) && (cursor.getCount() > 0)) {
                cursor.moveToFirst();
                // read the last sync time and check whether it is intial sync
                // or not
                // if lastSyncTime is null then read the end sync time from
                // settings
                final String lastSyncTime = cursor.getString(cursor
                        .getColumnIndex(ModuleColumns.LAST_SYNC_TIME));
                if (lastSyncTime == null) {
                    final long syncTime = pref.getLong(Util.PREF_SYNC_END_TIME,
                            endDate.getTime());
                    endDate.setTime(syncTime);
                }
                cursor.close();
            }
        }

        return endDate;
    }

    /**
     * getSyncStartDate
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param syncType
     *            integer syncType
     * @param module
     *            a {@link java.lang.String} object.
     * @return Date a {@link java.util.Date} object
     * 
     */
    private static Date getSyncStartDate(Context context, int syncType,
            String module) {

        final SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);

        final Date startDate = new Date();

        if (Util.SYNC_MODULES_DATA == syncType) {
            startDate.setMonth(startDate.getMonth() - 1);
            final long time = pref.getLong(Util.PREF_SYNC_START_TIME,
                    startDate.getTime());
            startDate.setTime(time);
        } else {

            // get the last sync time for module
            final String selection = ModuleColumns.MODULE_NAME + "='" + module
                    + "'";

            final Cursor cursor = context.getContentResolver().query(
                    Modules.CONTENT_URI, Modules.DETAILS_PROJECTION, selection,
                    null, null);
            if ((null != cursor) && (cursor.getCount() > 0)) {
                cursor.moveToFirst();
                // read the last sync time from db and set it startDate.
                final String lastSyncTime = cursor.getString(cursor
                        .getColumnIndex(ModuleColumns.LAST_SYNC_TIME));
                if (lastSyncTime != null) {
                    final long syncTime = Long.parseLong(lastSyncTime);
                    startDate.setTime(syncTime);
                } else {
                    startDate.setMonth(startDate.getMonth() - 1);
                    final long time = pref.getLong(Util.PREF_SYNC_START_TIME,
                            startDate.getTime());
                    startDate.setTime(time);
                }
                cursor.close();
            }
        }

        return startDate;
    }

    /**
     * syncRelationships
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param account
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param bean
     *            a {@link com.imaginea.android.sugarcrm.rest.SugarBean} object.
     * @param batchOperation
     *            a {@link com.imaginea.android.sugarcrm.sync.BatchOperation}
     *            object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static void syncRelationshipsData(Context context, String account,
            String sessionId, String moduleName, SugarBean bean,
            BatchOperation batchOperation) throws SugarCrmException {
        final String[] relationships = ContentUtils
                .getModuleRelationshipItems(moduleName);
        if (relationships == null) {
            if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                Log.v(LOG_TAG, "relationships is null");
            }
            return;
        }
        final String beandIdValue = bean.getFieldValue(mBeanIdField);
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            Log.v(LOG_TAG, "syncRelationshipsData: beanId:" + beandIdValue);
        }

        final long rawId = lookupRawId(context.getContentResolver(),
                moduleName, beandIdValue);
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            Log.v(LOG_TAG, "syncRelationshipsData: RawId:" + rawId);
        }

        for (final String relation : relationships) {
            final String linkFieldName = mDatabaseHelper
                    .getLinkfieldName(relation);
            // for a particular module-link field name
            final SugarBean[] relationshipBeans = bean
                    .getRelationshipBeans(linkFieldName);
            if (relationshipBeans == null || relationshipBeans.length == 0) {
                if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                    Log.v(LOG_TAG, "relationship beans is null or empty");
                }
                continue;
            }

            for (final SugarBean relationbean : relationshipBeans) {
                final String relationBeanId = relationbean
                        .getFieldValue(mBeanIdField);

                if (relationBeanId == null) {
                    continue;
                }

                final long relationRawId = lookupRawId(
                        context.getContentResolver(), relation, relationBeanId);
                if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                    Log.v(LOG_TAG, "RelationBeanId/RelatedRawid:"
                            + relationRawId + "/" + relationBeanId);
                }

                if (relationRawId != 0) {
                    if (!relationbean.getFieldValue(ModuleFields.DELETED)
                            .equals(Util.DELETED_ITEM)) {
                        // update module Item
                        Log.i(LOG_TAG, "updating... " + moduleName + "_"
                                + relation + ": relationRawId - "
                                + relationRawId + ")");
                        updateRelatedModuleItem(context, moduleName, rawId,
                                relation, relationbean, relationRawId,
                                batchOperation);
                    } else {
                        // delete module item
                        Log.i(LOG_TAG, "deleting... " + moduleName + "_"
                                + relation + ": relationRawId - "
                                + relationRawId + ") ");
                        deleteRelatedModuleItem(rawId, relationRawId,
                                moduleName, relation, batchOperation);
                    }
                } else {
                    // add new moduleItem
                    if (!relationbean.getFieldValue(ModuleFields.DELETED)
                            .equals(Util.DELETED_ITEM)) {
                        Log.d(LOG_TAG, "inserting... " + moduleName + "_"
                                + relation);
                        addRelatedModuleItem(context, rawId, bean,
                                relationbean, moduleName, relation,
                                batchOperation);
                    }
                }

            }
        }
    }

    /**
     * set LinkNameToFieldsArray sets the array of link names to get for a given
     * module
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static void setLinkNameToFieldsArray(Context context,
            String moduleName) throws SugarCrmException {
        final String[] relationships = ContentUtils
                .getModuleRelationshipItems(moduleName);
        if (relationships == null) {
            return;
        }
        for (final String relation : relationships) {
            // get the relationships for a user only if access is allowed
            if (ContentUtils.isModuleAccessAvailable(context, relation)) {
                final String linkFieldName = mDatabaseHelper
                        .getLinkfieldName(relation);
                final String[] relationProj = ContentUtils
                        .getModuleProjections(relation);

                // remove ACCOUNT_NAME from the projection
                final List<String> projList = new ArrayList<String>(
                        Arrays.asList(relationProj));
                projList.remove(ModuleFields.ACCOUNT_NAME);
                mLinkNameToFieldsArray.put(linkFieldName, projList);
            }
        }
    }

    /**
     * Adds a single sugar bean to the sugar crm provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param accountName
     *            the account the contact belongs to
     * @param sBean
     *            the SyncAdapter SugarBean object
     */
    private static void addModuleItem(Context context, String accountName,
            SugarBean sBean, String moduleName, BatchOperation batchOperation) {
        // Put the data in the contacts provider
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            Log.v(LOG_TAG, "In addModuleItem");
        }
        final SugarCRMOperations moduleItemOp = SugarCRMOperations
                .createNewModuleItem(context, moduleName, accountName,
                        batchOperation);
        moduleItemOp.addSugarBean(sBean);

    }

    /**
     * Adds a single sugar bean to the sugar crm provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param accountName
     *            the account the contact belongs to
     * @param sBean
     *            the SyncAdapter SugarBean object
     */
    private static void addRelatedModuleItem(Context context, long rawId,
            SugarBean sBean, SugarBean relatedBean, String moduleName,
            String relationModuleName, BatchOperation batchOperation) {
        // Put the data in the contacts provider
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            Log.v(LOG_TAG, "In addRelatedModuleItem");
            Log.v(LOG_TAG, " addRelatedModuleItem Rawid:" + rawId);
        }
        final SugarCRMOperations moduleItemOp = SugarCRMOperations
                .createNewRelatedModuleItem(context, moduleName,
                        relationModuleName, rawId, sBean, batchOperation);
        moduleItemOp.addRelatedSugarBean(sBean, relatedBean);

    }

    /**
     * Updates a single module item to the sugar crm content provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param resolver
     *            the ContentResolver to use
     * @param accountName
     *            the account the module item belongs to
     * @param moduleName
     *            the name of the module being synced
     * @param sBean
     *            the sugar crm sync adapter object.
     * @param rawId
     *            the unique Id for this raw module item in sugar crm content
     *            provider
     */
    private static void updateModuleItem(Context context,
            ContentResolver resolver, String moduleName, SugarBean sBean,
            long rawId, BatchOperation batchOperation) throws SugarCrmException {
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            Log.v(LOG_TAG, "In updateModuleItem");
        }
        final Uri contentUri = ContentUtils.getModuleUri(moduleName);
        final String[] projections = ContentUtils
                .getModuleProjections(moduleName);
        final Uri uri = ContentUris.withAppendedId(contentUri, rawId);
        // check the changes from server and mark them for merge in sync table
        final SyncRecord syncRecord = mDatabaseHelper.getSyncRecord(rawId,
                moduleName);
        if (syncRecord != null) {
            final ContentValues values = new ContentValues();
            values.put(SyncColumns.SYNC_STATUS, Util.SYNC_CONFLICTS);
            mDatabaseHelper.updateSyncRecord(syncRecord.mid, values);
        } else {

            final Cursor c = resolver.query(contentUri, projections,
                    mSelection, new String[] { String.valueOf(rawId) }, null);
            // TODO - do something here with cursor, create update only for
            // values that have changed
            c.close();
            final SugarCRMOperations moduleItemOp = SugarCRMOperations
                    .updateExistingModuleItem(context, moduleName, sBean,
                            rawId, batchOperation);
            moduleItemOp.updateSugarBean(sBean, uri);
        }

    }

    private static void updateRelatedModuleItem(Context context,
            String moduleName, long rawId, String relatedModuleName,
            SugarBean relatedBean, long relationRawId,
            BatchOperation batchOperation) throws SugarCrmException {
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            Log.v(LOG_TAG, "In updateRelatedModuleItem");

        }

        // modified the uri to have moduleName/#/relatedModuleName/# so the uri
        // would take care of
        // updates
        Uri contentUri = Uri.withAppendedPath(
                ContentUtils.getModuleUri(moduleName), rawId + "");
        contentUri = Uri.withAppendedPath(contentUri, relatedModuleName);
        contentUri = Uri.withAppendedPath(contentUri, relationRawId + "");

        // check the changes from server and mark them for merge in sync table
        final SyncRecord syncRecord = mDatabaseHelper.getSyncRecord(
                relationRawId, moduleName, relatedModuleName);
        if (syncRecord != null) {
            final ContentValues values = new ContentValues();
            values.put(SyncColumns.SYNC_STATUS, Util.SYNC_CONFLICTS);
            mDatabaseHelper.updateSyncRecord(syncRecord.mid, values);
        } else {
            // TODO - is this query resolver needed to query here

            // TODO - do something here with cursor
            final SugarCRMOperations moduleItemOp = SugarCRMOperations
                    .updateExistingModuleItem(context, relatedModuleName,
                            relatedBean, relationRawId, batchOperation);
            moduleItemOp.updateSugarBean(relatedBean, contentUri);
        }

    }

    /**
     * Deletes a module item from the sugar crm provider.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param rawId
     *            the unique Id for this rawId in the sugar crm provider
     */
    private static void deleteModuleItem(long rawId, String moduleName,
            BatchOperation batchOperation) {
        final Uri contentUri = ContentUtils.getModuleUri(moduleName);

        batchOperation.add(SugarCRMOperations.newDeleteCpo(
                ContentUris.withAppendedId(contentUri, rawId), true).build());
    }

    /**
     * Deletes a module item from the sugar crm provider.
     * 
     * @param rawId
     *            the unique Id for this rawId in the sugar crm provider
     */
    private static void deleteRelatedModuleItem(long rawId, long relatedRawId,
            String moduleName, String relaledModuleName,
            BatchOperation batchOperation) {
        final Uri contentUri = ContentUtils.getModuleUri(moduleName);
        final Uri parentUri = ContentUris.withAppendedId(contentUri, rawId);
        final Uri relatedUri = Uri.withAppendedPath(parentUri,
                relaledModuleName);
        final Uri deleteUri = ContentUris.withAppendedId(relatedUri,
                relatedRawId);
        batchOperation.add(SugarCRMOperations.newDeleteCpo(deleteUri, true)
                .build());
    }

    /**
     * Returns the Raw Module item id for a sugar crm SyncAdapter , or 0 if the
     * item is not found.
     * 
     * @param context
     *            the Authenticator Activity context
     * @param userId
     *            the SyncAdapter bean ID to lookup
     * @return the Raw item id, or 0 if not found
     */
    private static long lookupRawId(ContentResolver resolver,
            String moduleName, String beanId) {
        long rawId = 0;
        final Uri contentUri = ContentUtils.getModuleUri(moduleName);
        final String[] projection = new String[] { SugarCRMContent.RECORD_ID };
        final Cursor c = resolver.query(contentUri, projection, mSelection,
                new String[] { beanId }, null);
        try {
            if (c.moveToFirst()) {
                rawId = c.getLong(0);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return rawId;
    }

    /**
     * syncModules, syncs the changes to any modules that are associated with
     * the user
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param account
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @return a boolean.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static synchronized boolean syncModules(Context context,
            String account, String sessionId) throws SugarCrmException {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper(context);
        }

        List<String> userModules = ContentUtils.getUserModules(context);
        final SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        final String url = pref.getString(Util.PREF_REST_URL,
                context.getString(R.string.defaultUrl));

        try {
            if (userModules == null || userModules.size() == 0) {
                userModules = Rest.getAvailableModules(url, sessionId);
            }

            ContentUtils.setUserModules(context, userModules);
        } catch (final SugarCrmException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return false;
        }

        final Set<Module> moduleFieldsInfo = new HashSet<Module>();
        final List<String> moduleList = ContentUtils.getModuleList(context);
        for (final String moduleName : moduleList) {
            final String[] fields = {};
            try {
                final Module module = Rest.getModuleFields(url, sessionId,
                        moduleName, fields);
                moduleFieldsInfo.add(module);
                Log.i(LOG_TAG, "loaded module fields for : " + moduleName);
            } catch (final SugarCrmException sce) {
                Log.e(LOG_TAG, "failed to load module fields for : "
                        + moduleName);
            }
        }
        try {
            mDatabaseHelper.setModuleFieldsInfo(moduleFieldsInfo);
            return true;
        } catch (final SugarCrmException sce) {
            Log.e(LOG_TAG, sce.getMessage(), sce);
        }
        mDatabaseHelper.close();
        mDatabaseHelper = null;
        return false;
    }

    /**
     * syncOutgoingModuleData, should be only run after incoming changes are
     * synced and the sync status flag is set for the modules that have merge
     * conflicts
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param account
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param syncResult
     *            a {@link android.content.SyncResult} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static synchronized void syncOutgoingModuleData(Context context,
            String account, String sessionId, String moduleName,
            SyncResult syncResult) throws SugarCrmException {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper(context);
        }
        // Get outgoing items with no merge conflicts
        final Cursor cursor = mDatabaseHelper.getSyncRecordsToSync(moduleName);
        final int num = cursor.getCount();
        Log.d(LOG_TAG, "UNSYNCD Item count:" + num);
        final String selectFields[] = ContentUtils
                .getModuleProjections(moduleName);
        cursor.moveToFirst();
        for (int i = 0; i < num; i++) {
            final long syncRecordId = cursor.getLong(Sync.ID_COLUMN);
            final long syncId = cursor.getLong(Sync.SYNC_ID_COLUMN);
            final long syncRelatedId = cursor
                    .getLong(Sync.SYNC_RELATED_ID_COLUMN);
            final int command = cursor.getInt(Sync.SYNC_COMMAND_COLUMN);
            final String relatedModuleName = cursor
                    .getString(Sync.RELATED_MODULE_NAME_COLUMN);
            syncOutgoingModuleItem(context, sessionId, moduleName,
                    relatedModuleName, command, syncRecordId, syncRelatedId,
                    syncId, selectFields);
        }
        cursor.close();
        mDatabaseHelper.close();
        mDatabaseHelper = null;
    }

    /**
     * Sync outgoing module item.
     * 
     * @param context
     *            the context
     * @param sessionId
     *            the session id
     * @param moduleName
     *            the module name
     * @param relatedModuleName
     *            the related module name
     * @param command
     *            the command
     * @param syncRecordId
     *            the sync record id
     * @param syncRelatedId
     *            the sync related id
     * @param syncId
     *            the sync id
     * @param selectedFields
     *            the selected fields
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    private static void syncOutgoingModuleItem(Context context,
            String sessionId, String moduleName, String relatedModuleName,
            int command, long syncRecordId, long syncRelatedId, long syncId,
            String[] selectedFields) throws SugarCrmException {
        final SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        final String url = pref.getString(Util.PREF_REST_URL,
                context.getString(R.string.defaultUrl));
        String updatedBeanId = null;

        Uri uri = ContentUris.withAppendedId(
                ContentUtils.getModuleUri(relatedModuleName), syncId);

        final Map<String, String> moduleItemValues = new LinkedHashMap<String, String>();
        final Cursor cursor = context.getContentResolver().query(uri,
                selectedFields, null, null, null);
        String relatedModuleLinkedFieldName = null;
        // we are storing the same values for moduleName and related moduleName
        // - if there is no relationship
        if (!relatedModuleName.equals(moduleName)) {
            relatedModuleLinkedFieldName = mDatabaseHelper
                    .getLinkfieldName(relatedModuleName);
            uri = ContentUris.withAppendedId(uri, syncRelatedId);
        }
        String beanId = null;
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            Log.w(LOG_TAG, "No module data found for the module:"
                    + relatedModuleName);
            return;
        }
        switch (command) {
        case Util.INSERT:
            // discard the RECORD_ID at column index -0 and the random Sync
            // BeanId we generated
            for (int i = 2; i < selectedFields.length; i++) {
                moduleItemValues.put(selectedFields[i], cursor.getString(cursor
                        .getColumnIndex(selectedFields[i])));
            }
            // inserts with a relationship
            if (relatedModuleLinkedFieldName != null) {
                beanId = "";
                updatedBeanId = Rest.setEntry(url, sessionId,
                        relatedModuleName, moduleItemValues);
                final RelationshipStatus status = Rest.setRelationship(url,
                        sessionId, moduleName, beanId,
                        relatedModuleLinkedFieldName,
                        new String[] { updatedBeanId },
                        new LinkedHashMap<String, String>(),
                        Util.EXCLUDE_DELETED_ITEMS);
                if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                    Log.i(LOG_TAG, "created: " + status.getCreatedCount()
                            + " failed: " + status.getFailedCount()
                            + " deleted: " + status.getDeletedCount());
                }

                if (status.getCreatedCount() >= 1) {
                    final int count = mDatabaseHelper
                            .deleteSyncRecord(syncRecordId);
                    if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                        Log.i(LOG_TAG, "Relationship is also set!");
                        Log.v(LOG_TAG, "Sync--insert bean on server successful");
                        Log.v(LOG_TAG, "Sync--record deleted:"
                                + (count > 0 ? true : false));
                    }
                } else {
                    if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                        Log.i(LOG_TAG, "setRelationship failed!");
                    }
                }
            } else {
                // insert case for an orphan module add without any
                // relationship, theupdatedBeanId is actually a new beanId
                // returned by server
                updatedBeanId = Rest.setEntry(url, sessionId,
                        relatedModuleName, moduleItemValues);
                if (updatedBeanId != null) {
                    final int count = mDatabaseHelper
                            .deleteSyncRecord(syncRecordId);
                    if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                        Log.v(LOG_TAG, "Sync--insert bean on server successful");
                        Log.v(LOG_TAG, "Sync--record deleted:"
                                + (count > 0 ? true : false));
                    }

                } else {
                    // TODO - we keep the item with last sync_failure date -
                    // status
                }

            }
            break;

        // make the same calls for update and delete as delete only changes the
        // DELETED flag to 1
        case Util.UPDATE:

            if (relatedModuleLinkedFieldName != null) {
                final String rowId = syncId + "";
                final String parentBeanId = mDatabaseHelper.lookupBeanId(
                        moduleName, rowId);

                // related BeanId

                beanId = cursor.getString(cursor
                        .getColumnIndex(SugarCRMContent.SUGAR_BEAN_ID));
                moduleItemValues.put(SugarCRMContent.SUGAR_BEAN_ID, beanId);

                final String serverUpdatedBeanId = Rest.setEntry(url,
                        sessionId, relatedModuleName, moduleItemValues);
                if (serverUpdatedBeanId.equals(beanId)) {
                    final RelationshipStatus status = Rest.setRelationship(url,
                            sessionId, moduleName, parentBeanId,
                            relatedModuleLinkedFieldName,
                            new String[] { beanId },
                            new LinkedHashMap<String, String>(),
                            Util.EXCLUDE_DELETED_ITEMS);
                    if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                        Log.i(LOG_TAG, "created: " + status.getCreatedCount()
                                + " failed: " + status.getFailedCount()
                                + " deleted: " + status.getDeletedCount());
                    }

                    if (status.getCreatedCount() >= 1) {
                        final int count = mDatabaseHelper
                                .deleteSyncRecord(syncRecordId);
                        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                            Log.i(LOG_TAG, "Relationship is also set!");
                            Log.v(LOG_TAG, "sync --updated server successful");
                            Log.v(LOG_TAG, "Sync--record deleted:"
                                    + (count > 0 ? true : false));
                        }

                    } else {
                        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                            Log.i(LOG_TAG, "setRelationship failed!");
                        }
                        // TODO - we keep the item with last sync_failure date -
                        // status
                    }
                } else {
                    // a new bean was created instead of sending back the same
                    // updated bean
                    // TODO - we keep the item with last sync_failure date -
                    // status
                }
            } else {
                beanId = cursor.getString(cursor
                        .getColumnIndex(SugarCRMContent.SUGAR_BEAN_ID));
                moduleItemValues.put(SugarCRMContent.SUGAR_BEAN_ID, beanId);
                updatedBeanId = Rest.setEntry(url, sessionId,
                        relatedModuleName, moduleItemValues);
                if (beanId.equals(updatedBeanId)) {

                    final int count = mDatabaseHelper
                            .deleteSyncRecord(syncRecordId);
                    if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                        Log.v(LOG_TAG, "sync --updated server successful");
                        Log.v(LOG_TAG, "Sync--record deleted:"
                                + (count > 0 ? true : false));
                    }
                } else {
                    // TODO - we keep the item with last sync_failure date -
                    // status
                }
            }
            break;

        case Util.DELETE:
            beanId = cursor.getString(cursor
                    .getColumnIndex(SugarCRMContent.SUGAR_BEAN_ID));
            moduleItemValues.put(SugarCRMContent.SUGAR_BEAN_ID, beanId);
            moduleItemValues.put(ModuleFields.DELETED, Util.DELETED_ITEM);

            if (relatedModuleLinkedFieldName != null) {
                final String rowId = syncId + "";
                final String parentBeanId = mDatabaseHelper.lookupBeanId(
                        moduleName, rowId);
                // related BeanId
                moduleItemValues.put(SugarCRMContent.SUGAR_BEAN_ID, beanId);
                final String serverUpdatedBeanId = Rest.setEntry(url,
                        sessionId, relatedModuleName, moduleItemValues);
                if (serverUpdatedBeanId.equals(beanId)) {
                    final RelationshipStatus status = Rest.setRelationship(url,
                            sessionId, moduleName, parentBeanId,
                            relatedModuleLinkedFieldName,
                            new String[] { beanId },
                            new LinkedHashMap<String, String>(),
                            Util.EXCLUDE_DELETED_ITEMS);
                    if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                        Log.i(LOG_TAG, "created: " + status.getCreatedCount()
                                + " failed: " + status.getFailedCount()
                                + " deleted: " + status.getDeletedCount());
                    }

                    if (status.getCreatedCount() >= 1) {
                        final int count = mDatabaseHelper
                                .deleteSyncRecord(syncRecordId);
                        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                            Log.i(LOG_TAG, "Relationship is also set!");
                            Log.v(LOG_TAG, "sync --updated server successful");
                            Log.v(LOG_TAG, "Sync--record deleted:"
                                    + (count > 0 ? true : false));
                        }

                    } else {
                        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                            Log.i(LOG_TAG, "setRelationship failed!");
                        }
                    }
                } else {
                    // a new bean was created instead of sending back the same
                    // updated bean
                }
            } else {
                beanId = cursor.getString(cursor
                        .getColumnIndex(SugarCRMContent.SUGAR_BEAN_ID));
                moduleItemValues.put(SugarCRMContent.SUGAR_BEAN_ID, beanId);
                updatedBeanId = Rest.setEntry(url, sessionId,
                        relatedModuleName, moduleItemValues);
                if (beanId.equals(updatedBeanId)) {

                    final int count = mDatabaseHelper
                            .deleteSyncRecord(syncRecordId);
                    if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                        Log.v(LOG_TAG, "sync --updated server successful");
                        Log.v(LOG_TAG, "Sync--record deleted:"
                                + (count > 0 ? true : false));
                    }
                } else {
                    // TODO - we keep the item with last sync_failure date -
                    // status
                }
            }
            break;
        }
        cursor.close();

    }

    /**
     * syncAclAccess
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param account
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static synchronized boolean syncAclAccess(Context context,
            String account, String sessionId) {
        try {
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "Sync Acl Access");
            }
            final SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(context);
            // TODO use a constant and remove this as we start from the login
            // screen
            final String url = pref.getString(Util.PREF_REST_URL,
                    context.getString(R.string.defaultUrl));
            final HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();

            final String[] userSelectFields = { ModuleFields.ID };
            if (mDatabaseHelper == null) {
                mDatabaseHelper = new DatabaseHelper(context);
            }
            final String moduleName = Util.USERS;
            final String aclLinkNameField = mDatabaseHelper
                    .getLinkfieldName(Util.ACLROLES);
            linkNameToFieldsArray.put(aclLinkNameField,
                    Arrays.asList(ACLRoles.INSERT_PROJECTION));

            final String actionsLinkNameField = mDatabaseHelper
                    .getLinkfieldName(Util.ACLACTIONS);
            final HashMap<String, List<String>> linkNameToFieldsArrayForActions = new HashMap<String, List<String>>();
            linkNameToFieldsArrayForActions.put(actionsLinkNameField,
                    Arrays.asList(ACLActions.INSERT_PROJECTION));

            // this gives the user bean for the logged in user along with the
            // acl roles associated
            final SugarBean[] userBeans = Rest.getEntryList(url, sessionId,
                    moduleName, "Users.user_name='" + account + "'", "", "",
                    userSelectFields, linkNameToFieldsArray, "", "");
            // UserBeans always contains only one bean as we use getEntryList
            // with the logged in user name as the query parameter
            for (final SugarBean userBean : userBeans) {
                // Get the acl roles
                final SugarBean[] roleBeans = userBean
                        .getRelationshipBeans(aclLinkNameField);
                // Get the beanIds of the roles that are inserted
                if (roleBeans != null) {
                    List<String> roleIds = new ArrayList<String>();

                    roleIds = mDatabaseHelper.insertRoles(roleBeans);

                    // Get the acl actions for each roleId
                    for (final String roleId : roleIds) {
                        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                            Log.d(LOG_TAG, "roleId - " + roleId);
                        }

                        // Get the aclRole along with the acl actions associated
                        final SugarBean roleBean = Rest.getEntry(url,
                                sessionId, Util.ACLROLES, roleId,
                                ACLRoles.INSERT_PROJECTION,
                                linkNameToFieldsArrayForActions);
                        final SugarBean[] roleRelationBeans = roleBean
                                .getRelationshipBeans(actionsLinkNameField);
                        if (roleRelationBeans != null) {
                            mDatabaseHelper.insertActions(roleId,
                                    roleRelationBeans);
                        }
                    }
                }
            }
            return true;
        } catch (final SugarCrmException sce) {
            Log.e(LOG_TAG, "" + sce.getMessage(), sce);
        }
        return false;
    }

    /**
     * syncUsersList
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static synchronized boolean syncUsersList(Context context,
            String sessionId) {
        try {
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "Sync Acl Access");
            }
            final SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(context);
            // TODO use a constant and remove this as we start from the login
            // screen
            final String url = pref.getString(Util.PREF_REST_URL,
                    context.getString(R.string.defaultUrl));

            final HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();
            final SugarBean[] userBeans = Rest.getEntryList(url, sessionId,
                    Util.USERS, null, null, "0", Users.INSERT_PROJECTION,
                    linkNameToFieldsArray, null, "0");

            final Map<String, Map<String, String>> usersMap = new TreeMap<String, Map<String, String>>();
            for (final SugarBean userBean : userBeans) {
                final Map<String, String> userBeanValues = getUserBeanValues(userBean);
                final String userName = userBean
                        .getFieldValue(ModuleFields.USER_NAME);
                if (userBeanValues != null && userBeanValues.size() > 0) {
                    usersMap.put(userName, userBeanValues);
                }
            }

            if (mDatabaseHelper == null) {
                mDatabaseHelper = new DatabaseHelper(context);
            }
            mDatabaseHelper.insertUsers(usersMap);

            return true;
        } catch (final SugarCrmException sce) {
            Log.e(LOG_TAG, "" + sce.getMessage(), sce);
        }
        return false;
    }

    /**
     * Gets the user bean values.
     * 
     * @param userBean
     *            the user bean
     * @return the user bean values
     */
    private static Map<String, String> getUserBeanValues(SugarBean userBean) {
        final Map<String, String> userBeanValues = new TreeMap<String, String>();
        for (final String fieldName : Users.INSERT_PROJECTION) {
            final String fieldValue = userBean.getFieldValue(fieldName);
            userBeanValues.put(fieldName, fieldValue);
        }
        if (userBeanValues.size() > 0) {
            return userBeanValues;
        }
        return null;
    }
}
