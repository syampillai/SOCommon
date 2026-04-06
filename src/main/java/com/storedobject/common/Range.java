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

import java.util.Objects;

/**
 * Represents a generic range of values of type T. This abstract class is intended to
 * be extended by specific range implementations that define behavior for particular types.
 * The range is defined by two endpoints: a starting point ("from") and an ending point ("to").
 *
 * @param <T> the type of the values representing the range boundaries
 *
 * @author Syam
 */
public abstract class Range<T> {

    /**
     * Represents the starting point of the range. This variable defines
     * the inclusive lower boundary of the range for the specified type {@code T}.
     * It is immutable and retains the initial value provided at the time of
     * instantiation of the range.
     *
     * The interpretation of the value is specific to the type {@code T} and
     * may depend on the implementation of the subclass. For example,
     * in a numeric range, this could represent the minimum value, while
     * in a date range, this could represent the earlier date.
     *
     * The variable is protected, allowing access to subclasses of {@link Range},
     * and final, ensuring that its value cannot be modified after initialization.
     */
    protected final T from, to;

    /**
     * Constructs a new Range instance with the specified start and end values.
     *
     * @param from the starting value of the range; must be of type T
     * @param to the ending value of the range; must be of type T
     */
    public Range(T from, T to) {
        this.from = clone(from);
        this.to = clone(to);
    }

    /**
     * Retrieves the starting value of the range.
     *
     * @return the starting value of the range, cloned from the internal "from" field
     */
    public T getFrom() {
        return clone(from);
    }

    /**
     * Retrieves the ending value of the range.
     *
     * @return the ending value of the range, cloned from the internal "to" field
     */
    public T getTo() {
        return clone(to);
    }

    /**
     * Creates a shallow copy of the provided object. This method is used to generate
     * a clone of the specified input object of the same type.
     *
     * @param data the object to be cloned; must be of type T
     * @return a shallow copy of the input object
     */
    protected T clone(T data) {
        return data;
    }

    @Override
    public final boolean equals(Object another) {
        if(another == null || !getClass().isAssignableFrom(another.getClass())) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Range<T> r = (Range<T>)another;
        if(this == another) {
            return true;
        }
        if((from == null && r.from != null) || (r.from == null && from != null)) {
            return false;
        }
        if((to == null && r.to != null) || (r.to == null && to != null)) {
            return false;
        }
        return same(from, r.from) && same(to, r.to);
    }

    /**
     * Compares the given range boundaries with the current range for equality.
     *
     * @param from the starting value of the range to compare; can be null
     * @param to the ending value of the range to compare; can be null
     * @return true if both the starting and ending values match the current range; false otherwise
     */
    public final boolean equals(T from, T to) {
        if((this.from == null && from != null) || (from == null && this.from != null)) {
            return false;
        }
        if((this.to == null && to != null) || (to == null && this.to != null)) {
            return false;
        }
        return same(this.from, from) && same(this.to, to);
    }

    /**
     * Compares two objects of type T for equality.
     *
     * @param one the first object to compare; can be null
     * @param two the second object to compare; can be null
     * @return true if both objects are equal or both are null; false otherwise
     */
    protected boolean same(T one, T two) {
        return Objects.equals(one, two);
    }

    /**
     * Checks if the range is considered valid based on its boundaries.
     * A range is valid if neither boundary is null and one of the following is true:
     * the starting boundary value is less than the ending boundary value,
     * or both boundaries are considered equal.
     *
     * @return true if the range is valid; false otherwise
     */
    public boolean isValid() {
        if(from == null || to == null) {
            return false;
        }
        return value(from) < value(to) || same(from, to);
    }

    /**
     * Calculates a numeric representation of the given data. The implementation
     * currently returns a default value.
     *
     * @param data the input data, potentially used to determine the numeric value; must be of type T
     * @return a numeric representation of the input data, which is currently a fixed value of 0L
     */
    protected long value(@SuppressWarnings("unused") T data) {
        return 0L;
    }

    /**
     * Converts the provided data of type T to its string representation.
     *
     * @param data the input data to convert; can be null
     * @return an empty string if the input data is null, otherwise the string representation of the data
     */
    protected String toString(T data) {
        return data == null ? "" : data.toString();
    }

    @Override
    public String toString() {
        return toString(from) + " - " + toString(to);
    }
}
