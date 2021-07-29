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

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
* A class used to check/ compare the current value with the previous value in a series of values and returns a boolean result.
* 
* @author Syam
**/
public class CheckWithPrevious<T> implements Predicate<T> {

    private T previous;
    private BiPredicate<? super T, ? super T> checker;

    /**
    * Constructor
    *
    * @param checker A BiPredicate that defines the validation to take place between the current and the previous values.
    **/
    public CheckWithPrevious(BiPredicate<? super T, ? super T> checker) {
        this(null, checker);
    }

    /**
    * Constructor
    * 
    * @param initial The initial value to compare/ check.
    * @param checker A BiPredicate that defines the validation to take place between the current and the previous values.
    **/
    public CheckWithPrevious(T initial, BiPredicate<? super T, ? super T> checker) {
        this.previous = initial;
        this.checker = checker;
    }

    @Override
    public boolean test(T current) {
        T p = previous;
        previous = current;
        return p == null || checker.test(p, current);
    }

    /**
    * Sets the previous value that is to be compared with the current value.
    * 
    * @param previous The previous value.
    **/
    public void setPrevious(T previous) {
        this.previous = previous;
    }

    /**
    * Gets the previous value that is compared with the current value.
    * 
    * @return previous The previous value.
    **/
    public T getPrevious() {
        return previous;
    }
}
