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
 * Interface to denote that a class has got "string filling" capability.
 * Please see {@link StringUtility#fill(String, StringFiller)}.
 *
 * @author Syam
 */
public interface StringFiller {

    default String fill(String name) {
        try {
            return StringUtility.stringify(getClass().getMethod("get" + name, (Class<?>[])null).invoke(this, (Object[])null));
        } catch (Throwable ignore) {
        }
        return "?";
    }
}