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

import java.util.Collection;

/**
 * An ArrayList that has the properties of a Set (Only one instance will appear even if duplicate elements are added).
 *
 * @author Syam
 */
public class ArrayListSet<E> extends ArrayList<E> {
    
    /**
    * Create an ArrayList that has the properties of a Set
    **/
    public ArrayListSet() {
    }

    /**
    * Create an ArrayList that has the properties of a Set for a specified initial capacity
    *
    * @param initialCapacity Initial capacity specified while creating the list
    **/
    public ArrayListSet(int initialCapacity) {
        super(initialCapacity);
    }

    
    /**
    * Create an ArrayList that has the properties of a Set for specified elements of a collection
    *
    * @param c Collection
    **/
    public ArrayListSet(Collection<? extends E> c) {
        addAll(c);
    }

    
    /**
    * Create an ArrayList that has the properties of a Set for specified iterable elements
    *
    * @param elements Iterable elements
    **/
    public ArrayListSet(Iterable<? extends E> elements) {
        addAll(elements);
    }

    @Override
    public boolean add(E e) {
        if(contains(e)) {
            return false;
        }
        return super.add(e);
    }

    @Override
    public E set(int index, E e) {
        int p = indexOf(e);
        if(p >= 0) {
            if(p == index) {
                return null;
            }
        }
        E previous = super.set(index, e);
        if(p >= 0) {
            remove(p);
        }
        return previous;
    }

    @Override
    public void add(int index, E e) {
        if(contains(e)) {
            if(indexOf(e) == index) {
                return;
            }
            remove(e);
        }
        super.add(index, e);
    }

    /**
    * add elements to a list
    *
    * @param elements Iterable elements
    *
    * @return ArrayListSet containing all the specified elements
    **/
    @SuppressWarnings("UnusedReturnValue")
    public boolean addAll(Iterable<? extends E> elements) {
        return addAll(size(), elements);
    }

    /**
    * add elements to a list
    *
    * @param elements Iterable elements
    * @param index The offset from here the elements gets added to the list
    *
    * @return ArrayListSet containing all the specified elements.
    **/
    public boolean addAll(int index, Iterable<? extends E> elements) {
        boolean changed = false;
        for(E e: elements) {
            if(contains(e)) {
                continue;
            }
            add(index++, e);
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size(), (Iterable<? extends E>)c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return addAll(index, (Iterable<? extends E>)c);
    }
}
