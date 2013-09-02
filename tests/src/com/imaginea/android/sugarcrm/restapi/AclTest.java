package com.imaginea.android.sugarcrm.restapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLActions;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLRoles;
import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.rest.SugarBean;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * The Class AclTest.
 */
public class AclTest extends RestAPITest {

    /** The tag. */
    String TAG = AclTest.class.getSimpleName();

    /** The link name to fields array. */
    HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();

    /** The user select fields. */
    String[] userSelectFields = { ModuleFields.ID };

    /**
     * Test acl access.
     */
    @SmallTest
    public void testAclAccess() {
        try {
            final DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            final String moduleName = Util.USERS;
            linkNameToFieldsArray.put("aclroles",
                    Arrays.asList(ACLRoles.INSERT_PROJECTION));

            final HashMap<String, List<String>> linkNameToFieldsArrayForActions = new HashMap<String, List<String>>();
            linkNameToFieldsArrayForActions.put(
                    dbHelper.getLinkfieldName(Util.ACLACTIONS),
                    Arrays.asList(ACLActions.INSERT_PROJECTION));
            // TODO: get the user name from Account Manager
            // String userName = SugarCrmSettings.getUsername(getContext());

            // this gives the user bean for the logged in user along with the
            // acl roles associated
            final SugarBean[] userBeans = Rest.getEntryList(url, mSessionId,
                    moduleName, "Users.user_name='" + userName + "'", "", "",
                    userSelectFields, linkNameToFieldsArray, "", "");
            // userBeans always contains only one bean as we use getEntryList
            // with the logged in
            // user name as the query parameter
            for (final SugarBean userBean : userBeans) {
                // get the acl roles
                final SugarBean[] roleBeans = userBean
                        .getRelationshipBeans("aclroles");
                List<String> roleIds = new ArrayList<String>();
                if (roleBeans != null) {
                    // get the beanIds of the roles that are inserted
                    roleIds = dbHelper.insertRoles(roleBeans);

                    // get the acl actions for each roleId
                    for (final String roleId : roleIds) {
                        if (Log.isLoggable(TAG, Log.DEBUG)) {
                            Log.d(TAG, "roleId - " + roleId);
                        }
                        // get the aclRole along with the acl actions associated
                        final SugarBean roleBean = Rest.getEntry(url,
                                mSessionId, Util.ACLROLES, roleId,
                                ACLRoles.INSERT_PROJECTION,
                                linkNameToFieldsArrayForActions);
                        final SugarBean[] roleRelationBeans = roleBean
                                .getRelationshipBeans("actions");
                        if (roleRelationBeans != null) {
                            dbHelper.insertActions(roleId, roleRelationBeans);
                        }
                    }
                }

            }
        } catch (final SugarCrmException sce) {
            Log.e(TAG, "" + sce.getMessage());
        }
    }

}
