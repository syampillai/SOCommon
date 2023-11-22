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
import java.util.stream.Collectors;

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

    private static final StringList types = StringList.create("Apartment", "Villa", "House", "Office", "Flat", "Suite");
    private static final String INVALID = "Invalid address - ";
    /**
     * Country of the address.
     */
    Country country;
    /**
     * Apartment code. '0' - Apartment, '1' - Villa, '2' - House, '3' - Office, '4' - Flat, '5' - Suite
     */
    char apartmentCode = '0';
    /**
     * Apartment name.
     */
    String apartmentName;
    /**
     * Building name.
     */
    String buildingName;
    /**
     * Street name.
     */
    String streetName;
    /**
     * Area name.
     */
    String areaName;
    /**
     * Post Box number
     */
    int poBox;
    /**
     * Postal code.
     */
    String postalCode;
    /**
     * Extra ines in the address.
     */
    String[] lines;
    /**
     * Whether the address is valid or not.
     */
    boolean valid;

    /**
     * Constructor.
     */
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
        try {
            if(StringUtility.isWhite(address)) {
                if(allowEmpty) {
                    return "";
                }
                throw new SOException("Empty address");
            }
            Address a = checkAddress(address, true);
            if(!a.valid) {
                throw new SOException(INVALID + address);
            }
            return a.encode();
        } catch(SOException eee) {
            eee.printStackTrace();
            throw  eee;
        }
    }

    private static void error(String address, String error) throws SOException {
        String err = INVALID + address;
        if(error != null) {
            err += " (" + error + ")";
        }
        throw new SOException(err);
    }

    private static Address checkAddress(String address, boolean parse) throws SOException {
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
        a.apartmentName = lines[0].substring(3).trim();
        switch (a.apartmentCode) {
            case '0':
                if(lines[1].isEmpty() && StringUtility.isDigit(a.apartmentName)) {
                    error(address, "Building name missing");
                }
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
                break;
            default:
                a.apartmentCode = '0';
                error(address, "First line, unknown type: [" + a.apartmentCode + "]");
        }
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
            a.postalCode = lines[5];
            a.setPC(a.postalCode);
        }
        int m = 6;
        for(int i = 0; i < a.lines.length; i++) {
            a.lines[i] = m < lines.length ? lines[m] : "";
            ++m;
        }
        try {
            a.parse();
            if(!a.checkPC()) {
                a.valid = false;
                if(parse) {
                    error(address, a.getPostalCodeCaption());
                }
            }
        } catch (SOException soerror) {
            a.valid = false;
            if(parse) {
                error(address, soerror.getMessage());
            }
        } catch (Throwable error) {
            a.valid = false;
            if(parse) {
                throw new SOException(INVALID + address, error);
            }
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
        try {
            validate();
        } catch (Throwable ignored) {
        }
        return valid;
    }

    /**
     * Validate the address.
     *
     * @throws Exception if invalid.
     */
    public final void validate() throws Exception {
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
            apartmentName = "";
        }
        if(postalCode == null) {
            postalCode = "";
        }
        if(apartmentName.isBlank()) {
            throw new SOException("Blank " + getTypeValue() + " Number/Name");
        }
        valid = parse();
        if(!valid) {
            throw new SOException("Invalid address");
        }
        if(!checkPC()) {
            valid = false;
            throw new SOException("Invalid " + getPostalCodeCaption() + " (" + postalCode + ")");
        }
    }

    /**
     * Copy details from the given address. Country will not be copied.
     *
     * @param address Address from which details to be copied.
     * @return True if the address is valid after copying details.
     */
    public boolean copy(Address address) {
        valid = true;
        setApartmentName(address.apartmentName);
        setType(address.apartmentCode);
        setBuildingName(address.buildingName);
        setStreetName(address.streetName);
        setAreaName(address.areaName);
        setPOBox(address.poBox);
        setPC(address.postalCode);
        lines = new String[getExtraLines()];
        for(int i = 0; i < lines.length; i++) {
            lines[i] = i < address.lines.length ? address.lines[i] : "";
        }
        try {
            valid = valid && parse() && checkPC();
        } catch (Throwable e) {
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
            return checkAddress(address, false);
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
            a = (Address)Address.class.getClassLoader().loadClass("com.storedobject.common."
                            + country.getShortName() + "Address")
                    .getDeclaredConstructor().newInstance();
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
        String s = switch(apartmentCode) {
            case '2', '3' -> "";
            default -> getTypeValue() + " ";
        };
        boolean streetNameFirst = streetNameFirst();
        if(streetNameFirst) {
            slines.add(street());
        }
        String line = apartmentName(s);
        boolean separateBuilding = separateBuildingLine();
        String bn = buildingName();
        if(!separateBuilding && !bn.isBlank()) {
            line = (line + ", " + bn).trim();
        }
        slines.add(line);
        String sn = street();
        if(separateBuilding) {
            if(streetNameFirst) {
                slines.add(bn);
            } else if(apartmentCode == '1' || apartmentCode == '2') { // Office or house
                slines.add(sn);
                slines.add(bn);
            } else {
                slines.add(bn);
                slines.add(sn);
            }
        } else {
            if(!streetNameFirst) {
                slines.add(sn);
            }
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

    private String toString(List<String> lines, String personName) {
        ArrayList<String> list = new ArrayList<>();
        if(personName != null) {
            if(apartmentCode != '3') { // Not an office
                if(splitNameTitle() && personName.contains(" ")) {
                    int p = personName.indexOf(' ');
                    list.add(personName.substring(0, p));
                    list.add(personName.substring(p + 1).trim());
                } else {
                    list.add(personName);
                }
                personName = null;
            }
        }
        String line;
        int pbPos = poBoxPosition(), pinPos = postalCodePosition();
        for(int i = 0; i < lines.size(); i++) {
            if(personName != null && i == 1) {
                list.add(personName);
            }
            if(poBox > 0 && pbPos >= 0 && i >= pbPos) {
                pbPos = Integer.MIN_VALUE;
                String pb = poBoxPrefix();
                if(!pb.isEmpty()) {
                    pb += " ";
                }
                list.add(pb + poBox + poBoxSuffix());
            }
            if(!postalCode.isEmpty() && pinPos >= 0 && i >= pinPos) {
                pinPos = Integer.MIN_VALUE;
                list.add(postalCodePrefix() + postalCode() + postalCodeSuffix());
            }
            line = lines.get(i);
            if(line != null && !line.isEmpty()) {
                list.add(line);
            }
        }
        if(poBox > 0 && pbPos > 0) {
            String pb = poBoxPrefix();
            if(!pb.isEmpty()) {
                pb += " ";
            }
            list.add(pb + poBox + poBoxSuffix());
        }
        if(!postalCode.isEmpty() && pinPos > 0) {
            list.add(postalCodePrefix() + postalCode() + postalCodeSuffix());
        }
        if(country != null) {
            list.add(country.getName());
        }
        arrangeLines(list);
        return list.stream().filter(l -> l != null && !l.isEmpty()).collect(Collectors.joining("\n"));
    }

    /**
     * Convert the address into a human-readable String representation.
     *
     * @return Human-readable String representation with required "new lines".
     */
    @Override
    public final String toString() {
        return toString(null);
    }

    /**
     * Same as {@link #toString()} method but the name passed is included as the first line in the case of
     * personal address or second name in the case of office addresses.
     * .
     * @param personName Person name to be included
     * @return Human-readable String representation with required "new lines".
     */
    public String toString(String personName) {
        String s = toString(toStrings(), personName);
        while(s.endsWith("\n")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    /**
     * This method is invoked for arranging the address lines to be displayed.
     *
     * @param lines Address lines.
     */
    protected void arrangeLines(ArrayList<String> lines) {
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

    /**
     * Parse the address for the extra lines part.
     *
     * @return True if successful.
     * @throws SOException If any errors.
     */
    boolean parse() throws SOException {
        return true;
    }

    /**
     * Get the number of extra lines.
     *
     * @return Number of extra lines in the address.
     */
    public int getExtraLines() {
        return 0;
    }

    /**
     * Get a specific line from the extra lines of the address for printing/displaying.
     *
     * @param lineNumber Line number
     * @return Line.
     * @throws SOException If an error occurs.
     */
    String convert(int lineNumber) throws SOException {
        return lines[lineNumber];
    }

    /**
     * A method to extract a number contained in a line.
     *
     * @param line Line to parse
     * @return Number got. Empty line returns 0.
     * @throws SOException If line doesn't contain a number.
     */
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

    /**
     * Find the best possible element from the string array that matches with the 'line'.
     *
     * @param line Line to match
     * @param names Elements array to match
     * @return Element that is matched.
     * @throws SOException If any error occurs.
     */
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

    /**
     * Extract an element from the string array.
     *
     * @param index Index (as a string)
     * @param names String array
     * @return Element from the array.
     */
    static String extractName(String index, String[] names) {
        return names[Integer.parseInt(index)];
    }

    /**
     * Extract number from a specific line.
     *
     * @param lineNumber Line number
     * @return Number extracted from the line.
     * @throws SOException If error occurs.
     */
    int extractNumber(int lineNumber) throws SOException {
        return extractNumber(lines[lineNumber]);
    }

    /**
     * Extract an element from the given array (indirect-index via extra lines).
     *
     * @param lineNumber Line number that has an index into the extra lines of the address
     * @param names String array
     * @return Extracted element.
     */
    String extractName(int lineNumber, String[] names) {
        return extractName(lines[lineNumber], names);
    }

    /**
     * Get a line (extra line) from the address.
     *
     * @param lineNumber Line number
     * @return Line if exists, else null.
     */
    public String getLine(int lineNumber) {
        return lineNumber >= 0 && lineNumber < lines.length ? lines[lineNumber] : null;
    }

    /**
     * Set an address line (extra line).
     *
     * @param lineNumber Line number.
     * @param line Value to be set.
     */
    public void setLine(int lineNumber, String line) {
        if(lineNumber >= 0 && lineNumber < lines.length) {
            lines[lineNumber] = line;
        }
    }

    /**
     * Get the type code value.
     *
     * @return Type code value.
     */
    public String getTypeValue() {
        return getTypeValue(apartmentCode);
    }


    /**
     * Get the type code value for the given type.
     *
     * @return Type code value.
     */
    public static String getTypeValue(char type) {
        int i = type - '0';
        return i < 0 || i > types.size() ? null : types.get(i);
    }

    /**
     * Get the type of addresses.
     *
     * @return Address types.
     */
    public static StringList getTypeValues() {
        return types;
    }

    /**
     * Get the type code.
     *
     * @return Type code.
     */
    public char getType() {
        return apartmentCode;
    }

    /**
     * Set the type code.
     *
     * @param type Type code
     */
    public void setType(char type) {
        if(type < '0' || type > '5') {
            valid = false;
            return;
        }
        this.apartmentCode = type;
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
        apartmentCode = '1';
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
        apartmentCode = '2';
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
        apartmentCode = '3';
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
        this.buildingName = buildingName == null ? "" : buildingName.trim();
    }

    /**
     * Whether to have building name in a separate line or not. By default, it will be
     * int the same line where apartment/villa/office number/name appears.
     *
     * @return True/false
     */
    boolean separateBuildingLine() {
        return false;
    }

    /**
     * Whether to have street name shown before apartment/villa/office or not.
     * @return True/false.
     */
    boolean streetNameFirst() {
        return false;
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
        this.streetName = streetName == null ? null : streetName.trim();
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
        this.areaName = areaName == null ? "" : areaName;
    }

    /**
     * Get country of this address.
     *
     * @return Country.
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Check if this is a Post Box based address.
     *
     * @return True or false.
     */
    public final boolean isPOBoxAddress() {
        return poBoxPosition() >= 0;
    }

    /**
     * Set the Post Box number.
     *
     * @param poBox Post Box number
     */
    public final void setPOBox(int poBox) {
        this.poBox = poBox;
    }

    /**
     * Get the Post Box number.
     *
     * @return Post Box number.
     */
    public final int getPOBox() {
        return poBox;
    }

    /**
     * Prefix to be used when printing the PO box.
     *
     * @return By default, {@link #getPOBoxName()} is returned.
     */
    public String poBoxPrefix() {
        return getPOBoxName();
    }

    /**
     * Suffix to be used when printing the PO box.
     *
     * @return By default, an empty string is returned.
     */
    public String poBoxSuffix() {
        return "";
    }

    /**
     * Check if this is a Postal Code based address.
     *
     * @return True or false.
     */
    public final boolean isPostalCodeAddress() {
        return postalCodePosition() >= 0;
    }

    /**
     * Get the Postal Code.
     *
     * @return Postal Code.
     */
    public final String getPostalCode() {
        return postalCode;
    }

    /**
     * Set the Postal Code.
     *
     * @param postalCode Postal Code
     */
    public final void setPostalCode(String postalCode) {
        setPC(postalCode);
        valid = valid && checkPC();
    }

    private void setPC(String postalCode) {
        this.postalCode = postalCode == null ? "" : postalCode.trim().toUpperCase();
        if(this.postalCode.chars().allMatch(c -> c == '0')) {
            this.postalCode = "";
        }
    }

    /**
     * Get the position of the Postal Code in the extra lines. Return -1 if this is not a postal code based address.
     *
     * @return Position.
     */
    int postalCodePosition() {
        return -1;
    }

    /**
     * Get the caption for the Postal Code.
     *
     * @return Default implementation returns "Postal Code".
     */
    public String getPostalCodeCaption() {
        return "Postal Code";
    }

    /**
     * Get the prefix of the Postal Code line.
     *
     * @return Default implementation uses the result from {@link #getPostalCodeCaption()} followed by a space.
     */
    String postalCodePrefix() {
        return getPostalCodeCaption() + " ";
    }

    /**
     * Whether the postal code is numeric or not.
     *
     * @return True if numeric. False if alpha-numeric.
     */
    public boolean isNumericPostalCode() {
        return true;
    }

    /**
     * Is postal code mandatory?
     *
     * @return True/false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isPostalCodeMandatory() {
        return isPostalCodeAddress();
    }

    /**
     * What is the minimum length of the postal code?
     *
     * @return Minimum required length of the postal code.
     */
    public int getPostalCodeMinLength() {
        int max = getPostalCodeMaxLength();
        return max == Integer.MAX_VALUE ? 1 : max;
    }

    /**
     * What is the maximum length of the postal code?
     *
     * @return Maximum required length of the postal code.
     */
    public int getPostalCodeMaxLength() {
        return Integer.MAX_VALUE;
    }

    /**
     * Get the suffix of the Postal Code line.
     *
     * @return Default implementation returns an empty string.
     */
    String postalCodeSuffix() {
        return "";
    }

    /**
     * Validate the Postal Code. (This will be invoked for Postal Code based addresses only).
     *
     * @return True if valid.
     */
    boolean checkPostalCode() {
        return true;
    }

    String postalCode() {
        String pc = postalCode;
        while(pc.startsWith("0")) {
            pc = pc.substring(1);
        }
        return pc;
    }

    private boolean checkPC() {
        if(postalCodePosition() < 0) { // Not a postal code based address
            return true;
        }
        if(postalCode == null) {
            postalCode = "";
        }
        if(postalCode.isEmpty()) {
            return !isPostalCodeMandatory();
        }
        if(postalCode.length() < getPostalCodeMinLength() || postalCode.length() > getPostalCodeMaxLength()) {
            return false;
        }
        if(isNumericPostalCode() && !StringUtility.isDigit(postalCode)) {
            return false;
        }
        return checkPostalCode();
    }

    /**
     * Get the caption for the Post Box.
     *
     * @return Default implementation returns "Post Box".
     */
    public String getPOBoxName() {
        return "Post Box";
    }

    /**
     * Get the position of the Post Box in the extra lines.
     *
     * @return Position.
     */
    int poBoxPosition() {
        return -1;
    }

    /**
     * Check whether "title" of the name should be shown in a separate line or not.
     *
     * @return True or false.
     */
    boolean splitNameTitle() {
        return false;
    }

    /**
     * Get the apartment name prefixed with the given prefix.
     *
     * @param prefix Prefix
     * @return Prefixed value.
     */
    String apartmentName(String prefix) {
        return prefix + apartmentName;
    }

    /**
     * Get the building name.
     *
     * @return Building name.
     */
    String buildingName() {
        return buildingName;
    }

    /**
     * Get the street name.
     *
     * @return Street name.
     */
    String streetName() {
        return streetName;
    }

    private String street() {
        String s = streetName();
        if(s == null || s.isBlank()) {
            return "";
        }
        return StringUtility.isDigit(s) ? "Street " + s : s;
    }

    /**
     * Get area name.
     *
     * @return Area name.
     */
    String areaName() {
        return areaName;
    }

    /**
     * Get the caption for the "area" line.
     *
     * @return Default implementation returns "Area".
     */
    public String getAreaCaption() {
        return "Area";
    }
}