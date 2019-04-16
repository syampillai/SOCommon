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

public class SimpleMethodInvoker implements MethodInvoker {

    private Method method;

    public SimpleMethodInvoker(Method method) {
        this.method = method;
    }

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
            return method.invoke(object, new Object[] { null });
        } catch(Exception e) {
        }
        return null;
    }
}