/*******************************************************************************
 * Copyright (c)
 *   {DATE} 27/08/2013
 *   {INITIAL COPYRIGHT OWNER} Asha , Muralidaran
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors::
 *                  Asha, Muralidaran- initial API and implementation and/or initial documentation
 *   Project Name : SugarCrm Pancake
 ******************************************************************************/

package com.imaginea.android.sugarcrm.sync;

/**
 * The Class SyncRecord.
 */
public class SyncRecord {

    /** The _id. */
    public long _id;

    /** The sync id. */
    public long syncId;

    /** The sync related id. */
    public long syncRelatedId;

    /** The sync command. */
    public int syncCommand;

    /** The module name. */
    public String moduleName;

    /** The related module name. */
    public String relatedModuleName;

    /** The status. */
    public int status;
}
