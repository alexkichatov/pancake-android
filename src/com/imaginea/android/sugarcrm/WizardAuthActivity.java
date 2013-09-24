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
 * FileName : WizardAuthActivity 
 * Desciption: 
 *             WizardAuthActivity is same as  Wizard Activity, but with account manager
 * integration works only with android 2.0 and above-minSdkVersion>=5
 ******************************************************************************/

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
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.ui.RecentModuleMultiPaneActivity;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

/**
 * The Class WizardAuthActivity.
 */
public class WizardAuthActivity extends AccountAuthenticatorActivity {

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
    private Boolean mConfirmCredentials = false;

    /** The Constant PARAM_CONFIRMCREDENTIALS. */
    public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";

    /** The Constant PARAM_AUTHTOKEN_TYPE. */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    /** The m username. */
    private String mUsername;

    /** The m password. */
    private String mPassword;

    /** The m account manager. */
    private AccountManager mAccountManager;

    /** The m authtoken. */
    private String mAuthtoken;

    /** The m authtoken type. */
    private String mAuthtokenType;

    /** Was the original caller asking for an entirely new account?. */
    private boolean mRequestNewAccount = false;

    /** The m app. */
    private SugarCrmApp mApp;

    /** The m auth task. */
    private AuthenticationTask mAuthTask;

    /** The m progress dialog. */
    private ProgressDialog mProgressDialog;

    /** The m url edit text. */
    private EditText mUrlEditText;

    /** The m usr edit text. */
    private EditText mUsrEditText;

    /** The m password edit text. */
    private EditText mPasswordEditText;

    /** The result wait. */
    public static Semaphore resultWait = new Semaphore(0);

    /** The Constant LOG_TAG. */
    private static final String LOG_TAG = WizardAuthActivity.class
            .getSimpleName();

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.accounts.AccountAuthenticatorActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mApp = (SugarCrmApp) getApplication();

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
        String restUrlValue = SugarCrmSettings
                .getSugarRestUrl(WizardAuthActivity.this);

        // valid URL
        Log.i(LOG_TAG, "resturl value = " + restUrlValue);
        // first time Url should read from mUrlEditText
        if (restUrlValue == null || (!URLUtil.isValidUrl(restUrlValue))) {
            restUrlValue = mUrlEditText.getText().toString();
        }
        mUrlEditText.setText(restUrlValue);

        /* Override the Done Key in Keyboard and mapped with Handle Login */
        mUrlEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mUrlEditText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handleLogin(v);
                    return true;
                } else {
                    return false;
                }
            }
        });
        mUsrEditText = (EditText) findViewById(R.id.loginUsername);
        mPasswordEditText = (EditText) findViewById(R.id.loginPassword);
    }

    /**
     * Gets the account.
     * 
     * @param userName
     *            the user name
     * @return the account
     */
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
     * Handle login.
     * 
     * @param view
     *            the view
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

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        Log.i(LOG_TAG, "onNewIntent");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPause()
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onStop()
     */
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
    /**
     * The Class AuthenticationTask.
     */
    class AuthenticationTask extends AsyncTask<Object, Object, Object>
            implements SyncStatusObserver {

        /** The usr. */
        private String usr;

        /** The has exceptions. */
        private boolean hasExceptions = false;

        /** The sce desc. */
        private String sceDesc;

        /** The prefs. */
        private SharedPreferences prefs;

        /** The sync handler. */
        private Object syncHandler;

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prefs = PreferenceManager
                    .getDefaultSharedPreferences(WizardAuthActivity.this);
            mProgressDialog = ViewUtil.getProgressDialog(
                    WizardAuthActivity.this,
                    getString(R.string.authenticatingMsg), false);
            mProgressDialog.show();
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(final Object... values) {
            super.onProgressUpdate(values);
            final String msg = (String) values[0];
            mProgressDialog.setMessage(msg);
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Object doInBackground(final Object... args) {

            usr = args[0].toString();
            // TODO this settings are important - make it cleaner later to use
            // the same variables
            mUsername = usr;
            mPassword = args[1].toString();
            String restUrl = null;
            // first time Url should read from mUrlEditText
            if (mUrlEditText != null) {
                restUrl = mUrlEditText.getText().toString();
            }
            if (restUrl == null || !(URLUtil.isValidUrl(restUrl))) {
                restUrl = SugarCrmSettings
                        .getSugarRestUrl(WizardAuthActivity.this);

            }

            String sessionId = null;
            if (!(Util.isNetworkOn(getBaseContext()))) {

                hasExceptions = true;
                sceDesc = "Network is not avialable";

                return null;
            }

            try {
                Log.i("ee", "prnt usr name = " + usr + " Pass = " + mPassword
                        + " url = " + restUrl);
                sessionId = Rest.loginToSugarCRM(restUrl, usr, mPassword);
                Log.i(LOG_TAG, "SessionId - " + sessionId);
                Log.i("ee", "SessionId usr name = " + sessionId);

                if (sessionId != null) {
                    onAuthenticationResult();

                    final Editor editor = prefs.edit();
                    editor.putString(Util.PREF_USERNAME, usr);
                    editor.putString(Util.PREF_REST_URL, restUrl);
                    editor.commit();

                } else {
                    return null;
                }
                final boolean metaDataSyncCompleted = prefs.getBoolean(
                        Util.SYNC_METADATA_COMPLETED, false);
                if (!metaDataSyncCompleted) {
                    // sync meta-data - modules and acl roles and actions for a
                    // user
                    publishProgress(getString(R.string.configureAppMsg));
                    startMetaDataSync();

                    WizardAuthActivity.resultWait.acquire();

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

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(final Object sessionId) {
            super.onPostExecute(sessionId);
            if (isCancelled()
                    && !prefs.getBoolean(Util.SYNC_METADATA_COMPLETED, false)) {
                return;
            }

            if (hasExceptions) {
                Toast.makeText(WizardAuthActivity.this, sceDesc,
                        Toast.LENGTH_SHORT).show();

                if (mProgressDialog != null) {
                    Log.d(LOG_TAG, "--- AsynchStartup() onPostExecute "
                            + "(Dismissing progress meter)");
                    mProgressDialog.cancel();
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                setResult(RESULT_CANCELED);

            } else {

                // save the sessionId in the application context after the
                // successful login
                mApp.setSessionId(sessionId.toString());

                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                setResult(RESULT_OK);
                final Long syncScreenCheck = prefs.getLong(
                        Util.PREF_SYNC_START_TIME, 0L);
                /*
                 * Once First time login final is completed start final the sync
                 * for final last seven Days
                 */
                if (syncScreenCheck == 0L) {
                    final Bundle extras = new Bundle();

                    extras.putBoolean(
                            ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
                    extras.putInt(Util.SYNC_TYPE, Util.SYNC_MODULES_DATA);
                    savePrefs();
                    ContentResolver.requestSync(mApp.getAccount(usr),
                            SugarCRMProvider.AUTHORITY, extras);
                }
                showDashboard();

            }
        }

        /**
         * Save prefs.
         */
        private void savePrefs() {
            final SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
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

        /*
         * (non-Javadoc)
         * 
         * @see android.content.SyncStatusObserver#onStatusChanged(int)
         */
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

        /**
         * Start meta data sync.
         */
        private void startMetaDataSync() {
            Log.d(LOG_TAG, "startMetaDataSync");
            final Bundle extras = new Bundle();
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
            extras.putInt(Util.SYNC_TYPE, Util.SYNC_ALL_META_DATA);
            final SugarCrmApp app = (SugarCrmApp) getApplication();
            final String usr = SugarCrmSettings.getUsername(
                    WizardAuthActivity.this).toString();
            ContentResolver.requestSync(app.getAccount(usr),
                    SugarCRMProvider.AUTHORITY, extras);

            syncHandler = ContentResolver.addStatusChangeListener(2, this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    protected void onActivityResult(final int requestCode,
            final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "Onactivityresult called!!! " + requestCode);
        if (requestCode == Util.SYNC_DATA_REQUEST_CODE) {
            showDashboard();
        }
    }

    /**
     * Show dashboard.
     */
    void showDashboard() {
        Log.d("WizardAuthActivity", "show dashboard called");
        if (ViewUtil.isHoneycombTablet(WizardAuthActivity.this)) {
            finish();
            final Intent myIntent = new Intent(WizardAuthActivity.this,
                    RecentModuleMultiPaneActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            myIntent.putExtra(RestConstants.MODULE_NAME, Util.RECENT);
            startActivity(myIntent);
        } else {

            final Intent myIntent = new Intent(WizardAuthActivity.this,
                    ModulesActivity.class);
            myIntent.putExtra(RestConstants.MODULE_NAME, Util.RECENT);
            startActivity(myIntent);
        }
        finish();

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

    // *** standard onDestroy()

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onDestroy()
     */
    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy(" + this + ")");

        if (mProgressDialog != null) {
            Log.d(LOG_TAG, "onDestroy(Dismissing progress meter)");
            mProgressDialog.dismiss();
            mProgressDialog = null;
        } else {
            Log.d(LOG_TAG, "onDestroy(Not required to dismiss progress meter)");
        }

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
