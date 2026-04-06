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
 * A functional interface representing a function that accepts one argument, produces a result,
 * and may throw a checked exception during execution.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of exception that may be thrown
 *
 * @author Syam
 */
@FunctionalInterface
public interface EFunction<T, R, E extends Exception> {

    /**
     * Applies this function to the given argument and returns the result.
     *
     * @param t the input argument
     * @return the computed result
     * @throws E if the function fails to complete successfully
     */
    R apply(T t) throws E;

    /**
     * Wraps an {@link EFunction} into a standard {@link Function}, converting any checked exception
     * it may throw into a {@link SORuntimeException}.
     *
     * @param <T> the type of the input to the function
     * @param <R> the type of the result of the function
     * @param <E> the type of exception that the wrapped function may throw
     * @param function the {@link EFunction} to be wrapped
     * @return a {@link Function} that applies the given {@link EFunction} and handles exceptions
     *         by wrapping them in a {@link SORuntimeException}
     */
    static <T, R, E extends Exception> Function<T, R> wrap(EFunction<T, R, E> function) {
        return arg -> {
            try {
                return function.apply(arg);
            } catch (Exception e) {
                throw new SORuntimeException(e);
            }
        };
    }
}
