/*******************************************************************************
 * Copyright (c) 2013 Asha, Muralidaran.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *          Asha, Muralidaran - initial API and implementation
 * author:  chander
 * Project Name : SugarCrm Pancake
 * FileName : DatabaseHelper 
 * Description : 
                This class is s to identify the selection arguments(projections) and
 * provide a projection map to retrieve column values by name instead of column
 * number
 ******************************************************************************/

package com.imaginea.android.sugarcrm.provider;

import android.app.SearchManager;
import android.net.Uri;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.rest.RestConstants;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * The Class SugarCRMContent.
 */
public final class SugarCRMContent {

    private static final String SCHEME = "content://";

    /** The Constant SORTBY_ASC. */
    private static final String SORTBY_ASC = " ASC";

    /** The Constant SORTBY_DESC. */
    private static final String SORTBY_DESC = " DESC";

    /** The Constant AUTHORITY. */
    public static final String AUTHORITY = SugarCRMProvider.AUTHORITY;

    /** The Constant RECORD_ID. */
    public static final String RECORD_ID = "_id";

    /** The Constant SUGAR_BEAN_ID. */
    public static final String SUGAR_BEAN_ID = ModuleFields.ID;

    /** The Constant MODULE_ROW_ID. */
    public static final String MODULE_ROW_ID = "module_id";

    /** The Constant ROLE_ROW_ID. */
    public static final String ROLE_ROW_ID = "role_id";

    private SugarCRMContent() {

    }

    /**
     * The Class Accounts.
     */
    public static final class Accounts implements AccountsColumns {

        /** The Constant CONTENT_URI. */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME
                + SugarCRMProvider.AUTHORITY + "/" + Util.ACCOUNTS);

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = NAME + SORTBY_ASC;

        /** The Constant LIST_PROJECTION. */
        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID,
                NAME, EMAIL1, CREATED_BY_NAME };

        /** The Constant LIST_VIEW_PROJECTION. */
        public static final String[] LIST_VIEW_PROJECTION = { NAME,
                PARENT_NAME, EMAIL1 };

        /** The Constant SEARCH_PROJECTION. */
        public static final String[] SEARCH_PROJECTION = {
                RECORD_ID,
                NAME + " AS " + (SearchManager.SUGGEST_COLUMN_TEXT_1),
                RECORD_ID + " AS "
                        + (SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID) };

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID,
                NAME, PARENT_NAME, EMAIL1, PHONE_OFFICE, PHONE_FAX, WEBSITE,
                EMPLOYEES, TICKER_SYMBOL, ANNUAL_REVENUE,
                BILLING_ADDRESS_STREET, BILLING_ADDRESS_STREET_2,
                BILLING_ADDRESS_STREET_3, BILLING_ADDRESS_STREET_4,
                BILLING_ADDRESS_CITY, BILLING_ADDRESS_STATE,
                BILLING_ADDRESS_POSTALCODE, BILLING_ADDRESS_COUNTRY,
                SHIPPING_ADDRESS_STREET, SHIPPING_ADDRESS_STREET_2,
                SHIPPING_ADDRESS_STREET_3, SHIPPING_ADDRESS_STREET_4,
                SHIPPING_ADDRESS_CITY, SHIPPING_ADDRESS_STATE,
                SHIPPING_ADDRESS_POSTALCODE, SHIPPING_ADDRESS_COUNTRY,
                ASSIGNED_USER_NAME, CREATED_BY_NAME, DATE_ENTERED,
                DATE_MODIFIED, DELETED };

    }

    /**
     * The Interface AccountsColumns.
     */
    public interface AccountsColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The bean id. */
        String BEAN_ID = SUGAR_BEAN_ID;

        /** The name. */
        String NAME = ModuleFields.NAME;

        /** The EMAI l1. */
        String EMAIL1 = ModuleFields.EMAIL1;

        /** The parent name. */
        String PARENT_NAME = ModuleFields.PARENT_NAME;

        /** The phone office. */
        String PHONE_OFFICE = ModuleFields.PHONE_OFFICE;

        /** The phone fax. */
        String PHONE_FAX = ModuleFields.PHONE_FAX;

        /** The website. */
        String WEBSITE = ModuleFields.WEBSITE;

        /** The employees. */
        String EMPLOYEES = ModuleFields.EMPLOYEES;

        /** The ticker symbol. */
        String TICKER_SYMBOL = ModuleFields.TICKER_SYMBOL;

        /** The annual revenue. */
        String ANNUAL_REVENUE = ModuleFields.ANNUAL_REVENUE;

        /** The billing address street. */
        String BILLING_ADDRESS_STREET = ModuleFields.BILLING_ADDRESS_STREET;

        /** The BILLIN g_ addres s_ stree t_2. */
        String BILLING_ADDRESS_STREET_2 = ModuleFields.BILLING_ADDRESS_STREET_2;

        /** The BILLIN g_ addres s_ stree t_3. */
        String BILLING_ADDRESS_STREET_3 = ModuleFields.BILLING_ADDRESS_STREET_3;

        /** The BILLIN g_ addres s_ stree t_4. */
        String BILLING_ADDRESS_STREET_4 = ModuleFields.BILLING_ADDRESS_STREET_4;

        /** The billing address city. */
        String BILLING_ADDRESS_CITY = ModuleFields.BILLING_ADDRESS_CITY;

        /** The billing address state. */
        String BILLING_ADDRESS_STATE = ModuleFields.BILLING_ADDRESS_STATE;

        /** The billing address postalcode. */
        String BILLING_ADDRESS_POSTALCODE = ModuleFields.BILLING_ADDRESS_POSTALCODE;;

        /** The billing address country. */
        String BILLING_ADDRESS_COUNTRY = ModuleFields.BILLING_ADDRESS_COUNTRY;;

        /** The shipping address street. */
        String SHIPPING_ADDRESS_STREET = ModuleFields.SHIPPING_ADDRESS_STREET;

        /** The SHIPPIN g_ addres s_ stree t_2. */
        String SHIPPING_ADDRESS_STREET_2 = ModuleFields.SHIPPING_ADDRESS_STREET_2;

        /** The SHIPPIN g_ addres s_ stree t_3. */
        String SHIPPING_ADDRESS_STREET_3 = ModuleFields.SHIPPING_ADDRESS_STREET_3;

        /** The SHIPPIN g_ addres s_ stree t_4. */
        String SHIPPING_ADDRESS_STREET_4 = ModuleFields.SHIPPING_ADDRESS_STREET_4;

        /** The shipping address city. */
        String SHIPPING_ADDRESS_CITY = ModuleFields.SHIPPING_ADDRESS_CITY;

        /** The shipping address state. */
        String SHIPPING_ADDRESS_STATE = ModuleFields.SHIPPING_ADDRESS_STATE;

        /** The shipping address postalcode. */
        String SHIPPING_ADDRESS_POSTALCODE = ModuleFields.SHIPPING_ADDRESS_POSTALCODE;

        /** The shipping address country. */
        String SHIPPING_ADDRESS_COUNTRY = ModuleFields.SHIPPING_ADDRESS_COUNTRY;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;

        /** The date entered. */
        String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The assigned user name. */
        String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        /** The created by name. */
        String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;

    }

    /**
     * The Class Contacts.
     */
    public static final class Contacts implements ContactsColumns {

        /** The Constant CONTENT_URI. */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME
                + SugarCRMProvider.AUTHORITY + "/" + Util.CONTACTS);

        /** The Constant ID_COLUMN. */
        public static final int ID_COLUMN = 0;

        /** The Constant BEAN_ID_COLUMN. */
        public static final int BEAN_ID_COLUMN = 1;

        /** The Constant FIRST_NAME_COLUMN. */
        public static final int FIRST_NAME_COLUMN = 2;

        /** The Constant LAST_NAME_COLUMN. */
        public static final int LAST_NAME_COLUMN = 3;

        /** The Constant ACCOUNT_NAME_COLUMN. */
        public static final int ACCOUNT_NAME_COLUMN = 4;

        /** The Constant PHONE_MOBILE_COLUMN. */
        public static final int PHONE_MOBILE_COLUMN = 5;

        /** The Constant PHONE_WORK_COLUMN. */
        public static final int PHONE_WORK_COLUMN = 6;

        /** The Constant CREATED_BY_COLUMN. */
        public static final int CREATED_BY_COLUMN = 7;

        /** The Constant MODIFIED_BY_NAME_COLUMN. */
        public static final int MODIFIED_BY_NAME_COLUMN = 8;

        /** The Constant EMAIL1_COLUMN. */
        public static final int EMAIL1_COLUMN = 9;

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = FIRST_NAME + SORTBY_ASC;

        /** The Constant LIST_PROJECTION. */
        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID,
                FIRST_NAME, LAST_NAME, EMAIL1, CREATED_BY_NAME };

        /** The Constant REST_LIST_PROJECTION. */
        public static final String[] REST_LIST_PROJECTION = { ModuleFields.ID,
                FIRST_NAME, LAST_NAME, EMAIL1 };

        /** The Constant LIST_VIEW_PROJECTION. */
        public static final String[] LIST_VIEW_PROJECTION = { FIRST_NAME,
                LAST_NAME, ACCOUNT_NAME, EMAIL1 };

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID,
                FIRST_NAME, LAST_NAME, ACCOUNT_NAME, EMAIL1, PHONE_MOBILE,
                PHONE_WORK, ASSIGNED_USER_NAME, CREATED_BY_NAME, DATE_ENTERED,
                DATE_MODIFIED, DELETED, ACCOUNT_ID };

    }

    /**
     * The Interface ContactsColumns.
     */
    public interface ContactsColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The bean id. */
        String BEAN_ID = SUGAR_BEAN_ID;

        /** The first name. */
        String FIRST_NAME = ModuleFields.FIRST_NAME;

        /** The last name. */
        String LAST_NAME = ModuleFields.LAST_NAME;

        /** The account name. */
        String ACCOUNT_NAME = ModuleFields.ACCOUNT_NAME;

        /** The phone mobile. */
        String PHONE_MOBILE = ModuleFields.PHONE_MOBILE;

        /** The phone work. */
        String PHONE_WORK = ModuleFields.PHONE_WORK;

        /** The EMAI l1. */
        String EMAIL1 = ModuleFields.EMAIL1;

        /** The created by. */
        String CREATED_BY = ModuleFields.CREATED_BY;

        /** The modified by name. */
        String MODIFIED_BY_NAME = ModuleFields.MODIFIED_BY_NAME;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;

        // accountId
        /** The account id. */
        String ACCOUNT_ID = ModuleFields.ACCOUNT_ID;

        /** The assigned user name. */
        String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        /** The date entered. */
        String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The created by name. */
        String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;
    }

    /**
     * The Interface AccountsContactsColumns.
     */
    public interface AccountsContactsColumns {

        /** The account id. */
        String ACCOUNT_ID = ModuleFields.ACCOUNT_ID;

        /** The contact id. */
        String CONTACT_ID = ModuleFields.CONTACT_ID;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;
    }

    /**
     * The Interface AccountsOpportunitiesColumns.
     */
    public interface AccountsOpportunitiesColumns {

        /** The account id. */
        String ACCOUNT_ID = ModuleFields.ACCOUNT_ID;

        /** The opportunity id. */
        String OPPORTUNITY_ID = ModuleFields.OPPORTUNITY_ID;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;
    }

    /**
     * The Interface AccountsCasesColumns.
     */
    public interface AccountsCasesColumns {

        /** The account id. */
        String ACCOUNT_ID = ModuleFields.ACCOUNT_ID;

        /** The case id. */
        String CASE_ID = Util.CASE_ID;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;
    }

    /**
     * The Interface ContactsOpportunitiesColumns.
     */
    public interface ContactsOpportunitiesColumns {

        /** The contact id. */
        String CONTACT_ID = ModuleFields.CONTACT_ID;

        /** The opportunity id. */
        String OPPORTUNITY_ID = ModuleFields.OPPORTUNITY_ID;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;
    }

    /**
     * The Interface ContactsCasesColumns.
     */
    public interface ContactsCasesColumns {

        /** The contact id. */
        String CONTACT_ID = ModuleFields.CONTACT_ID;

        // TODO - not really a todo but a cross check to see
        /** The case id. */
        String CASE_ID = Util.CASE_ID;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;
    }

    /**
     * The Interface RecentColumns.
     */
    public interface RecentColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The actual id. */
        String ACTUAL_ID = ModuleFields.ACTUAL_RECORD_ID;

        /** The bean id. */
        String BEAN_ID = SUGAR_BEAN_ID;

        /** The ref module name. */
        String REF_MODULE_NAME = ModuleFields.MODULE_NAME;

        /** The NAM e_1. */
        String NAME_1 = ModuleFields.NAME_1;

        /** The NAM e_2. */
        String NAME_2 = ModuleFields.NAME_2;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;

    }

    /**
     * The Class Recent.
     */
    public static final class Recent implements RecentColumns {

        /** The Constant CONTENT_URI. */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME
                + SugarCRMProvider.AUTHORITY + "/" + Util.RECENT);

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = ModuleFields.NAME
                + SORTBY_ASC;

        /** The Constant LIST_PROJECTION. */
        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID,
                NAME_1, NAME_2, REF_MODULE_NAME };

        /** The Constant LIST_VIEW_PROJECTION. */
        public static final String[] LIST_VIEW_PROJECTION = { NAME_1, NAME_2,
                REF_MODULE_NAME };

        // Details projection is not needed for recent, as the record_id,
        // bean_id and module name
        // will be used to show the detailed view

    }

    /**
     * The Class Leads.
     */
    public static final class Leads implements LeadsColumns {

        /** The Constant CONTENT_URI. */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME
                + SugarCRMProvider.AUTHORITY + "/" + Util.LEADS);

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = ModuleFields.LAST_NAME
                + SORTBY_ASC;

        /** The Constant LIST_PROJECTION. */
        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID,
                FIRST_NAME, LAST_NAME, CREATED_BY_NAME };

        /** The Constant LIST_VIEW_PROJECTION. */
        public static final String[] LIST_VIEW_PROJECTION = { FIRST_NAME,
                LAST_NAME, ACCOUNT_NAME, EMAIL1 };

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID,
                FIRST_NAME, LAST_NAME, ACCOUNT_NAME, EMAIL1, LEAD_SOURCE,
                PHONE_WORK, PHONE_FAX, TITLE, ASSIGNED_USER_NAME,
                CREATED_BY_NAME, DATE_ENTERED, DATE_MODIFIED, DELETED,
                ACCOUNT_ID };

    }

    /**
     * The Interface LeadsColumns.
     */
    public interface LeadsColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The bean id. */
        String BEAN_ID = SUGAR_BEAN_ID;

        /** The first name. */
        String FIRST_NAME = ModuleFields.FIRST_NAME;

        /** The last name. */
        String LAST_NAME = ModuleFields.LAST_NAME;

        /** The lead source. */
        String LEAD_SOURCE = ModuleFields.LEAD_SOURCE;

        /** The account name. */
        String ACCOUNT_NAME = ModuleFields.ACCOUNT_NAME;

        /** The phone mobile. */
        String PHONE_MOBILE = ModuleFields.PHONE_MOBILE;

        /** The EMAI l1. */
        String EMAIL1 = ModuleFields.EMAIL1;

        /** The phone work. */
        String PHONE_WORK = ModuleFields.PHONE_WORK;

        /** The phone fax. */
        String PHONE_FAX = ModuleFields.PHONE_FAX;

        /** The assigned user name. */
        String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        /** The title. */
        String TITLE = ModuleFields.TITLE;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;

        /** The account id. */
        String ACCOUNT_ID = ModuleFields.ACCOUNT_ID;

        /** The date entered. */
        String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The created by name. */
        String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;
    }

    /**
     * The Class Opportunities.
     */
    public static final class Opportunities implements OpportunitiesColumns {

        /** The Constant CONTENT_URI. */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME
                + SugarCRMProvider.AUTHORITY + "/" + Util.OPPORTUNITIES);

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = NAME + SORTBY_ASC;

        /** The Constant LIST_PROJECTION. */
        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID,
                NAME, OPPORTUNITY_TYPE, CREATED_BY_NAME };

        /** The Constant LIST_VIEW_PROJECTION. */
        public static final String[] LIST_VIEW_PROJECTION = { NAME,
                ACCOUNT_NAME, DATE_CLOSED };

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID,
                NAME, ACCOUNT_NAME, DATE_CLOSED, AMOUNT, OPPORTUNITY_TYPE,
                LEAD_SOURCE, SALES_STAGE, CAMPAIGN_NAME, PROBABILITY,
                ASSIGNED_USER_NAME, CREATED_BY_NAME, DATE_ENTERED,
                DATE_MODIFIED, DELETED, ACCOUNT_ID };

    }

    /**
     * The Interface OpportunitiesColumns.
     */
    public interface OpportunitiesColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The bean id. */
        String BEAN_ID = SUGAR_BEAN_ID;

        /** The name. */
        String NAME = ModuleFields.NAME;

        /** The date entered. */
        String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The modified user id. */
        String MODIFIED_USER_ID = ModuleFields.MODIFIED_USER_ID;

        /** The modified by name. */
        String MODIFIED_BY_NAME = ModuleFields.MODIFIED_BY_NAME;

        /** The created by. */
        String CREATED_BY = ModuleFields.CREATED_BY;

        /** The created by name. */
        String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;

        /** The description. */
        String DESCRIPTION = ModuleFields.DESCRIPTION;

        /** The assigned user id. */
        String ASSIGNED_USER_ID = ModuleFields.ASSIGNED_USER_ID;

        /** The assigned user name. */
        String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        /** The opportunity type. */
        String OPPORTUNITY_TYPE = ModuleFields.OPPORTUNITY_TYPE;

        /** The account name. */
        String ACCOUNT_NAME = ModuleFields.ACCOUNT_NAME;

        /** The campaign name. */
        String CAMPAIGN_NAME = ModuleFields.CAMPAIGN_NAME;

        /** The lead source. */
        String LEAD_SOURCE = ModuleFields.LEAD_SOURCE;

        /** The amount. */
        String AMOUNT = ModuleFields.AMOUNT;

        /** The amount usdollar. */
        String AMOUNT_USDOLLAR = ModuleFields.AMOUNT_USDOLLAR;

        /** The currency id. */
        String CURRENCY_ID = ModuleFields.CURRENCY_ID;

        /** The currency name. */
        String CURRENCY_NAME = ModuleFields.CURRENCY_NAME;

        /** The currency symbol. */
        String CURRENCY_SYMBOL = ModuleFields.CURRENCY_SYMBOL;

        /** The date closed. */
        String DATE_CLOSED = ModuleFields.DATE_CLOSED;

        /** The next step. */
        String NEXT_STEP = ModuleFields.NEXT_STEP;

        /** The sales stage. */
        String SALES_STAGE = ModuleFields.SALES_STAGE;

        /** The probability. */
        String PROBABILITY = ModuleFields.PROBABILITY;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;

        /** The account id. */
        String ACCOUNT_ID = ModuleFields.ACCOUNT_ID;

    }

    /**
     * The Class Cases.
     */
    public static final class Cases implements CasesColumns {

        /** The Constant CONTENT_URI. */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME
                + SugarCRMProvider.AUTHORITY + "/" + Util.CASES);

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = DATE_MODIFIED
                + SORTBY_ASC;

        /** The Constant LIST_PROJECTION. */
        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID,
                NAME, CASE_NUMBER, PRIORITY, DATE_MODIFIED, CREATED_BY_NAME };

        /** The Constant LIST_VIEW_PROJECTION. */
        public static final String[] LIST_VIEW_PROJECTION = { NAME,
                ACCOUNT_NAME, DATE_ENTERED };

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID,
                NAME, ACCOUNT_NAME, DATE_ENTERED, CASE_NUMBER, PRIORITY,
                ASSIGNED_USER_NAME, STATUS, DESCRIPTION, RESOLUTION,
                CREATED_BY_NAME, DATE_MODIFIED, DELETED };

    }

    /**
     * The Interface CasesColumns.
     */
    public interface CasesColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The bean id. */
        String BEAN_ID = SUGAR_BEAN_ID;

        /** The name. */
        String NAME = ModuleFields.NAME;

        /** The case number. */
        String CASE_NUMBER = ModuleFields.CASE_NUMBER;

        /** The account name. */
        String ACCOUNT_NAME = ModuleFields.ACCOUNT_NAME;

        /** The priority. */
        String PRIORITY = ModuleFields.PRIORITY;

        /** The assigned user name. */
        String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        /** The status. */
        String STATUS = ModuleFields.STATUS;

        /** The description. */
        String DESCRIPTION = ModuleFields.DESCRIPTION;

        /** The resolution. */
        String RESOLUTION = ModuleFields.RESOLUTION;

        /** The date entered. */
        String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;

        /** The created by name. */
        String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;

    }

    /**
     * The Class Calls.
     */
    public static final class Calls implements CallsColumns {

        /** The Constant CONTENT_URI. */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME
                + SugarCRMProvider.AUTHORITY + "/" + Util.CALLS);

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = START_DATE + SORTBY_ASC;

        /** The Constant LIST_PROJECTION. */
        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID,
                NAME, START_DATE, CREATED_BY_NAME };

        /** The Constant LIST_VIEW_PROJECTION. */
        public static final String[] LIST_VIEW_PROJECTION = { NAME, START_DATE,
                ASSIGNED_USER_NAME };

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID,
                NAME, ACCOUNT_NAME, START_DATE, ASSIGNED_USER_NAME,
                DURATION_HOURS, DURATION_MINUTES, DESCRIPTION, CREATED_BY_NAME,
                DATE_ENTERED, DATE_MODIFIED, DELETED };

    }

    /**
     * The Interface CallsColumns.
     */
    public interface CallsColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The bean id. */
        String BEAN_ID = SUGAR_BEAN_ID;

        /** The name. */
        String NAME = ModuleFields.NAME;

        /** The status. */
        String STATUS = ModuleFields.STATUS;

        /** The start date. */
        String START_DATE = ModuleFields.DATE_START;

        /** The duration hours. */
        String DURATION_HOURS = ModuleFields.DURATION_HOURS;

        /** The duration minutes. */
        String DURATION_MINUTES = ModuleFields.DURATION_MINUTES;

        /** The account name. */
        String ACCOUNT_NAME = ModuleFields.ACCOUNT_NAME;

        /** The assigned user name. */
        String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        /** The description. */
        String DESCRIPTION = ModuleFields.DESCRIPTION;

        /** The date entered. */
        String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;

        /** The created by name. */
        String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;

    }

    /**
     * The Class Meetings.
     */
    public static final class Meetings implements MeetingsColumns {

        /** The Constant CONTENT_URI. */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME
                + SugarCRMProvider.AUTHORITY + "/" + Util.MEETINGS);

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = START_DATE + SORTBY_ASC;

        /** The Constant LIST_PROJECTION. */
        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID,
                NAME, START_DATE, CREATED_BY_NAME };

        /** The Constant LIST_VIEW_PROJECTION. */
        public static final String[] LIST_VIEW_PROJECTION = { NAME, START_DATE,
                DURATION_HOURS };

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID,
                NAME, START_DATE, DURATION_HOURS, STATUS, LOCATION,
                DURATION_MINUTES, ASSIGNED_USER_NAME, DESCRIPTION,
                CREATED_BY_NAME, DATE_ENTERED, DATE_MODIFIED, DELETED };

    }

    /**
     * The Interface MeetingsColumns.
     */
    public interface MeetingsColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The bean id. */
        String BEAN_ID = SUGAR_BEAN_ID;

        /** The name. */
        String NAME = ModuleFields.NAME;

        /** The status. */
        String STATUS = ModuleFields.STATUS;

        /** The location. */
        String LOCATION = ModuleFields.LOCATION;

        /** The start date. */
        String START_DATE = ModuleFields.DATE_START;

        /** The duration hours. */
        String DURATION_HOURS = ModuleFields.DURATION_HOURS;

        /** The duration minutes. */
        String DURATION_MINUTES = ModuleFields.DURATION_MINUTES;

        /** The account name. */
        String ACCOUNT_NAME = ModuleFields.ACCOUNT_NAME;

        /** The assigned user name. */
        String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        /** The description. */
        String DESCRIPTION = ModuleFields.DESCRIPTION;

        /** The date entered. */
        String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;

        /** The created by name. */
        String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;

    }

    /**
     * The Class Campaigns.
     */
    public static final class Campaigns implements CampaignColumns {

        /** The Constant CONTENT_URI. */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME
                + SugarCRMProvider.AUTHORITY + "/" + Util.CAMPAIGNS);

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = START_DATE + SORTBY_ASC;

        /** The Constant LIST_PROJECTION. */
        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID,
                NAME, START_DATE, CREATED_BY_NAME };

        /** The Constant LIST_VIEW_PROJECTION. */
        public static final String[] LIST_VIEW_PROJECTION = { NAME, END_DATE,
                CAMPAIGN_TYPE };

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID,
                NAME, START_DATE, CAMPAIGN_TYPE, STATUS, END_DATE, BUDGET,
                ACTUAL_COST, EXPECTED_COST, EXPECTED_REVENUE, IMPRESSIONS,
                OBJECTIVE, FREQUENCY, ASSIGNED_USER_NAME, DESCRIPTION,
                CREATED_BY_NAME, DATE_ENTERED, DATE_MODIFIED, DELETED };

    }

    /**
     * The Interface CampaignColumns.
     */
    public interface CampaignColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The bean id. */
        String BEAN_ID = SUGAR_BEAN_ID;

        /** The name. */
        String NAME = ModuleFields.NAME;

        /** The status. */
        String STATUS = ModuleFields.STATUS;

        /** The start date. */
        String START_DATE = ModuleFields.START_DATE;

        /** The end date. */
        String END_DATE = ModuleFields.END_DATE;

        /** The campaign type. */
        String CAMPAIGN_TYPE = ModuleFields.CAMPAIGN_TYPE;

        /** The budget. */
        String BUDGET = ModuleFields.BUDGET;

        /** The actual cost. */
        String ACTUAL_COST = ModuleFields.ACTUAL_COST;

        /** The expected cost. */
        String EXPECTED_COST = ModuleFields.EXPECTED_COST;

        /** The expected revenue. */
        String EXPECTED_REVENUE = ModuleFields.EXPECTED_REVENUE;

        /** The impressions. */
        String IMPRESSIONS = ModuleFields.IMPRESSIONS;

        /** The objective. */
        String OBJECTIVE = ModuleFields.OBJECTIVE;

        /** The frequency. */
        String FREQUENCY = ModuleFields.FREQUENCY;

        /** The assigned user name. */
        String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        /** The description. */
        String DESCRIPTION = ModuleFields.DESCRIPTION;

        /** The date entered. */
        String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The deleted. */
        String DELETED = ModuleFields.DELETED;

        /** The created by name. */
        String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;

    }

    /**
     * The Class Modules.
     */
    public static final class Modules implements ModuleColumns {

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID,
                MODULE_NAME, LAST_SYNC_TIME };

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = MODULE_NAME
                + SORTBY_DESC;

        /** The Constant CONTENT_URI. */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME
                + SugarCRMProvider.AUTHORITY + "/" + Util.MODULES);
    }

    /**
     * The Interface ModuleColumns.
     */
    public interface ModuleColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The module name. */
        String MODULE_NAME = RestConstants.NAME;

        /** The last sync time. */
        String LAST_SYNC_TIME = "last_sync_time";
    }

    /**
     * The Class ModuleFieldsTableInfo.
     */
    public static final class ModuleFieldsTableInfo implements
            ModuleFieldColumns {

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID, NAME,
                LABEL, TYPE, IS_REQUIRED, MODULE_ID };

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = NAME + SORTBY_DESC;

        /** The Constant CONTENT_URI. */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME
                + SugarCRMProvider.AUTHORITY + "/" + Util.MODULES_FIELDS);
    }

    /**
     * The Interface ModuleFieldColumns.
     */
    public interface ModuleFieldColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The name. */
        String NAME = RestConstants.NAME;

        /** The label. */
        String LABEL = RestConstants.LABEL;

        /** The type. */
        String TYPE = RestConstants.TYPE;

        /** The is required. */
        String IS_REQUIRED = RestConstants.REQUIRED;

        /** The module id. */
        String MODULE_ID = MODULE_ROW_ID;
    }

    /**
     * The Class LinkFields.
     */
    public static final class LinkFields implements LinkFieldColumns {

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID, NAME,
                TYPE, RELATIONSHIP, MODULE, BEAN_NAME, MODULE_ID };

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = NAME + SORTBY_DESC;
    }

    /**
     * The Interface LinkFieldColumns.
     */
    public interface LinkFieldColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The name. */
        String NAME = RestConstants.NAME;

        /** The type. */
        String TYPE = RestConstants.TYPE;

        /** The relationship. */
        String RELATIONSHIP = RestConstants.RELATIONSHIP;

        /** The module. */
        String MODULE = RestConstants.MODULE;

        /** The bean name. */
        String BEAN_NAME = RestConstants.BEAN_NAME;

        /** The module id. */
        String MODULE_ID = MODULE_ROW_ID;
    }

    /**
     * The Interface SyncColumns.
     */
    public interface SyncColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The sync id. */
        String SYNC_ID = Util.SYNC_ID;

        /** The sync related id. */
        String SYNC_RELATED_ID = Util.SYNC_RELATED_ID;

        /** The sync command. */
        String SYNC_COMMAND = Util.SYNC_COMMAND;

        /** The module. */
        String MODULE = RestConstants.MODULE;

        /** The related module. */
        String RELATED_MODULE = Util.RELATED_MODULE;

        /** The date modified. */
        String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        /** The sync status. */
        String SYNC_STATUS = Util.STATUS;

    }

    /**
     * The Class Sync.
     */
    public static final class Sync implements SyncColumns {
        /** The Constant ID_COLUMN. */
        public static final int ID_COLUMN = 0;

        /** The Constant SYNC_ID_COLUMN. */
        public static final int SYNC_ID_COLUMN = 1;

        /** The Constant SYNC_RELATED_ID_COLUMN. */
        public static final int SYNC_RELATED_ID_COLUMN = 2;

        /** The Constant SYNC_COMMAND_COLUMN. */
        public static final int SYNC_COMMAND_COLUMN = 3;

        /** The Constant MODULE_NAME_COLUMN. */
        public static final int MODULE_NAME_COLUMN = 4;

        /** The Constant RELATED_MODULE_NAME_COLUMN. */
        public static final int RELATED_MODULE_NAME_COLUMN = 5;

        /** The Constant MODIFIED_DATE_COLUMN. */
        public static final int MODIFIED_DATE_COLUMN = 6;

        /** The Constant STATUS_COLUMN. */
        public static final int STATUS_COLUMN = 7;

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = ModuleFields.DATE_MODIFIED
                + SORTBY_DESC;

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { ID, SYNC_ID,
                SYNC_RELATED_ID, SYNC_COMMAND, MODULE, RELATED_MODULE,
                DATE_MODIFIED, SYNC_STATUS };

    }

    /**
     * The Interface ACLRoleColumns.
     */
    public interface ACLRoleColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The role id. */
        String ROLE_ID = SUGAR_BEAN_ID;

        /** The name. */
        String NAME = ModuleFields.NAME;

        /** The type. */
        String TYPE = ModuleFields.TYPE;

        /** The description. */
        String DESCRIPTION = ModuleFields.DESCRIPTION;
    }

    /**
     * The Class ACLRoles.
     */
    public static final class ACLRoles implements ACLRoleColumns {

        /** The Constant INSERT_PROJECTION. */
        public static final String[] INSERT_PROJECTION = { SUGAR_BEAN_ID, NAME,
                TYPE, DESCRIPTION };

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID,
                SUGAR_BEAN_ID, NAME, TYPE, DESCRIPTION };

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = NAME + SORTBY_DESC;
    }

    /**
     * The Interface ACLActionColumns.
     */
    public interface ACLActionColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The action id. */
        String ACTION_ID = SUGAR_BEAN_ID;

        /** The name. */
        String NAME = ModuleFields.NAME;

        /** The category. */
        String CATEGORY = "category";

        /** The aclaccess. */
        String ACLACCESS = "aclaccess";

        /** The acltype. */
        String ACLTYPE = "acltype";

        /** The role id. */
        String ROLE_ID = ROLE_ROW_ID;
    }

    /**
     * The Class ACLActions.
     */
    public static final class ACLActions implements ACLActionColumns {

        /** The Constant INSERT_PROJECTION. */
        public static final String[] INSERT_PROJECTION = { ACTION_ID, NAME,
                CATEGORY, ACLACCESS, ACLTYPE };

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID,
                ACTION_ID, NAME, CATEGORY, ACLACCESS, ACLTYPE };

        /** The default sort order for this table. */
        public static final String DEFAULT_SORT_ORDER = NAME + SORTBY_ASC;
    }

    /**
     * The Interface UserColumns.
     */
    public interface UserColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The user id. */
        String USER_ID = ModuleFields.ID;

        /** The user name. */
        String USER_NAME = ModuleFields.USER_NAME;

        /** The first name. */
        String FIRST_NAME = ModuleFields.FIRST_NAME;

        /** The last name. */
        String LAST_NAME = ModuleFields.LAST_NAME;
    }

    /**
     * The Class Users.
     */
    public static final class Users implements UserColumns {

        /** The Constant CONTENT_URI. */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME
                + SugarCRMProvider.AUTHORITY + "/" + Util.USERS);

        /** The Constant INSERT_PROJECTION. */
        public static final String[] INSERT_PROJECTION = { USER_ID, USER_NAME,
                FIRST_NAME, LAST_NAME };

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID, USER_ID,
                USER_NAME, FIRST_NAME, LAST_NAME };
    }

    /**
     * The Interface ModuleFieldGroupColumns.
     */
    public interface ModuleFieldGroupColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The title. */
        String TITLE = "title";

        /** The group id. */
        String GROUP_ID = "group_id";
    }

    /**
     * The Class ModuleFieldGroups.
     */
    public static final class ModuleFieldGroups implements
            ModuleFieldGroupColumns {

        /** The Constant INSERT_PROJECTION. */
        public static final String[] INSERT_PROJECTION = { TITLE, GROUP_ID };

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID, TITLE,
                GROUP_ID };
    }

    /**
     * The Interface ModuleFieldSortOrderColumns.
     */
    public interface ModuleFieldSortOrderColumns {

        /** The id. */
        String ID = RECORD_ID;

        /** The field sort id. */
        String FIELD_SORT_ID = "field_sort_id";

        /** The group id. */
        String GROUP_ID = "group_id";

        /** The module field id. */
        String MODULE_FIELD_ID = "module_field_id";

        /** The module id. */
        String MODULE_ID = "module_id";
    }

    /**
     * The Class ModuleFieldSortOrder.
     */
    public static final class ModuleFieldSortOrder implements
            ModuleFieldSortOrderColumns {

        /** The Constant INSERT_PROJECTION. */
        public static final String[] INSERT_PROJECTION = { FIELD_SORT_ID,
                GROUP_ID, MODULE_FIELD_ID, MODULE_ID };

        /** The Constant DETAILS_PROJECTION. */
        public static final String[] DETAILS_PROJECTION = { RECORD_ID,
                FIELD_SORT_ID, GROUP_ID, MODULE_FIELD_ID, MODULE_ID };

        /** The Constant DEFAULT_SORT_ORDER. */
        public static final String DEFAULT_SORT_ORDER = FIELD_SORT_ID
                + SORTBY_ASC;
    }

    /**
     * The Interface AlarmColumns.
     */
    public interface AlarmColumns {

        /** The id. */
        String ID = Util.ROW_ID;

        /** The alarm state. */
        String ALARM_STATE = Util.PREF_ALARM_STATE;
    }
}
