/*******************************************************************************
 * Copyright (c) 2013 Asha, Muralidaran.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Asha, Muralidaran - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : AlarmReceiver 
 * Description :
 *           AlarmReceiver Class
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.imaginea.android.sugarcrm.provider.SugarCRMContent.AlarmColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.MeetingsColumns;
import com.imaginea.android.sugarcrm.rest.RestConstants;

/**
 * The Class AlarmReceiver.
 */
public class AlarmReceiver extends BroadcastReceiver {

    /** The Constant ALARM_INTENT_ACTION. */
    public static final String ALARM_INTENT_ACTION = "com.imaginea.android.sugarcrm.ALARM_RECEIVED";

    /*
     * (non-Javadoc)
     * 
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        final boolean isAlarmEnabled = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(
                        Util.PREF_ALARM_STATE, false);
        final Bundle extras = intent
                .getBundleExtra(AlarmUtils.INTENT_EXTRA_EVENT_DETAILS);
        final String mRowId = extras.getString(AlarmColumns.ID);
        final String mModuleName = extras.getString(RestConstants.MODULE_NAME);
        if (isAlarmEnabled) {
            final int eventAlarmState = AlarmUtils.isAlarmEnabled(context,
                    mRowId);
            if (eventAlarmState == AlarmUtils.ALARM_STATE_ENABLED
                    || eventAlarmState == AlarmUtils.ALARM_STATE_NA) {
                showNotification(context, extras);
            } else {
                AlarmUtils.fireNextAlarm(context, mRowId, mModuleName);
            }
        } else {
            AlarmUtils.fireNextAlarm(context, mRowId, mModuleName);
        }
    }

    /**
     * Show notification.
     * 
     * @param context
     *            the context
     * @param extras
     *            the extras
     */
    private void showNotification(Context context, Bundle extras) {
        final String mTitle = extras.getString(MeetingsColumns.NAME);
        final String mMessage = extras.getString(MeetingsColumns.DESCRIPTION);
        final String mTickerText = extras
                .getString(MeetingsColumns.ASSIGNED_USER_NAME);
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        final Intent intent = new Intent();
        final PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        final Notification n = new Notification(
                android.R.drawable.stat_notify_sync_noanim, mTickerText,
                System.currentTimeMillis());
        n.setLatestEventInfo(context, mTitle, mMessage, pendingIntent);
        n.flags = Notification.FLAG_AUTO_CANCEL;
        n.defaults |= Notification.DEFAULT_SOUND;
        nm.notify(0, n);
    }
}
