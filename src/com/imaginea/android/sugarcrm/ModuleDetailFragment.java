/*******************************************************************************
 * Copyright (c) 27/08/2013 : 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Asha - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : ModuleDetailActivity 
 * Description : ModuleDetailFragment is used to show detail description for all modules.
 ******************************************************************************/
package com.imaginea.android.sugarcrm;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.ui.BaseActivity;
import com.imaginea.android.sugarcrm.ui.BaseMultiPaneActivity;
import com.imaginea.android.sugarcrm.ui.ModuleDetailsMultiPaneActivity;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.ServiceHelper;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

/**
 * The Class ModuleDetailFragment.
 */
public class ModuleDetailFragment extends Fragment {

    /** The m Record id. */
    private String mRowId;

    /** The m sugar bean id. */
    private String mSugarBeanId;

    /** The m module name. */
    private String mModuleName;

    /** The m cursor. */
    private Cursor mCursor;

    /** The adapter. */
    private SeparatedListAdapter mAdapter = null;

    /** The m select fields. */
    private String[] mSelectFields;

    /** The m relationship modules. */
    private String[] mRelationshipModules;

    /** The m uri. */
    private Uri mUri;

    /** The m db helper. */
    private DatabaseHelper mDbHelper;

    /** The m task. */
    private LoadContentTask mTask;

    /** The m progress dialog. */
    private ProgressDialog mProgressDialog;

    /** The m parent. */
    private RelativeLayout mParent;

    /** The bundle. */
    private Bundle mBundle;

    /** The layout view. */
    private LinearLayout mlayoutView;

    /** The inflater. */
    private LayoutInflater mInflater;

    /** The Constant LOG_TAG. */
    private static final String LOG_TAG = ModuleDetailFragment.class
            .getSimpleName();

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        mParent = (RelativeLayout) inflater.inflate(R.layout.account_details,
                container, false);
        mBundle = getArguments();
        return mParent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (getFragmentManager().findFragmentById(R.id.list_frag) != null) {
            intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
        }
        final Bundle extras = intent.getExtras();

        if (extras != null) {
            mRowId = intent.getStringExtra(Util.ROW_ID);
            mSugarBeanId = intent.getStringExtra(RestConstants.BEAN_ID);
            intent.getBooleanExtra("Recent", false);
            intent.getBooleanExtra("Relation", false);
            mModuleName = extras.getString(RestConstants.MODULE_NAME);

        } else {
            mRowId = mBundle.getString(Util.ROW_ID);
            mSugarBeanId = mBundle.getString(RestConstants.BEAN_ID);
            mModuleName = mBundle.getString(RestConstants.MODULE_NAME);

        }

        mDbHelper = new DatabaseHelper(getActivity().getBaseContext());
        if (intent.getData() == null) {
            if (mRowId == null) {
                mRowId = "1";
            }
            mUri = Uri.withAppendedPath(ContentUtils.getModuleUri(mModuleName),
                    mRowId);
        } else {
            mUri = intent.getData();
        }
        mSelectFields = ContentUtils.getModuleProjections(mModuleName);

        mRelationshipModules = ContentUtils
                .getModuleRelationshipItems(mModuleName);

        mInflater = (LayoutInflater) getActivity().getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /* inflate the Detail Module Fragment layout */
        mlayoutView = (LinearLayout) mInflater
                .inflate(R.layout.table_row, null);

        /* Open the Edit Screen when clicked on edit image */
        startEditDetailModule();

        /* Delete the open record from server and database */
        deleteOpenRecord();

        /*
         * As per the Requirement Action Bar need to be shown on Detail Screen
         * in Phone Version only
         */
        if (!ViewUtil.isHoneycombTablet(getActivity())) {
            setUpActionBar();
        }

        /* Starting a New Async task to populate the Data from the Database */
        mTask = new LoadContentTask(getActivity());
        mTask.execute(null, null, null);

    }

    /**
     * Description : Delete the open record from server and database.
     * 
     */
    private void deleteOpenRecord() {
        /* To Delete the selected Module Details */
        final ImageView deleteImageView = (ImageView) mlayoutView
                .findViewById(R.id.deleteimage);
        final int deleteResourcesId[];
        if (ViewUtil.isHoneycombTablet(getActivity())) {
            final int tabletDeleteResourcesId[] = {
                    R.drawable.ico_trash_pressed, R.drawable.ico_trash_pressed,
                    R.drawable.delete };
            deleteResourcesId = tabletDeleteResourcesId;
        } else {
            final int phoneDeleteResourcesId[] = {
                    R.drawable.icon_m_trash_pressed,
                    R.drawable.icon_m_trash_pressed, R.drawable.icon_m_trash };
            deleteResourcesId = phoneDeleteResourcesId;
        }
        deleteImageView.setImageDrawable(Util.getPressedImage(getActivity()
                .getBaseContext(), deleteResourcesId));
        deleteImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogFragment newFragment = new MyYesNoAlertDialogFragment()
                        .newInstance(R.string.delete);
                newFragment.show(getFragmentManager(), "YesNoAlertDialog");
            }
        });
    }

    /**
     * Description : Open the Record to Edit the Details.
     * 
     */
    private void startEditDetailModule() {
        /* To edit the module details fileds */
        final ImageView editImageView = (ImageView) mlayoutView
                .findViewById(R.id.editimage);
        final int editResourcesId[];
        if (ViewUtil.isHoneycombTablet(getActivity())) {
            final int tabletEditResourcesId[] = { R.drawable.ico_edit_pressed,
                    R.drawable.ico_edit_pressed, R.drawable.edit };
            editResourcesId = tabletEditResourcesId;
        } else {
            final int phoneEditResourcesId[] = { R.drawable.ico_m_edit_pressed,
                    R.drawable.ico_m_edit_pressed, R.drawable.ico_m_edit };

            editResourcesId = phoneEditResourcesId;
        }
        editImageView.setImageDrawable(Util.getPressedImage(getActivity()
                .getBaseContext(), editResourcesId));

        editImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent editDetailsIntent = new Intent(getActivity(),
                        EditModuleDetailActivity.class);
                editDetailsIntent.putExtra(Util.ROW_ID, mRowId);
                if (mUri != null) {
                    editDetailsIntent.setData(mUri);
                }

                editDetailsIntent.putExtra(RestConstants.BEAN_ID, mSugarBeanId);
                editDetailsIntent.putExtra(RestConstants.MODULE_NAME,
                        mModuleName);

                startActivity(editDetailsIntent);
            }
        });
    }

    /**
     * Description : Set up the Action Bar on Detail Screen in Phone Version
     * only.
     * 
     */
    private void setUpActionBar() {

        final CustomActionbar actionBar = (CustomActionbar) getActivity()
                .findViewById(R.id.custom_actionbar);

        final LinearLayout actionView = (LinearLayout) actionBar
                .findViewById(R.id.logo);
        actionView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ModuleDetailFragment.this.getActivity().finish();
            }
        });

        final ImageView back = (ImageView) actionBar
                .findViewById(R.id.actionbar_back);
        final ImageView logo = (ImageView) actionBar
                .findViewById(R.id.actionbar_logo);
        logo.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);

        /* Set the Module Name */
        actionBar.setTitle(mModuleName);

        /* Show the Menu when clicked on More Image */
        final ImageView settingsView = (ImageView) actionBar
                .findViewById(R.id.settings);
        final int settinsResourcesId[];

        final int phoneSettinsResourcesId[] = {
                R.drawable.ico_m_actionbar_menu_pressed,
                R.drawable.ico_m_actionbar_menu_pressed,
                R.drawable.ico_m_actionbar_menu_nor };
        settinsResourcesId = phoneSettinsResourcesId;

        settingsView.setImageDrawable(Util.getPressedImage(getActivity()
                .getBaseContext(), settinsResourcesId));
        settingsView.setVisibility(View.VISIBLE);

        final LinearLayout menuLayout = (LinearLayout) getActivity()
                .findViewById(R.id.settings_menu);
        final View transparentView = getActivity().findViewById(
                R.id.transparent_view);

        settingsView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!(menuLayout.getVisibility() == View.VISIBLE)) {
                    menuLayout.setVisibility(View.VISIBLE);
                    menuLayout.bringToFront();
                    transparentView.setVisibility(View.VISIBLE);
                } else {
                    menuLayout.setVisibility(View.INVISIBLE);
                    transparentView.setVisibility(View.INVISIBLE);

                }
            }
        });

        transparentView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                menuLayout.setVisibility(View.GONE);
                transparentView.setVisibility(View.GONE);
            }
        });
        /* Start Sync from server when clicked on Sync button */
        final ImageView syncView = (ImageView) getActivity().findViewById(
                R.id.sync);
        syncView.setVisibility(View.VISIBLE);
        final int syncResourcesId[];
        if (ViewUtil.isHoneycombTablet(getActivity())) {
            final int tabletSyncResourcesId[] = {
                    R.drawable.ico_actionbar_refresh_pressed,
                    R.drawable.ico_actionbar_refresh_pressed, R.drawable.sync };
            syncResourcesId = tabletSyncResourcesId;
        } else {
            final int phoneSyncResourcesId[] = {
                    R.drawable.ico_m_actionbar_refresh_pressed,
                    R.drawable.ico_m_actionbar_refresh_pressed,
                    R.drawable.ico_m_actionbar_refresh_nor };
            syncResourcesId = phoneSyncResourcesId;
        }
        syncView.setImageDrawable(Util.getPressedImage(getActivity()
                .getBaseContext(), syncResourcesId));
        syncView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                syncAction(v);

            }
        });

        /* Open the Settings Screen when clicked on setting Menu */
        final TextView mSettingsView = (TextView) getActivity().findViewById(
                R.id.settingsTv);
        mSettingsView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* Set Action to Settings Button */
                Util.startSettingsActivity(getActivity());
                menuLayout.setVisibility(View.GONE);
                transparentView.setVisibility(View.GONE);

            }
        });

        final TextView logoutView = (TextView) getActivity().findViewById(
                R.id.logoutTv);
        logoutView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* Set Action to Settings Button */
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        final String sessionId = ((SugarCrmApp) SugarCrmApp.mApp)
                                .getSessionId();
                        final String restUrl = SugarCrmSettings
                                .getSugarRestUrl(getActivity());
                        try {
                            Rest.logoutSugarCRM(restUrl, sessionId);
                        } catch (final SugarCrmException e) {
                            Log.e(LOG_TAG, "Error While Logging out:" + e);
                        }

                    }
                }).start();

                Util.logout(getActivity());
                menuLayout.setVisibility(View.GONE);
                transparentView.setVisibility(View.GONE);
            }
        });

    }

    /**
     * Sync action.
     * 
     * @param view
     *            the view
     */
    public void syncAction(final View view) {
        if (!Util.isNetworkOn(getActivity().getBaseContext())) {
            Toast.makeText(getActivity(), R.string.networkUnavailable,
                    Toast.LENGTH_SHORT).show();
        } else {

            Util.startModuleSync(getActivity(), mModuleName);

        }
    }

    /**
     * openListScreen.
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     */
    protected void openListScreen(final String moduleName) {
        Intent detailIntent;

        if (ViewUtil.isTablet(getActivity())) {
            detailIntent = new Intent(ModuleDetailFragment.this.getActivity(),
                    ModuleDetailsMultiPaneActivity.class);
            detailIntent.putExtra(Util.ROW_ID, mRowId);
            detailIntent.putExtra("Relation", true);
        } else {
            detailIntent = new Intent(ModuleDetailFragment.this.getActivity(),
                    ModulesActivity.class);
        }

        Uri uri = Uri.withAppendedPath(ContentUtils.getModuleUri(mModuleName),
                mRowId);
        uri = Uri.withAppendedPath(uri, moduleName);
        detailIntent.setData(uri);
        detailIntent.putExtra(RestConstants.BEAN_ID, mSugarBeanId);
        detailIntent.putExtra(RestConstants.MODULE_NAME, moduleName);
        startActivity(detailIntent);

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();

        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        if ((mProgressDialog != null) && (mProgressDialog.isShowing())) {
            mProgressDialog.dismiss();
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

        if (mTask != null && mTask.getStatus() != AsyncTask.Status.RUNNING) {
            Log.i(LOG_TAG, "is Inser Successful = "
                    + EditModuleDetailFragment.bInsertSuccessful);

            if (EditModuleDetailFragment.bInsertSuccessful) {
                mTask = new LoadContentTask(getActivity());
                mTask.execute(null, null, null);
                EditModuleDetailFragment.bInsertSuccessful = false;
            }
        }

    }

    /**
     * On create options menu.
     * 
     * @param menu
     *            the menu
     * @return true, if successful
     */
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the currently selected menu XML resource.
        final MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.details_activity_menu, menu);

        final SubMenu relationshipMenu = menu.addSubMenu(1, R.string.related,
                0, getString(R.string.related));
        relationshipMenu.setIcon(R.drawable.menu_related);
        if (mRelationshipModules.length > 0) {
            for (int i = 0; i < mRelationshipModules.length; i++) {
                relationshipMenu.add(0, Menu.FIRST + i, 0,
                        mRelationshipModules[i]);
            }
        } else {
            menu.setGroupEnabled(1, false);
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem
     * )
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.string.related:
            return true;
        default:
            if (Log.isLoggable(LOG_TAG, Log.INFO)) {
                Log.i(LOG_TAG, "item id : " + item.getItemId());
            }
            openListScreen(mRelationshipModules[item.getItemId() - 1]);
            return true;
        }
    }

    /**
     * The Class LoadContentTask.
     */
    class LoadContentTask extends AsyncTask<Object, Object, Object> {

        /** The m context. */
        private final Context mContext;

        /** The m billing address group. */
        private final List<String> mBillingAddressGroup = new ArrayList<String>();

        /** The m shipping address group. */
        private final List<String> mShippingAddressGroup = new ArrayList<String>();

        /** The m duration group. */
        private final List<String> mDurationGroup = new ArrayList<String>();

        /** The m fields excluded for details. */
        private final List<String> mFieldsExcludedForDetails = new ArrayList<String>();

        /** The detail items. */
        private final Map<String, DetailsItem> detailItems = new LinkedHashMap<String, DetailsItem>();

        /**
         * Instantiates a new load content task.
         * 
         * @param context
         *            the context
         */
        LoadContentTask(final Context context) {
            mContext = context;

        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = ViewUtil.getProgressDialog(getActivity(),
                    getString(R.string.loading), true);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
            // Initializing BillingAddressGroup the list
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_STREET);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_STREET_2);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_STREET_3);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_STREET_4);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_CITY);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_STATE);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_POSTALCODE);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_COUNTRY);

            // Initializing ShippingAddressGroup the list
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_STREET);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_STREET_2);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_STREET_3);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_STREET_4);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_CITY);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_STATE);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_POSTALCODE);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_COUNTRY);

            // Initializing DurationGroup the list
            mDurationGroup.add(ModuleFields.DURATION_HOURS);
            mDurationGroup.add(ModuleFields.DURATION_MINUTES);

            // add a field name to the map if a module field in detail
            // projection is to be excluded in
            // details screen
            mFieldsExcludedForDetails.add(SugarCRMContent.RECORD_ID);
            mFieldsExcludedForDetails.add(SugarCRMContent.SUGAR_BEAN_ID);
            mFieldsExcludedForDetails.add(ModuleFields.DELETED);
            mFieldsExcludedForDetails.add(ModuleFields.ACCOUNT_ID);
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Object doInBackground(final Object... params) {
            if (mSelectFields != null) {
                try {

                    mCursor = getActivity().getContentResolver().query(mUri,
                            mSelectFields, null, null,
                            ContentUtils.getModuleSortOrder(mModuleName));
                } catch (final Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    return Util.FETCH_FAILED;
                }

                prepareDetailItems();
            }

            return Util.FETCH_SUCCESS;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(final Object result) {
            super.onPostExecute(result);

            if (detailItems.size() > 0) {
                setupDetailViews();
            }

            // close the cursor irrespective of the result
            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

        }

        /**
         * Setup detail views.
         */
        private void setupDetailViews() {
            final TextView titleView;
            if (mlayoutView == null) {
                mlayoutView = (LinearLayout) mInflater.inflate(
                        R.layout.table_row, null);

            }
            /* Get the Detail fragment module header Title view */
            titleView = (TextView) mlayoutView.findViewById(R.id.titleview);

            final String[] keys = detailItems.keySet().toArray(
                    new String[detailItems.size()]);

            if (titleView != null) {
                final String header;
                if (mModuleName.equals(Util.CONTACTS)
                        || mModuleName.equals(Util.LEADS)) {
                    Log.i(LOG_TAG, "key values" + keys[0] + " " + keys[1]);
                    header = (detailItems.get("First Name:")).getValue() + " "
                            + (detailItems.get("Last Name:")).getValue();
                } else {
                    header = (detailItems.get(keys[0])).getValue();
                }
                titleView.setText(header);

            }
            ListView lv;
            if (mlayoutView.getParent() == null) {
                if (!ViewUtil.isHoneycombTablet(getActivity())) {
                    final RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                    params1.addRule(RelativeLayout.BELOW, R.id.custom_actionbar);
                    mParent.addView(mlayoutView, params1);

                } else {
                    mParent.addView(mlayoutView);
                }

                lv = (ListView) mlayoutView.findViewById(R.id.detailList);

            } else {
                mlayoutView = null;
                mlayoutView = new LinearLayout(getActivity());
                if (!ViewUtil.isHoneycombTablet(getActivity().getBaseContext())) {
                    final RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                    params1.addRule(RelativeLayout.BELOW, R.id.custom_actionbar);
                    mParent.addView(mlayoutView, params1);
                } else {
                    mParent.addView(mlayoutView);
                }

                lv = null;
                lv = new ListView(getActivity());
            }

            if (mAdapter == null) {
                mAdapter = new SeparatedListAdapter(detailItems);
                lv.setAdapter(mAdapter);
            }

        }

        /**
         * Prepare detail items.
         */
        private void prepareDetailItems() {
            final String[] detailsProjection = mSelectFields;
            if (mCursor.moveToFirst()) {
                if (mDbHelper == null) {
                    mDbHelper = new DatabaseHelper(getActivity()
                            .getBaseContext());
                }
                Arrays.asList(ContentUtils.getModuleListSelections(mModuleName));
                Arrays.asList(ContentUtils.getModuleListSelections(mModuleName));
                String value = "";
                final Map<String, ModuleField> fieldNameVsModuleField = ContentUtils
                        .getModuleFields(mContext, mModuleName);
                for (int i = 0; i < detailsProjection.length; i++) {
                    final String fieldName = detailsProjection[i];
                    // if the field name is excluded in details screen, skip it
                    if (mFieldsExcludedForDetails.contains(fieldName)) {
                        continue;
                    }
                    final int columnIndex = mCursor.getColumnIndex(fieldName);
                    final String tempValue = mCursor.getString(columnIndex);
                    // get the attributes of the moduleField
                    final ModuleField moduleField = fieldNameVsModuleField
                            .get(fieldName);
                    if (moduleField != null) {
                        String label = moduleField.getLabel();
                        /*
                         * check for the billing and shipping address groups
                         * only if the module is 'Accounts'
                         */
                        if (Util.ACCOUNTS.equals(mModuleName)) {
                            if (mBillingAddressGroup.contains(fieldName)) {
                                if (fieldName
                                        .equals(ModuleFields.BILLING_ADDRESS_STREET)) {
                                    // First field in the group
                                    value = (!TextUtils.isEmpty(tempValue)) ? tempValue
                                            + ", "
                                            : "";
                                    continue;
                                } else if (fieldName
                                        .equals(ModuleFields.BILLING_ADDRESS_COUNTRY)) {
                                    // last field in the group
                                    value = value
                                            + (!TextUtils.isEmpty(tempValue) ? tempValue
                                                    : "");
                                    label = getActivity()
                                            .getBaseContext()
                                            .getString(R.string.billing_address);
                                } else {
                                    value = value
                                            + (!TextUtils.isEmpty(tempValue) ? tempValue
                                                    + ", "
                                                    : "");
                                    continue;
                                }
                            } else if (mShippingAddressGroup
                                    .contains(fieldName)) {
                                if (fieldName
                                        .equals(ModuleFields.SHIPPING_ADDRESS_STREET)) {
                                    // First field in the group
                                    value = (!TextUtils.isEmpty(tempValue)) ? tempValue
                                            + ", "
                                            : "";
                                    continue;
                                } else if (fieldName
                                        .equals(ModuleFields.SHIPPING_ADDRESS_COUNTRY)) {
                                    // Last field in the group
                                    value = value
                                            + (!TextUtils.isEmpty(tempValue) ? tempValue
                                                    : "");
                                    label = getActivity().getBaseContext()
                                            .getString(
                                                    R.string.shipping_address);
                                } else {

                                    value = value
                                            + (!TextUtils.isEmpty(tempValue) ? tempValue
                                                    + ", "
                                                    : "");
                                    continue;
                                }
                            } else {
                                value = tempValue;
                            }
                        } else if (mDurationGroup.contains(fieldName)) {
                            if (fieldName.equals(ModuleFields.DURATION_HOURS)) {
                                // First field in the group
                                value = (!TextUtils.isEmpty(tempValue)) ? tempValue
                                        + "hr "
                                        : "";
                                continue;
                            } else if (fieldName
                                    .equals(ModuleFields.DURATION_MINUTES)) {
                                // Last field in the group
                                value = value
                                        + (!TextUtils.isEmpty(tempValue) ? tempValue
                                                + "mins "
                                                : "");
                                label = getActivity().getBaseContext()
                                        .getString(R.string.duration);
                            }
                        } else {
                            value = tempValue;
                        }
                        if (!TextUtils.isEmpty(value)) {
                            detailItems.put(label, new DetailsItem(fieldName,
                                    value, moduleField.getType()));
                        } else {
                            detailItems.put(label, new DetailsItem(fieldName,
                                    getString(R.string.notAvailable),
                                    moduleField.getType()));
                        }

                    } else {
                        // module fields is null
                    }
                }
            }
        }
    }

    /**
     * The Class DetailsItem.
     */
    class DetailsItem {

        /** The m field name. */
        private final String mFieldName;

        /** The m value. */
        private final String mValue;

        /** The m type. */
        private final String mType;

        /**
         * Instantiates a new details item.
         * 
         * @param fieldName
         *            the field name
         * @param value
         *            the value
         * @param type
         *            the type
         */
        DetailsItem(final String fieldName, final String value,
                final String type) {
            mFieldName = fieldName;
            mValue = value;
            mType = type;
        }

        /**
         * Gets the value.
         * 
         * @return the value
         */
        public String getValue() {
            return mValue;
        }

        /**
         * Gets the type.
         * 
         * @return the type
         */
        public String getType() {
            return mType;
        }

        /**
         * Gets the field name.
         * 
         * @return the field name
         */
        public String getFieldName() {
            return mFieldName;
        }
    }

    /**
     * The Class InternalURLSpan.
     */
    static class InternalURLSpan extends ClickableSpan {

        /** The m listener. */
        private final OnClickListener mListener;

        /**
         * Instantiates a new internal url span.
         * 
         * @param listener
         *            the listener
         */
        public InternalURLSpan(final OnClickListener listener) {
            super();
            mListener = listener;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.text.style.ClickableSpan#onClick(android.view.View)
         */
        @Override
        public void onClick(final View widget) {
            Log.i(LOG_TAG, "InternalURLSpan onClick");
            mListener.onClick(widget);
        }
    }

    /**
     * An asynchronous update interface for receiving notifications about
     * DeleteContent information as the DeleteContent is constructed.
     */
    private static class DeleteContentObserver extends ContentObserver {

        /**
         * This method is called when information about an DeleteContent which
         * was previously requested using an asynchronous interface becomes
         * available.
         * 
         * @param handler
         *            the handler
         */
        public DeleteContentObserver(final Handler handler) {
            super(handler);
        }

    }

    /**
     * The Class MyYesNoAlertDialogFragment.
     */
    public class MyYesNoAlertDialogFragment extends DialogFragment {

        /**
         * New instance.
         * 
         * @param title
         *            the title
         * @return the my yes no alert dialog fragment
         */
        public MyYesNoAlertDialogFragment newInstance(final int title) {
            final MyYesNoAlertDialogFragment frag = new MyYesNoAlertDialogFragment();
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
        public Dialog onCreateDialog(final Bundle savedInstanceState) {

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.delete)
                    .setMessage(R.string.deleteAlert)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        final DialogInterface dialog,
                                        final int whichButton) {

                                    final Uri deleteUri = Uri.withAppendedPath(
                                            ContentUtils
                                                    .getModuleUri(mModuleName),
                                            mRowId);
                                    getActivity().getContentResolver()
                                            .registerContentObserver(
                                                    deleteUri,
                                                    false,
                                                    new DeleteContentObserver(
                                                            new Handler()));
                                    ServiceHelper.startServiceForDelete(
                                            getActivity().getBaseContext(),
                                            deleteUri, mModuleName,
                                            mSugarBeanId);

                                    final ContentValues values = new ContentValues();
                                    values.put(ModuleFields.DELETED,
                                            Util.DELETED_ITEM);
                                    getActivity()
                                            .getBaseContext()
                                            .getContentResolver()
                                            .update(deleteUri, values, null,
                                                    null);

                                    if (ViewUtil.isTablet(getActivity())) {

                                        getActivity()
                                                .getSupportFragmentManager()
                                                .beginTransaction()
                                                .remove(ModuleDetailFragment.this)
                                                .commit();

                                        final ModuleDetailFragment moduleDetailFragment = new ModuleDetailFragment();
                                        final Bundle args = new Bundle();
                                        args.putString(
                                                RestConstants.MODULE_NAME,
                                                mModuleName);
                                        int rowId = Integer.parseInt(mRowId);
                                        rowId = rowId - 1;

                                        args.putString(Util.ROW_ID,
                                                Integer.toString(rowId));
                                        args.putString(RestConstants.BEAN_ID,
                                                mSugarBeanId);
                                        moduleDetailFragment.setArguments(args);
                                        getFragmentManager()
                                                .beginTransaction()
                                                .replace(
                                                        R.id.fragment_container_module_detail,
                                                        moduleDetailFragment)
                                                .commit();

                                    } else {
                                        ModuleDetailFragment.this.getActivity()
                                                .finish();
                                    }

                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        final DialogInterface dialog,
                                        final int whichButton) {
                                }
                            }).create();
        }
    }

    /**
     * Open detail screen.
     * 
     * @param rowId
     *            the row id
     */
    void openDetailScreen(String rowId) {

        final Intent detailIntent = new Intent(getActivity(),
                ModuleDetailActivity.class);

        Uri uri = Uri.withAppendedPath(ContentUtils.getModuleUri(mModuleName),
                mRowId);
        uri = Uri.withAppendedPath(uri, mModuleName);
        detailIntent.setData(uri);
        detailIntent.putExtra(Util.ROW_ID, rowId);
        detailIntent.putExtra(RestConstants.BEAN_ID, mSugarBeanId);
        detailIntent.putExtra(RestConstants.MODULE_NAME, mModuleName);

        if (ViewUtil.isTablet(getActivity())) {

            ((BaseMultiPaneActivity) getActivity())
                    .openActivityOrFragment(detailIntent);

        } else {
            startActivity(detailIntent);
        }
    }

    /**
     * The Class SeparatedListAdapter.
     */
    private class SeparatedListAdapter extends BaseAdapter {

        /** The m data. */
        private final LinkedHashMap<String, DetailsItem> mData;

        /** The inflater. */
        private final LayoutInflater inflater = (LayoutInflater) getActivity()
                .getBaseContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);

        /** The headerview1. */
        private TextView headerview1;

        /** The headerview2. */
        private TextView headerview2;

        /** The assigned icon view. */
        private ImageView assignedIconView;

        /** The lable view. */
        private TextView lableView;

        /** The summry view. */
        private TextView summryView;

        /** The m keys. */
        private final String[] mKeys;

        /** The list item_1. */
        private static final int LISTITEM_1 = 1;

        /** The list item_2. */
        private static final int LISTITEM_2 = 2;

        /** The list item_3. */
        private static final int LISTITEM_3 = 3;

        /**
         * Instantiates a new separated list adapter.
         * 
         * @param detailItems
         *            the data
         */
        public SeparatedListAdapter(final Map<String, DetailsItem> detailItems) {

            mData = (LinkedHashMap<String, DetailsItem>) detailItems;
            mKeys = mData.keySet().toArray(new String[detailItems.size()]);

        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            getItemViewType(position);
            if (position == 0) {
                convertView = inflater.inflate(
                        R.layout.detaillist_item_section, null);

                headerview1 = (TextView) convertView
                        .findViewById(R.id.headerview1);
                headerview2 = (TextView) convertView
                        .findViewById(R.id.headerview2);

            } else {
                convertView = inflater.inflate(R.layout.detaillist_item, null);
                lableView = (TextView) convertView
                        .findViewById(R.id.list_item_entry_lable);
                summryView = (TextView) convertView
                        .findViewById(R.id.list_item_summary);
                assignedIconView = (ImageView) convertView
                        .findViewById(R.id.list_assigned_icon);

            }

            if (position == 0) {

                /* set the Header Fileds */
                if (headerview1 != null) {
                    Log.i(LOG_TAG, "Module Name =" + mModuleName);
                    int pos;
                    /*
                     * if the module is leads or contact then the First header
                     * contain first name + last name do pos = position + 2
                     * (position[0] = first name & position[1] = Last name
                     */
                    if (mModuleName.equals(Util.CONTACTS)
                            || mModuleName.equals(Util.LEADS)) {
                        pos = position + LISTITEM_2;
                    } else {
                        pos = position + LISTITEM_1;

                    }
                    headerview1.setText((mData.get(mKeys[pos])).getValue()
                            .toString());
                }

                if (headerview2 != null) {
                    if (mModuleName.equals(Util.CONTACTS)
                            || mModuleName.equals(Util.LEADS)) {
                        headerview2.setText((mData.get(mKeys[LISTITEM_3]))
                                .getValue().toString());
                    } else {
                        headerview2.setText((mData.get(mKeys[LISTITEM_2]))
                                .getValue().toString());
                    }
                }

            } else {

                /* set the filed to the text view */
                final String field = mKeys[position];
                final String value = (mData.get(mKeys[position])).getValue()
                        .toString();

                if (mModuleName.equals(Util.CONTACTS)
                        || mModuleName.equals(Util.LEADS)) {
                    if (position == LISTITEM_1 || position == LISTITEM_2
                            || position == LISTITEM_3) {
                        lableView.setHeight(0);
                        summryView.setHeight(0);
                        return convertView;

                    }
                } else {
                    if (position == LISTITEM_1 || position == LISTITEM_2) {
                        lableView.setHeight(0);
                        summryView.setHeight(0);
                        return convertView;

                    }
                }

                if (lableView != null) {
                    lableView.setText(field);
                }

                /* set the field value to the view */
                if (summryView != null) {
                    summryView.setText(value);
                }

                if (field.equalsIgnoreCase("Assigned to:")
                        || field.equalsIgnoreCase("Assigned to")) {
                    assignedIconView.setVisibility(View.VISIBLE);
                } else {
                    assignedIconView.setVisibility(View.GONE);
                }

                // handle the map
                if (ModuleFields.SHIPPING_ADDRESS_COUNTRY.equals(mData.get(
                        mKeys[position]).getFieldName())
                        || ModuleFields.BILLING_ADDRESS_COUNTRY.equals(mData
                                .get(mKeys[position]).getFieldName())
                        && (!TextUtils.isEmpty(value) && (!value
                                .contains("Not Available")))) {
                    summryView.setLinksClickable(true);
                    summryView.setClickable(true);

                    final SpannableString spannableString = new SpannableString(
                            value);
                    spannableString.setSpan(
                            new InternalURLSpan(new OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    Log.i(LOG_TAG, "trying to locate - "
                                            + value);
                                    final Uri uri = Uri.parse("geo:0,0?q="
                                            + URLEncoder.encode(value));
                                    final Intent intent = new Intent(
                                            Intent.ACTION_VIEW, uri);
                                    intent.setData(uri);
                                    startActivity(Intent.createChooser(intent,
                                            getString(R.string.showAddressMsg)));
                                }
                            }), 0, value.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    summryView.setText(spannableString);

                    // for trackball movement
                    final MovementMethod m = summryView.getMovementMethod();
                    if ((m == null) || !(m instanceof LinkMovementMethod)
                            && (summryView.getLinksClickable())) {
                        summryView.setMovementMethod(LinkMovementMethod
                                .getInstance());

                    }

                }

            }

            return convertView;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(final int position) {
            // TODO Auto-generated method stub
            return position;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Object getItem(final int position) {
            // TODO Auto-generated method stub
            return null;
        }

    }

}
