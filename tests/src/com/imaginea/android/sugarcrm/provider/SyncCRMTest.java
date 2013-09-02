package com.imaginea.android.sugarcrm.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;

import com.imaginea.android.sugarcrm.ModuleFields;

/**
 * Sugar CRM Sync instrumentation tests. Testing creation of new accounts,
 * deleting accounts, editing accounts.
 */
public class SyncCRMTest extends CRMSyncTestingBase {

    /*
     * (non-Javadoc)
     * 
     * @see com.imaginea.android.sugarcrm.provider.CRMSyncTestingBase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test create new account.
     * 
     * @throws Exception
     *             the exception
     */
    public void testCreateNewAccount() throws Exception {
        final int countBeforeNewAccount = getAccountsCount();
        insertAccount();
        assertTrue("No New account was added. ",
                getAccountsCount() > countBeforeNewAccount);
    }

    /**
     * Test edit account name.
     * 
     * @throws Exception
     *             the exception
     */
    public void testEditAccountName() throws Exception {
        Cursor cursor;
        cursor = mResolver.query(mAccountsUri, null, null, null, null);

        final int countBeforeNewAccount = cursor.getCount();
        cursor.moveToNext();
        final Time time = new Time();
        time.setToNow();
        final String newTitle = cursor.getString(cursor
                .getColumnIndex(ModuleFields.NAME)) + time.toString();

        final long accountId = cursor.getLong(cursor
                .getColumnIndex(SugarCRMContent.RECORD_ID));

        cursor.close();
        final ContentValues values = new ContentValues();
        values.put(ModuleFields.NAME, newTitle);
        // values.put(ModuleFields., value)
        editAccount(accountId, values);
        cursor = mResolver.query(mAccountsUri, null, null, null, null);
        assertTrue("Events count should remain same.",
                getAccountsCount() == countBeforeNewAccount);

        while (cursor.moveToNext()) {
            if (cursor
                    .getLong(cursor.getColumnIndex(SugarCRMContent.RECORD_ID)) == accountId) {
                assertEquals(cursor.getString(cursor
                        .getColumnIndex(ModuleFields.NAME)), newTitle);
                break;
            }
        }
        cursor.close();
    }

    /**
     * Test create and delete account.
     * 
     * @throws Exception
     *             the exception
     */
    public void testCreateAndDeleteAccount() throws Exception {
        syncSugarCRMAccounts();
        final int countBeforeNewAccount = getAccountsCount();
        final Uri insertUri = insertAccount();

        assertTrue("A account should have been created.",
                getAccountsCount() > countBeforeNewAccount);
        deleteAccount(insertUri);
        assertEquals("Account should have been deleted.",
                countBeforeNewAccount, getAccountsCount());
    }

    /**
     * Test sync modules.
     * 
     * @throws Exception
     *             the exception
     */
    public void testSyncModules() throws Exception {
        insertAccount();
        final Bundle extras = new Bundle();
        // extras.putInt(key, value)
        ContentResolver.requestSync(getAccount(), SugarCRMProvider.AUTHORITY,
                extras);
    }
}
