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
 * A class that provides multithreaded synchronized Accumulator functionality
 *
 * @param <T> Value type.
 * @author Syam
 **/
public class ParallelAccumulator<T> extends Accumulator<T> {

    /**
     * Constructor
     *
     * @param accumulator A BiFunction that accepts the arguments and return value is of type T.
     **/
    public ParallelAccumulator(BiFunction<T, T, T> accumulator) {
        super(accumulator);
    }

    @Override
    public synchronized void accept(T value) {
        super.accept(value);
    }
}
