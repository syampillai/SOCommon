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

import java.util.function.Consumer;

/**
 * Build something with "styles".
 *
 * @author Syam
 */
public interface StyledBuilder {

    static final String[] EMPTY_STRINGS = new String[]{};

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
     * Set a value. (Previous content if any will be cleared).
     *
     * @param object Object (value) to set.
     */
    default void setValue(Object object) {
        clearContent().append(object, EMPTY_STRINGS);
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
     * Set a value with the given styles. (Previous content if any will be cleared).
     *
     * @param object Object (value) to set.
     * @param styles Styles to be set.
     */
    default void setValue(Object object, Style... styles) {
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
     * Append an object with the given style. Default implementation ignores styles.
     *
     * @param object Object to append.
     * @param styles Styles to be set.
     * @return Self-reference is returned.
     */
    default StyledBuilder append(Object object, Style... styles) {
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

    /**
     * Apply styles to a consumer.
     *
     * @param styles Styles text. It will be parsed create styles.
     * @param element Element that accepts the styles.
     */
    public static void apply(String styles, Consumer<Style> element) {
        styles = styles.replace('\n', ';');
        while (styles.contains(";;")) {
            styles = styles.replace(";;", ";");
        }
        String[] ss = styles.split(";");
        Style style;
        for(String s: ss) {
            style = Style.create(s);
            if(style != null) {
                element.accept(style);
            }
        }
    }

    /**
     * Representation of HTML style.
     *
     * @param name Name of the style.
     * @param value Value of the style.
     *
     * @author Syam
     */
    public record Style(String name, String value) {

        /**
         * Create a style from the given string.
         *
         * @param s Text from which the style can be created. (It is of the form "color: red").
         * @return Style instance if an instance can be created. Otherwise, null.
         */
        public static Style create(String s) {
            int p = s.indexOf(':');
            if(p <= 0) {
                return null;
            }
            String n = s.substring(0, p);
            s = s.substring(p + 1);
            if(n.isBlank() || s.isBlank()) {
                return null;
            }
            return new Style(n.trim(), s.trim());
        }

        /**
         * Create an array of Style instances from the given string that may contain multiple styles. (Note: If invalid
         * styles are part of the input, the output can contain null values corresponding to the invalid inputs).
         *
         * @param multipleStyles String containing multiple styles.
         * @return Style array.
         */
        public static Style[] array(String multipleStyles) {
            if(multipleStyles == null) {
                return new Style[]{};
            }
            multipleStyles = multipleStyles.trim().replace('\n', ';');
            if(multipleStyles.isBlank()) {
                return new Style[]{};
            }
            while (multipleStyles.contains(";;")) {
                multipleStyles = multipleStyles.replace(";;", ";");
            }
            String[] ss = multipleStyles.split(";");
            Style[] styles = new Style[ss.length];
            int i = 0;
            for(String s: ss) {
                styles[i++] = create(s);
            }
            return styles;
       }
    }
}