package com.imaginea.android.sugarcrm;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.util.ContentUtils;

public class DashboardAdapter extends BaseAdapter {

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
            ls = (LinearLayout) inflater.inflate(R.layout.dashboard_item, null);
        } else {
            ls = (LinearLayout) convertView;
        }
        final ImageView imageView = (ImageView) ls
                .findViewById(R.id.module_image);
        imageView.setImageResource(ContentUtils.getModuleIcon(mList
                .get(position)));

        final String moduleName = mList.get(position);

        final TextView textView = (TextView) ls.findViewById(R.id.module_name);
        textView.setText(moduleName);

        ls.setTag(moduleName);

        return ls;
    }

}
