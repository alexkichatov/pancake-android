package com.imaginea.android.sugarcrm.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

import com.imaginea.android.sugarcrm.R;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Accounts;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Calls;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Campaigns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Cases;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Contacts;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Leads;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Meetings;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ModuleColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ModuleFieldColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ModuleFields_TableInfo;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Modules;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Opportunities;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Recent;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Users;
import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;

public class ContentUtils {

    private static String[] defaultSupportedModules = { Util.CALLS,
            Util.MEETINGS, Util.CAMPAIGNS, Util.OPPORTUNITIES, Util.LEADS,
            Util.CASES, Util.CONTACTS, Util.ACCOUNTS };

    /*
     * gives all the available user modules
     */
    /**
     * <p>
     * getUserModules
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    public static List<String> getUserModules(Context context) {

        final ArrayList<String> moduleList = new ArrayList<String>();

        final Cursor cursor = context.getContentResolver().query(
                Modules.CONTENT_URI, Modules.DETAILS_PROJECTION, null, null,
                ModuleColumns.MODULE_NAME);
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            final String moduleName = cursor.getString(cursor
                    .getColumnIndex(ModuleColumns.MODULE_NAME));
            moduleList.add(moduleName);
            cursor.moveToNext();
        }
        cursor.close();
        return moduleList;
    }

    /**
     * while fetching relationship module items, we need to determine if current
     * user has access to that module, this module should be present in the
     * modules available to the user
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean isModuleAccessAvailable(Context context,
            String moduleName) {

        final List<String> userModules = getUserModules(context);
        final int index = Collections.binarySearch(userModules, moduleName);
        return index < 0 ? false : true;
    }

    /**
     * <p>
     * setUserModules
     * </p>
     * 
     * @param moduleNames
     *            a {@link java.util.List} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static void setUserModules(Context context, List<String> moduleNames)
            throws SugarCrmException {

        final ContentResolver cr = context.getContentResolver();

        try {
            // TODO: This has to be removed and is only added to fix the DB
            // insertion issue,
            final int numOfUserModulesDeleted = cr.delete(Modules.CONTENT_URI,
                    null, null);
            Log.d("ContentUtils", "number of user modules deleted: "
                    + numOfUserModulesDeleted);
        } catch (final SQLException sqlex) {
            Log.e("ContentUtils", sqlex.getMessage(), sqlex);
        }

        final ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        final HashSet<String> moduleNamesSet = new HashSet<String>(moduleNames);
        try {
            for (final String moduleName : moduleNamesSet) {
                if (moduleName != null && !moduleName.equals("")) {
                    ops.add(ContentProviderOperation
                            .newInsert(Modules.CONTENT_URI)
                            .withValue(ModuleColumns.MODULE_NAME, moduleName)
                            .build());
                }
            }
            cr.applyBatch(SugarCRMProvider.AUTHORITY, ops);

        } catch (final Exception sqlex) {
            Log.e("ContentUtils", sqlex.getMessage(), sqlex);
        }
    }

    /**
     * <p>
     * Getter for the field <code>moduleList</code>.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    public static List<String> getModuleList(Context context) {
        final List<String> userModules = getUserModules(context);
        final List<String> supportedModules = Arrays
                .asList(defaultSupportedModules);
        final List<String> modules = new ArrayList<String>();
        for (final String module : userModules) {
            if (supportedModules.contains(module)) {
                modules.add(module);
            }
        }
        return modules;
        // TODO: return the module List after the exclusion of modules from the
        // user moduleList
        // return moduleList;
    }

    /**
     * <p>
     * getModuleIcon
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a int.
     */
    public static int getModuleIcon(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS))
            return R.drawable.account;
        else if (moduleName.equalsIgnoreCase(Util.CONTACTS))
            return R.drawable.contacts;
        else if (moduleName.equalsIgnoreCase(Util.LEADS))
            return R.drawable.leads;
        else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES))
            return R.drawable.opportunity;
        else if (moduleName.equalsIgnoreCase(Util.CASES))
            return R.drawable.cases;
        else if (moduleName.equalsIgnoreCase(Util.CALLS))
            return R.drawable.calls;
        else if (moduleName.equalsIgnoreCase(Util.MEETINGS))
            return R.drawable.meeting;
        else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS))
            return R.drawable.campaigns;
        else if (moduleName.equalsIgnoreCase(Util.RECENT))
            return R.drawable.recent;
        else
            return android.R.drawable.alert_dark_frame;

    }

    /**
     * <p>
     * getModuleIcon
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a int.
     */
    public static int getModuleColor(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS))
            return R.color.account;
        else if (moduleName.equalsIgnoreCase(Util.CONTACTS))
            return R.color.contacts;
        else if (moduleName.equalsIgnoreCase(Util.LEADS))
            return R.color.leads;
        else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES))
            return R.color.opportunities;
        else if (moduleName.equalsIgnoreCase(Util.CASES))
            return R.color.cases;
        else if (moduleName.equalsIgnoreCase(Util.CALLS))
            return R.color.calls;
        else if (moduleName.equalsIgnoreCase(Util.MEETINGS))
            return R.color.meeting;
        else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS))
            return R.color.campaigns;
        else
            return android.R.color.darker_gray;

    }

    public static int getModuleSelectedIcon(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS))
            return R.drawable.account_sel;
        else if (moduleName.equalsIgnoreCase(Util.CONTACTS))
            return R.drawable.contacts_sel;
        else if (moduleName.equalsIgnoreCase(Util.LEADS))
            return R.drawable.leads_sel;
        else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES))
            return R.drawable.opportunity_sel;
        else if (moduleName.equalsIgnoreCase(Util.CASES))
            return R.drawable.cases_sel;
        else if (moduleName.equalsIgnoreCase(Util.CALLS))
            return R.drawable.calls_sel;
        else if (moduleName.equalsIgnoreCase(Util.MEETINGS))
            return R.drawable.meeting_sel;
        else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS))
            return R.drawable.campaigns_sel;

        else
            return android.R.drawable.alert_dark_frame;

    }

    /**
     * <p>
     * getModuleUri
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link android.net.Uri} object.
     */
    public static Uri getModuleUri(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS))
            return Accounts.CONTENT_URI;
        else if (moduleName.equalsIgnoreCase(Util.CONTACTS))
            return Contacts.CONTENT_URI;
        else if (moduleName.equalsIgnoreCase(Util.LEADS))
            return Leads.CONTENT_URI;
        else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES))
            return Opportunities.CONTENT_URI;
        else if (moduleName.equalsIgnoreCase(Util.CASES))
            return Cases.CONTENT_URI;
        else if (moduleName.equalsIgnoreCase(Util.CALLS))
            return Calls.CONTENT_URI;
        else if (moduleName.equalsIgnoreCase(Util.MEETINGS))
            return Meetings.CONTENT_URI;
        else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS))
            return Campaigns.CONTENT_URI;
        else if (moduleName.equalsIgnoreCase(Util.USERS))
            return Users.CONTENT_URI;
        else if (moduleName.equalsIgnoreCase(Util.RECENT))
            return Recent.CONTENT_URI;
        else
            return null;

    }

    /**
     * <p>
     * Getter for the field <code>moduleListSelections</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    public static String[] getModuleListSelections(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS))
            return Accounts.LIST_VIEW_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.CONTACTS))
            return Contacts.LIST_VIEW_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.LEADS))
            return Leads.LIST_VIEW_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES))
            return Opportunities.LIST_VIEW_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.CASES))
            return Cases.LIST_VIEW_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.CALLS))
            return Calls.LIST_VIEW_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.MEETINGS))
            return Meetings.LIST_VIEW_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS))
            return Campaigns.LIST_VIEW_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.RECENT))
            return Recent.LIST_VIEW_PROJECTION;
        else
            return null;
    }

    /**
     * <p>
     * Getter for the field <code>moduleProjections</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    public static String[] getModuleProjections(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS))
            return Accounts.DETAILS_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.CONTACTS))
            return Contacts.DETAILS_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.LEADS))
            return Leads.DETAILS_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES))
            return Opportunities.DETAILS_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.CASES))
            return Cases.DETAILS_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.CALLS))
            return Calls.DETAILS_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.MEETINGS))
            return Meetings.DETAILS_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS))
            return Campaigns.DETAILS_PROJECTION;
        else
            return null;
    }

    /**
     * <p>
     * Getter for the field <code>moduleSortOrder</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getModuleSortOrder(String moduleName) {
        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS))
            return Accounts.DEFAULT_SORT_ORDER;
        else if (moduleName.equalsIgnoreCase(Util.CONTACTS))
            return Contacts.DEFAULT_SORT_ORDER;
        else if (moduleName.equalsIgnoreCase(Util.LEADS))
            return Leads.DEFAULT_SORT_ORDER;
        else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES))
            return Opportunities.DEFAULT_SORT_ORDER;
        else if (moduleName.equalsIgnoreCase(Util.CASES))
            return Cases.DEFAULT_SORT_ORDER;
        else if (moduleName.equalsIgnoreCase(Util.CALLS))
            return Calls.DEFAULT_SORT_ORDER;
        else if (moduleName.equalsIgnoreCase(Util.MEETINGS))
            return Meetings.DEFAULT_SORT_ORDER;
        else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS))
            return Campaigns.DEFAULT_SORT_ORDER;
        else
            return null;
    }

    /**
     * <p>
     * Getter for the field <code>moduleListProjections</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    public static String[] getModuleListProjections(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS))
            return Accounts.LIST_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.CONTACTS))
            return Contacts.LIST_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.LEADS))
            return Leads.LIST_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES))
            return Opportunities.LIST_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.CASES))
            return Cases.LIST_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.CALLS))
            return Calls.LIST_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.MEETINGS))
            return Meetings.LIST_PROJECTION;
        else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS))
            return Campaigns.LIST_PROJECTION;
        else
            return null;
    }

    /**
     * <p>
     * Getter for the field <code>moduleRelationshipItems</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    public static String[] getModuleRelationshipItems(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS))
            return new String[] { Util.CONTACTS, Util.OPPORTUNITIES, Util.CASES };
        else if (moduleName.equalsIgnoreCase(Util.CONTACTS))
            return new String[] { Util.OPPORTUNITIES };
        else if (moduleName.equalsIgnoreCase(Util.LEADS))
            return new String[] {}; // Todo
        else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES))
            return new String[] { Util.CONTACTS };
        else if (moduleName.equalsIgnoreCase(Util.CASES))
            return new String[] {}; // Todo
        else if (moduleName.equalsIgnoreCase(Util.CALLS))
            return new String[] {}; // Todo
        else if (moduleName.equalsIgnoreCase(Util.MEETINGS))
            return new String[] {}; // Todo
        else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS))
            return new String[] {}; // Todo
        else
            return null;
    }

    /**
     * <p>
     * Getter for the field <code>moduleFields</code>.
     * </p>
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, ModuleField> getModuleFields(Context context,
            String moduleName) {

        final ContentResolver cr = context.getContentResolver();
        String selection = ModuleColumns.MODULE_NAME + "='" + moduleName + "'";
        Cursor cursor = cr.query(Modules.CONTENT_URI,
                Modules.DETAILS_PROJECTION, selection, null, null);
        cursor.moveToFirst();
        final String moduleId = cursor.getString(0);
        cursor.close();

        // name of the module field is the key and ModuleField is the value
        final HashMap<String, ModuleField> fieldNameVsModuleField = new HashMap<String, ModuleField>();
        selection = ModuleFieldColumns.MODULE_ID + "=" + moduleId;
        cursor = cr.query(ModuleFields_TableInfo.CONTENT_URI,
                ModuleFields_TableInfo.DETAILS_PROJECTION, selection, null,
                null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            final String name = cursor.getString(cursor
                    .getColumnIndex(ModuleFieldColumns.NAME));
            final ModuleField moduleField = new ModuleField(
                    name,
                    cursor.getString(cursor
                            .getColumnIndex(ModuleFieldColumns.TYPE)),
                    cursor.getString(cursor
                            .getColumnIndex(ModuleFieldColumns.LABEL)),
                    cursor.getInt(cursor
                            .getColumnIndex(ModuleFieldColumns.IS_REQUIRED)) == 1 ? true
                            : false);
            cursor.moveToNext();
            fieldNameVsModuleField.put(name, moduleField);
        }
        cursor.close();

        return fieldNameVsModuleField;
    }

    /**
     * <p>
     * getModuleField
     * </p>
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param fieldName
     *            a {@link java.lang.String} object.
     * @return a {@link com.imaginea.android.sugarcrm.util.ModuleField} object.
     */
    public static ModuleField getModuleField(Context context,
            String moduleName, String fieldName) {

        ModuleField moduleField = null;

        final ContentResolver cr = context.getContentResolver();

        String selection = ModuleColumns.MODULE_NAME + "='" + moduleName + "'";
        Cursor cursor = cr.query(Modules.CONTENT_URI,
                Modules.DETAILS_PROJECTION, selection, null, null);
        final int num = cursor.getCount();
        if (num > 0) {
            cursor.moveToFirst();
            final String moduleId = cursor.getString(0);
            cursor.close();

            selection = "(" + ModuleFieldColumns.MODULE_ID + "=" + moduleId
                    + " AND " + ModuleFieldColumns.NAME + "='" + fieldName
                    + "')";
            cursor = cr.query(ModuleFields_TableInfo.CONTENT_URI,
                    ModuleFields_TableInfo.DETAILS_PROJECTION, selection, null,
                    null);
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                moduleField = new ModuleField(
                        cursor.getString(cursor
                                .getColumnIndex(ModuleFieldColumns.NAME)),
                        cursor.getString(cursor
                                .getColumnIndex(ModuleFieldColumns.TYPE)),
                        cursor.getString(cursor
                                .getColumnIndex(ModuleFieldColumns.LABEL)),
                        cursor.getInt(cursor
                                .getColumnIndex(ModuleFieldColumns.IS_REQUIRED)) == 1 ? true

                                : false);
            }
        }
        cursor.close();

        return moduleField;
    }

}
