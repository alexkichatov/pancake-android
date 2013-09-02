/*******************************************************************************
 * Copyright (c) 2013 Asha, Muralidaran.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Asha, Muralidaran - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : SugarCrmApp 
 ******************************************************************************/

package com.imaginea.android.sugarcrm;

import java.util.HashMap;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * The Class SugarCrmApp.
 */
public class SugarCrmApp extends Application {

    // easy ref to App instance for classes which do not have access to
    // Activity/Service context
    /** The app. */
    public static Application app = null;

    /*
     * sessionId is obtained after successful login into the Sugar CRM instance
     * Now, sessionId will be available to the entire application Access the
     * sessionId from any part of the application as follows : SugarCrmApp app =
     * ((SugarCrmApp) getApplication()); app.getSessionId();
     */
    /** The m session id. */
    private String mSessionId;

    /** The m last login time. */
    private long mLastLoginTime = 0;

    /** The module sort order. */
    private final Map<String, Map<String, String>> moduleSortOrder = new HashMap<String, Map<String, String>>();

    /** The Constant LOG_TAG. */
    private static final String LOG_TAG = SugarCrmApp.class.getSimpleName();

    /**
     * Gets the session id.
     * 
     * @return the session id
     */
    public String getSessionId() {
        final long currentTime = SystemClock.currentThreadTimeMillis();

        if (Util.isNetworkOn(this)
                && (mSessionId == null || currentTime - mLastLoginTime > 30000)) {
            final String userName = SugarCrmSettings.getUsername(this);
            final SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(this);
            final String url = pref.getString(Util.PREF_REST_URL,
                    this.getString(R.string.defaultUrl));
            final Account account = getAccount(userName);
            if (account != null) {
                final String password = AccountManager.get(this).getPassword(
                        account);
                mLastLoginTime = currentTime;
                try {
                    mSessionId = Rest.loginToSugarCRM(url, account.name,
                            password);
                } catch (final SugarCrmException se) {
                    Log.e(LOG_TAG, se.getMessage(), se);
                } catch (final Exception ex) {
                    Log.e(LOG_TAG, ex.getMessage(), ex);
                }
            }
        }
        return mSessionId;
    }

    /**
     * returns the Account associated with the current user name.
     * 
     * @param userName
     *            a {@link java.lang.String} object.
     * @return a {@link android.accounts.Account} object.
     */
    public Account getAccount(String userName) {

        final AccountManager accountManager = AccountManager.get(this);
        final Account[] accounts = accountManager
                .getAccountsByType(Util.ACCOUNT_TYPE);
        Account userAccount = null;
        for (final Account account : accounts) {
            // never print the password
            // Log.i(LOG_TAG, "user name is " + account.name);
            if (account.name.equals(userName)) {
                userAccount = account;
                break;
            }
        }
        return userAccount;
    }

    /**
     * Sets the session id.
     * 
     * @param mSessionId
     *            the new session id
     */
    public void setSessionId(String mSessionId) {
        this.mSessionId = mSessionId;
    }

    /**
     * 
     * Setter for the field <code>moduleSortOrder</code>.
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param fieldName
     *            a {@link java.lang.String} object.
     * @param sortBy
     *            a {@link java.lang.String} object.
     */
    public void setModuleSortOrder(String moduleName, String fieldName,
            String sortBy) {
        final Map<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put(fieldName, sortBy);
        moduleSortOrder.put(moduleName, fieldMap);
    }

    /**
     * 
     * Getter for the field <code>moduleSortOrder</code>.
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getModuleSortOrder(String moduleName) {
        return moduleSortOrder.get(moduleName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        setDefaultModuleSortOrders();
    }

    /**
     * Sets the default module sort orders.
     */
    private void setDefaultModuleSortOrders() {
        Map<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.NAME, Util.ASC);
        moduleSortOrder.put(Util.ACCOUNTS, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.FIRST_NAME, Util.ASC);
        moduleSortOrder.put(Util.CONTACTS, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.FIRST_NAME, Util.ASC);
        moduleSortOrder.put(Util.LEADS, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.NAME, Util.ASC);
        moduleSortOrder.put(Util.OPPORTUNITIES, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.NAME, Util.ASC);
        moduleSortOrder.put(Util.CASES, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.NAME, Util.ASC);
        moduleSortOrder.put(Util.CALLS, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.NAME, Util.ASC);
        moduleSortOrder.put(Util.MEETINGS, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.NAME, Util.ASC);
        moduleSortOrder.put(Util.CAMPAIGNS, fieldMap);

        fieldMap = new HashMap<String, String>();
        fieldMap.put(ModuleFields.NAME_1, Util.ASC);
        moduleSortOrder.put(Util.RECENT, fieldMap);

    }

}
