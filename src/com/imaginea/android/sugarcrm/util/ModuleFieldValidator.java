/*******************************************************************************
 * Copyright (c) 2013 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:  chander - initial API and implementation
 * Project Name : SugarCrm Pancake
 * FileName : ModuleFieldValidator
 * Description : 
 *              Validates the various module fields while creating or
 * updating
 ******************************************************************************/

package com.imaginea.android.sugarcrm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class ModuleFieldValidator.
 */
public class ModuleFieldValidator {

    /**
     * isEmailValid: Validate email address using Java reg ex. This method
     * checks if the input string is a valid email address.
     * 
     * @param email
     *            Email address to validate
     * @return boolean: true if email address is valid, false otherwise.
     */
    public static boolean isEmailValid(final String email) {
        boolean isValid = false;
        /*
         * Email format: A valid email address will have following format:
         * [\\w\\.-]+: Begins with word characters, (may include periods and
         * hypens).
         * 
         * @: It must have a '@' symbol after initial characters.
         * ([\\w\\-]+\\.)+: '@' must follow by more alphanumeric characters (may
         * include hypens.). This part must also have a "." to separate domain
         * and subdomain names. [A-Z]{2,4}$ : Must end with two to four
         * alaphabets. (This will allow domain names with 2, 3 and 4 characters
         * e.g pa, com, net, wxyz)
         * 
         * Examples: Following email addresses will pass validation abc@xyz.net
         */

        // Initialize reg ex for email.
        final String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        final CharSequence inputStr = email;
        // Make the comparison case-insensitive.
        final Pattern pattern = Pattern.compile(expression,
                Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * isPhoneNumberValid: Validate phone number using Java reg ex. This method
     * checks if the input string is a valid phone number.
     * 
     * @param phoneNumber
     *            Phone number to validate
     * @return boolean: true if phone number is valid, false otherwise.
     */
    public static boolean isPhoneNumberValid(final String phoneNumber) {
        boolean isValid = false;
        /*
         * Phone Number formats: (nnn)nnn-nnnn; nnnnnnnnnn; nnn-nnn-nnnn ^\\(? :
         * May start with an option "(" . (\\d{3}): Followed by 3 digits. \\)? :
         * May have an optional ")" [- ]? : May have an optional "-" after the
         * first 3 digits or after optional ) character. (\\d{3}) : Followed by
         * 3 digits. [- ]? : May have another optional "-" after numeric digits.
         * (\\d{4})$ : ends with four digits.
         * 
         * Examples: Matches following phone numbers: (123)456-7890,
         * 123-456-7890, 1234567890, (123)-456-7890
         */
        // Initialize reg ex for phone number.
        final String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
        final CharSequence inputStr = phoneNumber;
        final Pattern pattern = Pattern.compile(expression);
        final Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * isNumeric: Validate a number using Java regex. This method checks if the
     * input string contains all numeric characters.
     * 
     * @param number
     *            Number to validate
     * @return boolean: true if the input is all numeric, false otherwise.
     */
    public static boolean isNumeric(final String number) {
        boolean isValid = false;
        /*
         * Number: A numeric value will have following format: ^[-+]?: Starts
         * with an optional "+" or "-" sign. [0-9]*: May have one or more
         * digits. \\.? : May contain an optional "." (decimal point) character.
         * [0-9]+$ : ends with numeric digit.
         */
        // Initialize reg ex for numeric data.
        final String expression = "^[-+]?[0-9]*\\.?[0-9]+$";
        final CharSequence inputStr = number;
        final Pattern pattern = Pattern.compile(expression);
        final Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * isNotEmpty: Validates a nonempty String. This method checks if the input
     * string which is not empty.
     * 
     * @param input
     *            the input
     * @return boolean: true if the input is not empty, false otherwise.
     */
    public static boolean isNotEmpty(final String input) {
        boolean isValid = false;
        if (input == null) {

        } else {
            if (input.length() > 0) {
                isValid = true;
            }
        }
        return isValid;
    }

}
