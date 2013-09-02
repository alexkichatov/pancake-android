/*******************************************************************************
 * Copyright (c) 2013 Asha, Muralidaran.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Asha, Muralidaran - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : AlarmUtils 
 * Description :
 *           AlarmUtils Class
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AlarmColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.MeetingsColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMProvider;

/**
 * The Class AlarmUtils.
 */
public class AlarmUtils {

    /** The Constant INTENT_EXTRA_EVENT_DETAILS. */
    public static final String INTENT_EXTRA_EVENT_DETAILS = "eventextras";

    /** The Constant ALARM_STATE_ENABLED. */
    public static final int ALARM_STATE_ENABLED = 1;

    /** The Constant ALARM_STATE_DISABLED. */
    public static final int ALARM_STATE_DISABLED = 0;

    /** The Constant ALARM_STATE_NA. */
    public static final int ALARM_STATE_NA = -1;

    /**
     * Sets the event alarm.
     * 
     * @param context
     *            the context
     * @param rowId
     *            the row id
     * @param time
     *            the time
     * @param eventExtras
     *            the event extras
     */
    public static void setEventAlarm(Context context, String rowId, long time,
            Bundle eventExtras) {
        final AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ALARM_INTENT_ACTION);
        intent.putExtra(AlarmColumns.ID, rowId);
        intent.setData(Uri.parse(rowId));
        intent.putExtra(INTENT_EXTRA_EVENT_DETAILS, eventExtras);
        final PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
    }

    /**
     * Cancel event alarm.
     * 
     * @param context
     *            the context
     * @param rowId
     *            the row id
     */
    public static void cancelEventAlarm(Context context, String rowId) {
        final AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ALARM_INTENT_ACTION);
        intent.putExtra(AlarmColumns.ID, rowId);
        intent.setData(Uri.parse(rowId));
        final PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pi);
    }

    /**
     * Fire first alarm.
     * 
     * @param context
     *            the context
     * @param mModuleName
     *            the m module name
     */
    public static void fireFirstAlarm(Context context, String mModuleName) {
        fireNextAlarm(context, "0", mModuleName);
    }

    /**
     * Fire next alarm.
     * 
     * @param context
     *            the context
     * @param currentRowId
     *            the current row id
     * @param mModuleName
     *            the m module name
     */
    public static void fireNextAlarm(Context context, String currentRowId,
            String mModuleName) {
        final String nextRowId = Integer.toString(Integer
                .parseInt(currentRowId) + 1);
        final Bundle eventDetails = fetchMeetingDetails(context, nextRowId,
                mModuleName);
        setEventAlarm(context, nextRowId,
                getTime(eventDetails.getString(MeetingsColumns.START_DATE)),
                eventDetails);
    }

    /**
     * Checks if is alarm enabled.
     * 
     * @param context
     *            the context
     * @param rowId
     *            the row id
     * @return the int
     */
    public static int isAlarmEnabled(Context context, String rowId) {
        int notificationStatus = -1;
        final DatabaseHelper mDbHelper = new DatabaseHelper(context);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final Cursor mCursor = db.query(DatabaseHelper.ALARM_TABLE_NAME, null,
                AlarmColumns.ID + "=\"" + rowId + "\"", null, null, null, null);
        if (mCursor != null && mCursor.moveToFirst()) {
            notificationStatus = mCursor.getInt(mCursor
                    .getColumnIndex(AlarmColumns.ALARM_STATE));
            mCursor.close();
        }
        db.close();
        return notificationStatus;
    }

    /**
     * Make alarm entry.
     * 
     * @param context
     *            the context
     * @param rowId
     *            the row id
     * @param alarmState
     *            the alarm state
     */
    public static void makeAlarmEntry(Context context, String rowId,
            int alarmState) {
        final DatabaseHelper mDbHelper = new DatabaseHelper(context);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(AlarmColumns.ID, rowId);
        values.put(AlarmColumns.ALARM_STATE, alarmState);
        final String whereClause = Util.ROW_ID + "=\"" + rowId + "\"";
        long n = db.update(DatabaseHelper.ALARM_TABLE_NAME, values,
                whereClause, null);
        if (n <= 0) {
            n = db.insert(DatabaseHelper.ALARM_TABLE_NAME, null, values);
        }

        db.close();
        return;
    }

    /**
     * Fetch meeting details.
     * 
     * @param context
     *            the context
     * @param mRowId
     *            the m row id
     * @param mModuleName
     *            the m module name
     * @return the bundle
     */
    public static Bundle fetchMeetingDetails(Context context, String mRowId,
            String mModuleName) {
        final Bundle extras = new Bundle();
        final Uri uri = Uri.withAppendedPath(
                Uri.parse("content://" + SugarCRMProvider.AUTHORITY + "/"
                        + Util.MEETINGS), mRowId);

        final Cursor mCursor = context.getContentResolver().query(uri,
                ContentUtils.getModuleProjections(mModuleName), null, null,
                ContentUtils.getModuleSortOrder(mModuleName));
        if (mCursor != null && mCursor.moveToFirst()) {
            final String title = mCursor.getString(mCursor
                    .getColumnIndex(MeetingsColumns.NAME));
            extras.putString(MeetingsColumns.NAME, title);
            final String message = mCursor.getString(mCursor
                    .getColumnIndex(MeetingsColumns.DESCRIPTION));
            extras.putString(MeetingsColumns.DESCRIPTION, message);
            final String ticker = mCursor.getString(mCursor
                    .getColumnIndex(MeetingsColumns.ASSIGNED_USER_NAME));
            extras.putString(MeetingsColumns.ASSIGNED_USER_NAME, ticker);
            final String date = mCursor.getString(mCursor
                    .getColumnIndex(MeetingsColumns.START_DATE));
            extras.putString(MeetingsColumns.START_DATE, date);
        }

        if (mCursor != null) {
            mCursor.close();
        }
        return extras;
    }

    /**
     * Gets the time.
     * 
     * @param dateString
     *            the date string
     * @return the time
     */
    public static long getTime(String dateString) {
        final SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        try {
            final Date date = formatter.parse(dateString);
            return date.getTime();
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return SystemClock.elapsedRealtime();
    }

    /**
     * Enable alarm setting.
     * 
     * @param context
     *            the context
     */
    public static void enableAlarmSetting(Context context) {
        final SharedPreferences mPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        final Editor editor = mPreferences.edit();
        editor.putBoolean(Util.PREF_ALARM_STATE, true);
        editor.commit();
    }
}
