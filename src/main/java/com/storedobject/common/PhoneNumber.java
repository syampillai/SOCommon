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
 * Class used to check phone number validity.<BR>
 * A valid phone number starts with a "+" symbol followed by dialing digits with optional space separators. The first
 * one or more digits following the "+" symbol should match with ISD code of one of the countries (see {@link Country#getISDCode()}).
 *
 * @author Syam
 */
public final class PhoneNumber {

    /**
     * Check correctness of the format of the given phone number.
     *
     * @param phoneNumber Phone number
     * @return Phone number (may be modified).
     * @throws SOException If the phone number format is not valid
     */
    public static String check(String phoneNumber) throws SOException {
        return check(phoneNumber, false);
    }

    /**
     * Check correctness of the format of the given phone number.
     *
     * @param phoneNumber Phone number
     * @param allowEmpty Whether empty should be taken as valid or not
     * @return Phone number (may be modified).
     * @throws SOException If the phone number format is not valid
     */
    public static String check(String phoneNumber, boolean allowEmpty) throws SOException {
        if(StringUtility.isWhite(phoneNumber)) {
            if(allowEmpty) {
                return "";
            }
            throw new SOException("Empty phone number");
        }
        phoneNumber = phoneNumber.trim();
        if(!phoneNumber.startsWith("+")) {
            phoneNumber = "+" + phoneNumber;
        }
        Country country = getCountry(phoneNumber);
        if(country != null) {
            int p = digits(phoneNumber), len = length(country, phoneNumber);
            if((len > 0 && p == len) || (len < 0 && p >= (-len))) {
                return phoneNumber;
            }
        }
        throw new SOException("Invalid phone number - " + phoneNumber);
    }

    private static int digits(String s) {
        int digits = 0;
        for(int i = 0; i < s.length(); i++) {
            if(Character.isDigit(s.charAt(i))) {
                ++digits;
            }
        }
        return digits;
    }

    /**
     * Get the country for the given phone number.
     *
     * @param phoneNumber Phone number
     * @return Country of the phone number if found, otherwise <code>null</code>.
     */
    public static Country getCountry(String phoneNumber) {
        Country country = Country.getByPhoneNumber(phoneNumber);
        if(country != null) {
            if(country == country.getNextByPhoneNumber(phoneNumber)) {
                return country;
            }
        }
        return null;
    }

    private static int length(Country country, String phone) {
        if(phone.startsWith("+1")) {
            return 11;
        }
        switch (country.getShortName()) {
            case "AE":
            case "KE":
            case "PK":
                phone = StringUtility.pack(phone);
        }
        switch (country.getShortName()) {
            case "AE":
                return phone.startsWith("+9715") ? 12 : 11;
            case "KE":
                return phone.startsWith("+2547") ? 12 : -10;
            case "PK":
                return phone.startsWith("+923") ? 12 : 11;
            case "GE":
            case "IN":
                return 12;
            case "CH":
            case "HK":
            case "ZA":
                return 11;
            case "UK":
                return -11;
        }
        int d = 0;
        for(String code: country.listISDPrefix()) {
            d = digits(code);
            break;
        }
        return -((d == 0 ? digits(country.getISDCode()) : d) + 4);
    }
}