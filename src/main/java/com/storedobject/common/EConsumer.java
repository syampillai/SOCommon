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
 * A functional interface that represents an operation on a single input
 * that might throw a checked exception of a specific type.
 *
 * This interface is designed for use with lambda expressions or method
 * references when a checked exception needs to be handled.
 *
 * @param <T> the type of the input argument to the operation
 * @param <E> the type of exception that the operation might throw
 *
 * @author Syam
 */
@FunctionalInterface
public interface EConsumer<T, E extends Exception> {

    /**
     * Performs an operation on the given input argument, potentially
     * throwing a checked exception of a specific type.
     *
     * @param t the input argument to the operation
     * @throws E if the operation encounters an exception
     */
    void accept(T t) throws E;

    /**
     * Wraps an {@link EConsumer} into a standard {@link Consumer}, converting
     * any checked exceptions thrown by the supplied {@link EConsumer} into an
     * unchecked {@link SORuntimeException}.
     *
     * @param <T> the type of the input to the operation
     * @param <E> the type of the exception that the {@link EConsumer} might throw
     * @param consumer the {@link EConsumer} to wrap
     * @return a {@link Consumer} that wraps the provided {@link EConsumer} and handles exceptions
     */
    static <T, E extends Exception> Consumer<T> wrap(EConsumer<T, E> consumer) {
        return arg -> {
            try {
                consumer.accept(arg);
            } catch (Exception e) {
                throw new SORuntimeException(e);
            }
        };
    }
}
