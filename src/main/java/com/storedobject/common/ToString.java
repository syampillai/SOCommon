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

import java.util.function.Function;

/**
 * An interface that converts some type of object to a {@link String}.
 *
 * @param <T> Type of object.
 * @author Syam
 */
public interface ToString<T> extends Function<T, String> {

    /**
     * Convert the object instance to a {@link String}.
     *
     * @param object Object to convert.
     * @return Default implementation returns {@link ToString#apply(Object)}.
     */
    default String toString(T object) {
        return StringUtility.toString(object);
    }

    /**
     * Convert the object instance to a {@link String}.
     *
     * @param object Object to convert.
     * @return Default implementation returns {@link StringUtility#toString(Object)}.
     */
    @Override
    default String apply(T object) {
        return StringUtility.toString(object);
    }
}
