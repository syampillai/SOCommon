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
* An ArrayList implementation
*
* @author Syam
**/
public class ArrayList<E> extends java.util.ArrayList<E> {

    public ArrayList() {
    }

    /**
    * Creates an empty list for the initial capacity passed as parameter
    *
    * @param initialCapacity an integer that specifies the initial capacity of the list
    **/
    public ArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    /**
    * Creates a list using specified collection elements
    *
    * @param c Collection
    **/
    public ArrayList(Collection<? extends E> c) {
        super(c);
    }

    /**
    * Gets the first element of a list
    *
    * @return first element of a list
    **/
    public E firstElement() {
        return size() > 0 ? get(0) : null;
    }

    /**
    * Gets the last element of a list
    *
    * @return last element of a list
    **/    
    public E lastElement() {
        return size() > 0 ? get(size() - 1) : null;
    }
}
