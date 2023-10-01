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

public class SortedList<E extends Comparable<E>> extends ArrayList<E> {

    public SortedList() {
    }

    public SortedList(int initialCapacity) {
        super(initialCapacity);
    }

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
