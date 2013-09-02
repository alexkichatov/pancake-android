/*******************************************************************************
 * Copyright (c) 2013 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *          chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : AccountRemovalService 
 * Description : 
                This is service class to remove the Account
 ******************************************************************************/

package com.imaginea.android.sugarcrm.services;

import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.imaginea.android.sugarcrm.R;
import com.imaginea.android.sugarcrm.SugarCrmApp;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.rest.SugarBean;
import com.imaginea.android.sugarcrm.util.AsyncServiceTask;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * The Class EntryListServiceTask.
 */
public class EntryListServiceTask extends
        AsyncServiceTask<Object, Void, Object> {

    /** The m context. */
    private final Context mContext;

    // TODO - remove this
    /** The m session id. */
    private String mSessionId;

    /** The m module name. */
    private final String mModuleName;

    /** The m select fields. */
    private final String[] mSelectFields;

    /** The m link name to fields array. */
    private final HashMap<String, List<String>> mLinkNameToFieldsArray = new HashMap<String, List<String>>();

    /** The m max results. */
    private final String mMaxResults = "0";

    /** The m uri. */
    private final Uri mUri;

    /** The m query. */
    private final String mQuery = "";

    /** The m order by. */
    private String mOrderBy = "";

    /** The m offset. */
    private String mOffset = "0";

    /** The m deleted. */
    private final String mDeleted = "0";

    /** The Constant LOG_TAG. */
    public static final String LOG_TAG = "EntryListTask";

    /**
     * Instantiates a new entry list service task.
     * 
     * @param context
     *            the context
     * @param intent
     *            the intent
     */
    public EntryListServiceTask(final Context context, final Intent intent) {
        super(context);
        mContext = context;

        final Bundle extras = intent.getExtras();
        mUri = intent.getData();
        final int count = mUri.getPathSegments().size();
        if (count == 3) {
            mOffset = mUri.getPathSegments().get(1);
        }
        mModuleName = extras.getString(RestConstants.MODULE_NAME);
        mSelectFields = extras.getStringArray(Util.PROJECTION);
        mOrderBy = extras.getString(Util.SORT_ORDER);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.imaginea.android.sugarcrm.util.AsyncServiceTask#doInBackground(Params
     * [])
     */
    @Override
    protected Object doInBackground(final Object... params) {
        try {

            if (mSessionId == null) {
                mSessionId = ((SugarCrmApp) SugarCrmApp.app).getSessionId();
            }
            final SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(mContext);
            // TODO use a constant and remove this as we start from the login
            // screen
            final String url = pref.getString(Util.PREF_REST_URL,
                    mContext.getString(R.string.defaultUrl));

            final SugarBean[] sBeans = Rest.getEntryList(url, mSessionId,
                    mModuleName, mQuery, mOrderBy, mOffset, mSelectFields,
                    mLinkNameToFieldsArray, mMaxResults, mDeleted);

            // TODO - do a bulk insert in the content provider instead
            for (final SugarBean sBean : sBeans) {
                final ContentValues values = new ContentValues();

                for (int i = 0; i < mSelectFields.length; i++) {
                    final String fieldValue = sBean
                            .getFieldValue(mSelectFields[i]);
                    Log.i(LOG_TAG, "FieldName:|Field value " + mSelectFields[i]
                            + ":" + fieldValue);
                    values.put(mSelectFields[i], fieldValue);
                }
                mContext.getContentResolver().insert(mUri, values);

            }

        } catch (final Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.imaginea.android.sugarcrm.util.AsyncServiceTask#onCancelled()
     */
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
