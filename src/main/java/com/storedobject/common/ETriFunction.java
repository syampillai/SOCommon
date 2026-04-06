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
 * A functional interface that represents a function accepting three arguments of types T1, T2, and T3,
 * which produces a result of type R, and can throw an exception of type E.
 *
 * @param <T1> the type of the first argument to the function
 * @param <T2> the type of the second argument to the function
 * @param <T3> the type of the third argument to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of exception that the function might throw
 */
@FunctionalInterface
public interface ETriFunction<T1, T2, T3, R, E extends Exception> {

    /**
     * Applies this function to the given arguments and produces a result.
     *
     * @param t1 the first argument
     * @param t2 the second argument
     * @param t3 the third argument
     * @return the result of applying this function to the given arguments
     * @throws E if an exception occurs during function execution
     */
    R apply(T1 t1, T2 t2, T3 t3) throws E;

    /**
     * Wraps an instance of {@link ETriFunction} into a {@link TriFunction}, converting any checked exceptions
     * thrown by the underlying function into a {@link SORuntimeException}.
     *
     * @param <T1> the type of the first argument to the function
     * @param <T2> the type of the second argument to the function
     * @param <T3> the type of the third argument to the function
     * @param <R> the type of the result of the function
     * @param <E> the type of exception that the {@link ETriFunction} may throw
     * @param function the {@link ETriFunction} to be wrapped
     * @return a {@link TriFunction} wrapping the given {@link ETriFunction}
     *         and converting its exceptions to {@link SORuntimeException}
     */
    static <T1, T2, T3, R, E extends Exception> TriFunction<T1, T2, T3, R> wrap(ETriFunction<T1, T2, T3, R, E> function) {
        return (arg1, arg2, arg3) -> {
            try {
                return function.apply(arg1, arg2, arg3);
            } catch (Exception e) {
                throw new SORuntimeException(e);
            }
        };
    }
}