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

/**
 * CH Address<BR>
 * line[0]: Post Code (4 digit numeric)<BR>
 * line[1]: Post Box Number (must be digits, 0 means no PO Box)<BR>
 *
 * @author Syam
 */
public class CHAddress extends POBoxAddress {

    CHAddress() {
    }

    @Override
    boolean parse() throws SOException {
        if(streetName.isEmpty()) {
            throw new SOException("Street name");
        }
        if(buildingName.isEmpty()) {
            throw new SOException("Building number");
        }
        if(areaName.isEmpty()) {
            throw new SOException("Place name");
        }
        int postalCode = getPostalCode();
        if(postalCode < 1000 || postalCode > 9999) {
            throw new SOException("Postal code");
        }
        lines[lines.length - 2] = "" + postalCode;
        return super.parse();
    }

    @Override
    protected int getLineCount() {
        return 2;
    }

    @Override
    protected int getReservedLines() {
        return 2;
    }

    @Override
    protected String convert(int lineNumber) throws SOException {
        if(lineNumber == (lines.length - 2)) {
            if(!lines[lineNumber].isEmpty() && !lines[lineNumber].equals("0")) {
                return lines[lineNumber];
            } else {
                return null;
            }
        }
        return super.convert(lineNumber);
    }

    public void setPostalCode(int postalCode) {
        lines[lines.length - 2] = "" + postalCode;
    }

    public int getPostalCode() throws SOException {
        return extractNumber(lines[lines.length - 2]);
    }

    @Override
    public String toString() {
        String s = apartmentName + "\n" + streetName + " " + buildingName + "\n";
        int po;
        try {
            po = getPOBox();
        } catch (SOException e) {
            po = 0;
        }
        if(po > 0) {
            s += "Postfach " + po + "\n";
        }
        try {
            s += getPostalCode();
        } catch (SOException e) {
            s += "????";
        }
        return s + " " + areaName + "\n" + country.getName();
    }
}
