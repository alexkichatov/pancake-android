package com.imaginea.android.sugarcrm;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.Inflater;

import com.imaginea.android.sugarcrm.provider.ContentUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DashboardAdapter extends BaseAdapter {
	
	
	private Context mContext;
	private ArrayList<String> mList = new ArrayList<String>();
		
	public DashboardAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addItem(String item){
		mList.add(item);
	}
	
	public void addItems(List<String> items){
		mList.addAll(items);
	}
	
	public void removeAll(){
		mList.clear();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		LinearLayout ls;
		
        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	ls = (LinearLayout) inflater.inflate(R.layout.dashboard_item, null);           
        } else {
        	ls = (LinearLayout) convertView;
        }
        ImageView imageView = (ImageView) ls.findViewById(R.id.module_image);
        imageView.setImageResource(ContentUtils.getModuleIcon(mList.get(position)));
        
        String moduleName =  mList.get(position);
        
        TextView textView = (TextView)ls.findViewById(R.id.module_name);
        textView.setText(moduleName);        
        
        ls.setTag(moduleName);
               
        return ls;
	}

}
