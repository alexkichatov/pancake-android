package com.imaginea.android.sugarcrm;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.CustomActionbar.Action;
import com.imaginea.android.sugarcrm.CustomActionbar.IntentAction;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.ui.ModuleDetailsMultiPaneActivity;
import com.imaginea.android.sugarcrm.ui.RecentModuleMultiPaneActivity;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

/**
 * DashboardFragment
 * 
 */
public class DashboardFragment extends Fragment {

    private List<String> mModuleNames;

    private DashboardAdapter mAdapter;

    private View root;

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.dashboard_fragment, container, false);

        mAdapter = new DashboardAdapter(getActivity());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mModuleNames.size() < 2) {

            if (mAdapter != null) {
                mAdapter.removeAll();
            }
            mModuleNames = ContentUtils.getModuleList(getActivity());
            mModuleNames.add(getString(R.string.recent));

            mAdapter.addItems(mModuleNames);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final CustomActionbar actionBar = (CustomActionbar) getActivity()
                .findViewById(R.id.custom_actionbar);
        final Action homeAction = new IntentAction(
                DashboardFragment.this.getActivity(), null,
                R.drawable.pancake_logo);
        actionBar.setHomeAction(homeAction);
        actionBar.setTitle("Pancake");

        final Action settingAction = new IntentAction(
                DashboardFragment.this.getActivity(), createSettingsIntent(),
                R.drawable.settings);
        actionBar.addActionItem(settingAction);

        final GridView gridView = (GridView) root.findViewById(R.id.gridview);
        gridView.setAdapter(mAdapter);

        mModuleNames = ContentUtils.getModuleList(getActivity());
        mModuleNames.add(getString(R.string.recent));

        mAdapter.addItems(mModuleNames);

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> arg0, final View view,
                    final int arg2, final long arg3) {
                viewModuleList(view);
            }

        });

    }

    public void viewModuleList(final View view) {
        Intent myIntent;
        final String moduleName = (String) view.getTag();
        Log.d("onClick moduleName message", moduleName);

        final Context context = getActivity();

        if (moduleName.equals(getString(R.string.settings))) {
            myIntent = new Intent(context, SugarCrmSettings.class);
        } else if (moduleName.equals(getString(R.string.recent))) {
            // TODO
            if (ViewUtil.isHoneycombTablet(context)) {
                myIntent = new Intent(context,
                        RecentModuleMultiPaneActivity.class);
            } else {
                myIntent = new Intent(context, RecentModuleActivity.class);
            }
        } else {
            if (ViewUtil.isHoneycombTablet(context)) {
                myIntent = new Intent(context,
                        ModuleDetailsMultiPaneActivity.class);

                myIntent.putExtra(Util.ROW_ID, "1");

            } else {
                myIntent = new Intent(context, ModulesActivity.class);
            }
        }
        myIntent.putExtra(RestConstants.MODULE_NAME, moduleName);
        startActivity(myIntent);
    }

    private Intent createSettingsIntent() {
        final Intent myIntent = new Intent(
                DashboardFragment.this.getActivity(), SugarCrmSettings.class);
        myIntent.putExtra(RestConstants.MODULE_NAME, "settings");
        return myIntent;
    }

    private class DashboardAdapter extends BaseAdapter {

        private final Context mContext;
        private final ArrayList<String> mList = new ArrayList<String>();

        public DashboardAdapter(final Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(final int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public void addItems(final List<String> items) {
            mList.addAll(items);
        }

        public void removeAll() {
            mList.clear();
        }

        @Override
        public long getItemId(final int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, final View convertView,
                final ViewGroup parent) {

            LinearLayout ls;

            if (convertView == null) { // if it's not recycled, initialize some
                                       // attributes
                final LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ls = (LinearLayout) inflater.inflate(R.layout.dashboard_item,
                        null);
            } else {
                ls = (LinearLayout) convertView;
            }
            final ImageView imageView = (ImageView) ls
                    .findViewById(R.id.module_image);
            imageView.setImageResource(ContentUtils.getModuleIcon(mList
                    .get(position)));

            final String moduleName = mList.get(position);

            final TextView textView = (TextView) ls
                    .findViewById(R.id.module_name);
            textView.setText(moduleName);

            ls.setTag(moduleName);

            return ls;
        }

    }

}
