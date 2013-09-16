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
 * FileName : ModuleDetailActivity 
 * Description : ModuleDetailActivity, used on phones to display the details of a Module
 ******************************************************************************/

package com.imaginea.android.sugarcrm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.imaginea.android.sugarcrm.ui.BaseSinglePaneActivity;

/**
 * The Class ModuleDetailActivity.
 */
public class ModuleDetailActivity extends BaseSinglePaneActivity {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.imaginea.android.sugarcrm.ui.BaseSinglePaneActivity#onCreatePane()
     */
    @Override
    protected Fragment onCreatePane() {
        return new ModuleDetailFragment();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.imaginea.android.sugarcrm.ui.BaseActivity#onPostCreate(android.os
     * .Bundle)
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
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

        final LinearLayout menuLayout = (LinearLayout) findViewById(R.id.settings_menu);

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
}
