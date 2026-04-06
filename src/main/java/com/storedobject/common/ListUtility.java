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
import java.util.Iterator;
import java.util.List;

/**
 * A utility class that provides methods to convert various types of collections
 * and iterables into lists or arrays.
 *
 * @author Syam
 */
public class ListUtility {

    private ListUtility() {
    }

    /**
     * Converts an {@link Iterable} into a {@link List}.
     * If the provided {@link Iterable} is already a {@link List}, it is returned as-is.
     * Otherwise, elements are copied into a new {@link ArrayList}.
     *
     * @param <T> the type of elements in the {@link Iterable}
     * @param list the {@link Iterable} to be converted into a {@link List}
     * @return a {@link List} containing all elements from the given {@link Iterable}
     */
    public static <T> List<T> list(Iterable<T> list) {
        if(list instanceof List) {
            return (List<T>) list;
        }
        ArrayList<T> a = new ArrayList<>();
        list.forEach(a::add);
        return a;
    }

    /**
     * Converts an {@link Iterator} into a {@link List}.
     * If the provided {@link Iterator} is not an instance of a {@link List},
     * the remaining elements in the {@link Iterator} are copied into a new {@link ArrayList}.
     *
     * @param <T> the type of elements in the {@link Iterator}
     * @param list the {@link Iterator} to be converted into a {@link List}
     * @return a {@link List} containing all elements from the given {@link Iterator}
     */
    public static <T> List<T> list(Iterator<T> list) {
        if(list instanceof List) {
            //noinspection unchecked
            return (List<T>) list;
        }
        ArrayList<T> a = new ArrayList<>();
        while(list.hasNext()) {
            a.add(list.next());
        }
        return a;
    }

    /**
     * Converts an array into a {@link List}.
     * Creates a {@link List} containing all the elements of the provided array.
     *
     * @param <T> the type of elements in the array
     * @param list the array to be converted into a {@link List}
     * @return a {@link List} containing all elements from the given array
     */
    public static <T> List<T> list(T[] list) {
        return new Array<>(list);
    }

    /**
     * Converts an {@link Iterable} into an array. Internally, the {@link Iterable} is first
     * transformed into a {@link List} using the {@code list} method, followed by converting the
     * resulting {@link List} into an array.
     *
     * @param <T> the type of elements in the {@link Iterable}
     * @param list the {@link Iterable} to be converted into an array
     * @return an array containing all elements from the given {@link Iterable}
     */
    public static <T> T[] array(Iterable<T> list) {
        return array(list(list));
    }

    /**
     * Converts a {@link Collection} into an array. The method creates a new array
     * of the same size as the provided {@link Collection} and populates it with the
     * elements from the {@link Collection}.
     *
     * @param <T> the type of elements in the {@link Collection}
     * @param list the {@link Collection} to be converted into an array
     * @return an array containing all elements from the given {@link Collection}
     */
    public static <T> T[] array(Collection<T> list) {
        @SuppressWarnings("unchecked")
        T[] es = (T[])new Object[list.size()];
        return list.toArray(es);
    }
}
