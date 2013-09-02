/*******************************************************************************
 * Copyright (c) 2013 Asha
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Asha - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : BaseActivity 
 * Description : 
 *              A base activity that defers common functionality across app activities to an
 * {@link ActivityHelper}. This class shouldn't be used directly; instead,
 * activities should inherit from {@link BaseSinglePaneActivity} or
 * {@link BaseMultiPaneActivity}.
 ******************************************************************************/

package com.imaginea.android.sugarcrm.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.imaginea.android.sugarcrm.util.ActivityHelper;

/**
 * The Class BaseActivity.
 */
public abstract class BaseActivity extends FragmentActivity {
    final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPostCreate(android.os.Bundle)
     */
    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onKeyLongPress(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyLongPress(final int keyCode, final KeyEvent event) {
        return mActivityHelper.onKeyLongPress(keyCode, event)
                || super.onKeyLongPress(keyCode, event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onKeyDown(int,
     * android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        return mActivityHelper.onKeyDown(keyCode, event)
                || super.onKeyDown(keyCode, event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return mActivityHelper.onCreateOptionsMenu(menu)
                || super.onCreateOptionsMenu(menu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return mActivityHelper.onOptionsItemSelected(item)
                || super.onOptionsItemSelected(item);
    }

    /**
     * Returns the {@link ActivityHelper} object associated with this activity.
     * 
     * @return the activity helper
     */
    protected ActivityHelper getActivityHelper() {
        return mActivityHelper;
    }

    /**
     * Takes a given intent and either starts a new activity to handle it (the
     * default behavior), or creates/updates a fragment (in the case of a
     * multi-pane activity) that can handle the intent.
     * 
     * Must be called from the main (UI) thread.
     * 
     * @param intent
     *            the intent
     */
    public void openActivityOrFragment(final Intent intent) {
        // Default implementation simply calls startActivity
        startActivity(intent);
    }

    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment
     * arguments.
     * 
     * @param intent
     *            the intent
     * @return the bundle
     */
    public static Bundle intentToFragmentArguments(final Intent intent) {
        final Bundle arguments = new Bundle();
        if (intent == null)
            return arguments;

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    /**
     * Converts a fragment arguments bundle into an intent.
     * 
     * @param arguments
     *            the arguments
     * @return the intent
     */
    public static Intent fragmentArgumentsToIntent(final Bundle arguments) {
        final Intent intent = new Intent();
        if (arguments == null)
            return intent;

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        return intent;
    }
}
