/*
 * Copyright (c) 2018-2022 Syam Pillai
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

import java.util.stream.Stream;

/**
 * US Address<BR>
 * line[0]: Code for the state.
 *
 * @author Syam
 */
public class USAddress extends Address {

    private static record ZIP(String state, int zipFrom, int zipTo) {}

    private final static ZIP[] states = new ZIP[] {
            new ZIP("AL Alabama", 35004, 36925),
            new ZIP("AK Alaska", 99501, 99950),
            new ZIP("AZ Arizona", 85001, 86556),
            new ZIP("AR Arkansas", 71601, 72959),
            new ZIP("CA California", 90001, 96162),
            new ZIP("CO Colorado", 80001, 81658),
            new ZIP("CT Connecticut", 6001, 6928),
            new ZIP("DE Delaware", 19701, 19980),
            new ZIP("FL Florida", 32003, 34997),
            new ZIP("GA Georgia", 30002, 39901),
            new ZIP("HI Hawaii", 96701, 96898),
            new ZIP("ID Idaho", 83201, 83877),
            new ZIP("IL Illinois", 60001, 62999),
            new ZIP("IN Indiana", 46001, 47997),
            new ZIP("IA Iowa", 50001, 52809),
            new ZIP("KS Kansas", 66002, 67954),
            new ZIP("KY Kentucky", 40003, 42788),
            new ZIP("LA Louisiana", 70001, 71497),
            new ZIP("ME Maine", 3901, 4992),
            new ZIP("MD Maryland", 20588, 21930),
            new ZIP("MA Massachusetts", 1001, 5544),
            new ZIP("MI Michigan", 48001, 49971),
            new ZIP("MN Minnesota", 55001, 56763),
            new ZIP("MS Mississippi", 38601, 39776),
            new ZIP("MO Missouri", 63001, 65899),
            new ZIP("MT Montana", 	59001, 59937),
            new ZIP("NE Nebraska", 68001, 69367),
            new ZIP("NV Nevada", 	88901, 89883),
            new ZIP("NH New Hampshire", 3031, 3897),
            new ZIP("NJ New Jersey", 	7001, 8989),
            new ZIP("NM New Mexico", 87001, 88439),
            new ZIP("NY New York", 501, 14925), // 31
            new ZIP("NC North Carolina", 27006, 28909),
            new ZIP("ND North Dakota", 58001, 58856),
            new ZIP("OH Ohio", 43001, 45999),
            new ZIP("OK Oklahoma", 73001, 74966),
            new ZIP("OR Oregon", 97001, 97920),
            new ZIP("PA Pennsylvania", 15001, 19640),
            new ZIP("RI Rhode Island", 2801, 2940),
            new ZIP("SC South Carolina", 29001, 29945),
            new ZIP("SD South Dakota", 57001, 57799),
            new ZIP("TN Tennessee", 37010, 38589),
            new ZIP("TX Texas", 	73301, 88595),
            new ZIP("UT Utah", 84001, 84791),
            new ZIP("VT Vermont", 5001, 5907),
            new ZIP("VA Virginia", 20101, 24658),
            new ZIP("WA Washington", 	98001, 99403),
            new ZIP("WV West Virginia", 24701, 26886),
            new ZIP("WI Wisconsin", 	53001, 54990),
            new ZIP("WY Wyoming", 82001, 83414),
            new ZIP("AS American Samoa", 96799, 96799),
            new ZIP("DC District of Columbia", 20001, 56972),
            new ZIP("GU Guam", 96910, 96932),
            new ZIP("MP Northern Mariana Islands", 96950, 96952),
            new ZIP("PR Puerto Rico", 601, 988),
            new ZIP("VI United States Virgin Islands", 801, 851)
    };

    @Override
    public int getExtraLines() {
        return 1;
    }

    @Override
    String convert(int lineNumber) throws SOException {
        if(lineNumber == (lines.length - 1) || lineNumber == 1) {
            return "";
        }
        return super.convert(lineNumber);
    }

    @Override
    boolean streetNameFirst() {
        return true;
    }

    @Override
    String areaName() {
        return "";
    }

    @Override
    public String getAreaCaption() {
        return "City";
    }

    /**
     * Set the state.
     *
     * @param state Index values of the state.
     */
    public void setState(int state) {
        lines[lines.length - 1] = "" + (state % states.length);
    }

    /**
     * Set the state.
     *
     * @param state 2-character state code..
     */
    public void setState(String state) {
        if(state != null) {
            state = state.toUpperCase();
            for(int i = 0; i < states.length; i++) {
                if(states[i].state.startsWith(state)) {
                    setState(i);
                    return;
                }
            }
        }
    }

    /**
     * Get the state index.
     * @return Index value of the state.
     * @throws SOException If state index is currently invalid.
     */
    public int getState() throws SOException {
        int s = extractNumber(lines.length - 1);
        if(s >= 0 && s < states.length) {
            return s;
        }
        throw new SOException("Invalid state");
    }

    /**
     * Get the name of the states.
     *
     * @return Stream of the state names.
     */
    public static Stream<String> getStates() {
        return new Array<>(states).stream().map(s -> s.state);
    }

    @Override
    int postalCodePosition() {
        return 100;
    }

    @Override
    public String getPostalCodeCaption() {
        return "ZIP/ZIP+4 Code";
    }

    @Override
    String postalCodePrefix() {
        ZIP zip = getZIP();
        String city = zip == null ? "XX" : zip.state.substring(0, 2);
        return getAreaName() + ", " + city + " ";
    }

    @Override
    public int getPostalCodeMinLength() {
        return 5;
    }

    @Override
    public int getPostalCodeMaxLength() {
        return 10;
    }

    @Override
    public boolean isPostalCodeMandatory() {
        return true;
    }

    @Override
    public boolean isNumericPostalCode() {
        return false;
    }

    @Override
    boolean checkPostalCode() {
        if(postalCode.startsWith("-") || postalCode.endsWith("-")) {
            return false;
        }
        String z;
        int dash = postalCode.indexOf('-');
        if(dash > 0) {
            z = postalCode.substring(dash + 1);
            if(z.length() != 4 || !StringUtility.isDigit(z) || "0000".equals(z)) {
                return false;
            }
            z = postalCode.substring(0, dash);
        } else {
            z = postalCode;
        }
        if(z.length() != 5 || "00000".equals(z)) {
            return false;
        }
        ZIP state = getZIP();
        if(state == null) {
            return false;
        }
        while(z.startsWith("0")) {
            z = z.substring(1);
        }
        int zip = Integer.parseInt(z);
        return zip >= state.zipFrom && zip <= state.zipTo;
    }

    private ZIP getZIP() {
        try {
            int s = getState();
            return states[s];
        } catch(SOException ignored) {
        }
        return null;
    }

    @Override
    String postalCode() {
        return postalCode; // No trimming of leading zeros
    }
}
