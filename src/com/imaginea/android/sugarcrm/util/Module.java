/*******************************************************************************
 * Copyright (c) 2013
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : Module
 * Description : 
 *              The Module Class
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import java.util.List;

/**
 * The Class Module.
 */
public class Module {

    /** The m module name. */
    private String mModuleName;

    /** The m module fields. */
    private List<ModuleField> mModuleFields;

    /** The m link fields. */
    private List<LinkField> mLinkFields;

    /**
     * Instantiates a new module.
     */
    public Module() {

    }

    /**
     * Instantiates a new module.
     * 
     * @param moduleName
     *            the module name
     * @param moduleFields
     *            the module fields
     * @param linkFields
     *            the link fields
     */
    public Module(String moduleName, List<ModuleField> moduleFields,
            List<LinkField> linkFields) {
        super();
        mModuleName = moduleName;
        mModuleFields = moduleFields;
        mLinkFields = linkFields;
    }

    /**
     * Gets the module fields.
     * 
     * @return the module fields
     */
    public List<ModuleField> getModuleFields() {
        return mModuleFields;
    }

    /**
     * Sets the module fields.
     * 
     * @param moduleFields
     *            the new module fields
     */
    public void setModuleFields(List<ModuleField> moduleFields) {
        mModuleFields = moduleFields;
    }

    /**
     * Gets the link fields.
     * 
     * @return the link fields
     */
    public List<LinkField> getLinkFields() {
        return mLinkFields;
    }

    /**
     * Sets the link fields.
     * 
     * @param linkFields
     *            the new link fields
     */
    public void setLinkFields(List<LinkField> linkFields) {
        mLinkFields = linkFields;
    }

    /**
     * Gets the module name.
     * 
     * @return the module name
     */
    public String getModuleName() {
        return mModuleName;
    }

}
