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

import java.math.BigInteger;

/**
 * IBAN utilities.
 *
 * @author Syam
 */
public class IBAN {

    private static final BigInteger NINETY_SEVEN = new BigInteger("97");
    private static final BigInteger HUNDRED = new BigInteger("100");

    /**
     * Validate a given IBAN. (For certain countries, the valid length is already coded (See {@link #length(Country)})
     * and for those countries length is also verified.)
     *
     * @param iban IBAN (It could be a formatted IBAN too).
     * @return True if valid.
     */
    public static boolean validate(String iban) {
        iban = StringUtility.pack(iban).toUpperCase();
        if(iban.length() < 8) {
            return false;
        }
        Country country = Country.get(iban.substring(0, 2));
        if(country == null) {
            return false;
        }
        int length = length(country);
        if(length != -1 && length != iban.length()) {
            return false;
        }
        iban = iban.substring(2);
        iban = iban.substring(2) + ibanDigits(country.getShortName()) + iban.substring(0, 2);
        iban = bbanCheck(country, iban);
        if(iban == null) {
            return false;
        }
        return iban(new BigInteger(iban));
    }

    private static boolean iban(BigInteger number) {
        return number.mod(NINETY_SEVEN).equals(BigInteger.ONE);
    }

    private static String bbanCheck(Country c, String bban) {
        switch(c.getShortName()) {
            case "AE":
                if(!StringUtility.isDigit(bban)) {
                    return null;
                }
                return bban;
            case "PK":
            case "GB":
                String b = bban.substring(0, 4);
                bban = bban.substring(4);
                if(!StringUtility.isLetter(b) || !StringUtility.isDigit(bban)) {
                    return null;
                }
                return ibanDigits(b) + bban;
        }
        return ibanDigitsA(bban);
    }

    private static String ibanDigitsA(String s) {
        if(StringUtility.isDigit(s)) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        char c;
        for(int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if(Character.isDigit(c)) {
                sb.append(c);
            } else {
                sb.append(10 + s.charAt(i) - 'A');
            }
        }
        return sb.toString();
    }

    private static String ibanDigits(String s) {
        if(StringUtility.isDigit(s)) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        char c;
        for(int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if(Character.isDigit(c)) {
                sb.append(c);
            } else {
                sb.append(10 + s.charAt(i) - 'A');
            }
        }
        return sb.toString();
    }

    /**
     * Get length of IBAN.
     *
     * @param country Country
     * @return Length of IBAN for known countries. -1 is returned if unknown (or not yet coded here).
     * -2 is returned if <code>country</code> is <code>null</code> or for invalid country code.
     */
    public static int length(String country) {
        return length(Country.get(country));
    }

    /**
     * Get length of IBAN.
     *
     * @param country Country
     * @return Length of IBAN for known countries. -1 is returned if unknown (or not yet coded here).
     * -2 is returned if <code>country</code> is <code>null</code>.
     */
    public static int length(Country country) {
        if(country == null) {
            return -2;
        }
        return switch (country.getShortName()) {
            case "AE" -> 23;
            case "CH" -> 21;
            case "GB", "GE" -> 22;
            case "PK" -> 24;
            default -> -1;
        };
    }

    /**
     * Compute the ISO7064 97-10 check digits for the number passed.
     *
     * @param number Number
     * @return Check digits. -1 is returned for invalid numbers.
     */
    private static int iso7064_97_10(BigInteger number) {
        try {
            return 98 - number.multiply(HUNDRED).mod(NINETY_SEVEN).intValue();
        } catch(Throwable ignored) {
        }
        return -1;
    }

    private static int iso7064_97_10Compute(String alphanumeric) {
        if(!StringUtility.isDigit(alphanumeric)) {
            alphanumeric = ibanDigits(alphanumeric);
        }
        return iso7064_97_10(alphanumeric);
    }

    /**
     * Compute the ISO7064 97-10 check digits for the alphanumeric string passed.
     *
     * @param alphanumeric Number
     * @return Check digits. -1 is returned for invalid numbers and -2 is returned for invalid strings.
     */
    private static int iso7064_97_10(String alphanumeric) {
        try {
            if(alphanumeric.startsWith("-") || alphanumeric.startsWith("+")) {
                alphanumeric = alphanumeric.substring(1);
            }
            return iso7064_97_10(new BigInteger(alphanumeric));
        } catch(Throwable ignored) {
        }
        return -2;
    }

    /**
     * Insert check digits to the given set of IBAN fragments for a given country to generate a valid IBAN.
     *
     * @param country Country
     * @param ibanFragments Fragment of the IBAN. If the length of the IBAN for the given country is known and
     *                     the total length of the fragments given is smaller in size, then, the last fragment
     *                     will be left-padded with zeros.
     * @return A valid IBAN. If the <code>country</code> is <code>null</code> or if the total length of the
     * IBAN fragments is <code>null</code> or too long, null is returned.
     */
    public static String generate(String country, String... ibanFragments) {
        return generate(Country.get(country), ibanFragments);
    }

    /**
     * Insert check digits to the given set of IBAN fragments for a given country to generate a valid IBAN.
     *
     * @param country Country
     * @param ibanFragments Fragment of the IBAN. If the length of the IBAN for the given country is known and
     *                     the total length of the fragments given is smaller in size, then, the last fragment
     *                     will be left-padded with zeros.
     * @return A valid IBAN. If the <code>country</code> is <code>null</code> or if the total length of the
     * IBAN fragments is <code>null</code> or too long, null is returned.
     */
    public static String generate(Country country, String... ibanFragments) {
        if(country == null || ibanFragments == null || ibanFragments.length == 0) {
            return null;
        }
        StringUtility.pack(ibanFragments);
        StringBuilder prefix = new StringBuilder();
        for(int i = 0; i < (ibanFragments.length - 1); i++) {
            prefix.append(ibanFragments[i]);
        }
        String prefixFragment = prefix.toString().toUpperCase();
        String ibanFragment = ibanFragments[ibanFragments.length - 1].toUpperCase();
        int len = length(country) - 4;
        if(len > 0) {
            if((prefixFragment.length() + ibanFragment.length()) > len) {
                return null;
            }
            len -= prefixFragment.length();
            if(ibanFragment.length() < len) {
                ibanFragment = StringUtility.padLeft(ibanFragment, len, '0');
            }
        }
        ibanFragment = prefixFragment + ibanFragment;
        int checkDigits = iso7064_97_10Compute(ibanFragment + country.getShortName());
        if(checkDigits < 0) {
            return null;
        }
        String cd = "" + checkDigits;
        if(checkDigits < 10) {
            cd = "0" + cd;
        }
        return country.getShortName() + cd + ibanFragment;
    }

    /**
     * Format an IBAN by inserting a space after every 4 positions from the left. (No validation is done).
     *
     * @param iban IBAN to format.
     * @return Formatted IBAN.
     */
    public static String format(String iban) {
        iban = StringUtility.pack(iban);
        StringBuilder sb = new StringBuilder();
        String b;
        while(iban.length() > 4) {
            if(sb.length() > 0) {
                sb.append(" ");
            }
            b = iban.substring(0, 4);
            iban = iban.substring(4);
            sb.append(b);
        }
        if(iban.length() > 0) {
            sb.append(" ").append(iban);
        }
        return sb.toString();
    }
}
