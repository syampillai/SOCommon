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
 * Class used to check phone number validity.
 * A valid phone number contains all digits or a plus (+) symbol followed by digits. It can also
 * starts with 2 character {@link Country} code instead of the country's ISD dialing prefix and in that
 * case the <code>'check'</code> methods replace that part with ISD dialing prefix and the return value will
 * not contain the leading "+" symbol.
 *
 * @author Syam
 */
public final class PhoneNumber {

    /**
     * Check correctness of the format of the given phone number.
     *
     * @param phoneNumber Phone number
     * @return Phone number (may be modified).
     * @throws SOException If the email format is not valid
     */
    public String check(String phoneNumber) throws SOException {
        return check(phoneNumber, false);
    }

    /**
     * Check correctness of the format of the given phone number.
     *
     * @param phoneNumber Phone number
     * @param allowEmpty Whether empty should be taken as valid or not
     * @return Phone number (may be modified).
     * @throws SOException If the email format is not valid
     */
    public String check(String phoneNumber, boolean allowEmpty) throws SOException {
        if(StringUtility.isWhite(phoneNumber)) {
            if(allowEmpty) {
                return "";
            }
            throw new SOException("Empty phone number");
        }
        phoneNumber = phoneNumber.trim();
        if(phoneNumber.length() > 3) {
            if (Character.isAlphabetic(phoneNumber.charAt(0))) {
                Country country = Country.get(Country.check(phoneNumber.substring(0, 2)));
                phoneNumber = phoneNumber.substring(2).trim();
                if(StringUtility.isDigit(StringUtility.pack(phoneNumber))) {
                    return country.getISDCode() + " " + phoneNumber;
                }
                phoneNumber = country.getISDCode() + " " + phoneNumber;
            } else {
                if(phoneNumber.startsWith("+")) {
                    phoneNumber = phoneNumber.substring(1).trim();
                }
                if(StringUtility.isDigit(StringUtility.pack(phoneNumber))) {
                    return phoneNumber;
                }
            }
        }
        throw new SOException("Invalid phone number - " + phoneNumber);
    }
}