package com.imaginea.android.sugarcrm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.imaginea.android.sugarcrm.services.AccountRemovalService;

public class AccountRemovalReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        AccountRemovalService.processBroadcastIntent(context, intent);

    }

}
