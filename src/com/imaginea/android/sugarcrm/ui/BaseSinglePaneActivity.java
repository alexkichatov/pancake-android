/*******************************************************************************
 * Copyright (c) 2013 Asha
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:Asha - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : BaseSinglePaneActivity 
 * Description : 
 *              A {@link BaseActivity} that simply contains a single fragment. The intent
 * used to invoke this activity is forwarded to the fragment as arguments during
 * fragment instantiation. Derived activities should only need to implement
 * {@link com.google.android.apps.iosched.ui.BaseSinglePaneActivity#onCreatePane()}
 ******************************************************************************/

package com.imaginea.android.sugarcrm.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.imaginea.android.sugarcrm.R;

/**
 * The Class BaseSinglePaneActivity.
 */
public abstract class BaseSinglePaneActivity extends BaseActivity {
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepane_empty);
        getActivityHelper().setupActionBar(getTitle(), 0);

        if (savedInstanceState == null) {
            mFragment = onCreatePane();
            mFragment.setArguments(intentToFragmentArguments(getIntent()));

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.root_container, mFragment).commit();
        }
    }

    /**
     * Called in <code>onCreate</code> when the fragment constituting this
     * activity is needed. The returned fragment's arguments will be set to the
     * intent used to invoke this activity.
     * 
     * @return the fragment
     */
    protected abstract Fragment onCreatePane();
}
