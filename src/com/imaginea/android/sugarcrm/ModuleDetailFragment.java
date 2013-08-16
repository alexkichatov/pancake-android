package com.imaginea.android.sugarcrm;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import android.text.util.Linkify;
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
import android.widget.TextView;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.ui.BaseActivity;
import com.imaginea.android.sugarcrm.ui.BaseMultiPaneActivity;
import com.imaginea.android.sugarcrm.ui.ModuleDetailsMultiPaneActivity;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.ServiceHelper;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

/**
 * ModuleDetailFragment is used to show detail description for all modules.
 * 
 */
public class ModuleDetailFragment extends Fragment {

    private String mRowId;

    private String mSugarBeanId;

    private String mModuleName;

    private Cursor mCursor;

    private SeparatedListAdapter adapter = null;

    private String[] mSelectFields;
    private String[] mRelationshipModules;

    private Uri mUri;

    private DatabaseHelper mDbHelper;

    private LoadContentTask mTask;

    private ProgressDialog mProgressDialog;

    private CustomActionbar actionBar;

    private LinearLayout mParent;

    private Bundle bundle;

    final Map<String, String> HeaderItem = new HashMap<String, String>();

    private LinearLayout layoutView;
    private LayoutInflater inflater;

    private static final String LOG_TAG = ModuleDetailFragment.class
            .getSimpleName();

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mParent = (LinearLayout) inflater.inflate(R.layout.account_details,
                container, false);

        bundle = getArguments();

        return mParent;
    }

    /** {@inheritDoc} */
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*
         * As per the new UI Tablet current Design Contain Dash board Image list
         * Fragment else it would be same dashboard activity.
         */
        if (!ViewUtil.isHoneycombTablet(getActivity())) {
            getFragmentManager().findFragmentById(R.id.list_imagefrag)
                    .getView().setVisibility(View.GONE);
        }

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
            mRowId = bundle.getString(Util.ROW_ID);
            mSugarBeanId = bundle.getString(RestConstants.BEAN_ID);
            mModuleName = bundle.getString(RestConstants.MODULE_NAME);

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

        inflater = (LayoutInflater) getActivity().getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layoutView = (LinearLayout) inflater.inflate(R.layout.table_row, null);
        /* To edit the module details fileds */
        final ImageView editImageView = (ImageView) layoutView
                .findViewById(R.id.editimage);
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

        /* To Delete the selected Module Details */
        final ImageView deleteImageView = (ImageView) layoutView
                .findViewById(R.id.deleteimage);
        deleteImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogFragment newFragment = new MyYesNoAlertDialogFragment()
                        .newInstance(R.string.delete);
                // String lastSelectedModuleName = mModuleName;
                newFragment.show(getFragmentManager(), "YesNoAlertDialog");
            }
        });
        /* Starting a New Async task to populate the Data from the Database */
        mTask = new LoadContentTask(getActivity());
        mTask.execute(null, null, null);

    }

    /**
     * <p>
     * openListScreen
     * </p>
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

    /** {@inheritDoc} */
    @Override
    public void onPause() {
        super.onPause();

        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mTask != null && mTask.getStatus() != AsyncTask.Status.RUNNING) {
            Log.i(LOG_TAG, "is Inser Successful = "
                    + EditModuleDetailFragment.bInsertSuccessful);

            if (EditModuleDetailFragment.bInsertSuccessful == true) {
                mTask = new LoadContentTask(getActivity());
                mTask.execute(null, null, null);
                EditModuleDetailFragment.bInsertSuccessful = false;
            }
        }

    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.home:
            final Intent myIntent = new Intent(
                    ModuleDetailFragment.this.getActivity(),
                    DashboardActivity.class);
            ModuleDetailFragment.this.startActivity(myIntent);
            return true;
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

    class LoadContentTask extends AsyncTask<Object, Object, Object> {

        Context mContext;

        private final List<String> mBillingAddressGroup = new ArrayList<String>();

        private final List<String> mShippingAddressGroup = new ArrayList<String>();

        private final List<String> mDurationGroup = new ArrayList<String>();

        private final List<String> mFieldsExcludedForDetails = new ArrayList<String>();

        private final LinkedHashMap<String, DetailsItem> detailItems = new LinkedHashMap<String, DetailsItem>();

        private String title;

        LoadContentTask(final Context context) {
            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = ViewUtil.getProgressDialog(getActivity(),
                    getString(R.string.loading), true);
            mProgressDialog.show();

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

            mProgressDialog.cancel();
            mProgressDialog = null;
        }

        private void setupDetailViews() {
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
            final TextView titleView;
            if (layoutView == null) {
                layoutView = (LinearLayout) inflater.inflate(
                        R.layout.table_row, null);

            }
            /* Get the Detail fragment module header Title view */
            titleView = (TextView) layoutView.findViewById(R.id.titleview);

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
            if (layoutView.getParent() == null) {
                mParent.addView(layoutView);
                lv = (ListView) layoutView.findViewById(R.id.detailList);

            } else {
                layoutView = null;
                final LinearLayout layoutView = new LinearLayout(getActivity());
                mParent.addView(layoutView);
                lv = null;
                lv = new ListView(getActivity());
            }

            if (adapter == null) {
                adapter = new SeparatedListAdapter(detailItems);
                lv.setAdapter(adapter);
            }

        }

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
                    if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                        Log.d(LOG_TAG, "Col:" + columnIndex + " moduleName : "
                                + mModuleName + " fieldName : " + fieldName);
                    }

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

    class DetailsItem {
        private final String mFieldName;
        private final String mValue;
        private final String mType;

        DetailsItem(final String fieldName, final String value,
                final String type) {
            mFieldName = fieldName;
            mValue = value;
            mType = type;
        }

        public String getValue() {
            return mValue;
        }

        public String getType() {
            return mType;
        }

        public String getFieldName() {
            return mFieldName;
        }
    }

    static class InternalURLSpan extends ClickableSpan {
        OnClickListener mListener;

        public InternalURLSpan(final OnClickListener listener) {
            super();
            mListener = listener;
        }

        @Override
        public void onClick(final View widget) {
            Log.i(LOG_TAG, "InternalURLSpan onClick");
            mListener.onClick(widget);
        }
    }

    private static class DeleteContentObserver extends ContentObserver {

        public DeleteContentObserver(final Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(final boolean selfChange) {
            super.onChange(selfChange);
        }
    }

    public class MyYesNoAlertDialogFragment extends DialogFragment {

        public MyYesNoAlertDialogFragment newInstance(final int title) {
            final MyYesNoAlertDialogFragment frag = new MyYesNoAlertDialogFragment();
            final Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

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

    private class SeparatedListAdapter extends BaseAdapter {

        private final LinkedHashMap<String, DetailsItem> mData;
        final LayoutInflater inflater = (LayoutInflater) getActivity()
                .getBaseContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
        public TextView headerview1;
        public TextView headerview2;
        public ImageView assignedIconView;
        public TextView lableView;
        public TextView summryView;
        String[] mKeys;
        int listItem_1 = 1;
        int listItem_2 = 2;
        int listItem_3 = 3;

        public SeparatedListAdapter(
                final LinkedHashMap<String, DetailsItem> data) {

            mData = data;
            mKeys = mData.keySet().toArray(new String[data.size()]);

        }

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
                        pos = position + listItem_2;
                    } else {
                        pos = position + listItem_1;

                    }
                    headerview1.setText((mData.get(mKeys[pos])).getValue()
                            .toString());
                }

                if (headerview2 != null) {
                    if (mModuleName.equals(Util.CONTACTS)
                            || mModuleName.equals(Util.LEADS)) {
                        headerview2.setText((mData.get(mKeys[listItem_3]))
                                .getValue().toString());
                    } else {
                        headerview2.setText((mData.get(mKeys[listItem_2]))
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
                    if (position == listItem_1 || position == listItem_2
                            || position == listItem_3) {
                        lableView.setHeight(0);
                        summryView.setHeight(0);
                        return convertView;

                    }
                } else {
                    if (position == listItem_1 || position == listItem_2) {
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

                if (field.contains("phone")) {
                    summryView.setAutoLinkMask(Linkify.PHONE_NUMBERS);
                }
                // handle the map
                if (ModuleFields.SHIPPING_ADDRESS_COUNTRY.equals(field)
                        || ModuleFields.BILLING_ADDRESS_COUNTRY.equals(field)) {
                    if (!TextUtils.isEmpty(value)) {
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
                                        startActivity(Intent
                                                .createChooser(
                                                        intent,
                                                        getString(R.string.showAddressMsg)));
                                    }
                                }), 0, value.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        summryView.setText(spannableString);

                        // for trackball movement
                        final MovementMethod m = summryView.getMovementMethod();
                        if ((m == null) || !(m instanceof LinkMovementMethod)) {
                            if (summryView.getLinksClickable()) {
                                summryView.setMovementMethod(LinkMovementMethod
                                        .getInstance());
                            }
                        }

                    }
                }

            }

            return convertView;
        }

        @Override
        public long getItemId(final int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }

        @Override
        public Object getItem(final int position) {
            // TODO Auto-generated method stub
            return null;
        }

    }

}
