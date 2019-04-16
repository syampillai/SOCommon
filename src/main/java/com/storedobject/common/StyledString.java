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

public class StyledString implements StyledBuilder {

    private StringBuilder s = new StringBuilder();

    @Override
    public StyledBuilder append(Object object) {
        s.append(object);
        return this;
    }

    @Override
    public boolean isEmpty() {
        return s.length() == 0;
    }

    @Override
    public StyledBuilder newLine(boolean forceIt) {
        if(!forceIt) {
            if(isEmpty() || s.charAt(s.length() - 1) == '\n') {
                return this;
            }
        }
        s.append('\n');
        return this;
    }

    public String toString() {
        return s.toString();
    }

    @Override
    public StyledBuilder clearContent() {
        s = new StringBuilder();
        return this;
    }
}