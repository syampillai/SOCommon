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
 * An interface that to defines that a {@link Runnable} class has an "execute" method. This is typically
 * used in a programming environment where certain "logic" classes need to be asked to "execute" the logic
 * implemented in it.
 *
 * @author Syam
 */
@FunctionalInterface
public interface Executable extends Runnable {

    /**
     * The functional interface method specified by this interface.
     */
    void execute();

    /**
     * The default implementation of of this method invokes {@link #execute()}.
     */
    default void run() {
        execute();
    }

    /**
     * The default implementation of of this method invokes {@link #execute()}.
     */
    default void act() { execute(); }
}