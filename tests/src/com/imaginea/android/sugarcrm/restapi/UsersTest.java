package com.imaginea.android.sugarcrm.restapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.test.suitebuilder.annotation.SmallTest;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Users;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.rest.SugarBean;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * The Class UsersTest.
 */
public class UsersTest extends RestAPITest {

    /** The module name. */
    String moduleName = Util.USERS;

    /** The link name to fields array. */
    HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();

    /**
     * Test users insertion.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testUsersInsertion() throws Exception {
        final SugarBean[] userBeans = getUsers();
        assertNotNull(userBeans);
        assertTrue(userBeans.length > 0);

        final Map<String, Map<String, String>> usersMap = new TreeMap<String, Map<String, String>>();
        for (final SugarBean userBean : userBeans) {
            assertNotNull(userBean);
            final Map<String, String> userBeanValues = getUserBeanValues(userBean);
            assertNotNull(userBeanValues);
            final String userName = userBean
                    .getFieldValue(ModuleFields.USER_NAME);
            assertNotNull(userName);
            if (userBeanValues != null & userBeanValues.size() > 0) {
                usersMap.put(userName, userBeanValues);
            }
        }

    }

    /**
     * Gets the user bean values.
     * 
     * @param userBean
     *            the user bean
     * @return the user bean values
     */
    private Map<String, String> getUserBeanValues(final SugarBean userBean) {
        final Map<String, String> userBeanValues = new TreeMap<String, String>();
        for (final String fieldName : Users.INSERT_PROJECTION) {
            final String fieldValue = userBean.getFieldValue(fieldName);
            userBeanValues.put(fieldName, fieldValue);
        }

        return userBeanValues;
    }

    /**
     * Gets the users.
     * 
     * @return the users
     * @throws Exception
     *             the exception
     */
    private SugarBean[] getUsers() throws Exception {
        return Rest.getEntryList(url, mSessionId, moduleName, null, null, "0",
                Users.INSERT_PROJECTION, linkNameToFieldsArray, null, "0");
    }
}
