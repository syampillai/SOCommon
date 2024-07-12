/*
 * Copyright (c) 2018-2024 Syam Pillai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.storedobject.common;

/**
 * Represents a fault or error condition in the system.
 *
 * This class provides methods to retrieve information about the fault, replace the fault message or code,
 * and append additional information to the fault message.
 */
public class Fault {

    private final int code;
    private final String message;

    /**
     * Represents a fault with an error code and a descriptive message.
     *
     * @param code    the error code associated with the fault
     * @param message the descriptive message of the fault
     */
    public Fault(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Retrieves the code value associated with this object.
     *
     * @return The code value.
     */
    public int getCode() {
        return code;
    }

    /**
     * Retrieves the message associated with this method.
     *
     * @return The message string.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Replaces the message of a Fault object.
     *
     * @param message the new message to be set
     * @return a new Fault object with the updated message
     */
    public Fault replace(String message) {
        return new Fault(code, message);
    }

    /**
     * Replaces the code of the Fault with the given code and returns the updated Fault object.
     *
     * @param code the new code to be set in the Fault
     * @return the updated Fault object with the new code and the same message as the original Fault
     */
    public Fault replace(int code) {
        return new Fault(code, message);
    }

    /**
     * Appends a message to the existing fault message with a default separator.
     *
     * @param message the message to be appended to the fault message
     * @return the updated Fault object with the appended message
     */
    public Fault append(String message) {
        return append(message, " - ");
    }

    /**
     * Appends the given message to the current message using the specified delimiter.
     *
     * @param message   the message to be appended
     * @param delimiter the delimiter to be used for joining the current message and the given message
     * @return a new Fault object with the updated message
     */
    public Fault append(String message, String delimiter) {
        return new Fault(code, this.message + delimiter + message);
    }

    /**
     * Checks if this fault is categorized.
     *
     * @return {@code true} if the fault is categorized, {@code false} otherwise.
     */
    public boolean isCategorized() {
        return code != Integer.MAX_VALUE;
    }

    /**
     * Checks if this fault is technical.
     *
     * @return true if this is technical, false otherwise.
     */
    public boolean isTechnical() {
        return code == Integer.MIN_VALUE;
    }

    // Common fault values

    public static final Fault TECHNICAL_FAULT = new Fault(Integer.MIN_VALUE, "Technical error");
    public static final Fault UNCATEGORIZED_FAULT = new Fault(Integer.MAX_VALUE, "Uncategorized error");

    public static final Fault ACCOUNT_ALREADY_EXISTS = new Fault(100100, "Account already exists");
    public static final Fault ACCOUNT_NOT_FOUND = new Fault(100101, "Account not found");
    public static final Fault ACCOUNT_NOT_SPECIFIED = new Fault(100102, "Account not specified");

    public static final Fault CUSTOMER_NOT_FOUND = new Fault(100111, "Customer does not exist");
    public static final Fault NOT_A_CUSTOMER_ACCOUNT = new Fault(100112, "Not a customer account");
    public static final Fault NOT_A_LOAN_ACCOUNT = new Fault(100113, "Not a loan account");
    public static final Fault NOT_A_DEPOSIT_ACCOUNT = new Fault(100114, "Not a deposit account");
    public static final Fault NOT_A_GL_ACCOUNT = new Fault(100115, "Not a GL account");

    public static final Fault INVALID_DATE = new Fault(100200, "Invalid date");
    public static final Fault INVALID_PERIOD = new Fault(100201, "Invalid period");
    public static final Fault INVALID_AMOUNT = new Fault(100202, "Invalid amount");
    public static final Fault INVALID_CURRENCY = new Fault(100203, "Invalid currency");

    public static final Fault TRANSACTION_APPROVAL_ERROR = new Fault(100400, "Transaction approval error");
    public static final Fault TRANSACTION_NOT_BALANCED = new Fault(100401, "Transaction not balanced");
    public static final Fault TRANSACTION_NOT_FOUND = new Fault(100402, "Transaction does not exist");
    public static final Fault PARTIAL_TRANSACTION_COMMITTED = new Fault(100403, "Partial transaction committed");
    public static final Fault NOT_ENOUGH_BALANCE = new Fault(100411, "Not enough balance");
    public static final Fault EMPTY_NARRATION = new Fault(100412, "Empty narration");
    public static final Fault VOUCHER_NOT_FOUND = new Fault(100499, "Voucher not found");

    public static final Fault ACTION_NOT_SPECIFIED = new Fault(100500, "Action not specified");
    public static final Fault UNKNOWN_ACTION = new Fault(100501, "Unknown action");

    public static final Fault DUPLICATE_ENTRY_FOUND = new Fault(100700, "Duplicate entry");
    public static final Fault NO_ENTRIES_FOUND = new Fault(100701, "No entries found");
    public static final Fault MULTIPLE_ENTRIES_FOUND = new Fault(100702, "Multiple entries");

    public static final Fault DUPLICATE_REFERENCE_FOUND = new Fault(100800, "Duplicate reference");
    public static final Fault REFERENCE_NOT_FOUND = new Fault(100801, "Reference does not exist");
    public static final Fault REFERENCE_ALREADY_EXISTS = new Fault(100802, "Reference already exists");
    public static final Fault REFERENCE_NOT_SPECIFIED = new Fault(100803, "Reference not specified");

    public static final Fault NOT_ALLOWED = new Fault(100901, "Not allowed");
    public static final Fault ILLEGAL_ACCESS = new Fault(100902, "Illegal access");
    public static final Fault EXTERNAL_SYSTEM_NOT_RECOGNIZED = new Fault(100999, "External system not recognized");
}
