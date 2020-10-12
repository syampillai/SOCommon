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

    public final static String[] states = new String[] {
      "Johor", "Kedah", "Kelantan", "Labuan", "Melaka", "Negeri Sembilan", "Pahang", "Perak",
            "Perlis", "Pinang", "Putrajaya", "Sabah", "Sarawak", "Selangor", "Terrengganu",
            "Wilayah Persekutuan Kuala Lumpur"
    };

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
        return postalCode >= 10000 && postalCode <= 99999;
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

    public String getPostalTown() {
        return lines[lines.length - POS_TOWN];
    }

    public void setPostalTown(String name) {
        lines[lines.length - POS_TOWN] = name == null ? "" : name;
    }

    public int getState()throws SOException {
        return extractNumber(lines.length - POS_STATE);
    }

    public void setState(int state) {
        lines[lines.length - POS_STATE] = "" + (state % states.length);
    }

    public String getStateName() {
        return extractName(lines.length - POS_STATE, states);
    }

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
