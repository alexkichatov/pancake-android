package com.imaginea.android.sugarcrm.provider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.test.SyncBaseInstrumentation;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * CRMSyncTestingBase.
 */
public class CRMSyncTestingBase extends SyncBaseInstrumentation {

    /** The m account manager. */
    protected AccountManager mAccountManager;

    /** The m target context. */
    protected Context mTargetContext;

    /** The m account. */
    protected Account mAccount;

    /** The m resolver. */
    protected ContentResolver mResolver;

    /** The m accounts uri. */
    protected Uri mAccountsUri = SugarCRMContent.Accounts.CONTENT_URI;

    /** The m random. */
    private final Random mRandom = new Random();

    /** The Constant TAG. */
    private static final String TAG = CRMSyncTestingBase.class.getSimpleName();

    /** The Constant ACCOUNT_COLUMNS_TO_SKIP. */
    static final Set<String> ACCOUNT_COLUMNS_TO_SKIP = new HashSet<String>();

    static {

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.test.SyncBaseInstrumentation#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTargetContext = getInstrumentation().getTargetContext();

        mAccountManager = AccountManager.get(mTargetContext);
        mAccount = getAccount();
        mResolver = mTargetContext.getContentResolver();
    }

    /**
     * A simple method that syncs the sugar crm provider.
     * 
     * @throws Exception
     *             the exception
     */
    protected void syncSugarCRMAccounts() throws Exception {
        cancelSyncsandDisableAutoSync();
        syncProvider(mAccountsUri, mAccount.name, SugarCRMContent.AUTHORITY);
    }

    /**
     * Creates a new account.
     * 
     * @return Uri of the account created.
     * @throws Exception
     *             the exception
     */
    protected Uri insertAccount() throws Exception {
        final ContentValues m = new ContentValues();
        m.put(ModuleFields.ID, getBeanId());
        m.put(ModuleFields.NAME, getMockAccountName());
        m.put(ModuleFields.DELETED, Util.NEW_ITEM);
        // TODO - date modified - long or string
        final Uri url = mResolver.insert(mAccountsUri, m);

        syncSugarCRMAccounts();
        return url;
    }

    /**
     * dummy implementation to return a random beanId.
     * 
     * @return the bean id
     */
    private String getBeanId() {
        return UUID.randomUUID().toString();
    }

    /**
     * return mock account name.
     * 
     * @return the mock account name
     */
    private String getMockAccountName() {
        return "1_AccountMock" + mRandom.nextInt();
    }

    /**
     * Edits the given account.
     * 
     * @param accountId
     *            accountId of the account to be edited.
     * @param values
     *            the values
     * @throws Exception
     *             the exception
     */
    protected void editAccount(long accountId, ContentValues values)
            throws Exception {

        final Uri uri = ContentUris.withAppendedId(
                SugarCRMContent.Accounts.CONTENT_URI, accountId);
        mResolver.update(uri, values, null, null);
        syncSugarCRMAccounts();
    }

    /**
     * Deletes a given account.
     * 
     * @param uri
     *            the uri
     * @throws Exception
     *             the exception
     */
    protected void deleteAccount(Uri uri) throws Exception {
        mResolver.delete(uri, null, null);
        syncSugarCRMAccounts();
    }

    /**
     * Returns a count of accounts.
     * 
     * @return the accounts count
     */
    protected int getAccountsCount() {
        Cursor cursor;
        cursor = mResolver.query(mAccountsUri, null, null, null, null);
        final int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Returns the ID of the default account.
     * 
     * @return the default account id
     */
    protected int getDefaultAccountId() {
        Cursor accountCursor;
        accountCursor = mResolver.query(mAccountsUri, null, null, null, null);
        accountCursor.moveToNext();
        final int accountId = accountCursor.getInt(accountCursor
                .getColumnIndex(SugarCRMContent.RECORD_ID));
        accountCursor.close();
        return accountId;
    }

    /**
     * Returns the default sugar crm account on the device.
     * 
     * @return the account name
     */
    protected String getAccountName() {
        final Account[] accounts = mAccountManager
                .getAccountsByType(Util.ACCOUNT_TYPE);

        assertTrue("Didn't find any sugar crm accounts", accounts.length > 0);

        final Account account = accounts[accounts.length - 1];
        Log.v(TAG, "Found " + accounts.length
                + " accounts; using the last one, " + account.name);
        return account.name;
    }

    /**
     * Returns the default sugar crm account on the device.
     * 
     * @return an Account obj
     */
    protected Account getAccount() {
        final Account[] accounts = mAccountManager
                .getAccountsByType(Util.ACCOUNT_TYPE);

        assertTrue("Didn't find any sugar crm accounts", accounts.length > 0);

        final Account account = accounts[accounts.length - 1];
        Log.v(TAG, "Found " + accounts.length
                + " accounts; using the last one, " + account.name);
        return account;
    }

    /**
     * Compares two cursors and skips the READ-ONLY columns that do not change.
     * 
     * @param cursor1
     *            the cursor1
     * @param cursor2
     *            the cursor2
     * @param columnsToSkip
     *            the columns to skip
     * @param tableName
     *            the table name
     */
    protected void compareCursors(Cursor cursor1, Cursor cursor2,
            Set<String> columnsToSkip, String tableName) {
        final String[] cols = cursor1.getColumnNames();
        final int length = cols.length;

        assertEquals(tableName + " count failed to match", cursor1.getCount(),
                cursor2.getCount());
        final Map<String, String> row = new HashMap<String, String>();
        while (cursor1.moveToNext() && cursor2.moveToNext()) {
            for (int i = 0; i < length; i++) {
                final String col = cols[i];
                if (columnsToSkip != null && columnsToSkip.contains(col)) {
                    continue;
                }
                row.put(col, cursor1.getString(i));

                assertEquals("Row: " + row + " Table: " + tableName + ": "
                        + cols[i] + " failed to match", cursor1.getString(i),
                        cursor2.getString(i));
            }
        }
    }
}
