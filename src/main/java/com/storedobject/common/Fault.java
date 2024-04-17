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
    Fault TECHNICAL_FAULT = new Fault(Integer.MIN_VALUE, "Technical error");
    Fault ACCOUNT_NOT_FOUND = new Fault(100001, "Account not found");
    Fault ACCOUNT_ALREADY_EXISTS = new Fault(100002, "Account already exists");
    Fault INVALID_DATE = new Fault(100003, "Invalid date");
    Fault INVALID_PERIOD = new Fault(100004, "Invalid period");
    Fault INVALID_AMOUNT = new Fault(100005, "Invalid amount");
    Fault INVALID_CURRENCY = new Fault(100006, "Invalid currency");
    Fault NOT_A_CUSTOMER_ACCOUNT = new Fault(100007, "Not a customer account");
    Fault TRANSACTION_NOT_BALANCED = new Fault(100008, "Transaction not balanced");
    Fault ACTION_NOT_SPECIFIED = new Fault(100009, "Action not specified");
    Fault UNKNOWN_ACTION = new Fault(1000010, "Unknown action");
    Fault VOUCHER_NOT_FOUND = new Fault(1000011, "Voucher not found");
    Fault TRANSACTION_APPROVAL_ERROR = new Fault(1000012, "Transaction approval error");
    Fault REQUESTING_SYSTEM_NOT_RECOGNIZED = new Fault(1000013, "Requesting system not recognized");
    Fault NO_ENTRIES_FOUND = new Fault(1000014, "No entries found");
    Fault MULTIPLE_ENTRIES_FOUND = new Fault(1000015, "Multiple entries");
    Fault DUPLICATE_ENTRY_FOUND = new Fault(1000016, "Duplicate entry");
    Fault DUPLICATE_REFERENCE_FOUND = new Fault(1000017, "Duplicate reference");
    Fault REFERENCE_NOT_FOUND = new Fault(1000018, "Reference does not exist");
    Fault CUSTOMER_NOT_FOUND = new Fault(1000019, "Customer does not exist");
    Fault NOT_ENOUGH_BALANCE = new Fault(1000020, "Not enough balance");
    Fault REFERENCE_ALREADY_EXISTS = new Fault(1000021, "Reference already exists");
    Fault REFERENCE_NOT_SPECIFIED = new Fault(1000022, "Reference not specified");
}
