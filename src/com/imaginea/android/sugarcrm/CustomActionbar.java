package com.imaginea.android.sugarcrm;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomActionbar extends RelativeLayout implements OnClickListener {
	private LayoutInflater mInflater;
	private RelativeLayout mActionBarView;
	private LinearLayout mActionItemsView;
	private ImageButton mHomeView;
	private View seperator;
	private TextView mTextView;
	private PopupWindow mPopupWindow;
	private LinearLayout mPopupView;
	
	public CustomActionbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mActionBarView = (RelativeLayout) mInflater.inflate(R.layout.custom_actionbar, null);
        addView(mActionBarView);
        
        mHomeView = (ImageButton) mActionBarView.findViewById(R.id.actionbar_home);
        seperator = (View) mActionBarView.findViewById(R.id.actionbar_homeseperator);
        mTextView = (TextView) mActionBarView.findViewById(R.id.actionbar_text);
        
        mActionItemsView = (LinearLayout) mActionBarView.findViewById(R.id.actionbar_items);
        
    }
	
	public void setHomeAction(Action action) {
		mHomeView.setOnClickListener(this);
		mHomeView.setTag(action);
		mHomeView.setImageResource(action.getDrawable());
		mHomeView.setVisibility(View.VISIBLE);
		if(action.getDrawable() == R.drawable.home)
			seperator.setVisibility(View.VISIBLE);
		else {
			mHomeView.setClickable(false);
			mHomeView.setFocusable(false);
		}
			
    }
	
	public void clearHomeAction() {
		mHomeView.setVisibility(View.GONE);
    }
	
	public void setTitle(CharSequence title) {
		mTextView.setVisibility(View.VISIBLE);
		mTextView.setText(title);
    }

    public void setTitle(int resid) {
    	mTextView.setText(resid);
    }
    
    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction(view);
        }
    }
    
    public void addActionItem(Action action) {
    	final int index = mActionItemsView.getChildCount();
    	if(index < 3)
    		addActionItem(action, index);
    	else if(index == 3 && mPopupView == null){
    		ImageButton item = (ImageButton)((RelativeLayout)mActionItemsView.getChildAt(2)).getChildAt(1);
    		Action ItemAction = (Action) item.getTag();
    		mActionItemsView.removeViewAt(2);
    		inflatePopupMenu(ItemAction, action);
    	}
    	else {
    		inflatePopupMenu(action);
    	}
    }
    
    public void addActionItem(Action action, int index) {
    	mActionItemsView.addView(inflateAction(action), index);    	
    }
    public void setActionVisiblity(int index , boolean bValue) {
    	if(bValue) {
    		mActionItemsView.getChildAt(index).setVisibility(View.VISIBLE);
    	}
    	else {
    		mActionItemsView.getChildAt(index).setVisibility(View.GONE);
    	}    	
    }
    public interface Action {
        public int getDrawable();
        public String getTitle();
        public void performAction(View view);
    }
    
    public static abstract class AbstractAction implements Action {
        final private int mDrawable;
        private String mTitle;

        public AbstractAction(int drawable) {
            mDrawable = drawable;
            mTitle = null;
        }
        
        public AbstractAction(int drawable, String title) {
        	this(drawable);
        	mTitle = title;
        }

		@Override
        public int getDrawable() {
            return mDrawable;
        }
        
        @Override
        public String getTitle() {
            return mTitle;
        }
    }
    
    public static class IntentAction extends AbstractAction {
        private Context mContext;
        private Intent mIntent;

        public IntentAction(Context context, Intent intent, int drawable) {
            super(drawable);
            mContext = context;
            mIntent = intent;
        }
        
        public IntentAction(Context context, Intent intent, int drawable, String title) {
            super(drawable, title);
            mContext = context;
            mIntent = intent;
        }
        
        @Override
        public void performAction(View view) {
        	if(mIntent != null)
            mContext.startActivity(mIntent);            
        }
    }
          
    private View inflateAction(Action action) {
        View view = mInflater.inflate(R.layout.custom_actionitem, mActionItemsView, false);

        ImageButton actionItem =(ImageButton) view.findViewById(R.id.actionbar_item);        
        actionItem.setImageResource(action.getDrawable());
        /*if(action.getTitle() != null) {
        	TextView tv = (TextView) view.findViewById(R.id.actionbar_itemtext);
        	tv.setText(action.getTitle());
        }*/
        actionItem.setTag(action);
        actionItem.setOnClickListener(this);
        return view;
    }
    
    private class PopupAction extends AbstractAction {

        public PopupAction(int drawable) {
            super(drawable);
        }

        @Override
        public void performAction(View view) {
        	if (!mPopupWindow.isShowing()) {
        		mPopupWindow.setTouchable(true);
                mPopupWindow.setFocusable(true);
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        	    int mChildCount = mPopupView.getChildCount();
        	    
        	    //mPopupWindow.update(mPopupView, 150, 50*mPopupView.getChildCount());
        	    mPopupWindow.setHeight((getResources().getInteger(R.integer.actionbar_popupheight))*mChildCount);
                mPopupWindow.update();
        		//mPopupWindow.showAtLocation(mActionItemsView, Gravity.TOP, 0, 0);
        	    mPopupWindow.showAsDropDown(mActionItemsView, getResources().getInteger(R.integer.actionbar_dropdownoffset),0);
            } else {
            	mPopupWindow.dismiss();
            }
        }

    }
    
    public void setPopupActionVisiblity(int index , boolean bValue) {
    	if(bValue) {
    		((LinearLayout)mPopupView.getChildAt(0)).getChildAt(index).setVisibility(View.VISIBLE);
    	}
    	else {
    		((LinearLayout)mPopupView.getChildAt(0)).getChildAt(index).setVisibility(View.GONE);
    	}    	
    }
    
    private void inflatePopupMenu(Action action) {
		
    	
    	View view = mInflater.inflate(R.layout.custom_popupitem, (LinearLayout)mPopupView.getChildAt(0), false);
        
    	TextView actionItem =(TextView) view.findViewById(R.id.actionbar_popupitem);
        actionItem.setText(action.getTitle());
        view.setTag(action);
        view.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				
				final Object tag = arg0.getTag();
		        if (tag instanceof Action) {
		            final Action action = (Action) tag;
		            action.performAction(arg0);
		            if(mPopupWindow!=null)
		            	mPopupWindow.dismiss();
		        }
			}
        });  
        int index = mPopupView.getChildCount();
        mPopupView.addView(view, index);       
    }
    
    private void inflatePopupMenu(Action itemAction, Action action) {
    		this.addActionItem(new PopupAction(itemAction.getDrawable()), 2);
    		
    		
    		mPopupView = (LinearLayout) mInflater.inflate(R.layout.custom_popuplayout, null, false);
    		mPopupWindow = new PopupWindow(mPopupView, 160, 250, false);
            this.mPopupWindow.setTouchInterceptor(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        mPopupWindow.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            mPopupView.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK)
			        {
			            if(mPopupWindow!=null)
			            	mPopupWindow.dismiss();
			            return true;
			        }
					return false;
				}
			});

            mPopupWindow.setContentView(mPopupView);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    		if(itemAction.getDrawable() != R.drawable.relation)    		
    			inflatePopupMenu(itemAction);
    		inflatePopupMenu(action);         
    		
    }   
    
}
