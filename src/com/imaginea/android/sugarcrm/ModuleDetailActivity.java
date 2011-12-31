package com.imaginea.android.sugarcrm;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.imaginea.android.sugarcrm.ui.BaseSinglePaneActivity;

/**
 * ModuleDetailActivity, used on phones to display the details of a Module
 * 
 */
public class ModuleDetailActivity extends BaseSinglePaneActivity {

    @Override
    protected Fragment onCreatePane() {
        return new ModuleDetailFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }
}
