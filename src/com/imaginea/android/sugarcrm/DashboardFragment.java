package com.imaginea.android.sugarcrm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.tab.ModuleDetailsMultiPaneActivity;
import com.imaginea.android.sugarcrm.ui.BaseSinglePaneActivity;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;
import com.imaginea.android.sugarcrm.CustomActionbar.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * DashboardFragment
 * 
 */
public class DashboardFragment extends Fragment {

    private List<String> mModuleNames;

    private DatabaseHelper mDbHelper;
    
    private ViewGroup root;
             
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dashboard_fragment, container, false);
        return root;
    }
    @Override
    public void onResume() {
    	super.onResume();
    	if(mModuleNames.size() < 2) {
    		
    		if(root != null)
    			root.removeAllViews();
    		
    		mModuleNames = mDbHelper.getModuleList();
    		mModuleNames.add(getString(R.string.recent));
			//mModuleNames.add(getString(R.string.settings));
    		Collections.sort(mModuleNames);
    		
    		for (Iterator<String> iterator = mModuleNames.iterator(); iterator.hasNext();) {
                Button view = (Button)LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.dashboard_item, null, false);
                String moduleName = (String) iterator.next();
                view.setText(moduleName);
                view.setCompoundDrawablesWithIntrinsicBounds(0, mDbHelper.getModuleIcon(moduleName), 0, 0);
                view.setTag(moduleName);
                view.setClickable(true);
                root.addView(view);
            }
    	}
    }
    @Override
    public void onViewCreated(View fragView, Bundle savedInstanceState) {
        super.onViewCreated(fragView, savedInstanceState);
       /* 
        ImageView imgview =  (ImageView)fragView.findViewById(R.id.one);
        imgview.setImageResource(R.drawable.menu_settings);
        imgview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(DashboardFragment.this.getActivity(), SugarCrmSettings.class);
				myIntent.putExtra(RestUtilConstants.MODULE_NAME, "settings");
				DashboardFragment.this.startActivity(myIntent);
			}
		});*/
        root = (ViewGroup) fragView.findViewById(R.id.dashboard);
        mDbHelper = new DatabaseHelper(getActivity().getBaseContext());
        mModuleNames = mDbHelper.getModuleList();
        //mModuleNames.add(getString(R.string.settings));
        mModuleNames.add(getString(R.string.recent));
        Collections.sort(mModuleNames);
        // ViewGroup dashboardLayout = (ViewGroup)root.findViewById(R.id.dashboard);

        for (Iterator<String> iterator = mModuleNames.iterator(); iterator.hasNext();) {
            //View view = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.dashboard_item, null, false);
            Button view = (Button)LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.dashboard_item, null, false);
            String moduleName = (String) iterator.next();
            view.setText(moduleName);
            view.setCompoundDrawablesWithIntrinsicBounds(0, mDbHelper.getModuleIcon(moduleName), 0, 0);
            //Button b = new Button(getActivity());
            //b.set
            //ImageView iv = (ImageView) view.findViewById(R.id.moduleImage);
           // iv.setImageResource(mDbHelper.getModuleIcon(moduleName));
           // iv.setTag(moduleName);
           // TextView tv = (TextView) view.findViewById(R.id.moduleName);
           // tv.setText(moduleName);
            view.setTag(moduleName);
            view.setClickable(true);
            root.addView(view);
        }
        // the image view click action is defined in the DashboardActivity
        //root.setActivated(true);
    }

    /** {@inheritDoc} */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        //TextView tv = (TextView) getActivity().findViewById(R.id.headerText);
       // tv.setText("Modules");
        
        final CustomActionbar actionBar = (CustomActionbar) getActivity().findViewById(R.id.custom_actionbar);
        final Action homeAction = new IntentAction(DashboardFragment.this.getActivity(), null, R.drawable.pancake_logo);
        actionBar.setHomeAction(homeAction);  
        actionBar.setTitle("Pancake");
               
        final Action settingAction = new IntentAction(DashboardFragment.this.getActivity(), createSettingsIntent(), R.drawable.settings);
        actionBar.addActionItem(settingAction);
    }

    public void viewModuleList(View view) {
        Intent myIntent;
        String moduleName = (String) view.getTag();
        if (ViewUtil.isHoneycombTablet(getActivity().getBaseContext())) {
            myIntent = new Intent(DashboardFragment.this.getActivity(), ModuleDetailsMultiPaneActivity.class);

            myIntent.putExtra(Util.ROW_ID, "1");
            // myIntent.putExtra(RestUtilConstants.BEAN_ID, cursor.getString(1));
            myIntent.putExtra(RestUtilConstants.MODULE_NAME, moduleName);
            // ModuleDetailFragment mddetails =
            // ModuleDetailFragment.newInstance(position);
            ((BaseSinglePaneActivity) getActivity()).openActivityOrFragment(myIntent);
        } else {
            myIntent = new Intent(DashboardFragment.this.getActivity(), ModulesActivity.class);
            myIntent.putExtra(Util.ROW_ID, "1");
            // myIntent.putExtra(RestUtilConstants.BEAN_ID, cursor.getString(1));
            myIntent.putExtra(RestUtilConstants.MODULE_NAME, moduleName);
            // this.getActivity().startActivity(myIntent);
            ((BaseSinglePaneActivity) getActivity()).openActivityOrFragment(myIntent);
        }
    }
    
    private Intent createSettingsIntent() {    	
    	Intent myIntent = new Intent(DashboardFragment.this.getActivity(), SugarCrmSettings.class);
		myIntent.putExtra(RestUtilConstants.MODULE_NAME, "settings");		
		return myIntent;
    }
    
}
