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
    String METHOD = "method";

    /** The Constant INPUT_TYPE. */
    String INPUT_TYPE = "input_type";

    /** The Constant RESPONSE_TYPE. */
    String RESPONSE_TYPE = "response_type";

    /** The Constant REST_DATA. */
    String REST_DATA = "rest_data";

    /** The Constant APPLICATION. */
    String APPLICATION = "application";

    /** The Constant NAME_VALUE_LIST. */
    String NAME_VALUE_LIST = "name_value_list";

    /** The Constant NAME_VALUE_LISTS. */
    String NAME_VALUE_LISTS = "name_value_lists";

    // input and response types for the request and response
    /** The Constant JSON. */
    String JSON = "json";

    // methods
    /** The Constant LOGIN. */
    String LOGIN = "login";

    /** The Constant LOGOUT. */
    String LOGOUT = "logout";

    /** The Constant GET_AVAILABLE_MODULES. */
    String GET_AVAILABLE_MODULES = "get_available_modules";

    /** The Constant GET_ENTRY_LIST. */
    String GET_ENTRY_LIST = "get_entry_list";

    /** The Constant GET_ENTRIES. */
    String GET_ENTRIES = "get_entries";

    /** The Constant GET_ENTRY. */
    String GET_ENTRY = "get_entry";

    /** The Constant GET_MODULE_FIELDS. */
    String GET_MODULE_FIELDS = "get_module_fields";

    /** The Constant SET_ENTRY. */
    String SET_ENTRY = "set_entry";

    /** The Constant SET_ENTRIES. */
    String SET_ENTRIES = "set_entries";

    /** The Constant GET_ENTRIES_COUNT. */
    String GET_ENTRIES_COUNT = "get_entries_count";

    /** The Constant GET_RELATIONSHIPS. */
    String GET_RELATIONSHIPS = "get_relationships";

    /** The Constant SET_RELATIONSHIP. */
    String SET_RELATIONSHIP = "set_relationship";

    /** The Constant SET_RELATIONSHIPS. */
    String SET_RELATIONSHIPS = "set_relationships";

    /** The Constant SEARCH_BY_MODULE. */
    String SEARCH_BY_MODULE = "search_by_module";

    /** The Constant GET_SERVER_INFO. */
    String GET_SERVER_INFO = "get_server_info";

    /** The Constant SEAMLESS_LOGIN. */
    String SEAMLESS_LOGIN = "seamless_login";

    /** The Constant GET_USER_ID. */
    String GET_USER_ID = "get_user_id";

    // params for the input JSON
    /** The Constant USER_AUTH. */
    String USER_AUTH = "user_auth";

    /** The Constant USER_NAME. */
    String USER_NAME = "user_name";

    /** The Constant PASSWORD. */
    String PASSWORD = "password";

    /** The Constant SESSION. */
    String SESSION = "session";

    /** The Constant PARENT_MODULE_NAME. */
    String PARENT_MODULE_NAME = "parent_module_name";

    /** The Constant MODULE_NAME. */
    String MODULE_NAME = "module_name";

    /** The Constant MODULE_NAMES. */
    String MODULE_NAMES = "module_names";

    /** The Constant BEAN_ID. */
    String BEAN_ID = "module_id";

    /** The Constant BEAN_IDS. */
    String BEAN_IDS = "module_ids";

    /** The Constant QUERY. */
    String QUERY = "query";

    /** The Constant ORDER_BY. */
    String ORDER_BY = "order_by";

    /** The Constant OFFSET. */
    String OFFSET = "offset";

    /** The Constant SELECT_FIELDS. */
    String SELECT_FIELDS = "select_fields";

    /** The Constant LINK_NAME_TO_FIELDS_ARRAY. */
    String LINK_NAME_TO_FIELDS_ARRAY = "link_name_to_fields_array";

    /** The Constant LINK_FIELD_NAME. */
    String LINK_FIELD_NAME = "link_field_name";

    /** The Constant LINK_FIELD_NAMES. */
    String LINK_FIELD_NAMES = "link_field_names";

    /** The Constant RELATED_MODULE_QUERY. */
    String RELATED_MODULE_QUERY = "related_module_query";

    /** The Constant RELATED_IDS. */
    String RELATED_IDS = "related_ids";

    /** The Constant RELATED_FIELDS. */
    String RELATED_FIELDS = "related_fields";

    /** The Constant RELATED_MODULE_LINK_NAME_TO_FIELDS_ARRAY. */
    String RELATED_MODULE_LINK_NAME_TO_FIELDS_ARRAY = "related_module_link_name_to_fields_array";

    /** The Constant MAX_RESULTS. */
    String MAX_RESULTS = "max_results";

    /** The Constant DELETED. */
    String DELETED = "deleted";

    /** The Constant FIELDS. */
    String FIELDS = "fields";

    /** The Constant SEARCH_STRING. */
    String SEARCH_STRING = "search_string";

    // params for the output JSON
    /** The Constant ID. */
    String ID = "id";

    /** The Constant IDS. */
    String IDS = "ids";

    /** The Constant MODULES. */
    String MODULES = "modules";

    /** The Constant MODULE_FIELDS. */
    String MODULE_FIELDS = "module_fields";

    /** The Constant ENTRY_LIST. */
    String ENTRY_LIST = "entry_list";

    /** The Constant RELATIONSHIP_LIST. */
    String RELATIONSHIP_LIST = "relationship_list";

    /** The Constant RESULT_COUNT. */
    String RESULT_COUNT = "result_count";

    /** The Constant NAME. */
    String NAME = "name";

    /** The Constant TYPE. */
    String TYPE = "type";

    /** The Constant LABEL. */
    String LABEL = "label";

    /** The Constant REQUIRED. */
    String REQUIRED = "required";

    /** The Constant CREATED. */
    String CREATED = "created";

    /** The Constant FAILED. */
    String FAILED = "failed";

    /** The Constant DESCRIPTION. */
    String DESCRIPTION = "description";

    /** The Constant LOGIN_FAILED. */
    String LOGIN_FAILED = "Login Failed!";

    /** The Constant RECORDS. */
    String RECORDS = "records";

    /** The Constant VALUE. */
    String VALUE = "value";

    /** The Constant MODULE. */
    String MODULE = "module";

    /** The Constant BEAN_NAME. */
    String BEAN_NAME = "bean_name";

    /** The Constant RELATIONSHIP. */
    String RELATIONSHIP = "relationship";

    /** The Constant ACCESS. */
    String ACCESS = "access";

    /** The Constant DELETE. */
    String DELETE = "delete";

    /** The Constant EDIT. */
    String EDIT = "edit";

    /** The Constant EXPORT. */
    String EXPORT = "export";

    /** The Constant IMPORT. */
    String IMPORT = "import";

    /** The Constant LIST. */
    String LIST = "list";

    /** The Constant VIEW. */
    String VIEW = "view";

    // Exceptions
    /** The Constant EXCEPTION. */
    String EXCEPTION = "Exception";

    /** The Constant JSON_EXCEPTION. */
    String JSON_EXCEPTION = "JSONException";

}
