package com.imaginea.android.sugarcrm.restapi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.rest.SugarBean;

/**
 * The Class ContactsApiTest.
 */
public class ContactsApiTest extends RestAPITest {

    /** The module name. */
    String moduleName = "Contacts";

    /** The fields. */
    String[] fields = new String[] {};

    /** The custom fields. */
    String[] customFields = new String[] { "a", "b" };

    /** The select fields. */
    String[] selectFields = { ModuleFields.FIRST_NAME, ModuleFields.LAST_NAME,
            ModuleFields.EMAIL1 };

    /** The link name to fields array. */
    HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();

    /** The Constant LOG_TAG. */
    public final static String LOG_TAG = "ContactsApiTest";

    /**
     * Test get all module fields.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testGetAllModuleFields() throws Exception {
        Rest.getModuleFields(url, mSessionId, moduleName, fields);
    }

    /**
     * Test get custom module fields.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testGetCustomModuleFields() throws Exception {
        Rest.getModuleFields(url, mSessionId, moduleName, customFields);
    }

    /**
     * Test entries by date.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testEntriesByDate() throws Exception {
        int offset = 0;
        final int maxResults = 20;
        SugarBean[] sBeans = getSugarBeansFilterByDate(offset, maxResults);
        assertNotNull(sBeans);
        assertTrue(sBeans.length > 0);

        int totalRuns = 1;
        while (sBeans.length > 0) {
            offset += 20;
            sBeans = getSugarBeans(offset, maxResults);
            assertNotNull(sBeans);
            totalRuns++;
        }
        Log.d(LOG_TAG, "Total Runs:" + totalRuns);
    }

    /**
     * Test contacts list.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testContactsList() throws Exception {
        final int offset = 0;
        final int maxResults = 10;
        // String[] selectFields = new String[] {};
        final SugarBean[] sBeans = getSugarBeans(offset, maxResults);
        assertNotNull(sBeans);
        assertTrue(sBeans.length > 0);

        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            for (final SugarBean sBean : sBeans) {
                assertNotNull(sBean);
                Log.d(LOG_TAG, sBean.getBeanId());
                Log.d(LOG_TAG, sBean.getFieldValue(ModuleFields.EMAIL1));
            }
        }
    }

    /**
     * Test entire contact list.
     * 
     * @throws Exception
     *             the exception
     */
    @LargeTest
    public void testEntireContactList() throws Exception {
        int offset = 0;
        final int maxResults = 20;
        // String[] selectFields = new String[] {};
        SugarBean[] sBeans = getSugarBeans(offset, maxResults);
        assertNotNull(sBeans);
        assertTrue(sBeans.length > 0);

        int totalRuns = 1;
        while (sBeans.length > 0) {
            offset += 20;
            sBeans = getSugarBeans(offset, maxResults);
            assertNotNull(sBeans);
            totalRuns++;
        }
        Log.d(LOG_TAG, "Total Runs:" + totalRuns);
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

        final int deleted = 0;

        final SugarBean[] sBeans = Rest.getEntryList(url, mSessionId,
                moduleName, query, orderBy, offset + "", selectFields,
                linkNameToFieldsArray, maxResults + "", deleted + "");
        return sBeans;
    }

    /**
     * Gets the sugar beans filter by date.
     * 
     * @param offset
     *            the offset
     * @param maxResults
     *            the max results
     * @return the sugar beans filter by date
     * @throws Exception
     *             the exception
     */
    private SugarBean[] getSugarBeansFilterByDate(final int offset,
            final int maxResults) throws Exception {
        final DateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        final java.util.Date date = new java.util.Date();
        date.setDate(date.getDate() - 1);

        final String query = moduleName + "." + ModuleFields.DATE_MODIFIED
                + ">'" + dateFormat.format(date) + "'";
        final String orderBy = "";
        final int deleted = 0;

        final SugarBean[] sBeans = Rest.getEntryList(url, mSessionId,
                moduleName, query, orderBy, offset + "", selectFields,
                linkNameToFieldsArray, maxResults + "", deleted + "");
        return sBeans;
    }

}
