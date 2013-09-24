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
 * FileName : ModuleImageListFragment 
 * Description :
 *              ModuleImageListFragment is used to display Dashboard Image Fragment both in Phone and in Tablet
 ******************************************************************************/

package com.imaginea.android.sugarcrm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
import android.widget.SearchView;

import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.ui.ModuleDetailsMultiPaneActivity;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

/**
 * The Class ModuleImageListFragment.
 */
public class ModuleImageListFragment extends Fragment {
    private List<String> mModuleNames;

    /** The m root. */
    private View mRoot;

    /** The m adapter. */
    private ImageListAdapter mAdapter;

    /** The m storedpos. */
    private static int mStoredpos = 0;

    /** The b open from image fragment. */
    private static boolean bOpenFromImageFragment;

    private static int mInitialPosition = -2;

    /** The m listener. */
    private OnItemSelectedListener mListener;

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.dashboardlist, container, false);
        mAdapter = new ImageListAdapter(getActivity());

        return mRoot;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mModuleNames.size() < 2) {

            if (mAdapter != null) {
                mAdapter.removeAll();
            }
            mModuleNames = ContentUtils.getModuleList(getActivity());
            mAdapter.addItems(mModuleNames);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e("OnAttach", "onattach called");
        if ((activity instanceof ModuleDetailsMultiPaneActivity)
                || activity instanceof ModulesActivity) {
            Log.e("OnAttach", "listener updated");
            mListener = (OnItemSelectedListener) activity;
        }
    }

    private void disableSearchActionBar() {
        final CustomActionbar actionBar = (CustomActionbar) getActivity()
                .findViewById(R.id.custom_actionbar);
        final SearchView search = (SearchView) actionBar
                .findViewById(R.id.searchView);
        final ImageView backView = (ImageView) actionBar
                .findViewById(R.id.actionbar_back);
        backView.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
        search.setQuery("", false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ListView listview = (ListView) mRoot.findViewById(R.id.imagelist);
        listview.setAdapter(mAdapter);

        mModuleNames = ContentUtils.getModuleList(getActivity());

        mAdapter.addItems(mModuleNames);

        final Intent intent = getActivity().getIntent();

        final Bundle extras = intent.getExtras();

        if (extras != null) {
            bOpenFromImageFragment = extras
                    .getBoolean(Util.isOpenFromImageFragment);
        }

        if (bOpenFromImageFragment) {
            mAdapter.setSelectedPosition(mStoredpos);
        }

        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> arg0, final View view,
                    final int arg2, final long arg3) {

                mAdapter.setSelectedPosition(arg2);
                mStoredpos = arg2;
                disableSearchActionBar();
                viewModuleList(view);
            }

        });
    }

    /**
     * View module list.
     * 
     * @param view
     *            the view
     */
    public void viewModuleList(final View view) {

        Intent myIntent;

        final String moduleName = (String) view.getTag();
        Log.d("onClick viewModuleList  moduleName message", moduleName);
        final Context context = getActivity();
        if (ViewUtil.isHoneycombTablet(getActivity())) {
            myIntent = new Intent(context, ModuleDetailsMultiPaneActivity.class);
        } else {
            myIntent = new Intent(context, ModulesActivity.class);
        }
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.putExtra(Util.ROW_ID, "1");
        myIntent.putExtra(RestConstants.MODULE_NAME, moduleName);
        myIntent.putExtra(Util.isOpenFromImageFragment, true);
        if (mListener != null) {
            Log.d("onClick viewModuleList listener", moduleName);
            mListener.onItemSelected(moduleName);
        } else {
            startActivity(myIntent);
            mAdapter.setSelectedPosition(mInitialPosition);

        }
    }

    /**
     * The listener interface for receiving onItemSelected events. The class
     * that is interested in processing a onItemSelected event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's
     * <code>addOnItemSelectedListener<code> method. When
     * the onItemSelected event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see OnItemSelectedEvent
     */
    public interface OnItemSelectedListener {
        void onItemSelected(String moduleName);
    }

    /**
     * The Class ImageListAdapter.
     */
    private class ImageListAdapter extends BaseAdapter {

        private final Context mContext;
        private final List<String> mList = new ArrayList<String>();
        private int selectedPos = -1;

        public ImageListAdapter(final Context context) {
            mContext = context;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return mList.size();
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Object getItem(final int arg0) {
            return null;
        }

        /**
         * Adds the items.
         * 
         * @param items
         *            the items
         */
        public void addItems(final List<String> items) {
            mList.addAll(items);
        }

        /**
         * Removes the all.
         */
        public void removeAll() {
            mList.clear();
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(final int position) {
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(final int position, final View convertView,
                final ViewGroup parent) {

            LinearLayout ls;
            /* if it's not recycled, initialize some attributes */
            if (convertView == null) {
                final LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ls = (LinearLayout) inflater.inflate(
                        R.layout.dashboard_list_item, null);
            } else {
                ls = (LinearLayout) convertView;
            }

            final ImageView imageView = (ImageView) ls
                    .findViewById(R.id.listImage);
            if (ViewUtil.isHoneycombTablet(getActivity())) {

                imageView.setImageResource(ContentUtils.getModuleIcon(mList
                        .get(position)));
            } else {
                imageView.setImageResource(ContentUtils
                        .getModulePhoneIcon(mList.get(position)));
            }

            if (selectedPos == position) {
                if (!(mList.get(position).equalsIgnoreCase(Util.RECENT))) {
                    if (ViewUtil.isHoneycombTablet(getActivity())) {

                        imageView.setImageResource(ContentUtils
                                .getModuleSelectedIcon(mList.get(position)));
                    } else {
                        imageView
                                .setImageResource(ContentUtils
                                        .getModulePhoneSelectedIcon(mList
                                                .get(position)));
                    }
                    ls.setBackgroundColor(getResources().getColor(
                            ContentUtils.getModuleColor(mList.get(position))));
                }
            } else {
                ls.setBackgroundColor(getResources().getColor(
                        R.color.dashboard_list_color));

            }

            final String moduleName = mList.get(position);

            ls.setTag(moduleName);

            return ls;
        }

        /**
         * Sets the selected position.
         * 
         * @param pos
         *            the new selected position
         */
        public void setSelectedPosition(int pos) {
            selectedPos = pos;
            if (selectedPos != mInitialPosition) {
                notifyDataSetChanged();
            }
        }

    }

}
