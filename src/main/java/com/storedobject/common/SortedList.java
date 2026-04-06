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

/**
 * A custom implementation of an ArrayList that maintains its elements in sorted order.
 * Elements are required to implement the {@link Comparable} interface.
 *
 * @param <E> the type of elements maintained by this list, extending {@link Comparable}.
 *
 * @author Syam
 */
public class SortedList<E extends Comparable<E>> extends ArrayList<E> {

    /**
     * Constructs an empty SortedList with an initial capacity of ten.
     * This list automatically maintains the sorting order of its elements.
     * Elements added to the list must implement the {@link Comparable} interface.
     */
    public SortedList() {
    }

    /**
     * Constructs an empty SortedList with the specified initial capacity.
     * This list automatically maintains the sorting order of its elements.
     * Elements added to the list must implement the {@link Comparable} interface.
     *
     * @param initialCapacity the initial capacity of the list.
     *                        The capacity represents the number of elements that the list
     *                        can hold before needing to resize.
     * @throws IllegalArgumentException if the specified initial capacity is negative.
     */
    public SortedList(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a new SortedList with elements from the specified collection.
     * The elements in the collection are added while maintaining their natural order.
     * Duplicate elements are not added to the list.
     *
     * @param c the collection containing elements to be added to the sorted list.
     *          All elements in the collection must implement the {@link Comparable} interface.
     *          Passing a null collection results in a {@link NullPointerException}.
     */
    public SortedList(Collection<E> c) {
        addAll(c);
    }

    @Override
    public boolean add(E e) {
        if(contains(e)) {
            return false;
        }
        int i;
        E v;
        for(i = 0; i < size(); i++) {
            v = get(i);
            if(v.compareTo(e) <= 0) {
                super.add(i, e);
                return true;
            }
        }
        return super.add(e);
    }

    @Override
    public E set(int index, E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for(E e: c) {
            if(add(e)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return addAll(c);
    }
}
