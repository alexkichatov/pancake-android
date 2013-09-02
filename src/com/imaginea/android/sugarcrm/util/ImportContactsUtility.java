/*******************************************************************************
 * Copyright (c) 2013 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : ImportContactsUtility
 * Description : 
 *              The ImportContactsUtility Class
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import java.util.HashMap;
import java.util.Map;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ContactsColumns;

/**
 * The Class ImportContactsUtility.
 */
public class ImportContactsUtility {

    /** The contacts api names vs module field names. */
    private static Map<String, String> contactsApiNamesVsModuleFieldNames;

    static {
        contactsApiNamesVsModuleFieldNames = new HashMap<String, String>();
        contactsApiNamesVsModuleFieldNames.put(ModuleFields.FIRST_NAME,
                ContactsColumns.FIRST_NAME);
        contactsApiNamesVsModuleFieldNames.put(ModuleFields.LAST_NAME,
                ContactsColumns.LAST_NAME);
        contactsApiNamesVsModuleFieldNames.put(ModuleFields.EMAIL1,
                ContactsColumns.EMAIL1);
        contactsApiNamesVsModuleFieldNames.put(ModuleFields.PHONE_MOBILE,
                ContactsColumns.PHONE_MOBILE);
        contactsApiNamesVsModuleFieldNames.put(ModuleFields.PHONE_WORK,
                ContactsColumns.PHONE_WORK);
    }

    /**
     * Gets the module field name for contacts field.
     * 
     * @param contactsFieldName
     *            the contacts field name
     * @return the module field name for contacts field
     */
    public static String getModuleFieldNameForContactsField(
            String contactsFieldName) {
        String moduleFieldName;
        moduleFieldName = contactsApiNamesVsModuleFieldNames
                .get(contactsFieldName);
        return moduleFieldName;
    }
}
