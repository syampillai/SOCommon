/*
 * Copyright (c) 2018-2020 Syam Pillai
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
 * MY Address<BR>
 * line[0]: Postal town<BR>
 * line[1]: State code<BR>
 *
 * @author Syam
 */
public class MYAddress extends Address {

    private static final int POS_STATE = 1, POS_TOWN = 2;

    private final static String[] states = new String[] {
      "Johor", "Kedah", "Kelantan", "Labuan", "Melaka", "Negeri Sembilan", "Pahang", "Perak",
            "Perlis", "Pinang", "Putrajaya", "Sabah", "Sarawak", "Selangor", "Terrengganu",
            "Wilayah Persekutuan Kuala Lumpur"
    };

    /**
     * Constructor.
     */
    MYAddress() {
    }

    @Override
    protected boolean parse() throws SOException {
        if(StringUtility.isWhite(getPostalTown())) {
            throw new SOException("Postal town");
        }
        if(StringUtility.isWhite(getAreaName())) {
            throw new SOException("District");
        }
        lines[lines.length - POS_STATE] = match(lines[lines.length - POS_STATE], states);
        return true;
    }

    @Override
    boolean checkPostalCode() {
        return postalCode.length() == 5 && StringUtility.isNumber(postalCode);
    }

    @Override
    public int getPostalCodeMaxLength() {
        return 5;
    }

    @Override
    String postalCodePrefix() {
        return "";
    }

    @Override
    String postalCodeSuffix() {
        return " " + getPostalTown();
    }

    @Override
    public int getExtraLines() {
        return 2;
    }

    @Override
    protected String convert(int lineNumber) throws SOException {
        int n = lines.length - POS_STATE;
        if(lineNumber == n) {
            return getStateName();
        }
        --n;
        if(lineNumber == n) {
            return ""; // Postal town, already merged with postal code
        }
        return super.convert(lineNumber);
    }

    /**
     * Retrieves the postal town from the address data. The postal town is
     * derived from the last element in the address array, offset by a
     * predefined position value.
     *
     * @return the postal town as a String
     */
    public String getPostalTown() {
        return lines[lines.length - POS_TOWN];
    }

    /**
     * Sets the postal town for the address. Updates the address data with the
     * provided postal town, replacing the existing value. If the input is null,
     * the postal town is set to an empty string.
     *
     * @param name the name of the postal town to be set. If null, it is replaced with an empty string.
     */
    public void setPostalTown(String name) {
        lines[lines.length - POS_TOWN] = name == null ? "" : name;
    }

    /**
     * Retrieves the state value derived from the address data.
     * The state is calculated based on a specific line in the address,
     * determined using the length of the lines and a predefined position constant.
     *
     * @return the state value as an integer.
     * @throws SOException if an error occurs during the extraction process.
     */
    public int getState()throws SOException {
        return extractNumber(lines.length - POS_STATE);
    }

    /**
     * Updates the state value in the address data. The state is set based on
     * the provided integer value and stored in the corresponding line of the
     * address array, calculated using the predefined position constant.
     *
     * @param state the integer value representing the state to be set. The value
     *              is normalized by computing the remainder when divided by
     *              the length of the states array.
     */
    public void setState(int state) {
        lines[lines.length - POS_STATE] = "" + (state % states.length);
    }

    /**
     * Retrieves the name of the state from the address data. The state name is
     * derived from the states array based on a calculated position determined
     * by the length of the lines and a predefined position constant.
     *
     * @return the name of the state as a String.
     */
    public String getStateName() {
        return extractName(lines.length - POS_STATE, states);
    }

    /**
     * Retrieves the array of state names available in the address data.
     * The list of states is predefined and stored in an internal array.
     *
     * @return an array of Strings containing the names of all available states.
     */
    public static String[] getStates() {
        return states;
    }

    @Override
    public String getPOBoxName() {
        return "Peti Surat";
    }

    @Override
    int poBoxPosition() {
        return 4;
    }

    @Override
    int postalCodePosition() {
        return 5;
    }

    @Override
    public String getAreaCaption() {
        return "District";
    }
}
