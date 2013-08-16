package com.imaginea.android.sugarcrm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.imaginea.android.sugarcrm.EditModuleDetailActivity;
import com.imaginea.android.sugarcrm.EditModuleDetailFragment;
import com.imaginea.android.sugarcrm.ModuleDetailActivity;
import com.imaginea.android.sugarcrm.ModuleDetailFragment;
import com.imaginea.android.sugarcrm.ModuleListFragment;
import com.imaginea.android.sugarcrm.ModulesActivity;
import com.imaginea.android.sugarcrm.R;

/**
 * A multi-pane activity, consisting of a {@link ModuleListFragment}, and
 * {@link ModuleDetailFragment}.
 * 
 * This activity requires API level 11 or greater
 */
public class ModuleDetailsMultiPaneActivity extends BaseMultiPaneActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modules);

        ModuleDetailFragment moduleDetailFragment = (ModuleDetailFragment) getSupportFragmentManager()
                .findFragmentByTag("module_detail");
        if (moduleDetailFragment == null) {
            moduleDetailFragment = new ModuleDetailFragment();
            moduleDetailFragment
                    .setArguments(intentToFragmentArguments(getIntent()));

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container_module_detail,
                            moduleDetailFragment, "module_detail").commit();

        }

    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();

        final ViewGroup detailContainer = (ViewGroup) findViewById(R.id.fragment_container_module_detail);
        if (detailContainer != null && detailContainer.getChildCount() > 0) {
            findViewById(R.id.fragment_container_module_detail)
                    .setBackgroundColor(0xffffffff);
        }
    }

    @Override
    public FragmentReplaceInfo onSubstituteFragmentForActivityLaunch(
            final String activityClassName) {
        if (ModulesActivity.class.getName().equals(activityClassName))
            return new FragmentReplaceInfo(ModuleListFragment.class, "modules",
                    R.id.fragment_container_module_detail);
        else if (ModuleDetailActivity.class.getName().equals(activityClassName)) {
            findViewById(R.id.fragment_container_module_detail)
                    .setBackgroundColor(0xffffffff);
            return new FragmentReplaceInfo(ModuleDetailFragment.class,
                    "module_detail", R.id.fragment_container_module_detail);
        } else if (EditModuleDetailActivity.class.getName().equals(
                activityClassName)) {
            findViewById(R.id.fragment_container_module_detail)
                    .setBackgroundColor(0xffffffff);
            return new FragmentReplaceInfo(EditModuleDetailFragment.class,
                    "module_detail", R.id.fragment_container_module_detail);
        }
        return null;
    }

    public void showAssignedItems(final View view) {
        // keep this empty as the header is used from list view
        // TODO - action bar and based on UI design
    }

    public void showAllItems(final View view) {
        // TODO
    }

    public void showHome(final View view) {
        // TODO
    }

    @Override
    public void onBackPressed() {
        finish();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
       
    }
}