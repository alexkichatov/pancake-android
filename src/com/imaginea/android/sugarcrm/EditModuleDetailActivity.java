package com.imaginea.android.sugarcrm;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.imaginea.android.sugarcrm.ui.BaseSinglePaneActivity;

/**
 * EditModuleDetailActivity
 * 
 */
public class EditModuleDetailActivity extends BaseSinglePaneActivity {

    @Override
    protected Fragment onCreatePane() {
        return new EditModuleDetailFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }
}