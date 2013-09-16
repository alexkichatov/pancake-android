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
    public long mid;

    /** The m sync id. */
    public long mSsyncId;

    /** The m sync related id. */
    public long mSyncRelatedId;

    /** The m sync command. */
    public int mSyncCommand;

    /** The m module name. */
    public String mModuleName;

    /** The m related module name. */
    public String mRelatedModuleName;

    /** The m status. */
    public int mStatus;
}
