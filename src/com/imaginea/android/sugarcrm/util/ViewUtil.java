/*******************************************************************************
 * Copyright (c) 2013 Vasavi, chander.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Vasavi, chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : ViewUtil 
 * Description :
 *           Utility class for commons views required by activities
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * The Class ViewUtil.
 */
public class ViewUtil {

    /** handle to a progress dialog used by all the activities. */
    private static ProgressDialog mProgressDialog;

    /**
     * show ProgressDialog.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param startMsg
     *            a {@link java.lang.String} object.
     */
    public static void showProgressDialog(final Context context,
            final String startMsg) {
        showProgressDialog(context, startMsg, true);
    }

    /**
     * show ProgressDialog.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param startMsg
     *            a {@link java.lang.String} object.
     * @param indeterminate
     *            a boolean.
     */
    public static void showProgressDialog(final Context context,
            final String startMsg, final boolean indeterminate) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(indeterminate);
        mProgressDialog.setMessage(startMsg);
        mProgressDialog.show();
    }

    /**
     * get ProgressDialog.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param startMsg
     *            a {@link java.lang.String} object.
     * @param indeterminate
     *            a boolean.
     * @return a {@link android.app.ProgressDialog} object.
     */
    public static ProgressDialog getProgressDialog(final Context context,
            final String startMsg, final boolean indeterminate) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(indeterminate);
        progressDialog.setMessage(startMsg);
        return progressDialog;

    }

    /**
     * cancel ProgressBar.
     */
    public static void cancelProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    /**
     * helper method to display a toast specified by the resource id
     * (strings.xml)
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param resid
     *            a int.
     */
    public static void makeToast(final Context context, final int resid) {
        final Toast toast = Toast.makeText(context, resid, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * helper method to display a toast specified by the resource id
     * (strings.xml)
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param str
     *            a {@link java.lang.CharSequence} object.
     */
    public static void makeToast(final Context context, final CharSequence str) {
        final Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * showFormattedToast.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param id
     *            a int.
     * @param args
     *            a {@link java.lang.Object} object.
     */
    public static void showFormattedToast(final Context context, final int id,
            final Object... args) {
        Toast.makeText(context, String.format(context.getString(id), args),
                Toast.LENGTH_LONG).show();
    }

    /**
     * dismissVirtualKeyboard.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     * @param view
     *            a {@link android.view.View} object.
     */
    public static void dismissVirtualKeyboard(final Context context,
            final View view) {

        final InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager.isActive(view)) {
            inputManager.hideSoftInputFromWindow(
                    view.getApplicationWindowToken(), 0);
        }
    }

    /**
     * Checks if is honeycomb.
     * 
     * @return true, if is honeycomb
     */
    public static boolean isHoneycomb() {
        // Can use static final constants like HONEYCOMB, declared in
        // later versionsof the OS since they are inlined at compile time. This
        // is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Checks if is tablet.
     * 
     * @param context
     *            the context
     * @return true, if is tablet
     */
    public static boolean isTablet(final Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Checks if is honeycomb tablet.
     * 
     * @param context
     *            the context
     * @return true, if is honeycomb tablet
     */
    public static boolean isHoneycombTablet(final Context context) {
        return isHoneycomb() && isTablet(context);
    }
}
