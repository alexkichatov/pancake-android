package com.imaginea.android.sugarcrm.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.imaginea.android.sugarcrm.rest.RestConstants;

/**
 * <p>
 * ModuleFieldsParser class.
 * </p>
 * 
 */
public class ModuleFieldsParser {

    private final String LOG_TAG = ModuleFieldsParser.class.getSimpleName();

    private List<ModuleField> moduleFields;

    private List<LinkField> linkFields;

    /**
     * <p>
     * Constructor for ModuleFieldsParser.
     * </p>
     * 
     * @param jsonResponse
     *            a {@link java.lang.String} object.
     * @throws org.json.JSONException
     *             if any.
     */
    public ModuleFieldsParser(final String jsonResponse) throws JSONException {
        final JSONObject responseObj = new JSONObject(jsonResponse);
        // String moduleName = responseObj.get("module_name").toString();

        final JSONObject moduleFieldsJSON = (JSONObject) responseObj
                .get("module_fields");
        setModuleFields(moduleFieldsJSON);

        try {
            final JSONObject linkFieldsJSON = (JSONObject) responseObj
                    .get("link_fields");
            setLinkFields(linkFieldsJSON);
        } catch (final ClassCastException cce) {
            // ignore : no linkFields
            linkFields = new ArrayList<LinkField>();
        }

    }

    private void setModuleFields(final JSONObject moduleFieldsJSON)
            throws JSONException {
        moduleFields = new ArrayList<ModuleField>();
        final Iterator iterator = moduleFieldsJSON.keys();
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

    private void setLinkFields(final JSONObject linkFieldsJSON)
            throws JSONException {
        linkFields = new ArrayList<LinkField>();
        final Iterator iterator = linkFieldsJSON.keys();
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

    private ModuleField getModuleField(final JSONObject nameValuePairsJson)
            throws JSONException {
        final String name = nameValuePairsJson.getString(RestConstants.NAME);
        final String type = nameValuePairsJson.getString(RestConstants.TYPE);
        final String label = nameValuePairsJson.getString(RestConstants.LABEL);
        final int required = nameValuePairsJson.getInt(RestConstants.REQUIRED);
        final boolean isRequired = required == 0 ? false : true;
        return new ModuleField(name, type, label, isRequired);
    }

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
     * <p>
     * Getter for the field <code>moduleFields</code>.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    public List<ModuleField> getModuleFields() {
        return moduleFields;
    }

    /**
     * <p>
     * Getter for the field <code>linkFields</code>.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    public List<LinkField> getLinkFields() {
        return linkFields;
    }

}
