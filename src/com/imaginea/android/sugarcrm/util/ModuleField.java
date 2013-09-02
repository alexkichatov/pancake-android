/*******************************************************************************
 * Copyright (c) 2013 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : ModuleField
 * Description : 
 *              The ModuleField Class
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

/**
 * The Class ModuleField.
 */
public class ModuleField {

    /** The m name. */
    private final String mName;

    /** The m type. */
    private final String mType;

    /** The m label. */
    private final String mLabel;

    /** The m is required. */
    private final boolean mIsRequired;

    /**
     * Instantiates a new module field.
     * 
     * @param name
     *            the name
     * @param type
     *            the type
     * @param label
     *            the label
     * @param isRequired
     *            the is required
     */
    public ModuleField(String name, String type, String label,
            boolean isRequired) {
        mName = name;
        mType = type;
        mLabel = label;
        mIsRequired = isRequired;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return mType;
    }

    /**
     * Gets the label.
     * 
     * @return the label
     */
    public String getLabel() {
        return mLabel;
    }

    /**
     * Checks if is required.
     * 
     * @return true, if is required
     */
    public boolean isRequired() {
        return mIsRequired;
    }

}
