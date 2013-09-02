package com.imaginea.android.sugarcrm.restapi;

import junit.framework.TestCase;
import android.accounts.AccountManager;
import android.content.Context;
import android.test.AndroidTestCase;

import com.imaginea.android.sugarcrm.rest.Rest;

/**
 * 
 * Extend this class for module specific unit tests.
 * 
 * Since this test doesn't need a {@link android.content.Context}, or any other
 * dependencies injected, it simply extends the standard {@link TestCase}.
 * 
 * See {@link com.imaginea.android.sugarcrm.AllTests} for documentation on
 * running all tests and individual tests in this application.
 */
public class RestAPITest extends AndroidTestCase {

    /** The m account manager. */
    protected AccountManager mAccountManager;

    /** The m target context. */
    protected Context mTargetContext;

    /** The m account. */
    protected String mAccount;

    /** used by module specific unit tests. */
    protected String url = "http://50.16.5.141/html/sugarcrm/service/v2/rest.php";

    /** The user name. */
    protected String userName = "will";

    /** The password. */
    protected String password = "will";

    /** The m session id. */
    protected String mSessionId;

    /*
     * (non-Javadoc)
     * 
     * @see android.test.AndroidTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

        super.setUp();

        authenticate();
    }

    /**
     * Server Authenticate.
     * 
     * @throws Exception
     *             the exception
     */
    public void authenticate() throws Exception {

        mSessionId = Rest.loginToSugarCRM(url, userName, password);
        assertNotNull(mSessionId);
    }
}
