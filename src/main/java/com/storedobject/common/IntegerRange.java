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
 * Represents a range of integer values with a start point ("from") and an end point ("to").
 * This class extends the generic {@code Range<T>} class and provides implementations
 * for integer-specific behavior of range manipulation.
 *
 * The class is designed to handle ranges of {@code Integer} objects and ensures that
 * the values within the range are represented and compared appropriately as integers.
 *
 * @author Syam
 */
public class IntegerRange extends Range<Integer> {

    /**
     * Default constructor for the {@code IntegerRange} class.
     * Initializes the range with a starting value of 0 and an ending value of 0,
     * effectively representing an empty or zero-length range.
     */
    public IntegerRange() {
        this(0, 0);
    }

    /**
     * Constructs an {@code IntegerRange} instance with the specified start and end values.
     *
     * @param from the starting value of the range; must be a non-null {@code Integer}
     * @param to the ending value of the range; must be a non-null {@code Integer}
     */
    public IntegerRange(Integer from, Integer to) {
        super(from, to);
    }

    @Override
    protected Integer clone(Integer data) {
        return data == null ? 0 : data;
    }

    @Override
    protected boolean same(Integer one, Integer two) {
        return one.intValue() == two.intValue();
    }

    @Override
    protected long value(Integer data) {
        return data.longValue();
    }
}