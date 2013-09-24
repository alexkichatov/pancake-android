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
 * FileName : ModuleListFragment 
 ******************************************************************************/

package com.imaginea.android.sugarcrm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.ModuleImageListFragment.OnItemSelectedListener;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.ui.BaseActivity;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * ModulesActivity.
 */
public class ModulesActivity extends BaseActivity implements
        OnItemSelectedListener {

    /** The menu layout. */
    private LinearLayout menuLayout;

    /** The module list fragment. */
    private ListFragment moduleListFragment;

    /** The m fragment. */
    private Fragment mFragment;

    /** The m module name. */
    private String mModuleName;
    private static final String TAG = ModulesActivity.class.getSimpleName();

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modules);
        menuLayout = (LinearLayout) findViewById(R.id.settings_menu);

        mModuleName = getIntent().getStringExtra(RestConstants.MODULE_NAME);
        /* Stub Only For Test Cases */
        if (mModuleName == null) {
            mModuleName = Util.ACCOUNTS;
        }
        if (mModuleName.equalsIgnoreCase(Util.RECENT)) {
            menuLayout.findViewById(R.id.orderByListContainer).setVisibility(
                    View.GONE);

            moduleListFragment = new RecentModuleListFragment();
            final FragmentTransaction ft = getSupportFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.list_frag_holder, moduleListFragment);
            ft.commit();

        } else {
            moduleListFragment = new ModuleListFragment();
            final FragmentTransaction ft = getSupportFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.list_frag_holder, moduleListFragment);
            ft.commit();
        }

        if (savedInstanceState == null) {
            if (mModuleName.equalsIgnoreCase(Util.RECENT)) {
                mFragment = new RecentModuleListFragment();
            } else {
                mFragment = new ModuleListFragment();
            }
            mFragment.setArguments(intentToFragmentArguments(getIntent()));
        }

        final TextView settingsView = (TextView) findViewById(R.id.settingsTv);

        settingsView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* Set Action to Settings Button */
                Util.startSettingsActivity(ModulesActivity.this);
                menuLayout.setVisibility(View.GONE);

                /* Hiding the Transparent view when click on Menu Tap */
                final View transParentView = findViewById(R.id.transparent_view);
                transParentView.setVisibility(View.GONE);

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
                                .getSugarRestUrl(ModulesActivity.this);
                        try {
                            Rest.logoutSugarCRM(restUrl, sessionId);
                        } catch (final SugarCrmException e) {
                            Log.e(TAG, "Error While Logging out:" + e);
                        }
                    }
                }).start();
                Util.logout(ModulesActivity.this);
                menuLayout.setVisibility(View.GONE);
            }
        });

        final ImageView dashboardView = (ImageView) findViewById(R.id.actionbar_logo);
        dashboardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLayout.findViewById(R.id.orderByListContainer)
                        .setVisibility(View.GONE);

                final TextView addNewView = (TextView) findViewById(R.id.addNew);
                addNewView.setVisibility(View.GONE);
                getIntent().putExtra(RestConstants.MODULE_NAME, Util.RECENT);
                moduleListFragment = new RecentModuleListFragment();
                final FragmentTransaction ft = getSupportFragmentManager()
                        .beginTransaction();
                ft.replace(R.id.list_frag_holder, moduleListFragment);
                ft.replace(R.id.list_imagefrag, new ModuleImageListFragment());
                ft.commit();
            }
        });

    }

    public void showAssignedItems(final View view) {

    }

    public void showAllItems(final View view) {
    }

    public void showHome(final View view) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.imaginea.android.sugarcrm.ModuleImageListFragment.OnItemSelectedListener
     * #onItemSelected(java.lang.String)
     */
    @Override
    public void onItemSelected(final String moduleName) {
        final TextView addNewView = (TextView) findViewById(R.id.addNew);
        addNewView.setVisibility(View.VISIBLE);
        menuLayout.findViewById(R.id.orderByListContainer).setVisibility(
                View.VISIBLE);
        addNewView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Intent myIntent = new Intent(ModulesActivity.this,
                        EditModuleDetailActivity.class);
                myIntent.putExtra(RestConstants.MODULE_NAME, moduleName);
                startActivity(myIntent);
                menuLayout.setVisibility(View.GONE);
            }
        });

        getIntent().putExtra(RestConstants.MODULE_NAME, moduleName);
        moduleListFragment = new ModuleListFragment();
        final FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction();
        ft.replace(R.id.list_frag_holder, moduleListFragment);
        ft.commit();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.imaginea.android.sugarcrm.ui.BaseActivity#onKeyDown(int,
     * android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        final View transparentView = findViewById(R.id.transparent_view);

        switch (keycode) {
        case KeyEvent.KEYCODE_MENU:
            if (!(menuLayout.getVisibility() == View.VISIBLE)) {
                menuLayout.setVisibility(View.VISIBLE);

                transparentView.setVisibility(View.VISIBLE);
            } else {
                menuLayout.setVisibility(View.INVISIBLE);
                transparentView.setVisibility(View.INVISIBLE);
            }
            return true;
        }

        return super.onKeyDown(keycode, e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {

        if (moduleListFragment instanceof ModuleListFragment
                && ((ModuleListFragment) moduleListFragment).bSearch) {

            final SearchView searchView = (SearchView) findViewById(R.id.searchView);
            final ImageView backView = (ImageView) findViewById(R.id.actionbar_back);
            final ImageView logoview = (ImageView) findViewById(R.id.actionbar_logo);
            searchView.clearFocus();
            searchView.setVisibility(View.GONE);
            searchView.setQuery("", false);
            backView.setVisibility(View.GONE);
            logoview.setVisibility(View.VISIBLE);
            ((ModuleListFragment) moduleListFragment).setUpActionBar();
            ((ModuleListFragment) moduleListFragment).bSearch = false;

        } else if (moduleListFragment instanceof ModuleListFragment) {
            menuLayout.findViewById(R.id.orderByListContainer).setVisibility(
                    View.GONE);
            getIntent().putExtra(RestConstants.MODULE_NAME, Util.RECENT);
            moduleListFragment = new RecentModuleListFragment();
            final FragmentTransaction ft = getSupportFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.list_frag_holder, moduleListFragment);
            ft.replace(R.id.list_imagefrag, new ModuleImageListFragment());
            ft.commit();

        } else {
            finish();
        }
    }
}
