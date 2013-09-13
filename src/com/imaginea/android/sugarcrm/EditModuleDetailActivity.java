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
 * FileName : EditModuleDetailActivity 
 ******************************************************************************/
package com.imaginea.android.sugarcrm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.imaginea.android.sugarcrm.ui.BaseSinglePaneActivity;

/**
 * The Class EditModuleDetailActivity.
 */
public class EditModuleDetailActivity extends BaseSinglePaneActivity {

    @Override
    protected Fragment onCreatePane() {
        return new EditModuleDetailFragment();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.showDialog(R.string.discard);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateDialog(int)
     */
    @Override
    public Dialog onCreateDialog(int id) {

        return new AlertDialog.Builder(this)
                .setTitle(id)
                .setMessage(R.string.discardAlert)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                finish();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {

                            }
                        }).create();
    }
}
