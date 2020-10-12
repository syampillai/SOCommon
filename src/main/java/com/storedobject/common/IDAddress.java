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
 * ID Address<BR>
 * line[0]: City/Town (required)<BR>
 * line[1]: Province (optional)<BR>
 *
 * @author Syam
 */
public class IDAddress extends XXAddress {

    IDAddress() {
    }

    @Override
    boolean checkPostalCode() {
        return postalCode >= 10000 && postalCode <= 99999;
    }

    @Override
    boolean parse() throws SOException {
        if(getLine(0).isEmpty()) {
            throw new SOException("Can't be empty - " + getLineCaption(0));
        }
        return super.parse();
    }

    @Override
    protected String convert(int lineNumber) throws SOException {
        if(lineNumber == 0) {
            return "";
        }
        return super.convert(lineNumber);
    }

    @Override
    int postalCodePosition() {
        return 4;
    }

    @Override
    String postalCodePrefix() {
        return getLine(0) + " ";
    }

    @Override
    public String getLineCaption(int index) {
        switch(index) {
            case 0:
                return "City/Town";
            case 1:
                return "Province";
        }
        return null;
    }
}
