/*******************************************************************************
 * Copyright (c) 2013 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : RelationshipStatus
 * Description : 
 *              Helper for set_relationships, set_relationship methods in the REST calls, 
 * give the number of relationships created, number of relationships failed and 
 * number of relationships deleted. This class can be used to get that status.
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

/**
 * The Class RelationshipStatus.
 */
public class RelationshipStatus {

    /** The m created count. */
    private final int mCreatedCount;

    /** The m failed count. */
    private final int mFailedCount;

    /** The m deleted count. */
    private final int mDeletedCount;

    /**
     * Instantiates a new relationship status.
     * 
     * @param createdCount
     *            the created count
     * @param failedCount
     *            the failed count
     * @param deletedCount
     *            the deleted count
     */
    public RelationshipStatus(int createdCount, int failedCount,
            int deletedCount) {
        mCreatedCount = createdCount;
        mFailedCount = failedCount;
        mDeletedCount = deletedCount;
    }

    /**
     * Gets the created count.
     * 
     * @return the created count
     */
    public int getCreatedCount() {
        return mCreatedCount;
    }

    /**
     * Gets the failed count.
     * 
     * @return the failed count
     */
    public int getFailedCount() {
        return mFailedCount;
    }

    /**
     * Gets the deleted count.
     * 
     * @return the deleted count
     */
    public int getDeletedCount() {
        return mDeletedCount;
    }

}
