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
 * FileName : CustomActionbar 
 ******************************************************************************/
package com.imaginea.android.sugarcrm;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * The Class CustomActionbar.
 */
public class CustomActionbar extends RelativeLayout implements OnClickListener {

    /** The m dashboard view. */
    private final ImageView mDashboardView;

    /** The add view. */
    private final LinearLayout mAddView;

    /** The m text view. */
    private final TextView mTextView;

    /**
     * Instantiates a new custom actionbar.
     * 
     * @param context
     *            the context
     * @param attrs
     *            the attrs
     */
    public CustomActionbar(Context context, AttributeSet attrs) {
        super(context, attrs);

        /* The inflater. */
        final LayoutInflater mInflater;

        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        /* The action bar view. */
        final RelativeLayout mActionBarView;

        mActionBarView = (RelativeLayout) mInflater.inflate(
                R.layout.custom_actionbar, null);
        addView(mActionBarView);

        mDashboardView = (ImageView) mActionBarView
                .findViewById(R.id.actionbar_logo);
        mAddView = (LinearLayout) mActionBarView.findViewById(R.id.addNew);

        mTextView = (TextView) mActionBarView
                .findViewById(R.id.actionbar_moduleName);

    }

    /**
     * Sets the home action.
     * 
     * @param action
     *            the new home action
     */
    public void setHomeAction(Action action) {
        mDashboardView.setOnClickListener(this);
        mDashboardView.setTag(action);

    }

    /**
     * Sets the adds the action.
     * 
     * @param action
     *            the new adds the action
     */
    public void setAddAction(Action action) {
        mAddView.setOnClickListener(this);
        mAddView.setTag(action);

    }

    /**
     * Sets the title.
     * 
     * @param title
     *            the new title
     */
    public void setTitle(CharSequence title) {
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setText(title);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction(view);
        }
    }

    /**
     * The Interface Action.
     */
    public interface Action {

        /**
         * Gets the title.
         * 
         * @return the title
         */
        String getTitle();

        /**
         * Perform action.
         * 
         * @param view
         *            the view
         */
        void performAction(View view);
    }

    /**
     * The Class AbstractAction.
     */
    public abstract static class AbstractAction implements Action {

        /** The m title. */
        private String mTitle;

        /**
         * Instantiates a new abstract action.
         */
        public AbstractAction() {
            mTitle = null;
        }

        /**
         * Instantiates a new abstract action.
         * 
         * @param drawable
         *            the drawable
         * @param title
         *            the title
         */
        public AbstractAction(String title) {
            this();
            mTitle = title;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.imaginea.android.sugarcrm.CustomActionbar.Action#getTitle()
         */
        @Override
        public String getTitle() {
            return mTitle;
        }
    }

    /**
     * The Class IntentAction.
     */
    public static class IntentAction extends AbstractAction {

        /** The m context. */
        private final Context mContext;

        /** The m intent. */
        private final Intent mIntent;

        /**
         * Instantiates a new intent action.
         * 
         * @param context
         *            the context
         * @param intent
         *            the intent
         */
        public IntentAction(Context context, Intent intent) {
            super();
            mContext = context;
            mIntent = intent;
        }

        /**
         * Instantiates a new intent action.
         * 
         * @param context
         *            the context
         * @param intent
         *            the intent
         * @param drawable
         *            the drawable
         * @param title
         *            the title
         */
        public IntentAction(Context context, Intent intent, String title) {
            super(title);
            mContext = context;
            mIntent = intent;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.imaginea.android.sugarcrm.CustomActionbar.Action#performAction
         * (android.view.View)
         */
        @Override
        public void performAction(View view) {
            if (mIntent != null) {
                mContext.startActivity(mIntent);
            }
        }
    }

}
