/*******************************************************************************
 * Copyright (c) 2013 Asha, Muralidaran.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Asha, Muralidaran - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : SugarBean 
 * Description : 
                This  class is SugarBean
 ******************************************************************************/

package com.imaginea.android.sugarcrm.rest;

import static com.imaginea.android.sugarcrm.rest.RestConstants.ENTRY_LIST;
import static com.imaginea.android.sugarcrm.rest.RestConstants.JSON_EXCEPTION;
import static com.imaginea.android.sugarcrm.rest.RestConstants.RECORDS;
import static com.imaginea.android.sugarcrm.rest.RestConstants.RELATIONSHIP_LIST;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.imaginea.android.sugarcrm.util.SBParseHelper;
import com.imaginea.android.sugarcrm.util.SugarCrmException;

/**
 * The Class SugarBean.
 */
public class SugarBean {

    /** The bean id. */
    private String beanId;

    /** The module name. */
    private String moduleName;

    /** The entry list. */
    private Map<String, String> entryList;

    /** The relationship list. */
    private Map<String, SugarBean[]> relationshipList;

    /**
     * Instantiates a new sugar bean.
     */
    public SugarBean() {
    }

    /**
     * Instantiates a new sugar bean.
     * 
     * @param jsonResponse
     *            the json response
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    public SugarBean(String jsonResponse) throws SugarCrmException {
        try {
            final JSONObject responseObj = new JSONObject(jsonResponse);
            final JSONArray entryListJson = responseObj
                    .getJSONArray(ENTRY_LIST);
            final JSONArray relationshipListJson = responseObj
                    .getJSONArray(RELATIONSHIP_LIST);

            final JSONObject jsonObject = (JSONObject) entryListJson.get(0);
            setBeanId(jsonObject.get("id").toString());
            final String nameValueList = jsonObject.get("name_value_list")
                    .toString();
            setEntryList(SBParseHelper.getNameValuePairs(nameValueList));

            final Map<String, SugarBean[]> relationshipList = getRelationshipBeans(relationshipListJson);
            setRelationshipList(relationshipList);
        } catch (final JSONException e) {
            throw new SugarCrmException(JSON_EXCEPTION, e.getMessage());
        }
    }

    /**
     * Gets the relationship beans.
     * 
     * @param mRelationshipListJson
     *            the m relationship list json
     * @return the relationship beans
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    private Map<String, SugarBean[]> getRelationshipBeans(
            JSONArray mRelationshipListJson) throws SugarCrmException {
        final Map<String, SugarBean[]> relationshipList = new HashMap<String, SugarBean[]>();
        try {
            if (mRelationshipListJson.length() != 0) {
                final JSONArray relationshipJson = mRelationshipListJson
                        .getJSONArray(0);
                if (relationshipJson.length() != 0) {
                    for (int i = 0; i < relationshipJson.length(); i++) {
                        final JSONObject relationshipModule = relationshipJson
                                .getJSONObject(i);
                        final String linkFieldName = relationshipModule
                                .getString("name");
                        final String recordsJson = relationshipModule.get(
                                RECORDS).toString();
                        final SugarBean[] sugarBeans = getSugarBeans(recordsJson);
                        relationshipList.put(linkFieldName, sugarBeans);
                    }
                }
            }
        } catch (final JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        }
        return relationshipList;
    }

    /**
     * Gets the sugar beans.
     * 
     * @param recordsJson
     *            the records json
     * @return the sugar beans
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    private SugarBean[] getSugarBeans(String recordsJson)
            throws SugarCrmException {
        try {
            final JSONArray recordsArray = new JSONArray(recordsJson);
            final SugarBean[] sugarBeans = new SugarBean[recordsArray.length()];
            for (int i = 0; i < recordsArray.length(); i++) {
                sugarBeans[i] = new SugarBean();
                sugarBeans[i].setEntryList(SBParseHelper
                        .getNameValuePairs(recordsArray.get(i).toString()));
            }
            return sugarBeans;
        } catch (final JSONException e) {
            throw new SugarCrmException(e.getMessage());
        }
    }

    /**
     * Getter for the field <code>beanId</code>.
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getBeanId() {
        return beanId;
    }

    /**
     * 
     * Setter for the field <code>beanId</code>.
     * 
     * @param beanId
     *            a {@link java.lang.String} object.
     */
    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    /**
     * Getter for the field <code>moduleName</code>.
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * 
     * Setter for the field <code>moduleName</code>.
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Getter for the field <code>entryList</code>.
     * 
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getEntryList() {
        return entryList;
    }

    /**
     * Setter for the field <code>entryList</code>.
     * 
     * @param map
     *            a {@link java.util.Map} object.
     */
    public void setEntryList(Map<String, String> map) {
        entryList = map;
    }

    /**
     * getFieldValue
     * 
     * @param fieldName
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getFieldValue(String fieldName) {
        return entryList.get(fieldName);
    }

    /**
     * Getter for the field <code>relationshipList</code>.
     * 
     * @return a {@link java.util.Map} object.
     */
    public Map<String, SugarBean[]> getRelationshipList() {
        return relationshipList;
    }

    /**
     * Setter for the field <code>relationshipList</code>.
     * 
     * @param relationshipList
     *            a {@link java.util.Map} object.
     */
    public void setRelationshipList(Map<String, SugarBean[]> relationshipList) {
        this.relationshipList = relationshipList;
    }

    /**
     * getRelationshipBeans
     * 
     * @param linkField
     *            a {@link java.lang.String} object.
     * @return an array of {@link com.imaginea.android.sugarcrm.rest.SugarBean}
     *         objects.
     */
    public SugarBean[] getRelationshipBeans(String linkField) {
        return relationshipList.get(linkField);
    }

}
