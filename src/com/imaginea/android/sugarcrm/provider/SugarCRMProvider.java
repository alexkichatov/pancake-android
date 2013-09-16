/*******************************************************************************
 * Copyright (c) 2013 Asha, Murli.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *          Asha, Muralidaran - initial API and implementation
 * author:  chander
 * Project Name : SugarCrm Pancake
 * FileName : DatabaseHelper 
 * Description : 
                SugarCRMProvider Provides access to a database of sugar modules, their data
 * and relationships
 ******************************************************************************/

package com.imaginea.android.sugarcrm.provider;

import java.util.HashMap;
import java.util.Map;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Accounts;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsCasesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsContactsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsOpportunitiesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Calls;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.CallsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.CampaignColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Campaigns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Cases;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.CasesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Contacts;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ContactsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ContactsOpportunitiesColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Leads;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.LeadsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Meetings;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.MeetingsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Modules;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Opportunities;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.OpportunitiesColumns;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * SugarCRMProvider Provides access to a database of sugar modules, their data
 * and relationships.
 */
public class SugarCRMProvider extends ContentProvider {

    /** The Constant AUTHORITY. */
    public static final String AUTHORITY = "com.imaginea.sugarcrm.provider";

    /** The Constant ACCOUNT. */
    private static final int ACCOUNT = 0;

    /** The Constant ACCOUNT_ID. */
    private static final int ACCOUNT_ID = 1;

    /** The Constant CONTACT. */
    private static final int CONTACT = 2;

    /** The Constant CONTACT_ID. */
    private static final int CONTACT_ID = 3;

    /** The Constant LEAD. */
    private static final int LEAD = 4;

    /** The Constant LEAD_ID. */
    private static final int LEAD_ID = 5;

    /** The Constant OPPORTUNITY. */
    private static final int OPPORTUNITY = 6;

    private static final String HASH_HASH = "/#/#";

    private static final String HASH = "/#/";

    private static final String AND = " AND (";

    /** The Constant OPPORTUNITY_ID. */
    private static final int OPPORTUNITY_ID = 7;

    /** The Constant MEETING. */
    private static final int MEETING = 8;

    /** The Constant MEETING_ID. */
    private static final int MEETING_ID = 9;

    /** The Constant CASE. */
    private static final int CASE = 10;

    /** The Constant CASE_ID. */
    private static final int CASE_ID = 11;

    /** The Constant CALL. */
    private static final int CALL = 12;

    /** The Constant CALL_ID. */
    private static final int CALL_ID = 13;

    /** The Constant CAMPAIGN. */
    private static final int CAMPAIGN = 14;

    /** The Constant CAMPAIGN_ID. */
    private static final int CAMPAIGN_ID = 15;

    /** The Constant ACCOUNT_CONTACT. */
    private static final int ACCOUNT_CONTACT = 16;

    /** The Constant ACCOUNT_LEAD. */
    private static final int ACCOUNT_LEAD = 17;

    /** The Constant ACCOUNT_OPPORTUNITY. */
    private static final int ACCOUNT_OPPORTUNITY = 18;

    /** The Constant ACCOUNT_CASE. */
    private static final int ACCOUNT_CASE = 19;

    /** The Constant CONTACT_LEAD. */
    private static final int CONTACT_LEAD = 20;

    // TODO - is this required
    /** The Constant CONTACT_OPPORTUNITY. */
    private static final int CONTACT_OPPORTUNITY = 21;

    /** The Constant CONTACT_CASE. */
    private static final int CONTACT_CASE = 22;

    /** The Constant LEAD_OPPORTUNITY. */
    private static final int LEAD_OPPORTUNITY = 23;

    /** The Constant OPPORTUNITY_CONTACT. */
    private static final int OPPORTUNITY_CONTACT = 24;

    /** The Constant USERS. */
    private static final int USERS = 25;

    /** The Constant SEARCH. */
    private static final int SEARCH = 26;

    /** The Constant RECENT. */
    private static final int RECENT = 27;

    /** The Constant SYNC. */
    private static final int SYNC = 28;

    /** The Constant MODULES. */
    private static final int MODULES = 29;

    /** The Constant ACCOUNTS_CONTACTS. */
    private static final int ACCOUNTS_CONTACTS = 30;

    /** The Constant ACCOUNTS_CASES. */
    private static final int ACCOUNTS_CASES = 31;

    /** The Constant ACCOUNTS_OPPORTUNITIES. */
    private static final int ACCOUNTS_OPPORTUNITIES = 32;

    /** The Constant ACL_ACTIONS. */
    private static final int ACL_ACTIONS = 33;

    /** The Constant ACL_ROLES. */
    private static final int ACL_ROLES = 34;

    /** The Constant ALARMS. */
    private static final int ALARMS = 35;

    /** The Constant CONTACTS_CASES. */
    private static final int CONTACTS_CASES = 36;

    /** The Constant CONTACTS_OPPORTUNITIES. */
    private static final int CONTACTS_OPPORTUNITIES = 37;

    /** The Constant LINK_FIELDS. */
    private static final int LINK_FIELDS = 38;

    /** The Constant MODULE_FIELDS. */
    private static final int MODULE_FIELDS = 39;

    /** The Constant sUriMatcher. */
    private static UriMatcher sUriMatcher;

    /** The Constant TAG. */
    private static final String TAG = SugarCRMProvider.class.getSimpleName();

    /** The m open helper. */
    private DatabaseHelper mOpenHelper;

    /** {@inheritDoc} */
    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Cursor query(final Uri uri, final String[] projection,
            String selection, final String[] selectionArgs, String sortOrder) {
        Cursor c = null;
        String selectionNew = null;
        final String offset = null;

        // Get the database and run the query
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)) {
        case SEARCH:
            final String query = uri.getLastPathSegment().toLowerCase();
            selectionNew = ModuleFields.NAME + " ='" + query + "'";
            c = db.query(DatabaseHelper.ACCOUNTS_TABLE_NAME,
                    Accounts.SEARCH_PROJECTION, selectionNew, selectionArgs,
                    null, null, null);
            break;
        case ACCOUNT:
            c = db.query(DatabaseHelper.ACCOUNTS_TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder);
            break;

        case ACCOUNT_ID:
            selectionNew = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.ACCOUNTS_TABLE_NAME, projection,
                    selectionNew,
                    new String[] { uri.getPathSegments().get(1) }, null, null,
                    null);
            break;

        case ACCOUNT_CONTACT:
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(DatabaseHelper.ACCOUNTS_TABLE_NAME + ","
                    + DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME + ","
                    + DatabaseHelper.CONTACTS_TABLE_NAME);

            selectionNew = DatabaseHelper.ACCOUNTS_TABLE_NAME + "."
                    + AccountsColumns.ID + " = ?" + " AND "
                    + DatabaseHelper.ACCOUNTS_TABLE_NAME + "."
                    + AccountsColumns.ID + "="
                    + DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME + "."
                    + AccountsContactsColumns.ACCOUNT_ID + " AND "
                    + DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME + "."
                    + AccountsContactsColumns.CONTACT_ID + "="
                    + DatabaseHelper.CONTACTS_TABLE_NAME + "."
                    + ContactsColumns.ID + " AND "
                    + DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME + "."
                    + AccountsContactsColumns.DELETED + "=" + Util.NEW_ITEM;
            final Map<String, String> contactsProjectionMap = getProjectionMap(
                    DatabaseHelper.CONTACTS_TABLE_NAME, projection);
            qb.setProjectionMap(contactsProjectionMap);
            c = qb.query(db, projection, selectionNew, new String[] { uri
                    .getPathSegments().get(1) }, null, null, sortOrder, "");

            break;

        case ACCOUNT_LEAD:

            selectionNew = LeadsColumns.ACCOUNT_ID + " = ?";
            c = db.query(DatabaseHelper.LEADS_TABLE_NAME, projection,
                    selectionNew,
                    new String[] { uri.getPathSegments().get(1) }, null, null,
                    null);
            break;

        case ACCOUNT_OPPORTUNITY:

            qb = new SQLiteQueryBuilder();
            qb.setTables(DatabaseHelper.ACCOUNTS_TABLE_NAME + ","
                    + DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME + ","
                    + DatabaseHelper.OPPORTUNITIES_TABLE_NAME);

            selectionNew = DatabaseHelper.ACCOUNTS_TABLE_NAME + "."
                    + AccountsColumns.ID + " = ?" + " AND "
                    + DatabaseHelper.ACCOUNTS_TABLE_NAME + "."
                    + AccountsColumns.ID + "="
                    + DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME + "."
                    + AccountsOpportunitiesColumns.ACCOUNT_ID + " AND "
                    + DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME + "."
                    + AccountsOpportunitiesColumns.OPPORTUNITY_ID + "="
                    + DatabaseHelper.OPPORTUNITIES_TABLE_NAME + "."
                    + OpportunitiesColumns.ID;
            Map<String, String> opportunityProjectionMap = getProjectionMap(
                    DatabaseHelper.OPPORTUNITIES_TABLE_NAME, projection);
            qb.setProjectionMap(opportunityProjectionMap);

            final String sortOrderNew = DatabaseHelper.OPPORTUNITIES_TABLE_NAME
                    + "." + OpportunitiesColumns.NAME + " ASC";
            c = qb.query(db, projection, selectionNew, new String[] { uri
                    .getPathSegments().get(1) }, null, null, sortOrderNew, "");

            break;

        case ACCOUNT_CASE:

            qb = new SQLiteQueryBuilder();
            qb.setTables(DatabaseHelper.ACCOUNTS_TABLE_NAME + ","
                    + DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME + ","
                    + DatabaseHelper.CASES_TABLE_NAME);

            selectionNew = DatabaseHelper.ACCOUNTS_TABLE_NAME + "."
                    + AccountsColumns.ID + " = ?" + " AND "
                    + DatabaseHelper.ACCOUNTS_TABLE_NAME + "."
                    + AccountsColumns.ID + "="
                    + DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME + "."
                    + AccountsCasesColumns.ACCOUNT_ID + " AND "
                    + DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME + "."
                    + AccountsCasesColumns.CASE_ID + "="
                    + DatabaseHelper.CASES_TABLE_NAME + "." + CasesColumns.ID;
            final Map<String, String> casesProjectionMap = getProjectionMap(
                    DatabaseHelper.CASES_TABLE_NAME, projection);
            qb.setProjectionMap(casesProjectionMap);

            final String sortOrder1 = DatabaseHelper.CASES_TABLE_NAME + "."
                    + CasesColumns.NAME + " ASC";
            c = qb.query(db, projection, selectionNew, new String[] { uri
                    .getPathSegments().get(1) }, null, null, sortOrder1, "");

            break;

        case CONTACT:

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Querying Contacts");
                Log.d(TAG, "Uri:->" + uri.toString());

                Log.d(TAG, "Offset" + offset);
            }
            c = db.query(DatabaseHelper.CONTACTS_TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder, "");

            break;

        case CONTACT_ID:

            selectionNew = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.CONTACTS_TABLE_NAME, projection,
                    selectionNew,
                    new String[] { uri.getPathSegments().get(1) }, null, null,
                    null);
            break;

        case CONTACT_LEAD:
            // TODO - this case is dubious - remove it later
            // Bug - contactId being used as accountId
            selectionNew = LeadsColumns.ACCOUNT_ID + " = ?";
            c = db.query(DatabaseHelper.LEADS_TABLE_NAME, projection,
                    selectionNew,
                    new String[] { uri.getPathSegments().get(1) }, null, null,
                    null);
            break;

        case CONTACT_OPPORTUNITY:

            qb = new SQLiteQueryBuilder();
            qb.setTables(DatabaseHelper.CONTACTS_TABLE_NAME + ","
                    + DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME + ","
                    + DatabaseHelper.OPPORTUNITIES_TABLE_NAME);

            selectionNew = DatabaseHelper.CONTACTS_TABLE_NAME + "."
                    + ContactsColumns.ID + " = ?" + " AND "
                    + DatabaseHelper.CONTACTS_TABLE_NAME + "."
                    + ContactsColumns.ID + "="
                    + DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME + "."
                    + ContactsOpportunitiesColumns.CONTACT_ID + " AND "
                    + DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME + "."
                    + ContactsOpportunitiesColumns.OPPORTUNITY_ID + "="
                    + DatabaseHelper.OPPORTUNITIES_TABLE_NAME + "."
                    + OpportunitiesColumns.ID;
            opportunityProjectionMap = getProjectionMap(
                    DatabaseHelper.OPPORTUNITIES_TABLE_NAME, projection);
            qb.setProjectionMap(opportunityProjectionMap);
            c = qb.query(db, projection, selectionNew, new String[] { uri
                    .getPathSegments().get(1) }, null, null, sortOrder, "");

            break;

        case LEAD:
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Querying Leads");
                Log.d(TAG, "Uri:->" + uri.toString());
                Log.d(TAG, "Offset" + offset);
            }
            c = db.query(DatabaseHelper.LEADS_TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder, "");

            break;

        case LEAD_ID:
            selectionNew = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.LEADS_TABLE_NAME, projection,
                    selectionNew,
                    new String[] { uri.getPathSegments().get(1) }, null, null,
                    null);

            break;

        case LEAD_OPPORTUNITY:
            // TODO - this case is dubious - remove it later
            // Bug - contactId being used as accountId
            selectionNew = OpportunitiesColumns.ACCOUNT_ID + " = ?";
            c = db.query(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, projection,
                    selectionNew,
                    new String[] { uri.getPathSegments().get(1) }, null, null,
                    null);
            break;

        case OPPORTUNITY:

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Querying OPPORTUNITIES");
                Log.d(TAG, "Uri:->" + uri.toString());
                Log.d(TAG, "Offset" + offset);
            }

            c = db.query(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder, "");

            break;

        case OPPORTUNITY_ID:

            selectionNew = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, projection,
                    selectionNew,
                    new String[] { uri.getPathSegments().get(1) }, null, null,
                    null);
            break;

        case OPPORTUNITY_CONTACT:

            qb = new SQLiteQueryBuilder();
            qb.setTables(DatabaseHelper.CONTACTS_TABLE_NAME + ","
                    + DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME + ","
                    + DatabaseHelper.OPPORTUNITIES_TABLE_NAME);

            selectionNew = DatabaseHelper.OPPORTUNITIES_TABLE_NAME + "."
                    + OpportunitiesColumns.ID + " = ?" + " AND "
                    + DatabaseHelper.CONTACTS_TABLE_NAME + "."
                    + ContactsColumns.ID + "="
                    + DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME + "."
                    + ContactsOpportunitiesColumns.CONTACT_ID + " AND "
                    + DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME + "."
                    + ContactsOpportunitiesColumns.OPPORTUNITY_ID + "="
                    + DatabaseHelper.OPPORTUNITIES_TABLE_NAME + "."
                    + OpportunitiesColumns.ID;
            opportunityProjectionMap = getProjectionMap(
                    DatabaseHelper.CONTACTS_TABLE_NAME, projection);
            qb.setProjectionMap(opportunityProjectionMap);

            final String newSortOrder = DatabaseHelper.CONTACTS_TABLE_NAME
                    + "." + ContactsColumns.FIRST_NAME + " ASC";
            c = qb.query(db, projection, selectionNew, new String[] { uri
                    .getPathSegments().get(1) }, null, null, newSortOrder, "");

            break;

        case CASE:
            c = db.query(DatabaseHelper.CASES_TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder);
            break;

        case CASE_ID:
            selectionNew = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.CASES_TABLE_NAME, projection,
                    selectionNew,
                    new String[] { uri.getPathSegments().get(1) }, null, null,
                    null);
            break;

        case CALL:

            c = db.query(DatabaseHelper.CALLS_TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder);
            break;

        case CALL_ID:
            selectionNew = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.CALLS_TABLE_NAME, projection,
                    selectionNew,
                    new String[] { uri.getPathSegments().get(1) }, null, null,
                    null);
            break;

        case MEETING:
            c = db.query(DatabaseHelper.MEETINGS_TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder);
            break;

        case MEETING_ID:
            selectionNew = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.MEETINGS_TABLE_NAME, projection,
                    selectionNew,
                    new String[] { uri.getPathSegments().get(1) }, null, null,
                    null);
            break;

        case CAMPAIGN:
            c = db.query(DatabaseHelper.CAMPAIGNS_TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder);
            break;

        case CAMPAIGN_ID:
            selectionNew = SugarCRMContent.RECORD_ID + " = ?";
            c = db.query(DatabaseHelper.CAMPAIGNS_TABLE_NAME, projection,
                    selectionNew,
                    new String[] { uri.getPathSegments().get(1) }, null, null,
                    null);
            break;

        case USERS:
            c = db.query(DatabaseHelper.USERS_TABLE_NAME, projection,
                    selection, selectionArgs, null, null, null);
            break;

        case RECENT:

            c = db.query(DatabaseHelper.RECENT_TABLE_NAME, projection,
                    selection, selectionArgs, null, null, null);
            break;

        case MODULES:
            c = db.query(DatabaseHelper.MODULES_TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder);
            break;
        case MODULE_FIELDS:
            c = db.query(DatabaseHelper.MODULE_FIELDS_TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Count:" + c.getCount());
        }
        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        // TODO - moce this code to sync and cache manager - database cache
        // miss, start a rest api
        // call , package the params appropriately
        return c;
    }

    /** {@inheritDoc} */
    @Override
    public String getType(final Uri uri) {
        switch (sUriMatcher.match(uri)) {
        // TODO - add the remaining types
        case ACCOUNT:
            return "vnd.android.cursor.dir/accounts";
        case CONTACT:
            return "vnd.android.cursor.dir/contacts";

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Uri insert(final Uri uri, final ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        // Make sure that the fields are all set
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
        case ACCOUNT:
            long rowId = db.insert(DatabaseHelper.ACCOUNTS_TABLE_NAME, "",
                    values);
            if (rowId > 0) {
                final Uri accountUri = ContentUris.withAppendedId(
                        Accounts.CONTENT_URI, rowId);
                getContext().getContentResolver()
                        .notifyChange(accountUri, null);
                return accountUri;
            }
            break;

        case ACCOUNT_CONTACT:
            String accountId = uri.getPathSegments().get(1);
            String selection = AccountsColumns.ID + "=" + accountId;

            final Uri parentUri = ContentUtils.getModuleUri(Util.ACCOUNTS);
            Cursor cursor = query(parentUri, Accounts.DETAILS_PROJECTION,
                    selection, null, null);
            final boolean rowsPresent = cursor.moveToFirst();
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Uri to insert:" + uri.toString() + " rows present:"
                        + rowsPresent);
            }
            if (!rowsPresent) {
                cursor.close();
                return uri;
            }
            String accountName = cursor.getString(cursor
                    .getColumnIndex(AccountsColumns.NAME));
            values.put(ContactsColumns.ACCOUNT_NAME, accountName);
            cursor.close();
            rowId = db.insert(DatabaseHelper.CONTACTS_TABLE_NAME, "", values);
            if (rowId > 0) {
                final Uri contactUri = ContentUris.withAppendedId(
                        Contacts.CONTENT_URI, rowId);
                getContext().getContentResolver()
                        .notifyChange(contactUri, null);

                final ContentValues val2 = new ContentValues();
                val2.put(AccountsContactsColumns.ACCOUNT_ID, accountId);
                val2.put(AccountsContactsColumns.CONTACT_ID, rowId);
                val2.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                // TODO - date_modified
                db.insert(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME, "", val2);

                return contactUri;
            }
            break;

        case ACCOUNT_LEAD:
            accountId = uri.getPathSegments().get(1);
            selection = AccountsColumns.ID + "=" + accountId;
            cursor = query(ContentUtils.getModuleUri(Util.ACCOUNTS),
                    Accounts.DETAILS_PROJECTION, selection, null, null);
            cursor.moveToFirst();
            accountName = cursor.getString(cursor
                    .getColumnIndex(AccountsColumns.NAME));
            values.put(ModuleFields.ACCOUNT_ID, accountId);
            values.put(ModuleFields.ACCOUNT_NAME, accountName);

            rowId = db.insert(DatabaseHelper.LEADS_TABLE_NAME, "", values);
            if (rowId > 0) {
                final Uri leadUri = ContentUris.withAppendedId(
                        Leads.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(leadUri, null);
                cursor.close();
                return leadUri;
            }
            break;

        case ACCOUNT_OPPORTUNITY:
            accountId = uri.getPathSegments().get(1);
            selection = AccountsColumns.ID + "=" + accountId;

            cursor = query(ContentUtils.getModuleUri(Util.ACCOUNTS),
                    Accounts.DETAILS_PROJECTION, selection, null, null);
            cursor.moveToFirst();
            accountName = cursor.getString(cursor
                    .getColumnIndex(AccountsColumns.NAME));
            values.put(ContactsColumns.ACCOUNT_NAME, accountName);
            cursor.close();
            rowId = db.insert(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, "",
                    values);
            if (rowId > 0) {
                final Uri opportunityUri = ContentUris.withAppendedId(
                        Opportunities.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(opportunityUri,
                        null);

                final ContentValues val2 = new ContentValues();
                val2.put(AccountsOpportunitiesColumns.ACCOUNT_ID, accountId);
                val2.put(AccountsOpportunitiesColumns.OPPORTUNITY_ID, rowId);
                val2.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                // TODO - date_modified
                db.insert(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME, "",
                        val2);

                return opportunityUri;
            }

            break;

        case ACCOUNT_CASE:
            accountId = uri.getPathSegments().get(1);
            selection = AccountsColumns.ID + "=" + accountId;

            rowId = db.insert(DatabaseHelper.CASES_TABLE_NAME, "", values);
            if (rowId > 0) {
                final Uri caseUri = ContentUris.withAppendedId(
                        Cases.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(caseUri, null);

                final ContentValues val2 = new ContentValues();
                val2.put(AccountsCasesColumns.ACCOUNT_ID, accountId);
                val2.put(AccountsCasesColumns.CASE_ID, rowId);
                val2.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                // TODO - date_modified
                db.insert(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME, "", val2);

                return caseUri;
            }

            break;

        case CONTACT:
            rowId = db.insert(DatabaseHelper.CONTACTS_TABLE_NAME, "", values);
            if (rowId > 0) {
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    // get the account name from the name-value map
                    accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName
                                + "'";
                        final Cursor c = db.query(
                                DatabaseHelper.ACCOUNTS_TABLE_NAME,
                                Accounts.LIST_PROJECTION, selection, null,
                                null, null, null);
                        c.moveToFirst();
                        final String newAccountId = c.getString(0);
                        c.close();

                        // create a new relationship with the account
                        final ContentValues val3 = new ContentValues();
                        val3.put(AccountsContactsColumns.ACCOUNT_ID,
                                newAccountId);
                        val3.put(AccountsContactsColumns.CONTACT_ID, rowId);
                        val3.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                        final long relationRowId = db.insert(
                                DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                                "", val3);
                        if (relationRowId > 0) {
                            Log.d(TAG, "created relation: contactId 5- "
                                    + rowId + " accountId - " + newAccountId);
                        }
                    }
                }

                final Uri contactUri = ContentUris.withAppendedId(
                        Contacts.CONTENT_URI, rowId);
                getContext().getContentResolver()
                        .notifyChange(contactUri, null);
                return contactUri;
            }
            break;

        case CONTACT_LEAD:
            rowId = db.insert(DatabaseHelper.LEADS_TABLE_NAME, "", values);
            if (rowId > 0) {
                final Uri leadUri = ContentUris.withAppendedId(
                        Leads.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(leadUri, null);
                return leadUri;
            }
            break;

        case CONTACT_OPPORTUNITY:
            final String contactId = uri.getPathSegments().get(1);
            selection = ContactsColumns.ID + "=" + contactId;

            rowId = db.insert(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, "",
                    values);
            if (rowId > 0) {
                final Uri opportunityUri = ContentUris.withAppendedId(
                        Opportunities.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(opportunityUri,
                        null);

                final ContentValues val2 = new ContentValues();
                val2.put(ContactsOpportunitiesColumns.CONTACT_ID, contactId);
                val2.put(ContactsOpportunitiesColumns.OPPORTUNITY_ID, rowId);
                val2.put(ContactsOpportunitiesColumns.DELETED, Util.NEW_ITEM);
                // TODO - date_modified
                long relationRowId = db.insert(
                        DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME, "",
                        val2);
                if (relationRowId > 0) {
                    Log.d(TAG, "created relation: opportunityId - " + rowId
                            + " contactId - " + contactId);
                }

                // accounts_opportunities relationship
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName
                                + "'";
                        cursor = query(
                                ContentUtils.getModuleUri(Util.ACCOUNTS),
                                Accounts.LIST_PROJECTION, selection, null, null);
                        String newAccountId = null;
                        if (cursor.moveToFirst()) {
                            newAccountId = cursor.getString(0);
                        }
                        cursor.close();

                        if (newAccountId != null) {
                            final ContentValues val3 = new ContentValues();
                            val3.put(AccountsOpportunitiesColumns.ACCOUNT_ID,
                                    newAccountId);
                            val3.put(
                                    AccountsOpportunitiesColumns.OPPORTUNITY_ID,
                                    rowId);
                            val3.put(AccountsOpportunitiesColumns.DELETED,
                                    Util.NEW_ITEM);
                            // TODO - date_modified
                            relationRowId = db
                                    .insert(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                                            "", val3);
                            if (relationRowId > 0) {
                                Log.d(TAG, "created relation: opportunityId - "
                                        + rowId + " accountId - "
                                        + newAccountId);
                            }
                        }
                    }
                }

                return opportunityUri;
            }

            break;

        case LEAD:
            accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);
            if (!TextUtils.isEmpty(accountName)) {
                // get the account id for the account name
                selection = AccountsColumns.NAME + "='" + accountName + "'";
                final Cursor c = db.query(DatabaseHelper.ACCOUNTS_TABLE_NAME,
                        Accounts.LIST_PROJECTION, selection, null, null, null,
                        null);
                String newAccountId = null;
                if (c.moveToFirst()) {
                    newAccountId = c.getString(0);
                    values.put(LeadsColumns.ACCOUNT_ID, newAccountId);
                }
                c.close();

            }

            rowId = db.insert(DatabaseHelper.LEADS_TABLE_NAME, "", values);
            if (rowId > 0) {
                final Uri leadUri = ContentUris.withAppendedId(
                        Leads.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(leadUri, null);
                return leadUri;
            }
            break;

        case LEAD_OPPORTUNITY:
            rowId = db.insert(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, "",
                    values);
            if (rowId > 0) {
                final Uri opportunityUri = ContentUris.withAppendedId(
                        Leads.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(opportunityUri,
                        null);
                return opportunityUri;
            }
            break;

        case OPPORTUNITY:
            rowId = db.insert(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, "",
                    values);
            if (rowId > 0) {
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName
                                + "'";
                        final Cursor c = db.query(
                                DatabaseHelper.ACCOUNTS_TABLE_NAME,
                                Accounts.LIST_PROJECTION, selection, null,
                                null, null, null);
                        String newAccountId = null;
                        if (c.moveToFirst()) {
                            newAccountId = c.getString(0);
                        }
                        c.close();

                        if (newAccountId != null) {
                            // create a new relationship with the new account
                            final ContentValues val3 = new ContentValues();
                            val3.put(AccountsOpportunitiesColumns.ACCOUNT_ID,
                                    newAccountId);
                            val3.put(
                                    AccountsOpportunitiesColumns.OPPORTUNITY_ID,
                                    rowId);
                            val3.put(AccountsOpportunitiesColumns.DELETED,
                                    Util.NEW_ITEM);
                            final long relationRowId = db
                                    .insert(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                                            "", val3);
                            if (relationRowId > 0) {
                                Log.d(TAG, "created relation: opportunityId - "
                                        + rowId + " accountId - "
                                        + newAccountId);
                            }
                        }
                    }
                }

                final Uri oppUri = ContentUris.withAppendedId(
                        Opportunities.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(oppUri, null);
                return oppUri;
            }
            break;

        case OPPORTUNITY_CONTACT:
            final String opportunityId = uri.getPathSegments().get(1);
            selection = OpportunitiesColumns.ID + "=" + opportunityId;

            rowId = db.insert(DatabaseHelper.CONTACTS_TABLE_NAME, "", values);
            if (rowId > 0) {
                final Uri contactsUri = ContentUris.withAppendedId(
                        Contacts.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(contactsUri,
                        null);

                final ContentValues val2 = new ContentValues();
                val2.put(ContactsOpportunitiesColumns.CONTACT_ID, rowId);
                val2.put(ContactsOpportunitiesColumns.OPPORTUNITY_ID,
                        opportunityId);
                val2.put(AccountsOpportunitiesColumns.DELETED, Util.NEW_ITEM);
                // TODO - date_modified
                db.insert(DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME, "",
                        val2);

                // accounts_contacts relationship
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName
                                + "'";
                        cursor = query(
                                ContentUtils.getModuleUri(Util.ACCOUNTS),
                                Accounts.LIST_PROJECTION, selection, null, null);
                        String newAccountId = null;
                        if (cursor.moveToFirst()) {
                            newAccountId = cursor.getString(0);
                        }
                        cursor.close();

                        if (newAccountId != null) {
                            final ContentValues val3 = new ContentValues();
                            val3.put(AccountsContactsColumns.ACCOUNT_ID,
                                    newAccountId);
                            val3.put(AccountsContactsColumns.CONTACT_ID, rowId);
                            val3.put(AccountsContactsColumns.DELETED,
                                    Util.NEW_ITEM);
                            // TODO - date_modified
                            final long relationRowId = db
                                    .insert(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                                            "", val3);
                            if (relationRowId > 0) {
                                Log.d(TAG, "created relation: contactId 1- "
                                        + rowId + " accountId - "
                                        + newAccountId);
                            }
                        }
                    }
                }

                return contactsUri;
            }

            break;

        case CASE:
            rowId = db.insert(DatabaseHelper.CASES_TABLE_NAME, "", values);
            if (rowId > 0) {
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    accountName = values.getAsString(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName
                                + "'";
                        final Cursor c = db.query(
                                DatabaseHelper.ACCOUNTS_TABLE_NAME,
                                Accounts.LIST_PROJECTION, selection, null,
                                null, null, null);
                        String newAccountId = null;
                        if (c.moveToFirst()) {
                            newAccountId = c.getString(0);
                        }
                        c.close();

                        if (newAccountId != null) {
                            // create a new relationship with the new account
                            final ContentValues val3 = new ContentValues();
                            val3.put(AccountsCasesColumns.ACCOUNT_ID,
                                    newAccountId);
                            val3.put(AccountsCasesColumns.CASE_ID, rowId);
                            val3.put(AccountsCasesColumns.DELETED,
                                    Util.NEW_ITEM);
                            final long relationRowId = db.insert(
                                    DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME,
                                    "", val3);
                            if (relationRowId > 0) {
                                Log.d(TAG, "created relation: caseId - "
                                        + rowId + " accountId - "
                                        + newAccountId);
                            }
                        }
                    }
                }
                final Uri caseUri = ContentUris.withAppendedId(
                        Cases.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(caseUri, null);
                return caseUri;
            }
            break;

        case CALL:
            rowId = db.insert(DatabaseHelper.CALLS_TABLE_NAME, "", values);
            if (rowId > 0) {
                final Uri callUri = ContentUris.withAppendedId(
                        Calls.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(callUri, null);
                return callUri;
            }
            break;

        case MEETING:
            rowId = db.insert(DatabaseHelper.MEETINGS_TABLE_NAME, "", values);
            if (rowId > 0) {
                final Uri meetingUri = ContentUris.withAppendedId(
                        Meetings.CONTENT_URI, rowId);
                getContext().getContentResolver()
                        .notifyChange(meetingUri, null);
                return meetingUri;
            }
            break;

        case CAMPAIGN:
            rowId = db.insert(DatabaseHelper.CAMPAIGNS_TABLE_NAME, "", values);
            if (rowId > 0) {
                final Uri campaignUri = ContentUris.withAppendedId(
                        Campaigns.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(campaignUri,
                        null);
                return campaignUri;
            }
            break;

        case RECENT:
            rowId = db.insert(DatabaseHelper.RECENT_TABLE_NAME, "", values);

            if (rowId > 0) {
                final Uri campaignUri = ContentUris.withAppendedId(
                        Campaigns.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(campaignUri,
                        null);
                return campaignUri;
            }
            break;

        case MODULES:
            rowId = db.insert(DatabaseHelper.MODULES_TABLE_NAME, "", values);
            Log.d("inserting", "Module is inserted into db = " + rowId);
            if (rowId > 0) {
                final Uri modulesUri = ContentUris.withAppendedId(
                        Modules.CONTENT_URI, rowId);
                return modulesUri;
            }
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);

        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    /** {@inheritDoc} */
    @Override
    public int delete(final Uri uri, final String where,
            final String[] whereArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri)) {
        case ACCOUNT:
            count = db.delete(DatabaseHelper.ACCOUNTS_TABLE_NAME, where,
                    whereArgs);
            break;

        case ACCOUNT_ID:
            String accountId = uri.getPathSegments().get(1);
            count = db.delete(
                    DatabaseHelper.ACCOUNTS_TABLE_NAME,
                    AccountsColumns.ID
                            + "="
                            + accountId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            break;

        case ACCOUNT_CONTACT:
            accountId = uri.getPathSegments().get(1);
            String contactId = uri.getPathSegments().get(3);
            count = db.delete(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                    AccountsContactsColumns.ACCOUNT_ID
                            + "="
                            + accountId
                            + " AND "
                            + AccountsContactsColumns.CONTACT_ID
                            + "="
                            + contactId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            break;

        case ACCOUNT_LEAD:
            accountId = uri.getPathSegments().get(1);
            count = db.delete(
                    DatabaseHelper.LEADS_TABLE_NAME,
                    AccountsColumns.ID
                            + "="
                            + accountId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            break;

        case ACCOUNT_OPPORTUNITY:
            accountId = uri.getPathSegments().get(1);
            final String opportunityId = uri.getPathSegments().get(3);
            count = db.delete(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                    AccountsOpportunitiesColumns.ACCOUNT_ID
                            + "="
                            + accountId
                            + " AND "
                            + AccountsOpportunitiesColumns.OPPORTUNITY_ID
                            + "="
                            + opportunityId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            break;

        case CONTACT:
            count = db.delete(DatabaseHelper.CONTACTS_TABLE_NAME, where,
                    whereArgs);
            break;

        case CONTACT_ID:
            contactId = uri.getPathSegments().get(1);
            count = db.delete(
                    DatabaseHelper.CONTACTS_TABLE_NAME,
                    ContactsColumns.ID
                            + "="
                            + contactId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            // delete all relationships
            String[] tableNames = mOpenHelper
                    .getRelationshipTables(Util.CONTACTS);
            String whereClause = ModuleFields.CONTACT_ID + "=" + contactId;
            for (final String tableName : tableNames) {
                db.delete(tableName, whereClause, null);
            }
            break;

        case LEAD:
            count = db
                    .delete(DatabaseHelper.LEADS_TABLE_NAME, where, whereArgs);
            break;

        case LEAD_ID:
            final String leadId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.LEADS_TABLE_NAME, LeadsColumns.ID
                    + "=" + leadId
                    + (!TextUtils.isEmpty(where) ? AND + where + ')' : ""),
                    whereArgs);
            break;
        case OPPORTUNITY:
            count = db.delete(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, where,
                    whereArgs);
            break;

        case OPPORTUNITY_ID:
            final String oppId = uri.getPathSegments().get(1);
            count = db.delete(
                    DatabaseHelper.OPPORTUNITIES_TABLE_NAME,
                    OpportunitiesColumns.ID
                            + "="
                            + oppId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            // delete all relationships
            tableNames = mOpenHelper.getRelationshipTables(Util.CONTACTS);
            whereClause = ModuleFields.OPPORTUNITY_ID + "=" + oppId;
            for (final String tableName : tableNames) {
                db.delete(tableName, whereClause, null);
            }
            break;

        case CASE:
            count = db
                    .delete(DatabaseHelper.CASES_TABLE_NAME, where, whereArgs);
            break;

        case CASE_ID:
            final String caseId = uri.getPathSegments().get(1);
            count = db.delete(
                    DatabaseHelper.CASES_TABLE_NAME,
                    OpportunitiesColumns.ID
                            + "="
                            + caseId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            // delete all relationships
            tableNames = mOpenHelper.getRelationshipTables(Util.CONTACTS);
            whereClause = ModuleFields.CONTACT_ID + "=" + caseId;
            for (final String tableName : tableNames) {
                db.delete(tableName, whereClause, null);
            }
            break;

        case CALL:
            count = db
                    .delete(DatabaseHelper.CALLS_TABLE_NAME, where, whereArgs);
            break;

        case CALL_ID:
            final String callId = uri.getPathSegments().get(1);
            count = db.delete(DatabaseHelper.CALLS_TABLE_NAME, CallsColumns.ID
                    + "=" + callId
                    + (!TextUtils.isEmpty(where) ? AND + where + ')' : ""),
                    whereArgs);
            break;

        case MEETING:
            count = db.delete(DatabaseHelper.MEETINGS_TABLE_NAME, where,
                    whereArgs);
            break;

        case MEETING_ID:
            final String meetingId = uri.getPathSegments().get(1);
            count = db.delete(
                    DatabaseHelper.MEETINGS_TABLE_NAME,
                    MeetingsColumns.ID
                            + "="
                            + meetingId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            break;

        case CAMPAIGN:
            count = db.delete(DatabaseHelper.CAMPAIGNS_TABLE_NAME, where,
                    whereArgs);
            break;

        case CAMPAIGN_ID:
            final String campaignId = uri.getPathSegments().get(1);
            count = db.delete(
                    DatabaseHelper.CAMPAIGNS_TABLE_NAME,
                    CampaignColumns.ID
                            + "="
                            + campaignId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            break;

        case RECENT:
        case SYNC:
        case USERS:
        case MODULES:
        case ACCOUNTS_CASES:
        case ACCOUNTS_CONTACTS:
        case ACCOUNTS_OPPORTUNITIES:
        case ACL_ACTIONS:
        case ACL_ROLES:
        case ALARMS:
        case CONTACTS_CASES:
        case CONTACTS_OPPORTUNITIES:
        case LINK_FIELDS:
        case MODULE_FIELDS:
            final String tablename = uri.getPathSegments().get(0);
            count = db.delete(tablename, where, whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /** {@inheritDoc} */
    @Override
    public int update(final Uri uri, final ContentValues values,
            final String where, final String[] whereArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case ACCOUNT:
            count = db.update(DatabaseHelper.ACCOUNTS_TABLE_NAME, values,
                    where, whereArgs);
            break;

        case ACCOUNT_ID:
            String accountId = uri.getPathSegments().get(1);
            count = db.update(
                    DatabaseHelper.ACCOUNTS_TABLE_NAME,
                    values,
                    AccountsColumns.ID
                            + "="
                            + accountId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            break;

        case ACCOUNT_CONTACT:
            accountId = uri.getPathSegments().get(1);
            String contactId = uri.getPathSegments().get(3);

            // update the contact
            String selection = ContactsColumns.ID + "=" + contactId;
            count = db.update(DatabaseHelper.CONTACTS_TABLE_NAME, values,
                    selection, null);

            if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                final String accountName = values
                        .getAsString(ModuleFields.ACCOUNT_NAME);

                if (!TextUtils.isEmpty(accountName)) {
                    // get the account id for the account name
                    selection = AccountsColumns.NAME + "='" + accountName + "'";
                    final Cursor cursor = query(
                            ContentUtils.getModuleUri(Util.ACCOUNTS),
                            Accounts.LIST_PROJECTION, selection, null, null);
                    String newAccountId = null;
                    if (cursor.moveToFirst()) {
                        newAccountId = cursor.getString(0);
                    }
                    cursor.close();

                    if (newAccountId != null && !accountId.equals(newAccountId)) {
                        // the row Id of the new account is not the same as
                        // the old one

                        // update the delete flag to '1' for the old
                        // relationship
                        final ContentValues val2 = new ContentValues();
                        selection = AccountsContactsColumns.ACCOUNT_ID + "="
                                + accountId + " AND "
                                + AccountsContactsColumns.CONTACT_ID + "="
                                + contactId;
                        val2.put(AccountsContactsColumns.DELETED,
                                Util.DELETED_ITEM);
                        db.update(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                                val2, selection, null);
                        Log.i(TAG,
                                "updated deleted flag for the relation ACCOUNT_CONTACT: contactId - "
                                        + contactId + " oldAccountId - "
                                        + accountId);

                        // create a new relationship with the new account
                        final ContentValues val3 = new ContentValues();
                        val3.put(AccountsContactsColumns.ACCOUNT_ID,
                                newAccountId);
                        val3.put(AccountsContactsColumns.CONTACT_ID, contactId);
                        val3.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                        final long rowId = db.insert(
                                DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                                "", val3);
                        if (rowId > 0) {
                            Log.d(TAG, "created relation: contactId 2- "
                                    + contactId + " accountId - "
                                    + newAccountId);
                        }

                    }

                } else {
                    // if the accountName is removed while updating, delete the
                    // relationship

                    // update the delete flag to '1' for the old relationship
                    final ContentValues val2 = new ContentValues();
                    selection = AccountsContactsColumns.ACCOUNT_ID + "="
                            + accountId + " AND "
                            + AccountsContactsColumns.CONTACT_ID + "="
                            + contactId;
                    val2.put(AccountsContactsColumns.DELETED, Util.DELETED_ITEM);
                    db.update(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                            val2, selection, null);
                    Log.i(TAG,
                            "updated deleted flag for the relation: contactId - "
                                    + contactId + " oldAcountId - " + accountId);
                }
            }

            break;

        case ACCOUNT_LEAD:
            accountId = uri.getPathSegments().get(1);
            // TOOD: not handling this case as of now
            count = db.update(
                    DatabaseHelper.LEADS_TABLE_NAME,
                    values,
                    AccountsColumns.ID
                            + "="
                            + accountId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            break;

        case ACCOUNT_OPPORTUNITY:
            accountId = uri.getPathSegments().get(1);
            String opportunityId = uri.getPathSegments().get(3);

            // update the opportunity
            selection = OpportunitiesColumns.ID + "=" + opportunityId;
            count = db.update(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, values,
                    selection, null);

            if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                final String accountName = values
                        .getAsString(ModuleFields.ACCOUNT_NAME);

                if (!TextUtils.isEmpty(accountName)) {
                    // get the account id for the account name
                    selection = AccountsColumns.NAME + "='" + accountName + "'";
                    final Cursor cursor = query(
                            ContentUtils.getModuleUri(Util.ACCOUNTS),
                            Accounts.LIST_PROJECTION, selection, null, null);
                    String newAccountId = null;
                    if (cursor.moveToFirst()) {
                        newAccountId = cursor.getString(0);
                    }
                    cursor.close();

                    if (newAccountId != null && !accountId.equals(newAccountId)) {
                        // the row Id of the new account is not the same as
                        // the old one

                        // update the delete flag to '1' for the old
                        // relationship
                        final ContentValues val2 = new ContentValues();
                        selection = AccountsOpportunitiesColumns.ACCOUNT_ID
                                + "=" + accountId + " AND "
                                + AccountsOpportunitiesColumns.OPPORTUNITY_ID
                                + "=" + opportunityId;
                        val2.put(AccountsOpportunitiesColumns.DELETED,
                                Util.DELETED_ITEM);
                        db.update(
                                DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                                val2, selection, null);

                        // create a new relationship with the new account
                        final ContentValues val3 = new ContentValues();
                        val3.put(AccountsOpportunitiesColumns.ACCOUNT_ID,
                                newAccountId);
                        val3.put(AccountsOpportunitiesColumns.OPPORTUNITY_ID,
                                opportunityId);
                        val3.put(AccountsOpportunitiesColumns.DELETED,
                                Util.NEW_ITEM);
                        final long rowId = db
                                .insert(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                                        "", val3);
                        if (rowId > 0) {
                            Log.d(TAG, "created relation: opportunityId - "
                                    + opportunityId + " accountId - "
                                    + newAccountId);
                        }

                    }

                } else {
                    // if the accountName is removed while updating, delete the
                    // relationship

                    // update the delete flag to '1' for the old relationship
                    final ContentValues val2 = new ContentValues();
                    selection = AccountsOpportunitiesColumns.ACCOUNT_ID + "="
                            + accountId + " AND "
                            + AccountsOpportunitiesColumns.OPPORTUNITY_ID + "="
                            + opportunityId;
                    val2.put(AccountsOpportunitiesColumns.DELETED,
                            Util.DELETED_ITEM);
                    db.update(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                            val2, selection, null);

                }
            }

            break;

        case ACCOUNT_CASE:
            accountId = uri.getPathSegments().get(1);
            String caseId = uri.getPathSegments().get(3);

            // update the case
            selection = CasesColumns.ID + "=" + caseId;
            count = db.update(DatabaseHelper.CASES_TABLE_NAME, values,
                    selection, null);

            if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                final String accountName = values
                        .getAsString(ModuleFields.ACCOUNT_NAME);

                if (!TextUtils.isEmpty(accountName)) {
                    // get the account id for the account name
                    selection = AccountsColumns.NAME + "='" + accountName + "'";
                    final Cursor cursor = query(
                            ContentUtils.getModuleUri(Util.ACCOUNTS),
                            Accounts.LIST_PROJECTION, selection, null, null);
                    String newAccountId = null;
                    if (cursor.moveToFirst()) {
                        newAccountId = cursor.getString(0);
                    }
                    cursor.close();

                    if (newAccountId != null && !accountId.equals(newAccountId)) {
                        // the row Id of the new account is not the same as
                        // the old one

                        // update the delete flag to '1' for the old
                        // relationship
                        final ContentValues val2 = new ContentValues();
                        selection = AccountsCasesColumns.ACCOUNT_ID + "="
                                + accountId + " AND "
                                + AccountsCasesColumns.CASE_ID + "=" + caseId;
                        val2.put(AccountsCasesColumns.DELETED,
                                Util.DELETED_ITEM);
                        db.update(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME,
                                val2, selection, null);
                        Log.i(TAG,
                                "updated deleted flag for the relation: caseId - "
                                        + caseId + " oldAccuntId - "
                                        + accountId);

                        // create a new relationship with the new account
                        final ContentValues val3 = new ContentValues();
                        val3.put(AccountsCasesColumns.ACCOUNT_ID, newAccountId);
                        val3.put(AccountsCasesColumns.CASE_ID, caseId);
                        val3.put(AccountsCasesColumns.DELETED, Util.NEW_ITEM);
                        final long rowId = db.insert(
                                DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME, "",
                                val3);
                        if (rowId > 0) {
                            Log.d(TAG, "created relation: caseId - " + caseId
                                    + " accountId - " + newAccountId);
                        }

                    }
                } else {
                    // if the accountName is removed while updating, delete the
                    // relationship

                    // update the delete flag to '1' for the old relationship
                    final ContentValues val2 = new ContentValues();
                    selection = AccountsCasesColumns.ACCOUNT_ID + "="
                            + accountId + " AND "
                            + AccountsCasesColumns.CASE_ID + "=" + caseId;
                    val2.put(AccountsCasesColumns.DELETED, Util.DELETED_ITEM);
                    db.update(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME, val2,
                            selection, null);
                    Log.d(TAG,
                            "updated deleted flag for the relation: caseId - "
                                    + caseId + " oldaccountId - " + accountId);
                }
            }

            break;

        case CONTACT:
            count = db.update(DatabaseHelper.CONTACTS_TABLE_NAME, values,
                    where, whereArgs);
            break;

        case CONTACT_ID:
            contactId = uri.getPathSegments().get(1);
            count = db.update(
                    DatabaseHelper.CONTACTS_TABLE_NAME,
                    values,
                    ContactsColumns.ID
                            + "="
                            + contactId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);

            String deleted = values.getAsString(ModuleFields.DELETED);
            if (!TextUtils.isEmpty(deleted)
                    && deleted.equals(Util.DELETED_ITEM)) {
                // update the delete flag of all relationships
                final String[] tableNames = mOpenHelper
                        .getRelationshipTables(Util.CONTACTS);
                final String whereClause = ModuleFields.CONTACT_ID + "="
                        + contactId;
                for (final String tableName : tableNames) {
                    db.update(tableName, values, whereClause, null);
                }
            } else {
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    final String accountName = (String) values
                            .get(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName
                                + "'";
                        Cursor cursor = query(
                                ContentUtils.getModuleUri(Util.ACCOUNTS),
                                Accounts.LIST_PROJECTION, selection, null, null);
                        String newAccountId = null;
                        if (cursor.moveToFirst()) {
                            newAccountId = cursor.getString(0);
                        }
                        cursor.close();

                        // get the accountId to which this contact is related to
                        selection = AccountsContactsColumns.CONTACT_ID + "="
                                + contactId;
                        cursor = db
                                .query(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                                        new String[] { AccountsContactsColumns.ACCOUNT_ID },
                                        selection, null, null, null, null);
                        accountId = null;
                        if (cursor.moveToFirst()) {
                            accountId = cursor.getString(0);
                        }
                        cursor.close();

                        if (newAccountId != null && accountId != null
                                && !newAccountId.equals(accountId)) {
                            // the row Id of the new account is not the same
                            // as the old one

                            // update the delete flag to '1' for the old
                            // relationship
                            final ContentValues val2 = new ContentValues();
                            selection = AccountsContactsColumns.ACCOUNT_ID
                                    + "=" + accountId + " AND "
                                    + AccountsContactsColumns.CONTACT_ID + "="
                                    + contactId;
                            val2.put(AccountsContactsColumns.DELETED,
                                    Util.DELETED_ITEM);
                            db.update(
                                    DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                                    val2, selection, null);
                            Log.i(TAG,
                                    "updated deleted flag for the relation: contactId - "
                                            + contactId + " oldAccountId - "
                                            + accountId);

                            // create a new relationship with the new
                            // account
                            final ContentValues val3 = new ContentValues();
                            val3.put(AccountsContactsColumns.ACCOUNT_ID,
                                    newAccountId);
                            val3.put(AccountsContactsColumns.CONTACT_ID,
                                    contactId);
                            val3.put(AccountsContactsColumns.DELETED,
                                    Util.NEW_ITEM);
                            final long rowId = db
                                    .insert(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                                            "", val3);
                            if (rowId > 0) {
                                Log.d(TAG, "created relation: contactId 3- "
                                        + contactId + " accountId - "
                                        + newAccountId);
                            }

                        }

                    } else {
                        // delete the relationship if there exists one

                        final ContentValues relationValues = new ContentValues();
                        selection = AccountsContactsColumns.CONTACT_ID + "="
                                + contactId;
                        relationValues.put(AccountsContactsColumns.DELETED,
                                Util.DELETED_ITEM);
                        db.update(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                                relationValues, selection, null);
                    }
                }
            }

            break;

        case CONTACT_OPPORTUNITY:
            contactId = uri.getPathSegments().get(1);
            opportunityId = uri.getPathSegments().get(3);

            // update the opportunity
            selection = OpportunitiesColumns.ID + "=" + opportunityId;
            count = db.update(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, values,
                    selection, null);

            // update the relationship
            ContentValues relationValues = new ContentValues();
            relationValues.put(ContactsOpportunitiesColumns.CONTACT_ID,
                    contactId);
            relationValues.put(ContactsOpportunitiesColumns.OPPORTUNITY_ID,
                    opportunityId);
            relationValues.put(ContactsOpportunitiesColumns.DELETED,
                    Util.NEW_ITEM);
            long rowId = db.insert(
                    DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME, "",
                    relationValues);
            if (rowId > 0) {
                Log.d(TAG, "created relation: opportunityId - " + opportunityId
                        + " contactId - " + contactId);
            }

            if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                final String accountName = values
                        .getAsString(ModuleFields.ACCOUNT_NAME);

                if (!TextUtils.isEmpty(accountName)) {

                    // get the account id for the account name
                    selection = AccountsColumns.NAME + "='" + accountName + "'";
                    Cursor cursor = query(
                            ContentUtils.getModuleUri(Util.ACCOUNTS),
                            Accounts.LIST_PROJECTION, selection, null, null);
                    String newAccountId = null;
                    if (cursor.moveToFirst()) {
                        newAccountId = cursor.getString(0);
                    }
                    cursor.close();

                    // get the accountId to which this opportunity is related to
                    selection = AccountsOpportunitiesColumns.OPPORTUNITY_ID
                            + "=" + opportunityId;
                    cursor = db
                            .query(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                                    new String[] { AccountsOpportunitiesColumns.ACCOUNT_ID },
                                    selection, null, null, null, null);
                    accountId = null;
                    if (cursor.moveToFirst()) {
                        accountId = cursor.getString(0);
                    }
                    cursor.close();

                    if (newAccountId != null && accountId != null
                            && !newAccountId.equals(accountId)) {
                        // the row Id of the new account is not the same as
                        // the old one

                        // update the delete flag to '1' for the old
                        // relationship
                        final ContentValues val2 = new ContentValues();
                        selection = AccountsOpportunitiesColumns.ACCOUNT_ID
                                + "=" + accountId + " AND "
                                + AccountsOpportunitiesColumns.OPPORTUNITY_ID
                                + "=" + opportunityId;
                        val2.put(AccountsOpportunitiesColumns.DELETED,
                                Util.DELETED_ITEM);
                        db.update(
                                DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                                val2, selection, null);

                        // create a new relationship with the new account
                        final ContentValues val3 = new ContentValues();
                        val3.put(AccountsOpportunitiesColumns.ACCOUNT_ID,
                                newAccountId);
                        val3.put(AccountsOpportunitiesColumns.OPPORTUNITY_ID,
                                opportunityId);
                        val3.put(AccountsOpportunitiesColumns.DELETED,
                                Util.NEW_ITEM);
                        rowId = db
                                .insert(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                                        "", val3);
                        if (rowId > 0) {
                            Log.d(TAG, "created relation: opportunityId - "
                                    + opportunityId + " accountId - "
                                    + newAccountId);
                        }

                    }

                } else {
                    // if the accountName is removed while updating, delete the
                    // relationship

                    // update the delete flag to '1' for the old relationship
                    final ContentValues val2 = new ContentValues();
                    selection = AccountsOpportunitiesColumns.OPPORTUNITY_ID
                            + "=" + opportunityId;
                    val2.put(AccountsOpportunitiesColumns.DELETED,
                            Util.DELETED_ITEM);
                    db.update(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                            val2, selection, null);
                    Log.i(TAG,
                            "updated deleted flag for the relations in CONTACT_OPPORTUNITIES table with opportunityId - "
                                    + opportunityId);

                }
            }

            break;

        case LEAD:
            count = db.update(DatabaseHelper.LEADS_TABLE_NAME, values, where,
                    whereArgs);
            break;

        case LEAD_ID:
            final String leadId = uri.getPathSegments().get(1);

            if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                final String accountName = values
                        .getAsString(ModuleFields.ACCOUNT_NAME);

                if (!TextUtils.isEmpty(accountName)) {

                    // get the account id for the account name
                    selection = AccountsColumns.NAME + "='" + accountName + "'";
                    Cursor cursor = query(
                            ContentUtils.getModuleUri(Util.ACCOUNTS),
                            Accounts.LIST_PROJECTION, selection, null, null);
                    String newAccountId = null;
                    if (cursor.moveToFirst()) {
                        newAccountId = cursor.getString(0);
                    }
                    cursor.close();

                    // get the accountId to which this lead is related to
                    selection = LeadsColumns.ID + "=" + leadId;
                    cursor = db.query(DatabaseHelper.LEADS_TABLE_NAME,
                            new String[] { LeadsColumns.ACCOUNT_ID },
                            selection, null, null, null, null);
                    accountId = null;
                    if (cursor.moveToFirst()) {
                        accountId = cursor.getString(0);
                    }
                    cursor.close();

                    if (newAccountId != null && accountId != null
                            && !newAccountId.equals(accountId)) {
                        // the row Id of the new account is not the same as
                        // the old one

                        // update the field 'account_id'
                        values.put(ModuleFields.ACCOUNT_ID, newAccountId);

                    }

                }
            }

            count = db.update(DatabaseHelper.LEADS_TABLE_NAME, values,
                    LeadsColumns.ID
                            + "="
                            + leadId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);

            break;

        case OPPORTUNITY:
            count = db.update(DatabaseHelper.OPPORTUNITIES_TABLE_NAME, values,
                    where, whereArgs);
            break;

        case OPPORTUNITY_ID:
            opportunityId = uri.getPathSegments().get(1);
            count = db.update(
                    DatabaseHelper.OPPORTUNITIES_TABLE_NAME,
                    values,
                    OpportunitiesColumns.ID
                            + "="
                            + opportunityId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);

            deleted = values.getAsString(ModuleFields.DELETED);
            if (!TextUtils.isEmpty(deleted)
                    && deleted.equals(Util.DELETED_ITEM)) {
                // update the delete flag of all relationships
                final String[] tableNames = mOpenHelper
                        .getRelationshipTables(Util.OPPORTUNITIES);
                final String whereClause = ModuleFields.OPPORTUNITY_ID + "="
                        + opportunityId;
                for (final String tableName : tableNames) {
                    db.update(tableName, values, whereClause, null);
                }
            } else {
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    final String accountName = (String) values
                            .get(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName
                                + "'";
                        Cursor cursor = query(
                                ContentUtils.getModuleUri(Util.ACCOUNTS),
                                Accounts.LIST_PROJECTION, selection, null, null);
                        String newAccountId = null;
                        if (cursor.moveToFirst()) {
                            newAccountId = cursor.getString(0);
                        }
                        cursor.close();

                        // get the accountId to which this opportunity is
                        // related to
                        selection = AccountsOpportunitiesColumns.OPPORTUNITY_ID
                                + "=" + opportunityId;
                        cursor = db
                                .query(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                                        new String[] { AccountsOpportunitiesColumns.ACCOUNT_ID },
                                        selection, null, null, null, null);
                        accountId = null;
                        if (cursor.moveToFirst()) {
                            accountId = cursor.getString(0);
                        }
                        cursor.close();

                        if (newAccountId != null && accountId != null
                                && !newAccountId.equals(accountId)) {
                            // the row Id of the new account is not the same
                            // as the old one

                            // update the delete flag to '1' for the old
                            // relationship
                            final ContentValues val2 = new ContentValues();
                            selection = AccountsOpportunitiesColumns.ACCOUNT_ID
                                    + "="
                                    + accountId
                                    + " AND "
                                    + AccountsOpportunitiesColumns.OPPORTUNITY_ID
                                    + "=" + opportunityId;
                            val2.put(AccountsOpportunitiesColumns.DELETED,
                                    Util.DELETED_ITEM);
                            db.update(
                                    DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                                    val2, selection, null);

                            // create a new relationship with the new
                            // account
                            final ContentValues val3 = new ContentValues();
                            val3.put(AccountsOpportunitiesColumns.ACCOUNT_ID,
                                    newAccountId);
                            val3.put(
                                    AccountsOpportunitiesColumns.OPPORTUNITY_ID,
                                    opportunityId);
                            val3.put(AccountsOpportunitiesColumns.DELETED,
                                    Util.NEW_ITEM);
                            rowId = db
                                    .insert(DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                                            "", val3);
                            if (rowId > 0) {
                                Log.d(TAG, "created relation: opportunityId - "
                                        + opportunityId + " accountId - "
                                        + newAccountId);
                            }

                        }

                    } else {
                        // delete the relationship if there exists one

                        relationValues = new ContentValues();
                        selection = AccountsOpportunitiesColumns.OPPORTUNITY_ID
                                + "=" + opportunityId;
                        relationValues.put(
                                AccountsOpportunitiesColumns.DELETED,
                                Util.DELETED_ITEM);
                        db.update(
                                DatabaseHelper.ACCOUNTS_OPPORTUNITIES_TABLE_NAME,
                                relationValues, selection, null);
                    }
                }
            }

            break;

        case OPPORTUNITY_CONTACT:
            opportunityId = uri.getPathSegments().get(1);
            contactId = uri.getPathSegments().get(3);

            // update the contact
            selection = ContactsColumns.ID + "=" + contactId;
            count = db.update(DatabaseHelper.CONTACTS_TABLE_NAME, values,
                    selection, null);

            // update the relationship
            relationValues = new ContentValues();
            relationValues.put(ContactsOpportunitiesColumns.CONTACT_ID,
                    contactId);
            relationValues.put(ContactsOpportunitiesColumns.OPPORTUNITY_ID,
                    opportunityId);
            relationValues.put(ContactsOpportunitiesColumns.DELETED,
                    Util.NEW_ITEM);
            rowId = db.insert(DatabaseHelper.CONTACTS_OPPORTUNITIES_TABLE_NAME,
                    "", relationValues);
            if (rowId > 0) {
                Log.d(TAG, "created relation: opportunityId - " + opportunityId
                        + " contactId - " + contactId);
            }

            if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                final String accountName = values
                        .getAsString(ModuleFields.ACCOUNT_NAME);

                if (!TextUtils.isEmpty(accountName)) {

                    // get the account id for the account name
                    selection = AccountsColumns.NAME + "='" + accountName + "'";
                    Cursor cursor = query(
                            ContentUtils.getModuleUri(Util.ACCOUNTS),
                            Accounts.LIST_PROJECTION, selection, null, null);
                    String newAccountId = null;
                    if (cursor.moveToFirst()) {
                        newAccountId = cursor.getString(0);
                    }
                    cursor.close();

                    // get the accountId to which this opportunity is related to
                    selection = AccountsContactsColumns.CONTACT_ID + "="
                            + contactId;
                    cursor = db
                            .query(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                                    new String[] { AccountsContactsColumns.ACCOUNT_ID },
                                    selection, null, null, null, null);
                    accountId = null;
                    if (cursor.moveToFirst()) {
                        accountId = cursor.getString(0);
                    }
                    cursor.close();

                    if (newAccountId != null && accountId != null
                            && !newAccountId.equals(accountId)) {
                        // the row Id of the new account is not the same as
                        // the old one

                        // update the delete flag to '1' for the old
                        // relationship
                        final ContentValues val2 = new ContentValues();
                        selection = AccountsContactsColumns.ACCOUNT_ID + "="
                                + accountId + " AND "
                                + AccountsContactsColumns.CONTACT_ID + "="
                                + contactId;
                        val2.put(AccountsContactsColumns.DELETED,
                                Util.DELETED_ITEM);
                        db.update(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                                val2, selection, null);
                        Log.i(TAG,
                                "updated deleted flag for the relation: contactId  OPPORTUNITY_CONTACT- "
                                        + contactId + " oldAccountId - "
                                        + accountId);

                        // create a new relationship with the new account
                        final ContentValues val3 = new ContentValues();
                        val3.put(AccountsContactsColumns.ACCOUNT_ID,
                                newAccountId);
                        val3.put(AccountsContactsColumns.CONTACT_ID,
                                opportunityId);
                        val3.put(AccountsContactsColumns.DELETED, Util.NEW_ITEM);
                        rowId = db.insert(
                                DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                                "", val3);
                        if (rowId > 0) {
                            Log.d(TAG, "created relation: contactId 4- "
                                    + contactId + " accountId - "
                                    + newAccountId);
                        }

                    }

                } else {
                    // if the accountName is removed while updating, delete the
                    // relationship

                    // update the delete flag to '1' for the old relationship
                    final ContentValues val2 = new ContentValues();
                    selection = AccountsContactsColumns.CONTACT_ID + "="
                            + contactId;
                    val2.put(AccountsContactsColumns.DELETED, Util.DELETED_ITEM);
                    db.update(DatabaseHelper.ACCOUNTS_CONTACTS_TABLE_NAME,
                            val2, selection, null);
                    Log.i(TAG,
                            "updated deleted flag for the relations in ACCOUNT_CONTACTS table with contactId - "
                                    + contactId);
                }
            }

            break;

        case CASE:
            count = db.update(DatabaseHelper.CASES_TABLE_NAME, values, where,
                    whereArgs);
            break;

        case CASE_ID:
            caseId = uri.getPathSegments().get(1);
            count = db.update(DatabaseHelper.CASES_TABLE_NAME, values,
                    CasesColumns.ID
                            + "="
                            + caseId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);

            deleted = values.getAsString(ModuleFields.DELETED);
            if (!TextUtils.isEmpty(deleted)
                    && deleted.equals(Util.DELETED_ITEM)) {
                // update the delete flag of all relationships
                final String[] tableNames = mOpenHelper
                        .getRelationshipTables(Util.CASES);
                final String whereClause = Util.CASE_ID + "=" + caseId;
                for (final String tableName : tableNames) {
                    db.update(tableName, values, whereClause, null);
                }
            } else {
                if (values.containsKey(ModuleFields.ACCOUNT_NAME)) {
                    final String accountName = (String) values
                            .get(ModuleFields.ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        // get the account id for the account name
                        selection = AccountsColumns.NAME + "='" + accountName
                                + "'";
                        Cursor cursor = query(
                                ContentUtils.getModuleUri(Util.ACCOUNTS),
                                Accounts.LIST_PROJECTION, selection, null, null);
                        String newAccountId = null;
                        if (cursor.moveToFirst()) {
                            newAccountId = cursor.getString(0);
                        }
                        cursor.close();

                        // get the accountId to which this opportunity is
                        // related to
                        selection = AccountsCasesColumns.CASE_ID + "=" + caseId;
                        cursor = db
                                .query(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME,
                                        new String[] { AccountsCasesColumns.ACCOUNT_ID },
                                        selection, null, null, null, null);
                        accountId = null;
                        if (cursor.moveToFirst()) {
                            accountId = cursor.getString(0);
                        }
                        cursor.close();

                        if (newAccountId != null && accountId != null
                                && !newAccountId.equals(accountId)) {
                            // the row Id of the new account is not the same
                            // as the old one

                            // update the delete flag to '1' for the old
                            // relationship
                            final ContentValues val2 = new ContentValues();
                            selection = AccountsCasesColumns.ACCOUNT_ID + "="
                                    + accountId + " AND "
                                    + AccountsCasesColumns.CASE_ID + "="
                                    + caseId;
                            val2.put(AccountsCasesColumns.DELETED,
                                    Util.DELETED_ITEM);
                            db.update(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME,
                                    val2, selection, null);
                            Log.i(TAG,
                                    "updated deleted flag for the relation: caseId - "
                                            + caseId + " oldAccountId - "
                                            + accountId);

                            // create a new relationship with the new
                            // account
                            final ContentValues val3 = new ContentValues();
                            val3.put(AccountsCasesColumns.ACCOUNT_ID,
                                    newAccountId);
                            val3.put(AccountsCasesColumns.CASE_ID, caseId);
                            val3.put(AccountsCasesColumns.DELETED,
                                    Util.NEW_ITEM);
                            rowId = db.insert(
                                    DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME,
                                    "", val3);
                            if (rowId > 0) {
                                Log.d(TAG, "created relation: caseId - "
                                        + caseId + " accountId - "
                                        + newAccountId);
                            }

                        }

                    } else {
                        // delete the relationship if there exists one

                        relationValues = new ContentValues();
                        selection = AccountsCasesColumns.CASE_ID + "=" + caseId;
                        relationValues.put(AccountsCasesColumns.DELETED,
                                Util.DELETED_ITEM);
                        db.update(DatabaseHelper.ACCOUNTS_CASES_TABLE_NAME,
                                relationValues, selection, null);
                    }
                }
            }

            break;

        case CALL:
            count = db.update(DatabaseHelper.CALLS_TABLE_NAME, values, where,
                    whereArgs);
            break;

        case CALL_ID:
            final String callId = uri.getPathSegments().get(1);
            count = db.update(DatabaseHelper.CALLS_TABLE_NAME, values,
                    CallsColumns.ID
                            + "="
                            + callId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            break;

        case MEETING:
            count = db.update(DatabaseHelper.MEETINGS_TABLE_NAME, values,
                    where, whereArgs);
            break;

        case MEETING_ID:
            final String meetingId = uri.getPathSegments().get(1);
            count = db.update(
                    DatabaseHelper.MEETINGS_TABLE_NAME,
                    values,
                    MeetingsColumns.ID
                            + "="
                            + meetingId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            break;

        case CAMPAIGN:
            count = db.update(DatabaseHelper.CAMPAIGNS_TABLE_NAME, values,
                    where, whereArgs);
            break;

        case CAMPAIGN_ID:
            final String campaignId = uri.getPathSegments().get(1);
            count = db.update(
                    DatabaseHelper.CAMPAIGNS_TABLE_NAME,
                    values,
                    CampaignColumns.ID
                            + "="
                            + campaignId
                            + (!TextUtils.isEmpty(where) ? AND + where + ')'
                                    : ""), whereArgs);
            break;
        case MODULES:
            count = db.update(DatabaseHelper.MODULES_TABLE_NAME, values, where,
                    whereArgs);
            break;

        case RECENT:
            Log.e(TAG, "update made for recent");

            // TODO
            count = 0;
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * Gets the projection map.
     * 
     * @param tableName
     *            the table name
     * @param projections
     *            the projections
     * @return the projection map
     */
    static Map<String, String> getProjectionMap(final String tableName,
            final String[] projections) {
        @SuppressWarnings("unchecked")
        Map<String, String> projectionMap = mProjectionMaps.get(tableName);
        if (projectionMap != null) {
            return projectionMap;
        }
        projectionMap = new HashMap<String, String>();
        for (final String column : projections) {
            projectionMap.put(column, tableName + "." + column);
        }
        mProjectionMaps.put(tableName, projectionMap);
        return projectionMap;
    }

    /** The m projection maps. */
    @SuppressWarnings("rawtypes")
    private static Map<String, Map> mProjectionMaps = new HashMap<String, Map>();

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, "Search" + "/"
                + SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS, ACCOUNT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY,
                DatabaseHelper.ACCOUNTS_TABLE_NAME, ACCOUNT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY,
                Util.ACCOUNTS + HASH_HASH, ACCOUNT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + "/#",
                ACCOUNT_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + HASH
                + Util.CONTACTS, ACCOUNT_CONTACT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + HASH
                + Util.CONTACTS + "/#", ACCOUNT_CONTACT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + HASH
                + Util.LEADS, ACCOUNT_LEAD);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + HASH
                + Util.OPPORTUNITIES, ACCOUNT_OPPORTUNITY);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + HASH
                + Util.OPPORTUNITIES + "/#", ACCOUNT_OPPORTUNITY);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + HASH
                + Util.CASES, ACCOUNT_CASE);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS + HASH
                + Util.CASES + "/#", ACCOUNT_CASE);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CONTACTS, CONTACT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CONTACTS + "/#",
                CONTACT_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY,
                Util.CONTACTS + HASH_HASH, CONTACT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CONTACTS + HASH
                + Util.OPPORTUNITIES, CONTACT_OPPORTUNITY);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CONTACTS + HASH
                + Util.OPPORTUNITIES + "/#", CONTACT_OPPORTUNITY);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY,
                Util.CONTACTS + "/#/case", CONTACT_CASE);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.LEADS, LEAD);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.LEADS + "/#",
                LEAD_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.LEADS + HASH_HASH,
                LEAD);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.LEADS + HASH
                + Util.OPPORTUNITIES, LEAD_OPPORTUNITY);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.OPPORTUNITIES,
                OPPORTUNITY);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY,
                Util.OPPORTUNITIES + "/#", OPPORTUNITY_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.OPPORTUNITIES
                + HASH_HASH, OPPORTUNITY);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.OPPORTUNITIES + HASH
                + Util.CONTACTS, OPPORTUNITY_CONTACT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.OPPORTUNITIES + HASH
                + Util.CONTACTS + "/#", OPPORTUNITY_CONTACT);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.MEETINGS, MEETING);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.MEETINGS + "/#",
                MEETING_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY,
                Util.MEETINGS + HASH_HASH, MEETING);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CALLS, CALL);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CALLS + "/#",
                CALL_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CALLS + HASH_HASH,
                CALL);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CASES, CASE);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CASES + "/#",
                CASE_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CASES + HASH_HASH,
                CASE);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CAMPAIGNS, CAMPAIGN);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CAMPAIGNS + "/#",
                CAMPAIGN_ID);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CAMPAIGNS
                + HASH_HASH, CAMPAIGN);

        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.USERS, USERS);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.RECENT, RECENT);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.SYNC_TABLE, SYNC);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.MODULES, MODULES);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS_CASES,
                ACCOUNTS_CASES);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACCOUNTS_CONTACTS,
                ACCOUNTS_CONTACTS);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY,
                Util.ACCOUNTS_OPPORTUNITIES, ACCOUNTS_OPPORTUNITIES);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ACL_ACTIONS,
                ACL_ACTIONS);
        sUriMatcher
                .addURI(SugarCRMContent.AUTHORITY, Util.ACL_ROLES, ACL_ROLES);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.ALARMS, ALARMS);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.CONTACTS_CASES,
                CONTACTS_CASES);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY,
                Util.CONTACTS_OPPORTUNITIES, CONTACTS_OPPORTUNITIES);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.LINK_FIELDS,
                LINK_FIELDS);
        sUriMatcher.addURI(SugarCRMContent.AUTHORITY, Util.MODULES_FIELDS,
                MODULE_FIELDS);

    }
}
