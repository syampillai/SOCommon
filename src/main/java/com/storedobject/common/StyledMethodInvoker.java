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
 * An abstract class that facilitates invocation of methods while allowing for
 * the use of a styled builder for result processing. This class extends the
 * {@link CustomMethodInvoker} and provides additional abstraction for creating
 * and populating a {@link StyledBuilder}.
 *
 * The {@code StyledMethodInvoker} is responsible for:
 * <pre>
 * - Overriding the default return type of the method invocation to {@code StyledBuilder}.
 * - Defining the contracts for creating a {@link StyledBuilder} and building it with a given object.
 * </pre>
 *
 * @author Syam
 */
public abstract class StyledMethodInvoker extends CustomMethodInvoker {

    /**
     * Constructs a new instance of {@code StyledMethodInvoker}.
     *
     * This constructor initializes the object without any parameters.
     * Being part of an abstract class, it serves as a base for
     * subclass implementations that define specific behavior for creating
     * and populating a {@code StyledBuilder}.
     */
    public StyledMethodInvoker() {
    }

    @Override
    public final StyledBuilder invoke(Object object) {
        return build(object, createBuilder());
    }

    @Override
    public Class<?> getReturnType() {
        return StyledBuilder.class;
    }

    /**
     * Creates and returns a new instance of {@link StyledBuilder}.
     *
     * This method should be implemented by subclasses to provide
     * the specific logic for creating a new {@link StyledBuilder} instance,
     * which can then be used in conjunction with other methods like
     * {@link #build(Object, StyledBuilder)} for further processing.
     *
     * @return A new instance of {@link StyledBuilder}.
     */
    public abstract StyledBuilder createBuilder();

    /**
     * Builds and populates a {@link StyledBuilder} instance using the given object and builder.
     * This method is abstract and must be implemented by the subclass to define the actual
     * logic for how the {@link StyledBuilder} is constructed and modified.
     *
     * @param object The object to be used for building or populating the {@link StyledBuilder}.
     *               This may act as the source of data or context for the builder.
     * @param builder The {@link StyledBuilder} instance that will be populated or modified.
     *                This builder is expected to have been created beforehand, often using
     *                {@link #createBuilder()}.
     * @return A fully constructed and modified {@link StyledBuilder} instance, ready for use
     *         or further processing.
     */
    public abstract StyledBuilder build(Object object, StyledBuilder builder);
}