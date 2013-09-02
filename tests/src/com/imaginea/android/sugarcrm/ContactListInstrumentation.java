package com.imaginea.android.sugarcrm;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

/**
 * Instrumentation class for Contacts List launch performance testing.
 */
public class ContactListInstrumentation extends
        ActivityInstrumentationTestCase2<ModulesActivity> {

    /** The Constant LOG_TAG. */
    public static final String LOG_TAG = "ContactListInstrumentation";

    /**
     * Instantiates a new contact list instrumentation.
     */
    public ContactListInstrumentation() {
        super(ModulesActivity.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.test.ActivityInstrumentationTestCase2#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final ModulesActivity a = getActivity();
        // ensure a valid handle to the activity has been returned
        assertNotNull(a);

    }

    /**
     * The name 'test preconditions' is a convention to signal that if this test
     * doesn't pass, the test case was not set up properly and it might explain
     * any and all failures in other tests. This is not guaranteed to run before
     * other tests, as junit uses reflection to find the tests.
     */
    @MediumTest
    public void testPreconditions() {

    }
}
