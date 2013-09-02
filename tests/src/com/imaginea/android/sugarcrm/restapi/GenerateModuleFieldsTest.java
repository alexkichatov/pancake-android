package com.imaginea.android.sugarcrm.restapi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import android.os.Environment;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.imaginea.android.sugarcrm.rest.Rest;
import com.imaginea.android.sugarcrm.util.ModuleField;

/**
 * Do not run this, exclude this from the test suite.
 * 
 * @author chander
 */
public class GenerateModuleFieldsTest extends RestAPITest {

    /** The module name. */
    String moduleName = "Accounts";

    /** The fields. */
    String[] fields = new String[] {};

    /** The custom fields. */
    String[] customFields = new String[] { "a", "b" };

    /** The module fields set. */
    LinkedHashSet<String> moduleFieldsSet = new LinkedHashSet<String>();

    /** The module field list. */
    List<String> moduleFieldList = new ArrayList<String>();

    /** The Constant LOG_TAG. */
    public final static String LOG_TAG = "ModuleFieldTest";

    /**
     * the values are stored in a linked hashset so order is preserved. Add new
     * modules at the end, so you know the new elements for which constants have
     * to be created
     * 
     * @throws Exception
     *             the exception
     */
    @LargeTest
    public void testGetAllModuleFields() throws Exception {

        List<ModuleField> moduleFields = Rest.getModuleFields(url, mSessionId,
                "Accounts", fields).getModuleFields();
        assertNotNull(moduleFields);
        addToModuleFieldList(moduleFields);

        moduleFields = Rest
                .getModuleFields(url, mSessionId, "Contacts", fields)
                .getModuleFields();
        addToModuleFieldList(moduleFields);
        moduleFields = Rest.getModuleFields(url, mSessionId, "Opportunities",
                fields).getModuleFields();
        addToModuleFieldList(moduleFields);
        moduleFields = Rest.getModuleFields(url, mSessionId, "Leads", fields)
                .getModuleFields();
        addToModuleFieldList(moduleFields);
        moduleFields = Rest.getModuleFields(url, mSessionId, "Campaigns",
                fields).getModuleFields();
        addToModuleFieldList(moduleFields);
        moduleFields = Rest
                .getModuleFields(url, mSessionId, "Meetings", fields)
                .getModuleFields();
        addToModuleFieldList(moduleFields);

        moduleFields = Rest.getModuleFields(url, mSessionId, "Cases", fields)
                .getModuleFields();
        addToModuleFieldList(moduleFields);

        moduleFields = Rest.getModuleFields(url, mSessionId, "Calls", fields)
                .getModuleFields();
        addToModuleFieldList(moduleFields);

        moduleFields = Rest
                .getModuleFields(url, mSessionId, "ACLRoles", fields)
                .getModuleFields();
        addToModuleFieldList(moduleFields);

        moduleFields = Rest.getModuleFields(url, mSessionId, "ACLActions",
                fields).getModuleFields();
        addToModuleFieldList(moduleFields);

        moduleFields = Rest.getModuleFields(url, mSessionId, "Users", fields)
                .getModuleFields();
        addToModuleFieldList(moduleFields);

        for (final Iterator iterator = moduleFields.iterator(); iterator
                .hasNext();) {
            final ModuleField field = (ModuleField) iterator.next();
            Log.i("ModuleFields:", field.getName());
        }
        // generateClass(moduleFieldsSet);
        // Log.i("ModuleFields:"+ moduleName.)
    }

    /**
     * Adds the to module field list.
     * 
     * @param moduleFields
     *            the module fields
     */
    private void addToModuleFieldList(final List<ModuleField> moduleFields) {
        for (final ModuleField moduleField : moduleFields) {
            moduleFieldsSet.add(moduleField.getName());
        }
    }

    /**
     * This class is a generate Class file using the Velocity Template Engine.
     * 
     * @param set
     *            the set
     * @throws Exception
     *             the exception
     */

    public void generateClass(final Set set) throws Exception {
        /* first, we init the runtime engine. Defaults are not fine in android. */

        try {
            final Properties prop = new Properties();
            prop.put("runtime.log.logsystem.class",
                    "org.apache.velocity.runtime.log.NullLogSystem");
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Problem initializing Velocity : " + e);
            return;
        }

        StringWriter w = new StringWriter();
        try {
            final InputStream is = super.getContext().getAssets()
                    .open("classFile.vm");

            final int size = is.available();

            // Read the entire asset into a local byte buffer.
            final byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            new String(buffer);
        } catch (final IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }

        w = new StringWriter();

        try {
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Exception : " + e);
        }

        Log.d(LOG_TAG, " string : " + w);
        final File moduleFieldsClass = new File(
                Environment.getExternalStorageDirectory(), "ModuleFields.java");
        final FileWriter fw = new FileWriter(moduleFieldsClass);
        fw.write(w.toString());
        fw.close();
    }
}
