/*******************************************************************************
 * Copyright (c) 2013 Asha, Muralidaran.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Asha, Muralidaran - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : RecentModuleActivity 
 *  Description :
 *              RecentModuleActivity is used to lists the view projections for recent records
 ******************************************************************************/

package com.imaginea.android.sugarcrm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.ui.BaseActivity;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * RecentModuleActivity.
 */
public class RecentModuleActivity extends BaseActivity {

    /** The menu layout. */
    private LinearLayout mMenuLayout;

    /** The m fragment. */
    private Fragment mFragment;

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_modules);
        if (savedInstanceState == null) {
            mFragment = new RecentModuleListFragment();
            mFragment.setArguments(intentToFragmentArguments(getIntent()));
        }
        mMenuLayout = (LinearLayout) findViewById(R.id.settings_menu);
        final TextView settingsView = (TextView) findViewById(R.id.settingsTv);
        settingsView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* Set Action to Settings Button */
                Util.startSettingsActivity(RecentModuleActivity.this);
                mMenuLayout.setVisibility(View.GONE);
            }
        });
        final TextView logoutView = (TextView) findViewById(R.id.logoutTv);
        logoutView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* Set Action to Settings Button */

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        final String sessionId = ((SugarCrmApp) SugarCrmApp.mApp)
                                .getSessionId();
                        final String restUrl = SugarCrmSettings
                                .getSugarRestUrl(RecentModuleActivity.this);
                        try {
                            Rest.logoutSugarCRM(restUrl, sessionId);
                        } catch (final SugarCrmException e) {
                            Log.e("TAG", "Exception found " + e);
                        }
                    }
                }).start();
                Util.logout(RecentModuleActivity.this);
                mMenuLayout.setVisibility(View.GONE);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.imaginea.android.sugarcrm.ui.BaseActivity#onKeyDown(int,
     * android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch (keycode) {
        case KeyEvent.KEYCODE_MENU:
            if (!(mMenuLayout.getVisibility() == View.VISIBLE)) {
                mMenuLayout.bringToFront();
                mMenuLayout.setVisibility(View.VISIBLE);
                final View transparentView = findViewById(R.id.transparent_view);

                transparentView.setVisibility(View.VISIBLE);
            } else {
                mMenuLayout.setVisibility(View.INVISIBLE);
            }
            return true;
        }

        return super.onKeyDown(keycode, e);
    }
}
