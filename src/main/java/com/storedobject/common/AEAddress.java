/*
 * Copyright 2018 Syam Pillai
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
 * AE Address<BR>
 * line[0]: Code for the emirate (0: Abu Dhabi, 1: Dubai, 2: Sharjah, 3: Ajman,  4: Um Al Quwain, 5: Ras Al Khaimah, 6: Fujairah)<BR>
 *
 * @author Syam
 */
public final class AEAddress extends Address {

    private static final String[] emirates = new String[] {
            "Abu Dhabi", "Dubai", "Sharjah", "Ajman", "Um Al Quwain", "Ras Al Khaimah", "Fujairah"
    };

    /**
     * Constructs an instance of AEAddress.
     * This constructor initializes an AEAddress object, which represents an address
     * specific to the United Arab Emirates. The address includes information about
     * the emirate, identified by its code.
     */
    AEAddress() {
    }

    @Override
    boolean parse() throws SOException {
        lines[lines.length - 1] = match(lines[lines.length - 1], emirates);
        return super.parse();
    }

    @Override
    public int getExtraLines() {
        return 1;
    }

    @Override
    String convert(int lineNumber) throws SOException {
        if(lineNumber == (lines.length - 1)) {
            return getEmirateName();
        }
        return super.convert(lineNumber);
    }

    /**
     * Sets the emirate code for the address.
     *
     * @param emirate an integer representing the code of the emirate (0 for Abu Dhabi, 1 for Dubai,
     *                2 for Sharjah, 3 for Ajman, 4 for Um Al Quwain, 5 for Ras Al Khaimah, 6 for Fujairah).
     *                If the input exceeds the valid range, the code will be calculated using modulus division
     *                by the total number of emirates.
     */
    public void setEmirate(int emirate) {
        lines[lines.length - 1] = "" + (emirate % emirates.length);
    }

    /**
     * Retrieves the emirate code from the last line of the address.
     *
     * @return an integer representing the code of the emirate (0 for Abu Dhabi, 1 for Dubai,
     *         2 for Sharjah, 3 for Ajman, 4 for Um Al Quwain, 5 for Ras Al Khaimah, 6 for Fujairah).
     * @throws SOException if an error occurs during number extraction.
     */
    public int getEmirate()throws SOException {
        return extractNumber(lines.length - 1);
    }

    /**
     * Retrieves the name of the emirate based on the last line of the address.
     *
     * @return a string representing the name of the emirate.
     */
    public String getEmirateName() {
        return extractName(lines.length - 1, emirates);
    }

    /**
     * Retrieves the list of emirates in the United Arab Emirates.
     *
     * @return an array of strings, each representing the name of an emirate.
     */
    public static String[] getEmirates() {
        return emirates;
    }

    @Override
    int poBoxPosition() {
        return 4;
    }
}