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
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.Util;

public class ModuelImageListFragment extends Fragment {
    private List<String> mModuleNames;
    private View root;
    ImageListAdapter adapter;
    private static int storedpos = 0;
    public static boolean bOpenFromImageFragment = false;

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
        adapter.addItems(mModuleNames);

        if (bOpenFromImageFragment == true) {
            adapter.setSelectedPosition(storedpos);

            bOpenFromImageFragment = false;
        }

        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> arg0, final View view,
                    final int arg2, final long arg3) {
                adapter.setSelectedPosition(arg2);

                storedpos = arg2;
                view.setBackgroundColor(getResources().getColor(
                        ContentUtils.getModuleColor(mModuleNames.get(arg2))));
                viewModuleList(view);
            }

        });
    }

    public void viewModuleList(final View view) {
        Intent myIntent;
        final String moduleName = (String) view.getTag();
        Log.d("onClick viewModuleList  moduleName message", moduleName);
        final Context context = getActivity();
        myIntent = new Intent(context, ModuleDetailsMultiPaneActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.putExtra(Util.ROW_ID, "1");
        myIntent.putExtra(RestConstants.MODULE_NAME, moduleName);
        bOpenFromImageFragment = true;
        startActivity(myIntent);
    }

    private class ImageListAdapter extends BaseAdapter {

        private final Context mContext;
        private final ArrayList<String> mList = new ArrayList<String>();
        private int selectedPos = -1;

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
                ls = (LinearLayout) inflater.inflate(
                        R.layout.dashboard_list_item, null);
            } else {
                ls = (LinearLayout) convertView;
            }
            final ImageView imageView = (ImageView) ls
                    .findViewById(R.id.listImage);
            imageView.setImageResource(ContentUtils.getModuleIcon(mList
                    .get(position)));

            if (selectedPos == position) {
                if (!(mList.get(position).equalsIgnoreCase(Util.RECENT))) {
                    imageView.setImageResource(ContentUtils
                            .getModuleSelectedIcon(mList.get(position)));
                    ls.setBackgroundColor(getResources().getColor(
                            ContentUtils.getModuleColor(mList.get(position))));
                }
            } else {
                imageView.setImageResource(ContentUtils.getModuleIcon(mList
                        .get(position)));

            }

            final String moduleName = mList.get(position);

            ls.setTag(moduleName);

            return ls;
        }

        public void setSelectedPosition(int pos) {
            selectedPos = pos;
            notifyDataSetChanged();
        }

    }
}