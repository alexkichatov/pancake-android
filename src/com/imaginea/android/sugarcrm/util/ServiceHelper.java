/*******************************************************************************
 * Copyright (c) 2013 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : ServiceHelper 
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.services.SugarService;

/**
 * The Class ServiceHelper.
 */
public class ServiceHelper {

    /**
     * Instantiates a new service helper.
     */
    private ServiceHelper() {

    }

    /**
     * startService
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param uri
     *            a {@link android.net.Uri} object.
     * @param module
     *            a {@link java.lang.String} object.
     * @param projection
     *            an array of {@link java.lang.String} objects.
     * @param sortOrder
     *            a {@link java.lang.String} object.
     */
    public static void startService(Context context, Uri uri, String module,
            String[] projection, String sortOrder) {
        final Intent serviceIntent = new Intent(context, SugarService.class);
        serviceIntent.setData(uri);
        serviceIntent.putExtra(Util.COMMAND, Util.GET);
        serviceIntent.putExtra(RestConstants.MODULE_NAME, module);
        serviceIntent.putExtra(Util.PROJECTION, projection);
        serviceIntent.putExtra(Util.SORT_ORDER, sortOrder);
        context.startService(serviceIntent);
    }

    /**
     * startServiceForDelete
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param uri
     *            a {@link android.net.Uri} object.
     * @param module
     *            a {@link java.lang.String} object.
     * @param beanId
     *            a {@link java.lang.String} object.
     */
    public static void startServiceForDelete(Context context, Uri uri,
            String module, String beanId) {
        final Intent serviceIntent = new Intent(context, SugarService.class);
        serviceIntent.setData(uri);
        final Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(RestConstants.BEAN_ID, beanId);
        nameValuePairs.put(ModuleFields.DELETED, Util.DELETED_ITEM);

        serviceIntent.putExtra(Util.COMMAND, Util.DELETE);
        serviceIntent.putExtra(RestConstants.MODULE_NAME, module);
        serviceIntent.putExtra(RestConstants.BEAN_ID, beanId);
        serviceIntent.putExtra(RestConstants.NAME_VALUE_LIST,
                (Serializable) nameValuePairs);
        context.startService(serviceIntent);
    }

    /**
     * startServiceForUpdate
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param uri
     *            a {@link android.net.Uri} object.
     * @param module
     *            a {@link java.lang.String} object.
     * @param beanId
     *            a {@link java.lang.String} object.
     * @param nameValueList
     *            a {@link java.util.Map} object.
     */
    public static void startServiceForUpdate(Context context, Uri uri,
            String module, String beanId, Map<String, String> nameValueList) {
        final Intent serviceIntent = new Intent(context, SugarService.class);
        serviceIntent.setData(uri);

        serviceIntent.putExtra(Util.COMMAND, Util.UPDATE);
        serviceIntent.putExtra(RestConstants.BEAN_ID, beanId);
        serviceIntent.putExtra(RestConstants.MODULE_NAME, module);
        serviceIntent.putExtra(RestConstants.NAME_VALUE_LIST,
                (Serializable) nameValueList);
        context.startService(serviceIntent);
    }

    /**
     * startServiceForInsert
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param uri
     *            a {@link android.net.Uri} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param nameValueList
     *            a {@link java.util.Map} object.
     */
    public static void startServiceForInsert(Context context, Uri uri,
            String moduleName, Map<String, String> nameValueList) {
        final Intent serviceIntent = new Intent(context, SugarService.class);
        serviceIntent.setData(uri);

        serviceIntent.putExtra(Util.COMMAND, Util.INSERT);
        serviceIntent.putExtra(RestConstants.MODULE_NAME, moduleName);
        serviceIntent.putExtra(RestConstants.NAME_VALUE_LIST,
                (Serializable) nameValueList);
        context.startService(serviceIntent);
    }
}
