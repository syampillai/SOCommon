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
 *
 * @author Syam
 */
public class CHAddress extends Address {

    CHAddress() {
    }

    @Override
    boolean parse() throws SOException {
        if(streetName.isEmpty()) {
            throw new SOException("Street Name/Number");
        }
        if(areaName.isEmpty()) {
            throw new SOException(getAreaName());
        }
        return super.parse();
    }

    @Override
    public int getPostalCodeMaxLength() {
        return 4;
    }

    @Override
    int poBoxPosition() {
        return 2;
    }

    @Override
    public String getPOBoxName() {
        return "Postfach";
    }

    @Override
    int postalCodePosition() {
        return 3;
    }

    @Override
    String postalCodePrefix() {
        return "";
    }

    @Override
    String postalCodeSuffix() {
        return " " + areaName;
    }

    @Override
    String apartmentName(String prefix) {
        return "";
    }

    @Override
    String streetName() {
        String s = streetName;
        if(!apartmentName.isEmpty()) {
            s += "/" + apartmentName;
        }
        return s;
    }

    @Override
    String areaName() {
        return "";
    }

    @Override
    public String getAreaCaption() {
        return "Place Name";
    }

    @Override
    boolean splitNameTitle() {
        return true;
    }
}
