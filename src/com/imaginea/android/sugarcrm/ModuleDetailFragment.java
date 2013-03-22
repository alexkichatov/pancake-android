package com.imaginea.android.sugarcrm;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.CustomActionbar.AbstractAction;
import com.imaginea.android.sugarcrm.CustomActionbar.Action;
import com.imaginea.android.sugarcrm.CustomActionbar.IntentAction;
import com.imaginea.android.sugarcrm.provider.ContentUtils;
import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent;
import com.imaginea.android.sugarcrm.tab.ModuleDetailsMultiPaneActivity;
import com.imaginea.android.sugarcrm.ui.BaseActivity;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * ModuleDetailFragment is used to show details for all modules.
 * 
 */
public class ModuleDetailFragment extends Fragment {

    private String mRowId;

    private String mSugarBeanId;

    private String mModuleName;

    private Cursor mCursor;

    private String[] mSelectFields;

    private ViewGroup mDetailsTable;

    private String[] mRelationshipModules;

    private Uri mUri;

    private DatabaseHelper mDbHelper;

    private LoadContentTask mTask;

    private ProgressDialog mProgressDialog;

    private CustomActionbar actionBar;

    private RelativeLayout mParent;
  
    private static final String LOG_TAG = ModuleDetailFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mParent = (RelativeLayout) inflater.inflate(R.layout.account_details, container, false);
        return mParent;
    }

    /** {@inheritDoc} */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (getFragmentManager().findFragmentById(R.id.list_frag) != null)
            intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
        Bundle extras = intent.getExtras();
        mRowId = (String) intent.getStringExtra(Util.ROW_ID);
        mSugarBeanId = (String) intent.getStringExtra(RestUtilConstants.BEAN_ID);
        boolean bRecent = (boolean) intent.getBooleanExtra("Recent", false);
        boolean bRelation = (boolean) intent.getBooleanExtra("Relation", false);
        
        mModuleName = extras.getString(RestUtilConstants.MODULE_NAME);

        mDbHelper = new DatabaseHelper(getActivity().getBaseContext());
        if (intent.getData() == null) {
            if (mRowId == null)
                mRowId = "1";
            mUri = Uri.withAppendedPath(ContentUtils.getModuleUri(mModuleName), mRowId);
        } else
            mUri = intent.getData();
        mSelectFields = ContentUtils.getModuleProjections(mModuleName);
       
        mRelationshipModules = ContentUtils.getModuleRelationshipItems(mModuleName);

        /*
         * RelationshipAdapter adapter = new RelationshipAdapter(this);
         * adapter.setRelationshipArray(mRelationshipModules); listView.setAdapter(adapter);
         */
        // actionBar = (CustomActionbar)
        // ModuleDetailFragment.this.getActivity().findViewById(R.id.custom_actionbar);
        if (mSugarBeanId != null) {
            actionBar = (CustomActionbar) mParent.getChildAt(0);
            if (!ViewUtil.isTablet(getActivity())) {
                final Action homeAction = new IntentAction(ModuleDetailFragment.this.getActivity(), new Intent(ModuleDetailFragment.this.getActivity(), DashboardActivity.class), R.drawable.home);
                actionBar.setHomeAction(homeAction);
            }
            if (!bRecent && !bRelation) {
                actionBar.addActionItem(new EditAction());
                actionBar.addActionItem(new DeleteAction());
                if (mRelationshipModules.length > 0) {
                    actionBar.addActionItem(new IntentAction(ModuleDetailFragment.this.getActivity(), null, R.drawable.relation));
                    for (String mModule : mRelationshipModules) {
                        actionBar.addActionItem(new RelationAction(mModule));
                    }
                }
            }
        }
              
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
    protected void openListScreen(String moduleName) {
        Intent detailIntent;
        // if (mModuleName.equals("Accounts")) {
        // Intent detailIntent = new Intent(ModuleDetailFragment.this.getActivity(),
        // ModulesActivity.class);
        if (ViewUtil.isTablet(getActivity())) {
            detailIntent = new Intent(ModuleDetailFragment.this.getActivity(), ModuleDetailsMultiPaneActivity.class);
            detailIntent.putExtra(Util.ROW_ID, mRowId);
            detailIntent.putExtra("Relation", true);
        } else {
            detailIntent = new Intent(ModuleDetailFragment.this.getActivity(), ModulesActivity.class);
        }
       
        Uri uri = Uri.withAppendedPath(ContentUtils.getModuleUri(mModuleName), mRowId);
        uri = Uri.withAppendedPath(uri, moduleName);
        detailIntent.setData(uri);
        detailIntent.putExtra(RestUtilConstants.BEAN_ID, mSugarBeanId);
        detailIntent.putExtra(RestUtilConstants.MODULE_NAME, moduleName);
        startActivity(detailIntent);
        // } else {
        // Toast.makeText(this, "Not yet supported!",
        // Toast.LENGTH_SHORT).show();
        // }
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
			 mTask = new LoadContentTask(getActivity());
	         mTask.execute(null, null, null);
		 }    	 
    }
    /** {@inheritDoc} */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.details_activity_menu, menu);

        SubMenu relationshipMenu = menu.addSubMenu(1, R.string.related, 0, getString(R.string.related));
        relationshipMenu.setIcon(R.drawable.menu_related);
        if (mRelationshipModules.length > 0) {
            for (int i = 0; i < mRelationshipModules.length; i++) {
                relationshipMenu.add(0, Menu.FIRST + i, 0, mRelationshipModules[i]);
            }
        } else {
            menu.setGroupEnabled(1, false);
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.home:
            Intent myIntent = new Intent(ModuleDetailFragment.this.getActivity(), DashboardActivity.class);
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
    	  
        private List<String> mBillingAddressGroup = new ArrayList<String>();
        
        private List<String> mShippingAddressGroup = new ArrayList<String>();

        private List<String> mDurationGroup = new ArrayList<String>();     
        
        private List<String> mFieldsExcludedForDetails = new ArrayList<String>();

        private String title;
        private LinkedHashMap<String, DetailsItem> detailItems = new LinkedHashMap<String, DetailsItem>();

        LoadContentTask(Context context) {
        	mContext = context;
            mDetailsTable = (ViewGroup) getActivity().findViewById(R.id.accountDetalsTable);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            mProgressDialog = ViewUtil.getProgressDialog(ModuleDetailFragment.this.getActivity(), getString(R.string.loading), true);
            mProgressDialog.show();
            
            
            //Initializing BillingAddressGroup the list 
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_STREET);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_STREET_2);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_STREET_3);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_STREET_4);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_CITY);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_STATE);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_POSTALCODE);
            mBillingAddressGroup.add(ModuleFields.BILLING_ADDRESS_COUNTRY);
            
            //Initializing ShippingAddressGroup the list 
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_STREET);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_STREET_2);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_STREET_3);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_STREET_4);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_CITY);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_STATE);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_POSTALCODE);
            mShippingAddressGroup.add(ModuleFields.SHIPPING_ADDRESS_COUNTRY);
            
            //Initializing DurationGroup the list 
            mDurationGroup.add(ModuleFields.DURATION_HOURS);
            mDurationGroup.add(ModuleFields.DURATION_MINUTES);        
            
            // add a field name to the map if a module field in detail projection is to be excluded in
            // details screen
            mFieldsExcludedForDetails.add(SugarCRMContent.RECORD_ID);
            mFieldsExcludedForDetails.add(SugarCRMContent.SUGAR_BEAN_ID);
            mFieldsExcludedForDetails.add(ModuleFields.DELETED);
            mFieldsExcludedForDetails.add(ModuleFields.ACCOUNT_ID);       
        }
  
        @Override
        protected Object doInBackground(Object... params) {
            try {
                mCursor = getActivity().getContentResolver().query(mUri, mSelectFields, null, null, ContentUtils.getModuleSortOrder(mModuleName));
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                return Util.FETCH_FAILED;
            }
            
            prepareDetailItems();

            return Util.FETCH_SUCCESS;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (detailItems.size() > 0) {
            	setupDetailViews();
            } 
            
            // close the cursor irrespective of the result
            if (mCursor != null && !mCursor.isClosed())
                mCursor.close();

            mProgressDialog.cancel();
        }
        
        private void setupDetailViews(){
        	actionBar.setTitle(title);
        	
        	LayoutInflater inflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	
        	Set<Entry<String, DetailsItem>> entrySet = detailItems.entrySet();
        	
        	Iterator<Entry<String, DetailsItem>> iterator = entrySet.iterator();
        	
        	while(iterator.hasNext()){
        		
        		Entry<String, DetailsItem> item = iterator.next();
        		String label = item.getKey();
        		DetailsItem detailObj = item.getValue();
        		
	        	ViewGroup tableRow = (ViewGroup) inflater.inflate(R.layout.table_row, null);
	        	TextView textViewForLabel = (TextView) tableRow.getChildAt(0);
	        	TextView textViewForValue = (TextView) tableRow.getChildAt(1);
	        	
	        	tableRow.setVisibility(View.VISIBLE);
	        	textViewForLabel.setText(label);
	        	
	        	final String value = detailObj.getValue();
	        	
	        	if (detailObj.getType().equals("phone"))
	        		textViewForValue.setAutoLinkMask(Linkify.PHONE_NUMBERS);
	        	
	        	textViewForValue.setText(value);
	        	
	        	        	
	        	 // handle the map
	            String  fieldName = detailObj.getFieldName();
	            if (ModuleFields.SHIPPING_ADDRESS_COUNTRY.equals(fieldName)
	                                            || ModuleFields.BILLING_ADDRESS_COUNTRY.equals(fieldName)) {
	                if (!TextUtils.isEmpty(value)) {
	                	textViewForValue.setLinksClickable(true);
	                	textViewForValue.setClickable(true);
	
	                    SpannableString spannableString = new SpannableString(value);
	                    spannableString.setSpan(new InternalURLSpan(new OnClickListener() {
	                        @Override
	                        public void onClick(View v) {
	                            Log.i(LOG_TAG, "trying to locate - " + value);
	                            Uri uri = Uri.parse("geo:0,0?q=" + URLEncoder.encode(value));
	                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	                            intent.setData(uri);
	                            startActivity(Intent.createChooser(intent, getString(R.string.showAddressMsg)));
	                        }
	                    }), 0, value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	
	                    textViewForValue.setText(spannableString);
	
	                    // for trackball movement
	                    MovementMethod m = textViewForValue.getMovementMethod();
	                    if ((m == null) || !(m instanceof LinkMovementMethod)) {
	                        if (textViewForValue.getLinksClickable()) {
	                        	textViewForValue.setMovementMethod(LinkMovementMethod.getInstance());
	                        }
	                    }
	                }
	            }
	            mDetailsTable.addView(tableRow);
        	}
            
            
        }

        private void prepareDetailItems() {

            String[] detailsProjection = mSelectFields;
            
            mCursor.moveToFirst();

            if (mDbHelper == null)
                mDbHelper = new DatabaseHelper(getActivity().getBaseContext());

            List<String> titleFields = Arrays.asList(ContentUtils.getModuleListSelections(mModuleName));

            String value = "";
            Map<String, ModuleField> fieldNameVsModuleField = ContentUtils.getModuleFields(mContext, mModuleName);
  
            for (int i = 0; i < detailsProjection.length; i++) {
             
                String fieldName = detailsProjection[i];

                // if the field name is excluded in details screen, skip it
                if (mFieldsExcludedForDetails.contains(fieldName)) {
                    continue;
                }

                int columnIndex = mCursor.getColumnIndex(fieldName);
                if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                    Log.d(LOG_TAG, "Col:" + columnIndex + " moduleName : " + mModuleName
                                                    + " fieldName : " + fieldName);
                }

                String tempValue = mCursor.getString(columnIndex);

                // get the attributes of the moduleField
                ModuleField moduleField = fieldNameVsModuleField.get(fieldName);

                if (moduleField != null) {
               
                    // set the title
                    if (titleFields.contains(fieldName) && tempValue != null) {
                        title = tempValue;
                        continue;
                    }

                    String label = moduleField.getLabel();

                    // check for the billing and shipping address groups only if
                    // the module is
                    // 'Accounts'
                    if (Util.ACCOUNTS.equals(mModuleName)) {
                        if (mBillingAddressGroup.contains(fieldName)) {
                            if (fieldName.equals(ModuleFields.BILLING_ADDRESS_STREET)) {
                                // First field in the group
                                value = (!TextUtils.isEmpty(tempValue)) ? tempValue + ", " : "";
                                continue;
                            } else if (fieldName.equals(ModuleFields.BILLING_ADDRESS_COUNTRY)) {
                                // last field in the group

                                value = value + (!TextUtils.isEmpty(tempValue) ? tempValue : "");
                                label = getActivity().getBaseContext().getString(R.string.billing_address);
                            } else {
                                value = value
                                                                + (!TextUtils.isEmpty(tempValue) ? tempValue
                                                                                                + ", "
                                                                                                : "");
                                continue;
                            }
                        } else if (mShippingAddressGroup.contains(fieldName)) {
                            if (fieldName.equals(ModuleFields.SHIPPING_ADDRESS_STREET)) {
                                // First field in the group
                                value = (!TextUtils.isEmpty(tempValue)) ? tempValue + ", " : "";
                                continue;
                            } else if (fieldName.equals(ModuleFields.SHIPPING_ADDRESS_COUNTRY)) {
                                // Last field in the group

                                value = value + (!TextUtils.isEmpty(tempValue) ? tempValue : "");
                                label = getActivity().getBaseContext().getString(R.string.shipping_address);
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
                            value = (!TextUtils.isEmpty(tempValue)) ? tempValue + "hr " : "";
                            continue;
                        } else if (fieldName.equals(ModuleFields.DURATION_MINUTES)) {
                            // Last field in the group
                            value = value
                                                            + (!TextUtils.isEmpty(tempValue) ? tempValue
                                                                                            + "mins "
                                                                                            : "");
                            label = getActivity().getBaseContext().getString(R.string.duration);
                        }
                    } else {
                        value = tempValue;
                    }

                    if (!TextUtils.isEmpty(value)) {
                    	detailItems.put(label, new DetailsItem(fieldName, value, moduleField.getType()));
                    } else {
                    	detailItems.put(label, new DetailsItem(fieldName, getString(R.string.notAvailable), moduleField.getType()));
                    }

               } else {
                    // module fields is null
                }

            }
        }
    }
    
    class DetailsItem{
    	private String mFieldName;
    	private String mValue;
    	private String mType;
    	
    	DetailsItem(String fieldName, String value, String type){
    		this.mFieldName = fieldName;
    		this.mValue = value;
    		this.mType = type;
    	}
    	
    	public String getValue(){
    		return mValue;
    	}
    	
    	public String getType(){
    		return mType;
    	}
    	
    	public String getFieldName(){
    		return mFieldName;
    	}
    }

    static class InternalURLSpan extends ClickableSpan {
        OnClickListener mListener;

        public InternalURLSpan(OnClickListener listener) {
            super();
            mListener = listener;
        }

        @Override
        public void onClick(View widget) {
            Log.i("ModuleDetailFragment", "InternalURLSpan onClick");
            mListener.onClick(widget);
        }
    }

    private class EditAction extends AbstractAction {

        public EditAction() {
            super(R.drawable.edit);
        }

        @Override
        public void performAction(View view) {

            Intent editDetailsIntent = new Intent(ModuleDetailFragment.this.getActivity(), EditModuleDetailActivity.class);
            editDetailsIntent.putExtra(Util.ROW_ID, mRowId);
            if (mUri != null)
                editDetailsIntent.setData(mUri);

            editDetailsIntent.putExtra(RestUtilConstants.BEAN_ID, mSugarBeanId);
            editDetailsIntent.putExtra(RestUtilConstants.MODULE_NAME, mModuleName);
            
            startActivity(editDetailsIntent);
            
           /* ModuleDetailFragment details = (ModuleDetailFragment) getFragmentManager().findFragmentByTag("module_detail");
            //if (details != null) {
                *
                 * We can display everything in-place with fragments. Have the list highlight this
                 * item and show the data. Make new fragment to show this selection.
                 *
                // getListView().setItemChecked(position, true);
               // ((BaseMultiPaneActivity) getActivity()).openActivityOrFragment(editDetailsIntent);

            //} else {
                startActivity(editDetailsIntent);
           // }*/
        }
    }

    private class DeleteAction extends AbstractAction {

        public DeleteAction() {
            super(R.drawable.delete);
        }

        @Override
        public void performAction(View view) {

            DialogFragment newFragment = new MyYesNoAlertDialogFragment().newInstance(R.string.delete);
            newFragment.show(getFragmentManager(), "YesNoAlertDialog");
        }
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

    private class RelationAction extends AbstractAction {

        public RelationAction(String title) {
            super(R.drawable.relation, title);
        }

        @Override
        public void performAction(View view) {
            openListScreen(this.getTitle());
        }
    }

    public class MyYesNoAlertDialogFragment extends DialogFragment {

        public MyYesNoAlertDialogFragment newInstance(int title) {
            MyYesNoAlertDialogFragment frag = new MyYesNoAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return new AlertDialog.Builder(this.getActivity()).setTitle(R.string.delete).setMessage(R.string.deleteAlert).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                   
                    Uri deleteUri = Uri.withAppendedPath(ContentUtils.getModuleUri(mModuleName), mRowId);
                    getActivity().getContentResolver().registerContentObserver(deleteUri, false, new DeleteContentObserver(new Handler()));
                    ServiceHelper.startServiceForDelete(getActivity().getBaseContext(), deleteUri, mModuleName, mSugarBeanId);
                    
                    ContentValues values = new ContentValues();
                    values.put(ModuleFields.DELETED, Util.DELETED_ITEM);
                    getActivity().getBaseContext().getContentResolver().update(deleteUri, values, null, null);
                    
                    if (ViewUtil.isTablet(getActivity())) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(ModuleDetailFragment.this).commit();
                        ModuleDetailFragment moduleDetailFragment = new ModuleDetailFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_module_detail, moduleDetailFragment, "module_detail").commit();
                    } else
                        ModuleDetailFragment.this.getActivity().finish();

                }
            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            }).create();
        }
    }

}
