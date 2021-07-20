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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
* An array implementation with variable size.
*
* @author Syam
**/
public class Array<T> implements Loop<T>, List<T> {

    private final T[] array;
    private final int lower, upper;
    private int i;

    /**
    * Constructor
    * 
    * @param array Array
    **/
    public Array(T[] array) {
        this(array, 0, array.length);
    }

    /**
    * Constructor
    * 
    * @param array Array
    * @param lower Position of the first element in the array
    **/
    public Array(T[] array, int lower) {
        this(array, lower, array.length);
    }

    /**
    * Constructor
    * 
    * @param array
    * @param lower Position of the first element in the array
    * @param upper Position of the last element in the array
    **/
    public Array(T[] array, int lower, int upper) {
        this.array = array;
        if(array == null) {
            this.lower = this.upper = i = 0;
            return;
        }
        if(lower < 0) {
            lower = 0;
        }
        this.lower = lower;
        if(upper > array.length) {
            upper = array.length;
        }
        if(upper < lower) {
            upper = lower;
        }
        this.upper = upper;
        i = lower;
    }

    @Override
    public String toString() {
        return toString(", ");
    }

    public String toString(String delimiter) {
        return "(" + stream().map(StringUtility::toString).collect(Collectors.joining(delimiter)) + ")";
    }

    @Override
    public Iterator<T> iterator() {
        return new Array<>(array, lower, upper);
    }

    @Override
    public boolean hasNext() {
        return array != null && i < upper;
    }

    @Override
    public T next() {
        if(array != null && i < upper) {
            T v = array[i];
            ++i;
            return v;
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return upper - lower;
    }

    @Override
    public boolean isEmpty() {
        return lower == upper;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Object[] toArray() {
        if(array == null || (lower == 0 && upper == array.length)) {
            //noinspection ConstantConditions
            return array;
        }
        Object[] a = new Object[upper - lower];
        System.arraycopy(array, lower, a, 0, a.length);
        return a;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A> A[] toArray(A[] a) {
        if(array == null) {
            //noinspection ConstantConditions
            return null;
        }
        if(a.length >= (upper - lower)) {
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(array, lower, a, 0, upper - lower);
            for(int i = upper; i < a.length; i++) {
                a[i] = null;
            }
            return a;
        }
        return (A[]) Arrays.copyOfRange(array, lower, upper);
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object obj: c) {
            if(!contains(obj)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int index) {
        index += lower;
        if(index >= 0 && index < upper) {
            return array[index];
        }
        throw new NoSuchElementException();
    }

    @Override
    public T set(int index, T element) {
        index += lower;
        if(index >= 0 && index < upper) {
            T e = array[index];
            array[index] = element;
            return e;
        }
        throw new ArrayIndexOutOfBoundsException(index - lower);
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        if(array == null) {
            return -1;
        }
        T v;
        for(int i = lower; i < upper; i++) {
            v = array[i];
            if(v == o || v.equals(o)) {
                return i - lower;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if(array == null) {
            return -1;
        }
        T v;
        for(int i = upper - 1; i >= lower; i--) {
            v = array[i];
            if(v == o || v.equals(o)) {
                return i - lower;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new IList(lower);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new IList(lower + index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new Array<>(array, lower + fromIndex, lower + toIndex);
    }

    private class IList implements ListIterator<T> {

        private int i;

        private IList(int start) {
            if(start < lower) {
                start = lower;
            } else if(start > upper) {
                start = upper;
            }
            i = start;
        }

        @Override
        public boolean hasNext() {
            return i < upper;
        }

        @Override
        public T next() {
            if(i == upper) {
                throw new NoSuchElementException();
            }
            return array[i++];
        }

        @Override
        public boolean hasPrevious() {
            return i > lower;
        }

        @Override
        public T previous() {
            if(i == lower) {
                throw new NoSuchElementException();
            }
            return array[--i];
        }

        @Override
        public int nextIndex() {
            int n = i + 1;
            if(n > upper) {
                n = upper;
            }
            return n - lower;
        }

        @Override
        public int previousIndex() {
            int p = i - 1;
            if(p < lower) {
                return -1;
            }
            return p - lower;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(T e) {
            array[i] = e;
        }

        @Override
        public void add(T e) {
            throw new UnsupportedOperationException();
        }
    }
}
