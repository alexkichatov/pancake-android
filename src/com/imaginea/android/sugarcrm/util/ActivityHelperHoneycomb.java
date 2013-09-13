/*******************************************************************************
 * Copyright (c) 2013 Asha, Murli.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Asha, Muralidaran - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : ActivityHelperHoneycomb 
 * Description :
 *            An extension of {@link ActivityHelper} that provides Android 3.0-specific
 * functionality for Honeycomb tablets. It thus requires API level 11.
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * The Class ActivityHelperHoneycomb.
 */
public class ActivityHelperHoneycomb extends ActivityHelper {

    /**
     * Instantiates a new activity helper honeycomb.
     * 
     * @param activity
     *            the activity
     */
    protected ActivityHelperHoneycomb(Activity activity) {
        super(activity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.imaginea.android.sugarcrm.util.ActivityHelper#onPostCreate(android
     * .os.Bundle)
     */
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        // Do nothing in onPostCreate. ActivityHelper creates the old action
        // bar, we don't need to for Honeycomb.
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.imaginea.android.sugarcrm.util.ActivityHelper#onOptionsItemSelected
     * (android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
