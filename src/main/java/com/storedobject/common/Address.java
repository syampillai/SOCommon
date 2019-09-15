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

import java.util.Arrays;

/**
 * Class to check and manipulate mailing address information. Address is a {@link String} containing multiple lines
 * of text (delimited by newline character). A valid address is stored in the following format:<BR>
 * First line: CCdX where<BR>
 * CC = the ISO 2 character country code (Example: IN for India, PK for Pakistan, US for USA)<BR>
 * d = Digit 0, 1 or 2 (0 for Apartment, 1 for Villa, 2 for House, 3 for Office)<BR>
 * X = Apartment/villa/office number/name. Can contain any free text but can't be blank<BR>
 * Second line: Building name (may include floor/level etc.). Mandatory for apartments.<BR>
 * Depending on the country, rest of the lines are interpreted.
 *
 * @author Syam
 */
public abstract class Address {

    private static final String INVALID = "Invalid address - ";
    Country country;
    char apartmentCode;
    String apartmentName, buildingName;
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
        return check(address, allowEmpty, null);
    }

    private static String check(String address, boolean allowEmpty, Address load) throws SOException {
        if(StringUtility.isWhite(address)) {
            if(allowEmpty) {
                return "";
            }
            throw new SOException("Empty address");
        }
        address = address.trim();
        String[] lines = address.split("\\r?\\n");
        StringUtility.trim(lines);
        if(lines.length < 2 || lines[0].length() < 4) {
            throw new SOException(INVALID + address);
        }
        Country country = Country.get(Country.check(lines[0].substring(0, 2)));
        if(load != null) {
            load.country = country;
            load.lines = new String[lines.length - 2];
        }
        char apartmentChar = lines[0].charAt(2);
        switch (apartmentChar) {
            case '0':
                if(lines[1].isEmpty()) {
                    throw new SOException(INVALID + address);
                }
            case '1':
            case '2':
            case '3':
                break;
        }
        if(load != null) {
            load.apartmentCode = apartmentChar;
            load.apartmentName = lines[0].substring(3);
            load.buildingName = lines[1];
        }
        int nonBlank = 0;
        StringBuilder s = new StringBuilder();
        for(int i = 2; i < lines.length; i++) {
            if(load != null) {
                load.lines[i - 2] = lines[i];
            }
            if(lines[i].length() > 0) {
                ++nonBlank;
            }
            s.append(lines[i]).append('\n');
        }
        if(nonBlank >= 0) {
            s.append(country.getName());
            return s.toString();
        }
        throw new SOException(INVALID + address);
    }

    /**
     * Returns if the address is valid or not.
     *
     * @return True or false.
     */
    public boolean isValid() {
        if(!valid) {
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
        valid = copy(address, true);
        return valid;
    }

    private boolean copy(Address address, boolean parse) {
        apartmentCode = address.apartmentCode;
        apartmentName = address.apartmentName;
        buildingName = address.buildingName;
        if(!parse) {
            lines = address.lines;
            return true;
        }
        lines = new String[address.lines.length];
        System.arraycopy(address.lines, 0, lines, 0, lines.length);
        try {
            if(parse()) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        Arrays.fill(lines, "");
        return false;
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
        Address a = new A();
        try {
            check(address, false, a);
        } catch (SOException e) {
            return null;
        }
        Address aa;
        try {
            aa = (Address)a.getClass().getClassLoader().loadClass("com.storedobject.common." + a.country.getShortName() + "Address").newInstance();
        } catch (Throwable e) {
            return null;
        }
        aa.country = a.country;
        aa.copy(a, false);
        aa.apartmentCode = a.apartmentCode;
        aa.lines = a.lines;
        try {
            aa.valid = aa.parse();
        } catch (Throwable ignored) {
        }
        return aa;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        switch (apartmentCode) {
            case '0':
                s.append("Apartment ");
                break;
            case '1':
                s.append("Villa ");
                break;
        }
        s.append(apartmentName).append('\n');
        if(buildingName != null && !buildingName.isEmpty()) {
            s.append(buildingName).append('\n');
        }
        String line;
        for(int i = 0; i < lines.length; i++) {
            line = convert(i);
            if(line != null && !line.isEmpty()) {
                s.append(line).append('\n');
            }
        }
        s.append(country.getName());
        return s.toString();
    }

    protected abstract boolean parse();

    protected String convert(int lineNumber) {
        return lines[lineNumber];
    }

    static int extractNumber(String line) {
        if(StringUtility.isDigit(line)) {
            return Integer.parseInt(line);
        }
        int n = line.length() - 1;
        while(Character.isDigit(line.charAt(n))) {
            --n;
        }
        line = line.substring(n + 1);
        return Integer.parseInt(line);
    }

    static String match(String line, String[] names) {
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
        throw new SORuntimeException();
    }

    static String extractName(String line, String[] names) {
        return names[Integer.parseInt(line)];
    }

    int extractNumber(int lineNumber) {
        return extractNumber(lines[lineNumber]);
    }

    String extractName(int lineNumber, String[] names) {
        return extractName(lines[lineNumber], names);
    }

    private static class A extends Address {

        @Override
        protected boolean parse() {
            return true;
        }
    }

    public static void main(String[] args) {
        System.err.println(Address.create("AE0105\nAl Ghaf 1B\n\nEmaar Greens\nPost Box 3306\nabu dhubi"));
        System.err.println(Address.create("IN113\n\nNoel Ivy Creek\nChittethukara\n682037\nErnakulam\nKeraa"));
    }
}