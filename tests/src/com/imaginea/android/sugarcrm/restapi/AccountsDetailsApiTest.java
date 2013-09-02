package com.imaginea.android.sugarcrm.restapi;

import static com.imaginea.android.sugarcrm.ModuleFields.NAME;

import java.util.HashMap;
import java.util.List;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.rest.SugarBean;

/**
 * The Class AccountsDetailsApiTest.
 */
public class AccountsDetailsApiTest extends RestAPITest {

    /** The module name. */
    String moduleName = "Accounts";

    /** The fields. */
    String[] fields = new String[] {};

    /** The select fields. */
    String[] selectFields = { NAME };

    /** The link name to fields array. */
    HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();

    /** The m link name to fields array. */
    HashMap<String, List<String>> mLinkNameToFieldsArray = new HashMap<String, List<String>>();

    /** The Constant LOG_TAG. */
    public final static String LOG_TAG = "ContactDetailsTest";

    /**
     * Test account detail.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testAccountDetail() throws Exception {
        // get only one sugar bean
        final SugarBean[] sBeans = getSugarBeans(0, 1);
        assertTrue(sBeans.length > 0);
        final String beanId = sBeans[0].getBeanId();
        assertNotNull(beanId);
        final SugarBean sBean = getSugarBean(beanId);
        assertNotNull(sBean);
        for (int i = 0; i < selectFields.length; i++) {
            final String fieldValue = sBean.getFieldValue(selectFields[i]);
            Log.i(LOG_TAG, "FieldName:|Field value " + selectFields[i] + ":"
                    + fieldValue);
            assertNotNull(fieldValue);
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
        final SugarBean[] sBeans = Rest.getEntryList(url, mSessionId,
                moduleName, query, orderBy, offset + "", selectFields,
                linkNameToFieldsArray, maxResults + "", "");
        return sBeans;
    }

    /**
     * demonstrates the usage of RestUtil for contact detail. ModuleFields.NAME
     * or FULL_NAME is not returned by Sugar CRM. The fields that are not
     * returned by SugarCRM can be automated, but not yet generated
     * 
     * @param beanId
     *            the bean id
     * @return the sugar bean
     * @throws Exception
     *             the exception
     */
    private SugarBean getSugarBean(final String beanId) throws Exception {
        final SugarBean sBean = Rest.getEntry(url, mSessionId, moduleName,
                beanId, selectFields, mLinkNameToFieldsArray);
        return sBean;
    }

}
