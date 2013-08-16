package com.imaginea.android.sugarcrm;

import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.CustomActionbar.Action;
import com.imaginea.android.sugarcrm.CustomActionbar.IntentAction;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Contacts;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.ui.BaseMultiPaneActivity;
import com.imaginea.android.sugarcrm.ui.RecentModuleMultiPaneActivity;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

/**
 * RecentListActivity, lists the view projections for all the Recently accessed
 * records.
 * 
 * 
 * @author Jagadeeshwaran K
 */
public class RecentModuleListFragment extends ListFragment {

    private ListView mListView;

    private View mEmpty;

    private View mListFooterView;

    private TextView mListFooterText;

    private View mListFooterProgress;

    private boolean mBusy = false;

    private String mModuleName;

    private Uri mModuleUri;

    private Uri mIntentUri;

    // we don't make this final as we may want to use the sugarCRM value
    // dynamically, but prevent
    // others from modiying anyway
    // private static int mMaxResults = 20;

    private GenericCursorAdapter mAdapter;

    private final String mSelections = ModuleFields.DELETED + "=?";

    private final String[] mSelectionArgs = new String[] { Util.EXCLUDE_DELETED_ITEMS };

    private SugarCrmApp app;

    public final static String LOG_TAG = "RecentModuleList";

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {

        return inflater.inflate(R.layout.common_list, container, false);

    }

    /** {@inheritDoc} */
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        app = (SugarCrmApp) getActivity().getApplication();
        final Intent intent = getActivity().getIntent();
        // final Intent intent =
        // BaseActivity.fragmentArgumentsToIntent(getArguments());
        final Bundle extras = intent.getExtras();

        mModuleName = Util.CONTACTS;
        if (extras != null) {
            mModuleName = extras.getString(RestConstants.MODULE_NAME);
        }

        setUpActionBar();

        mListView = getListView();

        mIntentUri = intent.getData();
        // mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> arg0, final View view,
                    final int position, final long id) {

                openDetailScreen(position);
            }
        });

        // button code in the layout - 1.6 SDK feature to specify onClick
        mListView.setItemsCanFocus(true);
        mListView.setFocusable(true);
        mEmpty = getActivity().findViewById(R.id.empty);
        mListView.setEmptyView(mEmpty);
        mListView.setFastScrollEnabled(false);
        mListView.setScrollbarFadingEnabled(true);
        // registerForContextMenu(getListView());

        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, "ModuleName:-->" + mModuleName);
        }

        mModuleUri = ContentUtils.getModuleUri(mModuleName);
        if (mIntentUri == null) {
            intent.setData(mModuleUri);
            mIntentUri = mModuleUri;
        }
        // Perform a managed query. The Activity will handle closing and
        // requerying the cursor
        // when needed.
        // TODO - optimize this, if we sync up a dataset, then no need to run
        // detail projection
        // here, just do a list projection
        final Cursor cursor = getActivity().managedQuery(intent.getData(),
                ContentUtils.getModuleProjections(mModuleName), mSelections,
                mSelectionArgs, getSortOrder());

        // CRMContentObserver observer = new CRMContentObserver()
        // cursor.registerContentObserver(observer);
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
        // make the list filterable using the keyboard
        mListView.setTextFilterEnabled(true);

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

        mListFooterView = ((LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.list_item_footer, mListView, false);
        getListView().addFooterView(mListFooterView);
        mListFooterText = (TextView) getActivity().findViewById(R.id.status);

        mListFooterProgress = mListFooterView.findViewById(R.id.progress);
        if (ViewUtil.isHoneycombTablet(getActivity())
                && mAdapter.getCount() != 0) {
            openDetailScreen(0);
        }

    }

    private void setUpActionBar() {

        final CustomActionbar actionBar = (CustomActionbar) getActivity()
                .findViewById(R.id.custom_actionbar);

        final Intent myIntent = new Intent(getActivity(),
                RecentModuleMultiPaneActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.putExtra(RestConstants.MODULE_NAME, Util.RECENT);

        final Action recentAction = new IntentAction(getActivity(), myIntent);

        actionBar.setHomeAction(recentAction);
        actionBar.setTitle(mModuleName);

        final ImageView settingsView = (ImageView) getActivity().findViewById(
                R.id.settings);
        settingsView.setVisibility(View.VISIBLE);
        /* Set Action to Settings Button */
        final Action settingsAction = new IntentAction(
                RecentModuleListFragment.this.getActivity(),
                createSettingsIntent());
        actionBar.setSettingAction(settingsAction);

        final ImageView syncView = (ImageView) getActivity().findViewById(
                R.id.sync);
        syncView.setVisibility(View.VISIBLE);
        syncView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO

            }
        });

    }

    private Intent createSettingsIntent() {
        final Intent myIntent = new Intent(
                RecentModuleListFragment.this.getActivity(),
                SugarCrmSettings.class);
        myIntent.putExtra(RestConstants.MODULE_NAME, "settings");
        return myIntent;
    }

    /**
     * GenericCursorAdapter
     */
    private final class GenericCursorAdapter extends SimpleCursorAdapter
            implements Filterable {

        private int realoffset = 0;

        private final int limit = 20;

        private final ContentResolver mContent;

        public GenericCursorAdapter(final Context context, final int layout,
                final Cursor c, final String[] from, final int[] to) {
            super(context, layout, c, from, to);
            mContent = context.getContentResolver();
        }

        @Override
        public View getView(final int position, final View convertView,
                final ViewGroup parent) {

            final View v = super.getView(position, convertView, parent);
            final int count = getCursor().getCount();

            Log.d(LOG_TAG, "Get Item" + getItemId(position));
            if (!mBusy && position != 0 && position == count - 1) {
                mBusy = true;
                realoffset += count;
                // Uri uri = getIntent().getData();
                // TODO - fix this, this is no longer used
                final Uri newUri = Uri.withAppendedPath(Contacts.CONTENT_URI,
                        realoffset + "/" + limit);
                Log.d(LOG_TAG, "Changing cursor:" + newUri.toString());
                final Cursor cursor = getActivity().managedQuery(newUri,
                        Contacts.LIST_PROJECTION, null, null,
                        Contacts.DEFAULT_SORT_ORDER);
                final CRMContentObserver observer = new CRMContentObserver(
                        new Handler() {

                            @Override
                            public void handleMessage(final Message msg) {
                                super.handleMessage(msg);
                                Log.d(LOG_TAG, "Changing cursor: in handler");
                                changeCursor(cursor);
                                mListFooterText.setVisibility(View.GONE);
                                mListFooterProgress.setVisibility(View.GONE);
                                mBusy = false;
                            }
                        });
                cursor.registerContentObserver(observer);
            }
            if (mBusy) {
                mListFooterProgress.setVisibility(View.VISIBLE);
                mListFooterText.setVisibility(View.VISIBLE);
                mListFooterText.setText("Loading...");
                // Non-null tag means the view still needs to load it's data
                // text.setTag(this);
            }

            final TextView moduleTextView = (TextView) v
                    .findViewById(R.id.text5);
            if (moduleTextView != null) {
                final String moduleName = moduleTextView.getText().toString();
                Log.e("Module", "value for moduel name : " + moduleName);
                Log.e(LOG_TAG,
                        "Module name from textview:: "
                                + moduleTextView.getText());
                moduleTextView.setBackgroundColor(getResources().getColor(
                        ContentUtils.getModuleColor(moduleName)));
            }

            if (position % 2 == 0) {
                v.setBackgroundColor(getResources().getColor(
                        R.color.listview_row1));
                if (moduleTextView != null) {
                    moduleTextView.setTextColor(getResources().getColor(
                            R.color.listview_row1));
                }
            } else {
                v.setBackgroundColor(getResources().getColor(
                        R.color.listview_row2));
                if (moduleTextView != null) {
                    moduleTextView.setTextColor(getResources().getColor(
                            R.color.listview_row1));
                }
            }
            return v;
        }

        @Override
        public String convertToString(final Cursor cursor) {
            Log.i(LOG_TAG, "convertToString : " + cursor.getString(2));
            return cursor.getString(2);
        }

        @Override
        public Cursor runQueryOnBackgroundThread(final CharSequence constraint) {
            if (getFilterQueryProvider() != null)
                return getFilterQueryProvider().runQuery(constraint);

            StringBuilder buffer = null;
            String[] args = null;
            if (constraint != null) {
                buffer = new StringBuilder();
                buffer.append("UPPER(");
                buffer.append(ContentUtils.getModuleListSelections(mModuleName)[0]);
                buffer.append(") GLOB ?");
                args = new String[] { constraint.toString().toUpperCase() + "*" };
            }

            return mContent.query(ContentUtils.getModuleUri(mModuleName),
                    ContentUtils.getModuleListProjections(mModuleName),
                    buffer == null ? null : buffer.toString(), args,
                    ContentUtils.getModuleSortOrder(mModuleName));
        }
    }

    /**
     * opens the Detail Screen
     * 
     * @param position
     */
    void openDetailScreen(final int position) {
        final Intent detailIntent = new Intent(getActivity(),
                ModuleDetailActivity.class);

        final Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor == null)
            // For some reason the requested item isn't available, do nothing
            return;
        // use the details available from cursor to open detailed view
        detailIntent.putExtra(Util.ROW_ID, cursor.getString(1));
        detailIntent.putExtra(RestConstants.BEAN_ID, cursor.getString(2));
        detailIntent.putExtra(RestConstants.MODULE_NAME, cursor.getString(3));
        detailIntent.putExtra("Recent", true);
        Log.d(LOG_TAG,
                "rowId:" + cursor.getString(1) + "BEAN_ID:"
                        + cursor.getString(2) + "MODULE_NAME:"
                        + cursor.getString(3));

        if (ViewUtil.isTablet(getActivity())) {
            ((BaseMultiPaneActivity) getActivity())
                    .openActivityOrFragment(detailIntent);
        } else {
            startActivity(detailIntent);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void onPause() {
        super.onPause();
    }

    public void showAssignedItems(final View view) {
        // keep this empty as the header is used from list view
    }

    /**
     * <p>
     * showAllItems
     * </p>
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
     * <p>
     * showHome
     * </p>
     * 
     * @param view
     *            a {@link android.view.View} object.
     */
    public void showHome(final View view) {
        final Intent homeIntent = new Intent(getActivity(),
                DashboardActivity.class);
        startActivity(homeIntent);
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

}
