/*******************************************************************************
 * Copyright (c) 2013 Asha.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Asha - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : SugarCrmSettings 
 ******************************************************************************/

package com.imaginea.android.sugarcrm;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;
import com.imaginea.android.sugarcrm.util.Util;
import com.imaginea.android.sugarcrm.util.ViewUtil;

/**
 * The Class SugarCrmSettings.
 */
public class SugarCrmSettings extends Activity {

    /** The Constant LOG_TAG. */
    private static final String LOG_TAG = "SugarCrmSettings";

    /** The m context. */
    private Context mContext;

    /** The saved settings. */
    private static Map<String, Object> savedSettings = null;

    /** The m start date button. */
    private Button mStartDateButton;

    /** The m end date button. */
    private Button mEndDateButton;

    /** The start date. */
    private Date startDate;

    /** The end date. */
    private Date endDate;

    /** The m records size spinner. */
    private Spinner mRecordsSizeSpinner;

    /** The enable alarm. */
    private CheckBox enableAlarm;

    /** The crm url. */
    private EditText crmUrl;

    /** The m records size. */
    private final String[] mRecordsSize = { "500", "1000", "2000", "5000",
            "10000", "ALL" };

    // cache the time
    /** The m start time. */
    private Date mStartTime;

    /** The m end time. */
    private Date mEndTime;

    /** The Constant THREE_MONTHS. */
    public static final long THREE_MONTHS = 3 * 30 * 24 * 60 * 60 * 1000L;

    /** The dialog. */
    private Dialog dialog;

    /** The is sync settings changed. */
    private boolean isSyncSettingsChanged = false;

    /** The cancel. */
    public Button apply, cancel;;

    /**
     * Creates the settings dilog.
     */
    public void createSettingsDilog() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ViewUtil.isHoneycombTablet(this)) {

            dialog = new Dialog(this);
            mContext = dialog.getContext();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.settings_dialog);

            dialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                        KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        finish();
                        dialog.dismiss();
                    }
                    return true;
                }
            });

            /* Set the Apply Button to Save the Settings */
            apply = (Button) dialog.findViewById(R.id.dialogButtonOK);
            apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    saveCurrentSettings(getApplicationContext());
                    if (isSyncSettingsChanged) {
                        Log.i(LOG_TAG,
                                "Sync settings has been changed Start Sync ");
                        isSyncSettingsChanged = false;
                        startSync();
                    }
                    // invalidate the session if the current settings changes
                    if (currentSettingsChanged(SugarCrmSettings.this)) {
                        final SugarCrmApp app = ((SugarCrmApp) getApplicationContext());
                        app.setSessionId(null);
                    }
                    finish();
                    dialog.dismiss();

                }
            });
            /* Set the Cancel Button to dismiss the Dialog */
            cancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    dialog.dismiss();

                }
            });
            dialog.show();
            /* set All the layout views */
            setDialogLayoutViews();

        } else {
            setContentView(R.layout.settings_dialog);

            mContext = this;
            /* Set the Action Bar */
            setupActionBar();

            /* set All the layout views */
            setDialogLayoutViewsForPhone();
        }
        /* Set the Synchronizing view */
        setSyncSpinner();
        /* Set the Last Saved Settings as default */
        setLastDefaultSettings();

    }

    /**
     * This methods tells if the settings have changed.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @return a boolean.
     */
    public static boolean currentSettingsChanged(Context context) {

        if (savedSettings.isEmpty())
            return false;

        try {
            if (!getSugarRestUrl(context).equals(
                    savedSettings.get(Util.PREF_REST_URL)))
                return true;

            return false;
        } finally {

        }
    }

    private void setDialogLayoutViewsForPhone() {
        /* LOGIN */

        crmUrl = (EditText) findViewById(R.id.crmUrl);

        /* NOTOFICATION */
        enableAlarm = (CheckBox) findViewById(R.id.enableAlarm);

        /* SYNCRONIZING */
        mRecordsSizeSpinner = (Spinner) findViewById(R.id.recordsize);
        mStartDateButton = (Button) findViewById(R.id.startDate);
        mEndDateButton = (Button) findViewById(R.id.endDate);

    }

    private void setupActionBar() {
        /* SYNCRONIZING */
        final ImageView actionView = (ImageView) findViewById(R.id.actionbar_back);
        actionView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        final ImageView saveButton = (ImageView) findViewById(R.id.editDoneImg);
        final int[] saveResourcesId;
        if (ViewUtil.isHoneycombTablet(mContext)) {
            final int[] saveTabletResourcesId = {
                    R.drawable.ico_actionbar_done_pressed,
                    R.drawable.ico_actionbar_done_pressed, R.drawable.edit_done };
            saveResourcesId = saveTabletResourcesId;
        } else {
            final int[] savePhoneResourcesId = {
                    R.drawable.ico_m_actionbar_done_pressed,
                    R.drawable.ico_m_actionbar_done_pressed,
                    R.drawable.ico_m_actionbar_done_nor };
            saveResourcesId = savePhoneResourcesId;
        }
        saveButton.setImageDrawable(Util.getPressedImage(mContext,
                saveResourcesId));
        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveButton
                        .setImageResource(R.drawable.ico_m_actionbar_done_pressed);
                saveCurrentSettings(getApplicationContext());
                if (isSyncSettingsChanged) {
                    Log.i(LOG_TAG, "Sync settings has been changed Start Sync ");
                    isSyncSettingsChanged = false;
                    startSync();
                }
                // invalidate the session if the current settings changes
                if (currentSettingsChanged(SugarCrmSettings.this)) {
                    final SugarCrmApp app = ((SugarCrmApp) getApplicationContext());
                    app.setSessionId(null);
                }
                finish();
            }
        });
    }

    /**
     * Sets the dialog layout views.
     */
    private void setDialogLayoutViews() {
        /* LOGIN */

        crmUrl = (EditText) dialog.findViewById(R.id.crmUrl);

        /* NOTOFICATION */
        enableAlarm = (CheckBox) dialog.findViewById(R.id.enableAlarm);

        /* SYNCRONIZING */
        mRecordsSizeSpinner = (Spinner) dialog.findViewById(R.id.recordsize);
        mStartDateButton = (Button) dialog.findViewById(R.id.startDate);
        mEndDateButton = (Button) dialog.findViewById(R.id.endDate);

    }

    /**
     * Sets the last default settings.
     */
    private void setLastDefaultSettings() {

        if (savedSettings != null && !savedSettings.isEmpty()) {
            /* Set CRM URL */
            crmUrl.setText(savedSettings.get(Util.PREF_REST_URL).toString());
            /* Set Alarm state */
            enableAlarm.setChecked((Boolean) savedSettings
                    .get(Util.PREF_ALARM_STATE));

            /* Set Start Date */
            final Date startDate = (Date) savedSettings
                    .get(Util.PREF_SYNC_START_TIME);
            if (startDate != null) {
                setDate(mStartDateButton, startDate);
            }
            /* set End Date */
            final Date endDate = (Date) savedSettings
                    .get(Util.PREF_SYNC_END_TIME);
            if (endDate != null) {
                setDate(mEndDateButton, endDate);
            }

        } else {
            crmUrl.setText(getSugarRestUrl(getApplicationContext()));
            enableAlarm.setChecked(PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext())
                    .getBoolean(Util.PREF_ALARM_STATE, false));

        }
    }

    /**
     * Sets the sync spinner.
     */
    private void setSyncSpinner() {

        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                this, android.R.layout.simple_spinner_item, mRecordsSize);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRecordsSizeSpinner.setAdapter(adapter);

        final long time = System.currentTimeMillis();
        final SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        final long startTime = pref.getLong(Util.PREF_SYNC_START_TIME, time
                - THREE_MONTHS);
        final long endTime = pref.getLong(Util.PREF_SYNC_END_TIME, time);
        mStartTime = new Date();
        mStartTime.setTime(startTime);
        mEndTime = new Date();
        mEndTime.setTime(endTime);

        populateWhen();

        final String records_size = SugarCrmSettings
                .getFetchRecordsSize(getApplicationContext());
        final int position = Arrays.binarySearch(mRecordsSize, records_size);
        mRecordsSizeSpinner.setSelection(position);
    }

    /**
     * getUsername
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getUsername(Context context) {
        /* Get the User NAme Edit Text Box */
        // return mUserName.getText().toString();
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(Util.PREF_USERNAME, null);

    }

    /**
     * getFetchRecordsSize
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getFetchRecordsSize(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(Util.PREF_FETCH_RECORDS_SIZE, "2000");
    }

    /**
     * gets SugarCRM RestUrl, on production it returns empty url, if debuggable
     * is set to "false" in the manifest file.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @return a {@link java.lang.Strinect.
     */
    public static String getSugarRestUrl(Context context) {
        final PackageManager pm = context.getPackageManager();
        try {
            final ApplicationInfo appInfo = pm.getApplicationInfo(
                    "com.imaginea.android.sugarcrm",
                    ApplicationInfo.FLAG_DEBUGGABLE);
            if ((ApplicationInfo.FLAG_DEBUGGABLE & appInfo.flags) == ApplicationInfo.FLAG_DEBUGGABLE)
                return PreferenceManager.getDefaultSharedPreferences(context)
                        .getString(Util.PREF_REST_URL,
                                context.getString(R.string.defaultUrl));
        } catch (final Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(Util.PREF_REST_URL, "");
    }

    /**
     * This methods saves the current settings, to be able to check later if
     * settings changed.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     */
    private void saveCurrentSettings(Context context) {
        savedSettings = new HashMap<String, Object>();
        savedSettings.clear();
        savedSettings.put(Util.PREF_REST_URL, crmUrl.getText().toString());
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (crmUrl.getText().toString() != null
                && URLUtil.isValidUrl(crmUrl.getText().toString())) {
            final Editor editor = prefs.edit();
            editor.putString(Util.PREF_REST_URL, crmUrl.getText().toString());
            editor.commit();
        }
        savedSettings.put(Util.PREF_ALARM_STATE, enableAlarm.isChecked());

        savedSettings.put(Util.PREF_SYNC_START_TIME, startDate);
        savedSettings.put(Util.PREF_SYNC_END_TIME, endDate);

    }

    /**
     * Populate when.
     */
    private void populateWhen() {
        setDate(mStartDateButton, mStartTime);
        setDate(mEndDateButton, mEndTime);

        mStartDateButton.setOnClickListener(new DateClickListener(mStartTime));
        mEndDateButton.setOnClickListener(new DateClickListener(mEndTime));
    }

    /**
     * Sets the date.
     * 
     * @param view
     *            the view
     * @param date
     *            the date
     */
    private void setDate(final TextView view, final Date date) {
        final int flags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY
                | DateUtils.FORMAT_ABBREV_MONTH
                | DateUtils.FORMAT_ABBREV_WEEKDAY;
        view.setText(DateUtils.formatDateTime(this, date.getTime(), flags));
    }

    /**
     * DateClickListener.
     * 
     * @see DateClickEvent
     */
    private class DateClickListener implements View.OnClickListener {

        /** The m date. */
        private final Date mDate;

        /**
         * Instantiates a new date click listener.
         * 
         * @param date
         *            the date
         */
        public DateClickListener(final Date date) {
            mDate = date;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(final View v) {
            isSyncSettingsChanged = true;
            final Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(mDate.getTime());

            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int date = calendar.get(Calendar.DATE);

            new DatePickerDialog(mContext, new DateListener(v), year, month,
                    date).show();
        }
    }

    /**
     * DateListener.
     * 
     * @see DateEvent
     */
    private class DateListener implements OnDateSetListener {

        /** The m view. */
        View mView;

        /**
         * Instantiates a new date listener.
         * 
         * @param view
         *            the view
         */
        public DateListener(final View view) {
            mView = view;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.app.DatePickerDialog.OnDateSetListener#onDateSet(android.
         * widget.DatePicker, int, int, int)
         */
        @Override
        public void onDateSet(final DatePicker view, final int year,
                final int month, final int monthDay) {
            // Cache the member variables locally to avoid inner class overhead.
            startDate = mStartTime;
            endDate = mEndTime;

            final Calendar calendar = Calendar.getInstance();

            // Cache the start and end millis so that we limit the number
            // of calls to normalize() and toMillis(), which are fairly
            // expensive.
            long startMillis;
            long endMillis;
            if (mView == mStartDateButton) {
                // The start date was changed.
                calendar.set(year, month, monthDay);
                startMillis = calendar.getTimeInMillis();
                endMillis = endDate.getTime();
                final long curTime = System.currentTimeMillis();
                // see to that the endDate does not exceed the current date
                if (endMillis > curTime) {
                    endMillis = curTime;
                }

                if (startMillis > endMillis) {
                    startMillis = endMillis;
                }

            } else {
                // The end date was changed.
                startMillis = startDate.getTime();
                calendar.set(year, month, monthDay);
                endMillis = calendar.getTimeInMillis();

                final long curTime = System.currentTimeMillis();
                // see to that the endDate does not exceed the current date
                if (endMillis > curTime) {
                    endMillis = curTime;
                }

                // Do not allow an event to have an end time before the start
                // time.
                if (startMillis > endMillis) {
                    endMillis = startMillis;
                }

            }

            startDate.setTime(startMillis);
            endDate.setTime(endMillis);

            setDate(mStartDateButton, startDate);
            setDate(mEndDateButton, endDate);

        }
    }

    /**
     * starts sync for all the modules in the background.
     * 
     */
    private void startSync() {
        final Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
        extras.putInt(Util.SYNC_TYPE, Util.SYNC_MODULES_DATA);
        final SugarCrmApp app = (SugarCrmApp) getApplication();
        final String usr = SugarCrmSettings
                .getUsername(getApplicationContext());
        if (ContentResolver.isSyncActive(app.getAccount(usr),
                SugarCRMProvider.AUTHORITY))
            return;

        ContentResolver.requestSync(app.getAccount(usr),
                SugarCRMProvider.AUTHORITY, extras);

    }

}
