package com.imaginea.android.sugarcrm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ViewGroup;

import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.sync.SyncConfigActivity;
import com.imaginea.android.sugarcrm.ui.BaseSinglePaneActivity;
import com.imaginea.android.sugarcrm.ui.RecentModuleMultiPaneActivity;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

/**
 * DashboardActivity
 * 
 * @author Vasavi
 */
public class DashboardActivity extends BaseSinglePaneActivity {

    @Override
    protected Fragment onCreatePane() {
        final Fragment fragment = new DashboardFragment();

        return fragment;
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupHomeActivity();
    }

    /**
     * {@inheritDoc}
     * 
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivityForResult(new Intent(this, WizardAuthActivity.class),
                Util.LOGIN_REQUEST_CODE);

    }

    /** {@inheritDoc} */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /** {@inheritDoc} */
    @Override
    protected void onResume() {
        super.onResume();
        final String usr = SugarCrmSettings.getUsername(this);
        if (usr == null) {
            startActivityForResult(new Intent(this, WizardAuthActivity.class),
                    Util.LOGIN_REQUEST_CODE);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onActivityResult(final int requestCode,
            final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case Util.LOGIN_REQUEST_CODE:
            if (resultCode == RESULT_CANCELED) {
                finish();
                return;
            }
            if (resultCode == RESULT_OK) {
                final SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(DashboardActivity.this);
                final Long syncScreenCheck = prefs.getLong(
                        Util.PREF_SYNC_START_TIME, 0L);
                if (syncScreenCheck == 0L) {
                    startActivityForResult(new Intent(this,
                            SyncConfigActivity.class),
                            Util.SYNC_DATA_REQUEST_CODE);
                } else {
                    showDashboard();
                }
            }
            break;

        case Util.SYNC_DATA_REQUEST_CODE:
            // whatever is the result code, we take the user to dashboard
            // we have the module list after the login, so get them and store
            showDashboard();
            break;
        default:
            break;
        }
    }

    void showDashboard() {
        Log.d("DashbooardActivity", "show dashboard called");
        if (ViewUtil.isHoneycombTablet(DashboardActivity.this)) {
            final Intent myIntent = new Intent(DashboardActivity.this,
                    RecentModuleMultiPaneActivity.class);
            myIntent.putExtra(RestConstants.MODULE_NAME, Util.RECENT);
            startActivity(myIntent);
        } else {
            final ViewGroup root = (ViewGroup) findViewById(R.id.home);
            root.requestLayout();
        }

    }

}
