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
 * FileName : EditModuleDetailFragment 
 * Description : EditModuleDetailFragment, used on phones and Tablet  to  edit the details of module
 ******************************************************************************/

package com.imaginea.android.sugarcrm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.imaginea.android.sugarcrm.CustomActionbar.Action;
import com.imaginea.android.sugarcrm.CustomActionbar.IntentAction;
import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Accounts;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AccountsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.UserColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Users;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.services.SugarService;
import com.imaginea.android.sugarcrm.ui.BaseActivity;
import com.imaginea.android.sugarcrm.ui.RecentModuleMultiPaneActivity;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.ImportContactsUtility;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.ModuleFieldValidator;
import com.imaginea.android.sugarcrm.util.ServiceHelper;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

/**
 * The Class EditModuleDetailFragment.
 */
public class EditModuleDetailFragment extends Fragment {

    /** The Constant TAG. */
    private final static String TAG = "EditModuleDetail";

    /** The mode. */
    private int MODE = -1;

    /** The m details table. */
    private ViewGroup mDetailsTable;

    /** The m details optional table. */
    private ViewGroup mDetailsOptionalTable;

    /** The m cursor. */
    private Cursor mCursor;

    /** The date view. */
    private AutoCompleteTextView dateView;

    /** The time view. */
    private AutoCompleteTextView timeView;

    /** The m sugar bean id. */
    private String mSugarBeanId;

    /** The m module name. */
    private String mModuleName;

    /** The m row id. */
    private String mRowId;

    /** The import flag. */
    private int importFlag;

    /** The m select fields. */
    private String[] mSelectFields;

    /** The m intent uri. */
    private Uri mIntentUri;

    /** The m db helper. */
    private DatabaseHelper mDbHelper;

    /** The m task. */
    private LoadContentTask mTask;

    /** The m messenger. */
    private Messenger mMessenger;

    /** The m status handler. */
    private StatusHandler mStatusHandler;

    /** The m account adapter. */
    private AutoSuggestAdapter mAccountAdapter;

    /** The m user adapter. */
    private AutoSuggestAdapter mUserAdapter;

    /** The m account cursor. */
    private Cursor mAccountCursor;

    /** The m user cursor. */
    private Cursor mUserCursor;

    /** The m selected account name. */
    private String mSelectedAccountName;

    /** The m selected user name. */
    private String mSelectedUserName;

    /** The m account name. */
    private String mAccountName;

    /** The m user name. */
    private String mUserName;

    /** The m progress dialog. */
    private ProgressDialog mProgressDialog;

    /** The has error. */
    private boolean hasError;

    /** The m description. */
    private String mDescription;

    /** The m parent. */
    private RelativeLayout mParent;

    /** The insert successful. Flag */
    public static boolean bInsertSuccessful = false;

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mParent = (RelativeLayout) inflater.inflate(R.layout.edit_details,
                container, false);

        return mParent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDbHelper = new DatabaseHelper(getActivity());
        final Intent intent = BaseActivity
                .fragmentArgumentsToIntent(getArguments());
        final Bundle extras = intent.getExtras();

        mModuleName = Util.CONTACTS;
        if (extras != null) {
            // i always get the module name
            mModuleName = extras.getString(RestConstants.MODULE_NAME);
            importFlag = extras.getInt(Util.IMPORT_FLAG);
            mRowId = intent.getStringExtra(Util.ROW_ID);
            mSugarBeanId = intent.getStringExtra(RestConstants.BEAN_ID);
        }
        // when the user comes from the relationships, intent.getData() won't be
        // null
        mIntentUri = intent.getData();
        Log.v(TAG, "uri - " + (mIntentUri != null ? mIntentUri : ""));

        if (intent.getData() != null && mRowId != null) {
            MODE = Util.EDIT_RELATIONSHIP_MODE;
        } else if (mRowId != null) {
            MODE = Util.EDIT_ORPHAN_MODE;
        } else if (intent.getData() != null) {
            MODE = Util.NEW_RELATIONSHIP_MODE;
        } else {
            MODE = Util.NEW_ORPHAN_MODE;
        }

        Log.v(TAG, "mode - " + MODE);

        if (intent.getData() == null && MODE == Util.EDIT_ORPHAN_MODE) {
            mIntentUri = Uri.withAppendedPath(
                    ContentUtils.getModuleUri(mModuleName), mRowId);
            intent.setData(mIntentUri);
        } else if (intent.getData() == null && MODE == Util.NEW_ORPHAN_MODE) {
            mIntentUri = ContentUtils.getModuleUri(mModuleName);
            intent.setData(mIntentUri);
        }

        mSelectFields = ContentUtils.getModuleProjections(mModuleName);

        mTask = new LoadContentTask(getActivity());
        mTask.execute(null, null, null);

        if (importFlag == Util.CONTACT_IMPORT_FLAG) {
            importContact();
        }

    }

    /**
     * Sets the up action bar.
     */
    private void setUpActionBar() {

        final CustomActionbar actionBar = (CustomActionbar) getActivity()
                .findViewById(R.id.custom_actionbar);
        final Intent myIntent;
        final int[] saveResourcesId;
        if (ViewUtil.isHoneycombTablet(getActivity())) {
            final int[] saveTabletResourcesId = {
                    R.drawable.ico_actionbar_done_pressed,
                    R.drawable.ico_actionbar_done_pressed, R.drawable.edit_done };
            myIntent = new Intent(getActivity(),
                    RecentModuleMultiPaneActivity.class);
            saveResourcesId = saveTabletResourcesId;
        } else {
            final int[] savePhoneResourcesId = {
                    R.drawable.ico_m_actionbar_done_pressed,
                    R.drawable.ico_m_actionbar_done_pressed,
                    R.drawable.ico_m_actionbar_done_nor };
            saveResourcesId = savePhoneResourcesId;
            myIntent = new Intent(getActivity(), ModulesActivity.class);
        }

        final Action recentAction = new IntentAction(getActivity(), myIntent);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.putExtra(RestConstants.MODULE_NAME, Util.RECENT);
        actionBar.setHomeAction(recentAction);

        String header = null;
        if (mCursor != null) {
            if (mCursor.moveToNext()) {
                if (mModuleName.equals(Util.CONTACTS)
                        || mModuleName.equals(Util.LEADS)) {
                    header = mCursor.getString(mCursor
                            .getColumnIndex(mSelectFields[2]))
                            + mCursor.getString(mCursor
                                    .getColumnIndex(mSelectFields[3]));

                } else {
                    header = mCursor.getString(mCursor
                            .getColumnIndex(mSelectFields[2]));

                }
            }
        }
        if (header == null) {
            header = mModuleName;
        }
        actionBar.setTitle(header);
        /* Set the Pressed State to Save Image */
        final ImageView doneImageView = (ImageView) actionBar
                .findViewById(R.id.editDoneImg);
        doneImageView.setImageDrawable(Util.getPressedImage(getActivity()
                .getBaseContext(), saveResourcesId));

        final ImageView back = (ImageView) actionBar
                .findViewById(R.id.actionbar_back);
        final ImageView logo = (ImageView) actionBar
                .findViewById(R.id.actionbar_logo);
        logo.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        final LinearLayout actionView = (LinearLayout) actionBar
                .findViewById(R.id.logo);
        actionView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().showDialog(R.string.discard);

            }
        });
        final LinearLayout editDoneView = (LinearLayout) getActivity()
                .findViewById(R.id.editDone);
        editDoneView.setVisibility(View.VISIBLE);
        editDoneView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveModuleItem(getActivity().getCurrentFocus());

            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        SugarService.unregisterMessenger(mMessenger);
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }

        if (mAccountCursor != null) {
            mAccountCursor.close();
        }

        if (mUserCursor != null) {
            mUserCursor.close();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mMessenger == null) {
            mStatusHandler = new StatusHandler();
            mMessenger = new Messenger(mStatusHandler);
        }
        SugarService.registerMessenger(mMessenger);
    }

    /**
     * The Class LoadContentTask.
     */
    class LoadContentTask extends AsyncTask<Object, Object, Object> {

        /** The static rows count. */
        int staticRowsCount;

        /** The m context. */
        Context mContext;

        /** The Constant STATIC_ROW. */
        final static int STATIC_ROW = 1;

        /** The Constant DYNAMIC_ROW. */
        final static int DYNAMIC_ROW = 2;

        /** The Constant SAVE_BUTTON. */
        final static int SAVE_BUTTON = 3;

        /** The Constant INPUT_TYPE. */
        final static int INPUT_TYPE = 4;

        /** The m duration group. */
        private final List<String> mDurationGroup = new ArrayList<String>();

        /**
         * Instantiates a new load content task.
         * 
         * @param context
         *            the context
         */
        LoadContentTask(Context context) {
            mContext = context;
            mDetailsTable = (ViewGroup) getActivity().findViewById(
                    R.id.moduleDetailsTable);

            mDetailsOptionalTable = (ViewGroup) getActivity().findViewById(
                    R.id.moduleDetailsOptionalTable);

            // as the last child is the SAVE button, count - 1 has to be done.
            staticRowsCount = mDetailsOptionalTable.getChildCount() - 1;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            final CustomActionbar tv = (CustomActionbar) mParent.getChildAt(0);
            if (MODE == Util.EDIT_ORPHAN_MODE
                    || MODE == Util.EDIT_RELATIONSHIP_MODE) {
                tv.setTitle(String.format(
                        getString(R.string.editDetailsHeader), mModuleName));
            } else if (MODE == Util.NEW_ORPHAN_MODE
                    || MODE == Util.NEW_RELATIONSHIP_MODE) {
                tv.setTitle(String.format(getString(R.string.newDetailsHeader),
                        mModuleName));
            }

            // Initializing DurationGroup the list
            mDurationGroup.add(ModuleFields.DURATION_HOURS);
            mDurationGroup.add(ModuleFields.DURATION_MINUTES);

            mAccountCursor = getActivity().getContentResolver().query(
                    ContentUtils.getModuleUri(Util.ACCOUNTS),
                    Accounts.LIST_PROJECTION, null, null, null);
            mAccountAdapter = new AccountsSuggestAdapter(getActivity()
                    .getBaseContext(), mAccountCursor);

            mUserCursor = getActivity().getContentResolver().query(
                    ContentUtils.getModuleUri(Util.USERS),
                    Users.DETAILS_PROJECTION, null, null, null);
            mUserAdapter = new UsersSuggestAdapter(getActivity()
                    .getBaseContext(), mUserCursor);

            mProgressDialog = ViewUtil.getProgressDialog(getActivity(),
                    getString(R.string.loading), false);
            mProgressDialog.show();
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            switch ((Integer) values[0]) {

            case STATIC_ROW:
                final String fieldName = (String) values[1];
                View editRow = (View) values[2];
                editRow.setVisibility(View.VISIBLE);

                TextView labelView = (TextView) values[3];
                labelView.setText((String) values[4]);
                final AutoCompleteTextView valueView = (AutoCompleteTextView) values[5];
                valueView.setTag(fieldName);
                String editTextValue = (String) values[6];
                valueView.setText(editTextValue);

                if (fieldName
                        .equalsIgnoreCase(ImportContactsUtility
                                .getModuleFieldNameForContactsField(ModuleFields.EMAIL1))) {
                    valueView
                            .setValidator(new AutoCompleteTextView.Validator() {
                                @Override
                                public CharSequence fixText(
                                        CharSequence invalidText) {
                                    return invalidText;
                                }

                                @Override
                                public boolean isValid(CharSequence text) {
                                    final String email = text.toString();
                                    if (!ModuleFieldValidator.isNotEmpty(email)
                                            || !ModuleFieldValidator
                                                    .isEmailValid(email)) {
                                        hasError = true;
                                        valueView
                                                .setError(getString(R.string.emailValidationErrorMsg));
                                    }
                                    return false;
                                }
                            });
                }

                if (fieldName
                        .equalsIgnoreCase(ImportContactsUtility
                                .getModuleFieldNameForContactsField(ModuleFields.PHONE_MOBILE))
                        || fieldName
                                .equalsIgnoreCase(ImportContactsUtility
                                        .getModuleFieldNameForContactsField(ModuleFields.PHONE_WORK))) {
                    valueView.setInputType(InputType.TYPE_CLASS_NUMBER);
                    valueView
                            .setValidator(new AutoCompleteTextView.Validator() {
                                @Override
                                public CharSequence fixText(
                                        CharSequence invalidText) {
                                    return invalidText;
                                }

                                @Override
                                public boolean isValid(CharSequence text) {
                                    final String phoneNumber = text.toString();
                                    if (ModuleFieldValidator
                                            .isNotEmpty(phoneNumber)
                                            && !ModuleFieldValidator
                                                    .isPhoneNumberValid(phoneNumber)) {
                                        hasError = true;
                                        valueView
                                                .setError(getString(R.string.phNoValidationErrorMsg));
                                    }
                                    return false;
                                }
                            });
                }

                if (fieldName
                        .equalsIgnoreCase(ImportContactsUtility
                                .getModuleFieldNameForContactsField(ModuleFields.FIRST_NAME))
                        || fieldName
                                .equalsIgnoreCase(ImportContactsUtility
                                        .getModuleFieldNameForContactsField(ModuleFields.LAST_NAME))) {
                    valueView
                            .setValidator(new AutoCompleteTextView.Validator() {
                                @Override
                                public CharSequence fixText(
                                        CharSequence invalidText) {
                                    return invalidText;
                                }

                                @Override
                                public boolean isValid(CharSequence text) {
                                    if (!ModuleFieldValidator.isNotEmpty(text
                                            .toString())) {
                                        hasError = true;
                                        valueView.setError(String
                                                .format(getString(R.string.emptyValidationErrorMsg),
                                                        fieldName));
                                    }
                                    return false;
                                }
                            });
                }

                // set the adapter to auto-suggest
                if (!Util.ACCOUNTS.equals(mModuleName)
                        && fieldName.equals(ModuleFields.ACCOUNT_NAME)) {
                    // only if the module is directly related to Accounts,
                    // disable the account name
                    // field populating it with the corresponding account name
                    if (MODE == Util.NEW_RELATIONSHIP_MODE) {

                        // get the module name from the URI
                        final String module = mIntentUri.getPathSegments().get(
                                0);

                        // only if the module is directly related with the
                        // Accounts modulemenu in
                        if (Util.ACCOUNTS.equals(module)) {

                            if (mDbHelper == null) {
                                mDbHelper = new DatabaseHelper(getActivity()
                                        .getBaseContext());
                            }

                            // get the account name using the account row id in
                            // the URI
                            final int accountRowId = Integer
                                    .parseInt(mIntentUri.getPathSegments().get(
                                            1));
                            final String selection = AccountsColumns.ID + "="
                                    + accountRowId;
                            final Cursor cursor = getActivity()
                                    .getContentResolver()
                                    .query(ContentUtils
                                            .getModuleUri(Util.ACCOUNTS),
                                            Accounts.LIST_PROJECTION,
                                            selection, null, null);
                            cursor.moveToFirst();
                            final String accountName = cursor.getString(2);
                            cursor.close();

                            // pre-populate the field with the account name and
                            // disable it
                            valueView.setText(accountName);
                            valueView.setEnabled(false);
                        } else {
                            // if the module is not directly related to Accounts
                            // module, show the
                            // auto-suggest instead of pre-populating the
                            // account name field
                            valueView.setAdapter(mAccountAdapter);
                            valueView
                                    .setOnItemClickListener(new AccountsClickedItemListener());
                        }
                    } else {
                        // set the adapter to show the auto-suggest
                        valueView.setAdapter(mAccountAdapter);
                        valueView
                                .setOnItemClickListener(new AccountsClickedItemListener());

                        if (MODE == Util.EDIT_ORPHAN_MODE
                                || MODE == Util.EDIT_RELATIONSHIP_MODE)
                            // store the account name in mAccountName if the
                            // bean is already related
                            // to an account
                            if (!TextUtils.isEmpty(editTextValue)) {
                                mAccountName = editTextValue;
                            }
                    }
                } else if (fieldName.equals(ModuleFields.ASSIGNED_USER_NAME)) {
                    // set the adapter to show the auto-suggest
                    valueView.setAdapter(mUserAdapter);
                    valueView
                            .setOnItemClickListener(new UsersClickedItemListener());

                    if (MODE == Util.EDIT_ORPHAN_MODE
                            || MODE == Util.EDIT_RELATIONSHIP_MODE) {
                        // store the user name in mUserName if the bean is
                        // already assigned to a
                        // user
                        if (!TextUtils.isEmpty(editTextValue)) {
                            mUserName = editTextValue;
                        }
                    }
                }
                break;

            case DYNAMIC_ROW:
                final String dynamicFieldName = (String) values[1];

                editRow = (View) values[2];
                editRow.setVisibility(View.VISIBLE);

                labelView = (TextView) values[3];
                labelView.setText((String) values[4]);
                final AutoCompleteTextView dynamicValueView = (AutoCompleteTextView) values[5];
                dynamicValueView.setTag(dynamicFieldName);
                editTextValue = (String) values[6];
                dynamicValueView.setText(editTextValue);

                if (dynamicFieldName
                        .equalsIgnoreCase(ImportContactsUtility
                                .getModuleFieldNameForContactsField(ModuleFields.EMAIL1))) {
                    dynamicValueView
                            .setValidator(new AutoCompleteTextView.Validator() {
                                @Override
                                public CharSequence fixText(
                                        CharSequence invalidText) {
                                    return invalidText;
                                }

                                @Override
                                public boolean isValid(CharSequence text) {
                                    final String email = text.toString();
                                    if (!ModuleFieldValidator.isNotEmpty(email)
                                            || !ModuleFieldValidator
                                                    .isEmailValid(email)) {
                                        hasError = true;
                                        dynamicValueView
                                                .setError(getString(R.string.emailValidationErrorMsg));
                                    }
                                    return false;
                                }
                            });
                }

                if (dynamicFieldName
                        .equalsIgnoreCase(ImportContactsUtility
                                .getModuleFieldNameForContactsField(ModuleFields.PHONE_MOBILE))
                        || dynamicFieldName
                                .equalsIgnoreCase(ImportContactsUtility
                                        .getModuleFieldNameForContactsField(ModuleFields.PHONE_WORK))) {
                    dynamicValueView.setInputType(InputType.TYPE_CLASS_NUMBER);
                    dynamicValueView
                            .setValidator(new AutoCompleteTextView.Validator() {
                                @Override
                                public CharSequence fixText(
                                        CharSequence invalidText) {
                                    return invalidText;
                                }

                                @Override
                                public boolean isValid(CharSequence text) {
                                    final String phoneNumber = text.toString();
                                    if (ModuleFieldValidator
                                            .isNotEmpty(phoneNumber)
                                            && !ModuleFieldValidator
                                                    .isPhoneNumberValid(phoneNumber)) {
                                        hasError = true;
                                        dynamicValueView
                                                .setError(getString(R.string.phNoValidationErrorMsg));
                                    }
                                    return false;
                                }
                            });
                }

                if (dynamicFieldName
                        .equalsIgnoreCase(ImportContactsUtility
                                .getModuleFieldNameForContactsField(ModuleFields.FIRST_NAME))
                        || dynamicFieldName
                                .equalsIgnoreCase(ImportContactsUtility
                                        .getModuleFieldNameForContactsField(ModuleFields.LAST_NAME))) {
                    dynamicValueView
                            .setValidator(new AutoCompleteTextView.Validator() {
                                @Override
                                public CharSequence fixText(
                                        CharSequence invalidText) {
                                    return invalidText;
                                }

                                @Override
                                public boolean isValid(CharSequence text) {
                                    if (!ModuleFieldValidator.isNotEmpty(text
                                            .toString())) {
                                        hasError = true;
                                        dynamicValueView.setError(String
                                                .format(getString(R.string.emptyValidationErrorMsg),
                                                        dynamicFieldName));
                                    }
                                    return false;
                                }
                            });
                }

                // set the adapter to auto-suggest
                if (!Util.ACCOUNTS.equals(mModuleName)
                        && dynamicFieldName.equals(ModuleFields.ACCOUNT_NAME)) {
                    /*
                     * only if the module is directly related to Accounts,
                     * disable the account name field populating it with the
                     * corresponding account name
                     */
                    if (MODE == Util.NEW_RELATIONSHIP_MODE) {

                        // get the module name from the URI
                        final String module = mIntentUri.getPathSegments().get(
                                0);

                        // only if the module is directly related with the
                        // Accounts module
                        if (Util.ACCOUNTS.equals(module)) {

                            if (mDbHelper == null) {
                                mDbHelper = new DatabaseHelper(getActivity()
                                        .getBaseContext());
                            }

                            // get the account name using the account row id
                            // in the URI
                            final int accountRowId = Integer
                                    .parseInt(mIntentUri.getPathSegments().get(
                                            1));
                            final String selection = AccountsColumns.ID + "="
                                    + accountRowId;
                            final Cursor cursor = getActivity()
                                    .getContentResolver()
                                    .query(ContentUtils
                                            .getModuleUri(Util.ACCOUNTS),
                                            Accounts.LIST_PROJECTION,
                                            selection, null, null);
                            cursor.moveToFirst();
                            final String accountName = cursor.getString(2);
                            cursor.close();

                            // pre-populate the field with the account name
                            // and disable it
                            dynamicValueView.setText(accountName);
                            dynamicValueView.setEnabled(false);
                        } else {
                            // if the module is not directly related to
                            // Accounts
                            // module, show the
                            // auto-suggest instead of pre-populating the
                            // account name field
                            dynamicValueView.setAdapter(mAccountAdapter);
                            dynamicValueView
                                    .setOnItemClickListener(new AccountsClickedItemListener());
                        }
                    } else {
                        // set the adapter to show the auto-suggest
                        dynamicValueView.setAdapter(mAccountAdapter);
                        dynamicValueView
                                .setOnItemClickListener(new AccountsClickedItemListener());

                        if (MODE == Util.EDIT_ORPHAN_MODE
                                || MODE == Util.EDIT_RELATIONSHIP_MODE)
                            // store the account name in mAccountName if the
                            // bean is already related
                            // to an account
                            if (!TextUtils.isEmpty(editTextValue)) {
                                mAccountName = editTextValue;
                            }
                    }
                } else if (dynamicFieldName
                        .equals(ModuleFields.ASSIGNED_USER_NAME)) {
                    // set the adapter to show the auto-suggest
                    dynamicValueView.setAdapter(mUserAdapter);
                    dynamicValueView
                            .setOnItemClickListener(new UsersClickedItemListener());

                    if (MODE == Util.EDIT_ORPHAN_MODE
                            || MODE == Util.EDIT_RELATIONSHIP_MODE) {
                        // store the user name in mUserName if the bean is
                        // already assigned to a
                        // user
                        if (!TextUtils.isEmpty(editTextValue)) {
                            mUserName = editTextValue;
                        }
                    }
                }

                mDetailsOptionalTable.addView(editRow);
                break;

            case INPUT_TYPE:
                final AutoCompleteTextView inputTypeValueView = (AutoCompleteTextView) values[1];
                inputTypeValueView.setInputType((Integer) values[2]);
                break;

            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (MODE == Util.EDIT_ORPHAN_MODE
                        || MODE == Util.EDIT_RELATIONSHIP_MODE) {
                    mCursor = getActivity().getContentResolver().query(
                            Uri.withAppendedPath(
                                    ContentUtils.getModuleUri(mModuleName),
                                    mRowId), mSelectFields, null, null,
                            ContentUtils.getModuleSortOrder(mModuleName));
                }
            } catch (final Exception e) {
                Log.e(TAG, e.getMessage(), e);
                return Util.FETCH_FAILED;
            }

            return Util.FETCH_SUCCESS;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#onCancelled()
         */
        @Override
        protected void onCancelled() {
            super.onCancelled();

        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            setUpActionBar();
            setContents();

            // close the cursor irrespective of the result
            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }

            if (isCancelled())
                return;
            final int retVal = (Integer) result;
            switch (retVal) {
            case Util.FETCH_FAILED:
                break;
            case Util.FETCH_SUCCESS:
                // set visibility for the SAVE button
                final LinearLayout parent = (LinearLayout) getActivity()
                        .findViewById(R.id.editDone);
                parent.setVisibility(View.VISIBLE);
                break;
            default:
            }

            mProgressDialog.cancel();
        }

        /**
         * Sets the contents.
         */
        private void setContents() {

            final String[] detailsProjection = mSelectFields;

            if (mDbHelper == null) {
                mDbHelper = new DatabaseHelper(getActivity().getBaseContext());
            }

            if (MODE == Util.EDIT_ORPHAN_MODE
                    || MODE == Util.EDIT_RELATIONSHIP_MODE) {
                if (!isCancelled()) {
                    mCursor.moveToFirst();
                    mSugarBeanId = mCursor.getString(1); // beanId has

                }
            }

            final Map<String, ModuleField> fieldNameVsModuleField = ContentUtils
                    .getModuleFields(mContext, mModuleName);

            final Map<String, String> fieldsExcludedForEdit = mDbHelper
                    .getFieldsExcludedForEdit();

            int rowsCount = 1; // to keep track of number of rows being used

            TextView textViewForLabel;
            AutoCompleteTextView editTextForValue;

            for (int i = 0; i < detailsProjection.length; i++) {
                // if the task gets cancelled
                if (isCancelled()) {
                    break;
                }
                final String fieldName = detailsProjection[i];

                // if the field name is excluded in details screen, skip it
                if (fieldsExcludedForEdit.containsKey(fieldName)) {
                    continue;
                }

                // get the attributes of the moduleField
                final ModuleField moduleField = fieldNameVsModuleField
                        .get(fieldName);
                ViewGroup tableRow;

                if (moduleField != null) {

                    if (moduleField.isRequired()) {
                        tableRow = (ViewGroup) mDetailsTable
                                .getChildAt(rowsCount);

                        textViewForLabel = (TextView) tableRow.getChildAt(0);
                        editTextForValue = (AutoCompleteTextView) tableRow
                                .getChildAt(1);

                    } else {
                        if (staticRowsCount > rowsCount) {
                            tableRow = (ViewGroup) mDetailsOptionalTable
                                    .getChildAt(rowsCount);

                            textViewForLabel = (TextView) tableRow
                                    .getChildAt(0);
                            editTextForValue = (AutoCompleteTextView) tableRow
                                    .getChildAt(1);

                        } else {
                            final LayoutInflater inflater = (LayoutInflater) getActivity()
                                    .getBaseContext().getSystemService(
                                            Context.LAYOUT_INFLATER_SERVICE);

                            tableRow = (ViewGroup) inflater.inflate(
                                    R.layout.edit_table_row, null);

                            textViewForLabel = (TextView) tableRow
                                    .getChildAt(0);

                            editTextForValue = (AutoCompleteTextView) tableRow
                                    .getChildAt(1);
                        }

                    }

                    String label = moduleField.getLabel();

                    if (label.contains("Email") || label.contains("Website")) {
                        editTextForValue.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    }

                    if (label.contains("Phone") || label.contains("Fax")
                            || label.contains("Number")
                            || label.contains("Amount")) {
                        editTextForValue
                                .setRawInputType(Configuration.KEYBOARD_12KEY);
                    }
                    if (label.contains("Start Date:")) {

                        dateView = editTextForValue;
                        editTextForValue.setInputType(InputType.TYPE_NULL);
                        editTextForValue
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Calendar calendar = new GregorianCalendar();

                                        final int year = calendar
                                                .get(Calendar.YEAR);
                                        final int month = calendar
                                                .get(Calendar.MONTH);
                                        final int date = calendar
                                                .get(Calendar.DATE);

                                        new DatePickerDialog(getActivity(),
                                                new MyOnDateChangedListener(),
                                                year, month, date).show();

                                    }
                                });

                    }

                    if (label.contains("Duration Hours")) {
                        timeView = editTextForValue;
                        editTextForValue.setInputType(InputType.TYPE_NULL);
                        editTextForValue
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Calendar c = Calendar
                                                .getInstance();
                                        final int hour = c
                                                .get(Calendar.HOUR_OF_DAY);
                                        final int minute = c
                                                .get(Calendar.MINUTE);
                                        final TimePickerDialog timePicker = new TimePickerDialog(
                                                getActivity(),
                                                new OnMyTimeSetListener(),
                                                hour, minute, true);
                                        timePicker.setTitle(getResources()
                                                .getString(R.string.duration));
                                        timePicker.show();
                                    }
                                });
                    }

                    if (mDurationGroup.contains(fieldName)) {
                        if (fieldName.equals(ModuleFields.DURATION_MINUTES)) {

                            continue;
                        } else if (fieldName
                                .equals(ModuleFields.DURATION_HOURS)) {
                            label = getActivity().getBaseContext().getString(
                                    R.string.duration);
                        }
                    }

                    int command = STATIC_ROW;
                    if (staticRowsCount < rowsCount) {
                        command = DYNAMIC_ROW;
                    }

                    if (MODE == Util.EDIT_ORPHAN_MODE
                            || MODE == Util.EDIT_RELATIONSHIP_MODE) {
                        final String value = mCursor.getString(mCursor
                                .getColumnIndex(fieldName));
                        if (!TextUtils.isEmpty(value)) {
                            publishProgress(command, fieldName, tableRow,
                                    textViewForLabel, label, editTextForValue,
                                    value);
                        } else {
                            publishProgress(command, fieldName, tableRow,
                                    textViewForLabel, label, editTextForValue,
                                    "");
                        }
                        setInputType(editTextForValue, moduleField);

                    } else {
                        publishProgress(command, fieldName, tableRow,
                                textViewForLabel, label, editTextForValue, "");
                    }
                    rowsCount++;
                }
            }

        }

        /**
         * The listener interface for receiving myOnDateChanged events. The
         * class that is interested in processing a myOnDateChanged event
         * implements this interface, and the object created with that class is
         * registered with a component using the component's
         * <code>addMyOnDateChangedListener<code> method. When
         * the myOnDateChanged event occurs, that object's appropriate
         * method is invoked.
         * 
         * @see MyOnDateChangedEvent
         */
        private class MyOnDateChangedListener implements OnDateSetListener {

            /*
             * (non-Javadoc)
             * 
             * @see
             * android.app.DatePickerDialog.OnDateSetListener#onDateSet(android
             * .widget.DatePicker, int, int, int)
             */
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {

                final String Date = year + "-" + (monthOfYear + 1) + "-"
                        + dayOfMonth;
                dateView.setText(Date);

            }
        };

        /**
         * The listener interface for receiving onMyTimeSet events. The class
         * that is interested in processing a onMyTimeSet event implements this
         * interface, and the object created with that class is registered with
         * a component using the component's
         * <code>addOnMyTimeSetListener<code> method. When
         * the onMyTimeSet event occurs, that object's appropriate
         * method is invoked.
         * 
         * @see OnMyTimeSetEvent
         */
        private class OnMyTimeSetListener implements OnTimeSetListener {

            /*
             * (non-Javadoc)
             * 
             * @see
             * android.app.TimePickerDialog.OnTimeSetListener#onTimeSet(android
             * .widget.TimePicker, int, int)
             */
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                final String Time = hourOfDay + ":" + minute + ":" + "00";
                timeView.setText(Time);

            }
        };

        /*
         * takes care of basic validation automatically for some fields
         */
        /**
         * Sets the input type.
         * 
         * @param editTextForValue
         *            the edit text for value
         * @param moduleField
         *            the module field
         */
        private void setInputType(TextView editTextForValue,
                ModuleField moduleField) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "ModuleField type:" + moduleField.getType());
            }
            // TODO: there has to be a better way to get the constant
            if (moduleField.getType().equals("phone")) {
                publishProgress(INPUT_TYPE, editTextForValue,
                        InputType.TYPE_CLASS_PHONE);
            }
        }
    }

    /**
     * on click listener for saving a module item.
     * 
     * @param v
     *            a {@link android.view.View} object.
     */
    public void saveModuleItem(View v) {

        mProgressDialog = ViewUtil.getProgressDialog(getActivity(),
                getString(R.string.saving), false);
        mProgressDialog.show();

        final String[] detailsProjection = mSelectFields;

        final Map<String, String> modifiedValues = new LinkedHashMap<String, String>();
        if (MODE == Util.EDIT_ORPHAN_MODE
                || MODE == Util.EDIT_RELATIONSHIP_MODE) {
            modifiedValues.put(RestConstants.ID, mSugarBeanId);
        }

        final Uri uri = mIntentUri;
        final Map<String, ModuleField> fieldNameVsModuleField = ContentUtils
                .getModuleFields(getActivity(), mModuleName);

        final Map<String, String> fieldsExcludedForEdit = mDbHelper
                .getFieldsExcludedForEdit();
        int rowsCount = 1;
        for (int i = 0; i < detailsProjection.length; i++) {

            final String fieldName = detailsProjection[i];

            // if the field name is excluded in details screen, skip it
            if (fieldsExcludedForEdit.containsKey(fieldName)) {
                continue;
            }

            final ModuleField moduleField = fieldNameVsModuleField
                    .get(fieldName);
            if (moduleField != null) {
                final AutoCompleteTextView editText;
                ViewGroup tableRow;
                if (moduleField.isRequired()) {

                    tableRow = (ViewGroup) mDetailsTable.getChildAt(rowsCount);
                    editText = (AutoCompleteTextView) tableRow.getChildAt(1);
                    if (editText.getText().toString().isEmpty()) {
                        hasError = true;
                        editText.setError("Please fill the mandatory field "
                                + moduleField.getName());
                    }

                } else {
                    tableRow = (ViewGroup) mDetailsOptionalTable
                            .getChildAt(rowsCount);

                    editText = (AutoCompleteTextView) tableRow.getChildAt(1);
                }

                String fieldValue = editText.getText().toString();

                if (!Util.ACCOUNTS.equals(mModuleName)
                        && fieldName.equals(ModuleFields.ACCOUNT_NAME)) {

                    if (!TextUtils.isEmpty(fieldValue)) {

                        if (!TextUtils.isEmpty(mSelectedAccountName)) {
                            // if the user has selected an account from the
                            // auto-suggest list

                            // check if the field value is the selected value
                            if (!mSelectedAccountName.equals(fieldValue)) {
                                // account name is incorrect.
                                hasError = true;
                                editText.setError(getString(R.string.accountNameErrorMsg));
                            }

                        } else if (!TextUtils.isEmpty(mAccountName)) {
                            /*
                             * if the user doesn't change the account name and
                             * it remains the same
                             */

                            if (!fieldValue.equals(mAccountName)) {
                                /*
                                 * if the user just enters some value without
                                 * selecting from the auto-suggest
                                 */
                                hasError = true;
                                editText.setError(getString(R.string.accountNameErrorMsg));
                            }

                        } else {
                            /*
                             * if the editText has been disabled, do not show
                             * the error
                             */
                            if (editText.isEnabled()) {
                                // if the user just enters some value without
                                // selecting((ViewGroup)
                                // mDetailsTable.getChildAt(rowsCount)).getChildAt(1)
                                // from the auto-suggest
                                hasError = true;
                                editText.setError(getString(R.string.accountNameErrorMsg));
                            }
                        }

                    } else {
                        fieldValue = null;
                    }

                } else if (fieldName.equals(ModuleFields.ASSIGNED_USER_NAME)) {

                    if (!TextUtils.isEmpty(fieldValue)) {

                        if (!TextUtils.isEmpty(mSelectedUserName)) {
                            // if the user has selected a user name from the
                            // auto-suggest list

                            // check if the field value is the selected value
                            if (!mSelectedUserName.equals(fieldValue)) {
                                // user name is incorrect.
                                hasError = true;
                                editText.setError(getString(R.string.userNameErrorMsg));
                            }
                        } else if (!TextUtils.isEmpty(mUserName)) {
                            // if the user doesn't change the user name and it
                            // remains the same

                            if (!mUserName.equals(fieldValue)) {
                                hasError = true;
                                editText.setError(getString(R.string.userNameErrorMsg));
                            }
                        } else {
                            // if the editText has been disabled, do not show
                            // the error
                            if (editText.isEnabled()) {

                                hasError = true;
                                editText.setError(getString(R.string.userNameErrorMsg));
                            }
                        }
                    }
                }
                if (fieldName.contains(ModuleFields.DURATION_MINUTES)) {
                    mDescription = editText.getText().toString();
                    modifiedValues.put(fieldName, "");
                } else if (fieldName.contains(ModuleFields.DESCRIPTION)) {
                    modifiedValues.put(fieldName, mDescription);
                } else {
                    modifiedValues
                            .put(fieldName, editText.getText().toString());
                }
                rowsCount++;
            }
        }

        if (!hasError) {
            if (MODE == Util.EDIT_ORPHAN_MODE) {
                ServiceHelper.startServiceForUpdate(getActivity()
                        .getBaseContext(), uri, mModuleName, mSugarBeanId,
                        modifiedValues);
            } else if (MODE == Util.EDIT_RELATIONSHIP_MODE) {
                ServiceHelper.startServiceForUpdate(getActivity()
                        .getBaseContext(), uri, mModuleName, mSugarBeanId,
                        modifiedValues);
            } else if (MODE == Util.NEW_RELATIONSHIP_MODE) {
                modifiedValues.put(ModuleFields.DELETED, Util.NEW_ITEM);
                ServiceHelper.startServiceForInsert(getActivity()
                        .getBaseContext(), uri, mModuleName, modifiedValues);
            } else if (MODE == Util.NEW_ORPHAN_MODE) {
                modifiedValues.put(ModuleFields.DELETED, Util.NEW_ITEM);
                ServiceHelper
                        .startServiceForInsert(getActivity().getBaseContext(),
                                ContentUtils.getModuleUri(mModuleName),
                                mModuleName, modifiedValues);
            }

        } else {
            ViewUtil.makeToast(getActivity().getBaseContext(),
                    R.string.validationErrorMsg);
            hasError = false;
            mProgressDialog.cancel();

        }
        ViewUtil.dismissVirtualKeyboard(getActivity().getBaseContext(), v);

    }

    /**
     * importContact for invoking a subactivity for picking up contact from
     * device contacts.
     */
    public void importContact() {
        final Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, Util.IMPORT_CONTACTS_REQUEST_CODE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
        case (Util.IMPORT_CONTACTS_REQUEST_CODE):
            if (resultCode == Activity.RESULT_OK) {
                getContactInfo(data);
            }
            break;
        }
    }

    /**
     * Gets the contact info.
     * 
     * @param intent
     *            the intent
     * @return the contact info
     */
    protected void getContactInfo(Intent intent) {

        final Cursor cursor = getActivity().managedQuery(intent.getData(),
                null, null, null, null);
        while (cursor.moveToNext()) {
            final String contactId = cursor.getString(cursor
                    .getColumnIndex(BaseColumns._ID));

            final Cursor nameCursor = getActivity()
                    .getApplicationContext()
                    .getContentResolver()
                    .query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.MIMETYPE
                                    + " = ? AND "
                                    + ContactsContract.RawContactsEntity.CONTACT_ID
                                    + " = ? ",
                            new String[] {
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                                    contactId }, null);

            while (nameCursor.moveToNext()) {
                final String givenName = nameCursor
                        .getString(nameCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                final String familyName = nameCursor
                        .getString(nameCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                ((AutoCompleteTextView) mDetailsTable
                        .findViewWithTag(ImportContactsUtility
                                .getModuleFieldNameForContactsField(ModuleFields.FIRST_NAME)))
                        .setText(givenName);
                ((AutoCompleteTextView) mDetailsTable
                        .findViewWithTag(ImportContactsUtility
                                .getModuleFieldNameForContactsField(ModuleFields.LAST_NAME)))
                        .setText(familyName);
            }
            nameCursor.close();

            String hasPhone = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (hasPhone.equalsIgnoreCase("1")) {
                hasPhone = "true";
            } else {
                hasPhone = "false";
            }

            if (Boolean.parseBoolean(hasPhone)) {
                final Cursor phones = getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = " + contactId, null, null);
                while (phones.moveToNext()) {
                    final String contactPhno = phones
                            .getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    final int type = phones
                            .getInt(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    if (ContactsContract.CommonDataKinds.Phone.TYPE_WORK == type) {
                        ((AutoCompleteTextView) mDetailsTable
                                .findViewWithTag(ImportContactsUtility
                                        .getModuleFieldNameForContactsField(ModuleFields.PHONE_WORK)))
                                .setText(contactPhno);
                    }
                    if (ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE == type) {
                        ((AutoCompleteTextView) mDetailsTable
                                .findViewWithTag(ImportContactsUtility
                                        .getModuleFieldNameForContactsField(ModuleFields.PHONE_MOBILE)))
                                .setText(contactPhno);
                    }
                }
                phones.close();
            }

            final Cursor emails = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = "
                            + contactId, null, null);
            while (emails.moveToNext()) {
                final String contactEmail = emails
                        .getString(emails
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                ((AutoCompleteTextView) mDetailsTable
                        .findViewWithTag(ImportContactsUtility
                                .getModuleFieldNameForContactsField(ModuleFields.EMAIL1)))
                        .setText(contactEmail);
            }
            emails.close();
        }
        cursor.close();

    }// getContactInfo

    /** {@inheritDoc} */
    // @Override
    // TODO
    /*
     * public boolean onCreateOptionsMenu(Menu menu) { // Hold on to this //
     * Inflate the currently selected menu XML resource. MenuItem item; item =
     * menu.add(1, R.id.save, 1, R.string.save);
     * item.setIcon(android.R.drawable.ic_menu_save);
     * item.setAlphabeticShortcut('s'); return true; }
     */
    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        /*
         * case R.id.save: saveModuleItem(getActivity().getCurrentFocus());
         * return true;
         */
        default:
            return true;
        }
        // return false;
    }

    /*
     * Status Handler, Handler updates the screen based on messages sent by the
     * SugarService or any tasks
     */
    /**
     * The Class StatusHandler.
     */
    private class StatusHandler extends Handler {

        /**
         * Instantiates a new status handler.
         */
        StatusHandler() {
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
            case R.id.status:
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Display Status");
                }
                mProgressDialog.cancel();
                ViewUtil.makeToast(getActivity().getBaseContext(),
                        (String) message.obj);

                bInsertSuccessful = true;
                getActivity().finish();
                break;
            }
        }
    }

    /**
     * The Class AutoSuggestAdapter.
     */
    public static class AutoSuggestAdapter extends CursorAdapter implements
            Filterable {

        /** The m content. */
        protected ContentResolver mContent;

        /** The m db helper. */
        protected DatabaseHelper mDbHelper;

        /**
         * Instantiates a new auto suggest adapter.
         * 
         * @param context
         *            the context
         * @param c
         *            the c
         */
        public AutoSuggestAdapter(Context context, Cursor c) {
            super(context, c);
            mContent = context.getContentResolver();
            mDbHelper = new DatabaseHelper(context);
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.CursorAdapter#newView(android.content.Context,
         * android.database.Cursor, android.view.ViewGroup)
         */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final LinearLayout view = (LinearLayout) inflater.inflate(
                    R.layout.autosuggest_list_item, parent, false);
            ((TextView) view.getChildAt(0)).setText(cursor.getString(2));
            return view;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.CursorAdapter#bindView(android.view.View,
         * android.content.Context, android.database.Cursor)
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "bindView : " + cursor.getString(2));
            }
            ((TextView) ((LinearLayout) view).getChildAt(0)).setText(cursor
                    .getString(2));
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.widget.CursorAdapter#convertToString(android.database.Cursor)
         */
        @Override
        public String convertToString(Cursor cursor) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "convertToString : " + cursor.getString(2));
            }
            return cursor.getString(2);
        }

    }

    /**
     * The Class AccountsSuggestAdapter.
     */
    public static class AccountsSuggestAdapter extends AutoSuggestAdapter {

        /**
         * Instantiates a new accounts suggest adapter.
         * 
         * @param context
         *            the context
         * @param c
         *            the c
         */
        public AccountsSuggestAdapter(Context context, Cursor c) {
            super(context, c);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.widget.CursorAdapter#runQueryOnBackgroundThread(java.lang
         * .CharSequence)
         */
        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            if (getFilterQueryProvider() != null)
                return getFilterQueryProvider().runQuery(constraint);

            StringBuilder buffer = null;
            String[] args = null;
            if (constraint != null) {
                buffer = new StringBuilder();
                buffer.append("UPPER(");
                buffer.append(AccountsColumns.NAME);
                buffer.append(") GLOB ?");
                args = new String[] { constraint.toString().toUpperCase() + "*" };
            }

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG,
                        "constraint "
                                + (constraint != null ? constraint.toString()
                                        : ""));
            }

            return mContent.query(ContentUtils.getModuleUri(Util.ACCOUNTS),
                    Accounts.LIST_PROJECTION,
                    buffer == null ? null : buffer.toString(), args,
                    Accounts.DEFAULT_SORT_ORDER);
        }
    }

    /**
     * The Class UsersSuggestAdapter.
     */
    public static class UsersSuggestAdapter extends AutoSuggestAdapter {

        /**
         * Instantiates a new users suggest adapter.
         * 
         * @param context
         *            the context
         * @param c
         *            the c
         */
        public UsersSuggestAdapter(Context context, Cursor c) {
            super(context, c);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.widget.CursorAdapter#runQueryOnBackgroundThread(java.lang
         * .CharSequence)
         */
        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            if (getFilterQueryProvider() != null)
                return getFilterQueryProvider().runQuery(constraint);

            StringBuilder buffer = null;
            String[] args = null;
            if (constraint != null) {
                buffer = new StringBuilder();
                buffer.append("UPPER(");
                buffer.append(UserColumns.USER_NAME);
                buffer.append(") GLOB ?");
                args = new String[] { constraint.toString().toUpperCase() + "*" };
            }

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG,
                        "constraint "
                                + (constraint != null ? constraint.toString()
                                        : ""));
            }

            return mContent.query(ContentUtils.getModuleUri(Util.USERS),
                    Users.DETAILS_PROJECTION,
                    buffer == null ? null : buffer.toString(), args, null);
        }
    }

    /**
     * The listener interface for receiving accountsClickedItem events. The
     * class that is interested in processing a accountsClickedItem event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addAccountsClickedItemListener<code> method. When
     * the accountsClickedItem event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see AccountsClickedItemEvent
     */
    public class AccountsClickedItemListener implements
            AdapterView.OnItemClickListener {

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.widget.AdapterView.OnItemClickListener#onItemClick(android
         * .widget.AdapterView, android.view.View, int, long)
         */
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,
                int position, long l) {
            try {
                // Remembers the selected account name
                final Cursor cursor = (Cursor) adapterView
                        .getItemAtPosition(position);
                mSelectedAccountName = cursor.getString(2);
            } catch (final Exception e) {
                Log.e(TAG, "cannot get the clicked index " + position);
            }

        }
    }

    /**
     * The listener interface for receiving usersClickedItem events. The class
     * that is interested in processing a usersClickedItem event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's
     * <code>addUsersClickedItemListener<code> method. When
     * the usersClickedItem event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see UsersClickedItemEvent
     */
    public class UsersClickedItemListener implements
            AdapterView.OnItemClickListener {

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.widget.AdapterView.OnItemClickListener#onItemClick(android
         * .widget.AdapterView, android.view.View, int, long)
         */
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,
                int position, long l) {
            try {
                // Remembers the selected username
                final Cursor cursor = (Cursor) adapterView
                        .getItemAtPosition(position);
                mSelectedUserName = cursor.getString(2);
            } catch (final Exception e) {
                Log.e(TAG, "cannot get the clicked index " + position);
            }

        }
    }

    /**
     * The Class HomeYesNoAlertDialogFragment.
     */
    public class HomeYesNoAlertDialogFragment extends DialogFragment {

        /**
         * New instance.
         * 
         * @param title
         *            the title
         * @return the home yes no alert dialog fragment
         */
        public HomeYesNoAlertDialogFragment newInstance(int title) {
            final HomeYesNoAlertDialogFragment frag = new HomeYesNoAlertDialogFragment();
            final Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle
         * )
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.discard)
                    .setMessage(R.string.discardAlert)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {

                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                }
                            }).create();
        }
    }

}
