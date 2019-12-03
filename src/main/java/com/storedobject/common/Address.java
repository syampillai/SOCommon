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
 * Depending on the country, rest of the lines are interpreted.<BR>
 * Generic address is formatted as follows:<BR>
 * Line 0: Apartment/Villa/House XXX<BR>
 * Line 1: Building Name<BR>
 * Line 2: Street Name<BR>
 * Line 3: Area/Community Name<BR>
 * Line 4: Country Name<BR>
 *
 * @author Syam
 */
public abstract class Address {

    private static final String INVALID = "Invalid address - ";
    Country country;
    char apartmentCode = '0';
    String apartmentName, buildingName, streetName, areaName;
    int poBox, postalCode;
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
                a.apartmentCode = '0';
                error(address, "First line, unknown code: [" + a.apartmentCode + "]");
        }
        a.setApartmentName(lines[0].substring(3));
        a.setBuildingName(lines[1]);
        a.setStreetName(lines[2]);
        a.setAreaName(lines[3]);
        if(lines.length > 4) {
            try {
                a.poBox = Integer.parseInt(lines[4]);
            } catch (Throwable e) {
                a.poBox = 0;
            }
            a.setPOBox(a.poBox);
        }
        if(lines.length > 5) {
            try {
                a.postalCode = Integer.parseInt(lines[5]);
            } catch (Throwable e) {
                a.postalCode = 0;
            }
            a.setPostalCode(a.postalCode);
        }
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
        try {
            a.parse();
            if(!a.checkPostalCode()) {
                a.valid = false;
                error(address, a.getPostalCodeCaption());
            }
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
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public final boolean isValid() {
        if(!valid) {
            valid = true;
            if(areaName == null) {
                setAreaName("");
            }
            if(streetName == null) {
                setStreetName("");
            }
            if(buildingName == null) {
                setBuildingName("");
            }
            if(apartmentName == null) {
                setApartmentName("");
            }
            try {
                valid = valid && checkPostalCode() && parse();
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
        valid = true;
        setApartmentCode(address.apartmentCode);
        setApartmentName(address.apartmentName);
        setBuildingName(address.buildingName);
        setStreetName(address.streetName);
        setAreaName(address.areaName);
        setPOBox(address.poBox);
        setPostalCode(address.postalCode);
        int n = getExtraLines(), m = address.lines.length;
        lines = new String[n];
        while (n > 0) {
            --n;
            --m;
            try {
                lines[n] = m >= 0 ? address.convert(m) : "";
            } catch (Throwable e) {
                lines[n] = "";
            }
        }
        try {
            valid = valid && checkPostalCode() && parse();
        } catch (Throwable ignored) {
            valid = false;
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
        a.lines = new String[a.getExtraLines()];
        Arrays.fill(a.lines, "");
        a.valid = true;
        return a;
    }

    /**
     * Get address as a list of lines (Country line is not included).
     *
     * @return List of address lines (without country name).
     */
    private List<String> toStrings() {
        ArrayList<String> slines = new ArrayList<>();
        boolean v = valid;
        valid = false;
        if(!isValid() || !v) {
            v = false;
            slines.add("[Not a valid address]");
        }
        valid = v;
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
        String line = apartmentName(s);
        slines.add(line);
        if(apartmentCode == '1' || apartmentCode == '2') {
            slines.add(streetName());
            slines.add(buildingName());
        } else {
            slines.add(buildingName());
            slines.add(streetName());
        }
        slines.add(areaName());
        for(int i = 0; i < lines.length; i++) {
            if(valid) {
                try {
                    line = convert(i);
                } catch (Throwable error) {
                    line = "?";
                }
                slines.add(line == null ? "" : line);
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
     * @param personName Name of the person to be printed (Name should contain title as the first word - Example: Mr. Tim John).
     *                   <code>null</code> could be passed if no name to be printed.
     * @return Line-feed delimited string that can be printed or displayed.
     */
    private String toString(List<String> lines, String personName) {
        StringBuilder s = new StringBuilder();
        if(personName != null) {
            if(apartmentCode <= '2') { // Not an office
                if(splitNameTitle() && personName.contains(" ")) {
                    int p = personName.indexOf(' ');
                    s.append(personName, 0, p).append('\n');
                    s.append(personName.substring(p + 1).trim());
                } else {
                    s.append(personName);
                }
                s.append('\n');
                personName = null;
            }
        }
        String line;
        int pbPos = poBoxPosition(), pinPos = postalCodePosition();
        for(int i = 0; i < lines.size(); i++) {
            if(personName != null && i == 1) {
                s.append(personName).append('\n');
            }
            if(poBox > 0 && pbPos >= 0 && i >= pbPos) {
                pbPos = Integer.MIN_VALUE;
                s.append(getPOBoxName()).append(' ').append(poBox).append('\n');
            }
            if(postalCode > 0 && pinPos >= 0 && i >= pinPos) {
                pinPos = Integer.MIN_VALUE;
                s.append(postalCodePrefix()).append(postalCode).append(postalCodeSuffix()).append('\n');
            }
            line = lines.get(i);
            if(line != null && !line.isEmpty()) {
                s.append(line).append('\n');
            }
        }
        if(poBox > 0 && pbPos > 0) {
            s.append(getPOBoxName()).append(' ').append(poBox).append('\n');
        }
        if(postalCode > 0 && pinPos > 0) {
            s.append(postalCodePrefix()).append(postalCode).append(postalCodeSuffix()).append('\n');
        }
        if(country != null) {
            s.append(country.getName()).append('\n');
        }
        return s.toString();
    }

    @Override
    public final String toString() {
        return toString(null);
    }

    public String toString(String personName) {
        return toString(toStrings(), personName);
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
        s.append(streetName).append('\n').append(areaName).append('\n').append(poBox).append('\n').append(postalCode);
        for(String line: lines) {
            s.append('\n').append(line == null ? "" : line);
        }
        return s.toString();
    }

    boolean parse() throws SOException {
        return true;
    }

    public int getExtraLines() {
        return 0;
    }

    public int getReservedLines() {
        return 0;
    }

    String convert(int lineNumber) throws SOException {
        return lines[lineNumber];
    }

    static int extractNumber(String line) throws SOException {
        if(StringUtility.isDigit(line)) {
            return Integer.parseInt(line);
        }
        if(line.isEmpty()) {
            return 0;
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
        return lineNumber >= 0 && lineNumber < lines.length ? lines[lineNumber] : null;
    }

    /**
     * Set an address line.
     *
     * @param lineNumber Line number
     * @param line Value to be set
     */
    public void setLine(int lineNumber, String line) {
        if(lineNumber >= 0 && lineNumber < lines.length) {
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
            valid = false;
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

    public final boolean isPOBoxAddress() {
        return poBoxPosition() >= 0;
    }

    public final void setPOBox(int poBox) {
        this.poBox = poBox;
    }

    public final int getPOBox() {
        return poBox;
    }

    public final boolean isPostalCodeAddress() {
        return postalCodePosition() >= 0;
    }

    public final int getPostalCode() {
        return postalCode;
    }

    public final void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
        valid = valid && checkPostalCode();
    }

    int postalCodePosition() {
        return -1;
    }

    public String getPostalCodeCaption() {
        return "Postal Code";
    }

    String postalCodePrefix() {
        return getPostalCodeCaption() + " ";
    }

    String postalCodeSuffix() {
        return "";
    }

    boolean checkPostalCode() {
        return true;
    }

    public String getPOBoxName() {
        return "Post Box";
    }

    int poBoxPosition() {
        return -1;
    }

    boolean splitNameTitle() {
        return false;
    }

    String apartmentName(String prefix) {
        return prefix + apartmentName;
    }

    String buildingName() {
        return buildingName;
    }

    String streetName() {
        return streetName;
    }

    String areaName() {
        return areaName;
    }

    public String getAreaCaption() {
        return "Area";
    }
}