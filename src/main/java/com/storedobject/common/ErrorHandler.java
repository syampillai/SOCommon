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
 * Represents a functional interface for handling errors or exceptions.
 * Implementations of this interface define a single method to process
 * a given throwable object.
 *
 * This interface can be used for centralized or custom error processing
 * logic in applications, allowing developers to specify how various
 * types of exceptions should be handled.
 *
 * @author Syam
 */
@FunctionalInterface
public interface ErrorHandler {

    /**
     * Handles the given throwable, allowing custom processing or logging of the error.
     *
     * @param error the throwable to be handled; must not be null
     */
    void handle(Throwable error);
}