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

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link DataSet} using an internal {@link HashMap}.
 *
 * @author Syam
 */
public class HashDataSet implements DataSet {

    private StringList keys;
    private final Map<String, Object> map = new HashMap<>();

    /**
     * Constructor.
     *
     * @param keys Keys.
     */
    public HashDataSet(String... keys) {
        this(StringList.create(keys));
    }

    /**
     * Constructor.
     *
     * @param keys Keys.
     */
    public HashDataSet(StringList keys) {
        this.keys = keys;
    }

    @Override
    public void set(String key, Object value) {
        if(canSet(key)) {
            map.put(key, value);
        }
    }

    @Override
    public boolean canSet(String key) {
        return keys.contains(key);
    }

    @Override
    public StringList keys() {
        return keys;
    }

    @Override
    public Object get(String key) {
        return map.get(key);
    }

    @Override
    public void add(String key) {
        if(keys.contains(key)) {
            return;
        }
        keys = keys.concat(new String[] { key });
    }

    @Override
    public void remove(String key) {
        keys = keys.remove(key);
        map.remove(key);
    }
}