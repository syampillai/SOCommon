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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The Condition class extends AtomicBoolean and provides
 * utility methods to manage a boolean state that can be used
 * to signify whether a condition is met or not.
 *
 * @author Syam
 */
public class Condition extends AtomicBoolean {

    /**
     * Constructs a new {@code Condition} object with an initial value of {@code true}.
     * This constructor initializes the internal state of the {@code Condition} to signify
     * that the condition is initially met.
     */
    public Condition() {
        super(true);
    }

    /**
     * Checks the current state of the condition.
     *
     * @return {@code true} if the condition is met, otherwise {@code false}.
     */
    public boolean ok() {
        return get();
    }

    /**
     * Updates the internal state to indicate that the condition is no longer met.
     * This method sets the state of this {@code Condition} to {@code false}.
     */
    public void stop() {
        set(false);
    }
}