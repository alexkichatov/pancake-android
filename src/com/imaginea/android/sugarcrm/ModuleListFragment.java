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
 * FileName : ModuleListFragment 
 * Description :
 *              ModuleListFragment is used to lists the view projections for all the modules.
 ******************************************************************************/

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
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginea.android.sugarcrm.CustomActionbar.Action;
import com.imaginea.android.sugarcrm.CustomActionbar.IntentAction;
import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Recent;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.RecentColumns;
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
 * The Class ModuleListFragment.
 */
public class ModuleListFragment extends ListFragment implements
        LoaderCallbacks<Cursor> {

    /** The m list view. */
    private ListView mListView;

    /** The b search. */
    public boolean bSearch = false;

    /** The m module name. */
    private String mModuleName;

    /** The query done. */
    private boolean bQueryDone = false; // this flag is for search view and for
                                        // when query is applied don't reset the
                                        // loader
    /** The m module uri. */
    private Uri mModuleUri;

    /** The m cur filter. */
    String mCurFilter; // Search Filter

    /** The m intent uri. */
    private Uri mIntentUri;

    /** The m current selection. */
    private int mCurrentSelection;

    /** The m db helper. */
    private DatabaseHelper mDbHelper;

    /** The m adapter. */
    private CustomCursorAdapter mAdapter;

    /** The m module fields. */
    private String[] mModuleFields;

    /** The Searchrow id. */
    public static String mSearchrowId;

    private int mlistPosition = -1;

    /** The m module fields choice. */
    private String[] mModuleFieldsChoice;

    /** The map. */
    private Map<String, ModuleField> map;

    /** The field map. */
    private final Map<String, String> fieldMap = new HashMap<String, String>();

    /** The m sort column index. */
    private int mSortColumnIndex;

    /** The m selections. */
    private String mSelections = ModuleFields.DELETED + "=?";

    /** The m selection args. */
    private String[] mSelectionArgs = new String[] { Util.EXCLUDE_DELETED_ITEMS };

    /** The app. */
    private SugarCrmApp app;

    /** The b relation item. */
    private final boolean bRelationItem = true;

    /** The cursor. */
    private Cursor c;

    public final static String LOG_TAG = ModuleListFragment.class
            .getSimpleName();

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater
     * , android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {

        return inflater.inflate(R.layout.common_list, container, false);

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mlistPosition = -1;

        mDbHelper = new DatabaseHelper(getActivity().getBaseContext());
        app = (SugarCrmApp) getActivity().getApplication();
        final Intent intent = getActivity().getIntent();

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

                mAdapter.setSelectedPosition(position);
                addToRecent(position);
                openDetailScreen(position);
            }
        });

        // button code in the layout - 1.6 SDK feature to specify onClick
        mListView.setItemsCanFocus(true);
        mListView.setFocusable(true);
        mListView.setFastScrollEnabled(false);
        mListView.setScrollbarFadingEnabled(true);

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

        final View empty = getActivity().findViewById(R.id.empty);
        final TextView tv1 = (TextView) (empty.findViewById(R.id.mainText));
        tv1.setText("No " + mModuleName + " found");
        mListView.setEmptyView(empty);

        /* setting dynamic selector based on modules */
        final int listResourcesId[] = {
                ContentUtils.getModuleAlphaColor(mModuleName),
                ContentUtils.getModuleAlphaColor(mModuleName) };

        mListView.setSelector(Util.getListColorState(getActivity()
                .getBaseContext(), listResourcesId));

        // get the module fields for the module
        map = ContentUtils.getModuleFields(getActivity(), mModuleName);
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

        /* open details screen with 1st row highlighted */

        c = getActivity().managedQuery(ContentUtils.getModuleUri(mModuleName),
                ContentUtils.getModuleProjections(mModuleName), mSelections,
                mSelectionArgs, getSortOrder());
        final Intent detailIntent = new Intent(getActivity(),
                ModuleDetailActivity.class);
        detailIntent.putExtra(RestConstants.MODULE_NAME, mModuleName);

        /* open details screen with 1st row highlighted */
        Util.OpenDetailScreenWithSelectedRow(getActivity(), c, detailIntent,
                false);

        getLoaderManager().initLoader(0, null, this);

    }

    /**
     * Reload list data.
     * 
     * @param moduleName
     *            the module name
     */
    public void reloadListData(String moduleName) {
        mModuleName = moduleName;

        mListView = getListView();
        mlistPosition = -1;
        final View empty = getActivity().findViewById(R.id.empty);
        final TextView tv1 = (TextView) (empty.findViewById(R.id.mainText));
        tv1.setText("No " + mModuleName + " found");
        mListView.setEmptyView(empty);

        mModuleUri = ContentUtils.getModuleUri(mModuleName);

        mIntentUri = mModuleUri;

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
        map = ContentUtils.getModuleFields(getActivity(), mModuleName);
        if (map == null) {
            Log.w(LOG_TAG, "Cannot prepare Options as Map is null for module:"
                    + mModuleName);

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
        mSelections = null;
        mSelectionArgs = null;
        getLoaderManager().restartLoader(0, null, this);

        c = getActivity().managedQuery(mIntentUri,
                ContentUtils.getModuleProjections(mModuleName), mSelections,
                mSelectionArgs, getSortOrder());
        final Intent detailIntent = new Intent(getActivity(),
                ModuleDetailActivity.class);
        detailIntent.putExtra(RestConstants.MODULE_NAME, mModuleName);

        /* open details screen with 1st row highlighted */
        Util.OpenDetailScreenWithSelectedRow(getActivity(), c, detailIntent,
                false);

    }

    /**
     * Sets the up action bar.
     */
    public void setUpActionBar() {

        /* Get the Custom Bar Layout */
        final CustomActionbar actionBar = (CustomActionbar) getActivity()
                .findViewById(R.id.custom_actionbar);
        final Intent myIntent;
        /*
         * If Tablet then only we need to launch Recent Activity else it is just
         * fragmnet loading taken care in Activity
         */
        if (ViewUtil.isHoneycombTablet(getActivity())) {
            myIntent = new Intent(getActivity(),
                    RecentModuleMultiPaneActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            myIntent.putExtra(RestConstants.MODULE_NAME, Util.RECENT);

            final Action recentAction = new IntentAction(
                    ModuleListFragment.this.getActivity(), myIntent);

            actionBar.setHomeAction(recentAction);
        }

        actionBar.setTitle(mModuleName);

        /* Set the Menu Layout */
        setupMenuOption();

        /* Start Sync when clicked on Sync Image */
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
                SyncAction(v);

            }
        });

        /* Start Search when clicked on Search Image */
        final ImageView searchView = (ImageView) getActivity().findViewById(
                R.id.search);

        final int searchResourcesId[];
        if (ViewUtil.isHoneycombTablet(getActivity())) {
            final int tabletSearchResourcesId[] = {
                    R.drawable.ico_actionbar_search_pressed,
                    R.drawable.ico_actionbar_search_pressed, R.drawable.search };
            searchResourcesId = tabletSearchResourcesId;
        } else {
            final int phoneSettinsResourcesId[] = {
                    R.drawable.ico_m_actionbar_search_pressed,
                    R.drawable.ico_m_actionbar_search_pressed,
                    R.drawable.ico_m_actionbar_search_nor };
            searchResourcesId = phoneSettinsResourcesId;
        }
        searchView.setImageDrawable(Util.getPressedImage(getActivity()
                .getBaseContext(), searchResourcesId));
        searchView.setVisibility(View.VISIBLE);
        searchView.requestFocus();
        searchView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bSearch = true; // This Flag is used to check if Search is not
                                // Enable then do not Handle BAck key
                setupSearchView();

            }
        });

        /* Set up Spinner for sorting based on Device and Orientation */
        setupSortingOption();
        /*
         * Set the AddNew Item in Action BAr Visibility to True or dalse based
         * on orientation
         */
        final View separator2 = getActivity().findViewById(
                R.id.actionbar_homeseperator2);
        if (ViewUtil.isHoneycombTablet(getActivity())) {
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                onLandscapeChange();
                separator2.setVisibility(View.VISIBLE);
            } else {
                onPortraitChange();
                separator2.setVisibility(View.GONE);
            }

        }

    }

    /**
     * Setup sorting option.
     */
    private void setupSortingOption() {

        /* Dynamically inflate menu items for the "sort by" fields */
        final LinearLayout orderByParent = (LinearLayout) getActivity()
                .findViewById(R.id.orderByListContainer);

        orderByParent.removeAllViews();
        final TextView sortby = new TextView(getActivity());
        sortby.setText("Sort By");
        sortby.setTextColor(getResources().getColor(R.color.sortby_text_color));
        sortby.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        sortby.setBackgroundColor(getResources().getColor(
                R.color.menu_background));
        int x = (int) (15 * getResources().getDisplayMetrics().density);
        int y = (int) (10 * getResources().getDisplayMetrics().density);
        sortby.setPadding(x, 5, x, 5);

        orderByParent.addView(sortby);
        int i = 0;
        for (final String fieldName : mModuleFieldsChoice) {
            final TextView tv = new TextView(getActivity());
            tv.setText(fieldName);
            tv.setTag(mModuleFields[i]);
            tv.setTextAppearance(getActivity(),
                    android.R.style.TextAppearance_Small);
            tv.setBackgroundResource(R.drawable.selector);
            tv.setTextColor(getResources().getColorStateList(R.color.textcolor));
            // android.R.style.TextAppearance_Medium
            tv.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            x = (int) (20 * getResources().getDisplayMetrics().density);
            y = (int) (5 * getResources().getDisplayMetrics().density);
            tv.setPadding(x, y, x, y);
            tv.setOnClickListener(new View.OnClickListener() {
                final LinearLayout menuLayout = (LinearLayout) getActivity()
                        .findViewById(R.id.settings_menu);
                final View transparentView = getActivity().findViewById(
                        R.id.transparent_view);

                @Override
                public void onClick(View v) {
                    final String sortOrder = tv.getTag().toString() + " ASC";
                    app.setModuleSortOrder(mModuleName,
                            fieldMap.get(tv.getText().toString()), "ASC");
                    Log.e(LOG_TAG, "SORT :" + sortOrder);
                    Log.e(LOG_TAG, "field choice: " + tv.getText().toString());
                    sortList(sortOrder);
                    menuLayout.setVisibility(View.GONE);
                    transparentView.setVisibility(View.GONE);
                }
            });
            i++;
            orderByParent.addView(tv);
        }

    }

    /**
     * Setup menu option.
     */
    private void setupMenuOption() {
        final LinearLayout menuLayout = (LinearLayout) getActivity()
                .findViewById(R.id.settings_menu);
        final View transparentView = getActivity().findViewById(
                R.id.transparent_view);
        transparentView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                menuLayout.setVisibility(View.GONE);
                transparentView.setVisibility(View.GONE);
            }
        });
        final ImageView settingsView = (ImageView) getActivity().findViewById(
                R.id.settings);
        settingsView.setVisibility(View.VISIBLE);
        final int settinsResourcesId[];
        if (ViewUtil.isHoneycombTablet(getActivity())) {
            final int tabletSettinsResourcesId[] = {
                    R.drawable.ico_actionbar_menu_pressed,
                    R.drawable.ico_actionbar_menu_pressed, R.drawable.settings };
            settinsResourcesId = tabletSettinsResourcesId;
        } else {
            final int phoneSettinsResourcesId[] = {
                    R.drawable.ico_m_actionbar_menu_pressed,
                    R.drawable.ico_m_actionbar_menu_pressed,
                    R.drawable.ico_m_actionbar_menu_nor };
            settinsResourcesId = phoneSettinsResourcesId;
        }
        settingsView.setImageDrawable(Util.getPressedImage(getActivity()
                .getBaseContext(), settinsResourcesId));
        settingsView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!(menuLayout.getVisibility() == View.VISIBLE)) {
                    menuLayout.bringToFront();
                    menuLayout.setVisibility(View.VISIBLE);
                    transparentView.setVisibility(View.VISIBLE);
                } else {
                    menuLayout.setVisibility(View.INVISIBLE);
                    transparentView.setVisibility(View.GONE);
                }

            }
        });

    }

    /**
     * On landscape change.
     */
    public void onLandscapeChange() {
        final CustomActionbar actionBar = (CustomActionbar) getActivity()
                .findViewById(R.id.custom_actionbar);
        final LinearLayout menuLayout = (LinearLayout) getActivity()
                .findViewById(R.id.settings_menu);
        final TextView addNewMenuItem = (TextView) menuLayout
                .findViewById(R.id.addNewMenuItem);
        final LinearLayout addNewView = (LinearLayout) getActivity()
                .findViewById(R.id.addNew);
        addNewView.setVisibility(View.VISIBLE);
        /* Add the Pressed State to Add Image */
        final int addNewResourcesId[] = { R.drawable.ico_actionbar_add_pressed,
                R.drawable.ico_actionbar_add_pressed, R.drawable.add };

        final ImageView addNewImage = (ImageView) actionBar
                .findViewById(R.id.add);

        addNewImage.setImageDrawable(Util.getPressedImage(getActivity()
                .getBaseContext(), addNewResourcesId));

        addNewMenuItem.setVisibility(View.GONE);
        /* Set Action to Add Button */
        final Action addAction = new IntentAction(getActivity(), AddAction());
        actionBar.setAddAction(addAction);

    }

    public void onPortraitChange() {
        final LinearLayout menuLayout = (LinearLayout) getActivity()
                .findViewById(R.id.settings_menu);
        final TextView addNewMenuItem = (TextView) menuLayout
                .findViewById(R.id.addNewMenuItem);
        final LinearLayout addNewView = (LinearLayout) getActivity()
                .findViewById(R.id.addNew);
        addNewView.setVisibility(View.GONE);
        addNewMenuItem.setVisibility(View.VISIBLE);
        addNewMenuItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Intent myIntent = new Intent(ModuleListFragment.this
                        .getActivity(), EditModuleDetailActivity.class);
                myIntent.putExtra(RestConstants.MODULE_NAME, mModuleName);
                startActivity(myIntent);
                menuLayout.setVisibility(View.INVISIBLE);
                /* Hiding the Transparent view when click on Menu Tap */
                final View transParentView = getActivity().findViewById(
                        R.id.transparent_view);
                transParentView.setVisibility(View.GONE);
            }
        });
    }

    /*
     * Screen
     * 
     * @param position the position
     */
    void openDetailScreen(final int position) {

        final Cursor cursor = (Cursor) getListAdapter().getItem(position);
        Log.w(LOG_TAG, "openDetailScreen, Cursor is not null ");
        if (cursor == null)
            // For some reason the requested item isn't available, do nothing
            return;
        final Intent detailIntent = new Intent(getActivity(),
                ModuleDetailActivity.class);
        detailIntent.putExtra(Util.ROW_ID, cursor.getString(0));
        detailIntent.putExtra(RestConstants.BEAN_ID, cursor.getString(1));
        detailIntent.putExtra(RestConstants.MODULE_NAME, mModuleName);
        detailIntent.putExtra("Relation", bRelationItem);
        if (ViewUtil.isTablet(getActivity())) {
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

    /**
     * An asynchronous update interface for receiving notifications about
     * DeleteContent information as the DeleteContent is constructed.
     */
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

    public void setupSearchActionBar() {
        final CustomActionbar actionBar = (CustomActionbar) getActivity()
                .findViewById(R.id.custom_actionbar);

        final SearchView searchView = (SearchView) actionBar
                .findViewById(R.id.searchView);
        final ImageView backView = (ImageView) actionBar
                .findViewById(R.id.actionbar_back);
        final ImageView logoview = (ImageView) actionBar
                .findViewById(R.id.actionbar_logo);

        if (!ViewUtil.isHoneycombTablet(getActivity())) {
            final TextView modulenameView = (TextView) actionBar
                    .findViewById(R.id.actionbar_moduleName);
            modulenameView.setVisibility(View.GONE);
        }

        final LinearLayout logo = (LinearLayout) actionBar
                .findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchView.setVisibility(View.GONE);
                backView.setVisibility(View.GONE);
                logoview.setVisibility(View.VISIBLE);
                searchView.setQuery("", false);

                setUpActionBar();

            }
        });
        if (ViewUtil.isHoneycombTablet(getActivity())) {
            final Spinner orderBySpinner = (Spinner) actionBar
                    .findViewById(R.id.orderBySpinner);
            final LinearLayout addnew = (LinearLayout) actionBar
                    .findViewById(R.id.addNew);
            orderBySpinner.setVisibility(View.GONE);
            addnew.setVisibility(View.GONE);

        }
        final ImageView search = (ImageView) actionBar
                .findViewById(R.id.search);
        search.setVisibility(View.GONE);
        final ImageView sync = (ImageView) actionBar.findViewById(R.id.sync);
        sync.setVisibility(View.GONE);

    }

    /**
     * Setup search view.
     */
    private void setupSearchView() {

        final CustomActionbar actionBar = (CustomActionbar) getActivity()
                .findViewById(R.id.custom_actionbar);

        setupSearchActionBar();
        bQueryDone = false;
        final SearchView searchView = (SearchView) actionBar
                .findViewById(R.id.searchView);
        final ImageView logoview = (ImageView) actionBar
                .findViewById(R.id.actionbar_logo);
        final ImageView backView = (ImageView) actionBar
                .findViewById(R.id.actionbar_back);
        searchView.requestFocus();

        searchView.setVisibility(View.VISIBLE);
        logoview.setVisibility(View.GONE);
        backView.setVisibility(View.VISIBLE);
        searchView.setQueryHint("Search Records");
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("ee", "onQueryTextChange.....");
                // Called when the action bar search text has changed. Update
                // the search filter, and restart the loader to do a new query
                // with this filter.
                final String newFilter = !TextUtils.isEmpty(newText) ? newText
                        : null;

                // Don't do anything if the filter hasn't actually changed.
                // Prevents restarting the loader when restoring state.

                if (mCurFilter != null && mCurFilter.equals(newFilter))
                    return true;
                mCurFilter = newFilter;

                if (mCurFilter == null && newFilter == null
                        & bQueryDone == false) {
                    Log.i("ee", "onQueryTextChange. both filters are null....");
                    mSelections = null;
                    mSelectionArgs = null;

                    getLoaderManager().restartLoader(0, null,
                            ModuleListFragment.this);
                    openDetailViewOnSearch(null);
                    return true;
                }
                Log.i("ee", "onQueryTextChange... newFilter.." + newFilter
                        + "mCurFilter ===" + mCurFilter);

                mSelections = null;
                mSelectionArgs = null;
                if (mCurFilter != null) {
                    getLoaderManager().restartLoader(0, null,
                            ModuleListFragment.this);
                    openDetailViewOnSearch(mCurFilter);

                }
                return true;
            }

            /*
             * (non-Javadoc)
             * 
             * @see
             * android.widget.SearchView.OnQueryTextListener#onQueryTextSubmit
             * (java.lang.String)
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(LOG_TAG, "onQueryTextSubmit....." + query);
                getLoaderManager().restartLoader(0, null,
                        ModuleListFragment.this);
                openDetailViewOnSearch(query);
                bQueryDone = true;
                searchView.setVisibility(View.GONE);
                searchView.setQuery("", false);
                searchView.setIconified(true);
                return true;
            }

        });

    }

    /**
     * Open detail view on search.
     * 
     * @param query
     *            the query
     */
    public void openDetailViewOnSearch(String query) {
        final Cursor cursor;
        cursor = getActivity().managedQuery(
                ContentUtils.getModuleUri(mModuleName),
                ContentUtils.getModuleProjections(mModuleName),
                mDbHelper.getModuleSelection(mModuleName, query), null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                mSearchrowId = cursor.getString(0);
            } else {
                mSearchrowId = "1";
            }
        }
        final Intent detailIntent = new Intent(getActivity(),
                ModuleDetailActivity.class);
        detailIntent.putExtra(Util.ROW_ID, mSearchrowId);
        detailIntent.putExtra(RestConstants.MODULE_NAME, mModuleName);
        if (ViewUtil.isTablet(getActivity())) {
            ((BaseMultiPaneActivity) getActivity())
                    .openActivityOrFragment(detailIntent);

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.Fragment#onCreateContextMenu(android.view.ContextMenu
     * , android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.Fragment#onContextItemSelected(android.view.MenuItem
     * )
     */
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

    /**
     * Sort list.
     * 
     * @param sortOrder
     *            the sort order
     */
    private void sortList(final String sortOrder) {

        mSelections = null;
        mSelectionArgs = null;
        getLoaderManager().restartLoader(0, null, this);
        if (ViewUtil.isTablet(getActivity())) {
            c = getActivity().managedQuery(mIntentUri,
                    ContentUtils.getModuleProjections(mModuleName),
                    mSelections, mSelectionArgs, getSortOrder());
            final Intent detailIntent = new Intent(getActivity(),
                    ModuleDetailActivity.class);
            detailIntent.putExtra(RestConstants.MODULE_NAME, mModuleName);

            /* open details screen with 1st row highlighted */
            Util.OpenDetailScreenWithSelectedRow(getActivity(), c,
                    detailIntent, false);

        }
    }

    void addToRecent(final int position) {
        final ContentValues modifiedValues = new ContentValues();
        // push the selected record into recent table
        final Cursor cursor = (Cursor) getListAdapter().getItem(position);
        // now insert into recent table
        modifiedValues.put(RecentColumns.ACTUAL_ID, cursor.getInt(0) + "");
        modifiedValues.put(RecentColumns.BEAN_ID, cursor.getString(1));
        modifiedValues.put(RecentColumns.NAME_1, cursor.getString(2));
        modifiedValues.put(RecentColumns.NAME_2, cursor.getString(3));
        modifiedValues.put(RecentColumns.REF_MODULE_NAME, mModuleName);
        modifiedValues.put(RecentColumns.DELETED, "0");
        final Uri insertResultUri = getActivity().getApplicationContext()
                .getContentResolver()
                .insert(Recent.CONTENT_URI, modifiedValues);
        Log.i(LOG_TAG, "insertResultURi - " + insertResultUri);

    }

    /**
     * Gets the sort order.
     * 
     * @return the sort order
     */
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
     * Call number.
     * 
     * @param position
     *            the position
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
     * Send mail.
     * 
     * @param position
     *            the position
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

    /**
     * Adds the action.
     * 
     * @return the intent
     */
    private Intent AddAction() {
        final Intent myIntent = new Intent(getActivity(),
                EditModuleDetailActivity.class);
        myIntent.putExtra(RestConstants.MODULE_NAME, mModuleName);
        return myIntent;

    }

    /**
     * Sync action.
     * 
     * @param view
     *            the view
     */
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
            Util.startModuleSync(getActivity(), mModuleName);

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
     * android.os.Bundle)
     */
    @Override
    public Loader<Cursor> onCreateLoader(final int arg0, final Bundle arg1) {
        if (mCurFilter != null) {
            Log.i(LOG_TAG, "onCreate Loader query filter not null");
            mSelections = mDbHelper.getModuleSelection(mModuleName, mCurFilter);

            mSelectionArgs = null;
        }

        return new CursorLoader(getActivity(),
                ContentUtils.getModuleUri(mModuleName),
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

    /**
     * The Class ModuleListAlertDialogFragment.
     */
    public class ModuleListAlertDialogFragment extends DialogFragment {

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

    /**
     * The Class CustomCursorAdapter.
     */
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
            final TextView text1 = (TextView) view.findViewById(R.id.text1);
            final TextView text2 = (TextView) view.findViewById(R.id.text2);
            final TextView text3 = (TextView) view.findViewById(R.id.text3);
            final TextView lastNameView = (TextView) view
                    .findViewById(R.id.textLastName);
            final TextView text5 = (TextView) view.findViewById(R.id.text5);
            text5.setVisibility(View.GONE);
            if (text2.getText().toString().isEmpty()) {
                final ModuleField modField = map.get(mModuleFields[1]);
                text2.setText(modField.getLabel() + " Not Assigned");

            }

            if (text3.getText().toString().isEmpty()) {
                final ModuleField modField = map.get(mModuleFields[2]);
                text3.setText(modField.getLabel() + " Not Assigned");

            }
            if (position % 2 == 0) {
                view.setBackgroundColor(getResources().getColor(
                        R.color.listview_row1));
            } else {
                view.setBackgroundColor(getResources().getColor(
                        R.color.listview_row2));
            }
            if (position == mlistPosition) {
                view.setBackgroundColor(getResources().getColor(
                        ContentUtils.getModuleColor(mModuleName)));
                text1.setTextColor(getResources().getColor(
                        android.R.color.white));
                text2.setTextColor(getResources().getColor(
                        android.R.color.white));
                text3.setTextColor(getResources().getColor(
                        android.R.color.white));
                if (mModuleName.equals(Util.CONTACTS)
                        || mModuleName.equals(Util.LEADS)) {
                    lastNameView.setTextColor(getResources().getColor(
                            android.R.color.white));
                }

            } else {
                text1.setTextColor(getResources().getColor(
                        R.color.dshboardlist_text_color));
                text2.setTextColor(getResources().getColor(
                        R.color.dshboardlist_text_color));
                text3.setTextColor(getResources().getColor(
                        R.color.dshboardlist_text_color));
                if (mModuleName.equals(Util.CONTACTS)
                        || mModuleName.equals(Util.LEADS)) {
                    lastNameView.setTextColor(getResources().getColor(
                            R.color.dshboardlist_text_color));
                }
            }
            return view;
        }

        public void setSelectedPosition(int pos) {
            mlistPosition = pos;
            if (mlistPosition != -1) {
                notifyDataSetChanged();
            }
        }

    }

}
