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
 * An interface that defines some sort of data with attributes. Attribute values is gettable and optionally settable.
 *
 * @author Syam
 */
public interface DataSet {

    /**
     * Set an attribute value.
     *
     * @param key Key of the attribute.
     * @param value Value to set.
     */
    void set(String key, Object value);

    /**
     * Check whether an attribute can be set or not. It is not mandatory that an attribute is settable.
     *
     * @param key Key of the attribute.
     * @return True/false.
     */
    boolean canSet(String key);

    /**
     * Get the value for the given attribute.
     *
     * @param key Key of the attribute.
     * @return Value of the attribute.
     */
    Object get(String key);

    /**
     * Get the keys a list.
     *
     * @return Keys.
     */
    StringList keys();

    /**
     * Add an attribute key.
     *
     * @param key Key to be added.
     */
    void add(String key);

    /**
     * Remove an attribute key.
     *
     * @param key Key to be removed.
     */
    void remove(String key);

    /**
     * Get the value-type of the given attribute.
     *
     * @param key Key of the attribute.
     * @return Value-type of the attribute.
     */
    default Class<?> getType(String key) {
        Object v = get(key);
        return v == null ? null : v.getClass();
    }
}