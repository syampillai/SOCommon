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
 * SG Address<BR>
 * line[0]: Post office (required if Post Box is specified)<BR>
 *
 * @author Syam
 */
public class SGAddress extends XXAddress {

    SGAddress() {
    }

    @Override
    public int getExtraLines() {
        return 1;
    }

    @Override
    boolean parse() throws SOException {
        if(poBox > 0 && getLine(0).isEmpty()) {
            throw new SOException("Can't be empty when Post Box is specified - " + getLineCaption(0));
        }
        return super.parse();
    }

    @Override
    String areaName() {
        return "";
    }

    @Override
    String postalCodePrefix() {
        return getAreaName() + " ";
    }

    @Override
    boolean checkPostalCode() {
        return postalCode >= 100000 && postalCode <= 999999;
    }

    @Override
    int poBoxPosition() {
        return 2;
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
        return 3;
    }

    @Override
    public String poBoxPrefix() {
        return getLine(0) + " PO Box";
    }

    @Override
    public String getLineCaption(int index) {
        return index == 0 ? "Post Office" : null;
    }
}
