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

import java.util.function.BiFunction;

/**
 * Functional interface that represents a function which accepts two arguments and produces a result.
 * It allows for a checked exception to be thrown during execution.
 *
 * @param <T1> the type of the first argument to the function
 * @param <T2> the type of the second argument to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of exception that the function might throw
 *
 * @author Syam
 */
@FunctionalInterface
public interface EBiFunction<T1, T2, R, E extends Exception> {

    /**
     * Applies this function to the given arguments and produces a result.
     *
     * @param t1 the first argument to the function
     * @param t2 the second argument to the function
     * @return the result of the function
     * @throws E if an exception of type E occurs during function execution
     */
    R apply(T1 t1, T2 t2) throws E;

    /**
     * Wraps an {@link EBiFunction} that allows checked exceptions into a standard {@link BiFunction},
     * rethrowing any caught exception as a {@link SORuntimeException}.
     *
     * @param <T1> the type of the first argument to the function
     * @param <T2> the type of the second argument to the function
     * @param <R> the type of the result of the function
     * @param <E> the type of exception that the function might throw
     * @param function the {@link EBiFunction} to be wrapped
     * @return a {@link BiFunction} that performs the same logic as the provided {@link EBiFunction},
     *         but converts any caught exception to a {@link SORuntimeException}
     */
    static <T1, T2, R, E extends Exception> BiFunction<T1, T2, R> wrap(EBiFunction<T1, T2, R, E> function) {
        return (arg1, arg2) -> {
            try {
                return function.apply(arg1, arg2);
            } catch (Exception e) {
                throw new SORuntimeException(e);
            }
        };
    }
}
