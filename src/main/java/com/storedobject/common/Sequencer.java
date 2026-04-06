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

import java.util.concurrent.atomic.AtomicLong;

/**
 * The Sequencer class extends AtomicLong to provide functionality
 * for generating sequential numbers starting from a specified value.
 * It ensures the sequence resets to 1 if the current value reaches Long.MAX_VALUE.
 *
 * @author Syam
 */
public class Sequencer extends AtomicLong {

    /**
     * Constructs a new Sequencer instance with the sequence starting at 1.
     * This constructor invokes the parameterized constructor with the default starting value of 1.
     */
    public Sequencer() {
        this(1);
    }

    /**
     * Constructs a new Sequencer instance with the sequence starting at the specified value.
     *
     * @param startingNumber the initial value from which the sequence will start
     */
    public Sequencer(long startingNumber) {
        super(startingNumber);
    }

    /**
     * Retrieves the current value of the sequencer without incrementing it.
     * The value represents the latest number in the sequence.
     *
     * @return the current value of the sequence
     */
    public long current() {
        return get();
    }

    /**
     * Generates the next number in the sequence. If the current value of the sequence
     * has reached Long.MAX_VALUE, the sequence resets to 1 before continuing.
     *
     * @return the next number in the sequence
     */
    public long next() {
        if(get() == Long.MAX_VALUE) {
            set(1);
        }
        return getAndIncrement();
    }
}