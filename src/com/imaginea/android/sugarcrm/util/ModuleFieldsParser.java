/*******************************************************************************
 * Copyright (c) 2013 Asha, Murli.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : ModuleFieldsParser
 * Description : 
 *              ModuleFieldsParser Class
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.imaginea.android.sugarcrm.rest.RestConstants;

/**
 * The Class ModuleFieldsParser.
 */
public class ModuleFieldsParser {

    /** The log tag. */
    private final String LOG_TAG = ModuleFieldsParser.class.getSimpleName();

    /** The module fields. */
    private List<ModuleField> moduleFields;

    /** The link fields. */
    private List<LinkField> linkFields;

    /**
     * Instantiates a new module fields parser.
     * 
     * @param jsonResponse
     *            the json response
     * @throws JSONException
     *             the jSON exception
     */
    public ModuleFieldsParser(final String jsonResponse) throws JSONException {
        final JSONObject responseObj = new JSONObject(jsonResponse);

        final JSONObject moduleFieldsJSON = (JSONObject) responseObj
                .get("module_fields");
        setModuleFields(moduleFieldsJSON);

        try {
            final JSONObject linkFieldsJSON = (JSONObject) responseObj
                    .get("link_fields");
            setLinkFields(linkFieldsJSON);
        } catch (final ClassCastException cce) {
            linkFields = new ArrayList<LinkField>();
        }

    }

    /**
     * Sets the module fields.
     * 
     * @param moduleFieldsJSON
     *            the new module fields
     * @throws JSONException
     *             the jSON exception
     */
    private void setModuleFields(final JSONObject moduleFieldsJSON)
            throws JSONException {
        moduleFields = new ArrayList<ModuleField>();
        final Iterator<?> iterator = moduleFieldsJSON.keys();
        while (iterator.hasNext()) {
            final String key = (String) iterator.next();
            if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                Log.v(LOG_TAG, key);
            }
            final JSONObject nameValuePairsJson = (JSONObject) moduleFieldsJSON
                    .get(key);
            moduleFields.add(getModuleField(nameValuePairsJson));
        }
    }

    /**
     * Sets the link fields.
     * 
     * @param linkFieldsJSON
     *            the new link fields
     * @throws JSONException
     *             the jSON exception
     */
    private void setLinkFields(final JSONObject linkFieldsJSON)
            throws JSONException {
        linkFields = new ArrayList<LinkField>();
        final Iterator<?> iterator = linkFieldsJSON.keys();
        while (iterator.hasNext()) {
            final String key = (String) iterator.next();
            if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                Log.v(LOG_TAG, key);
            }
            final JSONObject nameValuePairsJson = (JSONObject) linkFieldsJSON
                    .get(key);
            linkFields.add(getLinkFieldAttributes(nameValuePairsJson));
        }
    }

    /**
     * Gets the module field.
     * 
     * @param nameValuePairsJson
     *            the name value pairs json
     * @return the module field
     * @throws JSONException
     *             the jSON exception
     */
    private ModuleField getModuleField(final JSONObject nameValuePairsJson)
            throws JSONException {
        final String name = nameValuePairsJson.getString(RestConstants.NAME);
        final String type = nameValuePairsJson.getString(RestConstants.TYPE);
        final String label = nameValuePairsJson.getString(RestConstants.LABEL);
        final int required = nameValuePairsJson.getInt(RestConstants.REQUIRED);
        final boolean isRequired = required == 0 ? false : true;
        return new ModuleField(name, type, label, isRequired);
    }

    /**
     * Gets the link field attributes.
     * 
     * @param nameValuePairsJson
     *            the name value pairs json
     * @return the link field attributes
     * @throws JSONException
     *             the jSON exception
     */
    private LinkField getLinkFieldAttributes(final JSONObject nameValuePairsJson)
            throws JSONException {
        final String name = nameValuePairsJson.getString(RestConstants.NAME);
        final String type = nameValuePairsJson.getString(RestConstants.TYPE);
        final String relationship = nameValuePairsJson
                .getString(RestConstants.RELATIONSHIP);
        final String module = nameValuePairsJson
                .getString(RestConstants.MODULE);
        final String beanName = nameValuePairsJson
                .getString(RestConstants.BEAN_NAME);

        return new LinkField(name, type, relationship, module, beanName);
    }

    /**
     * Gets the module fields.
     * 
     * @return the module fields
     */
    public List<ModuleField> getModuleFields() {
        return moduleFields;
    }

    /**
     * Gets the link fields.
     * 
     * @return the link fields
     */
    public List<LinkField> getLinkFields() {
        return linkFields;
    }

}
