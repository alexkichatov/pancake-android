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
 * EntryListServiceTask
 * 
 * @author chander
 */
public class EntryListServiceTask extends
        AsyncServiceTask<Object, Void, Object> {

    private final Context mContext;

    // TODO - remove this
    private String mSessionId;

    private final String mModuleName;

    private final String[] mSelectFields;

    private final HashMap<String, List<String>> mLinkNameToFieldsArray = new HashMap<String, List<String>>();

    private final String mMaxResults = "0";

    private final Uri mUri;

    private final String mQuery = "";

    private String mOrderBy = "";

    private String mOffset = "0";

    private final String mDeleted = "0";

    public static final String LOG_TAG = "EntryListTask";

    /**
     * <p>
     * Constructor for EntryListServiceTask.
     * </p>
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param intent
     *            a {@link android.content.Intent} object.
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

    /** {@inheritDoc} */
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
            // mAdapter.setSugarBeanArray(sBeans);
            // We can stop loading once we do not get the
            // if (sBeans.length < mMaxResults)
            // mStopLoading = true;

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
                // mContext.getContentResolver().update(mUri, values, null,
                // null);
            }

        } catch (final Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
