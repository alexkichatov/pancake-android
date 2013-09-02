package com.imaginea.android.sugarcrm.restapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.rest.SugarBean;
import com.imaginea.android.sugarcrm.util.LinkField;
import com.imaginea.android.sugarcrm.util.Module;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.RelationshipStatus;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * AccountssApiTest tests the REST API calls.
 */
public class AccountsApiTest extends RestAPITest {

    /** The module name. */
    String moduleName = Util.ACCOUNTS;

    /** The fields. */
    String[] fields = new String[] {};

    /** The select fields. */
    String[] selectFields = { ModuleFields.NAME, ModuleFields.PARENT_NAME,
            ModuleFields.PHONE_OFFICE, ModuleFields.PHONE_FAX,
            ModuleFields.EMAIL1, ModuleFields.DELETED };

    /** The link name to fields array. */
    HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();

    /** The Constant LOG_TAG. */
    public final static String LOG_TAG = AccountsApiTest.class.getSimpleName();

    /**
     * Test get accounts list.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testGetAccountsList() throws Exception {
        final int offset = 15;
        final int maxResults = 2;
        // get the sugar beans
        final SugarBean[] sBeans = getSugarBeans(offset, maxResults);
        assertTrue(sBeans.length > 0);

        // retrieve the bean info
        for (final SugarBean sBean : sBeans) {
            Log.d(LOG_TAG, sBean.getBeanId());
            Log.d(LOG_TAG, sBean.getFieldValue(ModuleFields.NAME));
            assertNotNull(sBean.getBeanId());
            assertNotNull(sBean.getFieldValue(ModuleFields.NAME));

            // get the related beans
            final DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            final String linkFieldName = dbHelper
                    .getLinkfieldName(Util.CONTACTS);
            final SugarBean[] relationshipBeans = sBean
                    .getRelationshipBeans(linkFieldName);
            if (relationshipBeans != null) {
                // retrieve the related bean info
                for (final SugarBean relationshipBean : relationshipBeans) {
                    Log.d(LOG_TAG,
                            ""
                                    + relationshipBean
                                            .getFieldValue(ModuleFields.FIRST_NAME));
                    assertNotNull(relationshipBean
                            .getFieldValue(ModuleFields.FIRST_NAME));
                }
            }
        }
    }

    /**
     * Test entire get accounts list.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testEntireGetAccountsList() throws Exception {
        int offset = 0;
        final int maxResults = 10;
        // get sugar beans
        SugarBean[] sBeans = getSugarBeans(offset, maxResults);
        assertNotNull(sBeans);
        assertTrue(sBeans.length > 0);

        int totalRuns = 1;
        while (sBeans.length > totalRuns) {
            offset += 10; // update the offset as we fetch the beans
            // get sugar beans based on the offset
            sBeans = getSugarBeans(offset, maxResults);
            assertNotNull(sBeans);
            totalRuns++;
        }
        Log.d(LOG_TAG, "Total Runs:" + totalRuns);
    }

    /**
     * Test get entry.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testGetEntry() throws Exception {
        // get only one sugar bean
        final int offset = 0;
        final int maxResults = 1;
        final SugarBean[] sBeans = getSugarBeans(offset, maxResults);
        assertTrue(sBeans.length > 0);
        assertNotNull(sBeans[0]);
        final String beanId = sBeans[0].getBeanId();
        assertNotNull(sBeans[0].getBeanId());

        final DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        final String linkFieldName = dbHelper.getLinkfieldName(Util.CONTACTS);
        linkNameToFieldsArray.put(linkFieldName,
                Arrays.asList(new String[] { "first_name", "last_name" }));

        // use the bean id obtained from one of the beans in getEntryList API
        // call's response with the getEntry API call to fetch a bean with id.
        final SugarBean sBean = Rest.getEntry(url, mSessionId, moduleName,
                beanId, selectFields, linkNameToFieldsArray);
        assertNotNull(sBean);

        Log.d(LOG_TAG, "Account Name : " + sBean.getFieldValue("name"));
        Log.d(LOG_TAG, "Account email : " + sBean.getFieldValue("email1"));
        Log.d(LOG_TAG,
                "Phone office : "
                        + sBean.getFieldValue(ModuleFields.PHONE_OFFICE));
        Log.d(LOG_TAG,
                "Account deleted ? "
                        + sBean.getFieldValue(ModuleFields.DELETED));
    }

    /**
     * Test get entries count.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testGetEntriesCount() throws Exception {
        final String query = "";
        final int deleted = 0;
        // search the entries in the module for the keyword
        final int entriesCount = Rest.getEntriesCount(url, mSessionId,
                moduleName, query, deleted + "");
        Log.d(LOG_TAG, "entriesCount = " + entriesCount);
        assertTrue(entriesCount > 0);
    }

    /**
     * Test set entry.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testSetEntry() throws Exception {
        // create a sugar bean
        Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Advertising Inc."); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        final String beanId = Rest.setEntry(url, mSessionId, moduleName,
                nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + beanId);
        assertNotNull(beanId);

        // modify the newly created bean
        nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.ID, beanId);
        nameValuePairs.put(ModuleFields.NAME, "R R Advertising Inc."); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        final String _beanId = Rest.setEntry(url, mSessionId, moduleName,
                nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + _beanId);
        assertNotNull(_beanId);
        // if update is successful we get the same beanId returned
        assertEquals(beanId, _beanId);
    }

    /**
     * Test set entries.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testSetEntries() throws Exception {
        final List<Map<String, String>> beanNameValuePairs = new ArrayList<Map<String, String>>();

        // create a sugar bean
        final Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Advertising Inc."); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        final String beanId = Rest.setEntry(url, mSessionId, moduleName,
                nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + beanId);
        assertNotNull(beanId);

        beanNameValuePairs.add(nameValuePairs);
        final List<String> beanIds = Rest.setEntries(url, mSessionId,
                moduleName, beanNameValuePairs);
        for (final String _beanId : beanIds) {
            Log.d(LOG_TAG, _beanId);
            assertNotNull(_beanId);
        }
    }

    /**
     * Test delete entry.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testDeleteEntry() throws Exception {
        // create a new bean
        Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Advertising Inc."); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        final String beanId = Rest.setEntry(url, mSessionId, moduleName,
                nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + beanId);
        assertNotNull(beanId);

        // delete the newly created bean
        nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.ID, beanId);
        nameValuePairs.put(ModuleFields.DELETED, "1"); //
        final String _beanId = Rest.setEntry(url, mSessionId, moduleName,
                nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + _beanId);
        assertNotNull(_beanId);
        // if deletion is successful we get the same beanId returned
        assertEquals(beanId, _beanId);
    }

    /**
     * Test get relationships.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testGetRelationships() throws Exception {
        // get only one sugar bean
        final int offset = 0;
        final int maxResults = 1;
        final SugarBean[] sBeans = getSugarBeans(offset, maxResults);
        assertTrue(sBeans.length > 0);

        final String beanId = sBeans[0].getBeanId();
        final DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        final String linkFieldName = dbHelper.getLinkfieldName(Util.CONTACTS);
        final String relatedModuleQuery = "";
        final String[] relatedFields = { "id", "first_name", "account_id" };
        final Map<String, List<String>> relatedModuleLinkNameToFieldsArray = new HashMap<String, List<String>>();
        relatedModuleLinkNameToFieldsArray
                .put(linkFieldName,
                        Arrays.asList(new String[] { "id", "first_name",
                                "last_name" }));
        final int deleted = 0;

        // get the related beans using the bean id obtained from one of the
        // beans from the
        // getEntryList API call
        final SugarBean[] relationBeans = Rest.getRelationships(url,
                mSessionId, moduleName, beanId, linkFieldName,
                relatedModuleQuery, relatedFields,
                relatedModuleLinkNameToFieldsArray, deleted + "");
        assertNotNull(relationBeans);
        for (final SugarBean sBean : relationBeans) {
            Log.i("LOG_TAG", "BeanId - " + sBean.getBeanId());
            assertNotNull(sBean);
        }
    }

    /**
     * Test set relationship.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testSetRelationship() throws Exception {
        // create a new bean : Account
        Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Advertising Inc."); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        final String accountBeanId = Rest.setEntry(url, mSessionId, moduleName,
                nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + accountBeanId);
        assertNotNull(accountBeanId);

        // create a new bean : Contact
        nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Contact"); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        final String contactBeanId = Rest.setEntry(url, mSessionId,
                Util.CONTACTS, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + contactBeanId);
        assertNotNull(contactBeanId);

        final DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        final String linkFieldName = dbHelper.getLinkfieldName(Util.CONTACTS);
        final String[] relatedIds = { contactBeanId };
        final Map<String, String> nameValueList = new LinkedHashMap<String, String>();
        nameValueList.put(ModuleFields.ID, relatedIds[0]);
        nameValueList.put(ModuleFields.FIRST_NAME, "Nekkanti");
        nameValueList.put(ModuleFields.LAST_NAME, "Vasu");
        nameValueList.put(ModuleFields.EMAIL1, "vasu1705@gmail.com");

        final int delete = 0;
        // use the beans created above to set one bean as a related bean for the
        // other
        final RelationshipStatus response = Rest.setRelationship(url,
                mSessionId, moduleName, contactBeanId, linkFieldName,
                relatedIds, null, delete + "");
        Log.d(LOG_TAG, "setRelationship : " + response.getCreatedCount() + "-"
                + response.getFailedCount() + "-" + response.getDeletedCount());
        assertEquals(response.getCreatedCount(), 1);
    }

    /**
     * Test set relationships.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testSetRelationships() throws Exception {
        // create a new bean : Account
        Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Account"); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        final String accountBeanId = Rest.setEntry(url, mSessionId, moduleName,
                nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + accountBeanId);
        assertNotNull(accountBeanId);

        // create a new bean : Contact
        nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put("last_name", "Test Contact"); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        final String contactBeanId = Rest.setEntry(url, mSessionId,
                Util.CONTACTS, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + contactBeanId);
        assertNotNull(contactBeanId);

        final List<Map<String, String>> nameValueLists = new ArrayList<Map<String, String>>();

        final DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        final String linkFieldName = dbHelper.getLinkfieldName(Util.CONTACTS);

        final String[] moduleNames = { Util.ACCOUNTS };
        final String[] beanIds = { accountBeanId };
        final String[] linkFieldNames = { linkFieldName };
        final String[] relatedIds = { contactBeanId };

        final Map<String, String> nameValueList = new LinkedHashMap<String, String>();
        nameValueList.put(ModuleFields.ID, beanIds[0]);

        final int deleted = 0;

        nameValueLists.add(nameValueList);
        final RelationshipStatus response = Rest.setRelationships(url,
                mSessionId, moduleNames, beanIds, linkFieldNames, relatedIds,
                nameValueLists, deleted + "");

        assertTrue(response.getCreatedCount() >= 1);
    }

    /**
     * Test get entry list with no select fields.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testGetEntryListWithNoSelectFields() throws Exception {
        final String query = "", orderBy = "";
        final String offset = 0 + "", maxResults = 5 + "", deleted = 0 + "";

        final SugarBean[] sBeans = Rest.getEntryList(url, mSessionId,
                moduleName, query, orderBy, offset, selectFields,
                linkNameToFieldsArray, maxResults, deleted);
        assertTrue(sBeans.length > 0);
        for (final SugarBean sBean : sBeans) {
            Log.d(LOG_TAG, sBean.getBeanId());
            assertNotNull(sBean);
        }
    }

    /**
     * Test get module fields.
     * 
     * @throws Exception
     *             the exception
     */
    public void testGetModuleFields() throws Exception {
        // obtain the modules fields of the module
        final Module module = Rest.getModuleFields(url, mSessionId, moduleName,
                new String[] {});
        assertNotNull(module);
        final List<ModuleField> moduleFields = module.getModuleFields();
        assertNotNull(moduleFields);
        assertTrue(moduleFields.size() > 0);

        // get the module fields
        for (final ModuleField moduleField : moduleFields) {
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "name :" + moduleField.getName());
                Log.d(LOG_TAG, "label :" + moduleField.getLabel());
                Log.d(LOG_TAG, "type :" + moduleField.getType());
                Log.d(LOG_TAG, "isReuired :" + moduleField.isRequired());
            }
            assertNotNull(moduleField);
            assertNotNull(moduleField.getName());
        }

        // get the linked module fields
        final List<LinkField> linkFields = module.getLinkFields();
        assertNotNull(linkFields);
        for (final LinkField linkField : linkFields) {
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "name :" + linkField.getName());
                Log.d(LOG_TAG, "type :" + linkField.getType());
                Log.d(LOG_TAG, "relationship :" + linkField.getRelationship());
                Log.d(LOG_TAG, "module :" + linkField.getModule());
                Log.d(LOG_TAG, "beanName :" + linkField.getBeanName());
            }
            assertNotNull(linkField);
        }
    }

    /**
     * Test search by module.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testSearchByModule() throws Exception {
        final String searchString = "beans.the.vegan";
        final String[] modules = { Util.ACCOUNTS };
        final int offset = 0;
        final int maxResults = 20;
        final Map<String, SugarBean[]> searchResults = Rest
                .searchByModule(url, mSessionId, searchString, modules, offset
                        + "", maxResults + "");
        assertNotNull(searchResults);
        for (final Entry<String, SugarBean[]> entry : searchResults.entrySet()) {
            Log.d(LOG_TAG, "Module Name : " + entry.getKey());
            final SugarBean[] sugarBeans = entry.getValue();
            for (int i = 0; i < sugarBeans.length; i++) {
                if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                    Log.d(LOG_TAG,
                            "ID : "
                                    + sugarBeans[i]
                                            .getFieldValue(ModuleFields.ID));
                    Log.d(LOG_TAG,
                            "NAME : "
                                    + sugarBeans[i]
                                            .getFieldValue(ModuleFields.NAME));
                    Log.d(LOG_TAG,
                            "Billing address city : "
                                    + sugarBeans[i]
                                            .getFieldValue(ModuleFields.BILLING_ADDRESS_CITY));
                    Log.d(LOG_TAG,
                            "Phone office : "
                                    + sugarBeans[i]
                                            .getFieldValue(ModuleFields.PHONE_OFFICE));
                    Log.d(LOG_TAG,
                            "Assigned user name : "
                                    + sugarBeans[i]
                                            .getFieldValue(ModuleFields.ASSIGNED_USER_NAME));
                }
            }
        }
    }

    /**
     * demonstrates the usage of RestUtil for contacts List. ModuleFields.NAME
     * or FULL_NAME is not returned by Sugar CRM. The fields that are not
     * returned by SugarCRM can be automated, but not yet generated
     * 
     * @param offset
     *            the offset
     * @param maxResults
     *            the max results
     * @return the sugar beans
     * @throws Exception
     *             the exception
     */
    private SugarBean[] getSugarBeans(final int offset, final int maxResults)
            throws Exception {
        final String query = "", orderBy = "";
        // int deleted = 0;
        final DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        String linkFieldName = dbHelper.getLinkfieldName(Util.CONTACTS);
        linkNameToFieldsArray
                .put(linkFieldName,
                        Arrays.asList(new String[] { "id", "first_name",
                                "last_name" }));
        linkFieldName = dbHelper.getLinkfieldName(Util.OPPORTUNITIES);
        linkNameToFieldsArray.put(linkFieldName,
                Arrays.asList(new String[] { "id", "name" }));
        linkFieldName = dbHelper.getLinkfieldName(Util.LEADS);
        linkNameToFieldsArray.put(linkFieldName,
                Arrays.asList(new String[] { "id" }));

        final SugarBean[] sBeans = Rest.getEntryList(url, mSessionId,
                moduleName, query, orderBy, offset + "", selectFields,
                linkNameToFieldsArray, maxResults + "", "");
        return sBeans;
    }
}
