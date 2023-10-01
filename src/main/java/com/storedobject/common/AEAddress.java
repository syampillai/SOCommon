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
 * AE Address<BR>
 * line[0]: Code for the emirate (0: Abu Dhabi, 1: Dubai, 2: Sharjah, 3: Ajman,  4: Um Al Quwain, 5: Ras Al Khaimah, 6: Fujairah)<BR>
 *
 * @author Syam
 */
public final class AEAddress extends Address {

    private static final String[] emirates = new String[] {
            "Abu Dhabi", "Dubai", "Sharjah", "Ajman", "Um Al Quwain", "Ras Al Khaimah", "Fujairah"
    };

    AEAddress() {
    }

    @Override
    boolean parse() throws SOException {
        lines[lines.length - 1] = match(lines[lines.length - 1], emirates);
        return super.parse();
    }

    @Override
    public int getExtraLines() {
        return 1;
    }

    @Override
    String convert(int lineNumber) throws SOException {
        if(lineNumber == (lines.length - 1)) {
            return getEmirateName();
        }
        return super.convert(lineNumber);
    }

    public void setEmirate(int emirate) {
        lines[lines.length - 1] = "" + (emirate % emirates.length);
    }

    public int getEmirate()throws SOException {
        return extractNumber(lines.length - 1);
    }

    public String getEmirateName() {
        return extractName(lines.length - 1, emirates);
    }

    public static String[] getEmirates() {
        return emirates;
    }

    @Override
    int poBoxPosition() {
        return 4;
    }
}