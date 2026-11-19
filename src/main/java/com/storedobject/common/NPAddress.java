/*
 * Copyright (c) 2018-2026 Syam Pillai
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

import java.util.List;

/**
 * NL (Nepal) address.
 *
 * @author Syam
 */
public class NPAddress extends Address {

    NPAddress() {
        setCity("Kathmandu");
    }

    @Override
    public int getExtraLines() {
        return 1;
    }

    @Override
    boolean parse() throws SOException {
        if(getWard() <= 0) {
            throw new SOException("Ward number must be specified");
        }
        return super.parse();
    }

    @Override
    protected void add(List<String> lines, String apartmentName, String buildingName, String streetName) {
        StringBuilder s = new StringBuilder(apartmentName);
        if(buildingName != null && !buildingName.isBlank()) {
            s.append(", ").append(buildingName);
        }
        if(streetName != null && !streetName.isBlank()) {
            s.append(", ").append(streetName);
        }
        s.append(", Ward ").append(this.lines[0]);
        lines.add(s.toString());
    }

    /**
     * Get the ward number.
     *
     * @return Ward number.
     */
    public int getWard() {
        try {
            return extractNumber(0);
        } catch (SOException e) {
            return 0;
        }
    }

    /**
     * Set the ward number.
     *
     * @param ward Ward number.
     */
    public void setWard(int ward) {
        lines[0] = "" + ward;
    }

    @Override
    int postalCodePosition() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getPostalCodeMinLength() {
        return 5;
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
        return " NEPAL";
    }

    @Override
    protected boolean isCountryNeeded() {
        return false;
    }

    @Override
    public String getAreaCaption() {
        return "City";
    }

    @Override
    public String getStreetNameCaption() {
        return super.getStreetNameCaption() + " / Tole";
    }

    @Override
    String convert(int lineNumber) throws SOException {
        return null;
    }
}
