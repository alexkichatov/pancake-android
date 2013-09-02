/*******************************************************************************
 * Copyright (c) 2013 Asha, Muralidaran.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Asha, Muralidaran - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : DatabaseHelper 
 * Description : 
                This class helps open, create, and upgrade the database file.
 ******************************************************************************/

package com.imaginea.android.sugarcrm.provider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.SugarCrmSettings;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLActionColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLActions;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLRoleColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLRoles;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsCasesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsContactsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsOpportunitiesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AlarmColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.CallsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.CampaignColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.CasesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ContactsCasesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ContactsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ContactsOpportunitiesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.LeadsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.LinkFieldColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.MeetingsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ModuleColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ModuleFieldColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ModuleFieldSortOrder;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ModuleFieldSortOrderColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ModuleFields_TableInfo;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Modules;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.OpportunitiesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.RecentColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Sync;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.SyncColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.UserColumns;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.rest.SugarBean;
import com.imaginea.android.sugarcrm.sync.SyncRecord;
import com.imaginea.android.sugarcrm.util.ACLConstants;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.LinkField;
import com.imaginea.android.sugarcrm.util.Module;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.ModuleFieldBean;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * The Class DatabaseHelper.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /** The Constant DATABASE_NAME. */
    private static final String DATABASE_NAME = "sugar_crm.db";

    // TODO: RESET the database version to 1
    /** The Constant DATABASE_VERSION. */
    private static final int DATABASE_VERSION = 34;

    /** The Constant ACCOUNTS_TABLE_NAME. */
    public static final String ACCOUNTS_TABLE_NAME = "accounts";

    /** The Constant CONTACTS_TABLE_NAME. */
    public static final String CONTACTS_TABLE_NAME = "contacts";

    /** The Constant ACCOUNTS_CONTACTS_TABLE_NAME. */
    public static final String ACCOUNTS_CONTACTS_TABLE_NAME = "accounts_contacts";

    /** The Constant ACCOUNTS_OPPORTUNITIES_TABLE_NAME. */
    public static final String ACCOUNTS_OPPORTUNITIES_TABLE_NAME = "accounts_opportunities";

    /** The Constant ACCOUNTS_CASES_TABLE_NAME. */
    public static final String ACCOUNTS_CASES_TABLE_NAME = "accounts_cases";

    /** The Constant CONTACTS_OPPORTUNITIES_TABLE_NAME. */
    public static final String CONTACTS_OPPORTUNITIES_TABLE_NAME = "contacts_opportunities";

    /** The Constant CONTACTS_CASES_TABLE_NAME. */
    public static final String CONTACTS_CASES_TABLE_NAME = "contacts_cases";

    /** The Constant LEADS_TABLE_NAME. */
    public static final String LEADS_TABLE_NAME = "leads";

    /** The Constant RECENT_TABLE_NAME. */
    public static final String RECENT_TABLE_NAME = "recent";

    /** The Constant ALARM_TABLE_NAME. */
    public static final String ALARM_TABLE_NAME = "alarms";

    /** The Constant OPPORTUNITIES_TABLE_NAME. */
    public static final String OPPORTUNITIES_TABLE_NAME = "opportunities";

    /** The Constant MEETINGS_TABLE_NAME. */
    public static final String MEETINGS_TABLE_NAME = "meetings";

    /** The Constant CALLS_TABLE_NAME. */
    public static final String CALLS_TABLE_NAME = "calls";

    /** The Constant CASES_TABLE_NAME. */
    public static final String CASES_TABLE_NAME = "cases";

    /** The Constant CAMPAIGNS_TABLE_NAME. */
    public static final String CAMPAIGNS_TABLE_NAME = "campaigns";

    /** The Constant MODULES_TABLE_NAME. */
    public static final String MODULES_TABLE_NAME = "modules";

    /** The Constant MODULE_FIELDS_TABLE_NAME. */
    public static final String MODULE_FIELDS_TABLE_NAME = "module_fields";

    /** The Constant LINK_FIELDS_TABLE_NAME. */
    public static final String LINK_FIELDS_TABLE_NAME = "link_fields";

    /** The Constant SYNC_TABLE_NAME. */
    public static final String SYNC_TABLE_NAME = "sync_table";

    /** The Constant USERS_TABLE_NAME. */
    public static final String USERS_TABLE_NAME = "users";

    /** The Constant ACL_ROLES_TABLE_NAME. */
    public static final String ACL_ROLES_TABLE_NAME = "acl_roles";

    /** The Constant ACL_ACTIONS_TABLE_NAME. */
    public static final String ACL_ACTIONS_TABLE_NAME = "acl_actions";

    /** The Constant MODULE_FIELDS_SORT_ORDER_TABLE_NAME. */
    public static final String MODULE_FIELDS_SORT_ORDER_TABLE_NAME = "module_fields_sort_order";

    /** The Constant MODULE_FIELDS_GROUP_TABLE_NAME. */
    public static final String MODULE_FIELDS_GROUP_TABLE_NAME = "module_fields_group";

    /** The Constant TAG. */
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    /** The link fields. */
    private static HashMap<String, HashMap<String, LinkField>> linkFields;

    /** The Constant relationshipTables. */
    private static final HashMap<String, String[]> relationshipTables = new HashMap<String, String[]>();

    /** The Constant accountRelationsSelection. */
    private static final HashMap<String, String> accountRelationsSelection = new HashMap<String, String>();

    /** The Constant accountRelationsTableName. */
    private static final HashMap<String, String> accountRelationsTableName = new HashMap<String, String>();

    /** The Constant linkfieldNames. */
    private static final HashMap<String, String> linkfieldNames = new HashMap<String, String>();

    /** The access map. */
    private final Map<String, Map<String, Integer>> accessMap = new HashMap<String, Map<String, Integer>>();

    /** The fields excluded for edit. */
    private static Map<String, String> fieldsExcludedForEdit = new HashMap<String, String>();

    /** The fields excluded for details. */
    private static Map<String, String> fieldsExcludedForDetails = new HashMap<String, String>();

    /** The m context. */
    private final Context mContext;

    /** The m selection. */
    private static String mSelection = SugarCRMContent.RECORD_ID + "=?";

    static {

        /* relationship tables for each module */
        relationshipTables.put(Util.CONTACTS,
                new String[] { ACCOUNTS_CONTACTS_TABLE_NAME,
                        CONTACTS_OPPORTUNITIES_TABLE_NAME });
        relationshipTables.put(Util.LEADS, new String[] {});
        relationshipTables.put(Util.OPPORTUNITIES, new String[] {
                ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                CONTACTS_OPPORTUNITIES_TABLE_NAME });
        relationshipTables.put(Util.CASES,
                new String[] { ACCOUNTS_CASES_TABLE_NAME });
        relationshipTables.put(Util.CALLS, new String[] {});
        relationshipTables.put(Util.MEETINGS, new String[] {});
        relationshipTables.put(Util.CAMPAIGNS, new String[] {});

        // selection for the moduleName that has relationship with Accounts
        // module moduleName vs selection column name
        accountRelationsSelection.put(Util.CONTACTS, ModuleFields.CONTACT_ID);
        accountRelationsSelection.put(Util.OPPORTUNITIES,
                ModuleFields.OPPORTUNITY_ID);
        accountRelationsSelection.put(Util.CASES, Util.CASE_ID);

        // table name for the relationships with Accounts module
        // moduleName vs relationship table name
        accountRelationsTableName.put(Util.CONTACTS,
                ACCOUNTS_CONTACTS_TABLE_NAME);
        accountRelationsTableName.put(Util.OPPORTUNITIES,
                ACCOUNTS_OPPORTUNITIES_TABLE_NAME);
        accountRelationsTableName.put(Util.CASES, ACCOUNTS_CASES_TABLE_NAME);

        linkfieldNames.put(Util.CONTACTS, "contacts");
        linkfieldNames.put(Util.LEADS, "leads");
        linkfieldNames.put(Util.OPPORTUNITIES, "opportunities");
        linkfieldNames.put(Util.CASES, "cases");
        linkfieldNames.put(Util.CALLS, "calls");
        linkfieldNames.put(Util.MEETINGS, "meetings");
        linkfieldNames.put(Util.CAMPAIGNS, "campaigns");
        linkfieldNames.put(Util.ACLROLES, "aclroles");
        linkfieldNames.put(Util.ACLACTIONS, "actions");

        // add a field name to the map if a module field in detail projection is
        // to be excluded
        fieldsExcludedForEdit.put(SugarCRMContent.RECORD_ID,
                SugarCRMContent.RECORD_ID);
        fieldsExcludedForEdit.put(SugarCRMContent.SUGAR_BEAN_ID,
                SugarCRMContent.SUGAR_BEAN_ID);
        fieldsExcludedForEdit.put(ModuleFields.DELETED, ModuleFields.DELETED);
        fieldsExcludedForEdit.put(ModuleFields.ACCOUNT_ID,
                ModuleFields.ACCOUNT_ID);
        fieldsExcludedForEdit.put(ModuleFields.DATE_ENTERED,
                ModuleFields.DATE_ENTERED);
        fieldsExcludedForEdit.put(ModuleFields.DATE_MODIFIED,
                ModuleFields.DATE_MODIFIED);
        fieldsExcludedForEdit.put(ModuleFields.CREATED_BY,
                ModuleFields.CREATED_BY);
        fieldsExcludedForEdit.put(ModuleFields.CREATED_BY_NAME,
                ModuleFields.CREATED_BY_NAME);
        fieldsExcludedForEdit.put(ModuleFields.MODIFIED_USER_ID,
                ModuleFields.MODIFIED_USER_ID);
        fieldsExcludedForEdit.put(ModuleFields.MODIFIED_BY_NAME,
                ModuleFields.MODIFIED_BY_NAME);

        // add a field name to the map if a module field in detail projection is
        // to be excluded in details screen
        fieldsExcludedForDetails.put(SugarCRMContent.RECORD_ID,
                SugarCRMContent.RECORD_ID);
        fieldsExcludedForDetails.put(SugarCRMContent.SUGAR_BEAN_ID,
                SugarCRMContent.SUGAR_BEAN_ID);
        fieldsExcludedForDetails
                .put(ModuleFields.DELETED, ModuleFields.DELETED);
        fieldsExcludedForDetails.put(ModuleFields.ACCOUNT_ID,
                ModuleFields.ACCOUNT_ID);

    }

    /**
     * Instantiates a new database helper.
     * 
     * @param context
     *            the context
     */
    public DatabaseHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     * .SQLiteDatabase)
     */
    @Override
    public void onCreate(final SQLiteDatabase db) {
        createAccountsTable(db);
        createContactsTable(db);
        createLeadsTable(db);
        createOpportunitiesTable(db);
        createCasesTable(db);
        createCallsTable(db);
        createMeetingsTable(db);
        createCampaignsTable(db);
        createAlarmsTable(db);

        // create meta-data tables
        createModulesTable(db);
        createModuleFieldsTable(db);
        createLinkFieldsTable(db);

        createUsersTable(db);
        createAclRolesTable(db);
        createAclActionsTable(db);

        // create join tables
        createAccountsContactsTable(db);
        createAccountsOpportunitiesTable(db);
        createAccountsCasesTable(db);
        createContactsOpportunitiesTable(db);
        createContactsCases(db);

        // create sync tables
        createSyncTable(db);
        createRecentTable(db);

    }

    /**
     * Drop accounts table.
     * 
     * @param db
     *            the db
     */
    void dropAccountsTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_TABLE_NAME);
    }

    /**
     * Drop contacts table.
     * 
     * @param db
     *            the db
     */
    void dropContactsTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
    }

    /**
     * Drop leads table.
     * 
     * @param db
     *            the db
     */
    void dropLeadsTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + LEADS_TABLE_NAME);
    }

    /**
     * Drop opportunities table.
     * 
     * @param db
     *            the db
     */
    void dropOpportunitiesTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + OPPORTUNITIES_TABLE_NAME);
    }

    /**
     * Drop cases table.
     * 
     * @param db
     *            the db
     */
    void dropCasesTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + CASES_TABLE_NAME);
    }

    /**
     * Drop calls table.
     * 
     * @param db
     *            the db
     */
    void dropCallsTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + CALLS_TABLE_NAME);
    }

    /**
     * Drop meetings table.
     * 
     * @param db
     *            the db
     */
    void dropMeetingsTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + MEETINGS_TABLE_NAME);
    }

    /**
     * Drop campaigns table.
     * 
     * @param db
     *            the db
     */
    void dropCampaignsTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + CAMPAIGNS_TABLE_NAME);
    }

    /**
     * Drop modules table.
     * 
     * @param db
     *            the db
     */
    void dropModulesTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + MODULES_TABLE_NAME);
    }

    /**
     * Drop module fields table.
     * 
     * @param db
     *            the db
     */
    void dropModuleFieldsTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + MODULE_FIELDS_TABLE_NAME);
    }

    /**
     * Drop link fields table.
     * 
     * @param db
     *            the db
     */
    void dropLinkFieldsTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + LINK_FIELDS_TABLE_NAME);
    }

    /**
     * Drop accounts contacts table.
     * 
     * @param db
     *            the db
     */
    void dropAccountsContactsTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_CONTACTS_TABLE_NAME);
    }

    /**
     * Drop accounts cases table.
     * 
     * @param db
     *            the db
     */
    void dropAccountsCasesTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_CASES_TABLE_NAME);
    }

    /**
     * Drop accounts opportunities table.
     * 
     * @param db
     *            the db
     */
    void dropAccountsOpportunitiesTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_OPPORTUNITIES_TABLE_NAME);
    }

    /**
     * Drop contacts opportunities table.
     * 
     * @param db
     *            the db
     */
    void dropContactsOpportunitiesTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_OPPORTUNITIES_TABLE_NAME);
    }

    /**
     * Drop contacts cases table.
     * 
     * @param db
     *            the db
     */
    void dropContactsCasesTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_CASES_TABLE_NAME);
    }

    /**
     * Drop sync table.
     * 
     * @param db
     *            the db
     */
    void dropSyncTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + SYNC_TABLE_NAME);
    }

    /**
     * Drop users table.
     * 
     * @param db
     *            the db
     */
    void dropUsersTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
    }

    /**
     * Drop acl roles table.
     * 
     * @param db
     *            the db
     */
    void dropAclRolesTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + ACL_ROLES_TABLE_NAME);
    }

    /**
     * Drop acl actions table.
     * 
     * @param db
     *            the db
     */
    void dropAclActionsTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + ACL_ACTIONS_TABLE_NAME);
    }

    /**
     * Drop module fields sort order table.
     * 
     * @param db
     *            the db
     */
    void dropModuleFieldsSortOrderTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "
                + MODULE_FIELDS_SORT_ORDER_TABLE_NAME);
    }

    /**
     * Drop module fields group table.
     * 
     * @param db
     *            the db
     */
    void dropModuleFieldsGroupTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + MODULE_FIELDS_GROUP_TABLE_NAME);
    }

    /**
     * Drop recent table.
     * 
     * @param db
     *            the db
     */
    void dropRecentTable(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + RECENT_TABLE_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
            final int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        // TODO - do not drop - only for development right now
        dropAllDataTables(db);
        onCreate(db);
    }

    /**
     * Drop all data tables.
     * 
     * @param db
     *            the db
     */
    private void dropAllDataTables(final SQLiteDatabase db) {
        dropAccountsTable(db);
        dropContactsTable(db);
        dropLeadsTable(db);
        dropOpportunitiesTable(db);
        dropCasesTable(db);
        dropCallsTable(db);
        dropMeetingsTable(db);
        dropCampaignsTable(db);

        dropModulesTable(db);
        dropModuleFieldsTable(db);
        dropLinkFieldsTable(db);

        dropUsersTable(db);
        dropAclRolesTable(db);
        dropAclActionsTable(db);

        // drop join tables
        dropAccountsContactsTable(db);
        dropAccountsOpportunitiesTable(db);
        dropAccountsCasesTable(db);
        dropContactsOpportunitiesTable(db);
        dropContactsCasesTable(db);

        dropSyncTable(db);
        dropRecentTable(db);
    }

    /**
     * Creates the alarms table.
     * 
     * @param db
     *            the db
     */
    private static void createAlarmsTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + ALARM_TABLE_NAME + " (" + AlarmColumns.ID
                + " INTEGER," + AlarmColumns.ALARM_STATE + " INTEGER" + ");");
    }

    /**
     * Creates the recent table.
     * 
     * @param db
     *            the db
     */
    private static void createRecentTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + RECENT_TABLE_NAME + " ("
                + RecentColumns.ID + " INTEGER," + RecentColumns.ACTUAL_ID
                + " INTEGER," + RecentColumns.BEAN_ID + " TEXT,"
                + RecentColumns.REF_MODULE_NAME + " TEXT,"
                + RecentColumns.NAME_1 + " TEXT," + RecentColumns.NAME_2
                + " TEXT," + RecentColumns.DELETED + " INTEGER" + ");");
    }

    /**
     * Creates the accounts table.
     * 
     * @param db
     *            the db
     */
    private static void createAccountsTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + ACCOUNTS_TABLE_NAME + " ("
                + AccountsColumns.ID + " INTEGER PRIMARY KEY,"
                + AccountsColumns.BEAN_ID + " TEXT," + AccountsColumns.NAME
                + " TEXT," + AccountsColumns.EMAIL1 + " TEXT,"
                + AccountsColumns.PARENT_NAME + " TEXT,"
                + AccountsColumns.PHONE_OFFICE + " TEXT,"
                + AccountsColumns.PHONE_FAX + " TEXT,"
                + AccountsColumns.WEBSITE + " TEXT,"
                + AccountsColumns.EMPLOYEES + " TEXT,"
                + AccountsColumns.TICKER_SYMBOL + " TEXT,"
                + AccountsColumns.ANNUAL_REVENUE + " TEXT,"
                + AccountsColumns.BILLING_ADDRESS_STREET + " TEXT,"
                + AccountsColumns.BILLING_ADDRESS_STREET_2 + " TEXT,"
                + AccountsColumns.BILLING_ADDRESS_STREET_3 + " TEXT,"
                + AccountsColumns.BILLING_ADDRESS_STREET_4 + " TEXT,"
                + AccountsColumns.BILLING_ADDRESS_CITY + " TEXT,"
                + AccountsColumns.BILLING_ADDRESS_STATE + " TEXT,"
                + AccountsColumns.BILLING_ADDRESS_POSTALCODE + " TEXT,"
                + AccountsColumns.BILLING_ADDRESS_COUNTRY + " TEXT,"
                + AccountsColumns.SHIPPING_ADDRESS_STREET + " TEXT,"
                + AccountsColumns.SHIPPING_ADDRESS_STREET_2 + " TEXT,"
                + AccountsColumns.SHIPPING_ADDRESS_STREET_3 + " TEXT,"
                + AccountsColumns.SHIPPING_ADDRESS_STREET_4 + " TEXT,"
                + AccountsColumns.SHIPPING_ADDRESS_CITY + " TEXT,"
                + AccountsColumns.SHIPPING_ADDRESS_STATE + " TEXT,"
                + AccountsColumns.SHIPPING_ADDRESS_POSTALCODE + " TEXT,"
                + AccountsColumns.SHIPPING_ADDRESS_COUNTRY + " TEXT,"
                + AccountsColumns.ASSIGNED_USER_NAME + " TEXT,"
                + AccountsColumns.CREATED_BY_NAME + " TEXT,"
                + AccountsColumns.DATE_ENTERED + " TEXT,"
                + AccountsColumns.DATE_MODIFIED + " TEXT,"
                + AccountsColumns.DELETED + " INTEGER," + " UNIQUE("
                + AccountsColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the contacts table.
     * 
     * @param db
     *            the db
     */
    private static void createContactsTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + CONTACTS_TABLE_NAME + " ("
                + ContactsColumns.ID + " INTEGER PRIMARY KEY,"
                + ContactsColumns.BEAN_ID + " TEXT,"
                + ContactsColumns.FIRST_NAME + " TEXT,"
                + ContactsColumns.LAST_NAME + " TEXT,"
                + ContactsColumns.ACCOUNT_NAME + " TEXT,"
                + ContactsColumns.PHONE_MOBILE + " TEXT,"
                + ContactsColumns.PHONE_WORK + " TEXT,"
                + ContactsColumns.EMAIL1 + " TEXT,"
                + ContactsColumns.ASSIGNED_USER_NAME + " TEXT,"
                + ContactsColumns.CREATED_BY + " TEXT,"
                + ContactsColumns.MODIFIED_BY_NAME + " TEXT,"
                + ContactsColumns.CREATED_BY_NAME + " TEXT,"
                + ContactsColumns.DATE_ENTERED + " TEXT,"
                + ContactsColumns.DATE_MODIFIED + " TEXT,"
                + ContactsColumns.DELETED + " INTEGER,"
                + ContactsColumns.ACCOUNT_ID + " INTEGER," + " UNIQUE("
                + ContactsColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the leads table.
     * 
     * @param db
     *            the db
     */
    private static void createLeadsTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + LEADS_TABLE_NAME + " (" + LeadsColumns.ID
                + " INTEGER PRIMARY KEY," + LeadsColumns.BEAN_ID + " TEXT,"
                + LeadsColumns.FIRST_NAME + " TEXT," + LeadsColumns.LAST_NAME
                + " TEXT," + LeadsColumns.LEAD_SOURCE + " TEXT,"
                + LeadsColumns.EMAIL1 + " TEXT," + LeadsColumns.PHONE_WORK
                + " TEXT," + LeadsColumns.PHONE_FAX + " TEXT,"
                + LeadsColumns.ACCOUNT_NAME + " TEXT," + LeadsColumns.TITLE
                + " TEXT," + LeadsColumns.ASSIGNED_USER_NAME + " TEXT,"
                + LeadsColumns.CREATED_BY_NAME + " TEXT,"
                + LeadsColumns.DATE_ENTERED + " TEXT,"
                + LeadsColumns.DATE_MODIFIED + " TEXT," + LeadsColumns.DELETED
                + " INTEGER," + LeadsColumns.ACCOUNT_ID + " INTEGER,"
                + " UNIQUE(" + LeadsColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the opportunities table.
     * 
     * @param db
     *            the db
     */
    private static void createOpportunitiesTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + OPPORTUNITIES_TABLE_NAME + " ("
                + OpportunitiesColumns.ID + " INTEGER PRIMARY KEY,"
                + OpportunitiesColumns.BEAN_ID + " TEXT,"
                + OpportunitiesColumns.NAME + " TEXT,"
                + OpportunitiesColumns.ACCOUNT_NAME + " TEXT,"
                + OpportunitiesColumns.AMOUNT + " TEXT,"
                + OpportunitiesColumns.AMOUNT_USDOLLAR + " TEXT,"
                + OpportunitiesColumns.ASSIGNED_USER_ID + " TEXT,"
                + OpportunitiesColumns.ASSIGNED_USER_NAME + " TEXT,"
                + OpportunitiesColumns.CAMPAIGN_NAME + " TEXT,"
                + OpportunitiesColumns.CREATED_BY + " TEXT,"
                + OpportunitiesColumns.CREATED_BY_NAME + " TEXT,"
                + OpportunitiesColumns.CURRENCY_ID + " TEXT,"
                + OpportunitiesColumns.CURRENCY_NAME + " TEXT,"
                + OpportunitiesColumns.CURRENCY_SYMBOL + " TEXT,"
                + OpportunitiesColumns.DATE_CLOSED + " TEXT,"
                + OpportunitiesColumns.DATE_ENTERED + " TEXT,"
                + OpportunitiesColumns.DATE_MODIFIED + " TEXT,"
                + OpportunitiesColumns.DESCRIPTION + " TEXT,"
                + OpportunitiesColumns.LEAD_SOURCE + " TEXT,"
                + OpportunitiesColumns.MODIFIED_BY_NAME + " TEXT,"
                + OpportunitiesColumns.MODIFIED_USER_ID + " TEXT,"
                + OpportunitiesColumns.NEXT_STEP + " TEXT,"
                + OpportunitiesColumns.OPPORTUNITY_TYPE + " TEXT,"
                + OpportunitiesColumns.PROBABILITY + " TEXT,"
                + OpportunitiesColumns.SALES_STAGE + " TEXT,"
                + OpportunitiesColumns.DELETED + " INTEGER,"
                + OpportunitiesColumns.ACCOUNT_ID + " INTEGER," + " UNIQUE("
                + OpportunitiesColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the cases table.
     * 
     * @param db
     *            the db
     */
    private static void createCasesTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + CASES_TABLE_NAME + " (" + CasesColumns.ID
                + " INTEGER PRIMARY KEY," + CasesColumns.BEAN_ID + " TEXT,"
                + CasesColumns.NAME + " TEXT," + CasesColumns.ACCOUNT_NAME
                + " TEXT," + CasesColumns.CASE_NUMBER + " TEXT,"
                + CasesColumns.PRIORITY + " TEXT,"
                + CasesColumns.ASSIGNED_USER_NAME + " TEXT,"
                + CasesColumns.STATUS + " TEXT," + CasesColumns.DESCRIPTION
                + " TEXT," + CasesColumns.RESOLUTION + " TEXT,"
                + CasesColumns.CREATED_BY_NAME + " TEXT,"
                + CasesColumns.DATE_ENTERED + " TEXT,"
                + CasesColumns.DATE_MODIFIED + " TEXT," + CasesColumns.DELETED
                + " INTEGER," + " UNIQUE(" + CasesColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the calls table.
     * 
     * @param db
     *            the db
     */
    private static void createCallsTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + CALLS_TABLE_NAME + " (" + CallsColumns.ID
                + " INTEGER PRIMARY KEY," + CallsColumns.BEAN_ID + " TEXT,"
                + CallsColumns.NAME + " TEXT," + CallsColumns.ACCOUNT_NAME
                + " TEXT," + CallsColumns.STATUS + " TEXT,"
                + CallsColumns.START_DATE + " TEXT,"
                + CallsColumns.DURATION_HOURS + " TEXT,"
                + CallsColumns.DURATION_MINUTES + " TEXT,"
                + CallsColumns.ASSIGNED_USER_NAME + " TEXT,"
                + CallsColumns.DESCRIPTION + " TEXT,"
                + CallsColumns.CREATED_BY_NAME + " TEXT,"
                + CallsColumns.DATE_ENTERED + " TEXT,"
                + CallsColumns.DATE_MODIFIED + " TEXT," + CallsColumns.DELETED
                + " INTEGER," + " UNIQUE(" + CallsColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the meetings table.
     * 
     * @param db
     *            the db
     */
    private static void createMeetingsTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + MEETINGS_TABLE_NAME + " ("
                + MeetingsColumns.ID + " INTEGER PRIMARY KEY,"
                + MeetingsColumns.BEAN_ID + " TEXT," + MeetingsColumns.NAME
                + " TEXT," + MeetingsColumns.ACCOUNT_NAME + " TEXT,"
                + MeetingsColumns.STATUS + " TEXT," + MeetingsColumns.LOCATION
                + " TEXT," + MeetingsColumns.START_DATE + " TEXT,"
                + MeetingsColumns.DURATION_HOURS + " TEXT,"
                + MeetingsColumns.DURATION_MINUTES + " TEXT,"
                + MeetingsColumns.ASSIGNED_USER_NAME + " TEXT,"
                + MeetingsColumns.DESCRIPTION + " TEXT,"
                + MeetingsColumns.CREATED_BY_NAME + " TEXT,"
                + MeetingsColumns.DATE_ENTERED + " TEXT,"
                + MeetingsColumns.DATE_MODIFIED + " TEXT,"
                + MeetingsColumns.DELETED + " INTEGER," + " UNIQUE("
                + MeetingsColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the campaigns table.
     * 
     * @param db
     *            the db
     */
    private static void createCampaignsTable(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CAMPAIGNS_TABLE_NAME + " ("
                + CampaignColumns.ID + " INTEGER PRIMARY KEY,"
                + CampaignColumns.BEAN_ID + " TEXT," + CampaignColumns.NAME
                + " TEXT," + CampaignColumns.STATUS + " TEXT,"
                + CampaignColumns.START_DATE + " TEXT,"
                + CampaignColumns.END_DATE + " TEXT,"
                + CampaignColumns.CAMPAIGN_TYPE + " TEXT,"
                + CampaignColumns.BUDGET + " TEXT,"
                + CampaignColumns.ACTUAL_COST + " TEXT,"
                + CampaignColumns.EXPECTED_COST + " TEXT,"
                + CampaignColumns.EXPECTED_REVENUE + " TEXT,"
                + CampaignColumns.IMPRESSIONS + " TEXT,"
                + CampaignColumns.OBJECTIVE + " TEXT,"
                + CampaignColumns.FREQUENCY + " TEXT,"
                + CampaignColumns.ASSIGNED_USER_NAME + " TEXT,"
                + CampaignColumns.DESCRIPTION + " TEXT,"
                + CampaignColumns.CREATED_BY_NAME + " TEXT,"
                + CampaignColumns.DATE_ENTERED + " TEXT,"
                + CampaignColumns.DATE_MODIFIED + " TEXT,"
                + CampaignColumns.DELETED + " INTEGER," + " UNIQUE("
                + MeetingsColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the modules table.
     * 
     * @param db
     *            the db
     */
    private static void createModulesTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + MODULES_TABLE_NAME + " ("
                + ModuleColumns.ID + " INTEGER PRIMARY KEY,"
                + ModuleColumns.MODULE_NAME + " TEXT,"
                + ModuleColumns.LAST_SYNC_TIME + " TEXT," + " UNIQUE("
                + ModuleColumns.MODULE_NAME + ")" + ");");
    }

    /**
     * Creates the module fields table.
     * 
     * @param db
     *            the db
     */
    private static void createModuleFieldsTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + MODULE_FIELDS_TABLE_NAME + " ("
                + ModuleFieldColumns.ID + " INTEGER PRIMARY KEY,"
                + ModuleFieldColumns.NAME + " TEXT," + ModuleFieldColumns.LABEL
                + " TEXT," + ModuleFieldColumns.TYPE + " TEXT,"
                + ModuleFieldColumns.IS_REQUIRED + " INTEGER,"
                + ModuleFieldColumns.MODULE_ID + " INTEGER" + ");");
    }

    /**
     * Creates the link fields table.
     * 
     * @param db
     *            the db
     */
    private static void createLinkFieldsTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + LINK_FIELDS_TABLE_NAME + " ("
                + LinkFieldColumns.ID + " INTEGER PRIMARY KEY,"
                + LinkFieldColumns.NAME + " TEXT," + LinkFieldColumns.TYPE
                + " TEXT," + LinkFieldColumns.RELATIONSHIP + " TEXT,"
                + LinkFieldColumns.MODULE + " TEXT,"
                + LinkFieldColumns.BEAN_NAME + " TEXT,"
                + LinkFieldColumns.MODULE_ID + " INTEGER" + ");");
    }

    /**
     * Creates the accounts contacts table.
     * 
     * @param db
     *            the db
     */
    private static void createAccountsContactsTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + ACCOUNTS_CONTACTS_TABLE_NAME + " ("
                + AccountsContactsColumns.ACCOUNT_ID + " INTEGER ,"
                + AccountsContactsColumns.CONTACT_ID + " INTEGER ,"
                + AccountsContactsColumns.DATE_MODIFIED + " TEXT,"
                + AccountsContactsColumns.DELETED + " INTEGER," + " UNIQUE("
                + AccountsContactsColumns.ACCOUNT_ID + ","
                + AccountsContactsColumns.CONTACT_ID + ")" + ");");
    }

    /**
     * Creates the accounts opportunities table.
     * 
     * @param db
     *            the db
     */
    private static void createAccountsOpportunitiesTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + ACCOUNTS_OPPORTUNITIES_TABLE_NAME + " ("
                + AccountsOpportunitiesColumns.ACCOUNT_ID + " INTEGER ,"
                + AccountsOpportunitiesColumns.OPPORTUNITY_ID + " INTEGER ,"
                + AccountsOpportunitiesColumns.DATE_MODIFIED + " TEXT,"
                + AccountsOpportunitiesColumns.DELETED + " INTEGER,"
                + " UNIQUE(" + AccountsOpportunitiesColumns.ACCOUNT_ID + ","
                + AccountsOpportunitiesColumns.OPPORTUNITY_ID + ")" + ");");
    }

    /**
     * Creates the accounts cases table.
     * 
     * @param db
     *            the db
     */
    private static void createAccountsCasesTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + ACCOUNTS_CASES_TABLE_NAME + " ("
                + AccountsCasesColumns.ACCOUNT_ID + " INTEGER ,"
                + AccountsCasesColumns.CASE_ID + " INTEGER ,"
                + AccountsCasesColumns.DATE_MODIFIED + " TEXT,"
                + AccountsCasesColumns.DELETED + " INTEGER," + " UNIQUE("
                + AccountsCasesColumns.ACCOUNT_ID + ","
                + AccountsCasesColumns.CASE_ID + ")" + ");");
    }

    /**
     * Creates the contacts opportunities table.
     * 
     * @param db
     *            the db
     */
    private static void createContactsOpportunitiesTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + CONTACTS_OPPORTUNITIES_TABLE_NAME + " ("
                + ContactsOpportunitiesColumns.CONTACT_ID + " INTEGER ,"
                + ContactsOpportunitiesColumns.OPPORTUNITY_ID + " INTEGER ,"
                + ContactsOpportunitiesColumns.DATE_MODIFIED + " TEXT,"
                + ContactsOpportunitiesColumns.DELETED + " INTEGER,"
                + " UNIQUE(" + ContactsOpportunitiesColumns.CONTACT_ID + ","
                + ContactsOpportunitiesColumns.OPPORTUNITY_ID + ")" + ");");
    }

    /**
     * Creates the contacts cases.
     * 
     * @param db
     *            the db
     */
    private static void createContactsCases(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + CONTACTS_CASES_TABLE_NAME + " ("
                + ContactsCasesColumns.CONTACT_ID + " INTEGER ,"
                + ContactsCasesColumns.CASE_ID + " INTEGER ,"
                + ContactsCasesColumns.DATE_MODIFIED + " TEXT,"
                + ContactsCasesColumns.DELETED + " INTEGER," + " UNIQUE("
                + ContactsCasesColumns.CONTACT_ID + ","
                + ContactsCasesColumns.CASE_ID + ")" + ");");
    }

    /**
     * Creates the sync table.
     * 
     * @param db
     *            the db
     */
    private static void createSyncTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + SYNC_TABLE_NAME + " (" + SyncColumns.ID
                + " INTEGER PRIMARY KEY," + SyncColumns.SYNC_ID + " INTEGER ,"
                + SyncColumns.SYNC_RELATED_ID + " INTEGER ,"
                + SyncColumns.SYNC_COMMAND + " INTEGER," + SyncColumns.MODULE
                + " TEXT," + SyncColumns.RELATED_MODULE + " TEXT,"
                + SyncColumns.DATE_MODIFIED + " TEXT,"
                + SyncColumns.SYNC_STATUS + " INTEGER," + " UNIQUE("
                + SyncColumns.SYNC_ID + "," + SyncColumns.MODULE + ","
                + SyncColumns.RELATED_MODULE + ")" + ");");
    }

    /**
     * Creates the users table.
     * 
     * @param db
     *            the db
     */
    private static void createUsersTable(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USERS_TABLE_NAME + " (" + UserColumns.ID
                + " INTEGER PRIMARY KEY," + UserColumns.USER_ID + " INTEGER,"
                + UserColumns.USER_NAME + " TEXT," + UserColumns.FIRST_NAME
                + " TEXT," + UserColumns.LAST_NAME + " TEXT," + " UNIQUE("
                + UserColumns.USER_NAME + ")" + ");");
    }

    /**
     * Creates the acl roles table.
     * 
     * @param db
     *            the db
     */
    private static void createAclRolesTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + ACL_ROLES_TABLE_NAME + " ("
                + ACLRoleColumns.ID + " INTEGER PRIMARY KEY,"
                + ACLRoleColumns.ROLE_ID + " INTEGER," + ACLRoleColumns.NAME
                + " TEXT," + ACLRoleColumns.TYPE + " TEXT,"
                + ACLRoleColumns.DESCRIPTION + " TEXT," + " UNIQUE("
                + ACLRoleColumns.ROLE_ID + ")" + ");");
    }

    /**
     * Creates the acl actions table.
     * 
     * @param db
     *            the db
     */
    private static void createAclActionsTable(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + ACL_ACTIONS_TABLE_NAME + " ("
                + ACLActionColumns.ID + " INTEGER PRIMARY KEY,"
                + ACLActionColumns.ACTION_ID + " INTEGER,"
                + ACLActionColumns.NAME + " INTEGER,"
                + ACLActionColumns.CATEGORY + " TEXT,"
                + ACLActionColumns.ACLACCESS + " TEXT,"
                + ACLActionColumns.ACLTYPE + " TEXT,"
                + ACLActionColumns.ROLE_ID + " INTEGER," + " UNIQUE("
                + ACLActionColumns.ACTION_ID + ")" + ");");
    }

    /**
     * Sets the acl access map.
     */
    private void setAclAccessMap() {
        // get the module list
        final List<String> moduleNames = ContentUtils.getModuleList(mContext);

        final SQLiteDatabase db = getReadableDatabase();
        for (final String moduleName : moduleNames) {
            final String selection = "(" + ACLActionColumns.CATEGORY + "= '"
                    + moduleName + "')";
            final Cursor cursor = db.query(
                    DatabaseHelper.ACL_ACTIONS_TABLE_NAME,
                    ACLActions.DETAILS_PROJECTION, selection, null, null, null,
                    null);
            cursor.moveToFirst();

            // access map for each module
            final Map<String, Integer> moduleAccessMap = new HashMap<String, Integer>();
            for (int i = 0; i < cursor.getCount(); i++) {
                final String name = cursor.getString(2);
                final String category = cursor.getString(3);
                final int aclAccess = cursor.getInt(4);
                final String aclType = cursor.getString(5);

                moduleAccessMap.put(name, aclAccess);
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, name + " " + category + " " + aclAccess + " "
                            + aclType);
                }

                cursor.moveToNext();
            }
            if (moduleAccessMap.size() > 0) {
                accessMap.put(moduleName, moduleAccessMap);
            }
            cursor.close();
        }
    }

    /**
     * Gets the acl access map.
     * 
     * @return the acl access map
     */
    private Map<String, Map<String, Integer>> getAclAccessMap() {
        if (accessMap != null && accessMap.size() != 0)
            return accessMap;
        else {
            setAclAccessMap();
            return accessMap;
        }
    }

    /**
     * <p>
     * isAclEnabled
     * </p>
     * .
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param name
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean isAclEnabled(final String moduleName, final String name) {
        return isAclEnabled(moduleName, name, null);
    }

    /**
     * <p>
     * isAclEnabled
     * </p>
     * .
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param name
     *            a {@link java.lang.String} object.
     * @param ownerName
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean isAclEnabled(final String moduleName, final String name,
            final String ownerName) {
        final Map<String, Map<String, Integer>> aclAccessMap = getAclAccessMap();
        // TODO - checkk if syncd ACLRoles and Actions succesfully- if no roles
        // are given to a user,
        // then we give access to the entire application
        if (aclAccessMap.size() == 0)
            return true;
        final Map<String, Integer> moduleAccessMap = aclAccessMap
                .get(moduleName);
        // if (moduleAccessMap == null || moduleAccessMap.size() == 0)
        // return true;
        final int aclAccess = moduleAccessMap.get(name);
        switch (aclAccess) {
        case ACLConstants.ACL_ALLOW_ADMIN:
            break;
        case ACLConstants.ACL_ALLOW_ADMIN_DEV:
            break;
        case ACLConstants.ACL_ALLOW_ALL:
            return true;
        case ACLConstants.ACL_ALLOW_DEFAULT:
            break;
        case ACLConstants.ACL_ALLOW_DEV:
            break;
        case ACLConstants.ACL_ALLOW_DISABLED:
            return false;
        case ACLConstants.ACL_ALLOW_ENABLED:
            return true;
        case ACLConstants.ACL_ALLOW_NONE:
            return false;
        case ACLConstants.ACL_ALLOW_NORMAL:
            break;
        case ACLConstants.ACL_ALLOW_OWNER:
            if (ownerName != null) {
                // TODO: get the user name from Account Manager
                final String userName = SugarCrmSettings.getUsername(mContext);
                return userName.equals(ownerName) ? true : false;
            } else
                return false;
        }
        return true;
    }

    /**
     * <p>
     * Getter for the field <code>fieldsExcludedForEdit</code>.
     * </p>
     * 
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getFieldsExcludedForEdit() {
        return fieldsExcludedForEdit;
    }

    /**
     * <p>
     * Getter for the field <code>fieldsExcludedForDetails</code>.
     * </p>
     * 
     * @param moduleName
     *            the module name
     * @param searchString
     *            the search string
     * @return a {@link java.util.Map} object.
     */
    /*
     * public Map<String, String> getFieldsExcludedForDetails() { return
     * fieldsExcludedForDetails; }
     */
    /**
     * <p>
     * Getter for the field <code>moduleProjections</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    /*
     * public String[] getModuleProjections(String moduleName) { return
     * moduleProjections.get(moduleName); }
     */

    /**
     * <p>
     * Getter for the field <code>moduleListProjections</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    /*
     * public String[] getModuleListProjections(String moduleName) { return
     * moduleListProjections.get(moduleName); }
     */

    /**
     * <p>
     * Getter for the field <code>moduleListSelections</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    // This moved to ContentUtil
    /*
     * public String[] getModuleListSelections(String moduleName) { return
     * moduleListSelections.get(moduleName); }
     */

    /**
     * <p>
     * Getter for the field <code>moduleSortOrder</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    /*
     * public String getModuleSortOrder(String moduleName) { return
     * moduleSortOrder.get(moduleName); }
     */

    /**
     * <p>
     * getModuleUri
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link android.net.Uri} object.
     */
    // this moved to ContentUtils
    /*
     * public Uri getModuleUri(String moduleName) { return
     * moduleUris.get(moduleName); }
     */
    /**
     * <p>
     * getModuleSelection
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param searchString
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getModuleSelection(final String moduleName,
            final String searchString) {
        // TODO: modify this if the selection criteria has to be applied on a
        // different module field
        // for a module
        if (moduleName.equals(Util.CONTACTS) || moduleName.equals(Util.LEADS))
            return "(" + LeadsColumns.FIRST_NAME + " LIKE '%" + searchString
                    + "%' OR " + LeadsColumns.LAST_NAME + " LIKE '%"
                    + searchString + "%'" + ")";
        else
            // for Accounts, Opportunities, Cases, Calls and Mettings
            return ModuleFields.NAME + " LIKE '%" + searchString + "%'";
    }

    // TODO - get from DB
    /**
     * <p>
     * Getter for the field <code>moduleRelationshipItems</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    /*
     * public String[] getModuleRelationshipItems(String moduleName) { return
     * moduleRelationshipItems.get(moduleName); }
     */
    /**
     * <p>
     * Getter for the field <code>relationshipTables</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getRelationshipTables(final String moduleName) {
        return relationshipTables.get(moduleName);
    }

    /**
     * <p>
     * Getter for the field <code>accountRelationsSelection</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getAccountRelationsSelection(final String moduleName) {
        return accountRelationsSelection.get(moduleName);
    }

    /**
     * <p>
     * Getter for the field <code>accountRelationsTableName</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getAccountRelationsTableName(final String moduleName) {
        return accountRelationsTableName.get(moduleName);
    }

    /**
     * <p>
     * getLinkfieldName
     * </p>
     * .
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getLinkfieldName(final String moduleName) {
        return linkfieldNames.get(moduleName);
    }

    /**
     * <p>
     * Getter for the field <code>linkFields</code>.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public Map<String, LinkField> getLinkFields(final String moduleName) {
        if (linkFields != null) {
            final HashMap<String, LinkField> map = linkFields.get(moduleName);
            if (map != null && map.size() > 0)
                return map;
        } else {
            linkFields = new HashMap<String, HashMap<String, LinkField>>();
        }
        final SQLiteDatabase db = getReadableDatabase();
        String selection = ModuleColumns.MODULE_NAME + "='" + moduleName + "'";
        Cursor cursor = db.query(MODULES_TABLE_NAME,
                Modules.DETAILS_PROJECTION, selection, null, null, null, null);
        cursor.moveToFirst();
        final String moduleId = cursor.getString(0);
        cursor.close();

        // name of the link field is the key and LinkField is the value
        final HashMap<String, LinkField> nameVsLinkField = new HashMap<String, LinkField>();
        selection = LinkFieldColumns.MODULE_ID + "=" + moduleId;
        cursor = db
                .query(LINK_FIELDS_TABLE_NAME,
                        com.imaginea.android.sugarcrm.provider.SugarCRMContent.LinkFields.DETAILS_PROJECTION,
                        selection, null, null, null, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            final String name = cursor.getString(1);
            final LinkField linkField = new LinkField(name,
                    cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5));
            cursor.moveToNext();
            nameVsLinkField.put(name, linkField);
        }
        cursor.close();
        db.close();
        linkFields.put(moduleName, nameVsLinkField);
        return nameVsLinkField;
    }

    /**
     * <p>
     * setUserModules
     * </p>
     * .
     * 
     * @param moduleFieldsInfo
     *            the new module fields info
     * @throws SugarCrmException
     *             the sugar crm exception
     */

    /**
     * <p>
     * setModuleFieldsInfo
     * </p>
     * 
     * @param moduleFieldsInfo
     *            a {@link java.util.Set} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public void setModuleFieldsInfo(final Set<Module> moduleFieldsInfo)
            throws SugarCrmException {
        boolean hasFailed = false;

        for (final Module module : moduleFieldsInfo) {
            // get module row id
            SQLiteDatabase db = getReadableDatabase();
            final String selection = ModuleColumns.MODULE_NAME + "='"
                    + module.getModuleName() + "'";
            final Cursor cursor = db.query(MODULES_TABLE_NAME,
                    Modules.DETAILS_PROJECTION, selection, null, null, null,
                    null);
            cursor.moveToFirst();
            final String moduleId = cursor.getString(0);
            cursor.close();
            db.close();

            db = getWritableDatabase();
            db.beginTransaction();
            final List<ModuleField> moduleFields = module.getModuleFields();
            for (final ModuleField moduleField : moduleFields) {
                final ContentValues values = new ContentValues();
                values.put(ModuleFieldColumns.NAME, moduleField.getName());
                values.put(ModuleFieldColumns.LABEL, moduleField.getLabel());
                values.put(ModuleFieldColumns.TYPE, moduleField.getType());
                values.put(ModuleFieldColumns.IS_REQUIRED,
                        moduleField.isRequired());
                values.put(ModuleFieldColumns.MODULE_ID, moduleId);
                final long rowId = db.insert(MODULE_FIELDS_TABLE_NAME, "",
                        values);
                if (rowId <= 0) {
                    hasFailed = true;
                    break;
                }
            }

            if (!hasFailed) {
                final List<LinkField> linkFields = module.getLinkFields();
                for (final LinkField linkField : linkFields) {
                    final ContentValues values = new ContentValues();
                    values.put(LinkFieldColumns.NAME, linkField.getName());
                    values.put(LinkFieldColumns.TYPE, linkField.getType());
                    values.put(LinkFieldColumns.RELATIONSHIP,
                            linkField.getRelationship());
                    values.put(LinkFieldColumns.MODULE, linkField.getModule());
                    values.put(LinkFieldColumns.BEAN_NAME,
                            linkField.getBeanName());
                    values.put(ModuleFieldColumns.MODULE_ID, moduleId);
                    final long rowId = db.insert(LINK_FIELDS_TABLE_NAME, "",
                            values);
                    if (rowId < 0) {
                        hasFailed = true;
                        break;
                    }
                }
            }

            if (hasFailed) {
                db.endTransaction();
                db.close();
                throw new SugarCrmException("FAILED to insert module fields!");
            } else {
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * get a Sync record given syncId and moduleName.
     * 
     * @param syncId
     *            a long.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link com.imaginea.android.sugarcrm.sync.SyncRecord} object.
     */
    public SyncRecord getSyncRecord(final long syncId, final String moduleName) {
        // TODO -currently we are storing module name in both the fields in
        // database if only orphans
        // are involved, if related items
        // if DB is switched to use null, then change this
        final String relatedModuleName = moduleName;
        return getSyncRecord(syncId, moduleName, relatedModuleName);
    }

    /**
     * get a Sync record given syncId and moduleName.
     * 
     * @param syncId
     *            a long.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param relatedModuleName
     *            a {@link java.lang.String} object.
     * @return a {@link com.imaginea.android.sugarcrm.sync.SyncRecord} object.
     */
    public SyncRecord getSyncRecord(final long syncId, final String moduleName,
            final String relatedModuleName) {
        SyncRecord record = null;
        final SQLiteDatabase db = getReadableDatabase();
        final String selection = Util.SYNC_ID + "=?" + " AND "
                + RestConstants.MODULE + "=?" + " AND " + Util.RELATED_MODULE
                + "=?";
        final String selectionArgs[] = new String[] { "" + syncId, moduleName,
                relatedModuleName };
        final Cursor cursor = db.query(SYNC_TABLE_NAME,
                Sync.DETAILS_PROJECTION, selection, selectionArgs, null, null,
                null);
        final int num = cursor.getCount();

        if (num > 0) {
            cursor.moveToFirst();
            record = new SyncRecord();
            record._id = cursor.getLong(Sync.ID_COLUMN);
            record.syncId = cursor.getLong(Sync.SYNC_ID_COLUMN);
            record.syncRelatedId = cursor.getLong(Sync.SYNC_RELATED_ID_COLUMN);
            record.syncCommand = cursor.getInt(Sync.SYNC_COMMAND_COLUMN);
            record.moduleName = cursor.getString(Sync.MODULE_NAME_COLUMN);
            record.relatedModuleName = cursor
                    .getString(Sync.RELATED_MODULE_NAME_COLUMN);
            record.status = cursor.getInt(Sync.STATUS_COLUMN);
        }
        cursor.close();
        db.close();

        return record;

    }

    /**
     * gets the unsynced sync records from the sync table.
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param status
     *            a int.
     * @return a {@link android.database.Cursor} object.
     */
    public Cursor getSyncRecords(final String moduleName, final int status) {
        final SQLiteDatabase db = getReadableDatabase();
        final String selection = RestConstants.MODULE + "=?" + " AND "
                + Util.STATUS + "=?";
        final String selectionArgs[] = new String[] { moduleName, "" + status };

        final Cursor cursor = db.query(DatabaseHelper.SYNC_TABLE_NAME,
                Sync.DETAILS_PROJECTION, selection, selectionArgs, null, null,
                null);
        return cursor;
    }

    /**
     * <p>
     * getConflictingSyncRecords
     * </p>
     * .
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link android.database.Cursor} object.
     */
    public Cursor getConflictingSyncRecords(final String moduleName) {
        return getSyncRecords(moduleName, Util.SYNC_CONFLICTS);
    }

    /**
     * <p>
     * getSyncRecordsToSync
     * </p>
     * .
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link android.database.Cursor} object.
     */
    public Cursor getSyncRecordsToSync(final String moduleName) {
        return getSyncRecords(moduleName, Util.UNSYNCED);
    }

    /**
     * <p>
     * getModuleProjectionInOrder
     * </p>
     * .
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public Map<String, ModuleFieldBean> getModuleProjectionInOrder(
            final String moduleName) {
        final int moduleId = getModuleId(moduleName);

        final Map<String, ModuleFieldBean> moduleFieldMap = new LinkedHashMap<String, ModuleFieldBean>();
        final SQLiteDatabase db = getReadableDatabase();

        String selection = ModuleFieldSortOrderColumns.MODULE_ID + "="
                + moduleId + "";
        // using the DETAILS_PROJECTION here to select the columns
        final Cursor cursor = db.query(
                DatabaseHelper.MODULE_FIELDS_SORT_ORDER_TABLE_NAME,
                ModuleFieldSortOrder.DETAILS_PROJECTION, selection, null, null,
                null, ModuleFieldSortOrder.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        // iterating through the module_fields_sort_order table in the ascending
        // sort order
        for (int i = 0; i < cursor.getCount(); i++) {
            final int sortId = cursor.getInt(1);
            final int groupId = cursor.getInt(2);
            final int moduleFieldId = cursor.getInt(3);

            // get the module field details from the module_fields table for the
            // moduleFieldId
            selection = ModuleFieldColumns.ID + "=" + moduleFieldId + "";
            final Cursor moduleFieldCursor = db.query(
                    DatabaseHelper.MODULE_FIELDS_TABLE_NAME,
                    ModuleFields_TableInfo.DETAILS_PROJECTION, selection, null,
                    null, null, null);
            moduleFieldCursor.moveToFirst();
            final String moduleFieldName = moduleFieldCursor.getString(1);
            final ModuleField moduleFieldObj = new ModuleField(moduleFieldName,
                    moduleFieldCursor.getString(3),
                    moduleFieldCursor.getString(2),
                    moduleFieldCursor.getInt(4) == 1 ? true : false);
            moduleFieldCursor.close();

            // create ModuleFieldBean to store the ModuleField, its sortOrder
            // and groupId
            final ModuleFieldBean moduleFieldBean = new ModuleFieldBean(
                    moduleFieldObj, moduleFieldId, sortId, groupId);
            moduleFieldMap.put(moduleFieldName, moduleFieldBean);

            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return moduleFieldMap;
    }

    /*
     * get the moduleId given the name of the module
     */
    /**
     * Gets the module id.
     * 
     * @param moduleName
     *            the module name
     * @return the module id
     */
    private int getModuleId(final String moduleName) {
        final SQLiteDatabase db = getReadableDatabase();
        final String selection = ModuleColumns.MODULE_NAME + "='" + moduleName
                + "'";
        // using the DETAILS_PROJECTION here to select the columns
        final Cursor cursor = db
                .query(DatabaseHelper.MODULES_TABLE_NAME,
                        com.imaginea.android.sugarcrm.provider.SugarCRMContent.Modules.DETAILS_PROJECTION,
                        selection, null, null, null, null);
        cursor.moveToFirst();
        final int moduleId = cursor.getInt(0);
        cursor.close();
        db.close();

        return moduleId;
    }

    /**
     * // TODO - when do we update ?? - not required -??.
     * 
     * @param record
     *            a {@link com.imaginea.android.sugarcrm.sync.SyncRecord}
     *            object.
     * @return a int.
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    public int updateSyncRecord(final SyncRecord record)
            throws SugarCrmException {

        final SQLiteDatabase db = getWritableDatabase();
        final ContentValues values = new ContentValues();
        // values.put(SyncColumns.ID, record._id);
        values.put(SyncColumns.SYNC_ID, record.syncId);
        values.put(SyncColumns.SYNC_RELATED_ID, record.syncRelatedId);
        // values.put(SyncColumns.SYNC_COMMAND, record.syncCommand);
        values.put(SyncColumns.MODULE, record.moduleName);
        values.put(SyncColumns.RELATED_MODULE, record.relatedModuleName);
        values.put(SyncColumns.SYNC_STATUS, record.status);

        final int rowId = db.update(SYNC_TABLE_NAME, values, SyncColumns.ID
                + "=?", new String[] { "" + record._id });
        if (rowId < 0)
            throw new SugarCrmException("FAILED to update sync record!");
        return rowId;
    }

    /**
     * updateSyncRecord.
     * 
     * @param syncRecordId
     *            a long.
     * @param values
     *            a {@link android.content.ContentValues} object.
     * @return a int.
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    public int updateSyncRecord(final long syncRecordId,
            final ContentValues values) throws SugarCrmException {
        final SQLiteDatabase db = getWritableDatabase();
        final int rowId = db.update(SYNC_TABLE_NAME, values, SyncColumns.ID
                + "=?", new String[] { "" + syncRecordId });
        if (rowId < 0)
            throw new SugarCrmException("FAILED to update sync record!");
        return rowId;
    }

    /**
     * <p>
     * insertSyncRecord
     * </p>
     * .
     * 
     * @param record
     *            a {@link com.imaginea.android.sugarcrm.sync.SyncRecord}
     *            object.
     * @return a long.
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    public long insertSyncRecord(final SyncRecord record)
            throws SugarCrmException {

        final SQLiteDatabase db = getWritableDatabase();
        final ContentValues values = new ContentValues();
        // values.put(SyncColumns.ID, record._id);
        values.put(SyncColumns.SYNC_ID, record.syncId);
        values.put(SyncColumns.SYNC_RELATED_ID, record.syncRelatedId);
        values.put(SyncColumns.SYNC_COMMAND, record.syncCommand);
        values.put(SyncColumns.MODULE, record.moduleName);
        values.put(SyncColumns.RELATED_MODULE, record.relatedModuleName);
        values.put(SyncColumns.SYNC_STATUS, record.status);

        final long rowId = db.insert(SYNC_TABLE_NAME, "", values);
        if (rowId < 0)
            throw new SugarCrmException("FAILED to insert sync record!");
        return rowId;
    }

    /**
     * deletes a sync record based on the syncRecdordId (_id).
     * 
     * @param syncRecordId
     *            a long.
     * @return a int.
     */
    public int deleteSyncRecord(final long syncRecordId) {
        final SQLiteDatabase db = getWritableDatabase();
        // String accountId = uri.getPathSegments().get(1);
        final int count = db.delete(DatabaseHelper.SYNC_TABLE_NAME,
                SyncColumns.ID + "=" + syncRecordId, null);
        // + (!TextUtils.isEmpty(where) ? " AND (" + where + ')'
        // : ""), whereArgs);
        return count;
    }

    /**
     * <p>
     * insertActions
     * </p>
     * .
     * 
     * @param roleId
     *            a {@link java.lang.String} object.
     * @param roleRelationBeans
     *            an array of
     * @throws SugarCrmException
     *             the sugar crm exception
     *             {@link com.imaginea.android.sugarcrm.rest.SugarBean} objects.
     */
    public void insertActions(final String roleId,
            final SugarBean[] roleRelationBeans) throws SugarCrmException {
        boolean hasFailed = false;

        final SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        for (final SugarBean actionBean : roleRelationBeans) {

            final ContentValues values = new ContentValues();
            final String[] aclActionFields = ACLActions.INSERT_PROJECTION;
            for (int i = 0; i < aclActionFields.length; i++) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, actionBean.getFieldValue(aclActionFields[i]));
                }

                values.put(aclActionFields[i],
                        actionBean.getFieldValue(aclActionFields[i]));
            }

            // get the row id of the role
            final String selection = ACLRoleColumns.ROLE_ID + "='" + roleId
                    + "'";
            final Cursor cursor = db.query(DatabaseHelper.ACL_ROLES_TABLE_NAME,
                    ACLRoles.DETAILS_PROJECTION, selection, null, null, null,
                    null);
            cursor.moveToFirst();
            final int roleRowId = cursor.getInt(0);
            cursor.close();

            values.put(ACLActionColumns.ROLE_ID, roleRowId);
            final long rowId = db.insert(ACL_ACTIONS_TABLE_NAME, "", values);
            if (rowId < 0) {
                hasFailed = true;
                break;
            }
        }
        if (hasFailed) {
            db.endTransaction();
            db.close();
            throw new SugarCrmException("FAILED to insert ACL Actions!");
        } else {
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }
    }

    /**
     * <p>
     * insertRoles
     * </p>
     * .
     * 
     * @param roleBeans
     *            an array of
     * @return a {@link java.util.List} object.
     * @throws SugarCrmException
     *             the sugar crm exception
     *             {@link com.imaginea.android.sugarcrm.rest.SugarBean} objects.
     */
    public List<String> insertRoles(final SugarBean[] roleBeans)
            throws SugarCrmException {
        boolean hasFailed = false;

        final List<String> roleIds = new ArrayList<String>();
        final SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < roleBeans.length; i++) {
            final ContentValues values = new ContentValues();
            for (final String fieldName : ACLRoles.INSERT_PROJECTION) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG,
                            fieldName + " : "
                                    + roleBeans[i].getFieldValue(fieldName));
                }

                if (fieldName.equals(ModuleFields.ID)) {
                    roleIds.add(roleBeans[i].getFieldValue(fieldName));
                }
                values.put(fieldName, roleBeans[i].getFieldValue(fieldName));
            }
            final long rowId = db.insert(ACL_ROLES_TABLE_NAME, "", values);
            if (rowId < 0) {
                hasFailed = true;
                break;
            }
        }

        if (hasFailed) {
            db.close();
            throw new SugarCrmException("FAILED to insert ACL Roles!");
        } else {
            db.close();
        }

        return roleIds;
    }

    // key : userName value: userValues
    /**
     * <p>
     * insertUsers
     * </p>
     * .
     * 
     * @param usersList
     *            a {@link java.util.Map} object.
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    public void insertUsers(final Map<String, Map<String, String>> usersList)
            throws SugarCrmException {
        boolean hasFailed = false;

        final SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        for (final Entry<String, Map<String, String>> entry : usersList
                .entrySet()) {
            // String userName = entry.getKey();
            final ContentValues values = new ContentValues();
            final Map<String, String> userListValues = entry.getValue();
            for (final Entry<String, String> userEntry : userListValues
                    .entrySet()) {
                values.put(userEntry.getKey(), userEntry.getValue());
            }
            final long rowId = db.insert(USERS_TABLE_NAME, "", values);
            if (rowId < 0) {
                hasFailed = true;
                break;
            }
        }
        if (hasFailed) {
            db.endTransaction();
            db.close();
            throw new SugarCrmException("FAILED to insert Users!");
        } else {
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }
    }

    /**
     * executes SQL statements from a SQL file name present in assets folder.
     * 
     * @param fileName
     *            a {@link java.lang.String} object.
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    public void executeSQLFromFile(final String fileName)
            throws SugarCrmException {
        final SQLiteDatabase db = getWritableDatabase();
        try {
            final InputStream is = mContext.getAssets().open(fileName);
            db.beginTransaction();
            /*
             * Use the openFileInput() method the ActivityContext provides.
             * Again for security reasons with openFileInput(...)
             */
            final BufferedReader br = new BufferedReader(new InputStreamReader(
                    is));
            String sql;
            while ((sql = br.readLine()) != null) {
                db.execSQL(sql);
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "read from file: " + sql);
                }
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (final Exception e) {
            throw new SugarCrmException("FAILED to execute SQL from file");
        }
        db.close();
    }

    /**
     * Returns the beanId id , or null if the item is not found.
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param rowId
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String lookupBeanId(final String moduleName, final String rowId) {
        final ContentResolver resolver = mContext.getContentResolver();
        String beanId = null;
        final Uri contentUri = ContentUtils.getModuleUri(moduleName);
        final String[] projection = new String[] { SugarCRMContent.SUGAR_BEAN_ID };

        final Cursor c = resolver.query(contentUri, projection, mSelection,
                new String[] { rowId }, null);
        try {
            if (c.moveToFirst()) {
                beanId = c.getString(0);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return beanId;
    }

    /**
     * Returns the corresponding account beanId id , or null if the item is not
     * found.
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param rowId
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String lookupAccountBeanId(final String moduleName,
            final String rowId) {

        final ContentResolver resolver = mContext.getContentResolver();
        String beanId = null;
        final Uri contentUri = ContentUtils.getModuleUri(moduleName);
        final String[] projection = new String[] { SugarCRMContent.SUGAR_BEAN_ID };

        final Cursor c = resolver.query(contentUri, projection, mSelection,
                new String[] { rowId }, null);
        try {
            if (c.moveToFirst()) {
                beanId = c.getString(0);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return beanId;
    }

    /**
     * Lookup user bean id.
     * 
     * @param userBeanName
     *            the user bean name
     * @return the string
     */
    public String lookupUserBeanId(final String userBeanName) {
        final ContentResolver resolver = mContext.getContentResolver();
        String beanId = null;
        final Uri contentUri = ContentUtils.getModuleUri(Util.USERS);
        final String[] projection = new String[] { ModuleFields.ID };
        final String selection = UserColumns.USER_NAME + "='" + userBeanName
                + "'";
        final Cursor c = resolver.query(contentUri, projection, selection,
                null, null);
        try {
            if (c.moveToFirst()) {
                beanId = c.getString(0);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return beanId;
    }
}
