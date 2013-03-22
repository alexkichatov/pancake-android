package com.imaginea.android.sugarcrm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginea.android.sugarcrm.CustomActionbar.AbstractAction;
import com.imaginea.android.sugarcrm.CustomActionbar.Action;
import com.imaginea.android.sugarcrm.CustomActionbar.IntentAction;
import com.imaginea.android.sugarcrm.provider.ContentUtils;
import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Recent;
import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;
import com.imaginea.android.sugarcrm.ui.BaseMultiPaneActivity;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

/**
 * ModuleListFragment, lists the view projections for all the modules.
 * 
 */
public class ModuleListFragment extends ListFragment implements LoaderCallbacks<Cursor>{

    private ListView mListView;

    private String mModuleName;

    private Uri mModuleUri;

    private Uri mIntentUri;

    private int mCurrentSelection;

    private DatabaseHelper mDbHelper;

    private SimpleCursorAdapter mAdapter;

    private String[] mModuleFields;

    private String[] mModuleFieldsChoice;
    
    private Map<String, String> fieldMap = new HashMap<String, String>();

    private int mSortColumnIndex;

    private int MODE = Util.LIST_MODE;

    private String mSelections = ModuleFields.DELETED + "=?";

    private String[] mSelectionArgs = new String[] { Util.EXCLUDE_DELETED_ITEMS };

    private SugarCrmApp app;

    private boolean bRelationItem = true;

    public final static String LOG_TAG = ModuleListFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.common_list, container, false);

    }

    /** {@inheritDoc} */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDbHelper = new DatabaseHelper(getActivity().getBaseContext());
        app = (SugarCrmApp) getActivity().getApplication();
        Intent intent = getActivity().getIntent();
     
        // final Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
        Bundle extras = intent.getExtras();
        mModuleName = Util.CONTACTS;
        if (extras != null) {
            mModuleName = extras.getString(RestUtilConstants.MODULE_NAME);
        }

        mIntentUri = intent.getData();
       
        final CustomActionbar actionBar = (CustomActionbar) getActivity().findViewById(R.id.custom_actionbar);

        final Action homeAction = new IntentAction(ModuleListFragment.this.getActivity(), new Intent(ModuleListFragment.this.getActivity(), DashboardActivity.class), R.drawable.home);
        actionBar.setHomeAction(homeAction);
        actionBar.setTitle(mModuleName);

        if (mIntentUri == null || mIntentUri.getPathSegments().size() < 3) {
            actionBar.addActionItem(new IntentAction(ModuleListFragment.this.getActivity(), AddAction(), R.drawable.add));
            actionBar.addActionItem(new SearchAction());
            actionBar.addActionItem(new SortAction());
            actionBar.addActionItem(new SyncAction());
            actionBar.addActionItem(new ShowAllAction());
            actionBar.addActionItem(new ShowAssignedAction());

            bRelationItem = false;
        }

        mListView = getListView();

        // mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                addToRecent(position);
                openDetailScreen(position);
            }
        });

        // button code in the layout - 1.6 SDK feature to specify onClick
        mListView.setItemsCanFocus(true);
        mListView.setFocusable(true);
        
        View empty = getActivity().findViewById(R.id.empty);    
        TextView tv1 = (TextView) (empty.findViewById(R.id.mainText));
        tv1.setText("No " + mModuleName + " found");
        mListView.setEmptyView(empty);        
        
        registerForContextMenu(getListView());

        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, "ModuleName:-->" + mModuleName);
        }

        mModuleUri = ContentUtils.getModuleUri(mModuleName);
        if (mIntentUri == null) {
            intent.setData(mModuleUri);
            mIntentUri = mModuleUri;
        }
   
        mModuleFields = ContentUtils.getModuleListSelections(mModuleName);
        if (mModuleFields.length >= 2)
            mAdapter = new SimpleCursorAdapter(this.getActivity(), R.layout.contact_listitem, null, mModuleFields, new int[] {
                    android.R.id.text1, android.R.id.text2 }, 0);
        else
            mAdapter = new SimpleCursorAdapter(this.getActivity(), R.layout.contact_listitem, null, mModuleFields, new int[] { android.R.id.text1 }, 0);
        setListAdapter(mAdapter);
        // make the list filterable using the keyboard
        mListView.setTextFilterEnabled(true);

       // get the module fields for the module
        Map<String, ModuleField> map = ContentUtils.getModuleFields(getActivity(), mModuleName);
        if (map == null) {
            Log.w(LOG_TAG, "Cannot prepare Options as Map is null for module:" + mModuleName);
            // TODO return false;
            return;
        }
        mModuleFieldsChoice = new String[mModuleFields.length];
        for (int i = 0; i < mModuleFields.length; i++) {
            // add the module field label to be displayed in the choice menu
            ModuleField modField = map.get(mModuleFields[i]);
            if (modField != null) {
                mModuleFieldsChoice[i] = modField.getLabel();
                // fieldMap: label vs name
                fieldMap.put(mModuleFieldsChoice[i], mModuleFields[i]);
            } else
                mModuleFieldsChoice[i] = "";
            
            if (mModuleFieldsChoice[i].indexOf(":") > 0) {
                mModuleFieldsChoice[i] = mModuleFieldsChoice[i].substring(0, mModuleFieldsChoice[i].length() - 1);
                fieldMap.put(mModuleFieldsChoice[i], mModuleFields[i]);
            }
        }
        
        getLoaderManager().initLoader(0, null, this);
    }

   
    /**
     * opens the Detail Screen
     * 
     * @param position
     */
    void openDetailScreen(int position) {

        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            Log.w(LOG_TAG, "openDetailScreen, Cursor is null for " + position);
            return;
        }
        Intent detailIntent = new Intent(this.getActivity(), ModuleDetailActivity.class);
        detailIntent.putExtra(Util.ROW_ID, cursor.getString(0));
        detailIntent.putExtra(RestUtilConstants.BEAN_ID, cursor.getString(1));
        detailIntent.putExtra(RestUtilConstants.MODULE_NAME, mModuleName);
        detailIntent.putExtra("Relation", bRelationItem);
        if (ViewUtil.isTablet(getActivity())) {
            /*
             * We can display everything in-place with fragments. Have the list highlight this item
             * and show the data. Check what fragment is shown, replace if needed.
             */
            // ModuleDetailFragment details = (ModuleDetailFragment)
            // getFragmentManager().findFragmentByTag("module_detail");
            // Log.d("onClick list item", "details is null");
            ((BaseMultiPaneActivity) getActivity()).openActivityOrFragment(detailIntent);

        } else {
            startActivity(detailIntent);
        }
    }

    /**
     * opens the Edit Screen
     * 
     * @param position
     */
    private void openEditScreen(int position) {

        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }
        Intent editDetailsIntent = new Intent(this.getActivity(), EditModuleDetailActivity.class);
        editDetailsIntent.putExtra(Util.ROW_ID, cursor.getString(0));
        if (mIntentUri != null)
            editDetailsIntent.setData(Uri.withAppendedPath(mIntentUri, cursor.getString(0)));

        editDetailsIntent.putExtra(RestUtilConstants.BEAN_ID, cursor.getString(1));
        editDetailsIntent.putExtra(RestUtilConstants.MODULE_NAME, mModuleName);

        if (Log.isLoggable(LOG_TAG, Log.DEBUG))
            Log.d(LOG_TAG, "beanId:" + cursor.getString(1));
        
        startActivity(editDetailsIntent);
        
        /*ModuleDetailFragment details = (ModuleDetailFragment) getFragmentManager().findFragmentByTag("module_detail");
        //if (details != null) {
            *
             * We can display everything in-place with fragments. Have the list highlight this item
             * and show the data. Make new fragment to show this selection.
             *
          //  getListView().setItemChecked(position, true);
         //   ((BaseMultiPaneActivity) getActivity()).openActivityOrFragment(editDetailsIntent);

        //} else {
            startActivity(editDetailsIntent);
       // }*/
    }

    /**
     * deletes an item
     */
    void deleteItem() {
        Cursor cursor = (Cursor) getListAdapter().getItem(mCurrentSelection);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }

        String beanId = cursor.getString(1);
        if (Log.isLoggable(LOG_TAG, Log.DEBUG))
            Log.d(LOG_TAG, "beanId:" + beanId);

        mModuleUri = ContentUtils.getModuleUri(mModuleName);
        Uri deleteUri = Uri.withAppendedPath(mModuleUri, cursor.getString(0));
        getActivity().getContentResolver().registerContentObserver(deleteUri, false, new DeleteContentObserver(new Handler()));
        ServiceHelper.startServiceForDelete(getActivity().getBaseContext(), deleteUri, mModuleName, beanId);
        ContentValues values = new ContentValues();
        values.put(ModuleFields.DELETED, Util.DELETED_ITEM);
        getActivity().getBaseContext().getContentResolver().update(deleteUri, values, null, null);
        
    }     

    private static class DeleteContentObserver extends ContentObserver {

        public DeleteContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }
    }


    /** {@inheritDoc} */
    // TODO
    // @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        String[] modules = { mModuleName };
        appData.putString(RestUtilConstants.MODULE_NAME, mModuleName);
        appData.putStringArray(RestUtilConstants.MODULES, modules);
        appData.putInt(RestUtilConstants.OFFSET, 0);
        appData.putInt(RestUtilConstants.MAX_RESULTS, 20);

        getActivity().startSearch(null, false, appData, false);
        return true;
    }

    
    /** {@inheritDoc} */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.options);
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "Bad menuInfo", e);
            return;
        }

        if (mDbHelper == null)
            mDbHelper = new DatabaseHelper(getActivity().getBaseContext());

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }
        int index = cursor.getColumnIndex(ModuleFields.CREATED_BY_NAME);
        String ownerName = cursor.getString(index);

        menu.add(1, R.string.view, 2, R.string.view).setEnabled(mDbHelper.isAclEnabled(mModuleName, RestUtilConstants.VIEW, ownerName));
        menu.add(2, R.string.edit, 3, R.string.edit).setEnabled(mDbHelper.isAclEnabled(mModuleName, RestUtilConstants.EDIT, ownerName));
        menu.add(3, R.string.delete, 4, R.string.delete).setEnabled(mDbHelper.isAclEnabled(mModuleName, RestUtilConstants.DELETE, ownerName));

        // TODO disable options based on acl actions for the module

        // TODO
        if (ContentUtils.getModuleField(getActivity(), mModuleName, ModuleFields.PHONE_WORK) != null)
            menu.add(4, R.string.call, 4, R.string.call);
        if (ContentUtils.getModuleField(getActivity(), mModuleName, ModuleFields.EMAIL1) != null)
            menu.add(5, R.string.email, 4, R.string.email);

    }

    /** {@inheritDoc} */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "bad menuInfo", e);
            return false;
        }
        int position = info.position;

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
            DialogFragment newFragment = new ModuleListAlertDialogFragment(R.string.delete);
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

    private void sortList(String sortOrder) {
        String selection = null;
        if (MODE == Util.ASSIGNED_ITEMS_MODE) {
            String userName = SugarCrmSettings.getUsername(this.getActivity());
            selection = ModuleFields.ASSIGNED_USER_NAME + "='" + userName + "'";
        }
        mSelections = selection;
        mSelectionArgs = null;
        getLoaderManager().restartLoader(0, null, this);
    }

    void addToRecent(int position) {
        ContentValues modifiedValues = new ContentValues();
        // push the selected record into recent table
        Cursor cursor = (Cursor) getListAdapter().getItem(position);

        // now insert into recent table
        modifiedValues.put(Recent.ACTUAL_ID, cursor.getInt(0) + "");
        modifiedValues.put(Recent.BEAN_ID, cursor.getString(1));
        modifiedValues.put(Recent.NAME_1, cursor.getString(2));
        modifiedValues.put(Recent.NAME_2, cursor.getString(3));
        modifiedValues.put(Recent.REF_MODULE_NAME, mModuleName);
        modifiedValues.put(Recent.DELETED, "0");
        Uri insertResultUri = getActivity().getApplicationContext().getContentResolver().insert(Recent.CONTENT_URI, modifiedValues);
        Log.i(LOG_TAG, "insertResultURi - " + insertResultUri);

    }

    private void showHome() {
        Intent homeIntent = new Intent(getActivity(), DashboardActivity.class);
        startActivity(homeIntent);
    }

    private String getSortOrder() {
        String sortOrder = null;
        Map<String, String> sortOrderMap = app.getModuleSortOrder(mModuleName);
        for (Entry<String, String> entry : sortOrderMap.entrySet()) {
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
    public void callNumber(int position) {
        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }

        int index = cursor.getColumnIndex(ModuleFields.PHONE_WORK);
        String number = cursor.getString(index);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
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
    public void sendMail(int position) {
        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }
        // emailAddress
        int index = cursor.getColumnIndex(ModuleFields.EMAIL1);
        String emailAddress = cursor.getString(index);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + emailAddress));
        startActivity(Intent.createChooser(intent, getActivity().getString(R.string.email)));
    }
    
    private void startModuleSync() {
        Log.d(LOG_TAG, "startModuleSync");
        Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
        extras.putInt(Util.SYNC_TYPE, Util.SYNC_MODULE_DATA);
        extras.putString(RestUtilConstants.MODULE_NAME, mModuleName);
        SugarCrmApp app = (SugarCrmApp) ModuleListFragment.this.getActivity().getApplication();
        final String usr = SugarCrmSettings.getUsername(ModuleListFragment.this.getActivity()).toString();
        ContentResolver.requestSync(app.getAccount(usr), SugarCRMProvider.AUTHORITY, extras);
    }

 /*   // Task to sync individual module
    class ModuleSyncTask extends AsyncTask<Object, Object, Object> {

        boolean hasExceptions = false;

        SharedPreferences prefs;

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prefs = PreferenceManager.getDefaultSharedPreferences(ModuleListFragment.this.getActivity());
            mProgressDialog = ViewUtil.getProgressDialog(ModuleListFragment.this.getActivity(), "Syncing...", true);
            mProgressDialog.show();
        }

        @Override
        protected Object doInBackground(Object... args) {
            startModuleSync();
            return true;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Object sessionId) {
            super.onPostExecute(sessionId);
            if (isCancelled())
                return;

            mProgressDialog.cancel();
            mProgressDialog = null;
        }

        @SuppressWarnings("deprecation")
        private void startModuleSync() {
            Log.d(LOG_TAG, "startModuleSync");
            Bundle extras = new Bundle();
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
            extras.putInt(Util.SYNC_TYPE, Util.SYNC_MODULE_DATA);
            extras.putString(RestUtilConstants.MODULE_NAME, mModuleName);
            SugarCrmApp app = (SugarCrmApp) ModuleListFragment.this.getActivity().getApplication();
            final String usr = SugarCrmSettings.getUsername(ModuleListFragment.this.getActivity()).toString();
            ContentResolver.requestSync(app.getAccount(usr), SugarCRMProvider.AUTHORITY, extras);
        }
    }
*/
    private Intent AddAction() {
        Intent myIntent = new Intent(this.getActivity(), EditModuleDetailActivity.class);
        myIntent.putExtra(RestUtilConstants.MODULE_NAME, mModuleName);
        return myIntent;
    }

    private class SearchAction extends AbstractAction {

        public SearchAction() {
            super(R.drawable.search);
        }

        @Override
        public void performAction(View view) {
            onSearchRequested();
        }
    }

   

    private class SortAction extends AbstractAction {

        public SortAction() {
            super(R.drawable.more, "Sort");
        }

        @Override
        public void performAction(View view) {
            DialogFragment newFragment = new ModuleListAlertDialogFragment(R.string.sortBy);
            newFragment.show(getFragmentManager(), "dialog");
        }
    }

    private class SyncAction extends AbstractAction {

        public SyncAction() {
            super(R.drawable.more, "Sync");
        }

        @Override
        public void performAction(View view) {
        	if(!Util.isNetworkOn(ModuleListFragment.this.getActivity().getBaseContext())) {
        		Toast.makeText(ModuleListFragment.this.getActivity(), R.string.networkUnavailable, Toast.LENGTH_SHORT).show();
        	} else {
        	    SugarCrmApp app = (SugarCrmApp) ModuleListFragment.this.getActivity().getApplication();
                final String usr = SugarCrmSettings.getUsername(ModuleListFragment.this.getActivity()).toString();
        	    if(ContentResolver.isSyncActive(app.getAccount(usr), SugarCRMProvider.AUTHORITY))
                {
        	    	 DialogFragment newFragment = new ModuleListAlertDialogFragment(R.string.info);
        	         newFragment.show(getFragmentManager(), "dialog");
                     return;
                }   
        	    startModuleSync();
//        	    mSyncTask = new ModuleSyncTask();
//        		mSyncTask.execute();
        	}
        }
    }

    private class ShowAllAction extends AbstractAction {

        public ShowAllAction() {
            super(R.drawable.more, "All");
        }

        @Override
        public void performAction(View view) {
        	
        	MODE = Util.LIST_MODE;
            mSelections = null;
            mSelectionArgs = null;
            
            getLoaderManager().restartLoader(0, null, ModuleListFragment.this);
        }
    }

    private class ShowAssignedAction extends AbstractAction {

        public ShowAssignedAction() {
            super(R.drawable.more, "Assigned");
        }

        @Override
        public void performAction(View view) {
        	
        	 MODE = Util.ASSIGNED_ITEMS_MODE;
             String userName = SugarCrmSettings.getUsername(ModuleListFragment.this.getActivity());
             String selection = ModuleFields.ASSIGNED_USER_NAME + "='" + userName + "'";
             
             mSelections = selection;
             mSelectionArgs = null;
             getLoaderManager().restartLoader(0, null, ModuleListFragment.this);
        }
    }


	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
		return new CursorLoader(getActivity(), mIntentUri,
				ContentUtils.getModuleProjections(mModuleName), mSelections, mSelectionArgs,
                getSortOrder());
	
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		
		mAdapter.swapCursor(data);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
		mAdapter.swapCursor(null);
		
	}
	
	private class ModuleListAlertDialogFragment extends DialogFragment {

        public ModuleListAlertDialogFragment(int title) {        	
            Bundle args = new Bundle();
            args.putInt("title", title);
            setArguments(args);         
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("title");
            
            switch(title){
            case R.string.sortBy:
            	 mSortColumnIndex = 0;
                 
                 Map<String, String> sortOrderMap = app.getModuleSortOrder(mModuleName);
                 if(sortOrderMap != null) {
                     for (Entry<String, String> entry : sortOrderMap.entrySet()) {
                         for (mSortColumnIndex = 0; mSortColumnIndex < mModuleFieldsChoice.length;) {
                             if(fieldMap.get(mModuleFieldsChoice[mSortColumnIndex]).equals(entry.getKey()))
                                 break;
                             mSortColumnIndex++;
                         }                    
                     }        
                 }
                 if(mSortColumnIndex == mModuleFieldsChoice.length)
                     mSortColumnIndex = 0;
                 return new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(title).setSingleChoiceItems(mModuleFieldsChoice, mSortColumnIndex, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int whichButton) {
                         mSortColumnIndex = whichButton;
                     }
                 }).setPositiveButton(R.string.ascending, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int whichButton) {
                         String sortOrder = mModuleFields[mSortColumnIndex] + " ASC";
                         app.setModuleSortOrder(mModuleName, fieldMap.get(mModuleFieldsChoice[mSortColumnIndex]), "ASC");
                         sortList(sortOrder);
                     }
                 })

                 .setNegativeButton(R.string.descending, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int whichButton) {
                         // ((FragmentAlertDialog)getActivity()).doNegativeClick();
                         String sortOrder = mModuleFields[mSortColumnIndex] + " DESC";
                         app.setModuleSortOrder(mModuleName, fieldMap.get(mModuleFieldsChoice[mSortColumnIndex]), "DESC");
                         sortList(sortOrder);
                     }
                 }).create();
            	
            case R.string.delete:
            	return new AlertDialog.Builder(this.getActivity()).setTitle(title).setMessage(R.string.deleteAlert).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        deleteItem();
                        if (ViewUtil.isTablet(getActivity())) {
                            ModuleDetailFragment fragment = (ModuleDetailFragment) getActivity().getSupportFragmentManager().findFragmentByTag("module_detail");
                            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                            ModuleDetailFragment moduleDetailFragment = new ModuleDetailFragment();
                            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_module_detail, moduleDetailFragment, "module_detail").commit();
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).create();
            	
            case R.string.info:
            	return new AlertDialog.Builder(ModuleListFragment.this.getActivity()).setTitle(title).setMessage(R.string.syncProgressMsg).setIcon(R.drawable.applaunch)
            	.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}}).create();
                            	
            }
            return null;
           
        }
    }
}