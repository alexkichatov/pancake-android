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
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ModuleFieldsTableInfo;
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

    /** The Constant INTEGER_COMMA. */
    private static final String INTEGER_COMMA = " INTEGER,";

    /** The Constant TEXT. */
    private static final String TEXT_COMMA = " TEXT,";

    /** The Constant INTEGER_PRIMARY_KEY. */
    private static final String INTEGER_PRIMARY_KEY = " INTEGER PRIMARY KEY,";

    /** The Constant INTEGER. */
    private static final String INTEGER = " INTEGER";

    /** The Constant CREATE_TABLE. */
    private static final String CREATE_TABLE = "CREATE TABLE ";

    /** The Constant DROPTABLE_IF_EXIST. */
    private static final String DROPTABLE_IF_EXIST = "DROP TABLE IF EXISTS ";

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
    private static Map<String, HashMap<String, LinkField>> linkFields;

    /** The Constant relationshipTables. */
    private static Map<String, String[]> relationshipTables = new HashMap<String, String[]>();

    /** The Constant accountRelationsSelection. */
    private static Map<String, String> accountRelationsSelection = new HashMap<String, String>();

    /** The Constant accountRelationsTableName. */
    private static Map<String, String> accountRelationsTableName = new HashMap<String, String>();

    /** The Constant linkfieldNames. */
    private static Map<String, String> linkfieldNames = new HashMap<String, String>();

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
        db.execSQL(DROPTABLE_IF_EXIST + ACCOUNTS_TABLE_NAME);
    }

    /**
     * Drop contacts table.
     * 
     * @param db
     *            the db
     */
    void dropContactsTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + CONTACTS_TABLE_NAME);
    }

    /**
     * Drop leads table.
     * 
     * @param db
     *            the db
     */
    void dropLeadsTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + LEADS_TABLE_NAME);
    }

    /**
     * Drop opportunities table.
     * 
     * @param db
     *            the db
     */
    void dropOpportunitiesTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + OPPORTUNITIES_TABLE_NAME);
    }

    /**
     * Drop cases table.
     * 
     * @param db
     *            the db
     */
    void dropCasesTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + CASES_TABLE_NAME);
    }

    /**
     * Drop calls table.
     * 
     * @param db
     *            the db
     */
    void dropCallsTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + CALLS_TABLE_NAME);
    }

    /**
     * Drop meetings table.
     * 
     * @param db
     *            the db
     */
    void dropMeetingsTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + MEETINGS_TABLE_NAME);
    }

    /**
     * Drop campaigns table.
     * 
     * @param db
     *            the db
     */
    void dropCampaignsTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + CAMPAIGNS_TABLE_NAME);
    }

    /**
     * Drop modules table.
     * 
     * @param db
     *            the db
     */
    void dropModulesTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + MODULES_TABLE_NAME);
    }

    /**
     * Drop module fields table.
     * 
     * @param db
     *            the db
     */
    void dropModuleFieldsTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + MODULE_FIELDS_TABLE_NAME);
    }

    /**
     * Drop link fields table.
     * 
     * @param db
     *            the db
     */
    void dropLinkFieldsTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + LINK_FIELDS_TABLE_NAME);
    }

    /**
     * Drop accounts contacts table.
     * 
     * @param db
     *            the db
     */
    void dropAccountsContactsTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + ACCOUNTS_CONTACTS_TABLE_NAME);
    }

    /**
     * Drop accounts cases table.
     * 
     * @param db
     *            the db
     */
    void dropAccountsCasesTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + ACCOUNTS_CASES_TABLE_NAME);
    }

    /**
     * Drop accounts opportunities table.
     * 
     * @param db
     *            the db
     */
    void dropAccountsOpportunitiesTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + ACCOUNTS_OPPORTUNITIES_TABLE_NAME);
    }

    /**
     * Drop contacts opportunities table.
     * 
     * @param db
     *            the db
     */
    void dropContactsOpportunitiesTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + CONTACTS_OPPORTUNITIES_TABLE_NAME);
    }

    /**
     * Drop contacts cases table.
     * 
     * @param db
     *            the db
     */
    void dropContactsCasesTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + CONTACTS_CASES_TABLE_NAME);
    }

    /**
     * Drop sync table.
     * 
     * @param db
     *            the db
     */
    void dropSyncTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + SYNC_TABLE_NAME);
    }

    /**
     * Drop users table.
     * 
     * @param db
     *            the db
     */
    void dropUsersTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + USERS_TABLE_NAME);
    }

    /**
     * Drop acl roles table.
     * 
     * @param db
     *            the db
     */
    void dropAclRolesTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + ACL_ROLES_TABLE_NAME);
    }

    /**
     * Drop acl actions table.
     * 
     * @param db
     *            the db
     */
    void dropAclActionsTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + ACL_ACTIONS_TABLE_NAME);
    }

    /**
     * Drop module fields sort order table.
     * 
     * @param db
     *            the db
     */
    void dropModuleFieldsSortOrderTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + MODULE_FIELDS_SORT_ORDER_TABLE_NAME);
    }

    /**
     * Drop module fields group table.
     * 
     * @param db
     *            the db
     */
    void dropModuleFieldsGroupTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + MODULE_FIELDS_GROUP_TABLE_NAME);
    }

    /**
     * Drop recent table.
     * 
     * @param db
     *            the db
     */
    void dropRecentTable(final SQLiteDatabase db) {
        db.execSQL(DROPTABLE_IF_EXIST + RECENT_TABLE_NAME);
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

        db.execSQL(CREATE_TABLE + ALARM_TABLE_NAME + " (" + AlarmColumns.ID
                + INTEGER_COMMA + AlarmColumns.ALARM_STATE + INTEGER + ");");
    }

    /**
     * Creates the recent table.
     * 
     * @param db
     *            the db
     */
    private static void createRecentTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + RECENT_TABLE_NAME + " (" + RecentColumns.ID
                + INTEGER_COMMA + RecentColumns.ACTUAL_ID + INTEGER_COMMA
                + RecentColumns.BEAN_ID + TEXT_COMMA
                + RecentColumns.REF_MODULE_NAME + TEXT_COMMA
                + RecentColumns.NAME_1 + TEXT_COMMA + RecentColumns.NAME_2
                + TEXT_COMMA + RecentColumns.DELETED + INTEGER + ");");

    }

    /**
     * Creates the accounts table.
     * 
     * @param db
     *            the db
     */
    private static void createAccountsTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + ACCOUNTS_TABLE_NAME + " ("
                + AccountsColumns.ID + INTEGER_PRIMARY_KEY
                + AccountsColumns.BEAN_ID + TEXT_COMMA + AccountsColumns.NAME
                + TEXT_COMMA + AccountsColumns.EMAIL1 + TEXT_COMMA
                + AccountsColumns.PARENT_NAME + TEXT_COMMA
                + AccountsColumns.PHONE_OFFICE + TEXT_COMMA
                + AccountsColumns.PHONE_FAX + TEXT_COMMA
                + AccountsColumns.WEBSITE + TEXT_COMMA
                + AccountsColumns.EMPLOYEES + TEXT_COMMA
                + AccountsColumns.TICKER_SYMBOL + TEXT_COMMA
                + AccountsColumns.ANNUAL_REVENUE + TEXT_COMMA
                + AccountsColumns.BILLING_ADDRESS_STREET + TEXT_COMMA
                + AccountsColumns.BILLING_ADDRESS_STREET_2 + TEXT_COMMA
                + AccountsColumns.BILLING_ADDRESS_STREET_3 + TEXT_COMMA
                + AccountsColumns.BILLING_ADDRESS_STREET_4 + TEXT_COMMA
                + AccountsColumns.BILLING_ADDRESS_CITY + TEXT_COMMA
                + AccountsColumns.BILLING_ADDRESS_STATE + TEXT_COMMA
                + AccountsColumns.BILLING_ADDRESS_POSTALCODE + TEXT_COMMA
                + AccountsColumns.BILLING_ADDRESS_COUNTRY + TEXT_COMMA
                + AccountsColumns.SHIPPING_ADDRESS_STREET + TEXT_COMMA
                + AccountsColumns.SHIPPING_ADDRESS_STREET_2 + TEXT_COMMA
                + AccountsColumns.SHIPPING_ADDRESS_STREET_3 + TEXT_COMMA
                + AccountsColumns.SHIPPING_ADDRESS_STREET_4 + TEXT_COMMA
                + AccountsColumns.SHIPPING_ADDRESS_CITY + TEXT_COMMA
                + AccountsColumns.SHIPPING_ADDRESS_STATE + TEXT_COMMA
                + AccountsColumns.SHIPPING_ADDRESS_POSTALCODE + TEXT_COMMA
                + AccountsColumns.SHIPPING_ADDRESS_COUNTRY + TEXT_COMMA
                + AccountsColumns.ASSIGNED_USER_NAME + TEXT_COMMA
                + AccountsColumns.CREATED_BY_NAME + TEXT_COMMA
                + AccountsColumns.DATE_ENTERED + TEXT_COMMA
                + AccountsColumns.DATE_MODIFIED + TEXT_COMMA
                + AccountsColumns.DELETED + INTEGER_COMMA + " UNIQUE("
                + AccountsColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the contacts table.
     * 
     * @param db
     *            the db
     */
    private static void createContactsTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + CONTACTS_TABLE_NAME + " ("
                + ContactsColumns.ID + INTEGER_PRIMARY_KEY
                + ContactsColumns.BEAN_ID + TEXT_COMMA
                + ContactsColumns.FIRST_NAME + TEXT_COMMA
                + ContactsColumns.LAST_NAME + TEXT_COMMA
                + ContactsColumns.ACCOUNT_NAME + TEXT_COMMA
                + ContactsColumns.PHONE_MOBILE + TEXT_COMMA
                + ContactsColumns.PHONE_WORK + TEXT_COMMA
                + ContactsColumns.EMAIL1 + TEXT_COMMA
                + ContactsColumns.ASSIGNED_USER_NAME + TEXT_COMMA
                + ContactsColumns.CREATED_BY + TEXT_COMMA
                + ContactsColumns.MODIFIED_BY_NAME + TEXT_COMMA
                + ContactsColumns.CREATED_BY_NAME + TEXT_COMMA
                + ContactsColumns.DATE_ENTERED + TEXT_COMMA
                + ContactsColumns.DATE_MODIFIED + TEXT_COMMA
                + ContactsColumns.DELETED + INTEGER_COMMA
                + ContactsColumns.ACCOUNT_ID + INTEGER_COMMA + " UNIQUE("
                + ContactsColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the leads table.
     * 
     * @param db
     *            the db
     */
    private static void createLeadsTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + LEADS_TABLE_NAME + " (" + LeadsColumns.ID
                + INTEGER_PRIMARY_KEY + LeadsColumns.BEAN_ID + TEXT_COMMA
                + LeadsColumns.FIRST_NAME + TEXT_COMMA + LeadsColumns.LAST_NAME
                + TEXT_COMMA + LeadsColumns.LEAD_SOURCE + TEXT_COMMA
                + LeadsColumns.EMAIL1 + TEXT_COMMA + LeadsColumns.PHONE_WORK
                + TEXT_COMMA + LeadsColumns.PHONE_FAX + TEXT_COMMA
                + LeadsColumns.ACCOUNT_NAME + TEXT_COMMA + LeadsColumns.TITLE
                + TEXT_COMMA + LeadsColumns.ASSIGNED_USER_NAME + TEXT_COMMA
                + LeadsColumns.CREATED_BY_NAME + TEXT_COMMA
                + LeadsColumns.DATE_ENTERED + TEXT_COMMA
                + LeadsColumns.DATE_MODIFIED + TEXT_COMMA
                + LeadsColumns.DELETED + INTEGER_COMMA
                + LeadsColumns.ACCOUNT_ID + INTEGER_COMMA + " UNIQUE("
                + LeadsColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the opportunities table.
     * 
     * @param db
     *            the db
     */
    private static void createOpportunitiesTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + OPPORTUNITIES_TABLE_NAME + " ("
                + OpportunitiesColumns.ID + INTEGER_PRIMARY_KEY
                + OpportunitiesColumns.BEAN_ID + TEXT_COMMA
                + OpportunitiesColumns.NAME + TEXT_COMMA
                + OpportunitiesColumns.ACCOUNT_NAME + TEXT_COMMA
                + OpportunitiesColumns.AMOUNT + TEXT_COMMA
                + OpportunitiesColumns.AMOUNT_USDOLLAR + TEXT_COMMA
                + OpportunitiesColumns.ASSIGNED_USER_ID + TEXT_COMMA
                + OpportunitiesColumns.ASSIGNED_USER_NAME + TEXT_COMMA
                + OpportunitiesColumns.CAMPAIGN_NAME + TEXT_COMMA
                + OpportunitiesColumns.CREATED_BY + TEXT_COMMA
                + OpportunitiesColumns.CREATED_BY_NAME + TEXT_COMMA
                + OpportunitiesColumns.CURRENCY_ID + TEXT_COMMA
                + OpportunitiesColumns.CURRENCY_NAME + TEXT_COMMA
                + OpportunitiesColumns.CURRENCY_SYMBOL + TEXT_COMMA
                + OpportunitiesColumns.DATE_CLOSED + TEXT_COMMA
                + OpportunitiesColumns.DATE_ENTERED + TEXT_COMMA
                + OpportunitiesColumns.DATE_MODIFIED + TEXT_COMMA
                + OpportunitiesColumns.DESCRIPTION + TEXT_COMMA
                + OpportunitiesColumns.LEAD_SOURCE + TEXT_COMMA
                + OpportunitiesColumns.MODIFIED_BY_NAME + TEXT_COMMA
                + OpportunitiesColumns.MODIFIED_USER_ID + TEXT_COMMA
                + OpportunitiesColumns.NEXT_STEP + TEXT_COMMA
                + OpportunitiesColumns.OPPORTUNITY_TYPE + TEXT_COMMA
                + OpportunitiesColumns.PROBABILITY + TEXT_COMMA
                + OpportunitiesColumns.SALES_STAGE + TEXT_COMMA
                + OpportunitiesColumns.DELETED + INTEGER_COMMA
                + OpportunitiesColumns.ACCOUNT_ID + INTEGER_COMMA + " UNIQUE("
                + OpportunitiesColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the cases table.
     * 
     * @param db
     *            the db
     */
    private static void createCasesTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + CASES_TABLE_NAME + " (" + CasesColumns.ID
                + INTEGER_PRIMARY_KEY + CasesColumns.BEAN_ID + TEXT_COMMA
                + CasesColumns.NAME + TEXT_COMMA + CasesColumns.ACCOUNT_NAME
                + TEXT_COMMA + CasesColumns.CASE_NUMBER + TEXT_COMMA
                + CasesColumns.PRIORITY + TEXT_COMMA
                + CasesColumns.ASSIGNED_USER_NAME + TEXT_COMMA
                + CasesColumns.STATUS + TEXT_COMMA + CasesColumns.DESCRIPTION
                + TEXT_COMMA + CasesColumns.RESOLUTION + TEXT_COMMA
                + CasesColumns.CREATED_BY_NAME + TEXT_COMMA
                + CasesColumns.DATE_ENTERED + TEXT_COMMA
                + CasesColumns.DATE_MODIFIED + TEXT_COMMA
                + CasesColumns.DELETED + INTEGER_COMMA + " UNIQUE("
                + CasesColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the calls table.
     * 
     * @param db
     *            the db
     */
    private static void createCallsTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + CALLS_TABLE_NAME + " (" + CallsColumns.ID
                + INTEGER_PRIMARY_KEY + CallsColumns.BEAN_ID + TEXT_COMMA
                + CallsColumns.NAME + TEXT_COMMA + CallsColumns.ACCOUNT_NAME
                + TEXT_COMMA + CallsColumns.STATUS + TEXT_COMMA
                + CallsColumns.START_DATE + TEXT_COMMA
                + CallsColumns.DURATION_HOURS + TEXT_COMMA
                + CallsColumns.DURATION_MINUTES + TEXT_COMMA
                + CallsColumns.ASSIGNED_USER_NAME + TEXT_COMMA
                + CallsColumns.DESCRIPTION + TEXT_COMMA
                + CallsColumns.CREATED_BY_NAME + TEXT_COMMA
                + CallsColumns.DATE_ENTERED + TEXT_COMMA
                + CallsColumns.DATE_MODIFIED + TEXT_COMMA
                + CallsColumns.DELETED + INTEGER_COMMA + " UNIQUE("
                + CallsColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the meetings table.
     * 
     * @param db
     *            the db
     */
    private static void createMeetingsTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + MEETINGS_TABLE_NAME + " ("
                + MeetingsColumns.ID + INTEGER_PRIMARY_KEY
                + MeetingsColumns.BEAN_ID + TEXT_COMMA + MeetingsColumns.NAME
                + TEXT_COMMA + MeetingsColumns.ACCOUNT_NAME + TEXT_COMMA
                + MeetingsColumns.STATUS + TEXT_COMMA
                + MeetingsColumns.LOCATION + TEXT_COMMA
                + MeetingsColumns.START_DATE + TEXT_COMMA
                + MeetingsColumns.DURATION_HOURS + TEXT_COMMA
                + MeetingsColumns.DURATION_MINUTES + TEXT_COMMA
                + MeetingsColumns.ASSIGNED_USER_NAME + TEXT_COMMA
                + MeetingsColumns.DESCRIPTION + TEXT_COMMA
                + MeetingsColumns.CREATED_BY_NAME + TEXT_COMMA
                + MeetingsColumns.DATE_ENTERED + TEXT_COMMA
                + MeetingsColumns.DATE_MODIFIED + TEXT_COMMA
                + MeetingsColumns.DELETED + INTEGER_COMMA + " UNIQUE("
                + MeetingsColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the campaigns table.
     * 
     * @param db
     *            the db
     */
    private static void createCampaignsTable(final SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE + CAMPAIGNS_TABLE_NAME + " ("
                + CampaignColumns.ID + INTEGER_PRIMARY_KEY
                + CampaignColumns.BEAN_ID + TEXT_COMMA + CampaignColumns.NAME
                + TEXT_COMMA + CampaignColumns.STATUS + TEXT_COMMA
                + CampaignColumns.START_DATE + TEXT_COMMA
                + CampaignColumns.END_DATE + TEXT_COMMA
                + CampaignColumns.CAMPAIGN_TYPE + TEXT_COMMA
                + CampaignColumns.BUDGET + TEXT_COMMA
                + CampaignColumns.ACTUAL_COST + TEXT_COMMA
                + CampaignColumns.EXPECTED_COST + TEXT_COMMA
                + CampaignColumns.EXPECTED_REVENUE + TEXT_COMMA
                + CampaignColumns.IMPRESSIONS + TEXT_COMMA
                + CampaignColumns.OBJECTIVE + TEXT_COMMA
                + CampaignColumns.FREQUENCY + TEXT_COMMA
                + CampaignColumns.ASSIGNED_USER_NAME + TEXT_COMMA
                + CampaignColumns.DESCRIPTION + TEXT_COMMA
                + CampaignColumns.CREATED_BY_NAME + TEXT_COMMA
                + CampaignColumns.DATE_ENTERED + TEXT_COMMA
                + CampaignColumns.DATE_MODIFIED + TEXT_COMMA
                + CampaignColumns.DELETED + INTEGER_COMMA + " UNIQUE("
                + MeetingsColumns.BEAN_ID + ")" + ");");
    }

    /**
     * Creates the modules table.
     * 
     * @param db
     *            the db
     */
    private static void createModulesTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + MODULES_TABLE_NAME + " (" + ModuleColumns.ID
                + INTEGER_PRIMARY_KEY + ModuleColumns.MODULE_NAME + TEXT_COMMA
                + ModuleColumns.LAST_SYNC_TIME + TEXT_COMMA + " UNIQUE("
                + ModuleColumns.MODULE_NAME + ")" + ");");
    }

    /**
     * Creates the module fields table.
     * 
     * @param db
     *            the db
     */
    private static void createModuleFieldsTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + MODULE_FIELDS_TABLE_NAME + " ("
                + ModuleFieldColumns.ID + INTEGER_PRIMARY_KEY
                + ModuleFieldColumns.NAME + TEXT_COMMA
                + ModuleFieldColumns.LABEL + TEXT_COMMA
                + ModuleFieldColumns.TYPE + TEXT_COMMA
                + ModuleFieldColumns.IS_REQUIRED + INTEGER_COMMA
                + ModuleFieldColumns.MODULE_ID + INTEGER + ");");
    }

    /**
     * Creates the link fields table.
     * 
     * @param db
     *            the db
     */
    private static void createLinkFieldsTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + LINK_FIELDS_TABLE_NAME + " ("
                + LinkFieldColumns.ID + INTEGER_PRIMARY_KEY
                + LinkFieldColumns.NAME + TEXT_COMMA + LinkFieldColumns.TYPE
                + TEXT_COMMA + LinkFieldColumns.RELATIONSHIP + TEXT_COMMA
                + LinkFieldColumns.MODULE + TEXT_COMMA
                + LinkFieldColumns.BEAN_NAME + TEXT_COMMA
                + LinkFieldColumns.MODULE_ID + INTEGER + ");");
    }

    /**
     * Creates the accounts contacts table.
     * 
     * @param db
     *            the db
     */
    private static void createAccountsContactsTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + ACCOUNTS_CONTACTS_TABLE_NAME + " ("
                + AccountsContactsColumns.ACCOUNT_ID + " INTEGER ,"
                + AccountsContactsColumns.CONTACT_ID + " INTEGER ,"
                + AccountsContactsColumns.DATE_MODIFIED + TEXT_COMMA
                + AccountsContactsColumns.DELETED + INTEGER_COMMA + " UNIQUE("
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

        db.execSQL(CREATE_TABLE + ACCOUNTS_OPPORTUNITIES_TABLE_NAME + " ("
                + AccountsOpportunitiesColumns.ACCOUNT_ID + " INTEGER ,"
                + AccountsOpportunitiesColumns.OPPORTUNITY_ID + " INTEGER ,"
                + AccountsOpportunitiesColumns.DATE_MODIFIED + TEXT_COMMA
                + AccountsOpportunitiesColumns.DELETED + INTEGER_COMMA
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

        db.execSQL(CREATE_TABLE + ACCOUNTS_CASES_TABLE_NAME + " ("
                + AccountsCasesColumns.ACCOUNT_ID + " INTEGER ,"
                + AccountsCasesColumns.CASE_ID + " INTEGER ,"
                + AccountsCasesColumns.DATE_MODIFIED + TEXT_COMMA
                + AccountsCasesColumns.DELETED + INTEGER_COMMA + " UNIQUE("
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

        db.execSQL(CREATE_TABLE + CONTACTS_OPPORTUNITIES_TABLE_NAME + " ("
                + ContactsOpportunitiesColumns.CONTACT_ID + " INTEGER ,"
                + ContactsOpportunitiesColumns.OPPORTUNITY_ID + " INTEGER ,"
                + ContactsOpportunitiesColumns.DATE_MODIFIED + TEXT_COMMA
                + ContactsOpportunitiesColumns.DELETED + INTEGER_COMMA
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

        db.execSQL(CREATE_TABLE + CONTACTS_CASES_TABLE_NAME + " ("
                + ContactsCasesColumns.CONTACT_ID + " INTEGER ,"
                + ContactsCasesColumns.CASE_ID + " INTEGER ,"
                + ContactsCasesColumns.DATE_MODIFIED + TEXT_COMMA
                + ContactsCasesColumns.DELETED + INTEGER_COMMA + " UNIQUE("
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

        db.execSQL(CREATE_TABLE + SYNC_TABLE_NAME + " (" + SyncColumns.ID
                + INTEGER_PRIMARY_KEY + SyncColumns.SYNC_ID + " INTEGER ,"
                + SyncColumns.SYNC_RELATED_ID + " INTEGER ,"
                + SyncColumns.SYNC_COMMAND + INTEGER_COMMA + SyncColumns.MODULE
                + TEXT_COMMA + SyncColumns.RELATED_MODULE + TEXT_COMMA
                + SyncColumns.DATE_MODIFIED + TEXT_COMMA
                + SyncColumns.SYNC_STATUS + INTEGER_COMMA + " UNIQUE("
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
        db.execSQL(CREATE_TABLE + USERS_TABLE_NAME + " (" + UserColumns.ID
                + INTEGER_PRIMARY_KEY + UserColumns.USER_ID + INTEGER_COMMA
                + UserColumns.USER_NAME + TEXT_COMMA + UserColumns.FIRST_NAME
                + TEXT_COMMA + UserColumns.LAST_NAME + TEXT_COMMA + " UNIQUE("
                + UserColumns.USER_NAME + ")" + ");");
    }

    /**
     * Creates the acl roles table.
     * 
     * @param db
     *            the db
     */
    private static void createAclRolesTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + ACL_ROLES_TABLE_NAME + " ("
                + ACLRoleColumns.ID + INTEGER_PRIMARY_KEY
                + ACLRoleColumns.ROLE_ID + INTEGER_COMMA + ACLRoleColumns.NAME
                + TEXT_COMMA + ACLRoleColumns.TYPE + TEXT_COMMA
                + ACLRoleColumns.DESCRIPTION + TEXT_COMMA + " UNIQUE("
                + ACLRoleColumns.ROLE_ID + ")" + ");");
    }

    /**
     * Creates the acl actions table.
     * 
     * @param db
     *            the db
     */
    private static void createAclActionsTable(final SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE + ACL_ACTIONS_TABLE_NAME + " ("
                + ACLActionColumns.ID + INTEGER_PRIMARY_KEY
                + ACLActionColumns.ACTION_ID + INTEGER_COMMA
                + ACLActionColumns.NAME + INTEGER_COMMA
                + ACLActionColumns.CATEGORY + TEXT_COMMA
                + ACLActionColumns.ACLACCESS + TEXT_COMMA
                + ACLActionColumns.ACLTYPE + TEXT_COMMA
                + ACLActionColumns.ROLE_ID + INTEGER_COMMA + " UNIQUE("
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
        if (accessMap != null && accessMap.size() != 0) {
            return accessMap;
        } else {
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
        // are given to a user,
        // then we give access to the entire application
        if (aclAccessMap.size() == 0) {
            return true;
        }
        final Map<String, Integer> moduleAccessMap = aclAccessMap
                .get(moduleName);
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
            } else {
                return false;
            }
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
        if (moduleName.equals(Util.CONTACTS) || moduleName.equals(Util.LEADS)) {
            return "(" + LeadsColumns.FIRST_NAME + " LIKE '%" + searchString
                    + "%' OR " + LeadsColumns.LAST_NAME + " LIKE '%"
                    + searchString + "%'" + ")";
        } else {
            // for Accounts, Opportunities, Cases, Calls and Mettings
            return ModuleFields.NAME + " LIKE '%" + searchString + "%'";
        }
    }

    // TODO - get from DB

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
            if (map != null && map.size() > 0) {
                return map;
            }
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
            record.mid = cursor.getLong(Sync.ID_COLUMN);
            record.mSsyncId = cursor.getLong(Sync.SYNC_ID_COLUMN);
            record.mSyncRelatedId = cursor.getLong(Sync.SYNC_RELATED_ID_COLUMN);
            record.mSyncCommand = cursor.getInt(Sync.SYNC_COMMAND_COLUMN);
            record.mModuleName = cursor.getString(Sync.MODULE_NAME_COLUMN);
            record.mRelatedModuleName = cursor
                    .getString(Sync.RELATED_MODULE_NAME_COLUMN);
            record.mStatus = cursor.getInt(Sync.STATUS_COLUMN);
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
                    ModuleFieldsTableInfo.DETAILS_PROJECTION, selection, null,
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
        final Cursor cursor = db.query(DatabaseHelper.MODULES_TABLE_NAME,
                Modules.DETAILS_PROJECTION, selection, null, null, null, null);
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
        values.put(SyncColumns.SYNC_ID, record.mSsyncId);
        values.put(SyncColumns.SYNC_RELATED_ID, record.mSyncRelatedId);
        values.put(SyncColumns.MODULE, record.mModuleName);
        values.put(SyncColumns.RELATED_MODULE, record.mRelatedModuleName);
        values.put(SyncColumns.SYNC_STATUS, record.mStatus);

        final int rowId = db.update(SYNC_TABLE_NAME, values, SyncColumns.ID
                + "=?", new String[] { "" + record.mid });
        if (rowId < 0) {
            throw new SugarCrmException("FAILED to update sync record!");
        }
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
        if (rowId < 0) {
            throw new SugarCrmException("FAILED to update sync record!");
        }
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
        values.put(SyncColumns.SYNC_ID, record.mSsyncId);
        values.put(SyncColumns.SYNC_RELATED_ID, record.mSyncRelatedId);
        values.put(SyncColumns.SYNC_COMMAND, record.mSyncCommand);
        values.put(SyncColumns.MODULE, record.mModuleName);
        values.put(SyncColumns.RELATED_MODULE, record.mRelatedModuleName);
        values.put(SyncColumns.SYNC_STATUS, record.mStatus);

        final long rowId = db.insert(SYNC_TABLE_NAME, "", values);
        if (rowId < 0) {
            throw new SugarCrmException("FAILED to insert sync record!");
        }
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
        final int count = db.delete(DatabaseHelper.SYNC_TABLE_NAME,
                SyncColumns.ID + "=" + syncRecordId, null);
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
