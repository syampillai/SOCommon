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
 * Represents a functional interface that encapsulates a single action to be performed.
 * This interface extends {@code Runnable}, allowing implementations to be used in
 * contexts where a {@code Runnable} is expected.
 *
 * This interface provides a single abstract method {@code act()} that must be
 * implemented to define the specific action. Additionally, default implementations
 * of {@code run()} and {@code execute()} are provided, both of which delegate
 * their behavior to the {@code act()} method.
 *
 * @author Syam
 */
@FunctionalInterface
public interface Action extends Runnable {

    /**
     * Performs the specific action encapsulated by this method. This is the primary
     * abstract method of the functional interface, and concrete implementations
     * must provide its definition to specify the desired behavior.
     */
    void act();

    /**
     * Executes the action defined by the {@code act()} method.
     * This default implementation delegates to the {@code act()} method,
     * allowing the behavior of {@code run()} to be customized by providing
     * a concrete implementation of {@code act()}.
     */
    default void run() {
        act();
    }

    /**
     * Executes the action defined by the {@code act()} method.
     * This default implementation directly delegates its behavior to the {@code act()} method,
     * ensuring that the action is executed consistently wherever this method is invoked.
     */
    default void execute() { act(); }
}