/*******************************************************************************
 * Copyright (c) 2013 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : ModuleFieldBean
 * Description : 
 *              The ModuleFieldBean Class
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

/**
 * The Class ModuleFieldBean.
 */
public class ModuleFieldBean {

    /** The m module field. */
    private ModuleField mModuleField;

    /** The m module field id. */
    private int mModuleFieldId;

    /** The m field sort id. */
    private int mFieldSortId;

    /** The m group id. */
    private int mGroupId;

    /**
     * Instantiates a new module field bean.
     * 
     * @param moduleField
     *            the module field
     * @param moduleFieldId
     *            the module field id
     * @param fieldSortId
     *            the field sort id
     * @param groupId
     *            the group id
     */
    public ModuleFieldBean(ModuleField moduleField, int moduleFieldId,
            int fieldSortId, int groupId) {
        mModuleField = moduleField;
        mModuleFieldId = moduleFieldId;
        mFieldSortId = fieldSortId;
        mGroupId = groupId;
    }

    /**
     * Gets the module field.
     * 
     * @return the module field
     */
    public ModuleField getModuleField() {
        return mModuleField;
    }

    /**
     * Sets the module field.
     * 
     * @param moduleField
     *            the new module field
     */
    public void setModuleField(ModuleField moduleField) {
        mModuleField = moduleField;
    }

    /**
     * Gets the module field id.
     * 
     * @return the module field id
     */
    public int getModuleFieldId() {
        return mModuleFieldId;
    }

    /**
     * Sets the module field id.
     * 
     * @param moduleFieldId
     *            the new module field id
     */
    public void setModuleFieldId(int moduleFieldId) {
        mModuleFieldId = moduleFieldId;
    }

    /**
     * Gets the field sort id.
     * 
     * @return the field sort id
     */
    public int getFieldSortId() {
        return mFieldSortId;
    }

    /**
     * Sets the field sort id.
     * 
     * @param fieldSortId
     *            the new field sort id
     */
    public void setFieldSortId(int fieldSortId) {
        mFieldSortId = fieldSortId;
    }

    /**
     * Gets the group id.
     * 
     * @return the group id
     */
    public int getGroupId() {
        return mGroupId;
    }

    /**
     * Sets the group id.
     * 
     * @param groupId
     *            the new group id
     */
    public void setGroupId(int groupId) {
        mGroupId = groupId;
    }

}
