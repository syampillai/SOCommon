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

import java.lang.reflect.Method;

/**
 * A class that implements the MethodInvoker functional interface to facilitate the invocation
 * of specific methods from a given class.
 *
 * The class provides mechanisms to resolve and invoke methods based on naming conventions
 * such as "get", "is", or direct method names. If the method cannot be found or any errors occur,
 * the invocation defaults to a null operation.
 *
 * @author Syam
 */
public class SimpleMethodInvoker implements MethodInvoker {

    private Method method;

    /**
     * Constructs a SimpleMethodInvoker instance by associating it with the specified {@link Method}.
     * This method will be used for invocation through the invoker instance.
     *
     * @param method the {@link Method} to be invoked. It must not be null.
     */
    public SimpleMethodInvoker(Method method) {
        this.method = method;
    }

    /**
     * Constructs a SimpleMethodInvoker by attempting to resolve a method in the specified class
     * using a series of naming conventions. It first tries to find a method prefixed with "get",
     * then "is", and finally it attempts to locate a method with the exact provided name.
     * If a matching method is found, it is used as the target for invocation; otherwise, the method field is set to null.
     *
     * @param objectClass the class from which the method will be resolved. It must not be null.
     * @param name the base name of the method to resolve. This is expected to be the method name
     *             without the "get" or "is" prefix, if applicable. Must not be null or empty.
     */
    public SimpleMethodInvoker(Class<?> objectClass, String name) {
        try {
            method = objectClass.getMethod("get" + name, (Class<?>[])null);
        } catch(Exception e) {
            method = null;
        }
        if(method != null) {
            return;
        }
        try {
            method = objectClass.getMethod("is" + name, (Class<?>[])null);
        } catch(Exception e) {
            method = null;
        }
        if(method != null) {
            return;
        }
        try {
            method = objectClass.getMethod(name, (Class<?>[])null);
        } catch(Exception e) {
            method = null;
        }
    }

    @Override
    public String getAttributeName() {
        String m = method.getName();
        if(m.startsWith("get")) {
            return m.substring(3);
        }
        if(m.startsWith("is")) {
            return m.substring(2);
        }
        return m;
    }

    @Override
    public Method getTail() {
        return method;
    }

    @Override
    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    @Override
    public Object invoke(Object object) {
        try {
            return method.invoke(object);
        } catch(Exception ignored) {
        }
        return null;
    }
}