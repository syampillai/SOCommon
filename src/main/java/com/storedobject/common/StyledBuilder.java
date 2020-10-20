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
 * Build something with "styles".
 *
 * @author Syam
 */
public interface StyledBuilder {

    /**
     * Append an object.
     *
     * @param object Object to append.
     * @return Self-reference is returned.
     */
    StyledBuilder append(Object object);

    /**
     * Append an object with the given color.
     *
     * @param object Object to append.
     * @param color Color to be set. (Default implementation ignores this).
     * @return Self-reference is returned.
     */
    default StyledBuilder append(Object object, String color) {
        return append(object);
    }

    /**
     * Set a value with the given styles. (Previous content if any will be cleared).
     *
     * @param object Object (value) to set.
     * @param styles Styles to be set.
     */
    default void setValue(Object object, String... styles) {
        clearContent().append(object, styles);
    }

    /**
     * Append an object with the given style.
     *
     * @param object Object to append.
     * @param styles Styles to be set. (Default implementation ignores this).
     * @return Self-reference is returned.
     */
    default StyledBuilder append(Object object, String... styles) {
        return append(object);
    }

    /**
     * Check if the content is empty or not.
     *
     * @return True or false.
     */
    boolean isEmpty();

    /**
     * Adds a new line. (Default implementation doesn't force a new line).
     *
     * @return Self-reference is returned.
     */
    default StyledBuilder newLine() {
        return newLine(false);
    }

    /**
     * Adds a new line.
     *
     * @param forceIt If true, a new line is added anyway. Otherwise, no new line should be added
     *                if {@link #isNewLine()} returns false.
     * @return Self-reference is returned.
     */
    StyledBuilder newLine(boolean forceIt);

    /**
     * Check if the last add was a new line or not.
     *
     * @return True/false
     */
    boolean isNewLine();

    /**
     * Update internals if required. (Default implementation does nothing).
     *
     * @return Self-reference is returned.
     */
    default StyledBuilder update() {
        return this;
    }

    /**
     * Clear the content.
     *
     * @return Self-reference is returned.
     */
    StyledBuilder clearContent();
}