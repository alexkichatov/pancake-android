/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imaginea.android.sugarcrm.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.DashboardActivity;
import com.imaginea.android.sugarcrm.R;

/**
 * A class that handles some common activity-related functionality in the app, such as setting up
 * the action bar. This class provides functionality useful for both phones and tablets, and does
 * not require any Android 3.0-specific features.
 * 
 * Modified 
 */
public class ActivityHelper {
    protected Activity mActivity;

    /**
     * Factory method for creating {@link ActivityHelper} objects for a given activity. Depending on
     * which device the app is running, either a basic helper or Honeycomb-specific helper will be
     * returned.
     */
    public static ActivityHelper createInstance(Activity activity) {
        return ViewUtil.isHoneycomb() ? new ActivityHelperHoneycomb(activity)
                                        : new ActivityHelper(activity);
    }

    protected ActivityHelper(Activity activity) {
        mActivity = activity;
    }

    public void onPostCreate(Bundle savedInstanceState) {
        // Create the action bar
        SimpleMenu menu = new SimpleMenu(mActivity);
        mActivity.onCreatePanelMenu(Window.FEATURE_OPTIONS_PANEL, menu);
        // TODO: call onPreparePanelMenu here as well
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            //addActionButtonCompatFromMenuItem(item);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // mActivity.getMenuInflater().inflate(R.menu.default_menu_items, menu);
        // TODO
        mActivity.getMenuInflater().inflate(R.menu.settings_menu, menu);
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.search:
            goSearch();
            return true;
        }
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return false;
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goHome();
            return true;
        }
        return false;
    }

    /**
     * Method, to be called in <code>onPostCreate</code>, that sets up this activity as the home
     * activity for the app.
     */
    public void setupHomeActivity() {
    }

    /**
     * Method, to be called in <code>onPostCreate</code>, that sets up this activity as a
     * sub-activity in the app.
     */
    public void setupSubActivity() {
    }

    /**
     * Invoke "home" action, returning to {@link com.google.android.apps.iosched.ui.HomeActivity}.
     */
    public void goHome() {
        if (mActivity instanceof DashboardActivity) {
            return;
        }

        final Intent intent = new Intent(mActivity, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mActivity.startActivity(intent);      
    }

    /**
     * Invoke "search" action, triggering a default search.
     */
    public void goSearch() {
        mActivity.startSearch(null, false, Bundle.EMPTY, false);
    }

    /**
     * Sets up the action bar 
     */
    public void setupActionBar(CharSequence title, int color) {
        //TODO
    }

    /**
     * Returns the {@link ViewGroup} for the action bar on phones (compatibility action bar). Can
     * return null, and will return null on Honeycomb.
     */
    public ViewGroup getActionBarCompat() {
        return (ViewGroup) mActivity.findViewById(R.id.actionbar_compat);
    }
}
