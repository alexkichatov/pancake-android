package com.imaginea.android.sugarcrm.restapi;

import android.test.suitebuilder.annotation.SmallTest;

import com.imaginea.android.sugarcrm.rest.Rest;

// TODO: Auto-generated Javadoc
/**
 * The Class ServerInfoTest.
 */
public class ServerInfoTest extends RestAPITest {

    /**
     * Test get server info.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testGetServerInfo() throws Exception {
        final String serverVersion = Rest.getServerInfo(url);
        assertNotNull(serverVersion);
        assertEquals("6.4.3", serverVersion);
    }
}
