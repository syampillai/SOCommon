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
        setPostalCode("44600");
    }

    @Override
    public int getExtraLines() {
        return 2;
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

    /**
     * Get the district.
     *
     * @return District.
     */
    public String getDistrict() {
        return lines[1];
    }

    /**
     * Set the district.
     *
     * @param district District.
     */
    public void setDistrict(String district) {
        lines[1] = district;
    }

    @Override
    public boolean isPostalCodeMandatory() {
        return true;
    }

    @Override
    int postalCodePosition() {
        return -1;
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
        return getCity() + " ";
    }

    @Override
    protected boolean isCountryUpperCase() {
        return true;
    }

    @Override
    String areaName() {
        return (super.areaName() + " " + getPostalCode()).trim();
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
    String convert(int lineNumber) {
        if(lineNumber == 0) return null;
        String line = lines[lineNumber];
        if(line == null || line.isEmpty()) return null;
        return line + " District";
    }
}
