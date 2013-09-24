/*******************************************************************************
 * Copyright (c) 2013 Asha, Murli.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Asha, Muralidaran - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : ActivityHelper 
 * Description :
 *            A class that handles some common activity-related functionality in the app,
 * such as setting up the action bar. This class provides functionality useful
 * for both phones and tablets, and does not require any Android 3.0-specific
 * features
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;

import com.imaginea.android.sugarcrm.R;

/**
 * The Class ActivityHelper.
 */
public class ActivityHelper {

    /** The m activity. */
    private final Activity mActivity;

    /**
     * Factory method for creating {@link ActivityHelper} objects for a given
     * activity. Depending on which device the app is running, either a basic
     * helper or Honeycomb-specific helper will be returned.
     * 
     * @param activity
     *            the activity
     * @return the activity helper
     */
    public static ActivityHelper createInstance(final Activity activity) {
        return ViewUtil.isHoneycomb() ? new ActivityHelperHoneycomb(activity)
                : new ActivityHelper(activity);
    }

    /**
     * Instantiates a new activity helper.
     * 
     * @param activity
     *            the activity
     */
    protected ActivityHelper(final Activity activity) {
        mActivity = activity;
    }

    /**
     * On post create.
     * 
     * @param savedInstanceState
     *            the saved instance state
     */
    public void onPostCreate(final Bundle savedInstanceState) {
        // Create the action bar
        final SimpleMenu menu = new SimpleMenu(mActivity);
        mActivity.onCreatePanelMenu(Window.FEATURE_OPTIONS_PANEL, menu);

    }

    /**
     * On create options menu.
     * 
     * @param menu
     *            the menu
     * @return true, if successful
     */
    public boolean onCreateOptionsMenu(final Menu menu) {
        mActivity.getMenuInflater().inflate(R.menu.settings_menu, menu);
        return false;
    }

    /**
     * On options item selected.
     * 
     * @param item
     *            the item
     * @return true, if successful
     */
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.search:
            goSearch();
            return true;
        }
        return false;
    }

    /**
     * On key down.
     * 
     * @param keyCode
     *            the key code
     * @param event
     *            the event
     * @return true, if successful
     */
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return false;
    }

    /**
     * On key long press.
     * 
     * @param keyCode
     *            the key code
     * @param event
     *            the event
     * @return true, if successful
     */
    public boolean onKeyLongPress(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    /**
     * Method, to be called in <code>onPostCreate</code>, that sets up this
     * activity as the home activity for the app.
     */
    public void setupHomeActivity() {
    }

    /**
     * Method, to be called in <code>onPostCreate</code>, that sets up this
     * activity as a sub-activity in the app.
     */
    public void setupSubActivity() {
    }

    /**
     * Invoke "search" action, triggering a default search.
     */
    public void goSearch() {
        mActivity.startSearch(null, false, Bundle.EMPTY, false);
    }

    /**
     * Sets up the action bar.
     * 
     * @param title
     *            the title
     * @param color
     *            the color
     */
    public void setupActionBar(final CharSequence title, final int color) {
        // TODO
    }

    /**
     * Returns the {@link ViewGroup} for the action bar on phones (compatibility
     * action bar). Can return null, and will return null on Honeycomb.
     * 
     * @return the action bar compat
     */
    public ViewGroup getActionBarCompat() {
        return (ViewGroup) mActivity.findViewById(R.id.actionbar_compat);
    }
}
