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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to check and manipulate mailing address information. Address is a {@link String} containing multiple lines
 * of text (delimited by newline character). A valid address is stored in the following format:<BR>
 * First line: CCdX where<BR>
 * CC = the ISO 2 character country code (Example: IN for India, PK for Pakistan, US for USA)<BR>
 * d = Digit 0, 1, 2 or 3 (0 for Apartment, 1 for Villa, 2 for House, 3 for Office)<BR>
 * X = Apartment/villa/office number/name. Can contain any free text but can't be blank<BR>
 * Second line: Building name (may include floor/level etc.). Mandatory for apartments.<BR>
 * Third line: Street name.<BR>
 * Fourth line: Area/Community name.<BR>
 * Depending on the country, rest of the lines are interpreted.
 *
 * @author Syam
 */
public abstract class Address {

    private static final String INVALID = "Invalid address - ";
    Country country;
    char apartmentCode;
    String apartmentName, buildingName, streetName, areaName;
    String[] lines;
    boolean valid;

    Address() {
    }

    /**
     * Check correctness of the format of the given address.
     *
     * @param address Address
     * @return Address (may be modified).
     * @throws SOException If the email format is not valid
     */
    public static String check(String address) throws SOException {
        return check(address, false);
    }

    /**
     * Check correctness of the format of the given address.
     *
     * @param address Address
     * @param allowEmpty Whether empty should be taken as valid or not
     * @return Address (may be modified).
     * @throws SOException If the email format is not valid
     */
    public static String check(String address, boolean allowEmpty) throws SOException {
        if(StringUtility.isWhite(address)) {
            if(allowEmpty) {
                return "";
            }
            throw new SOException("Empty address");
        }
        Address a = checkAddress(address);
        if(!a.valid) {
            throw new SOException(INVALID + address);
        }
        return a.encode();
    }

    private static void error(String address, String error) throws SOException {
        String err = INVALID + address;
        if(error != null) {
            err += " (" + error + ")";
        }
        throw new SOException(err);
    }

    private static Address checkAddress(String address) throws SOException {
        address = address.trim();
        String[] lines = address.split("\\r?\\n");
        StringUtility.trim(lines);
        if(lines.length < 4) {
            error(address, "Less than 4 lines");
        }
        if(lines[0].length() < 4) {
            error(address, "First line");
        }
        Country country = Country.get(Country.check(lines[0].substring(0, 2)));
        Address a = create(country);
        a.apartmentCode = lines[0].charAt(2);
        switch (a.apartmentCode) {
            case '0':
                if(lines[1].isEmpty()) {
                    error(address, "Building name missing");
                }
            case '1':
            case '2':
            case '3':
                break;
            default:
                error(address, "First line, unknown code: [" + a.apartmentCode + "]");
        }
        a.apartmentName = lines[0].substring(3);
        a.buildingName = lines[1];
        a.streetName = lines[2];
        a.areaName = lines[3];
        int n = a.lines.length, m = lines.length, r = a.getReservedLines();
        while(n > 0) {
            --n;
            --m;
            if(m > 3) {
                a.lines[n] = lines[m];
            } else {
                if(r <= 0) {
                    a.lines[n] = "";
                } else {
                    if(!a.areaName.isEmpty()) {
                        a.lines[n] = a.areaName;
                        a.areaName = "";
                    } else if(!a.streetName.isEmpty()) {
                        a.lines[n] = a.streetName;
                        a.streetName = "";
                    } else {
                        a.lines[n] = "";
                    }
                }
            }
            --r;
        }
        if(a.isValid()) {
            return a;
        }
        try {
            a.parse();
        } catch (SOException soerror) {
            error(address, soerror.getMessage());
        } catch (Throwable error) {
            error.printStackTrace();
            throw new SOException(INVALID + address, error);
        }
        return a;
    }

    /**
     * Returns if the address is valid or not.
     *
     * @return True or false.
     */
    public final boolean isValid() {
        if(!valid) {
            if(areaName == null) {
                areaName = "";
            }
            if(streetName == null) {
                streetName = "";
            }
            if(buildingName == null) {
                buildingName = "";
            }
            if(apartmentName == null) {
                apartmentName = "";
            }
            try {
                valid = parse();
            } catch (Throwable ignored) {
            }
        }
        return valid;
    }

    /**
     * Copy details from the given address. Country will not be copied.
     *
     * @param address Address from which details to be copied.
     * @return True if the address is valid after copying details.
     */
    public boolean copy(Address address) {
        apartmentCode = address.apartmentCode;
        apartmentName = address.apartmentName;
        buildingName = address.buildingName;
        streetName = address.streetName;
        areaName = address.areaName;
        int n = getLineCount(), m = address.lines.length;
        lines = new String[n];
        while (n > 0) {
            --n;
            --m;
            lines[n] = m >= 0 ? address.lines[m] : "";
        }
        try {
            valid = parse();
        } catch (Throwable ignored) {
        }
        return valid;
    }

    /**
     * Create a country specific address from the give details.
     *
     * @param address Address information in appropriate format
     * @return Address created or null if address can't be created. The created address may be invalid.
     */
    public static Address create(String address) {
        if(address == null || address.length() < 5) {
            return null;
        }
        try {
            return checkAddress(address);
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Create a blank country specific address.
     *
     * @param country Country for which address to be created.
     * @return Country specific blank address.
     */
    public static Address create(Country country) {
        Address a;
        try {
            a = (Address)Address.class.getClassLoader().loadClass("com.storedobject.common." + country.getShortName() + "Address").newInstance();
        } catch (Throwable error) {
            a = new XXAddress();
        }
        a.country = country;
        a.lines = new String[a.getLineCount()];
        Arrays.fill(a.lines, "");
        return a;
    }

    /**
     * Get address as a list of lines (Country line is not included).
     *
     * @return List of address lines (without country name).
     */
    List<String> toStrings() {
        ArrayList<String> slines = new ArrayList<>();
        if(!isValid()) {
            slines.add("[Not a valid address]");
        }
        String s;
        switch (apartmentCode) {
            case '0':
                s = "Apartment ";
                break;
            case '1':
                s = "Villa ";
                break;
            default:
                s = "";
                break;
        }
        slines.add(s + apartmentName);
        if(!valid || !buildingName.isEmpty()) {
            slines.add(buildingName);
        }
        if(!valid || !streetName.isEmpty()) {
            slines.add(streetName);
        }
        if(!valid && !areaName.isEmpty()) {
            slines.add(areaName);
        }
        String line;
        for(int i = 0; i < lines.length; i++) {
            if(valid) {
                try {
                    line = convert(i);
                } catch (SOException ignored) {
                    line = "?";
                }
                if (line != null && !line.isEmpty()) {
                    slines.add(line);
                }
            } else {
                slines.add(lines[i]);
            }
        }
        return slines;
    }

    /**
     * Convert the list of address lines to line-feed delimited address that can be printed or displayed. Country name
     * will be added at the end.
     *
     * @param lines Lines of the address as a list.
     * @return Line-feed delimited string that can be printed or displayed.
     */
    String toString(List<String> lines) {
        return String.join("\n", lines) + "\n" + country.getName();
    }

    @Override
    public String toString() {
        return toString(toStrings());
    }

    /**
     * Encode the address.
     *
     * @return Encoded address. Null will be returned if the address is not valid.
     */
    public String encode() {
        if(!isValid()) {
            return null;
        }
        StringBuilder s = new StringBuilder(country.getShortName());
        s.append(apartmentCode).append(apartmentName).append('\n').append(buildingName).append('\n');
        s.append(streetName).append('\n').append(areaName);
        for(String line: lines) {
            s.append('\n').append(line);
        }
        return s.toString();
    }

    abstract boolean parse() throws SOException;

    abstract int getLineCount();

    abstract int getReservedLines();

    String convert(int lineNumber) throws SOException {
        return lines[lineNumber];
    }

    static int extractNumber(String line) throws SOException {
        if(StringUtility.isDigit(line)) {
            return Integer.parseInt(line);
        }
        String original = line;
        int n = line.length() - 1;
        while(Character.isDigit(line.charAt(n))) {
            --n;
        }
        line = line.substring(n + 1);
        try {
            return Integer.parseInt(line);
        } catch (Throwable e) {
            throw new SOException("'" + original + "' doesn't contain any number");
        }
    }

    static String match(String line, String[] names) throws SOException{
        line = line.trim();
        int code;
        try {
            code = extractNumber(line);
        } catch(Throwable ignored) {
            for(code = 0; code < names.length; code++) {
                if(names[code].equalsIgnoreCase(line)) {
                    break;
                }
            }
            if(code >= names.length) {
                code = -1;
                int i, d, distance = Integer.MAX_VALUE;
                for(i = 0; i < names.length; i++) {
                    d = StringUtility.distanceLevenshtein(names[i], line);
                    if(distance > d) {
                        distance = d;
                        code = i;
                    }
                }
                if(distance > (line.length() >> 1)) {
                    code = -1;
                }
            }
        }
        if(code >= 0 && code < names.length) {
            return "" + code;
        }
        StringBuilder s = new StringBuilder();
        for(code = 0; code < names.length; code++) {
            if(s.length() > 0) {
                s.append(", ");
            }
            s.append(code).append(": ").append(names[code]);
        }
        throw new SOException("'" + line + "' has no match in [" + s + "]");
    }

    static String extractName(String line, String[] names) {
        return names[Integer.parseInt(line)];
    }

    int extractNumber(int lineNumber) throws SOException {
        return extractNumber(lines[lineNumber]);
    }

    String extractName(int lineNumber, String[] names) {
        return extractName(lines[lineNumber], names);
    }

    /**
     * Get a line from the address.
     *
     * @param lineNumber Line number
     * @return Line if exists, else null.
     */
    public String getLine(int lineNumber) {
        return lineNumber < lines.length ? lines[lineNumber] : null;
    }

    /**
     * Set an address line.
     *
     * @param lineNumber Line number (Must be less than lineCount - reservedLines)
     * @param line Value to be set
     */
    public void setLine(int lineNumber, String line) {
        if(lineNumber < (getLineCount() - getReservedLines())) {
            lines[lineNumber] = line;
        }
    }

    /**
     * Get the apartment code.
     *
     * @return Apartment code.
     */
    public char getApartmentCode() {
        return apartmentCode;
    }

    /**
     * Set the apartment code.
     *
     * @param apartmentCode Apartment code
     */
    public void setApartmentCode(char apartmentCode) {
        if(apartmentCode < '0' || apartmentCode > '3') {
            return;
        }
        this.apartmentCode = apartmentCode;
    }

    /**
     * Get the apartment/villa/house/office name.
     *
     * @return Apartment/villa/house/office name.
     */
    public String getApartmentName() {
        return apartmentName;
    }

    /**
     * Set the apartment/villa/house/office name.
     *
     * @param apartmentName Apartment/villa/house/office name
     */
    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    /**
     * Get the apartment/villa/house/office name.
     *
     * @return Apartment/villa/house/office name.
     */
    public String getVillaName() {
        return apartmentName;
    }

    /**
     * Set the apartment/villa/house/office name.
     *
     * @param villaName Apartment/villa/house/office name
     */
    public void setVillaName(String villaName) {
        this.apartmentName = villaName;
    }

    /**
     * Get the apartment/villa/house/office name.
     *
     * @return Apartment/villa/house/office name.
     */
    public String getHouseName() {
        return apartmentName;
    }

    /**
     * Set the apartment/villa/house/office name.
     *
     * @param houseName Apartment/villa/house/office name
     */
    public void setHouseName(String houseName) {
        this.apartmentName = houseName;
    }

    /**
     * Get the apartment/villa/house/office name.
     *
     * @return Apartment/villa/house/office name.
     */
    public String getOfficeName() {
        return apartmentName;
    }

    /**
     * Set the apartment/villa/house/office name.
     *
     * @param officeName Apartment/villa/house/office name
     */
    public void setOfficeName(String officeName) {
        this.apartmentName = officeName;
    }

    /**
     * Get the building name.
     *
     * @return Building name.
     */
    public String getBuildingName() {
        return buildingName;
    }

    /**
     * Set the building name.
     *
     * @param buildingName Building name to be set
     */
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    /**
     * Get street name.
     *
     * @return Street name.
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * Set street name.
     *
     * @param streetName Street name to set
     */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    /**
     * Get area name.
     *
     * @return Area name.
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * Set area name.
     *
     * @param areaName Area name to set
     */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    /**
     * Get country of this address.
     *
     * @return Country.
     */
    public Country getCountry() {
        return country;
    }
}