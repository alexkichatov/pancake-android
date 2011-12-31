package com.imaginea.android.sugarcrm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.imaginea.android.sugarcrm.ui.BaseSinglePaneActivity;

/**
 * 
 * RecentModuleActivity
 * 
 */
public class RecentModuleActivity extends BaseSinglePaneActivity {
    @Override
    protected Fragment onCreatePane() {
        return new RecentModuleListFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
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
