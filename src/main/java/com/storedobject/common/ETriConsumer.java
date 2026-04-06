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
 * A functional interface that represents an operation that accepts three input arguments and potentially throws a checked exception.
 *
 * @param <T1> The type of the first argument to the operation.
 * @param <T2> The type of the second argument to the operation.
 * @param <T3> The type of the third argument to the operation.
 * @param <E> The type of exception that may be thrown.
 *
 * @author Syam
 */
@FunctionalInterface
public interface ETriConsumer<T1, T2, T3, E extends Exception> {

    /**
     * Performs the operation with the given three input arguments, potentially throwing a checked exception.
     *
     * @param t1 The first input argument.
     * @param t2 The second input argument.
     * @param t3 The third input argument.
     * @throws E If an error occurs during the operation.
     */
    void accept(T1 t1, T2 t2, T3 t3) throws E;

    /**
     * Wraps an {@link ETriConsumer} into a {@link TriConsumer} by handling checked exceptions and rethrowing them as runtime exceptions.
     *
     * @param <T1> The type of the first argument to the operation.
     * @param <T2> The type of the second argument to the operation.
     * @param <T3> The type of the third argument to the operation.
     * @param <E> The type of exception that may be thrown by the {@link ETriConsumer}.
     * @param consumer The {@link ETriConsumer} to be wrapped.
     * @return A {@link TriConsumer} that wraps the given {@link ETriConsumer}, rethrowing any checked exceptions as runtime exceptions.
     */
    static <T1, T2, T3, E extends Exception> TriConsumer<T1, T2, T3> wrap(ETriConsumer<T1, T2, T3, E> consumer) {
        return (arg1, arg2, arg3) -> {
            try {
                consumer.accept(arg1, arg2, arg3);
            } catch (Exception e) {
                throw new SORuntimeException(e);
            }
        };
    }
}