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

public class UsersTest extends RestAPITest {

    String moduleName = Util.USERS;

    HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();

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

        // this is not a test, so commenting for now, till a set of users can be
        // cleanly inserted
        // into the system and the result asserted
        // DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        // dbHelper.insertUsers(usersMap);
    }

    private Map<String, String> getUserBeanValues(final SugarBean userBean) {
        final Map<String, String> userBeanValues = new TreeMap<String, String>();
        for (final String fieldName : Users.INSERT_PROJECTION) {
            final String fieldValue = userBean.getFieldValue(fieldName);
            userBeanValues.put(fieldName, fieldValue);
        }

        return userBeanValues;
    }

    private SugarBean[] getUsers() throws Exception {
        return Rest.getEntryList(url, mSessionId, moduleName, null, null, "0",
                Users.INSERT_PROJECTION, linkNameToFieldsArray, null, "0");
    }
}
