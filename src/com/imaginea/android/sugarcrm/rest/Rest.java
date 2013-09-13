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
 * FileName : Rest 
 * Description : 
                This  class is for handling Sugar CRM Rest API calls.
 ******************************************************************************/

package com.imaginea.android.sugarcrm.rest;

import static com.imaginea.android.sugarcrm.rest.RestConstants.APPLICATION;
import static com.imaginea.android.sugarcrm.rest.RestConstants.BEAN_ID;
import static com.imaginea.android.sugarcrm.rest.RestConstants.BEAN_IDS;
import static com.imaginea.android.sugarcrm.rest.RestConstants.CREATED;
import static com.imaginea.android.sugarcrm.rest.RestConstants.DELETED;
import static com.imaginea.android.sugarcrm.rest.RestConstants.DESCRIPTION;
import static com.imaginea.android.sugarcrm.rest.RestConstants.FAILED;
import static com.imaginea.android.sugarcrm.rest.RestConstants.FIELDS;
import static com.imaginea.android.sugarcrm.rest.RestConstants.GET_AVAILABLE_MODULES;
import static com.imaginea.android.sugarcrm.rest.RestConstants.GET_ENTRIES;
import static com.imaginea.android.sugarcrm.rest.RestConstants.GET_ENTRIES_COUNT;
import static com.imaginea.android.sugarcrm.rest.RestConstants.GET_ENTRY;
import static com.imaginea.android.sugarcrm.rest.RestConstants.GET_ENTRY_LIST;
import static com.imaginea.android.sugarcrm.rest.RestConstants.GET_MODULE_FIELDS;
import static com.imaginea.android.sugarcrm.rest.RestConstants.GET_RELATIONSHIPS;
import static com.imaginea.android.sugarcrm.rest.RestConstants.GET_SERVER_INFO;
import static com.imaginea.android.sugarcrm.rest.RestConstants.ID;
import static com.imaginea.android.sugarcrm.rest.RestConstants.IDS;
import static com.imaginea.android.sugarcrm.rest.RestConstants.INPUT_TYPE;
import static com.imaginea.android.sugarcrm.rest.RestConstants.JSON;
import static com.imaginea.android.sugarcrm.rest.RestConstants.JSON_EXCEPTION;
import static com.imaginea.android.sugarcrm.rest.RestConstants.LINK_FIELD_NAME;
import static com.imaginea.android.sugarcrm.rest.RestConstants.LINK_FIELD_NAMES;
import static com.imaginea.android.sugarcrm.rest.RestConstants.LINK_NAME_TO_FIELDS_ARRAY;
import static com.imaginea.android.sugarcrm.rest.RestConstants.LOGIN;
import static com.imaginea.android.sugarcrm.rest.RestConstants.LOGOUT;
import static com.imaginea.android.sugarcrm.rest.RestConstants.MAX_RESULTS;
import static com.imaginea.android.sugarcrm.rest.RestConstants.METHOD;
import static com.imaginea.android.sugarcrm.rest.RestConstants.MODULES;
import static com.imaginea.android.sugarcrm.rest.RestConstants.MODULE_NAME;
import static com.imaginea.android.sugarcrm.rest.RestConstants.MODULE_NAMES;
import static com.imaginea.android.sugarcrm.rest.RestConstants.NAME;
import static com.imaginea.android.sugarcrm.rest.RestConstants.NAME_VALUE_LIST;
import static com.imaginea.android.sugarcrm.rest.RestConstants.NAME_VALUE_LISTS;
import static com.imaginea.android.sugarcrm.rest.RestConstants.OFFSET;
import static com.imaginea.android.sugarcrm.rest.RestConstants.ORDER_BY;
import static com.imaginea.android.sugarcrm.rest.RestConstants.PASSWORD;
import static com.imaginea.android.sugarcrm.rest.RestConstants.QUERY;
import static com.imaginea.android.sugarcrm.rest.RestConstants.RELATED_FIELDS;
import static com.imaginea.android.sugarcrm.rest.RestConstants.RELATED_IDS;
import static com.imaginea.android.sugarcrm.rest.RestConstants.RELATED_MODULE_LINK_NAME_TO_FIELDS_ARRAY;
import static com.imaginea.android.sugarcrm.rest.RestConstants.RELATED_MODULE_QUERY;
import static com.imaginea.android.sugarcrm.rest.RestConstants.RESPONSE_TYPE;
import static com.imaginea.android.sugarcrm.rest.RestConstants.REST_DATA;
import static com.imaginea.android.sugarcrm.rest.RestConstants.RESULT_COUNT;
import static com.imaginea.android.sugarcrm.rest.RestConstants.SEAMLESS_LOGIN;
import static com.imaginea.android.sugarcrm.rest.RestConstants.SEARCH_BY_MODULE;
import static com.imaginea.android.sugarcrm.rest.RestConstants.SEARCH_STRING;
import static com.imaginea.android.sugarcrm.rest.RestConstants.SELECT_FIELDS;
import static com.imaginea.android.sugarcrm.rest.RestConstants.SESSION;
import static com.imaginea.android.sugarcrm.rest.RestConstants.SET_ENTRIES;
import static com.imaginea.android.sugarcrm.rest.RestConstants.SET_ENTRY;
import static com.imaginea.android.sugarcrm.rest.RestConstants.SET_RELATIONSHIP;
import static com.imaginea.android.sugarcrm.rest.RestConstants.SET_RELATIONSHIPS;
import static com.imaginea.android.sugarcrm.rest.RestConstants.USER_AUTH;
import static com.imaginea.android.sugarcrm.rest.RestConstants.USER_NAME;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.util.Module;
import com.imaginea.android.sugarcrm.util.ModuleFieldsParser;
import com.imaginea.android.sugarcrm.util.RelationshipStatus;
import com.imaginea.android.sugarcrm.util.SearchResultParser;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * 
 * Rest class.
 * 
 * This class is for handling Sugar CRM Rest API calls.
 * 
 * Error Handling Note: The API has different set of ids for error and error
 * messages and has not been consistent when last attempted. Session id Issue:
 * The session id expirations need to be handled and propagated back, but is
 * currently handled in SugarCrmApp.getSessionId based on a fixed time for
 * renewing the session. If this fixed time is not enough then the
 * "Invalid session Ids" care not propagated back, so we keep the time low for
 * the time being.
 */
public final class Rest {

    public static final String LOG_TAG = "RestUtil";

    private static final String ERROR_MSG = "FAILED TO CONNECT!";

    private static final String KEY = "name";
    private static final String VALUE = "value";

    /** The m http client. */
    private static HttpClient mHttpClient = new DefaultHttpClient();

    /**
     * Retrieve a list of beans. This is the primary method for getting list of
     * SugarBeans
     * 
     * @param url
     *            the url
     * @param sessionId
     *            the session id
     * @param moduleName
     *            the module name
     * @param query
     *            the query
     * @param orderBy
     *            the order by
     * @param offset
     *            the offset
     * @param selectFields
     *            the select fields
     * @param linkNameToFieldsArray
     *            the link name to fields array
     * @param maxResults
     *            the max results
     * @param deleted
     *            the deleted
     * @return the entry list
     * @throws SugarCrmException
     *             the sugar crm exception
     */

    private Rest() {

    }

    public static SugarBean[] getEntryList(String url, String sessionId,
            String moduleName, String query, String orderBy, String offset,
            String[] selectFields,
            Map<String, List<String>> linkNameToFieldsArray, String maxResults,
            String deleted) throws SugarCrmException {

        String response = null;
        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);
        data.put(QUERY, query != null ? query.toLowerCase() : "");
        data.put(ORDER_BY, orderBy != null ? orderBy : "");
        data.put(OFFSET, offset != null ? offset : "");
        data.put(
                SELECT_FIELDS,
                (selectFields != null && selectFields.length != 0) ? new JSONArray(
                        Arrays.asList(selectFields)) : "");

        try {
            final JSONArray nameValueArray = new JSONArray();
            if (linkNameToFieldsArray != null
                    && linkNameToFieldsArray.size() != 0) {
                for (final Entry<String, List<String>> entry : linkNameToFieldsArray
                        .entrySet()) {
                    final JSONObject nameValue = new JSONObject();
                    nameValue.put(KEY, entry.getKey());
                    nameValue.put(VALUE, new JSONArray(entry.getValue()));
                    nameValueArray.put(nameValue);
                }
            }
            data.put(LINK_NAME_TO_FIELDS_ARRAY, nameValueArray);
            data.put(MAX_RESULTS, maxResults != null ? maxResults : "");
            data.put(DELETED, deleted != null ? deleted : 0);

            final String restData = org.json.simple.JSONValue
                    .toJSONString(data);

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, GET_ENTRY_LIST));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);
            response = EntityUtils.toString(res.getEntity());
            if (response == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }
            return new SBParser(response).getSugarBeans();
        } catch (final JSONException jo) {
            Log.e(LOG_TAG, "response is : " + response);
            throw new SugarCrmException(JSON_EXCEPTION, jo.getMessage());
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }
    }

    /**
     * 
     * getEntries
     * 
     * @param url
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param ids
     *            an array of {@link java.lang.String} objects.
     * @param selectFields
     *            an array of {@link java.lang.String} objects.
     * @param linkNameToFieldsArray
     *            a {@link java.util.Map} object.
     * @return an array of {@link com.imaginea.android.sugarcrm.rest.SugarBean}
     *         objects.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static SugarBean[] getEntries(String url, String sessionId,
            String moduleName, String[] ids, String[] selectFields,
            Map<String, List<String>> linkNameToFieldsArray)
            throws SugarCrmException {

        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);
        data.put(IDS, (ids != null && ids.length != 0) ? ids : "");
        data.put(
                SELECT_FIELDS,
                (selectFields != null && selectFields.length != 0) ? new JSONArray(
                        Arrays.asList(selectFields)) : "");

        try {
            final JSONArray nameValueArray = new JSONArray();
            if (linkNameToFieldsArray != null
                    && linkNameToFieldsArray.size() != 0) {
                for (final Entry<String, List<String>> entry : linkNameToFieldsArray
                        .entrySet()) {
                    final JSONObject nameValue = new JSONObject();
                    nameValue.put(KEY, entry.getKey());
                    nameValue.put(VALUE, new JSONArray(entry.getValue()));
                    nameValueArray.put(nameValue);
                }
            }
            data.put(LINK_NAME_TO_FIELDS_ARRAY, nameValueArray);

            final String restData = org.json.simple.JSONValue
                    .toJSONString(data);

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, GET_ENTRIES));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }
            final SugarBean[] beans = new SBParser(EntityUtils.toString(
                    res.getEntity()).toString()).getSugarBeans();
            return beans;
        } catch (final JSONException jo) {
            throw new SugarCrmException(JSON_EXCEPTION, jo.getMessage());
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }
    }

    /**
     * getEntry
     * 
     * @param url
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param id
     *            a {@link java.lang.String} object.
     * @param selectFields
     *            an array of {@link java.lang.String} objects.
     * @param linkNameToFieldsArray
     *            a {@link java.util.Map} object.
     * @return a {@link com.imaginea.android.sugarcrm.rest.SugarBean} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static SugarBean getEntry(String url, String sessionId,
            String moduleName, String id, String[] selectFields,
            Map<String, List<String>> linkNameToFieldsArray)
            throws SugarCrmException {

        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);
        data.put(ID, id != null ? id : "");
        data.put(
                SELECT_FIELDS,
                (selectFields != null && selectFields.length != 0) ? new JSONArray(
                        Arrays.asList(selectFields)) : "");

        try {
            final JSONArray nameValueArray = new JSONArray();
            if (linkNameToFieldsArray != null
                    && linkNameToFieldsArray.size() != 0) {
                for (final Entry<String, List<String>> entry : linkNameToFieldsArray
                        .entrySet()) {
                    final JSONObject nameValue = new JSONObject();
                    nameValue.put(KEY, entry.getKey());
                    nameValue.put(VALUE, new JSONArray(entry.getValue()));
                    nameValueArray.put(nameValue);
                }
            }
            data.put(LINK_NAME_TO_FIELDS_ARRAY, nameValueArray);
            final String restData = org.json.simple.JSONValue
                    .toJSONString(data);

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, GET_ENTRY));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }
            return new SugarBean(EntityUtils.toString(res.getEntity())
                    .toString());
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        } catch (final JSONException e) {
            throw new SugarCrmException(e.getMessage());
        }
    }

    /**
     * Retrieve number of records in a given module
     * 
     * @return Array result_count - integer - Total number of records for a
     *         given module and query
     * @param url
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param query
     *            a {@link java.lang.String} object.
     * @param deleted
     *            a {@link java.lang.String} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static int getEntriesCount(String url, String sessionId,
            String moduleName, String query, String deleted)
            throws SugarCrmException {
        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);
        data.put(QUERY, query != null ? query : "");
        data.put(DELETED, deleted != null ? deleted : 0);

        try {
            final String restData = org.json.simple.JSONValue
                    .toJSONString(data);

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs
                    .add(new BasicNameValuePair(METHOD, GET_ENTRIES_COUNT));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }
            final String response = EntityUtils.toString(res.getEntity())
                    .toString();
            final JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getInt(RESULT_COUNT);
        } catch (final JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        }
    }

    /**
     * Update or create a single SugarBean.
     * 
     * @return Array 'id' -- the ID of the bean that was written to (-1 on
     *         error)
     * @param url
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param nameValueList
     *            a {@link java.util.Map} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static String setEntry(String url, String sessionId,
            String moduleName, Map<String, String> nameValueList)
            throws SugarCrmException {
        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);

        try {
            final JSONArray nameValueArray = new JSONArray();
            if (nameValueList != null && nameValueList.size() != 0) {
                for (final Entry<String, String> entry : nameValueList
                        .entrySet()) {
                    final JSONObject nameValue = new JSONObject();
                    nameValue.put(KEY, entry.getKey());
                    nameValue.put(VALUE, entry.getValue());
                    nameValueArray.put(nameValue);
                }
            }
            data.put(NAME_VALUE_LIST, nameValueArray);

            final String restData = org.json.simple.JSONValue
                    .toJSONString(data);

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, SET_ENTRY));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }
            final String response = EntityUtils.toString(res.getEntity())
                    .toString();
            final JSONObject jsonResponse = new JSONObject(response);
            Log.i(LOG_TAG, "setEntry response : " + response);
            // TODO: have to see how the JSON response will be when it doesn't
            // return beanId
            return jsonResponse.get(ModuleFields.ID).toString();
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        } catch (final JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        }
    }

    /**
     * Update or create a list of SugarBeans
     * 
     * @return Array 'ids' -- Array of the IDs of the beans that was written to
     *         (-1 on error)
     * @param url
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param nameValueLists
     *            a {@link java.util.List} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static List<String> setEntries(String url, String sessionId,
            String moduleName, List<Map<String, String>> nameValueLists)
            throws SugarCrmException {
        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);

        try {
            final JSONArray nameValueArray = new JSONArray();
            if (nameValueLists != null && nameValueLists.size() != 0) {
                for (final Map<String, String> nameValueList : nameValueLists) {
                    final JSONArray beanNameValueArray = new JSONArray();
                    for (final Entry<String, String> entry : nameValueList
                            .entrySet()) {
                        final JSONObject nameValue = new JSONObject();
                        nameValue.put(KEY, entry.getKey());
                        nameValue.put(VALUE, entry.getValue());
                        beanNameValueArray.put(nameValue);
                    }
                    nameValueArray.put(beanNameValueArray);
                }
            }
            data.put(NAME_VALUE_LISTS, nameValueArray);

            final String restData = org.json.simple.JSONValue
                    .toJSONString(data);

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, SET_ENTRIES));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }
            final String response = EntityUtils.toString(res.getEntity())
                    .toString();
            Log.i(LOG_TAG, "setEntries response : " + response);
            final JSONObject jsonResponse = new JSONObject(response);
            final JSONArray beanIdsArray = new JSONArray(jsonResponse.get(IDS)
                    .toString());

            final List<String> beanIds = new ArrayList<String>();
            for (int i = 0; i < beanIdsArray.length(); i++) {
                final String module = beanIdsArray.getString(i).toString();
                beanIds.add(module);
            }

            return beanIds;
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        } catch (final JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        }
    }

    /**
     * Retrieve a collection of beans that are related to the specified bean and
     * optionally return relationship data for those related beans. So in this
     * API you can get contacts info for an account and also return all those
     * contact's email address or an opportunity info also.
     * 
     * @return Array 'entry_list' -- Array - The records that were retrieved
     *         'relationship_list' -- Array - The records link field data. The
     *         example is if asked about accounts contacts email address then
     *         return data would look like Array ( [0] => Array ( [name] =>
     *         email_addresses [records] => Array ( [0] => Array ( [0] => Array
     *         ( [name] => id [value] => 3fb16797-8d90-0a94-ac12-490b63a6be67 )
     *         [1] => Array ( [name] => email_address [value] =>
     *         hr.kid.qa@example.com ) [2] => Array ( [name] => opt_out [value]
     *         => 0 ) [3] => Array ( [name] => primary_address [value] => 1 ) )
     *         [1] => Array ( [0] => Array ( [name] => id [value] =>
     *         403f8da1-214b-6a88-9cef-490b63d43566 ) [1] => Array ( [name] =>
     *         email_address [value] => kid.hr@example.name ) [2] => Array (
     *         [name] => opt_out [value] => 0 ) [3] => Array ( [name] =>
     *         primary_address [value] => 0 ) ) ) ) )
     * @param url
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param beanId
     *            a {@link java.lang.String} object.
     * @param linkFieldName
     *            a {@link java.lang.String} object.
     * @param relatedModuleQuery
     *            a {@link java.lang.String} object.
     * @param relatedFields
     *            an array of {@link java.lang.String} objects.
     * @param relatedModuleLinkNameToFieldsArray
     *            a {@link java.util.Map} object.
     * @param deleted
     *            a {@link java.lang.String} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static SugarBean[] getRelationships(String url, String sessionId,
            String moduleName, String beanId, String linkFieldName,
            String relatedModuleQuery, String[] relatedFields,
            Map<String, List<String>> relatedModuleLinkNameToFieldsArray,
            String deleted) throws SugarCrmException {
        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);
        data.put(BEAN_ID, beanId != null ? beanId : "");
        data.put(LINK_FIELD_NAME, linkFieldName != null ? linkFieldName : "");
        data.put(RELATED_MODULE_QUERY,
                relatedModuleQuery != null ? relatedModuleQuery : "");
        data.put(
                RELATED_FIELDS,
                (relatedFields != null && relatedFields.length != 0) ? new JSONArray(
                        Arrays.asList(relatedFields)) : "");

        try {
            final JSONArray linkNametoFieldJson = new JSONArray();
            if (relatedModuleLinkNameToFieldsArray != null
                    && relatedModuleLinkNameToFieldsArray.size() != 0) {
                for (final Entry<String, List<String>> entry : relatedModuleLinkNameToFieldsArray
                        .entrySet()) {
                    final JSONObject nameValue = new JSONObject();
                    nameValue.put(KEY, entry.getKey());
                    nameValue.put(VALUE, new JSONArray(entry.getValue()));
                    linkNametoFieldJson.put(nameValue);
                }
            }

            data.put(RELATED_MODULE_LINK_NAME_TO_FIELDS_ARRAY,
                    linkNametoFieldJson);
            data.put(DELETED, deleted != null ? deleted : 0);

            final String restData = org.json.simple.JSONValue
                    .toJSONString(data);

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs
                    .add(new BasicNameValuePair(METHOD, GET_RELATIONSHIPS));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.i(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }
            final String response = EntityUtils.toString(res.getEntity())
                    .toString();
            Log.e(LOG_TAG, "getRelationships response : " + response);
            return new SBParser(response).getSugarBeans();
        } catch (final JSONException jo) {
            throw new SugarCrmException(JSON_EXCEPTION, jo.getMessage());
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }
    }

    /**
     * Set a single relationship between two beans. The items are related by
     * module name and id.
     * 
     * @return Array - created - integer - How many relationships has been
     *         created - failed - integer - How many relationsip creation failed
     *         - deleted - integer - How many relationships were deleted
     * @param url
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @param moduleNames
     *            an array of {@link java.lang.String} objects.
     * @param beanIds
     *            an array of {@link java.lang.String} objects.
     * @param linkFieldNames
     *            an array of {@link java.lang.String} objects.
     * @param relatedIds
     *            an array of {@link java.lang.String} objects.
     * @param nameValueLists
     *            a {@link java.util.List} object.
     * @param deleted
     *            a {@link java.lang.String} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static RelationshipStatus setRelationships(String url,
            String sessionId, String[] moduleNames, String[] beanIds,
            String[] linkFieldNames, String[] relatedIds,
            List<Map<String, String>> nameValueLists, String deleted)
            throws SugarCrmException {
        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(
                MODULE_NAMES,
                (moduleNames != null && moduleNames.length != 0) ? new JSONArray(
                        Arrays.asList(moduleNames)) : "");
        data.put(
                BEAN_IDS,
                (beanIds != null && beanIds.length != 0) ? new JSONArray(Arrays
                        .asList(beanIds)) : "");
        data.put(
                LINK_FIELD_NAMES,
                (linkFieldNames != null && linkFieldNames.length != 0) ? new JSONArray(
                        Arrays.asList(linkFieldNames)) : "");
        data.put(RELATED_IDS,
                (relatedIds != null && relatedIds.length != 0) ? new JSONArray(
                        Arrays.asList(relatedIds)) : "");

        try {
            final JSONArray nameValueArray = new JSONArray();
            if (nameValueLists != null && nameValueLists.size() != 0) {
                for (final Map<String, String> nameValueList : nameValueLists) {
                    final JSONArray beanNameValueArray = new JSONArray();
                    for (final Entry<String, String> entry : nameValueList
                            .entrySet()) {
                        final JSONObject nameValue = new JSONObject();
                        nameValue.put(KEY, entry.getKey());
                        nameValue.put(VALUE, entry.getValue());
                        beanNameValueArray.put(nameValue);
                    }
                    nameValueArray.put(beanNameValueArray);
                }
            }
            data.put(NAME_VALUE_LISTS, nameValueArray);
            data.put(DELETED, deleted != null ? deleted : 0);

            final String restData = org.json.simple.JSONValue
                    .toJSONString(data);

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs
                    .add(new BasicNameValuePair(METHOD, SET_RELATIONSHIPS));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }
            final String response = EntityUtils.toString(res.getEntity())
                    .toString();
            final JSONObject jsonResponse = new JSONObject(response);
            return new RelationshipStatus(jsonResponse.getInt(CREATED),
                    jsonResponse.getInt(FAILED), jsonResponse.getInt(DELETED));
        } catch (final JSONException jo) {
            throw new SugarCrmException(JSON_EXCEPTION, jo.getMessage());
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }
    }

    /**
     * Set a single relationship between two beans. The items are related by
     * module name and id.
     * 
     * @return Array - created - integer - How many relationships has been
     *         created - failed - integer - How many relationsip creation failed
     *         - deleted - integer - How many relationships were deleted
     * @param url
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param beanId
     *            a {@link java.lang.String} object.
     * @param linkFieldName
     *            a {@link java.lang.String} object.
     * @param relatedIds
     *            an array of {@link java.lang.String} objects.
     * @param nameValueList
     *            a {@link java.util.Map} object.
     * @param delete
     *            a {@link java.lang.String} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static RelationshipStatus setRelationship(String url,
            String sessionId, String moduleName, String beanId,
            String linkFieldName, String[] relatedIds,
            Map<String, String> nameValueList, String delete)
            throws SugarCrmException {
        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);
        data.put(BEAN_ID, beanId != null ? beanId : "");
        data.put(LINK_FIELD_NAME, linkFieldName != null ? linkFieldName : "");
        data.put(RELATED_IDS,
                (relatedIds != null && relatedIds.length != 0) ? new JSONArray(
                        Arrays.asList(relatedIds)) : "");

        try {
            final JSONArray nameValueArray = new JSONArray();
            if (nameValueList != null && nameValueList.size() != 0) {
                for (final Entry<String, String> entry : nameValueList
                        .entrySet()) {
                    final JSONObject nameValue = new JSONObject();
                    nameValue.put(KEY, entry.getKey());
                    nameValue.put(VALUE, entry.getValue());
                    nameValueArray.put(nameValue);
                }
            }
            data.put(NAME_VALUE_LIST, nameValueArray);
            data.put(DELETED, delete != null ? delete : 0);

            final String restData = org.json.simple.JSONValue
                    .toJSONString(data);

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs
                    .add(new BasicNameValuePair(METHOD, SET_RELATIONSHIP));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }
            final String response = EntityUtils.toString(res.getEntity())
                    .toString();
            final JSONObject jsonResponse = new JSONObject(response);
            return new RelationshipStatus(jsonResponse.getInt(CREATED),
                    jsonResponse.getInt(FAILED), jsonResponse.getInt(DELETED));
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        } catch (final JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        }
    }

    /**
     * Given a list of modules to search and a search string, return the id,
     * module_name, along with the fields We will support Accounts, Bug Tracker,
     * Cases, Contacts, Leads, Opportunities, Project, ProjectTask, Quotes
     * 
     * @return Array return_search_result - Array('Accounts' =>
     *         array(array('name' => 'first_name', 'value' => 'John', 'name' =>
     *         'last_name', 'value' => 'Do')))
     * @param url
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @param searchString
     *            a {@link java.lang.String} object.
     * @param modules
     *            an array of {@link java.lang.String} objects.
     * @param offset
     *            a {@link java.lang.String} object.
     * @param maxResults
     *            a {@link java.lang.String} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static Map<String, SugarBean[]> searchByModule(String url,
            String sessionId, String searchString, String[] modules,
            String offset, String maxResults) throws SugarCrmException {
        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(SEARCH_STRING, searchString != null ? searchString : "");
        data.put(
                MODULES,
                (modules != null && modules.length != 0) ? new JSONArray(Arrays
                        .asList(modules)) : "");
        data.put(OFFSET, offset != null ? offset : "");
        data.put(MAX_RESULTS, maxResults != null ? maxResults : "");

        try {
            final String restData = org.json.simple.JSONValue
                    .toJSONString(data);
            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs
                    .add(new BasicNameValuePair(METHOD, SEARCH_BY_MODULE));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }
            final String response = EntityUtils.toString(res.getEntity())
                    .toString();
            return new SearchResultParser(response).getSearchResults();
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        }

    }

    /**
     * Log the user into the application
     * 
     * @return Array - id - String id is the session_id of the session that was
     *         created. - module_name - String - module name of user -
     *         name_value_list - Array - The name value pair of user_id,
     *         user_name, user_language, user_currency_id, user_currency_name
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     * @param url
     *            a {@link java.lang.String} object.
     * @param username
     *            a {@link java.lang.String} object.
     * @param password
     *            a {@link java.lang.String} object.
     */
    public static String loginToSugarCRM(String url, String username,
            String password) throws SugarCrmException {
        final JSONObject credentials = new JSONObject();
        try {
            credentials.put(USER_NAME, username);
            credentials.put(PASSWORD, Util.MD5(password));

            final JSONArray jsonArray = new JSONArray();
            jsonArray.put(credentials);

            final JSONObject userAuth = new JSONObject();
            userAuth.put(USER_AUTH, credentials);

            final HttpPost reqLogin = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, LOGIN));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, userAuth
                    .toString()));
            nameValuePairs.add(new BasicNameValuePair(APPLICATION, ""));
            nameValuePairs.add(new BasicNameValuePair(NAME_VALUE_LIST, ""));

            reqLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            mHttpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            // Send POST request
            final HttpResponse resLogin = mHttpClient.execute(reqLogin);

            if (resLogin.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }

            final String response = EntityUtils.toString(resLogin.getEntity());
            final JSONObject responseObj = new JSONObject(response);
            try {
                return responseObj.get(ID).toString();
            } catch (final JSONException e) {
                throw new SugarCrmException(responseObj.get(NAME).toString(),
                        responseObj.get(DESCRIPTION).toString());
            }
        } catch (final JSONException jo) {
            throw new SugarCrmException(JSON_EXCEPTION, jo.getMessage());
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }

    }

    /**
     * Log out of the session. This will destroy the session and prevent other's
     * from using it.
     * 
     * @param String
     *            $session -- Session ID returned by a previous call to login.
     * @return Void
     * @exception 'SoapFault' -- The SOAP error, if any
     */
    public static void logoutSugarCRM(String url, String sessionId)
            throws SugarCrmException {
        try {
            final Map<String, Object> data = new LinkedHashMap<String, Object>();
            data.put(SESSION, sessionId);
            final String restData = org.json.simple.JSONValue
                    .toJSONString(data);
            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, LOGOUT));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        }
    }

    /**
     * Retrieve the list of available modules on the system available to the
     * currently logged in user.
     * 
     * @return Array 'modules' -- Array - An array of module names
     * @param url
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static List<String> getAvailableModules(String url, String sessionId)
            throws SugarCrmException {
        try {
            final JSONObject data = new JSONObject();
            data.put(SESSION, sessionId);

            final HttpPost req = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD,
                    GET_AVAILABLE_MODULES));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, data
                    .toString()));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            mHttpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = mHttpClient.execute(req);

            if (res.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }

            final String response = EntityUtils.toString(res.getEntity());
            if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
                Log.i(LOG_TAG, "available modules : " + response);
            }
            final JSONObject responseObj = new JSONObject(response);
            final JSONArray modulesArray = responseObj.getJSONArray(MODULES);
            final List<String> modules = new ArrayList<String>();
            for (int i = 0; i < modulesArray.length(); i++) {
                final String module = modulesArray.getString(i).toString();
                modules.add(module);
            }

            return modules;
        } catch (final JSONException jo) {
            throw new SugarCrmException(JSON_EXCEPTION, jo.getMessage());
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }
    }

    /**
     * Retrieve vardef information on the fields of the specified bean.
     * 
     * @return Array 'module_fields' -- Array - The vardef information on the
     *         selected fields. 'link_fields' -- Array - The vardef information
     *         on the link fields
     * @param url
     *            a {@link java.lang.String} object.
     * @param sessionId
     *            a {@link java.lang.String} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param fields
     *            an array of {@link java.lang.String} objects.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static Module getModuleFields(String url, String sessionId,
            String moduleName, String[] fields) throws SugarCrmException {
        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);

        try {
            if (fields != null && fields.length > 0) {
                final JSONArray arr = new JSONArray(Arrays.asList(fields));
                data.put(FIELDS, arr);
            }

            final String restData = org.json.simple.JSONValue
                    .toJSONString(data);

            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);

            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs
                    .add(new BasicNameValuePair(METHOD, GET_MODULE_FIELDS));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);

            if (res.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }

            final String response = EntityUtils.toString(res.getEntity());

            final ModuleFieldsParser parser = new ModuleFieldsParser(response);
            return new Module(moduleName, parser.getModuleFields(),
                    parser.getLinkFields());

        } catch (final JSONException jo) {
            throw new SugarCrmException(jo.getMessage());
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        }
    }

    /**
     * Gets server info. This will return information like version, flavor and
     * gmt_time.
     * 
     * @return Array - flavor - String - Retrieve the specific flavor of sugar.
     *         - version - String - Retrieve the version number of Sugar that
     *         the server is running. - gmt_time - String - Return the current
     *         time on the server in the format 'Y-m-d H:i:s'. This time is in
     *         GMT.
     * @param url
     *            a {@link java.lang.String} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static String getServerInfo(String url) throws SugarCrmException {

        String version = null;
        try {
            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);
            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, GET_SERVER_INFO));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }
            final String response = EntityUtils.toString(res.getEntity())
                    .toString();
            // TODO: have to parse the response
            JSONObject jsonResponse;
            try {
                jsonResponse = new JSONObject(response);
                version = (String) jsonResponse.get("version");
            } catch (final JSONException e) {
                Log.e(LOG_TAG, "Ops! Error" + e);
            }
        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        }

        return version;
    }

    /**
     * Perform a seamless login. This is used internally during the sync
     * process.
     * 
     * @param String
     *            $session -- Session ID returned by a previous call to login.
     * 
     * @return 1 -- integer - if the session was authenticated
     * 
     * @return 0 -- integer - if the session could not be authenticated
     * 
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public static int seamlessLogin(String url, String sessionId)
            throws SugarCrmException {

        int isValid = 0;

        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);

        try {
            final HttpClient httpClient = new DefaultHttpClient();
            final HttpPost req = new HttpPost(url);

            final String restData = org.json.simple.JSONValue
                    .toJSONString(data);

            // Add your data
            final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, SEAMLESS_LOGIN));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            httpClient.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            final HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.e(LOG_TAG, ERROR_MSG);
                throw new SugarCrmException(ERROR_MSG);
            }

            final String response = EntityUtils.toString(res.getEntity())
                    .toString();
            isValid = Integer.parseInt(response);

        } catch (final IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        }

        return isValid;
    }

}
