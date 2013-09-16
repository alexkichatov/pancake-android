/*******************************************************************************
 * Copyright (c) 2013 Vasavi
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Vasavi, chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : SimpleMenuItem 
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/**
 * A <em>really</em> dumb implementation of the {@link MenuItem} interface,
 * that's only useful for our old-actionbar purposes. See
 * <code>com.android.internal.view.menu.MenuItemImpl</code> in AOSP for a more
 * complete implementation.
 */
public class SimpleMenuItem implements MenuItem {

    /** The m menu. */
    private final SimpleMenu mMenu;

    /** The m id. */
    private final int mId;

    /** The m order. */
    private final int mOrder;

    /** The m title. */
    private CharSequence mTitle;

    /** The m title condensed. */
    private CharSequence mTitleCondensed;

    /** The m icon drawable. */
    private Drawable mIconDrawable;

    /** The m icon res id. */
    private int mIconResId = 0;

    /** The m enabled. */
    private boolean mEnabled = true;

    /**
     * Instantiates a new simple menu item.
     * 
     * @param menu
     *            the menu
     * @param id
     *            the id
     * @param order
     *            the order
     * @param title
     *            the title
     */
    public SimpleMenuItem(SimpleMenu menu, int id, int order, CharSequence title) {
        mMenu = menu;
        mId = id;
        mOrder = order;
        mTitle = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#getItemId()
     */
    @Override
    public int getItemId() {
        return mId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#getOrder()
     */
    @Override
    public int getOrder() {
        return mOrder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setTitle(java.lang.CharSequence)
     */
    @Override
    public MenuItem setTitle(CharSequence title) {
        mTitle = title;
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setTitle(int)
     */
    @Override
    public MenuItem setTitle(int titleRes) {
        return setTitle(mMenu.getContext().getString(titleRes));
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#getTitle()
     */
    @Override
    public CharSequence getTitle() {
        return mTitle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setTitleCondensed(java.lang.CharSequence)
     */
    @Override
    public MenuItem setTitleCondensed(CharSequence title) {
        mTitleCondensed = title;
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#getTitleCondensed()
     */
    @Override
    public CharSequence getTitleCondensed() {
        return mTitleCondensed != null ? mTitleCondensed : mTitle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setIcon(android.graphics.drawable.Drawable)
     */
    @Override
    public MenuItem setIcon(Drawable icon) {
        mIconResId = 0;
        mIconDrawable = icon;
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setIcon(int)
     */
    @Override
    public MenuItem setIcon(int iconResId) {
        mIconDrawable = null;
        mIconResId = iconResId;
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#getIcon()
     */
    @Override
    public Drawable getIcon() {
        if (mIconDrawable != null) {
            return mIconDrawable;
        }

        if (mIconResId != 0) {
            return mMenu.getResources().getDrawable(mIconResId);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setEnabled(boolean)
     */
    @Override
    public MenuItem setEnabled(boolean enabled) {
        mEnabled = enabled;
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    // No-op operations. We use no-ops to allow inflation from menu XML.

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#getGroupId()
     */
    @Override
    public int getGroupId() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#getActionView()
     */
    @Override
    public View getActionView() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setIntent(android.content.Intent)
     */
    @Override
    public MenuItem setIntent(Intent intent) {
        // Noop
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#getIntent()
     */
    @Override
    public Intent getIntent() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setShortcut(char, char)
     */
    @Override
    public MenuItem setShortcut(char c, char c1) {
        // Noop
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setNumericShortcut(char)
     */
    @Override
    public MenuItem setNumericShortcut(char c) {
        // Noop
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#getNumericShortcut()
     */
    @Override
    public char getNumericShortcut() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setAlphabeticShortcut(char)
     */
    @Override
    public MenuItem setAlphabeticShortcut(char c) {
        // Noop
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#getAlphabeticShortcut()
     */
    @Override
    public char getAlphabeticShortcut() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setCheckable(boolean)
     */
    @Override
    public MenuItem setCheckable(boolean b) {
        // Noop
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#isCheckable()
     */
    @Override
    public boolean isCheckable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setChecked(boolean)
     */
    @Override
    public MenuItem setChecked(boolean b) {
        // Noop
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#isChecked()
     */
    @Override
    public boolean isChecked() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setVisible(boolean)
     */
    @Override
    public MenuItem setVisible(boolean b) {
        // Noop
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#isVisible()
     */
    @Override
    public boolean isVisible() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#hasSubMenu()
     */
    @Override
    public boolean hasSubMenu() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#getSubMenu()
     */
    @Override
    public SubMenu getSubMenu() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.view.MenuItem#setOnMenuItemClickListener(android.view.MenuItem
     * .OnMenuItemClickListener)
     */
    @Override
    public MenuItem setOnMenuItemClickListener(
            OnMenuItemClickListener onMenuItemClickListener) {
        // Noop
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#getMenuInfo()
     */
    @Override
    public ContextMenu.ContextMenuInfo getMenuInfo() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setShowAsAction(int)
     */
    @Override
    public void setShowAsAction(int i) {
        // Noop
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setActionView(android.view.View)
     */
    @Override
    public MenuItem setActionView(View view) {
        // Noop
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setActionView(int)
     */
    @Override
    public MenuItem setActionView(int i) {
        // Noop
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#collapseActionView()
     */
    @Override
    public boolean collapseActionView() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#expandActionView()
     */
    @Override
    public boolean expandActionView() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#getActionProvider()
     */
    @Override
    public ActionProvider getActionProvider() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#isActionViewExpanded()
     */
    @Override
    public boolean isActionViewExpanded() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setActionProvider(android.view.ActionProvider)
     */
    @Override
    public MenuItem setActionProvider(ActionProvider arg0) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.view.MenuItem#setOnActionExpandListener(android.view.MenuItem
     * .OnActionExpandListener)
     */
    @Override
    public MenuItem setOnActionExpandListener(OnActionExpandListener arg0) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.MenuItem#setShowAsActionFlags(int)
     */
    @Override
    public MenuItem setShowAsActionFlags(int arg0) {
        return null;
    }

}
