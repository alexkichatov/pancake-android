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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.ui.ModuleDetailsMultiPaneActivity;
import com.imaginea.android.sugarcrm.ui.RecentModuleMultiPaneActivity;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

public class ModuleImageListFragment extends Fragment {
    private List<String> mModuleNames;
    private View root;
    ImageListAdapter adapter;

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.dashboardlist, container, false);

        adapter = new ImageListAdapter(getActivity());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mModuleNames.size() < 2) {

            if (adapter != null) {
                adapter.removeAll();
            }
            mModuleNames = ContentUtils.getModuleList(getActivity());
            mModuleNames.add(getString(R.string.recent));

            adapter.addItems(mModuleNames);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ListView listview = (ListView) root.findViewById(R.id.imagelist);
        listview.setAdapter(adapter);

        mModuleNames = ContentUtils.getModuleList(getActivity());
        mModuleNames.add(getString(R.string.recent));

        adapter.addItems(mModuleNames);

        listview.setOnItemClickListener(new OnItemClickListener() {

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
        Log.d("onClick viewModuleList  moduleName message", moduleName);

        final Context context = getActivity();

        if (moduleName.equals(getString(R.string.recent))) {
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
}

class ImageListAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<String> mList = new ArrayList<String>();

    public ImageListAdapter(final Context context) {
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

    public void addItem(final String item) {
        mList.add(item);
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
            ls = (LinearLayout) inflater.inflate(R.layout.dashboard_list_item,
                    null);
        } else {
            ls = (LinearLayout) convertView;
        }
        final ImageView imageView = (ImageView) ls.findViewById(R.id.listImage);
        imageView.setImageResource(ContentUtils.getModuleIcon(mList
                .get(position)));

        final String moduleName = mList.get(position);

        ls.setTag(moduleName);

        return ls;
    }

}
