package com.imaginea.android.sugarcrm;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.imaginea.android.sugarcrm.CustomActionbar.Action;
import com.imaginea.android.sugarcrm.CustomActionbar.IntentAction;
import com.imaginea.android.sugarcrm.provider.ContentUtils;
import com.imaginea.android.sugarcrm.tab.ModuleDetailsMultiPaneActivity;
import com.imaginea.android.sugarcrm.tab.RecentModuleMultiPaneActivity;
import com.imaginea.android.sugarcrm.ui.BaseSinglePaneActivity;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

/**
 * DashboardFragment
 * 
 */
public class DashboardFragment extends Fragment {

    private List<String> mModuleNames;
    
    private DashboardAdapter mAdapter;
    
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.dashboard_fragment, container, false);
        
        mAdapter = new DashboardAdapter(getActivity());
        
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mModuleNames.size() < 2) {
        	
        	if(mAdapter != null){
        		mAdapter.removeAll();
        	}           
            mModuleNames = ContentUtils.getModuleList(getActivity());
            mModuleNames.add(getString(R.string.recent));
            

            mAdapter.addItems(mModuleNames);
        }
        
    }

    
    /** {@inheritDoc} */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final CustomActionbar actionBar = (CustomActionbar) getActivity().findViewById(R.id.custom_actionbar);
        final Action homeAction = new IntentAction(DashboardFragment.this.getActivity(), null, R.drawable.pancake_logo);
        actionBar.setHomeAction(homeAction);
        actionBar.setTitle("Pancake");

        final Action settingAction = new IntentAction(DashboardFragment.this.getActivity(), createSettingsIntent(), R.drawable.settings);
        actionBar.addActionItem(settingAction);
        
        
        GridView gridView = (GridView)root.findViewById(R.id.gridview);
        gridView.setAdapter(mAdapter);
          
        mModuleNames = ContentUtils.getModuleList(getActivity());
        mModuleNames.add(getString(R.string.recent));
                
        mAdapter.addItems(mModuleNames);
        
        gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
				viewModuleList(view);
			}
        	
		});
     
    }

    public void viewModuleList(View view) {
    	 Intent myIntent;
         String moduleName = (String) view.getTag();
         Log.d("onClick moduleName message", moduleName);
         
         Context context = getActivity();
         
         if (moduleName.equals(getString(R.string.settings))) {
             myIntent = new Intent(context, SugarCrmSettings.class);
         } else if (moduleName.equals(getString(R.string.recent))) {
             // TODO
             if (ViewUtil.isHoneycombTablet(context)) {
                 myIntent = new Intent(context, RecentModuleMultiPaneActivity.class);
             } else {
                 myIntent = new Intent(context, RecentModuleActivity.class);
             }
         } else {
             if (ViewUtil.isHoneycombTablet(context)) {
                 myIntent = new Intent(context, ModuleDetailsMultiPaneActivity.class);

                 myIntent.putExtra(Util.ROW_ID, "1");
                 // myIntent.putExtra(RestUtilConstants.BEAN_ID, cursor.getString(1));                
                 // ModuleDetailFragment mddetails =
                 // ModuleDetailFragment.newInstance(position);
                // ((BaseSinglePaneActivity) this).openActivityOrFragment(myIntent);
             } else {
                 myIntent = new Intent(context, ModulesActivity.class);               
             }
         }
         myIntent.putExtra(RestUtilConstants.MODULE_NAME, moduleName);
         startActivity(myIntent);
    }

    private Intent createSettingsIntent() {
        Intent myIntent = new Intent(DashboardFragment.this.getActivity(), SugarCrmSettings.class);
        myIntent.putExtra(RestUtilConstants.MODULE_NAME, "settings");
        return myIntent;
    }
    
    
    private class DashboardAdapter extends BaseAdapter {
    	
    	
    	private Context mContext;
    	private ArrayList<String> mList = new ArrayList<String>();
    		
    	public DashboardAdapter(Context context) {
    		mContext = context;
    	}

    	@Override
    	public int getCount() {
    		return mList.size();
    	}

    	@Override
    	public Object getItem(int arg0) {
    		// TODO Auto-generated method stub
    		return null;
    	}
    	
    	public void addItem(String item){
    		mList.add(item);
    	}
    	
    	public void addItems(List<String> items){
    		mList.addAll(items);
    	}
    	
    	public void removeAll(){
    		mList.clear();
    	}

    	@Override
    	public long getItemId(int position) {
    		// TODO Auto-generated method stub
    		return 0;
    	}

    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		
    		
    		LinearLayout ls;
    		
            if (convertView == null) {  // if it's not recycled, initialize some attributes
            	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	ls = (LinearLayout) inflater.inflate(R.layout.dashboard_item, null);           
            } else {
            	ls = (LinearLayout) convertView;
            }
            ImageView imageView = (ImageView) ls.findViewById(R.id.module_image);
            imageView.setImageResource(ContentUtils.getModuleIcon(mList.get(position)));
            
            String moduleName =  mList.get(position);
            
            TextView textView = (TextView)ls.findViewById(R.id.module_name);
            textView.setText(moduleName);        
            
            ls.setTag(moduleName);
                   
            return ls;
    	}

    }

}
