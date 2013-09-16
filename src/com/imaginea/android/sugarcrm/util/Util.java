/*******************************************************************************
 * Copyright (c) 2013 Vasavi, chander.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Vasavi, chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : Util 
 * Description :
 *           Utility class for commons views required by activities
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.imaginea.android.sugarcrm.SugarCrmApp;
import com.imaginea.android.sugarcrm.SugarCrmSettings;
import com.imaginea.android.sugarcrm.WizardAuthActivity;
import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.ui.BaseMultiPaneActivity;

/**
 * The Class Util.
 */
public final class Util {

    /** The m request id. */
    private static int mRequestId = 0;

    /** Account type string. */

    public static final String ACCOUNT_TYPE = "com.imaginea.android.sugarcrm";

    /** Authtoken type string. */
    public static final String AUTHTOKEN_TYPE = "com.imaginea.android.sugarcrm";

    /** The Constant FETCH_FAILED. */
    public static final int FETCH_FAILED = 0;

    /** The Constant REFRESH_LIST. */
    public static final int REFRESH_LIST = 1;

    /** The Constant FETCH_SUCCESS. */
    public static final int FETCH_SUCCESS = 2;

    /** The Constant OFFLINE_MODE. */
    public static final int OFFLINE_MODE = 0;

    /** The Constant URL_NOT_AVAILABLE. */
    public static final int URL_NOT_AVAILABLE = 1;

    /** The Constant URL_AVAILABLE. */
    public static final int URL_AVAILABLE = 2;

    /** The Constant URL_USER_AVAILABLE. */
    public static final int URL_USER_AVAILABLE = 3;

    /** The Constant URL_USER_PWD_AVAILABLE. */
    public static final int URL_USER_PWD_AVAILABLE = 4;

    /** The Constant EDIT_ORPHAN_MODE. */
    public static final int EDIT_ORPHAN_MODE = 0;

    /** The Constant EDIT_RELATIONSHIP_MODE. */
    public static final int EDIT_RELATIONSHIP_MODE = 1;

    /** The Constant NEW_ORPHAN_MODE. */
    public static final int NEW_ORPHAN_MODE = 2;

    /** The Constant NEW_RELATIONSHIP_MODE. */
    public static final int NEW_RELATIONSHIP_MODE = 3;

    /** The Constant ASSIGNED_ITEMS_MODE. */
    public static final int ASSIGNED_ITEMS_MODE = 4;

    /** The Constant LIST_MODE. */
    public static final int LIST_MODE = 5;

    /** The Constant CONTACT_IMPORT_FLAG. */
    public static final int CONTACT_IMPORT_FLAG = 1;

    /** The Constant PREF_REST_URL. */
    public static final String PREF_REST_URL = "restUrl";

    /** The Constant PREF_USERNAME. */
    public static final String PREF_USERNAME = "usr";

    /** The Constant PREF_FETCH_RECORDS_SIZE. */
    public static final String PREF_FETCH_RECORDS_SIZE = "records_size";

    /** The Constant PREF_PASSWORD. */
    public static final String PREF_PASSWORD = "pwd";

    /** The Constant PREF_STATUS. */
    public static final String PREF_STATUS = "prefStatus";

    /** The Constant PREF_ALARM_STATE. */
    public static final String PREF_ALARM_STATE = "alarm";

    /** The Constant PREF_REMEMBER_PASSWORD. */
    public static final String PREF_REMEMBER_PASSWORD = "rememberPwd";

    /** The Constant PROJECTION. */
    public static final String PROJECTION = "select";

    /** The Constant SORT_ORDER. */
    public static final String SORT_ORDER = "orderby";

    /** The Constant COMMAND. */
    public static final String COMMAND = "cmd";

    /** The Constant ROW_ID. */
    public static final String ROW_ID = "row_id";

    /** The Constant EXCLUDE_DELETED_ITEMS. */
    public static final String EXCLUDE_DELETED_ITEMS = "0";

    /** The Constant INCLUDE_DELETED_ITEMS. */
    public static final String INCLUDE_DELETED_ITEMS = "1";

    /** The Constant DELETED_ITEM. */
    public static final String DELETED_ITEM = "1";

    /** The Constant NEW_ITEM. */
    public static final String NEW_ITEM = "0";

    /** The Constant CASE_ID. */
    public static final String CASE_ID = "case_id";

    // module names
    /** The Constant ACCOUNTS. */
    public static final String ACCOUNTS = "Accounts";

    /** The Constant ACCOUNTS_CASES. */
    public static final String ACCOUNTS_CASES = "Accounts_cases";

    /** The Constant ACCOUNTS_CONTACTS. */
    public static final String ACCOUNTS_CONTACTS = "Accounts_contacts";

    /** The Constant ACCOUNTS_OPPORTUNITIES. */
    public static final String ACCOUNTS_OPPORTUNITIES = "Accounts_opportunities";

    /** The Constant ACL_ACTIONS. */
    public static final String ACL_ACTIONS = "Acl_actions";

    /** The Constant ACL_ROLES. */
    public static final String ACL_ROLES = "Acl_roles";

    /** The Constant ALARMS. */
    public static final String ALARMS = "Alarms";

    /** The Constant CONTACTS. */
    public static final String CONTACTS = "Contacts";

    /** The Constant CONTACTS_CASES. */
    public static final String CONTACTS_CASES = "Contacts_cases";

    /** The Constant CONTACTS_OPPORTUNITIES. */
    public static final String CONTACTS_OPPORTUNITIES = "Contacts_opportunities";

    /** The Constant LEADS. */
    public static final String LEADS = "Leads";

    /** The Constant LINK_FIELDS. */
    public static final String LINK_FIELDS = "Link_fields";

    /** The Constant OPPORTUNITIES. */
    public static final String OPPORTUNITIES = "Opportunities";

    /** The Constant MODULES. */
    public static final String MODULES = "Modules";

    /** The Constant MODULES_FIELDS. */
    public static final String MODULES_FIELDS = "Module_fields";

    /** The Constant MEETINGS. */
    public static final String MEETINGS = "Meetings";

    /** The Constant CALLS. */
    public static final String CALLS = "Calls";

    /** The Constant CASES. */
    public static final String CASES = "Cases";

    /** The Constant CAMPAIGNS. */
    public static final String CAMPAIGNS = "Campaigns";

    /** The Constant USERS. */
    public static final String USERS = "Users";

    /** The Constant ACLROLES. */
    public static final String ACLROLES = "ACLRoles";

    /** The Constant ACLACTIONS. */
    public static final String ACLACTIONS = "ACLActions";

    /** The Constant STATUS. */
    public static final String STATUS = "Status";

    /** The Constant RECENT. */
    public static final String RECENT = "Recent";

    /** The Constant SYNC_TABLE. */
    public static final String SYNC_TABLE = "Sync_table";

    /** The Constant IMPORT_FLAG. */
    public static final String IMPORT_FLAG = "importFlag";

    // sql sort order contacts
    /** The Constant ASC. */
    public static final String ASC = "ASC";

    /** The Constant DESC. */
    public static final String DESC = "DESC";

    // CRUD constants
    /** The Constant GET. */
    public static final int GET = 0;

    /** The Constant INSERT. */
    public static final int INSERT = 1;

    /** The Constant UPDATE. */
    public static final int UPDATE = 2;

    /** The Constant DELETE. */
    public static final int DELETE = 3;

    // sync status
    /** The Constant UNSYNCED. */
    public static final int UNSYNCED = 0;

    /** The Constant SYNC_CONFLICTS. */
    public static final int SYNC_CONFLICTS = 1;

    // sync constants
    // RECORD_ID (_id) of the module that needs to be synced
    /** The Constant SYNC_ID. */
    public static final String SYNC_ID = "sync_id";

    /** The Constant SYNC_RELATED_ID. */
    public static final String SYNC_RELATED_ID = "sync_related_id";

    // The sync command - INSERT, DELETE, UPDATE
    /** The Constant SYNC_COMMAND. */
    public static final String SYNC_COMMAND = "sync_cmd";

    /** The Constant RELATED_MODULE. */
    public static final String RELATED_MODULE = "related_module";

    // Sync operations
    /** The Constant SYNC_TYPE. */
    public static final String SYNC_TYPE = "sync_type";

    /** The Constant SYNC_METADATA_COMPLETED. */
    public static final String SYNC_METADATA_COMPLETED = "metadata";

    /** The Constant SYNC_MODULE_META_DATA. */
    public static final int SYNC_MODULE_META_DATA = 0;

    /** The Constant SYNC_ACL_ACCESS_META_DATA. */
    public static final int SYNC_ACL_ACCESS_META_DATA = 1;

    /** The Constant SYNC_MODULES_DATA. */
    public static final int SYNC_MODULES_DATA = 2;

    /** The Constant SYNC_MODULE_DATA. */
    public static final int SYNC_MODULE_DATA = 3;

    /** The Constant SYNC_ALL_META_DATA. */
    public static final int SYNC_ALL_META_DATA = 4;

    /** The Constant SYNC_ALL. */
    public static final int SYNC_ALL = 5;

    /** The Constant PREF_SYNC_START_TIME. */
    public static final String PREF_SYNC_START_TIME = "syncStart";

    /** The Constant PREF_SYNC_END_TIME. */
    public static final String PREF_SYNC_END_TIME = "syncEnd";

    // sub Activity request codes
    /** The Constant LOGIN_REQUEST_CODE. */
    public static final int LOGIN_REQUEST_CODE = 0;

    /** The Constant SYNC_DATA_REQUEST_CODE. */
    public static final int SYNC_DATA_REQUEST_CODE = 1;

    /** The Constant IMPORT_CONTACTS_REQUEST_CODE. */
    public static final int IMPORT_CONTACTS_REQUEST_CODE = 1;

    /** The Constant SQL_FILE. */
    public static final String SQL_FILE = "sortOrderAndGroup.sql";

    /** The Constant LOG_TAG. */
    private static final String LOG_TAG = Util.class.getSimpleName();

    /** The Constant PREF_SORT_MODULE. */
    public static final String PREF_SORT_MODULE = "module";

    /** The Constant PREF_SORT_MODULE_FILED. */
    public static final String PREF_SORT_MODULE_FILED = "module_filed";

    /** The Constant PREF_SORT_ORDER. */
    public static final String PREF_SORT_ORDER = "sortby";

    /** The boolean for open from image fragment. */
    public static String isOpenFromImageFragment = "openFrom_Fragment";

    /**
     * Instantiates a new util.
     */
    private Util() {

    }

    /**
     * <p>
     * MD5, calculate the MD5 hash of a string
     * </p>
     * .
     * 
     * @param text
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    public static String MD5(final String text) throws SugarCrmException {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            final byte[] md5hash = md.digest();
            return convertToHex(md5hash);
        } catch (final UnsupportedEncodingException ue) {
            throw new SugarCrmException(ue.getMessage());
        } catch (final NoSuchAlgorithmException e) {
            throw new SugarCrmException(e.getMessage());
        }

    }

    /**
     * Convert to hex.
     * 
     * @param data
     *            the data
     * @return the string
     */
    private static String convertToHex(byte[] data) {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int twoHalfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (twoHalfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * changed from private to public so that anyone requiring unique requestIds
     * for Pending Intents can get it.
     * 
     * @return a int.
     */
    public static synchronized int getId() {
        mRequestId += 1;
        return mRequestId;
    }

    /**
     * is Network On.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @return a boolean.
     */
    public static boolean isNetworkOn(Context context) {
        boolean networkOn = false;
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (networkInfo != null) {

            Log.d(LOG_TAG, "network state name:"
                    + networkInfo.getState().name());
            Log.d(LOG_TAG,
                    "NetworkInfo.State.CONNECTED name:"
                            + NetworkInfo.State.CONNECTED.name() + " "
                            + networkInfo.isConnected());

            if (networkInfo.getState().name()
                    .equals(NetworkInfo.State.CONNECTED.name())) {
                networkOn = true;
            }
        }
        Log.d(LOG_TAG, "");
        return networkOn;
    }

    /**
     * Post notification, the package name of the context should be same as that
     * of any activity you want to start.
     * 
     * @param context
     *            - context of the component posting notification, can be an
     *            activity/service etc
     * @param clazz
     *            class of the optional activity that can be started
     * @param tickerTextRes
     *            a int.
     * @param titleRes
     *            a int.
     * @param message
     *            a {@link java.lang.String} object.
     * @return ID of notification so it can be cancelled/updated
     */
    public static synchronized int notify(Context context,
            Class<Activity> clazz, int tickerTextRes, int titleRes,
            String message) {

        return notify(context, context.getPackageName(), clazz, tickerTextRes,
                titleRes, message);

    }

    /**
     * Post notification, the package name should be same as that of any
     * activity clazz you want to start and the context passed can be in any
     * other package.
     * 
     * @param context
     *            - context of the component posting notification, can be an
     *            activity/service etc
     * @param packageName
     *            a {@link java.lang.String} object.
     * @param clazz
     *            class of the optional activity that can be started
     * @param tickerTextRes
     *            a int.
     * @param titleRes
     *            a int.
     * @param message
     *            a {@link java.lang.String} object.
     * @return ID of notification so it can be cancelled/updated
     */
    public static synchronized int notify(Context context, String packageName,
            @SuppressWarnings("rawtypes") Class clazz, int tickerTextRes,
            int titleRes, String message) {
        final CharSequence tickerText = context.getResources().getText(
                tickerTextRes);
        final CharSequence title = context.getResources().getText(titleRes);

        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        final ComponentName comp = new ComponentName(packageName,
                clazz.getSimpleName());
        final Intent intent = new Intent().setComponent(comp);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        final Notification n = new Notification(
                android.R.drawable.stat_notify_sync_noanim, tickerText,
                System.currentTimeMillis());
        n.setLatestEventInfo(context, title, message, pendingIntent);
        final int id = getId();
        nm.notify(id, n);
        return id;
    }

    /**
     * Notify.
     * 
     * @param context
     *            the context
     * @param intent
     *            the intent
     * @param ticker
     *            the ticker
     * @param title
     *            the title
     * @param message
     *            the message
     * @return the int
     */
    public static synchronized int notify(Context context, Intent intent,
            String ticker, String title, String message) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        final Notification n = new Notification(
                android.R.drawable.stat_notify_sync_noanim, ticker,
                System.currentTimeMillis());
        n.setLatestEventInfo(context, title, message, pendingIntent);
        n.flags = Notification.FLAG_AUTO_CANCEL;
        n.defaults |= Notification.DEFAULT_SOUND;
        final int id = getId();
        nm.notify(0, n);
        return id;
    }

    /**
     * Notification cancel.
     * 
     * @param context
     *            the context
     * @param id
     *            the id
     */
    public static synchronized void notificationCancel(Context context, int id) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(id);
    }

    /**
     * Save prefs.
     * 
     * @param activity
     *            the activity
     */
    private static void savePrefs(FragmentActivity activity) {
        final SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(activity);
        final long sevenDays = 7 * 24 * 60 * 60 * 1000L;
        final long time = System.currentTimeMillis();

        final long startMillis = time - sevenDays;
        final long endMillis = time;
        final Editor editor = pref.edit();
        editor.putLong(Util.PREF_SYNC_START_TIME, startMillis);
        editor.putLong(Util.PREF_SYNC_END_TIME, endMillis);

        editor.putString(Util.PREF_FETCH_RECORDS_SIZE, "2000");

        editor.commit();
    }

    /**
     * Start module sync.
     * 
     * @param activity
     *            the activity
     * @param modulename
     *            the modulename
     */
    public static void startModuleSync(FragmentActivity activity,
            String modulename) {
        Log.d(LOG_TAG, "startModuleSync");
        final Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
        extras.putInt(Util.SYNC_TYPE, Util.SYNC_MODULE_DATA);
        extras.putString(RestConstants.MODULE_NAME, modulename);
        final SugarCrmApp app = (SugarCrmApp) activity.getApplication();
        final String usr = SugarCrmSettings.getUsername(activity).toString();
        savePrefs(activity);
        ContentResolver.requestSync(app.getAccount(usr),
                SugarCRMProvider.AUTHORITY, extras);
    }

    /**
     * Start settings activity.
     * 
     * @param c
     *            the c
     */
    public static void startSettingsActivity(Context c) {
        final Intent myIntent = new Intent(c, SugarCrmSettings.class);
        myIntent.putExtra(RestConstants.MODULE_NAME, "settings");
        c.startActivity(myIntent);

    }

    /**
     * Logout.
     * 
     * @param c
     *            the c
     */
    public static void logout(Context c) {
        final Intent myIntent = new Intent(c, WizardAuthActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(c);
        final Editor editor = prefs.edit();
        editor.putString(Util.PREF_USERNAME, null);
        editor.commit();
        c.startActivity(myIntent);
    }

    /**
     * Gets the pressed image.
     * 
     * @param c
     *            the c
     * @param resourcesId
     *            the resources id
     * @return the pressed image
     */
    public static StateListDrawable getPressedImage(Context c, int[] resourcesId) {
        final StateListDrawable states = new StateListDrawable();
        states.addState(new int[] { android.R.attr.state_pressed }, c
                .getResources().getDrawable(resourcesId[0]));
        states.addState(new int[] { android.R.attr.state_focused }, c
                .getResources().getDrawable(resourcesId[1]));
        states.addState(new int[] {},
                c.getResources().getDrawable(resourcesId[2]));
        return states;
    }

    /**
     * Gets the list color state.
     * 
     * @param c
     *            the c
     * @param resourcesId
     *            the resources id
     * @return the list color state
     */
    public static StateListDrawable getListColorState(Context c,
            int[] resourcesId) {
        final StateListDrawable states = new StateListDrawable();
        states.addState(new int[] { android.R.attr.state_pressed }, c
                .getResources().getDrawable(resourcesId[0]));
        states.addState(new int[] { android.R.attr.state_focused }, c
                .getResources().getDrawable(resourcesId[1]));

        return states;
    }

    /**
     * Open detail screen with selected row.
     * 
     * @param c
     *            the c
     * @param cursor
     *            the cursor
     * @param detailIntent
     *            the detail intent
     * @param bRecent
     *            the b recent
     */
    public static void openDetailScreenWithSelectedRow(Context c,
            Cursor cursor, Intent detailIntent, boolean bRecent) {
        if (cursor.getCount() > 0) {
            cursor.moveToNext();

            if (bRecent) {
                detailIntent.putExtra("Recent", true);
                /* open details screen with 1st row highlighted */
                detailIntent.putExtra(Util.ROW_ID, cursor.getString(1));
                detailIntent.putExtra(RestConstants.BEAN_ID,
                        cursor.getString(2));
                detailIntent.putExtra(RestConstants.MODULE_NAME,
                        cursor.getString(3));

            } else {
                detailIntent.putExtra(Util.ROW_ID, cursor.getString(0));
                detailIntent.putExtra(RestConstants.BEAN_ID,
                        cursor.getString(1));
            }
        } else {
            if (bRecent) {
                detailIntent.putExtra(RestConstants.MODULE_NAME, Util.RECENT);
            }
        }
        if (ViewUtil.isTablet(c)) {
            ((BaseMultiPaneActivity) c).openActivityOrFragment(detailIntent);

        }

    }

}
