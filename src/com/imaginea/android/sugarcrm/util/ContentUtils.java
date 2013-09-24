/*******************************************************************************
 * Copyright (c) 2013 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : ContentUtils
 * Description : 
 *              The ContentUtils class
 ******************************************************************************/

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
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ModuleFieldsTableInfo;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Modules;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Opportunities;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Recent;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Users;
import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;

/**
 * The Class ContentUtils.
 */
public final class ContentUtils {
    /** The default supported modules. */
    private static String[] defaultSupportedModules = { Util.CALLS,
            Util.MEETINGS, Util.CAMPAIGNS, Util.OPPORTUNITIES, Util.LEADS,
            Util.CASES, Util.CONTACTS, Util.ACCOUNTS };

    /**
     * Instantiates a new content utils.
     */
    private ContentUtils() {

    }

    /**
     * Gets the user modules.
     * 
     * @param context
     *            the context
     * @return the user modules
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
     * modules available to the user.
     * 
     * @param context
     *            the context
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
     * Sets the user modules.
     * 
     * @param context
     *            the context
     * @param moduleNames
     *            the module names
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    public static void setUserModules(Context context, List<String> moduleNames)
            throws SugarCrmException {

        final ContentResolver cr = context.getContentResolver();

        try {

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
     * Gets the module list.
     * 
     * @param context
     *            the context
     * @return the module list
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

    }

    /**
     * Gets the module icon.
     * 
     * @param moduleName
     *            the module name
     * @return the module icon
     */
    public static int getModuleIcon(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS)) {
            return R.drawable.account;
        } else if (moduleName.equalsIgnoreCase(Util.CONTACTS)) {
            return R.drawable.contacts;
        } else if (moduleName.equalsIgnoreCase(Util.LEADS)) {
            return R.drawable.leads;
        } else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES)) {
            return R.drawable.opportunity;
        } else if (moduleName.equalsIgnoreCase(Util.CASES)) {
            return R.drawable.cases;
        } else if (moduleName.equalsIgnoreCase(Util.CALLS)) {
            return R.drawable.calls;
        } else if (moduleName.equalsIgnoreCase(Util.MEETINGS)) {
            return R.drawable.meeting;
        } else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS)) {
            return R.drawable.campaigns;
        } else if (moduleName.equalsIgnoreCase(Util.RECENT)) {
            return R.drawable.recent;
        } else {
            return android.R.drawable.alert_dark_frame;
        }

    }

    /**
     * Gets the module phone icon.
     * 
     * @param moduleName
     *            the module name
     * @return the module phone icon
     */
    public static int getModulePhoneIcon(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS)) {
            return R.drawable.ico_m_acounts_nor;
        } else if (moduleName.equalsIgnoreCase(Util.CONTACTS)) {
            return R.drawable.ico_m_contacts_nor;
        } else if (moduleName.equalsIgnoreCase(Util.LEADS)) {
            return R.drawable.ico_m_leads_nor;
        } else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES)) {
            return R.drawable.ico_m_opportunities_nor;
        } else if (moduleName.equalsIgnoreCase(Util.CASES)) {
            return R.drawable.ico_m_issues_nor;
        } else if (moduleName.equalsIgnoreCase(Util.CALLS)) {
            return R.drawable.ico_m_call_nor;
        } else if (moduleName.equalsIgnoreCase(Util.MEETINGS)) {
            return R.drawable.ico_m_meeting_nor;
        } else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS)) {
            return R.drawable.ico_m_campaigns_nor;
        } else {
            return android.R.drawable.alert_dark_frame;
        }

    }

    /**
     * Gets the module color.
     * 
     * @param moduleName
     *            the module name
     * @return the module color
     */
    public static int getModuleColor(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS)) {
            return R.color.account;
        } else if (moduleName.equalsIgnoreCase(Util.CONTACTS)) {
            return R.color.contacts;
        } else if (moduleName.equalsIgnoreCase(Util.LEADS)) {
            return R.color.leads;
        } else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES)) {
            return R.color.opportunities;
        } else if (moduleName.equalsIgnoreCase(Util.CASES)) {
            return R.color.cases;
        } else if (moduleName.equalsIgnoreCase(Util.CALLS)) {
            return R.color.calls;
        } else if (moduleName.equalsIgnoreCase(Util.MEETINGS)) {
            return R.color.meeting;
        } else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS)) {
            return R.color.campaigns;
        } else {
            return android.R.color.darker_gray;
        }

    }

    /**
     * Gets the recent modules label color.
     * 
     * @param moduleName
     *            the module name
     * @return the module color
     */
    public static int getRecentModulelablesColors(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS)) {
            return R.color.account_recent_label;
        } else if (moduleName.equalsIgnoreCase(Util.CONTACTS)) {
            return R.color.contacts_recent_label;
        } else if (moduleName.equalsIgnoreCase(Util.LEADS)) {
            return R.color.leads_recent_label;
        } else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES)) {
            return R.color.opportunities_recent_label;
        } else if (moduleName.equalsIgnoreCase(Util.CASES)) {
            return R.color.cases_recent_label;
        } else if (moduleName.equalsIgnoreCase(Util.CALLS)) {
            return R.color.calls_recent_label;
        } else if (moduleName.equalsIgnoreCase(Util.MEETINGS)) {
            return R.color.meeting_recent_label;
        } else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS)) {
            return R.color.campaigns_recent_label;
        } else {
            return android.R.color.darker_gray;
        }

    }

    /**
     * Gets the module alpha color for list view touch.
     * 
     * @param moduleName
     *            the module name
     * @return the module color
     */
    public static int getModuleAlphaColor(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS)) {
            return R.color.account_alpha;
        } else if (moduleName.equalsIgnoreCase(Util.CONTACTS)) {
            return R.color.contacts_alpha;
        } else if (moduleName.equalsIgnoreCase(Util.LEADS)) {
            return R.color.leads_alpha;
        } else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES)) {
            return R.color.opportunities_alpha;
        } else if (moduleName.equalsIgnoreCase(Util.CASES)) {
            return R.color.cases_alpha;
        } else if (moduleName.equalsIgnoreCase(Util.CALLS)) {
            return R.color.calls_alpha;
        } else if (moduleName.equalsIgnoreCase(Util.MEETINGS)) {
            return R.color.meeting_alpha;
        } else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS)) {
            return R.color.campaigns_alpha;
        } else if (moduleName.equalsIgnoreCase(Util.RECENT)) {
            return R.color.recents_alpha;
        } else {
            return android.R.color.darker_gray;
        }

    }

    /**
     * Gets the module selected icon.
     * 
     * @param moduleName
     *            the module name
     * @return the module selected icon
     */
    public static int getModuleSelectedIcon(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS)) {
            return R.drawable.account_sel;
        } else if (moduleName.equalsIgnoreCase(Util.CONTACTS)) {
            return R.drawable.contacts_sel;
        } else if (moduleName.equalsIgnoreCase(Util.LEADS)) {
            return R.drawable.leads_sel;
        } else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES)) {
            return R.drawable.opportunity_sel;
        } else if (moduleName.equalsIgnoreCase(Util.CASES)) {
            return R.drawable.cases_sel;
        } else if (moduleName.equalsIgnoreCase(Util.CALLS)) {
            return R.drawable.calls_sel;
        } else if (moduleName.equalsIgnoreCase(Util.MEETINGS)) {
            return R.drawable.meeting_sel;
        } else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS)) {
            return R.drawable.campaigns_sel;
        } else {
            return android.R.drawable.alert_dark_frame;
        }

    }

    /**
     * Gets the module phone selected icon.
     * 
     * @param moduleName
     *            the module name
     * @return the module phone selected icon
     */
    public static int getModulePhoneSelectedIcon(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS)) {
            return R.drawable.ico_m_acounts_sel;
        } else if (moduleName.equalsIgnoreCase(Util.CONTACTS)) {
            return R.drawable.ico_m_contacts_sel;
        } else if (moduleName.equalsIgnoreCase(Util.LEADS)) {
            return R.drawable.ico_m_leads_sel;
        } else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES)) {
            return R.drawable.ico_m_opportunities_sel;
        } else if (moduleName.equalsIgnoreCase(Util.CASES)) {
            return R.drawable.ico_m_issues_sel;
        } else if (moduleName.equalsIgnoreCase(Util.CALLS)) {
            return R.drawable.ico_m_call_sel;
        } else if (moduleName.equalsIgnoreCase(Util.MEETINGS)) {
            return R.drawable.ico_m_meeting_sel;
        } else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS)) {
            return R.drawable.ico_m_campaigns_sel;
        } else {
            return android.R.drawable.alert_dark_frame;
        }

    }

    /**
     * Gets the module uri.
     * 
     * @param moduleName
     *            the module name
     * @return the module uri
     */
    public static Uri getModuleUri(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS)) {
            return Accounts.CONTENT_URI;
        } else if (moduleName.equalsIgnoreCase(Util.CONTACTS)) {
            return Contacts.CONTENT_URI;
        } else if (moduleName.equalsIgnoreCase(Util.LEADS)) {
            return Leads.CONTENT_URI;
        } else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES)) {
            return Opportunities.CONTENT_URI;
        } else if (moduleName.equalsIgnoreCase(Util.CASES)) {
            return Cases.CONTENT_URI;
        } else if (moduleName.equalsIgnoreCase(Util.CALLS)) {
            return Calls.CONTENT_URI;
        } else if (moduleName.equalsIgnoreCase(Util.MEETINGS)) {
            return Meetings.CONTENT_URI;
        } else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS)) {
            return Campaigns.CONTENT_URI;
        } else if (moduleName.equalsIgnoreCase(Util.USERS)) {
            return Users.CONTENT_URI;
        } else if (moduleName.equalsIgnoreCase(Util.RECENT)) {
            return Recent.CONTENT_URI;
        } else {
            return null;
        }

    }

    /**
     * Gets the module list selections.
     * 
     * @param moduleName
     *            the module name
     * @return the module list selections
     */
    public static String[] getModuleListSelections(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS)) {
            return Accounts.LIST_VIEW_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.CONTACTS)) {
            return Contacts.LIST_VIEW_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.LEADS)) {
            return Leads.LIST_VIEW_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES)) {
            return Opportunities.LIST_VIEW_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.CASES)) {
            return Cases.LIST_VIEW_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.CALLS)) {
            return Calls.LIST_VIEW_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.MEETINGS)) {
            return Meetings.LIST_VIEW_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS)) {
            return Campaigns.LIST_VIEW_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.RECENT)) {
            return Recent.LIST_VIEW_PROJECTION;
        } else {
            return null;
        }
    }

    /**
     * Gets the module projections.
     * 
     * @param moduleName
     *            the module name
     * @return the module projections
     */
    public static String[] getModuleProjections(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS)) {
            return Accounts.DETAILS_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.CONTACTS)) {
            return Contacts.DETAILS_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.LEADS)) {
            return Leads.DETAILS_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES)) {
            return Opportunities.DETAILS_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.CASES)) {
            return Cases.DETAILS_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.CALLS)) {
            return Calls.DETAILS_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.MEETINGS)) {
            return Meetings.DETAILS_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS)) {
            return Campaigns.DETAILS_PROJECTION;
        } else {
            return null;
        }
    }

    /**
     * Gets the module sort order.
     * 
     * @param moduleName
     *            the module name
     * @return the module sort order
     */
    public static String getModuleSortOrder(String moduleName) {
        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS)) {
            return Accounts.DEFAULT_SORT_ORDER;
        } else if (moduleName.equalsIgnoreCase(Util.CONTACTS)) {
            return Contacts.DEFAULT_SORT_ORDER;
        } else if (moduleName.equalsIgnoreCase(Util.LEADS)) {
            return Leads.DEFAULT_SORT_ORDER;
        } else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES)) {
            return Opportunities.DEFAULT_SORT_ORDER;
        } else if (moduleName.equalsIgnoreCase(Util.CASES)) {
            return Cases.DEFAULT_SORT_ORDER;
        } else if (moduleName.equalsIgnoreCase(Util.CALLS)) {
            return Calls.DEFAULT_SORT_ORDER;
        } else if (moduleName.equalsIgnoreCase(Util.MEETINGS)) {
            return Meetings.DEFAULT_SORT_ORDER;
        } else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS)) {
            return Campaigns.DEFAULT_SORT_ORDER;
        } else {
            return null;
        }
    }

    /**
     * Gets the module list projections.
     * 
     * @param moduleName
     *            the module name
     * @return the module list projections
     */
    public static String[] getModuleListProjections(String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS)) {
            return Accounts.LIST_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.CONTACTS)) {
            return Contacts.LIST_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.LEADS)) {
            return Leads.LIST_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES)) {
            return Opportunities.LIST_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.CASES)) {
            return Cases.LIST_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.CALLS)) {
            return Calls.LIST_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.MEETINGS)) {
            return Meetings.LIST_PROJECTION;
        } else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS)) {
            return Campaigns.LIST_PROJECTION;
        } else {
            return null;
        }
    }

    /**
     * Gets the module relationship items.
     * 
     * @param moduleName
     *            the module name
     * @return the module relationship items
     */
    public static String[] getModuleRelationshipItems(final String moduleName) {

        if (moduleName.equalsIgnoreCase(Util.ACCOUNTS)) {
            return new String[] { Util.CONTACTS, Util.OPPORTUNITIES, Util.CASES };
        } else if (moduleName.equalsIgnoreCase(Util.CONTACTS)) {
            return new String[] { Util.OPPORTUNITIES };
        } else if (moduleName.equalsIgnoreCase(Util.LEADS)) {
            return new String[] {};
        } else if (moduleName.equalsIgnoreCase(Util.OPPORTUNITIES)) {
            return new String[] { Util.CONTACTS };
        } else if (moduleName.equalsIgnoreCase(Util.CASES)) {
            return new String[] {};
        } else if (moduleName.equalsIgnoreCase(Util.CALLS)) {
            return new String[] {};
        } else if (moduleName.equalsIgnoreCase(Util.MEETINGS)) {
            return new String[] {};
        } else if (moduleName.equalsIgnoreCase(Util.CAMPAIGNS)) {
            return new String[] {};
        } else {
            return null;
        }
    }

    /**
     * Gets the module fields.
     * 
     * @param context
     *            the context
     * @param moduleName
     *            the module name
     * @return the module fields
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
        cursor = cr
                .query(ModuleFieldsTableInfo.CONTENT_URI,
                        ModuleFieldsTableInfo.DETAILS_PROJECTION, selection,
                        null, null);
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
     * Gets the module field.
     * 
     * @param context
     *            the context
     * @param moduleName
     *            the module name
     * @param fieldName
     *            the field name
     * @return the module field
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
            cursor = cr.query(ModuleFieldsTableInfo.CONTENT_URI,
                    ModuleFieldsTableInfo.DETAILS_PROJECTION, selection, null,
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
