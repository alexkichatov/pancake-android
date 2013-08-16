package com.imaginea.android.sugarcrm.sync;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.CustomActionbar;
import com.imaginea.android.sugarcrm.CustomActionbar.Action;
import com.imaginea.android.sugarcrm.CustomActionbar.IntentAction;
import com.imaginea.android.sugarcrm.DashboardActivity;
import com.imaginea.android.sugarcrm.R;
import com.imaginea.android.sugarcrm.SugarCrmApp;
import com.imaginea.android.sugarcrm.SugarCrmSettings;
import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * SyncConfigActivity
 * 
 * @author chander
 */
public class SyncConfigActivity extends Activity {

    private Button mStartDateButton;

    private Button mEndDateButton;

    private Spinner mRecordsSizeSpinner;

    private final String[] mRecordsSize = { "500", "1000", "2000", "5000",
            "10000", "ALL" };

    // cache the time
    private Date mStartTime;

    private Date mEndTime;

    public static final long THREE_MONTHS = 3 * 30 * 24 * 60 * 60 * 1000L;

    public static final String TAG = SyncConfigActivity.class.getSimpleName();

    /** {@inheritDoc} */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup layout
        setContentView(R.layout.sync_config);
        final CustomActionbar actionBar = (CustomActionbar) findViewById(R.id.custom_actionbar);

        final Action homeAction = new IntentAction(this, new Intent(this,
                DashboardActivity.class));
        if(actionBar != null){
        	actionBar.setHomeAction(homeAction);
            actionBar.setTitle(getString(R.string.syncSettings));
        }
        

        mRecordsSizeSpinner = (Spinner) findViewById(R.id.recordsSize_spinner);
        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                this, android.R.layout.simple_spinner_item, mRecordsSize);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRecordsSizeSpinner.setAdapter(adapter);
        mRecordsSizeSpinner.setBackgroundColor(Color.GRAY);

        mStartDateButton = (Button) findViewById(R.id.start_date);
        mEndDateButton = (Button) findViewById(R.id.end_date);

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
        // setDate(mStartDateButton, mStartTime);
        // setDate(mEndDateButton, mEndTime);
        populateWhen();

        final SugarCrmApp app = (SugarCrmApp) getApplication();
        final String usr = SugarCrmSettings
                .getUsername(SyncConfigActivity.this);
        if (ContentResolver.isSyncActive(app.getAccount(usr),
                SugarCRMProvider.AUTHORITY)) {
            findViewById(R.id.syncLater).setVisibility(View.GONE);
            findViewById(R.id.cancelSync).setVisibility(View.VISIBLE);
        }

        final String records_size = SugarCrmSettings
                .getFetchRecordsSize(SyncConfigActivity.this);
        final int position = Arrays.binarySearch(mRecordsSize, records_size);
        mRecordsSizeSpinner.setSelection(position);

    }

    /**
     * starts sync for all the modules in the background
     * 
     * @param v
     *            a {@link android.view.View} object.
     */
    public void startSync(final View v) {
        final Bundle extras = new Bundle();
        // extras.putInt(key, value)
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
        extras.putInt(Util.SYNC_TYPE, Util.SYNC_MODULES_DATA);
        final SugarCrmApp app = (SugarCrmApp) getApplication();
        final String usr = SugarCrmSettings
                .getUsername(SyncConfigActivity.this);
        if (ContentResolver.isSyncActive(app.getAccount(usr),
                SugarCRMProvider.AUTHORITY)) {
            final AlertDialog alertDialog = new AlertDialog.Builder(
                    SyncConfigActivity.this).create();
            alertDialog.setTitle(R.string.info);
            alertDialog.setMessage(getString(R.string.syncProgressMsg));
            alertDialog.setIcon(R.drawable.applaunch);
            alertDialog.setButton(getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog,
                                final int which) {
                            SyncConfigActivity.this.finish();
                        }
                    });
            alertDialog.show();
            return;
        }

        savePrefs();
        ContentResolver.requestSync(app.getAccount(usr),
                SugarCRMProvider.AUTHORITY, extras);

        final AlertDialog alertDialog = new AlertDialog.Builder(
                SyncConfigActivity.this).create();
        alertDialog.setTitle(R.string.info);
        alertDialog.setMessage(getString(R.string.syncMsg));
        alertDialog.setIcon(R.drawable.applaunch);
        alertDialog.setButton(getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog,
                            final int which) {
                        SyncConfigActivity.this.finish();
                    }
                });
        alertDialog.show();

    }

    private void savePrefs() {
        final SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        final long startMillis = mStartTime.getTime();
        final long endMillis = mEndTime.getTime();
        final Editor editor = pref.edit();
        editor.putLong(Util.PREF_SYNC_START_TIME, startMillis);
        editor.putLong(Util.PREF_SYNC_END_TIME, endMillis);

        // saving the records size per http request.
        final String selected = mRecordsSize[mRecordsSizeSpinner
                .getSelectedItemPosition()];
        editor.putString(Util.PREF_FETCH_RECORDS_SIZE, selected);

        editor.commit();
    }

    /**
     * cancel Sync
     * 
     * @param v
     *            a {@link android.view.View} object.
     */
    public void cancelSync(final View v) {
        final SugarCrmApp app = (SugarCrmApp) getApplication();
        final String usr = SugarCrmSettings
                .getUsername(SyncConfigActivity.this);
        ContentResolver.cancelSync(app.getAccount(usr),
                SugarCRMProvider.AUTHORITY);
        finish();
    }

    /**
     * sync Later, closes the activity
     * 
     * @param v
     *            a {@link android.view.View} object.
     */
    public void syncLater(final View v) {
        savePrefs();
        setResult(RESULT_CANCELED);
        finish();
    }

    private void populateWhen() {
        setDate(mStartDateButton, mStartTime);
        setDate(mEndDateButton, mEndTime);

        mStartDateButton.setOnClickListener(new DateClickListener(mStartTime));
        mEndDateButton.setOnClickListener(new DateClickListener(mEndTime));
    }

    /**
     * DateListener
     */
    private class DateListener implements OnDateSetListener {
        View mView;

        public DateListener(final View view) {
            mView = view;
        }

        @Override
        public void onDateSet(final DatePicker view, final int year,
                final int month, final int monthDay) {
            // Cache the member variables locally to avoid inner class overhead.
            final Date startDate = mStartTime;
            final Date endDate = mEndTime;

            final Calendar calendar = Calendar.getInstance();

            // Cache the start and end millis so that we limit the number
            // of calls to normalize() and toMillis(), which are fairly
            // expensive.
            long startMillis;
            long endMillis;
            if (mView == mStartDateButton) {
                // The start date was changed.
                // long duartion = endDate.getTime() - startDate.getTime();
                calendar.set(year, month, monthDay);
                startMillis = calendar.getTimeInMillis();
                // endMillis = startMillis + duartion;
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
                    // endDate.setTime(startMillis);
                    endMillis = startMillis;
                }

            }

            startDate.setTime(startMillis);
            endDate.setTime(endMillis);

            setDate(mStartDateButton, startDate);
            setDate(mEndDateButton, endDate);
            final SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            final Editor editor = pref.edit();
            editor.putLong(Util.PREF_SYNC_START_TIME, startMillis);
            editor.putLong(Util.PREF_SYNC_END_TIME, endMillis);
            editor.commit();
        }
    }

    /**
     * DateClickListener
     */
    private class DateClickListener implements View.OnClickListener {
        private final Date mDate;

        public DateClickListener(final Date date) {
            mDate = date;
        }

        @Override
        public void onClick(final View v) {
            final Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(mDate.getTime());

            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int date = calendar.get(Calendar.DATE);

            new DatePickerDialog(SyncConfigActivity.this, new DateListener(v),
                    year, month, date).show();
        }
    }

    private void setDate(final TextView view, final Date date) {
        final int flags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY
                | DateUtils.FORMAT_ABBREV_MONTH
                | DateUtils.FORMAT_ABBREV_WEEKDAY;
        view.setText(DateUtils.formatDateTime(this, date.getTime(), flags));
    }
}
