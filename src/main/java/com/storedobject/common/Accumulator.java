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
* Accumulator function that is used to calculate a sum or product of a series of values.
*
* @author Syam
**/
public class Accumulator<T> {

    private T value;
    private final BiFunction<T, T, T> accumulator;

    /**
    * Create an Accumulator instance by passing a ByFunction.
    *
    * @param accumulator ByFunction with function arguments and result of the same type.
    **/
    public Accumulator(BiFunction<T, T, T> accumulator) {
        this.accumulator = accumulator;
    }

    /**
    * Gets the value from the accumulator.
    * 
    * @return value The value after calculation is applied for the series of values.
    **/
    public T get() {
        return value;
    }

    /**
    * Accepts values for calculation
    * 
    * @param value A value restricted to a specific type T.
    **/
    public void accept(T value) {
        if (this.value == null) {
            this.value = value;
        } else {
            this.value = accumulator.apply(this.value, value);
        }
    }
}
