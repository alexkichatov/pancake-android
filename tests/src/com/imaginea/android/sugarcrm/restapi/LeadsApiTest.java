package com.imaginea.android.sugarcrm.restapi;

import java.util.HashMap;
import java.util.List;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.rest.SugarBean;
import com.imaginea.android.sugarcrm.util.Module;
import com.imaginea.android.sugarcrm.util.ModuleField;

/**
 * LeadsApiTest, tests the rest api calls.
 * 
 * @author chander
 */
public class LeadsApiTest extends RestAPITest {

    /** The module name. */
    String moduleName = "Leads";

    /** The fields. */
    String[] fields = new String[] {};

    /** The custom fields. */
    String[] customFields = new String[] { "a", "b" };

    /** The select fields. */
    String[] selectFields = { ModuleFields.FIRST_NAME, ModuleFields.LAST_NAME,
            ModuleFields.EMAIL1 };

    /** The link name to fields array. */
    HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();

    /** The order by. */
    String query = "", orderBy = "";

    /** The deleted. */
    int deleted = 0;

    /** The Constant LOG_TAG. */
    public final static String LOG_TAG = "LeadsApiTest";

    /**
     * Test get all module fields.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testGetAllModuleFields() throws Exception {

        final Module module = Rest.getModuleFields(url, mSessionId, moduleName,
                fields);
        assertNotNull(module);

        final List<ModuleField> moduleFields = module.getModuleFields();
        assertNotNull(moduleFields);
        assertTrue(moduleFields.size() > 0);

        for (final ModuleField moduleField : moduleFields) {
            Log.d(LOG_TAG, "name :" + moduleField.getName());
            Log.d(LOG_TAG, "label :" + moduleField.getLabel());
            Log.d(LOG_TAG, "type :" + moduleField.getType());
            Log.d(LOG_TAG, "isReuired :" + moduleField.isRequired());
            assertNotNull(moduleField);
            assertNotNull(moduleField.getName());
        }
    }

    /**
     * Test leads list.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testLeadsList() throws Exception {
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
     * demonstrates the usage of RestUtil for leads List. ModuleFields.NAME or
     * FULL_NAME is not returned by Sugar CRM. The fields that are not returned
     * by SugarCRM can be automated, but not yet generated
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

        final SugarBean[] sBeans = Rest.getEntryList(url, mSessionId,
                moduleName, query, orderBy, offset + "", selectFields,
                linkNameToFieldsArray, maxResults + "", deleted + "");
        return sBeans;
    }

}
