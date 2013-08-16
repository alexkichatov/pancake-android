package com.imaginea.android.sugarcrm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginea.android.sugarcrm.CustomActionbar.Action;
import com.imaginea.android.sugarcrm.CustomActionbar.IntentAction;
import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Recent;
import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.ui.BaseMultiPaneActivity;
import com.imaginea.android.sugarcrm.ui.RecentModuleMultiPaneActivity;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.ServiceHelper;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

/**
 * ModuleListFragment, lists the view projections for all the modules.
 * 
 */
public class ModuleListFragment extends ListFragment implements
        LoaderCallbacks<Cursor> {

    private ListView mListView;

    private String mModuleName;

    private Uri mModuleUri;

    private Uri mIntentUri;

    private int mCurrentSelection;

    private DatabaseHelper mDbHelper;

    private SimpleCursorAdapter mAdapter;

    private String[] mModuleFields;

    private String[] mModuleFieldsChoice;

    private final Map<String, String> fieldMap = new HashMap<String, String>();

    private int mSortColumnIndex;

    private String mSelections = ModuleFields.DELETED + "=?";

    private String[] mSelectionArgs = new String[] { Util.EXCLUDE_DELETED_ITEMS };

    private SugarCrmApp app;

    private final boolean bRelationItem = true;

    public final static String LOG_TAG = ModuleListFragment.class
            .getSimpleName();

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {

        return inflater.inflate(R.layout.common_list, container, false);

    }

    /** {@inheritDoc} */
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Dummy Holder Object to hold previously selected list row state */
        final ListItemView listItemView = new ListItemView(null, 0);

        mDbHelper = new DatabaseHelper(getActivity().getBaseContext());
        app = (SugarCrmApp) getActivity().getApplication();
        final Intent intent = getActivity().getIntent();

        // final Intent intent =
        // BaseActivity.fragmentArgumentsToIntent(getArguments());
        final Bundle extras = intent.getExtras();
        mModuleName = Util.CONTACTS;
        if (extras != null) {
            mModuleName = extras.getString(RestConstants.MODULE_NAME);
        }

        mIntentUri = intent.getData();

        mListView = getListView();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> arg0, final View view,
                    final int position, final long id) {
                Log.e(LOG_TAG, "item clicked::" + position);

                view.setBackgroundColor(getResources().getColor(
                        ContentUtils.getModuleColor(mModuleName)));

                if (listItemView.getView() != null) {
                    // check for odd or even to set alternate colors to the row
                    // background
                    if (listItemView.getPosition() % 2 == 0) {
                        listItemView.getView().setBackgroundColor(
                                getResources().getColor(R.color.listview_row1));
                    } else {
                        listItemView.getView().setBackgroundColor(
                                getResources().getColor(R.color.listview_row2));
                    }
                }

                /* Update current row to the holder object */
                listItemView.setView(view);
                listItemView.setPosition(position);
                addToRecent(position);
                openDetailScreen(position);
            }
        });

        // button code in the layout - 1.6 SDK feature to specify onClick
        mListView.setItemsCanFocus(true);
        mListView.setFocusable(true);
        mListView.setFastScrollEnabled(false);
        mListView.setScrollbarFadingEnabled(true);

        final View empty = getActivity().findViewById(R.id.empty);
        final TextView tv1 = (TextView) (empty.findViewById(R.id.mainText));
        tv1.setText("No " + mModuleName + " found");
        mListView.setEmptyView(empty);

        registerForContextMenu(getListView());

        mModuleUri = ContentUtils.getModuleUri(mModuleName);
        if (mIntentUri == null) {
            intent.setData(mModuleUri);
            mIntentUri = mModuleUri;
        }

        int[] ids = new int[] { R.id.text1, R.id.text2, R.id.text3 };
        mModuleFields = ContentUtils.getModuleListSelections(mModuleName);

        if (mModuleName.equals(Util.CONTACTS) || mModuleName.equals(Util.LEADS)) {
            ids = new int[] { R.id.text1, R.id.textLastName, R.id.text2,
                    R.id.text3 };
        } else if (mModuleName.equals(Util.RECENT)) {
            ids = new int[] { R.id.text1, R.id.text2, R.id.text5 };
        }

        Log.e(LOG_TAG, mModuleName + " Fields length::" + mModuleFields.length);
        if (mModuleFields.length >= 2) {
            mAdapter = new CustomCursorAdapter(getActivity(),
                    R.layout.contact_listitem, null, mModuleFields, ids, 0);
        } else {
            mAdapter = new CustomCursorAdapter(getActivity(),
                    R.layout.contact_listitem, null, mModuleFields,
                    new int[] { android.R.id.text1 }, 0);
        }

        setListAdapter(mAdapter);
        // make the list filterable using the keyboard
        mListView.setTextFilterEnabled(true);

        // get the module fields for the module
        final Map<String, ModuleField> map = ContentUtils.getModuleFields(
                getActivity(), mModuleName);
        if (map == null) {
            Log.w(LOG_TAG, "Cannot prepare Options as Map is null for module:"
                    + mModuleName);
            // TODO return false;
            return;
        }
        mModuleFieldsChoice = new String[mModuleFields.length];
        for (int i = 0; i < mModuleFields.length; i++) {
            // add the module field label to be displayed in the choice menu
            final ModuleField modField = map.get(mModuleFields[i]);
            if (modField != null) {
                mModuleFieldsChoice[i] = modField.getLabel();
                // fieldMap: label vs name
                fieldMap.put(mModuleFieldsChoice[i], mModuleFields[i]);
            } else {
                mModuleFieldsChoice[i] = "";
            }

            if (mModuleFieldsChoice[i].indexOf(":") > 0) {
                mModuleFieldsChoice[i] = mModuleFieldsChoice[i].substring(0,
                        mModuleFieldsChoice[i].length() - 1);
                fieldMap.put(mModuleFieldsChoice[i], mModuleFields[i]);
            }
        }
        /* Action bar related modification */
        setUpActionBar();

        getLoaderManager().initLoader(0, null, this);
    }

    private void setUpActionBar() {

        final CustomActionbar actionBar = (CustomActionbar) getActivity()
                .findViewById(R.id.custom_actionbar);

        final Intent myIntent = new Intent(getActivity(),
                RecentModuleMultiPaneActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.putExtra(RestConstants.MODULE_NAME, Util.RECENT);

        final Action recentAction = new IntentAction(
                ModuleListFragment.this.getActivity(), myIntent);

        actionBar.setHomeAction(recentAction);
        actionBar.setTitle(mModuleName);

        final ImageView settingsView = (ImageView) getActivity().findViewById(
                R.id.settings);
        settingsView.setVisibility(View.VISIBLE);

        /* Set Action to Settings Button */
        final Action settingsAction = new IntentAction(
                ModuleListFragment.this.getActivity(), createSettingsIntent());
        actionBar.setSettingAction(settingsAction);

        final ImageView syncView = (ImageView) getActivity().findViewById(
                R.id.sync);
        syncView.setVisibility(View.VISIBLE);
        syncView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SyncAction(v);

            }
        });

        final ImageView searchView = (ImageView) getActivity().findViewById(
                R.id.search);
        searchView.setVisibility(View.VISIBLE);

        searchView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onSearchRequested();

            }
        });

        final LinearLayout addNewView = (LinearLayout) getActivity()
                .findViewById(R.id.addNew);
        addNewView.setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.add);
        /* Set Action to Add Button */
        final Action addAction = new IntentAction(
                ModuleListFragment.this.getActivity(), AddAction());
        actionBar.setAddAction(addAction);

        getResources().getConfiguration();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            final TextView addNewTextView = (TextView) getActivity()
                    .findViewById(R.id.addNewText);
            addNewTextView.setVisibility(View.GONE);
        }

        final Spinner spinnerView = (Spinner) getActivity().findViewById(
                R.id.orderBySpinner);
        spinnerView.setVisibility(View.VISIBLE);
        final Map<String, String> mSortOrder = app
                .getModuleSortOrder(mModuleName);

        if (mSortOrder != null) {
            for (final Entry<String, String> entry : mSortOrder.entrySet()) {
                for (mSortColumnIndex = 0; mSortColumnIndex < mModuleFieldsChoice.length;) {
                    if (fieldMap.get(mModuleFieldsChoice[mSortColumnIndex])
                            .equals(entry.getKey())) {
                        break;
                    }
                    mSortColumnIndex++;
                }
            }
        }
        if (mSortColumnIndex == mModuleFieldsChoice.length) {
            mSortColumnIndex = 0;
        }

        // String[] mModules = (String[]) mSortOrder.keySet().toArray(new
        // String[mSortOrder.size()]);

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                mModuleFieldsChoice);
        spinnerView.setAdapter(spinnerArrayAdapter);
        spinnerView
                .setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView,
                            View selectedItemView, int position, long id) {
                        // your code here
                        final String sortOrder = mModuleFields[position]
                                + " ASC";
                        app.setModuleSortOrder(mModuleName,
                                fieldMap.get(mModuleFieldsChoice[position]),
                                "ASC");
                        Log.e(LOG_TAG, "Sort order::" + sortOrder);
                        sortList(sortOrder);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });
        final View separator2 = getActivity().findViewById(
                R.id.actionbar_homeseperator2);
        separator2.setVisibility(View.VISIBLE);

    }

    private Intent createSettingsIntent() {
        final Intent myIntent = new Intent(
                ModuleListFragment.this.getActivity(), SugarCrmSettings.class);
        myIntent.putExtra(RestConstants.MODULE_NAME, "settings");
        return myIntent;
    }

    /**
     * opens the Detail Screen
     * 
     * @param position
     */
    void openDetailScreen(final int position) {

        final Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            Log.w(LOG_TAG, "openDetailScreen, Cursor is null for " + position);
            return;
        }
        final Intent detailIntent = new Intent(getActivity(),
                ModuleDetailActivity.class);
        detailIntent.putExtra(Util.ROW_ID, cursor.getString(0));
        detailIntent.putExtra(RestConstants.BEAN_ID, cursor.getString(1));
        detailIntent.putExtra(RestConstants.MODULE_NAME, mModuleName);
        detailIntent.putExtra("Relation", bRelationItem);
        if (ViewUtil.isTablet(getActivity())) {
            /*
             * We can display everything in-place with fragments. Have the list
             * highlight this item and show the data. Check what fragment is
             * shown, replace if needed.
             */
            // ModuleDetailFragment details = (ModuleDetailFragment)
            // getFragmentManager().findFragmentByTag("module_detail");
            // Log.d("onClick list item", "details is null");
            ((BaseMultiPaneActivity) getActivity())
                    .openActivityOrFragment(detailIntent);

        } else {
            startActivity(detailIntent);
        }
    }

    /**
     * opens the Edit Screen
     * 
     * @param position
     */
    private void openEditScreen(final int position) {

        final Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor == null)
            // For some reason the requested item isn't available, do nothing
            return;
        final Intent editDetailsIntent = new Intent(getActivity(),
                EditModuleDetailActivity.class);
        editDetailsIntent.putExtra(Util.ROW_ID, cursor.getString(0));
        if (mIntentUri != null) {
            editDetailsIntent.setData(Uri.withAppendedPath(mIntentUri,
                    cursor.getString(0)));
        }

        editDetailsIntent.putExtra(RestConstants.BEAN_ID, cursor.getString(1));
        editDetailsIntent.putExtra(RestConstants.MODULE_NAME, mModuleName);

        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, "beanId:" + cursor.getString(1));
        }

        startActivity(editDetailsIntent);

    }

    /**
     * deletes an item
     */
    void deleteItem() {
        final Cursor cursor = (Cursor) getListAdapter().getItem(
                mCurrentSelection);
        if (cursor == null)
            // For some reason the requested item isn't available, do nothing
            return;

        final String beanId = cursor.getString(1);
        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, "beanId:" + beanId);
        }

        mModuleUri = ContentUtils.getModuleUri(mModuleName);
        final Uri deleteUri = Uri.withAppendedPath(mModuleUri,
                cursor.getString(0));
        getActivity().getContentResolver().registerContentObserver(deleteUri,
                false, new DeleteContentObserver(new Handler()));
        ServiceHelper.startServiceForDelete(getActivity().getBaseContext(),
                deleteUri, mModuleName, beanId);
        final ContentValues values = new ContentValues();
        values.put(ModuleFields.DELETED, Util.DELETED_ITEM);
        getActivity().getBaseContext().getContentResolver()
                .update(deleteUri, values, null, null);

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

    /** {@inheritDoc} */
    // TODO
    // @Override
    public boolean onSearchRequested() {
        final Bundle appData = new Bundle();
        final String[] modules = { mModuleName };
        appData.putString(RestConstants.MODULE_NAME, mModuleName);
        appData.putStringArray(RestConstants.MODULES, modules);
        appData.putInt(RestConstants.OFFSET, 0);
        appData.putInt(RestConstants.MAX_RESULTS, 20);

        getActivity().startSearch(null, false, appData, false);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v,
            final ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.options);
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (final ClassCastException e) {
            Log.e(LOG_TAG, "Bad menuInfo", e);
            return;
        }

        if (mDbHelper == null) {
            mDbHelper = new DatabaseHelper(getActivity().getBaseContext());
        }

        final Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null)
            // For some reason the requested item isn't available, do nothing
            return;
        final int index = cursor.getColumnIndex(ModuleFields.CREATED_BY_NAME);
        final String ownerName = cursor.getString(index);

        menu.add(1, R.string.view, 2, R.string.view).setEnabled(
                mDbHelper.isAclEnabled(mModuleName, RestConstants.VIEW,
                        ownerName));
        menu.add(2, R.string.edit, 3, R.string.edit).setEnabled(
                mDbHelper.isAclEnabled(mModuleName, RestConstants.EDIT,
                        ownerName));
        menu.add(3, R.string.delete, 4, R.string.delete).setEnabled(
                mDbHelper.isAclEnabled(mModuleName, RestConstants.DELETE,
                        ownerName));

        // TODO disable options based on acl actions for the module

        // TODO
        if (ContentUtils.getModuleField(getActivity(), mModuleName,
                ModuleFields.PHONE_WORK) != null) {
            menu.add(4, R.string.call, 4, R.string.call);
        }
        if (ContentUtils.getModuleField(getActivity(), mModuleName,
                ModuleFields.EMAIL1) != null) {
            menu.add(5, R.string.email, 4, R.string.email);
        }

    }

    /** {@inheritDoc} */
    @Override
    public boolean onContextItemSelected(final MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (final ClassCastException e) {
            Log.e(LOG_TAG, "bad menuInfo", e);
            return false;
        }
        final int position = info.position;

        addToRecent(position);

        switch (item.getItemId()) {
        case R.string.view:
            openDetailScreen(position);
            return true;

        case R.string.edit:
            openEditScreen(position);
            return true;

        case R.string.delete:
            mCurrentSelection = position;
            // getActivity().showDialog(R.string.delete);
            final DialogFragment newFragment = new ModuleListAlertDialogFragment(
                    R.string.delete);
            newFragment.show(getFragmentManager(), "dialog");
            return true;

        case R.string.call:
            callNumber(position);
            return true;

        case R.string.email:
            sendMail(position);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void sortList(final String sortOrder) {
        final String selection = null;
        mSelections = selection;
        mSelectionArgs = null;
        getLoaderManager().restartLoader(0, null, this);
    }

    void addToRecent(final int position) {
        final ContentValues modifiedValues = new ContentValues();
        // push the selected record into recent table
        final Cursor cursor = (Cursor) getListAdapter().getItem(position);

        // now insert into recent table
        modifiedValues.put(Recent.ACTUAL_ID, cursor.getInt(0) + "");
        modifiedValues.put(Recent.BEAN_ID, cursor.getString(1));
        modifiedValues.put(Recent.NAME_1, cursor.getString(2));
        modifiedValues.put(Recent.NAME_2, cursor.getString(3));
        modifiedValues.put(Recent.REF_MODULE_NAME, mModuleName);
        modifiedValues.put(Recent.DELETED, "0");
        final Uri insertResultUri = getActivity().getApplicationContext()
                .getContentResolver()
                .insert(Recent.CONTENT_URI, modifiedValues);
        Log.i(LOG_TAG, "insertResultURi - " + insertResultUri);

    }

    private String getSortOrder() {
        String sortOrder = null;
        final Map<String, String> sortOrderMap = app
                .getModuleSortOrder(mModuleName);
        for (final Entry<String, String> entry : sortOrderMap.entrySet()) {
            sortOrder = entry.getKey() + " " + entry.getValue();
        }
        return sortOrder;
    }

    /**
     * <p>
     * callNumber
     * </p>
     * 
     * @param position
     *            a int.
     */
    public void callNumber(final int position) {
        final Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor == null)
            // For some reason the requested item isn't available, do nothing
            return;

        final int index = cursor.getColumnIndex(ModuleFields.PHONE_WORK);
        final String number = cursor.getString(index);
        final Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                + number));
        startActivity(intent);
    }

    /**
     * <p>
     * sendMail
     * </p>
     * 
     * @param position
     *            a int.
     */
    public void sendMail(final int position) {
        final Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor == null)
            // For some reason the requested item isn't available, do nothing
            return;
        // emailAddress
        final int index = cursor.getColumnIndex(ModuleFields.EMAIL1);
        final String emailAddress = cursor.getString(index);
        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("mailto:" + emailAddress));
        startActivity(Intent.createChooser(intent,
                getActivity().getString(R.string.email)));
    }

    private Intent AddAction() {
        final Intent myIntent = new Intent(getActivity(),
                EditModuleDetailActivity.class);
        myIntent.putExtra(RestConstants.MODULE_NAME, mModuleName);
        return myIntent;

    }

    public void SyncAction(final View view) {
        if (!Util.isNetworkOn(getActivity().getBaseContext())) {
            Toast.makeText(getActivity(), R.string.networkUnavailable,
                    Toast.LENGTH_SHORT).show();
        } else {
            final SugarCrmApp app = (SugarCrmApp) getActivity()
                    .getApplication();
            final String usr = SugarCrmSettings.getUsername(getActivity())
                    .toString();
            if (ContentResolver.isSyncActive(app.getAccount(usr),
                    SugarCRMProvider.AUTHORITY)) {
                final DialogFragment newFragment = new ModuleListAlertDialogFragment(
                        R.string.info);
                newFragment.show(getFragmentManager(), "dialog");
                return;
            }
            startModuleSync();

        }
    }

    private void startModuleSync() {
        Log.d(LOG_TAG, "startModuleSync");
        final Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
        extras.putInt(Util.SYNC_TYPE, Util.SYNC_MODULE_DATA);
        extras.putString(RestConstants.MODULE_NAME, mModuleName);
        final SugarCrmApp app = (SugarCrmApp) ModuleListFragment.this
                .getActivity().getApplication();
        final String usr = SugarCrmSettings.getUsername(
                ModuleListFragment.this.getActivity()).toString();
        ContentResolver.requestSync(app.getAccount(usr),
                SugarCRMProvider.AUTHORITY, extras);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int arg0, final Bundle arg1) {

        return new CursorLoader(getActivity(), mIntentUri,
                ContentUtils.getModuleProjections(mModuleName), mSelections,
                mSelectionArgs, getSortOrder());

    }

    @Override
    public void onLoadFinished(final Loader<Cursor> arg0, final Cursor data) {

        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(final Loader<Cursor> arg0) {

        mAdapter.swapCursor(null);

    }

    private class ModuleListAlertDialogFragment extends DialogFragment {

        public ModuleListAlertDialogFragment(final int title) {
            final Bundle args = new Bundle();
            args.putInt("title", title);
            setArguments(args);
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final int title = getArguments().getInt("title");

            switch (title) {
            case R.string.sortBy:
                mSortColumnIndex = 0;

                final Map<String, String> sortOrderMap = app
                        .getModuleSortOrder(mModuleName);
                if (sortOrderMap != null) {
                    for (final Entry<String, String> entry : sortOrderMap
                            .entrySet()) {
                        for (mSortColumnIndex = 0; mSortColumnIndex < mModuleFieldsChoice.length;) {
                            if (fieldMap.get(
                                    mModuleFieldsChoice[mSortColumnIndex])
                                    .equals(entry.getKey())) {
                                break;
                            }
                            mSortColumnIndex++;
                        }
                    }
                }
                if (mSortColumnIndex == mModuleFieldsChoice.length) {
                    mSortColumnIndex = 0;
                }
                return new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(title)
                        .setSingleChoiceItems(mModuleFieldsChoice,
                                mSortColumnIndex,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            final DialogInterface dialog,
                                            final int whichButton) {
                                        mSortColumnIndex = whichButton;
                                    }
                                })
                        .setPositiveButton(R.string.ascending,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            final DialogInterface dialog,
                                            final int whichButton) {
                                        final String sortOrder = mModuleFields[mSortColumnIndex]
                                                + " ASC";
                                        app.setModuleSortOrder(
                                                mModuleName,
                                                fieldMap.get(mModuleFieldsChoice[mSortColumnIndex]),
                                                "ASC");
                                        sortList(sortOrder);
                                    }
                                })

                        .setNegativeButton(R.string.descending,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            final DialogInterface dialog,
                                            final int whichButton) {
                                        // ((FragmentAlertDialog)getActivity()).doNegativeClick();
                                        final String sortOrder = mModuleFields[mSortColumnIndex]
                                                + " DESC";
                                        app.setModuleSortOrder(
                                                mModuleName,
                                                fieldMap.get(mModuleFieldsChoice[mSortColumnIndex]),
                                                "DESC");
                                        sortList(sortOrder);
                                    }
                                }).create();

            case R.string.delete:
                return new AlertDialog.Builder(getActivity())
                        .setTitle(title)
                        .setMessage(R.string.deleteAlert)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            final DialogInterface dialog,
                                            final int whichButton) {

                                        deleteItem();
                                        if (ViewUtil.isTablet(getActivity())) {
                                            final ModuleDetailFragment fragment = (ModuleDetailFragment) getActivity()
                                                    .getSupportFragmentManager()
                                                    .findFragmentByTag(
                                                            "module_detail");
                                            getActivity()
                                                    .getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .remove(fragment).commit();
                                            final ModuleDetailFragment moduleDetailFragment = new ModuleDetailFragment();
                                            getActivity()
                                                    .getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .add(R.id.fragment_container_module_detail,
                                                            moduleDetailFragment,
                                                            "module_detail")
                                                    .commit();
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

            case R.string.info:
                return new AlertDialog.Builder(
                        ModuleListFragment.this.getActivity())
                        .setTitle(title)
                        .setMessage(R.string.syncProgressMsg)
                        .setIcon(R.drawable.applaunch)
                        .setPositiveButton(getString(android.R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            final DialogInterface dialog,
                                            final int which) {
                                    }
                                }).create();

            }
            return null;

        }
    }

    private class CustomCursorAdapter extends SimpleCursorAdapter {

        public CustomCursorAdapter(Context context, int layout, Cursor c,
                String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // get reference to the row
            final View view = super.getView(position, convertView, parent);
            // check for odd or even to set alternate colors to the row
            // background
            if (position % 2 == 0) {
                view.setBackgroundColor(getResources().getColor(
                        R.color.listview_row1));
            } else {
                view.setBackgroundColor(getResources().getColor(
                        R.color.listview_row2));
            }
            return view;
        }
    }

    private class ListItemView {
        private View view;

        public void setView(View view) {
            this.view = view;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public View getView() {
            return view;
        }

        public int getPosition() {
            return position;
        }

        private int position;

        public ListItemView(View view, int position) {
            this.view = view;
            this.position = position;
        }
    }
}