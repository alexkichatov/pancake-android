package com.imaginea.android.sugarcrm;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomActionbar extends RelativeLayout implements OnClickListener {
    private final LayoutInflater mInflater;

    private final RelativeLayout mActionBarView;

    private LinearLayout mActionItemsView;

    private final ImageView mDashboardView;

    private final LinearLayout mAddView;

    private final TextView mTextView;

    private LinearLayout mPopupView;
    private final ImageView mSettingsView;

    public CustomActionbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mActionBarView = (RelativeLayout) mInflater.inflate(
                R.layout.custom_actionbar, null);
        addView(mActionBarView);

        mDashboardView = (ImageView) mActionBarView
                .findViewById(R.id.actionbar_logo);
        mAddView = (LinearLayout) mActionBarView.findViewById(R.id.addNew);

        mSettingsView = (ImageView) mActionBarView.findViewById(R.id.settings);

        mTextView = (TextView) mActionBarView
                .findViewById(R.id.actionbar_moduleName);

    }

    public void setHomeAction(Action action) {
        mDashboardView.setOnClickListener(this);
        mDashboardView.setTag(action);

    }

    public void setAddAction(Action action) {
        mAddView.setOnClickListener(this);
        mAddView.setTag(action);

    }

    public void setSettingAction(Action action) {
        mSettingsView.setOnClickListener(this);
        mSettingsView.setTag(action);

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
        addActionItem(action, index);
        /*
         * if (index < 3) addActionItem(action, index); else if (index == 3 &&
         * mPopupView == null) { ImageButton item = (ImageButton)
         * ((RelativeLayout) mActionItemsView.getChildAt(2)).getChildAt(1);
         * Action ItemAction = (Action) item.getTag();
         * mActionItemsView.removeViewAt(2); inflatePopupMenu(ItemAction,
         * action); } else { inflatePopupMenu(action); }
         */
    }

    public void addActionItem(Action action, int index) {
        mActionItemsView.addView(inflateAction(action), index);
    }

    public void setActionVisiblity(int index, boolean bValue) {
        if (bValue) {
            mActionItemsView.getChildAt(index).setVisibility(View.VISIBLE);
        } else {
            mActionItemsView.getChildAt(index).setVisibility(View.GONE);
        }
    }

    public interface Action {
        // public int getDrawableId();

        public String getTitle();

        public void performAction(View view);
    }

    public static abstract class AbstractAction implements Action {
        // final private int mDrawable;

        private String mTitle;

        public AbstractAction() {
            // mDrawable = drawable;
            mTitle = null;
        }

        public AbstractAction(int drawable, String title) {
            this();
            mTitle = title;
        }

        /*
         * @Override public int getDrawableId() { return mDrawable; }
         */

        @Override
        public String getTitle() {
            return mTitle;
        }
    }

    public static class IntentAction extends AbstractAction {
        private final Context mContext;

        private final Intent mIntent;

        public IntentAction(Context context, Intent intent) {
            super();
            mContext = context;
            mIntent = intent;
        }

        public IntentAction(Context context, Intent intent, int drawable,
                String title) {
            super(drawable, title);
            mContext = context;
            mIntent = intent;
        }

        @Override
        public void performAction(View view) {
            if (mIntent != null) {
                mContext.startActivity(mIntent);
            }
        }
    }

    private View inflateAction(Action action) {
        final View view = mInflater.inflate(R.layout.custom_actionitem,
                mActionItemsView, false);

        final ImageButton actionItem = (ImageButton) view
                .findViewById(R.id.actionbar_item);
        // actionItem.setImageResource(action.getDrawableId());
        /*
         * if(action.getTitle() != null) { TextView tv = (TextView)
         * view.findViewById(R.id.actionbar_itemtext);
         * tv.setText(action.getTitle()); }
         */
        actionItem.setTag(action);
        actionItem.setOnClickListener(this);
        return view;
    }

    public void setPopupActionVisiblity(int index, boolean bValue) {
        if (bValue) {
            ((LinearLayout) mPopupView.getChildAt(0)).getChildAt(index)
                    .setVisibility(View.VISIBLE);
        } else {
            ((LinearLayout) mPopupView.getChildAt(0)).getChildAt(index)
                    .setVisibility(View.GONE);
        }
    }

}
