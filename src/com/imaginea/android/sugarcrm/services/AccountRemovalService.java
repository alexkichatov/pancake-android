/*******************************************************************************
 * Copyright (c) 2013 Asha, Muralidaran.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *          Asha, Muralidaran - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : AccountRemovalService 
 * Description : 
                This is service class to remove the Account
 ******************************************************************************/

package com.imaginea.android.sugarcrm.services;

import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * The Class AccountRemovalService.
 */
public class AccountRemovalService extends IntentService {

    // Action used for BroadcastReceiver entry point
    /** The Constant ACTION_BROADCAST. */
    private static final String ACTION_BROADCAST = "broadcast_receiver";

    /**
     * Instantiates a new account removal service.
     */
    public AccountRemovalService() {
        // Class name will be the thread name.
        super(AccountRemovalService.class.getName());

        // Intent should be re delivered if the process gets killed before
        // completing the job.
        setIntentRedelivery(true);
    }

    /**
     * Clear the db.
     */
    private void clearTheDb() {

        final ContentResolver contentResolver = getContentResolver();

        final Uri accountsUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.ACCOUNTS);
        contentResolver.delete(accountsUrl, null, null);

        final Uri accountsCasesUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.ACCOUNTS_CASES);
        contentResolver.delete(accountsCasesUrl, null, null);

        final Uri accountsContUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.ACCOUNTS_CONTACTS);
        contentResolver.delete(accountsContUrl, null, null);

        final Uri accountsOpporUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/"
                + Util.ACCOUNTS_OPPORTUNITIES);
        contentResolver.delete(accountsOpporUrl, null, null);

        final Uri aclActionsUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.ACL_ACTIONS);
        contentResolver.delete(aclActionsUrl, null, null);

        final Uri aclRolesUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.ACL_ROLES);
        contentResolver.delete(aclRolesUrl, null, null);

        final Uri alarmUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.ALARMS);
        contentResolver.delete(alarmUrl, null, null);

        final Uri callsUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.CALLS);
        contentResolver.delete(callsUrl, null, null);

        final Uri campaignsUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.CAMPAIGNS);
        contentResolver.delete(campaignsUrl, null, null);

        final Uri casesUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.CASES);
        contentResolver.delete(casesUrl, null, null);

        final Uri contactsUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.CONTACTS);
        contentResolver.delete(contactsUrl, null, null);

        final Uri contactsCasesUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.CONTACTS_CASES);
        contentResolver.delete(contactsCasesUrl, null, null);

        final Uri contactsOpporUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/"
                + Util.CONTACTS_OPPORTUNITIES);
        contentResolver.delete(contactsOpporUrl, null, null);

        final Uri leadsUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.LEADS);
        contentResolver.delete(leadsUrl, null, null);

        final Uri linkFieldsUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.LINK_FIELDS);
        contentResolver.delete(linkFieldsUrl, null, null);

        final Uri meetingsUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.MEETINGS);
        contentResolver.delete(meetingsUrl, null, null);

        final Uri modulesUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.MODULES);
        contentResolver.delete(modulesUrl, null, null);

        final Uri moduleFieldsUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.MODULES_FIELDS);
        contentResolver.delete(moduleFieldsUrl, null, null);

        final Uri opportunitiesUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.OPPORTUNITIES);
        contentResolver.delete(opportunitiesUrl, null, null);

        final Uri recentUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.RECENT);
        contentResolver.delete(recentUrl, null, null);

        final Uri syncUrl = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                + "/" + Util.SYNC_TABLE);
        contentResolver.delete(syncUrl, null, null);

        final Uri uesersUrl = Uri.parse("content://"
                + SugarCRMProvider.AUTHORITY + "/" + Util.USERS);
        contentResolver.delete(uesersUrl, null, null);

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        if (ACTION_BROADCAST.equals(action)) {
            final Intent broadcastIntent = intent
                    .getParcelableExtra(Intent.EXTRA_INTENT);
            final String broadcastAction = broadcastIntent.getAction();
            if (AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION
                    .equals(broadcastAction)) {
                final android.accounts.Account[] sugarCRMAccounts = AccountManager
                        .get(this).getAccountsByType(
                                "com.imaginea.android.sugarcrm");

                if ((sugarCRMAccounts != null)
                        && (sugarCRMAccounts.length == 0)) {
                    clearTheDb();
                    final SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    prefs.edit().clear().commit();
                }
            }

        }

    }

    /**
     * Process broadcast intent.
     * 
     * @param context
     *            the context
     * @param intent
     *            the intent
     */
    public static void processBroadcastIntent(Context context, Intent intent) {
        final Intent i = new Intent(context, AccountRemovalService.class);
        i.setAction(ACTION_BROADCAST);
        i.putExtra(Intent.EXTRA_INTENT, intent);
        context.startService(i);

    }

}
