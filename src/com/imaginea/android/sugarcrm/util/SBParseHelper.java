/*******************************************************************************
 * Copyright (c) 2013 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : SBParseHelper
 * Description : 
 *              Helper for the SBParser and SugarBean to parse the JSON response to
 *retrieve the name value pairs either in the entry_list or the
 *relationship_list
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import static com.imaginea.android.sugarcrm.rest.RestConstants.VALUE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Class SBParseHelper.
 */
public class SBParseHelper {

    /**
     * Gets the name value pairs.
     * 
     * @param nameValueList
     *            the name value list
     * @return the name value pairs
     * @throws SugarCrmException
     *             the sugar crm exception
     */
    public static Map<String, String> getNameValuePairs(
            final String nameValueList) throws SugarCrmException {
        final Map<String, String> fields = new HashMap<String, String>();
        try {
            final JSONObject nameVal = new JSONObject(nameValueList);
            final Iterator<?> iter = nameVal.keys();
            while (iter.hasNext()) {
                final String key = (String) iter.next();
                final String val = ((JSONObject) (nameVal.get(key))).get(VALUE)
                        .toString();
                fields.put(key, val);
            }
            return fields;

        } catch (final JSONException e) {
            /*
             * when the select_fields is empty while making the rest call, the
             * default response will give empty JSONArray and hence it throws an
             * exception
             */
            throw new SugarCrmException("No name value pairs available!");
        }

    }

}
