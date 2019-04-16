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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListUtility {

    public static <T> List<T> list(Iterable<T> list) {
        if(list instanceof List) {
            return (List<T>) list;
        }
        java.util.ArrayList<T> a = new ArrayList<>();
        list.forEach(a::add);
        return a;
    }

    public static <T> List<T> list(T[] list) {
        return new Array<T>(list);
    }

    public static <T> T[] array(Iterable<T> list) {
        return array(list(list));
    }

    public static <T> T[] array(Collection<T> list) {
        @SuppressWarnings("unchecked")
        T[] es = (T[])new Object[list.size()];
        return list.toArray(es);
    }
}
