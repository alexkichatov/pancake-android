package com.imaginea.android.sugarcrm;

import java.util.concurrent.Semaphore;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SyncStatusObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

//import android.accounts.AccountAuthenticatorActivity;

/**
 * WizardAuthActivity, same as Wizard Activity, but with account manager
 * integration works only with android 2.0 and above-minSdkVersion>=5
 * 
 * //TODO - as password is saved in Account Manager with Settings credential
 * storage, we donot have to store the password
 * 
 * @author Vasavi
 * @author chander
 */
public class WizardAuthActivity extends AccountAuthenticatorActivity {

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
    private Boolean mConfirmCredentials = false;

    public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";

    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    private String mUsername;

    private String mPassword;

    private AccountManager mAccountManager;

    private String mAuthtoken;

    private String mAuthtokenType;

    /** Was the original caller asking for an entirely new account? */
    protected boolean mRequestNewAccount = false;

    private SugarCrmApp app;

    private AuthenticationTask mAuthTask;

    private ProgressDialog mProgressDialog;

    private EditText mUrlEditText;
    private EditText mUsrEditText;
    private EditText mPasswordEditText;

    public static Semaphore resultWait = new Semaphore(0);

    private static final String LOG_TAG = WizardAuthActivity.class
            .getSimpleName();

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        app = (SugarCrmApp) getApplication();

        mAccountManager = AccountManager.get(this);
        final Intent intent = getIntent();
        mUsername = intent.getStringExtra(Util.PREF_USERNAME);
        mAuthtokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
        mRequestNewAccount = mUsername == null;
        mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRMCREDENTIALS,
                false);

        Log.i(LOG_TAG, "request new: " + mRequestNewAccount);

        final String restUrl = SugarCrmSettings
                .getSugarRestUrl(WizardAuthActivity.this);
        final String usr = SugarCrmSettings
                .getUsername(WizardAuthActivity.this);
        Log.i(LOG_TAG, "restUrl - " + restUrl + "\nusr - " + usr + "\n");

        final Account userAccount = getAccount(usr);

        if (!TextUtils.isEmpty(usr) && (userAccount != null)
                && (mRequestNewAccount)) {
            mAuthTask = new AuthenticationTask();
            final String pwd = mAccountManager.getPassword(userAccount);
            mAuthTask.execute(usr, pwd);
            return;
        }

        setContentView(R.layout.authentication_activity);

        final Button next = (Button) findViewById(R.id.actionNext);
        next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                handleLogin(v);
            }
        });

        mUrlEditText = (EditText) findViewById(R.id.wizardUrl);
        mUsrEditText = (EditText) findViewById(R.id.loginUsername);
        mPasswordEditText = (EditText) findViewById(R.id.loginPassword);
    }

    private Account getAccount(final String userName) {

        final Account[] accounts = mAccountManager
                .getAccountsByType(Util.ACCOUNT_TYPE);
        Account userAccount = null;
        for (final Account account : accounts) {
            // never print the password
            Log.i(LOG_TAG, "user name is " + account.name);
            if (account.name.equals(userName)) {
                userAccount = account;
                break;
            }
        }
        return userAccount;
    }

    /**
     * <p>
     * handleLogin
     * </p>
     * 
     * @param view
     *            a {@link android.view.View} object.
     */
    public void handleLogin(final View view) {

        final String usr = mUsrEditText.getText().toString();
        final String pwd = mPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(mUrlEditText.getText().toString())) {
            Toast.makeText(WizardAuthActivity.this, "Url should not be empty",
                    Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(usr)) {
            Toast.makeText(WizardAuthActivity.this,
                    "User name should not be empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(WizardAuthActivity.this,
                    "Password name should not be empty", Toast.LENGTH_SHORT)
                    .show();
        } else {
            mAuthTask = new AuthenticationTask();
            mAuthTask.execute(usr, pwd);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        Log.i(LOG_TAG, "onNewIntent");
    }

    /** {@inheritDoc} */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
        if (mAuthTask != null
                && mAuthTask.getStatus() == AsyncTask.Status.RUNNING) {
            final SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(WizardAuthActivity.this);
            if (!prefs.getBoolean(Util.SYNC_METADATA_COMPLETED, false)) {
                mAuthTask.cancel(true);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        WizardAuthActivity.resultWait.release();
    }

    /** {@inheritDoc} */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    // Task to authenticate
    class AuthenticationTask extends AsyncTask<Object, Object, Object>
            implements SyncStatusObserver {
        private String usr;

        boolean hasExceptions = false;

        private String sceDesc;

        SharedPreferences prefs;

        Object syncHandler;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ;
            prefs = PreferenceManager
                    .getDefaultSharedPreferences(WizardAuthActivity.this);
            mProgressDialog = ViewUtil.getProgressDialog(
                    WizardAuthActivity.this,
                    getString(R.string.authenticatingMsg), false);
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(final Object... values) {
            super.onProgressUpdate(values);
            final String msg = (String) values[0];
            mProgressDialog.setMessage(msg);
        }

        @Override
        protected Object doInBackground(final Object... args) {
            /*
             * arg[0] : String - username arg[1] : String - password
             */
            usr = args[0].toString();
            // TODO this settings are important - make it cleaner later to use
            // the same variables
            mUsername = usr;
            mPassword = args[1].toString();

            String restUrl = SugarCrmSettings
                    .getSugarRestUrl(WizardAuthActivity.this);
            // first time Url should read from mUrlEditText
            if (restUrl == null) {
                restUrl = mUrlEditText.getText().toString();
            }

            String sessionId = null;
            if (!(Util.isNetworkOn(getBaseContext()))) {

                hasExceptions = true;
                sceDesc = "Network is not avialable";
                // Toast.makeText(WizardAuthActivity.this,
                // "Network is not avialable", Toast.LENGTH_SHORT).show();
                // setResult(RESULT_OK);
                // finish();

                return null;
            }

            try {
                sessionId = Rest.loginToSugarCRM(restUrl, usr, mPassword);
                Log.i(LOG_TAG, "SessionId - " + sessionId);
                if (sessionId != null) {
                    onAuthenticationResult();

                    final Editor editor = prefs.edit();
                    editor.putString(Util.PREF_USERNAME, usr);
                    editor.putString(Util.PREF_REST_URL, restUrl);
                    editor.commit();

                } else
                    return null;
                final boolean metaDataSyncCompleted = prefs.getBoolean(
                        Util.SYNC_METADATA_COMPLETED, false);
                if (!metaDataSyncCompleted) {
                    // sync meta-data - modules and acl roles and actions for a
                    // user
                    publishProgress(getString(R.string.configureAppMsg));
                    startMetaDataSync();
                    // TODO - commenting the 2 lines below as the group table
                    // logic is not needed
                    // for this release
                    // DatabaseHelper databaseHelper = new
                    // DatabaseHelper(getBaseContext());
                    // databaseHelper.executeSQLFromFile(Util.SQL_FILE);
                    // TODO - note , we need a mechanism to release the lock
                    // incase the metadata
                    // sync never happens, or its gets killed.
                    WizardAuthActivity.resultWait.acquire();
                    // while(!prefs.getBoolean(Util.SYNC_METADATA_COMPLETED,
                    // false)) {
                    // Thread.sleep(2000);
                    // }
                }

            } catch (final SugarCrmException sce) {
                hasExceptions = true;
                sceDesc = sce.getDescription();
                Log.e(LOG_TAG, sceDesc, sce);
            } catch (final InterruptedException ie) {
                hasExceptions = true;
                sceDesc = ie.getMessage();
                Log.e(LOG_TAG, ie.getMessage(), ie);
            }
            // test Account manager code
            return sessionId;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(final Object sessionId) {
            super.onPostExecute(sessionId);
            if (isCancelled()
                    && !prefs.getBoolean(Util.SYNC_METADATA_COMPLETED, false))
                return;

            if (hasExceptions) {
                Toast.makeText(WizardAuthActivity.this, sceDesc,
                        Toast.LENGTH_SHORT).show();
                mProgressDialog.cancel();
                mProgressDialog.dismiss();
                mProgressDialog = null;
                setResult(RESULT_CANCELED);

                finish();

            } else {

                // save the sessionId in the application context after the
                // successful login
                app.setSessionId(sessionId.toString());
                Log.d(LOG_TAG, "Cancelling progress bar which is showing:"
                        + mProgressDialog.isShowing());
                mProgressDialog.cancel();
                mProgressDialog.dismiss();
                mProgressDialog = null;
                setResult(RESULT_OK);
                finish();
            }
        }

        @Override
        public void onStatusChanged(final int which) {
            Log.d(LOG_TAG, "onStatusChanged:" + which);

            final SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            final boolean metaDataSyncCompleted = pref.getBoolean(
                    Util.SYNC_METADATA_COMPLETED, false);
            if (metaDataSyncCompleted) {
                WizardAuthActivity.resultWait.release();
                ContentResolver.removeStatusChangeListener(syncHandler);
            }
        }

        private void startMetaDataSync() {
            Log.d(LOG_TAG, "startMetaDataSync");
            final Bundle extras = new Bundle();
            // extras.putInt(key, value)
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
            extras.putInt(Util.SYNC_TYPE, Util.SYNC_ALL_META_DATA);
            final SugarCrmApp app = (SugarCrmApp) getApplication();
            final String usr = SugarCrmSettings.getUsername(
                    WizardAuthActivity.this).toString();
            ContentResolver.requestSync(app.getAccount(usr),
                    SugarCRMProvider.AUTHORITY, extras);
            // ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_PENDING,
            // this);
            // TODO -this is API - level 8 - using 2 for testing
            syncHandler = ContentResolver.addStatusChangeListener(2, this);
        }
    }

    /**
     * new method for back presses in android 2.0, instead of the standard
     * mechanism defined in the docs to handle legacy applications we use
     * version code to handle back button... implement onKeyDown for older
     * versions and use Override on that.
     */
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /** {@inheritDoc} */
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
            if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                Log.v(LOG_TAG, "OnBackButton: onKeyDown "
                        + Build.VERSION.SDK_INT);
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR) {

                setResult(RESULT_CANCELED);
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the currently selected menu XML resource.
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings:
            final Intent myIntent = new Intent(WizardAuthActivity.this,
                    SugarCrmSettings.class);
            WizardAuthActivity.this.startActivity(myIntent);
            return true;

        }
        return false;
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     */
    public void onAuthenticationResult() {
        Log.i(LOG_TAG, "onAuthenticationResult()");
        final Intent intent = new Intent();
        if (!mConfirmCredentials) {
            Log.i(LOG_TAG, "finishLogin()");
            final Account account = new Account(mUsername, Util.ACCOUNT_TYPE);

            if (mRequestNewAccount) {
                mAccountManager.addAccountExplicitly(account, mPassword, null);
                // Set contacts sync for this account.
                ContentResolver.setSyncAutomatically(account,
                        SugarCRMProvider.AUTHORITY, true);
            } else {
                mAccountManager.setPassword(account, mPassword);
            }

            mAuthtoken = mPassword;
            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Util.ACCOUNT_TYPE);
            if (mAuthtokenType != null
                    && mAuthtokenType.equals(Util.AUTHTOKEN_TYPE)) {
                intent.putExtra(AccountManager.KEY_AUTHTOKEN, mAuthtoken);
            }
        } else {
            Log.i(LOG_TAG, "finishConfirmCredentials()");
            final Account account = new Account(mUsername, Util.ACCOUNT_TYPE);
            mAccountManager.setPassword(account, mPassword);

            intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, true);
        }
        setAccountAuthenticatorResult(intent.getExtras());

    }
}
