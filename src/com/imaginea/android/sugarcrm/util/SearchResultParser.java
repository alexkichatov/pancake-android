/*******************************************************************************
 * Copyright (c) 2013 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : SearchResultParser 
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import static com.imaginea.android.sugarcrm.rest.RestConstants.ENTRY_LIST;
import static com.imaginea.android.sugarcrm.rest.RestConstants.NAME;
import static com.imaginea.android.sugarcrm.rest.RestConstants.RECORDS;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.imaginea.android.sugarcrm.rest.SugarBean;

/**
 * The Class SearchResultParser.
 */
public class SearchResultParser {

    /** The m search results. */
    private final Map<String, SugarBean[]> mSearchResults;

    /**
     * Constructor for SearchResultParser.
     * 
     * @param jsonText
     *            a {@link java.lang.String} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    public SearchResultParser(String jsonText, String moduleName)
            throws SugarCrmException {
        mSearchResults = new HashMap<String, SugarBean[]>();
        try {
            final JSONObject responseObj = new JSONObject(jsonText);
            final JSONArray mEntryListJson = responseObj
                    .getJSONArray(ENTRY_LIST);
            for (int i = 0; i < mEntryListJson.length(); i++) {
                final JSONObject moduleResultJson = new JSONObject(
                        mEntryListJson.get(i).toString());
                if (moduleName.equals(moduleResultJson.get(NAME).toString())) {
                    final String recordsJson = moduleResultJson.get(RECORDS)
                            .toString();
                    final SugarBean[] sugarBeans = getSugarBeans(recordsJson);
                    mSearchResults.put(moduleName, sugarBeans);
                }
            }
        } catch (final JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        }
    }

    /**
     * * Constructor for SearchResultParser.
     * 
     * @param jsonText
     *            a {@link java.lang.String} object.
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    public SearchResultParser(String jsonText) throws SugarCrmException {
        mSearchResults = new HashMap<String, SugarBean[]>();
        try {
            final JSONObject responseObj = new JSONObject(jsonText);
            final JSONArray mEntryListJson = responseObj
                    .getJSONArray(ENTRY_LIST);
            for (int i = 0; i < mEntryListJson.length(); i++) {
                final JSONObject moduleResultJson = new JSONObject(
                        mEntryListJson.get(i).toString());
                final String moduleName = moduleResultJson.get(NAME).toString();
                final String recordsJson = moduleResultJson.get(RECORDS)
                        .toString();
                final SugarBean[] sugarBeans = getSugarBeans(recordsJson);
                mSearchResults.put(moduleName, sugarBeans);
            }
        } catch (final JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        }
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
     * getSearchResults
     * 
     * @return a {@link java.util.Map} object.
     */
    public Map<String, SugarBean[]> getSearchResults() {
        return mSearchResults;
    }

}
