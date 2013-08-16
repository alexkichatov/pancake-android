package com.imaginea.android.sugarcrm.restapi;

import android.test.suitebuilder.annotation.SmallTest;

import com.imaginea.android.sugarcrm.rest.Rest;

public class ServerInfoTest extends RestAPITest {

    @SmallTest
    public void testGetServerInfo() throws Exception {
        final String serverVersion = Rest.getServerInfo(url);
        assertNotNull(serverVersion);
        assertEquals("6.3.0", serverVersion);
    }
}
