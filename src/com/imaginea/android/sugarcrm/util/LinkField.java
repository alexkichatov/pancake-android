/*******************************************************************************
 * Copyright (c) 2013 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : LinkField
 * Description : 
 *              The LinkField Class
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

// TODO: Auto-generated Javadoc
/**
 * The Class LinkField.
 */
public class LinkField {

    /** The m name. */
    private String mName;

    /** The m type. */
    private String mType;

    /** The m relationship. */
    private String mRelationship;

    /** The m module. */
    private String mModule;

    /** The m bean name. */
    private String mBeanName;

    /**
     * Instantiates a new link field.
     * 
     * @param name
     *            the name
     * @param type
     *            the type
     * @param relationship
     *            the relationship
     * @param module
     *            the module
     * @param beanName
     *            the bean name
     */
    public LinkField(String name, String type, String relationship,
            String module, String beanName) {
        super();
        mName = name;
        mType = type;
        mRelationship = relationship;
        mModule = module;
        mBeanName = beanName;
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
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    public void setName(String name) {
        mName = name;
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
     * Sets the type.
     * 
     * @param type
     *            the new type
     */
    public void setType(String type) {
        mType = type;
    }

    /**
     * Gets the relationship.
     * 
     * @return the relationship
     */
    public String getRelationship() {
        return mRelationship;
    }

    /**
     * Sets the relationship.
     * 
     * @param relationship
     *            the new relationship
     */
    public void setRelationship(String relationship) {
        mRelationship = relationship;
    }

    /**
     * Gets the module.
     * 
     * @return the module
     */
    public String getModule() {
        return mModule;
    }

    /**
     * Sets the module.
     * 
     * @param module
     *            the new module
     */
    public void setModule(String module) {
        mModule = module;
    }

    /**
     * Gets the bean name.
     * 
     * @return the bean name
     */
    public String getBeanName() {
        return mBeanName;
    }

    /**
     * Sets the m bean name.
     * 
     * @param mBeanName
     *            the new m bean name
     */
    public void setmBeanName(String mBeanName) {
        this.mBeanName = mBeanName;
    }

}
