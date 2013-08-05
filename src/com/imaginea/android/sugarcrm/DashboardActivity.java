package com.imaginea.android.sugarcrm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ViewGroup;

import com.imaginea.android.sugarcrm.sync.SyncConfigActivity;
import com.imaginea.android.sugarcrm.ui.BaseSinglePaneActivity;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * DashboardActivity
 * 
 * @author Vasavi
 */
public class DashboardActivity extends BaseSinglePaneActivity {

    // private GridView mDashboard;

    // private DatabaseHelper mDbHelper;

    // private ProgressDialog mProgressDialog;

    @Override
    protected Fragment onCreatePane() {
        final Fragment fragment = new DashboardFragment();

        return fragment;
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // showDashboard();
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
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView(R.layout.dashboard_activity);

        // mDbHelper = new DatabaseHelper(this);

        // Class wizardActivity = WizardDetector.getClass(getBaseContext());

        startActivityForResult(new Intent(this, WizardAuthActivity.class),
                Util.LOGIN_REQUEST_CODE);

        // super.onCreate(savedInstanceState);
        // TextView tv = (TextView) findViewById(R.id.headerText);
        // tv.setText(R.string.home);
        /*
         * mDashboard = (GridView) findViewById(R.id.dashboard);
         * 
         * mDashboard.setOnItemClickListener(new OnItemClickListener() { public
         * void onItemClick(AdapterView<?> parent, View v, int position, long
         * id) {
         * 
         * mProgressDialog = ViewUtil.getProgressDialog(DashboardActivity.this,
         * getString(R.string.loading), false); mProgressDialog.show();
         * 
         * // invoke the corresponding activity when the item in the // GridView
         * is clicked Intent myIntent; String moduleName =
         * mModuleNames.get(position); if
         * (moduleName.equals(getString(R.string.settings))) { myIntent = new
         * Intent(DashboardActivity.this, SugarCrmSettings.class); } else if
         * (moduleName.equals(getString(R.string.recent))) { // TODO if
         * (ViewUtil.isHoneycombTablet(getBaseContext())) { myIntent = new
         * Intent(DashboardActivity.this, RecentModuleMultiPaneActivity.class);
         * } else { myIntent = new Intent(DashboardActivity.this,
         * RecentModuleActivity.class); } } else { // TODO if
         * (ViewUtil.isHoneycombTablet(getBaseContext())) { myIntent = new
         * Intent(DashboardActivity.this, ModuleDetailsMultiPaneActivity.class);
         * 
         * myIntent.putExtra(Util.ROW_ID, "1"); //
         * myIntent.putExtra(RestUtilConstants.BEAN_ID, cursor.getString(1));
         * myIntent.putExtra(RestUtilConstants.MODULE_NAME, moduleName);
         * 
         * 
         * } }
         * 
         * myIntent.putExtra(RestUtilConstants.MODULE_NAME,
         * mModuleNames.get(position));
         * DashboardActivity.this.startActivity(myIntent); } });
         */
    }

    /** {@inheritDoc} */
    @Override
    protected void onPause() {
        super.onPause();
        /*
         * if (mProgressDialog != null) { mProgressDialog.cancel();
         * mProgressDialog = null; }
         */
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
                    // setContentView(R.layout.dashboard_activity);
                    showDashboard();
                }
            }
            break;

        case Util.SYNC_DATA_REQUEST_CODE:
            // whatever is the result code, we take the user to dashboard
            // we have the module list after the login, so get them and store
            // setContentView(R.layout.dashboard_activity);
            showDashboard();
            break;
        default:
            break;
        }
    }

    void showDashboard() {
        /*
         * mModuleNames = mDbHelper.getModuleList();
         * mModuleNames.add(getString(R.string.settings));
         * mModuleNames.add(getString(R.string.recent));
         * Collections.sort(mModuleNames); mDashboard.setAdapter(new
         * AppsAdapter(this));
         */
        // setContentView(R.layout.dashboard_activity);
        Log.d("DashbooardActivity", "show dashboard called");
        final ViewGroup root = (ViewGroup) findViewById(R.id.home);
        // root.setActivated(true);
        root.requestLayout();

        // DashboardFragment moduleDetailFragment = (DashboardFragment)
        // getSupportFragmentManager().findFragmentById(R.id.fragment_dashboard);
        // moduleDetailFragment.setArguments(intentToFragmentArguments(getIntent()));

    }

    /*
     * public void viewModuleList(View view) { Intent myIntent; String
     * moduleName = (String) view.getTag(); Log.d("onClick moduleName message",
     * moduleName); if (moduleName.equals(getString(R.string.settings))) {
     * myIntent = new Intent(DashboardActivity.this, SugarCrmSettings.class); }
     * else if (moduleName.equals(getString(R.string.recent))) { // TODO if
     * (ViewUtil.isHoneycombTablet(getBaseContext())) { myIntent = new
     * Intent(DashboardActivity.this, RecentModuleMultiPaneActivity.class); }
     * else { myIntent = new Intent(DashboardActivity.this,
     * RecentModuleActivity.class); } } else { if
     * (ViewUtil.isHoneycombTablet(getBaseContext())) { myIntent = new
     * Intent(this, ModuleDetailsMultiPaneActivity.class);
     * 
     * myIntent.putExtra(Util.ROW_ID, "1"); //
     * myIntent.putExtra(RestUtilConstants.BEAN_ID, cursor.getString(1)); //
     * ModuleDetailFragment mddetails = //
     * myIntent.putExtra(RestUtilConstants.MODULE_NAME, moduleName);
     * DashboardActivity.this.startActivity(myIntent); }
     */
    /*
     * public class AppsAdapter extends BaseAdapter { private Context mContext;
     * 
     * public AppsAdapter(Context context) { mContext = context; }
     * 
     * public View getView(int position, View convertView, ViewGroup parent) {
     * View view; // an item in the GridView if (convertView == null) { view =
     * LayoutInflater.from(mContext).inflate(R.layout.dashboard_item, parent,
     * false);
     * 
     * String moduleName = mModuleNames.get(position);
     * 
     * ImageView iv = (ImageView) view.findViewById(R.id.moduleImage);
     * iv.setImageResource(mDbHelper.getModuleIcon(moduleName));
     * 
     * TextView tv = (TextView) view.findViewById(R.id.moduleName);
     * tv.setText(moduleName); } else { view = convertView; }
     * 
     * return view; }
     * 
     * public final int getCount() { return mModuleNames.size(); }
     * 
     * public final Object getItem(int position) { return null; }
     * 
     * public final long getItemId(int position) { return 0; } }
     */
}
