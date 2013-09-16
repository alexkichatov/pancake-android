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
 * FileName : RecentModuleListFragment 
 *  Description :
 *              RecentModuleListFragment lists the view projections for all the Recently accessed
 * records.
 ******************************************************************************/

package com.imaginea.android.sugarcrm;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.ui.BaseMultiPaneActivity;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

/**
 * The Class RecentModuleListFragment.
 */
public class RecentModuleListFragment extends ListFragment {

    /** The list position. */
    private int mlistPosition = -1;

    /** The m module name. */
    private String mModuleName;

    /** The m adapter. */
    private GenericCursorAdapter mAdapter;

    /** The m selections. */
    private final String mSelections = ModuleFields.DELETED + "=?";

    /** The m selection args. */
    private final String[] mSelectionArgs = new String[] { Util.EXCLUDE_DELETED_ITEMS };

    /** The app. */
    private SugarCrmApp app;

    /** The Constant LOG_TAG. */
    public static final String LOG_TAG = "RecentModuleList";

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

    /** {@inheritDoc} */
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /** The module uri. */
        Uri mModuleUri;
        View mEmpty;
        /** The intent uri. */
        Uri mIntentUri;
        /** The list view. */
        ListView mListView;

        app = (SugarCrmApp) getActivity().getApplication();
        final Intent intent = getActivity().getIntent();
        // final Intent intent =
        final Bundle extras = intent.getExtras();

        mModuleName = Util.CONTACTS;
        if (extras != null) {
            mModuleName = extras.getString(RestConstants.MODULE_NAME);
        }

        setUpActionBar();

        mListView = getListView();

        setListView(mListView);

        mIntentUri = intent.getData();

        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, "ModuleName:-->" + mModuleName);
        }

        mModuleUri = ContentUtils.getModuleUri(mModuleName);
        if (mIntentUri == null) {
            intent.setData(mModuleUri);
            mIntentUri = mModuleUri;
        }
        // Perform a managed query. The Activity will handle closing and
        // re querying the cursor
        // when needed.

        final Cursor cursor = getActivity().managedQuery(intent.getData(),
                ContentUtils.getModuleProjections(mModuleName), mSelections,
                mSelectionArgs, getSortOrder());

        int[] ids = new int[] { R.id.text1, R.id.text2, R.id.text3, R.id.text5 };

        if (mModuleName.equals(Util.CONTACTS) || mModuleName.equals(Util.LEADS)) {
            ids = new int[] { R.id.text1, R.id.textLastName, R.id.text2,
                    R.id.text3, R.id.text5 };
        } else if (mModuleName.equals(Util.RECENT)) {
            ids = new int[] { R.id.text1, R.id.text2, R.id.text5 };
        }
        final String[] moduleSel = ContentUtils
                .getModuleListSelections(mModuleName);
        if (moduleSel.length >= 2) {
            mAdapter = new GenericCursorAdapter(getActivity(),
                    R.layout.contact_listitem, cursor, moduleSel, ids);
        } else {
            mAdapter = new GenericCursorAdapter(getActivity(),
                    R.layout.contact_listitem, cursor, moduleSel,
                    new int[] { android.R.id.text1 });
        }
        setListAdapter(mAdapter);

        /* setting dynamic selector based on modules */
        final int listResourcesId[] = {
                ContentUtils.getModuleAlphaColor(mModuleName),
                ContentUtils.getModuleAlphaColor(mModuleName) };

        mListView.setSelector(Util.getListColorState(getActivity()
                .getBaseContext(), listResourcesId));
        // make the list filterable using the keyboard
        mListView.setTextFilterEnabled(true);
        mEmpty = getActivity().findViewById(R.id.empty);
        final TextView tv1 = (TextView) (mEmpty.findViewById(R.id.mainText));

        if (mAdapter.getCount() == 0) {
            mListView.setVisibility(View.GONE);
            mEmpty.findViewById(R.id.progress).setVisibility(View.INVISIBLE);
            tv1.setVisibility(View.VISIBLE);
            if (mIntentUri != null) {
                tv1.setText("No " + mModuleName + " found");
            }
        } else {
            mEmpty.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            tv1.setVisibility(View.GONE);
        }

        /* open details screen with 1st row highlighted */

        final Intent detailIntent = new Intent(getActivity(),
                ModuleDetailActivity.class);
        Util.openDetailScreenWithSelectedRow(getActivity(), cursor,
                detailIntent, true);

    }

    private void setListView(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> arg0, final View view,
                    final int position, final long id) {

                mAdapter.setSelectedPosition(position);
                openDetailScreen(position);
            }
        });

        // button code in the layout - 1.6 SDK feature to specify onClick
        listView.setItemsCanFocus(true);
        listView.setFocusable(true);
        final View mEmpty = getActivity().findViewById(R.id.empty);
        listView.setEmptyView(mEmpty);
        listView.setFastScrollEnabled(false);
        listView.setScrollbarFadingEnabled(true);

    }

    /**
     * Sets the up action bar.
     */
    private void setUpActionBar() {

        final CustomActionbar actionBar = (CustomActionbar) getActivity()
                .findViewById(R.id.custom_actionbar);

        actionBar.setTitle(mModuleName);
        final LinearLayout menuLayout = (LinearLayout) getActivity()
                .findViewById(R.id.settings_menu);

        final ImageView search = (ImageView) actionBar
                .findViewById(R.id.search);
        search.setVisibility(View.GONE);

        final View transparentView = getActivity().findViewById(
                R.id.transparent_view);
        if (transparentView != null) {
            transparentView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    menuLayout.setVisibility(View.GONE);
                    transparentView.setVisibility(View.GONE);
                }
            });
        }
        final LinearLayout logo = (LinearLayout) actionBar
                .findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                return;

            }
        });

        /* Set the Settings View */
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
                }
            }
        });

        /* Set the Sync View */
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
     * GenericCursorAdapter.
     */
    private final class GenericCursorAdapter extends SimpleCursorAdapter
            implements Filterable {

        /**
         * Instantiates a new generic cursor adapter.
         * 
         * @param context
         *            the context
         * @param layout
         *            the layout
         * @param c
         *            the c
         * @param from
         *            the from
         * @param to
         *            the to
         */
        public GenericCursorAdapter(final Context context, final int layout,
                final Cursor c, final String[] from, final int[] to) {
            super(context, layout, c, from, to);

        }

        /**
         * Sets the selected position.
         * 
         * @param position
         *            the new selected position
         */
        public void setSelectedPosition(int position) {
            mlistPosition = position;
            if (mlistPosition != -1) {
                notifyDataSetChanged();
            }

        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.CursorAdapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(final int position, final View convertView,
                final ViewGroup parent) {

            final View v = super.getView(position, convertView, parent);
            final TextView text1 = (TextView) v.findViewById(R.id.text1);
            final TextView text2 = (TextView) v.findViewById(R.id.text2);
            v.findViewById(R.id.textLastName);

            final TextView text3 = (TextView) v.findViewById(R.id.text3);
            text3.setVisibility(View.GONE);

            final TextView moduleTextView = (TextView) v
                    .findViewById(R.id.text5);
            final String moduleName = moduleTextView.getText().toString();
            moduleTextView.setBackgroundColor(getResources().getColor(
                    ContentUtils.getRecentModulelablesColors(moduleName)));

            if (moduleName.equals(Util.CONTACTS)
                    || moduleName.equals(Util.LEADS)) {
                final String firstName = text1.getText().toString();
                text1.setText(firstName + " " + text2.getText());
                text2.setText(" ");

            }

            if (position % 2 == 0) {
                v.setBackgroundColor(getResources().getColor(
                        R.color.listview_row1));
                moduleTextView.setTextColor(getResources().getColor(
                        R.color.listview_row1));

            } else {
                v.setBackgroundColor(getResources().getColor(
                        R.color.listview_row2));
                moduleTextView.setTextColor(getResources().getColor(
                        R.color.listview_row1));

            }

            if (position == mlistPosition) {
                v.setBackgroundColor(getResources().getColor(
                        R.color.dashboard_list_color));
                text1.setTextColor(getResources().getColor(
                        android.R.color.white));
                text2.setTextColor(getResources().getColor(
                        android.R.color.white));

            } else {
                text1.setTextColor(getResources().getColor(
                        android.R.color.black));
                text2.setTextColor(getResources().getColor(
                        android.R.color.black));

            }
            return v;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.widget.SimpleCursorAdapter#convertToString(android.database
         * .Cursor)
         */
        @Override
        public String convertToString(final Cursor cursor) {
            Log.i(LOG_TAG, "convertToString : " + cursor.getString(2));
            return cursor.getString(2);
        }

    }

    /**
     * opens the Detail Screen.
     * 
     * @param position
     *            the position
     */
    void openDetailScreen(final int position) {
        final Intent detailIntent = new Intent(getActivity(),
                ModuleDetailActivity.class);

        final Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }
        // use the details available from cursor to open detailed view
        detailIntent.putExtra(Util.ROW_ID, cursor.getString(1));
        detailIntent.putExtra(RestConstants.BEAN_ID, cursor.getString(2));
        detailIntent.putExtra(RestConstants.MODULE_NAME, cursor.getString(3));
        detailIntent.putExtra("Recent", true);

        if (ViewUtil.isTablet(getActivity())) {
            ((BaseMultiPaneActivity) getActivity())
                    .openActivityOrFragment(detailIntent);
        } else {
            startActivity(detailIntent);
        }

    }

    /**
     * Show assigned items.
     * 
     * @param view
     *            the view
     */
    public void showAssignedItems(final View view) {
        // keep this empty as the header is used from list view
    }

    /**
     * <p>
     * showAllItems
     * </p>
     * .
     * 
     * @param view
     *            a {@link android.view.View} object.
     */
    public void showAllItems(final View view) {
        final Cursor cursor = getActivity().managedQuery(
                getActivity().getIntent().getData(),
                ContentUtils.getModuleProjections(mModuleName), null, null,
                getSortOrder());
        mAdapter.changeCursor(cursor);
        mAdapter.notifyDataSetChanged();
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

}
