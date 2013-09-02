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
 ******************************************************************************/

package com.imaginea.android.sugarcrm.sync;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.ParseException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModulesActivity;
import com.imaginea.android.sugarcrm.R;
import com.imaginea.android.sugarcrm.SugarCrmApp;
import com.imaginea.android.sugarcrm.WizardAuthActivity;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * SyncAdapter implementation for syncing sugarcrm modules on the server to
 * sugar crm provider and vice versa.
 * 
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    /** The m account manager. */
    private final AccountManager mAccountManager;

    /** The m context. */
    private final Context mContext;

    /** The m noti id. */
    private int mNotiId = -1;

    /** The Constant LOG_TAG. */
    private static final String LOG_TAG = "SyncAdapter";

    /**
     * <p>
     * Constructor for SyncAdapter.
     * </p>
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param autoInitialize
     *            a boolean.
     */
    public SyncAdapter(final Context context, final boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mAccountManager = AccountManager.get(context);
    }

    /** {@inheritDoc} */
    @Override
    public void onPerformSync(final Account account, final Bundle extras,
            final String authority, final ContentProviderClient provider,
            final SyncResult syncResult) {
        Log.i(LOG_TAG, "onPerformSync");
        final int syncType = extras.getInt(Util.SYNC_TYPE);

        try {

            /*
             * if we are a password based system, the SugarCRM OAuth setup is
             * not clear yet but based on preferences, we ahould select the
             * right one -?
             */
            final String userName = account.name;
            final String password = mAccountManager.getPassword(account);
            if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                Log.v(LOG_TAG, "Sync Type name: " + syncType);
                Log.v(LOG_TAG, "user name: " + userName + " and authority: "
                        + authority);
            }

            if (!Util.isNetworkOn(mContext)) {
                Log.v(LOG_TAG, "Network is not on..skipping sync:");
                syncResult.stats.numIoExceptions++;
                return;
            }
            final SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(mContext);
            final String url = pref.getString(Util.PREF_REST_URL,
                    mContext.getString(R.string.defaultUrl));
            final SugarCrmApp app = ((SugarCrmApp) SugarCrmApp.app);
            String sessionId = app != null ? app.getSessionId() : null;
            if (sessionId == null || Rest.seamlessLogin(url, sessionId) == 0) {
                sessionId = Rest.loginToSugarCRM(url, account.name, password);
                app.setSessionId(sessionId);
            }

            // TODO run this list through our local DB and see if any changes
            // have happened and sync
            // those modules and module fields
            if (!(syncType == Util.SYNC_MODULE_META_DATA || syncType == Util.SYNC_ALL_META_DATA)) {
                if (mNotiId != -1) {
                    Util.notificationCancel(mContext, mNotiId);
                }

                mNotiId = Util.notify(mContext, mContext
                        .getApplicationContext().getPackageName(),
                        ModulesActivity.class, R.string.appName,
                        R.string.appName, mContext.getString(R.string.syncing));
            }
            switch (syncType) {

            case Util.SYNC_MODULE_META_DATA:
                // use this only for testing
                // SugarSyncManager.syncModules(mContext, account.name,
                // sessionId);
                break;
            case Util.SYNC_ALL_META_DATA:
                // should be used once for one time set-up
                final boolean modulesSyncd = SugarSyncManager.syncModules(
                        mContext, account.name, sessionId);

                final boolean usersSyncd = SugarSyncManager.syncUsersList(
                        mContext, sessionId);

                // TODO - Need to resolve SYNC issue.
                WizardAuthActivity.resultWait.release();
                if (modulesSyncd && usersSyncd) {
                    final Editor editor = pref.edit();
                    editor.putBoolean(Util.SYNC_METADATA_COMPLETED, true);
                    editor.commit();
                }
                break;
            case Util.SYNC_ACL_ACCESS_META_DATA:
                // use this only for testing
                // SugarSyncManager.syncAclAccess(mContext, account.name,
                // sessionId);
                break;

            case Util.SYNC_MODULES_DATA:
                // default mode - sync all modules - from the sync screen
                syncAllModulesData(account, extras, authority, sessionId,
                        syncResult);
                break;
            case Util.SYNC_MODULE_DATA:
                // sync only one module - can be used once module based sync is
                // provided
                final String moduleName = extras
                        .getString(RestConstants.MODULE_NAME);
                syncModuleData(account, extras, authority, sessionId,
                        moduleName, syncResult);
                break;
            case Util.SYNC_ALL:
                // testing
                SugarSyncManager.syncModules(mContext, account.name, sessionId);
                // SugarSyncManager.syncAclAccess(mContext, account.name,
                // sessionId);
                syncAllModulesData(account, extras, authority, sessionId,
                        syncResult);
                break;
            default:
                // if called from accounts and sync screen, we sync only module
                // data
                syncAllModulesData(account, extras, authority, sessionId,
                        syncResult);
                break;
            }

        } catch (final ParseException e) {
            syncResult.stats.numParseExceptions++;
            Log.e(LOG_TAG, "ParseException", e);
        } catch (final SugarCrmException se) {
            Log.e(LOG_TAG, se.getMessage(), se);
        } catch (final Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
        }
    }

    /**
     * syncModulesData, syncs all modules.
     * 
     * @param account
     *            the account
     * @param extras
     *            the extras
     * @param authority
     *            the authority
     * @param sessionId
     *            the session id
     * @param syncResult
     *            the sync result
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    private void syncAllModulesData(final Account account, final Bundle extras,
            final String authority, final String sessionId,
            final SyncResult syncResult) throws SugarCrmException {

        final List<String> moduleList = ContentUtils.getModuleList(mContext);

        if (moduleList.size() == 0) {
            Log.w(LOG_TAG, "No modules to sync");
        }
        // TODO - dynamically determine the relationships and get the values
        Collections.sort(moduleList);
        for (final String moduleName : moduleList) {
            syncModuleData(account, extras, authority, sessionId, moduleName,
                    syncResult);
        }

        new Date();
        // do not use sync result status to notify, notify module specific
        // comprehensive stats
        mContext.getApplicationContext();
        final String msg = mContext.getString(R.string.syncMessage);
        if (mNotiId != -1) {
            Util.notificationCancel(mContext, mNotiId);
        }

        mNotiId = Util.notify(mContext, mContext.getApplicationContext()
                .getPackageName(), ModulesActivity.class, R.string.syncSuccess,
                R.string.syncSuccess, String.format(msg, moduleList.size()));
    }

    /**
     * syncModuleData.
     * 
     * @param account
     *            the account
     * @param extras
     *            the extras
     * @param authority
     *            the authority
     * @param sessionId
     *            the session id
     * @param moduleName
     *            the module name
     * @param syncResult
     *            the sync result
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    private void syncModuleData(final Account account, final Bundle extras,
            final String authority, final String sessionId,
            final String moduleName, final SyncResult syncResult)
            throws SugarCrmException {
        Log.i(LOG_TAG, "Syncing Incoming Module Data:" + moduleName);

        final int sincType = extras.getInt(Util.SYNC_TYPE);

        // TODO - should be catch SugarCRMException and allow processing other
        // modules and
        // fail completely
        SugarSyncManager.syncModulesData(mContext, account.name, sincType,
                sessionId, moduleName, syncResult);

        /*
         * at this point we are done with identifying the merge conflicts in the
         * sync table for incoming module data; the remaining un-synced items in
         * the sync table for that module can be published to the server now.
         */
        Log.i(LOG_TAG, "Syncing Outgoing Module Data:" + moduleName);
        SugarSyncManager.syncOutgoingModuleData(mContext, account.name,
                sessionId, moduleName, syncResult);

        if (Util.SYNC_MODULE_DATA == sincType) {
            if (mNotiId != -1) {
                Util.notificationCancel(mContext, mNotiId);
            }

            mNotiId = Util.notify(mContext, mContext.getApplicationContext()
                    .getPackageName(), ModulesActivity.class,
                    R.string.syncSuccess, R.string.syncSuccess, moduleName
                            + " " + mContext.getString(R.string.module));
        }

    }

    /** {@inheritDoc} */
    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
        // TODO - notify is part if sync framework, with the SyncResults giving
        // details about the
        // last sync, we perform additional steps that are specific to our app
        // if required
    }

}
