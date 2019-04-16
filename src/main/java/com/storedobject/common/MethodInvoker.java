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
import java.util.function.Function;

@FunctionalInterface
public interface MethodInvoker {

    default String getAttributeName() {
        return null;
    }

    default Method getTail() {
        return null;
    }

    Object invoke(Object object);

    default Class<?> getReturnType() {
        try {
            Method m = getClass().getMethod("invoke", Object.class);
            return m.getReturnType();
        } catch (NoSuchMethodException | SecurityException ignored) {
        }
        return String.class;
    }

    default Object invoke(Object object, boolean logError) {
        return invoke(object);
    }

    default Function<Object, ?> function() {
        return function(false);
    }

    default Function<Object, ?> function(boolean logError) {
        return object -> invoke(object, logError);
    }

    default Function<Object, String> string() {
        return string(false);
    }

    default Function<Object, String> string(boolean logError) {
        return object -> StringUtility.toString(invoke(object, logError));
    }
}