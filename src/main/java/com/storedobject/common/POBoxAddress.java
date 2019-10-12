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
 * P O Box based address.<BR>
 * line[last]: Post Box Number (must be digits, 0 means no PO Box)<BR>
 *
 * @author Syam
 */
public abstract class POBoxAddress extends Address {

    POBoxAddress() {
    }

    @Override
    boolean parse() throws SOException {
        lines[lines.length - 1] = "" + getPOBox();
        return true;
    }

    @Override
    String convert(int lineNumber) throws SOException {
        if(lineNumber == (lines.length - 1)) {
            if(!lines[lineNumber].isEmpty() && !lines[lineNumber].equals("0")) {
                return "Post Box " + lines[lineNumber];
            } else {
                return null;
            }
        }
        return super.convert(lineNumber);
    }

    public void setPOBox(int poBox) {
        lines[lines.length - 1] = "" + poBox;
    }

    public int getPOBox() throws SOException {
        return extractNumber(lines[lines.length - 1]);
    }
}
