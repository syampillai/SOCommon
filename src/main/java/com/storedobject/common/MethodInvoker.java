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

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * A functional interface designed for invoking a method on a given object.
 * It provides additional utility methods for obtaining information about the method
 * and formatting the result.
 *
 * @author Syam
 */
@FunctionalInterface
public interface MethodInvoker {

    /**
     * Retrieves the name of an attribute associated with this method.
     *
     * @return a string representing the attribute name, or null if no attribute name is defined.
     */
    default String getAttributeName() {
        return null;
    }

    /**
     * Retrieves the tail method associated with the implementation.
     * The tail method typically represents an additional or final processing method
     * in the context of the functional interface's behavior.
     *
     * @return the {@code Method} instance representing the tail method,
     *         or {@code null} if no such method is defined.
     */
    default Method getTail() {
        return null;
    }

    /**
     * Invokes a method on the provided object.
     *
     * @param object the object on which the method is to be invoked
     * @return the result of the method invocation, or null if the invocation fails
     */
    Object invoke(Object object);

    /**
     * Determines the return type of the `invoke` method defined in the implementing class.
     * If the `invoke` method cannot be located or an exception occurs, defaults to returning {@code String.class}.
     *
     * @return the {@code Class} object representing the return type of the `invoke` method
     *         or {@code String.class} if the method is not found or an error occurs.
     */
    default Class<?> getReturnType() {
        try {
            Method m = getClass().getMethod("invoke", Object.class);
            return m.getReturnType();
        } catch (NoSuchMethodException | SecurityException ignored) {
        }
        return String.class;
    }

    /**
     * Invokes a method on the provided object with an optional logging behavior for errors.
     *
     * @param object the object on which the method is to be invoked
     * @param logError a boolean flag indicating whether errors during invocation should be logged
     * @return the result of the method invocation, or null if the invocation fails
     */
    default Object invoke(Object object, boolean logError) {
        return invoke(object);
    }

    /**
     * Creates a {@code Function} that applies the {@code invoke} method to a given object.
     * This version of the {@code function()} method does not log errors during invocation.
     *
     * @return a {@code Function} that takes an {@code Object} as input and produces a result
     *         by invoking the {@code invoke} method on the input object without logging errors.
     */
    default Function<Object, ?> function() {
        return function(false);
    }

    /**
     * Creates a {@code Function} that applies the {@code invoke} method to a given object,
     * with an optional logging behavior for errors during method invocation.
     *
     * @param logError a boolean flag indicating whether errors during invocation should be logged
     * @return a {@code Function} that takes an {@code Object} as input and produces a result
     *         by invoking the {@code invoke} method on the input object, with error logging
     *         behavior determined by the {@code logError} parameter
     */
    default Function<Object, ?> function(boolean logError) {
        return object -> invoke(object, logError);
    }

    /**
     * Creates a {@code Function} that takes an {@code Object} as input and converts the result of
     * invoking a method on the object into a {@code String}. This method does not log errors during
     * the invocation process.
     *
     * @return a {@code Function} that applies the {@code invoke} method to a given object,
     *         converts the result to a {@code String}, and does not log errors during the process.
     */
    default Function<Object, String> string() {
        return string(false);
    }

    /**
     * Creates a {@code Function} that takes an {@code Object} as input, applies the {@code invoke} method to the input
     * object with an optional logging behavior for errors, and converts the result into a {@code String}.
     *
     * @param logError a boolean flag indicating whether errors during invocation should be logged
     * @return a {@code Function} that applies the {@code invoke} method to the input object, converts the result to
     *         a {@code String}, with error logging behavior determined by the {@code logError} parameter
     */
    default Function<Object, String> string(boolean logError) {
        return object -> StringUtility.toString(invoke(object, logError));
    }
}