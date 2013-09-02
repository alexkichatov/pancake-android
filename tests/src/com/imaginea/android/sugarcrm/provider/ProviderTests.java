package com.imaginea.android.sugarcrm.provider;

import android.content.Context;
import android.test.ProviderTestCase2;

/**
 * Tests of the SugarCRM Provider.
 * 
 * You can run this entire test case with: runtest -c
 * com.imaginea.android.sugarcrm.provider.ProviderTests crm
 */
public class ProviderTests extends ProviderTestCase2<SugarCRMProvider> {

    /** The m provider. */
    SugarCRMProvider mProvider;

    /** The m mock context. */
    Context mMockContext;

    /**
     * Instantiates a new provider tests.
     */
    public ProviderTests() {
        super(SugarCRMProvider.class, SugarCRMProvider.AUTHORITY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.test.ProviderTestCase2#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mMockContext = getMockContext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.test.ProviderTestCase2#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test simple account save/retrieve.
     */
    public void testAccountSave() {

    }

    /**
     * Test contact save.
     */
    public void testContactSave() {

    }
}
