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
            throw new SOException("Street name");
        }
        if(buildingName.isEmpty()) {
            throw new SOException("Building number");
        }
        if(areaName.isEmpty()) {
            throw new SOException("Place name");
        }
        return super.parse();
    }

    @Override
    public void setStreetName(String streetName) {
        super.setStreetName(streetName);
        valid = valid && !streetName.isEmpty();
    }

    @Override
    public void setBuildingName(String buildingName) {
        super.setBuildingName(buildingName);
        valid = valid && !buildingName.isEmpty();
    }

    @Override
    public void setAreaName(String areaName) {
        super.setAreaName(areaName);
        valid = valid && !areaName.isEmpty();
    }

    @Override
    boolean checkPostalCode() {
        return postalCode >= 1000 && postalCode <= 9999;
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
        return 2;
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
        String s = streetName + " " + buildingName();
        if(!apartmentName.isEmpty()) {
            s += Character.isDigit(apartmentName.charAt(0)) ? "/" : " ";
            s += apartmentName;
        }
        return s;
    }

    @Override
    String buildingName() {
        return "";
    }

    @Override
    String streetName() {
        return "";
    }

    @Override
    String areaName() {
        return "";
    }

    @Override
    public String getPlaceCaption() {
        return "Place Name";
    }
}
