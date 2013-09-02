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
 * FileName : SBParser 
 * Description : 
                This  class is SBParser
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

import android.util.Log;

import com.imaginea.android.sugarcrm.util.SBParseHelper;
import com.imaginea.android.sugarcrm.util.SugarCrmException;

/**
 * The Class SBParser.
 */
public class SBParser {

    /** The Constant LOG_TAG. */
    private static final String LOG_TAG = SBParser.class.getSimpleName();

    /** The m entry list json. */
    private JSONArray mEntryListJson;

    /** The m relationship list json. */
    private JSONArray mRelationshipListJson;

    /**
     * Constructor for SBParser.
     * 
     * @param jsonText
     *            a {@link java.lang.String} object.
     * @throws JSONException
     *             the jSON exception
     */
    public SBParser(String jsonText) throws JSONException {
        final JSONObject responseObj = new JSONObject(jsonText);
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            Log.v(LOG_TAG, jsonText);
            Log.v(LOG_TAG, "length" + responseObj.length());
        }
        if (responseObj.has(ENTRY_LIST)) {
            mEntryListJson = responseObj.getJSONArray(ENTRY_LIST);
        }
        if (responseObj.has(RELATIONSHIP_LIST)) {
            mRelationshipListJson = responseObj.getJSONArray(RELATIONSHIP_LIST);
        }
    }

    /**
     * getSugarBeans
     * 
     * @return an array of {@link com.imaginea.android.sugarcrm.rest.SugarBean}
     *         objects.
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    public SugarBean[] getSugarBeans() throws SugarCrmException {
        if (mEntryListJson == null)
            return null;
        final SugarBean[] sugarBeans = new SugarBean[mEntryListJson.length()];
        for (int i = 0; i < mEntryListJson.length(); i++) {
            sugarBeans[i] = new SugarBean();
            try {
                final JSONObject jsonObject = (JSONObject) mEntryListJson
                        .get(i);
                sugarBeans[i].setBeanId(jsonObject.get("id").toString());
                sugarBeans[i].setModuleName(jsonObject.getString("module_name")
                        .toString());
                final String nameValueList = jsonObject.get("name_value_list")
                        .toString();
                sugarBeans[i].setEntryList(SBParseHelper
                        .getNameValuePairs(nameValueList));

                final Map<String, SugarBean[]> relationshipList = getRelationshipBeans(i);
                sugarBeans[i].setRelationshipList(relationshipList);

            } catch (final JSONException e) {
                throw new SugarCrmException(JSON_EXCEPTION, e.getMessage());
            }
        }
        return sugarBeans;
    }

    /**
     * 
     * getRelationshipBeans
     * 
     * @param index
     *            a int.
     * @return a {@link java.util.Map} object.
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    public Map<String, SugarBean[]> getRelationshipBeans(int index)
            throws SugarCrmException {
        if (mRelationshipListJson == null)
            return null;
        final Map<String, SugarBean[]> relationshipList = new HashMap<String, SugarBean[]>();
        try {
            if (index >= mRelationshipListJson.length())
                return relationshipList;
            final JSONArray relationshipJson = mRelationshipListJson
                    .getJSONArray(index);
            if (relationshipJson.length() != 0) {
                for (int i = 0; i < relationshipJson.length(); i++) {
                    final JSONObject relationshipModule = relationshipJson
                            .getJSONObject(i);
                    final String linkFieldName = relationshipModule
                            .getString("name");
                    final String recordsJson = relationshipModule.get(RECORDS)
                            .toString();
                    final SugarBean[] sugarBeans = getSugarBeans(recordsJson);
                    relationshipList.put(linkFieldName, sugarBeans);
                }
            }
        } catch (final JSONException jsone) {
            Log.e(LOG_TAG, jsone.getMessage(), jsone);
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

}
