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
 * An abstract base class that provides a customizable method invocation framework
 * with support for caching the return type of the invoked method and handling
 * attribute name assignment. This class implements the {@link MethodInvoker} interface
 * and provides additional capabilities while delegating core behaviors to the interface.
 *
 * @author Syam
 */
public abstract class CustomMethodInvoker implements MethodInvoker {

    private final String attributeName;
    private Class<?> type = null;

    /**
     * Default constructor for the CustomMethodInvoker class.
     * Initializes a new instance with a null attribute name.
     * This constructor delegates to the parameterized constructor to ensure a consistent initialization process.
     */
    public CustomMethodInvoker() {
        this(null);
    }

    /**
     * Constructs a new instance of CustomMethodInvoker with the specified attribute name.
     *
     * @param attributeName the name of the attribute to be associated with the method invoker,
     *                      or null if no attribute name is specified.
     */
    public CustomMethodInvoker(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public Class<?> getReturnType() {
        if(type == null) {
            type = MethodInvoker.super.getReturnType();
        }
        return type;
    }

    @Override
    public Object invoke(Object object, boolean logError) {
        return invoke(object);
    }
}