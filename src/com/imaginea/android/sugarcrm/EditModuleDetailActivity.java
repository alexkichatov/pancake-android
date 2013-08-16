package com.imaginea.android.sugarcrm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.showDialog(R.string.discard);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

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