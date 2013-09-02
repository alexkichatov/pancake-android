/*******************************************************************************
 * Copyright (c) 2013 Vasavi, chander.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Vasavi, chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : SugarCrmException 
 * Description :
 *           SugarCrmException class.
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

/**
 * The Class SugarCrmException.
 */
public class SugarCrmException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The m name. */
    private String mName;

    /** The m description. */
    private final String mDescription;

    /**
     * Constructor for SugarCrmException.
     * 
     * @param name
     *            a {@link java.lang.String} object.
     * @param desc
     *            a {@link java.lang.String} object.
     */
    public SugarCrmException(final String name, final String desc) {
        mName = name;
        mDescription = desc;
    }

    /**
     * Constructor for SugarCrmException.
     * 
     * @param desc
     *            a {@link java.lang.String} object.
     */
    public SugarCrmException(final String desc) {
        mDescription = desc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#toString()
     */
    @Override
    public String toString() {
        return mName + " : " + mDescription;
    }

    /**
     * getName
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return mName;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return mDescription;
    }

}
