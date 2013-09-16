/*******************************************************************************
 * Copyright (c) 2013 Asha, Muralidaran.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Asha, Muralidaran - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : AccountRemovalReceiver 
 * Description : 
                handles receiver for Account removal.
 ******************************************************************************/
package com.imaginea.android.sugarcrm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.imaginea.android.sugarcrm.services.AccountRemovalService;

/**
 * The Class AccountRemovalReceiver.
 */
public class AccountRemovalReceiver extends BroadcastReceiver {

    /*
     * (non-Javadoc)
     * 
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        AccountRemovalService.processBroadcastIntent(context, intent);

    }

}
