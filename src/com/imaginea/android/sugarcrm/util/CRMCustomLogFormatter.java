/*******************************************************************************
 * Copyright (c) 2013 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : CRMCustomLogFormatter
 * Description : 
 *              The CCRMCustomLogFormatter formats the LogRecord as follows: date level localized
 * message with parameters
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * CRMCustomLogFormatter formats the LogRecord as follows: date level localized
 * message with parameters.
 */
public class CRMCustomLogFormatter extends Formatter {

    /** The m date format. */
    DateFormat mDateFormat;

    /**
     * Instantiates a new cRM custom log formatter.
     */
    public CRMCustomLogFormatter() {
        super();
        mDateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                DateFormat.SHORT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
     */
    @Override
    public String format(LogRecord record) {

        // Create a StringBuffer to contain the formatted record
        // start with the date.
        final StringBuffer sb = new StringBuffer();

        // Get the date from the LogRecord and add it to the buffer
        final Date date = new Date(record.getMillis());
        sb.append(mDateFormat.format(date.getTime()));
        sb.append(" ");

        // Get the level name and add it to the buffer
        sb.append(record.getLevel().getName());
        sb.append(" ");

        // Get the formatted message (includes localization
        // and substitution of paramters) and add it to the buffer
        sb.append(formatMessage(record));
        sb.append("\n");

        return sb.toString();
    }
}
