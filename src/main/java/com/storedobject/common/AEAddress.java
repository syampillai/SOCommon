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
 * line[0]: Street number/name (could be blank)<BR>
 * line[1]: Area/District/Community (could be blank)<BR>
 * line[2]: Post Box Number (must be digits)<BR>
 * line[3]: Code for the emirate (0: Abu Dhabi, 1: Dubai, 2: Sharjah, 3: Ajman,  4: Um Al Quwain, 5: Ras Al Khaimah, 6: Fujairah)<BR>
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
    protected boolean parse() {
        lines[lines.length - 2] = "" + extractNumber(lines[lines.length - 2]);
        lines[lines.length - 1] = match(lines[lines.length - 1], emirates);
        return true;
    }

    @Override
    protected String convert(int lineNumber) {
        if(lineNumber == (lines.length - 1)) {
            return getEmirateName();
        }
        if(lineNumber == (lines.length - 2)) {
            if(!lines[lineNumber].isEmpty() && !lines[lineNumber].equals("0")) {
                return "Post Box " + lines[lineNumber];
            } else {
                return null;
            }
        }
        return super.convert(lineNumber);
    }

    public int getEmirate() {
        return extractNumber(lines.length - 1);
    }

    public String getEmirateName() {
        return extractName(lines.length - 1, emirates);
    }

    public static String[] getEmirates() {
        return emirates;
    }
}