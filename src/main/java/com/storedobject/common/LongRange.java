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
 * Represents a range of long values, defined by a starting (inclusive) and ending (inclusive) boundary.
 * This class extends the generic {@code Range<Long>} and provides specific implementations
 * for behavior associated with long numeric values.
 *
 * @author Syam
 */
public class LongRange extends Range<Long> {

    /**
     * Default constructor for the {@code LongRange} class.
     * Initializes the range with a starting and ending boundary of 0.
     * This represents an empty range where both boundaries are the same.
     */
    public LongRange() {
        this(0L, 0L);
    }

    /**
     * Constructs a {@code LongRange} instance with the specified starting and ending boundaries.
     *
     * @param from the starting value of the range (inclusive); must be a non-null {@code Long}.
     * @param to the ending value of the range (inclusive); must be a non-null {@code Long}.
     */
    public LongRange(Long from, Long to) {
        super(from, to);
    }

    @Override
    protected Long clone(Long data) {
        return data == null ? 0L : data;
    }

    @Override
    protected boolean same(Long one, Long two) {
        return one.longValue() == two.longValue();
    }

    @Override
    protected long value(Long data) {
        return data;
    }
}
