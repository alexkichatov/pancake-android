package com.imaginea.android.sugarcrm;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;
import com.imaginea.android.sugarcrm.util.ContentUtils;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * <p>
 * SugarCrmSettings class.
 * </p>
 * 
 */
public class SugarCrmSettings extends Activity {

    private static final String LOG_TAG = "SugarCrmSettings";

    private static Map<String, Object> savedSettings = null;

    private Spinner mModuleNameSpinner;

    private Spinner mFieldNameSpinner;

    private Spinner mSortOrderSpinner;

    private Button mStartDateButton;

    private Button mEndDateButton;

    private Date startDate;
    private Date endDate;

    private Spinner mRecordsSizeSpinner;
    private CheckBox enableAlarm;
    private EditText crmUrl;
    private EditText loginUsr;

    private final String[] mRecordsSize = { "500", "1000", "2000", "5000",
            "10000", "ALL" };

    // cache the time
    private Date mStartTime;

    private Date mEndTime;

    public static final long THREE_MONTHS = 3 * 30 * 24 * 60 * 60 * 1000L;

    private final Map<String, String> fieldMap = new HashMap<String, String>();

    private final Map<String, String> orderMap = new HashMap<String, String>();;

    private SugarCrmApp app;

    private int selectedModuleIndex;

    private int selectedFieldIndex;

    private int selectedOrderIndex;

    private Dialog dialog;

    public Button apply, cancel;;

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.settings_dialog);

        /* Set the Apply Button to Save the Settings */
        apply = (Button) dialog.findViewById(R.id.dialogButtonOK);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveCurrentSettings(getApplicationContext());
                saveSortOrder();

                // invalidate the session if the current settings changes
                if (currentSettingsChanged(SugarCrmSettings.this)) {
                    final SugarCrmApp app = ((SugarCrmApp) getApplicationContext());
                    app.setSessionId(null);
                }
                dialog.dismiss();
            }
        });
        /* Set the Cancel Button to dismiss the Dialog */
        cancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

        /* set All the layout views */
        setDialogLayoutViews();

        /* Set the Sorting View */
        setSortSpinner();
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

            if (!getUsername(context).equals(
                    savedSettings.get(Util.PREF_USERNAME)))
                return true;

            return false;
        } finally {

        }
    }

    private void setDialogLayoutViews() {
        /* LOGIN */
        loginUsr = (EditText) dialog.findViewById(R.id.loginUsername);
        crmUrl = (EditText) dialog.findViewById(R.id.crmUrl);

        /* NOTOFICATION */
        enableAlarm = (CheckBox) dialog.findViewById(R.id.enableAlarm);

        /* SORTING */
        mModuleNameSpinner = (Spinner) dialog.findViewById(R.id.orderByModule);
        mFieldNameSpinner = (Spinner) dialog.findViewById(R.id.orderByName);
        mSortOrderSpinner = (Spinner) dialog.findViewById(R.id.orderBysort);

        /* SYNCRONIZING */
        mRecordsSizeSpinner = (Spinner) dialog.findViewById(R.id.recordsize);
        mStartDateButton = (Button) dialog.findViewById(R.id.startDate);
        mEndDateButton = (Button) dialog.findViewById(R.id.endDate);

    }

    private void setLastDefaultSettings() {

        if (savedSettings != null && !savedSettings.isEmpty()) {
            /* Set Login user name */
            loginUsr.setText(savedSettings.get(Util.PREF_USERNAME).toString());
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
            loginUsr.setText(getUsername(getApplicationContext()));
            crmUrl.setText(getSugarRestUrl(getApplicationContext()));
            enableAlarm.setChecked(PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext())
                    .getBoolean(Util.PREF_ALARM_STATE, false));

        }
    }

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

        final SugarCrmApp app = (SugarCrmApp) getApplication();
        final String usr = SugarCrmSettings
                .getUsername(getApplicationContext());

        if (ContentResolver.isSyncActive(app.getAccount(usr),
                SugarCRMProvider.AUTHORITY)) {

        }

        final String records_size = SugarCrmSettings
                .getFetchRecordsSize(getApplicationContext());
        final int position = Arrays.binarySearch(mRecordsSize, records_size);
        mRecordsSizeSpinner.setSelection(position);
    }

    private void setSortSpinner() {
        app = (SugarCrmApp) getApplication();

        // get the modules that are displayed in the dashboard
        final List<String> moduleList = ContentUtils.getModuleList(this);
        final String[] moduleNames = new String[moduleList.size()];
        moduleList.toArray(moduleNames);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                this, android.R.layout.simple_spinner_dropdown_item,
                moduleNames);
        mModuleNameSpinner.setAdapter(adapter);

        // disable the field name spinner until the user selects the module
        mFieldNameSpinner.setEnabled(false);

        // order vs sql syntax
        orderMap.put(getString(R.string.ascending), "ASC");
        orderMap.put(getString(R.string.descending), "DESC");

        adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_dropdown_item, new String[] {
                        getString(R.string.ascending),
                        getString(R.string.descending) });
        mSortOrderSpinner.setAdapter(adapter);
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // disable the sortOrder spinner until the user selects the fieldName
        mSortOrderSpinner.setEnabled(false);

        mModuleNameSpinner
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                            View view, int position, long id) {

                        // store the selected module index
                        selectedModuleIndex = position;

                        final String moduleName = moduleNames[position];
                        // get the fields that are in the LIST_PROJECTION of the
                        // module
                        final String[] moduleFields = ContentUtils
                                .getModuleListSelections(moduleName);
                        // get the ModuleField objects for the module
                        final Map<String, ModuleField> map = ContentUtils
                                .getModuleFields(getApplicationContext(),
                                        moduleName);
                        // get the labels of the module fields to display
                        final String[] moduleFieldsChoice = new String[moduleFields.length];
                        for (int i = 0; i < moduleFields.length; i++) {
                            // add the module field label to be displayed in the
                            // choice
                            // menu
                            final ModuleField modField = map
                                    .get(moduleFields[i]);
                            if (modField != null) {
                                moduleFieldsChoice[i] = modField.getLabel();
                                // fieldMap: label vs name
                                fieldMap.put(moduleFieldsChoice[i],
                                        moduleFields[i]);
                            } else {
                                moduleFieldsChoice[i] = "";
                            }

                            if (moduleFieldsChoice[i].indexOf(":") > 0) {
                                moduleFieldsChoice[i] = moduleFieldsChoice[i].substring(
                                        0, moduleFieldsChoice[i].length() - 1);
                                fieldMap.put(moduleFieldsChoice[i],
                                        moduleFields[i]);
                            }
                        }
                        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                                getBaseContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                moduleFieldsChoice);
                        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mFieldNameSpinner.setAdapter(adapter);
                        mFieldNameSpinner.setEnabled(true);

                        final Map<String, String> sortOrderMap = app
                                .getModuleSortOrder(moduleName);
                        if (sortOrderMap != null) {
                            for (final Entry<String, String> entry : sortOrderMap
                                    .entrySet()) {
                                mFieldNameSpinner
                                        .setSelection(((ArrayAdapter<CharSequence>) mFieldNameSpinner
                                                .getAdapter())
                                                .getPosition(entry.getKey()));
                                mSortOrderSpinner
                                        .setSelection(((ArrayAdapter<CharSequence>) mSortOrderSpinner
                                                .getAdapter())
                                                .getPosition(entry.getValue()));
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        mFieldNameSpinner
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                            View view, int position, long id) {
                        // store the selected field index
                        selectedFieldIndex = position;
                        mSortOrderSpinner.setEnabled(true);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        mSortOrderSpinner
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                            View view, int position, long id) {
                        // store the selected order index
                        selectedOrderIndex = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
    }

    /**
     * <p>
     * getUsername
     * </p>
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
     * <p>
     * getFetchRecordsSize
     * </p>
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
     * // TODO - optimize once loaded instead of calling package manager
     * everytime
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
     * settings changed
     * 
     * @param context
     *            a {@link android.content.Context} object.
     */
    private void saveCurrentSettings(Context context) {
        savedSettings = new HashMap<String, Object>();
        savedSettings.clear();
        savedSettings.put(Util.PREF_REST_URL, loginUsr.getText().toString());
        savedSettings.put(Util.PREF_USERNAME, crmUrl.getText().toString());
        savedSettings.put(Util.PREF_ALARM_STATE, enableAlarm.isChecked());

        savedSettings.put(Util.PREF_SYNC_START_TIME, startDate);
        savedSettings.put(Util.PREF_SYNC_END_TIME, endDate);

    }

    private void populateWhen() {
        setDate(mStartDateButton, mStartTime);
        setDate(mEndDateButton, mEndTime);

        mStartDateButton.setOnClickListener(new DateClickListener(mStartTime));
        mEndDateButton.setOnClickListener(new DateClickListener(mEndTime));
    }

    private void setDate(final TextView view, final Date date) {
        final int flags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY
                | DateUtils.FORMAT_ABBREV_MONTH
                | DateUtils.FORMAT_ABBREV_WEEKDAY;
        view.setText(DateUtils.formatDateTime(this, date.getTime(), flags));
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

            new DatePickerDialog(dialog.getContext(), new DateListener(v),
                    year, month, date).show();
        }
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

    private void saveSortOrder() {
        Log.i(LOG_TAG, "saveSortOrder : ");

        final String module = (String) mModuleNameSpinner.getAdapter().getItem(
                selectedModuleIndex);
        final String fieldName = fieldMap.get(mFieldNameSpinner.getAdapter()
                .getItem(selectedFieldIndex));
        final String sortBy = orderMap.get(mSortOrderSpinner.getAdapter()
                .getItem(selectedOrderIndex));
        Log.i(LOG_TAG, "module : " + module + " field : " + fieldName
                + " order : " + sortBy);

        app.setModuleSortOrder(module, fieldName, sortBy);

    }

}
