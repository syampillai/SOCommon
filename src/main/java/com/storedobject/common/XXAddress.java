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
 * Generic address.
 *
 * @author Syam
 */
public class XXAddress extends Address {

    XXAddress() {
    }

    @Override
    boolean parse() throws SOException {
        return true;
    }

    @Override
    public int getExtraLines() {
        return 2;
    }

    /**
     * Get the caption for the extra line. If returned <code>null</code>, the line value will be ignored by the UI.
     *
     * @param index Index.
     * @return Caption for the extra line.
     */
    public String getLineCaption(int index) {
        int n = getExtraLines();
        if(index < 0 || index >= n) {
            return null;
        }
        return "Address Line (" + (index + 1) + ")";
    }
}
