package com.imaginea.android.sugarcrm.tab;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.imaginea.android.sugarcrm.EditModuleDetailActivity;
import com.imaginea.android.sugarcrm.EditModuleDetailFragment;
import com.imaginea.android.sugarcrm.ModuleDetailActivity;
import com.imaginea.android.sugarcrm.ModuleDetailFragment;
import com.imaginea.android.sugarcrm.R;
import com.imaginea.android.sugarcrm.RecentModuleActivity;
import com.imaginea.android.sugarcrm.RecentModuleListFragment;
import com.imaginea.android.sugarcrm.ui.BaseMultiPaneActivity;

/**
 * A multi-pane activity, consisting of a {@link RecentModuleListFragment}, and
 * {@link ModuleDetailFragment}.
 * 
 * This activity requires API level 11 or greater
 */
public class RecentModuleMultiPaneActivity extends BaseMultiPaneActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_modules);

        ModuleDetailFragment moduleDetailFragment = (ModuleDetailFragment) getSupportFragmentManager().findFragmentByTag("module_detail");
        if (moduleDetailFragment == null) {
            moduleDetailFragment = new ModuleDetailFragment();
            moduleDetailFragment.setArguments(intentToFragmentArguments(getIntent()));

             getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_module_detail, moduleDetailFragment, "module_detail").commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();

        ViewGroup detailContainer = (ViewGroup) findViewById(R.id.fragment_container_module_detail);
        if (detailContainer != null && detailContainer.getChildCount() > 0) {
            findViewById(R.id.fragment_container_module_detail).setBackgroundColor(0xffffffff);
        }
    }

    @Override
    public FragmentReplaceInfo onSubstituteFragmentForActivityLaunch(String activityClassName) {
        if (RecentModuleActivity.class.getName().equals(activityClassName)) {
            // this won't happen as Module list fragment is not reloaded
            return new FragmentReplaceInfo(RecentModuleListFragment.class, "modules", R.id.fragment_container_module_detail);
        } else if (ModuleDetailActivity.class.getName().equals(activityClassName)) {
            findViewById(R.id.fragment_container_module_detail).setBackgroundColor(0xffffffff);
            return new FragmentReplaceInfo(ModuleDetailFragment.class, "module_detail", R.id.fragment_container_module_detail);
        } else if (EditModuleDetailActivity.class.getName().equals(activityClassName)) {
            findViewById(R.id.fragment_container_module_detail).setBackgroundColor(0xffffffff);
            return new FragmentReplaceInfo(EditModuleDetailFragment.class, "module_detail", R.id.fragment_container_module_detail);
        }
        return null;
    }

    public void showAssignedItems(View view) {
        // keep this empty as the header is used from list view
        // TODO - action bar and based on UI design
    }

    public void showAllItems(View view) {
        // TODO
    }

    public void showHome(View view) {
        // TODO
    }
}