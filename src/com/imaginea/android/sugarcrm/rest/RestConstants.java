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
 * FileName : RestConstants 
 * Description : 
                This  class is RestUtilConstants
 ******************************************************************************/

package com.imaginea.android.sugarcrm.rest;

/**
 * The Interface RestConstants.
 */
public interface RestConstants {

    // request params
    /** The Constant METHOD. */
    public static final String METHOD = "method";

    /** The Constant INPUT_TYPE. */
    public static final String INPUT_TYPE = "input_type";

    /** The Constant RESPONSE_TYPE. */
    public static final String RESPONSE_TYPE = "response_type";

    /** The Constant REST_DATA. */
    public static final String REST_DATA = "rest_data";

    /** The Constant APPLICATION. */
    public static final String APPLICATION = "application";

    /** The Constant NAME_VALUE_LIST. */
    public static final String NAME_VALUE_LIST = "name_value_list";

    /** The Constant NAME_VALUE_LISTS. */
    public static final String NAME_VALUE_LISTS = "name_value_lists";

    // input and response types for the request and response
    /** The Constant JSON. */
    public static final String JSON = "json";

    // methods
    /** The Constant LOGIN. */
    public static final String LOGIN = "login";

    /** The Constant LOGOUT. */
    public static final String LOGOUT = "logout";

    /** The Constant GET_AVAILABLE_MODULES. */
    public static final String GET_AVAILABLE_MODULES = "get_available_modules";

    /** The Constant GET_ENTRY_LIST. */
    public static final String GET_ENTRY_LIST = "get_entry_list";

    /** The Constant GET_ENTRIES. */
    public static final String GET_ENTRIES = "get_entries";

    /** The Constant GET_ENTRY. */
    public static final String GET_ENTRY = "get_entry";

    /** The Constant GET_MODULE_FIELDS. */
    public static final String GET_MODULE_FIELDS = "get_module_fields";

    /** The Constant SET_ENTRY. */
    public static final String SET_ENTRY = "set_entry";

    /** The Constant SET_ENTRIES. */
    public static final String SET_ENTRIES = "set_entries";

    /** The Constant GET_ENTRIES_COUNT. */
    public static final String GET_ENTRIES_COUNT = "get_entries_count";

    /** The Constant GET_RELATIONSHIPS. */
    public static final String GET_RELATIONSHIPS = "get_relationships";

    /** The Constant SET_RELATIONSHIP. */
    public static final String SET_RELATIONSHIP = "set_relationship";

    /** The Constant SET_RELATIONSHIPS. */
    public static final String SET_RELATIONSHIPS = "set_relationships";

    /** The Constant SEARCH_BY_MODULE. */
    public static final String SEARCH_BY_MODULE = "search_by_module";

    /** The Constant GET_SERVER_INFO. */
    public static final String GET_SERVER_INFO = "get_server_info";

    /** The Constant SEAMLESS_LOGIN. */
    public static final String SEAMLESS_LOGIN = "seamless_login";

    /** The Constant GET_USER_ID. */
    public static final String GET_USER_ID = "get_user_id";

    // params for the input JSON
    /** The Constant USER_AUTH. */
    public static final String USER_AUTH = "user_auth";

    /** The Constant USER_NAME. */
    public static final String USER_NAME = "user_name";

    /** The Constant PASSWORD. */
    public static final String PASSWORD = "password";

    /** The Constant SESSION. */
    public static final String SESSION = "session";

    /** The Constant PARENT_MODULE_NAME. */
    public static final String PARENT_MODULE_NAME = "parent_module_name";

    /** The Constant MODULE_NAME. */
    public static final String MODULE_NAME = "module_name";

    /** The Constant MODULE_NAMES. */
    public static final String MODULE_NAMES = "module_names";

    /** The Constant BEAN_ID. */
    public static final String BEAN_ID = "module_id";

    /** The Constant BEAN_IDS. */
    public static final String BEAN_IDS = "module_ids";

    /** The Constant QUERY. */
    public static final String QUERY = "query";

    /** The Constant ORDER_BY. */
    public static final String ORDER_BY = "order_by";

    /** The Constant OFFSET. */
    public static final String OFFSET = "offset";

    /** The Constant SELECT_FIELDS. */
    public static final String SELECT_FIELDS = "select_fields";

    /** The Constant LINK_NAME_TO_FIELDS_ARRAY. */
    public static final String LINK_NAME_TO_FIELDS_ARRAY = "link_name_to_fields_array";

    /** The Constant LINK_FIELD_NAME. */
    public static final String LINK_FIELD_NAME = "link_field_name";

    /** The Constant LINK_FIELD_NAMES. */
    public static final String LINK_FIELD_NAMES = "link_field_names";

    /** The Constant RELATED_MODULE_QUERY. */
    public static final String RELATED_MODULE_QUERY = "related_module_query";

    /** The Constant RELATED_IDS. */
    public static final String RELATED_IDS = "related_ids";

    /** The Constant RELATED_FIELDS. */
    public static final String RELATED_FIELDS = "related_fields";

    /** The Constant RELATED_MODULE_LINK_NAME_TO_FIELDS_ARRAY. */
    public static final String RELATED_MODULE_LINK_NAME_TO_FIELDS_ARRAY = "related_module_link_name_to_fields_array";

    /** The Constant MAX_RESULTS. */
    public static final String MAX_RESULTS = "max_results";

    /** The Constant DELETED. */
    public static final String DELETED = "deleted";

    /** The Constant FIELDS. */
    public static final String FIELDS = "fields";

    /** The Constant SEARCH_STRING. */
    public static final String SEARCH_STRING = "search_string";

    // params for the output JSON
    /** The Constant ID. */
    public static final String ID = "id";

    /** The Constant IDS. */
    public static final String IDS = "ids";

    /** The Constant MODULES. */
    public static final String MODULES = "modules";

    /** The Constant MODULE_FIELDS. */
    public static final String MODULE_FIELDS = "module_fields";

    /** The Constant ENTRY_LIST. */
    public static final String ENTRY_LIST = "entry_list";

    /** The Constant RELATIONSHIP_LIST. */
    public static final String RELATIONSHIP_LIST = "relationship_list";

    /** The Constant RESULT_COUNT. */
    public static final String RESULT_COUNT = "result_count";

    /** The Constant NAME. */
    public static final String NAME = "name";

    /** The Constant TYPE. */
    public static final String TYPE = "type";

    /** The Constant LABEL. */
    public static final String LABEL = "label";

    /** The Constant REQUIRED. */
    public static final String REQUIRED = "required";

    /** The Constant CREATED. */
    public static final String CREATED = "created";

    /** The Constant FAILED. */
    public static final String FAILED = "failed";

    /** The Constant DESCRIPTION. */
    public static final String DESCRIPTION = "description";

    /** The Constant LOGIN_FAILED. */
    public static final String LOGIN_FAILED = "Login Failed!";

    /** The Constant RECORDS. */
    public static final String RECORDS = "records";

    /** The Constant VALUE. */
    public static final String VALUE = "value";

    /** The Constant MODULE. */
    public static final String MODULE = "module";

    /** The Constant BEAN_NAME. */
    public static final String BEAN_NAME = "bean_name";

    /** The Constant RELATIONSHIP. */
    public static final String RELATIONSHIP = "relationship";

    /** The Constant ACCESS. */
    public static final String ACCESS = "access";

    /** The Constant DELETE. */
    public static final String DELETE = "delete";

    /** The Constant EDIT. */
    public static final String EDIT = "edit";

    /** The Constant EXPORT. */
    public static final String EXPORT = "export";

    /** The Constant IMPORT. */
    public static final String IMPORT = "import";

    /** The Constant LIST. */
    public static final String LIST = "list";

    /** The Constant VIEW. */
    public static final String VIEW = "view";

    // Exceptions
    /** The Constant EXCEPTION. */
    public static final String EXCEPTION = "Exception";

    /** The Constant JSON_EXCEPTION. */
    public static final String JSON_EXCEPTION = "JSONException";

}
