package com.imaginea.android.sugarcrm.restapi;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.imaginea.android.sugarcrm.provider.DatabaseHelper;

/**
 * The Class ModuleFieldsOrderInsertTest.
 */
public class ModuleFieldsOrderInsertTest extends AndroidTestCase {

    /*
     * Trying to insert the module fields sort oder and grouping. Open the
     * SQL_FILE, i.e. 'sortOrderAndGroup.sql' which has to be there in the
     * assets folder to read each SQL insert statement and execute
     */
    /**
     * Test sort order insertion.
     * 
     * @throws Exception
     *             the exception
     */
    @SmallTest
    public void testSortOrderInsertion() throws Exception {

        new DatabaseHelper(getContext());

    }
}
