/*
 * Copyright (c) 2018-2021 Syam Pillai
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

import java.util.regex.Pattern;

public class GBAddress extends Address {

    private final int POS_POST_TOWN = 1;
    private final Pattern shortPC = Pattern.compile("^[A-Z]{1,2}[0-9][A-Z0-9]? ?[0-9][A-Z]{2}$"),
            longPC = Pattern.compile("^(([A-Z]{1,2}[0-9][A-Z0-9]?|ASCN|STHL|TDCU|BBND|[BFS]IQQ|PCRN|TKCA) " +
                    "?[0-9][A-Z]{2}|BFPO ?[0-9]{1,4}|(KY[0-9]|MSR|VG|AI)[ -]?[0-9]{4}|[A-Z]{2} ?[0-9]{2}|GE " +
                    "?CX|GIR ?0A{2}|SAN ?TA1)$");

    GBAddress() {
    }

    @Override
    boolean parse() throws SOException {
        if(getPostTown().isBlank()) {
            throw new SOException("Blank Post Town");
        }
        return true;
    }

    @Override
    public int getPostalCodeMaxLength() {
        return 8;
    }

    @Override
    public int getPostalCodeMinLength() {
        return 5; // Actually 6 but we insert a space if required while checking
    }

    @Override
    boolean checkPostalCode() {
        postalCode = postalCode.toUpperCase().trim();
        while(postalCode.contains("  ")) {
            postalCode = postalCode.replace("  ", " ");
        }
        if(!postalCode.contains(" ")) {
            int n = postalCode.length() - 3;
            if(n > 0) {
                postalCode = postalCode.substring(0, n) + ' ' + postalCode.substring(n);
            }
        }
        if(postalCode.isEmpty()) {
            return false;
        }
        return shortPC.matcher(postalCode).matches() || !longPC.matcher(postalCode).matches();
    }

    @Override
    public int getExtraLines() {
        return 1;
    }

    public String getPostTown() {
        String s = lines[lines.length - POS_POST_TOWN];
        return s == null ? "" : s;
    }

    public void setPostTown(String postTown) {
        lines[lines.length - POS_POST_TOWN] = postTown.toUpperCase().trim();
    }

    @Override
    int postalCodePosition() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getPostalCodeCaption() {
        return "Postcode";
    }

    @Override
    public boolean isNumericPostalCode() {
        return false;
    }

    @Override
    public String getAreaCaption() {
        return "Locality";
    }
}
