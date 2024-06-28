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

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A class to keep a list of {@link String}s. This class is immutable.
 *
 * @author Syam
 */
@SuppressWarnings("NullableProblems")
public class StringList implements Iterable<String>, List<String> {

    private static final String[] empty_array = new String[] { };
    public static final StringList EMPTY = new StringList(empty_array);
    /**
     * Internal array to hold the {@link String} values.
     */
    String[] array;

    private StringList(String list) {
        this(toArray(list));
    }

    private StringList(Collection<String> collection) {
        toArray(collection);
    }

    private StringList(Iterable<String> list) {
        this(list.iterator());
    }

    private StringList(Iterator<String> list) {
        ArrayList<String> array = new ArrayList<>();
        list.forEachRemaining(array::add);
        toArray(array);
    }

    private StringList(String... array) {
        this.array = array == null || array.length == 0 ? empty_array : array;
    }

    private StringList(StringList... list) {
        array = concat(list).array;
    }

    /**
     * Create a string list.
     *
     * @param list Comma delimited list.
     * @return String list created.
     */
    public static StringList create(String list) {
        StringList s = new StringList(list);
        return s.array == empty_array ? EMPTY : s;
    }

    /**
     * Create a string list from a collection.
     *
     * @param collection Collection.
     * @return String list created.
     */
    public static StringList create(Collection<String> collection) {
        if(collection instanceof StringList) {
            return (StringList) collection;
        }
        StringList s = new StringList(collection);
        return s.array == empty_array ? EMPTY : s;
    }

    /**
     * Create a string list from an {@link Iterable}.
     *
     * @param list Iterable.
     * @return String list created.
     */
    public static StringList create(Iterable<String> list) {
        if(list instanceof StringList) {
            return (StringList) list;
        }
        StringList s = new StringList(list);
        return s.array == empty_array ? EMPTY : s;
    }

    /**
     * Create a string list from an {@link Iterator}.
     *
     * @param list Iterator.
     * @return String list created.
     */
    public static StringList create(Iterator<String> list) {
        if(list instanceof StringList) {
            return (StringList) list;
        }
        StringList s = new StringList(list);
        return s.array == empty_array ? EMPTY : s;
    }

    /**
     * Create a string list from an array of strings..
     *
     * @param array Array of strings.
     * @return String list created.
     */
    public static StringList create(String... array) {
        return array == null || array.length == 0 ? EMPTY : new StringList(array);
    }

    /**
     * Create a string list by concatenating one or more lists.
     *
     * @param list Lists.
     * @return String list created.
     */
    public static StringList create(StringList... list) {
        StringList s = new StringList(list);
        return s.array == empty_array ? EMPTY : s;
    }

    /**
     * Create another instance by mapping the elements of this list via a converter function.
     *
     * @param converter Converter function.
     * @return Converted list.
     */
    public StringList map(Function<String, String> converter) {
        String[] a = new String[array.length];
        for(int i = 0; i < a.length; i++) {
            a[i] = converter.apply(array[i]);
        }
        return a.length == 0 ? EMPTY : new StringList(a);
    }

    private void toArray(Collection<String> collection) {
        if(collection == null || collection.size() == 0) {
            array = empty_array;
            return;
        }
        array = new String[collection.size()];
        int i = 0;
        for(String s: collection) {
            array[i++] = s;
        }
    }

    private static String[] toArray(String list) {
        if(list == null || list.length() == 0) {
            return empty_array;
        }
        String[] s = StringUtility.trim(list.split(","));
        if(s.length == 1 && s[0].isEmpty()) {
            return empty_array;
        }
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringList strings = (StringList) o;
        return Arrays.equals(array, strings.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    @Override
    public void add(int index, String element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(String e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return array.length == 0;
    }

    @Override
    public boolean contains(Object o) {
        if(!(o instanceof String)) {
            return false;
        }
        return indexOf((String)o) >= 0;
    }

    @Override
    public Object[] toArray() {
        String[] s = array();
        Object[] a = new Object[s.length];
        System.arraycopy(s, 0, a, 0, a.length);
        return a;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream().noneMatch(item -> indexOf(item) < 0);
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
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
    public String set(int index, String element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        if(o instanceof String s) {
            return indexOf(s);
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if(!(o instanceof String s)) {
            return -1;
        }
        int p = indexOf(s), i;
        while(true) {
            i = indexOf(s, p + 1);
            if(i < 0) {
                break;
            }
            p = i;
        }
        return p;
    }

    @Override
    public ListIterator<String> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<String> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String get(int index) {
        return array[index];
    }

    @Override
    public int size() {
        return array.length;
    }

    /**
     * Get a copy of the elements as an array.
     *
     * @return Array containing elements of this list.
     */
    public String[] array() {
        return array.clone();
    }

    /**
     * Index of an element in the list.
     *
     * @param element Element.
     * @return Index position or -1 if not found.
     */
    public int indexOf(String element) {
        return indexOf(element, 0);
    }

    /**
     * Index of an element in the list.
     *
     * @param element Element.
     * @param from Start the search from this position.
     * @return Index position or -1 if not found.
     */
    public int indexOf(String element, int from) {
        return StringUtility.indexOf(array, element, from);
    }

    /**
     * Index of an element in the list.
     *
     * @param element Element.
     * @param from Start the search from this position.
     * @param to Stop the search before this position.
     * @return Index position or -1 if not found.
     */
    public int indexOf(String element, int from, int to) {
        return StringUtility.indexOf(array, element, from, to);
    }

    /**
     * Returns the index of the first occurrence of an element in the list that satisfies the given filter.
     *
     * @param filter the predicate used to test elements in the list
     * @return the index of the first matching element, or -1 if no element satisfies the filter
     */
    public int indexOf(Predicate<String> filter) {
        return indexOf(filter, 0, size());
    }

    /**
     * Returns the index of the first element that matches the given predicate starting from the specified index.
     *
     * @param filter the predicate used to match elements
     * @param from   the index to start the search from (inclusive)
     * @return the index of the first matching element or -1 if no match is found
     */
    public int indexOf(Predicate<String> filter, int from) {
        return indexOf(filter, from, size());
    }

    /**
     * Index of an element in the list that matches the given predicate.
     *
     * @param filter Filter predicate.
     * @param from Start the search from this position.
     * @param to Stop the search before this position.
     * @return Index position or -1 if not found.
     */
    public int indexOf(Predicate<String> filter, int from, int to) {
        if(to > size()) {
            to = size();
        }
        for(int i = from; i < to; i++) {
            if(filter.test(get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Index of an element in the list that matches the given predicate.
     *
     * @param filter Filter predicate.
     * @param unique Whether uniqueness to be checked or not.
     * @return Index position or -1 if not found.
     */
    public int indexOf(Predicate<String> filter, boolean unique) {
        return indexOf(filter, unique, 0, size());
    }

    /**
     * Index of an element in the list that matches the given predicate.
     *
     * @param filter Filter predicate.
     * @param unique Whether uniqueness to be checked or not.
     * @param from Start the search from this position.
     * @return Index position or -1 if not found.
     */
    public int indexOf(Predicate<String> filter, boolean unique, int from) {
        return indexOf(filter, unique, from, size());
    }

    /**
     * Index of an element in the list that matches the given predicate.
     *
     * @param filter Filter predicate.
     * @param unique Whether uniqueness to be checked or not.
     * @param from Start the search from this position.
     * @param to Stop the search before this position.
     * @return Index position or -1 if not found.
     */
    public int indexOf(Predicate<String> filter, boolean unique, int from, int to) {
        if(!unique) {
            return indexOf(filter, from, to);
        }
        if(to > size()) {
            to = size();
        }
        for(int i = from; i < to; i++) {
            if(filter.test(get(i))) {
                return indexOf(filter, i + 1, size()) < 0 ? i : -1;
            }
        }
        return -1;
    }

    /**
     * Check whether this list contains the given element or not.
     *
     * @param element Element.
     * @return True/false.
     */
    public boolean contains(String element) {
        return contains(element, 0);
    }

    /**
     * Check whether this list contains the given element or not.
     *
     * @param element Element.
     * @param from Start the search from this position.
     * @return True/false.
     */
    public boolean contains(String element, int from) {
        return indexOf(element, from) >= 0;
    }

    /**
     * Check whether this list contains the given element or not.
     *
     * @param element Element.
     * @param from Start the search from this position.
     * @param to Stop the search before this position.
     * @return True/false.
     */
    public boolean contains(String element, int from, int to) {
        return indexOf(element, from, to) >= 0;
    }

    /**
     * Check whether this list contains any element that matches a given predicate or not.
     *
     * @param filter Filter predicate.
     * @return True/false.
     */
    public boolean contains(Predicate<String> filter) {
        return contains(filter, 0);
    }

    /**
     * Check whether this list contains any element that matches a given predicate or not.
     *
     * @param filter Filter predicate.
     * @param from Start the search from this position.
     * @return True/false.
     */
    public boolean contains(Predicate<String> filter, int from) {
        return indexOf(filter, from) >= 0;
    }

    /**
     * Check whether this list contains any element that matches a given predicate or not.
     *
     * @param filter Filter predicate.
     * @param from Start the search from this position.
     * @param to Stop the search before this position.
     * @return True/false.
     */
    public boolean contains(Predicate<String> filter, int from, int to) {
        return indexOf(filter, from, to) >= 0;
    }

    @Override
    public String toString() {
        return toString(",");
    }

    /**
     * Concatenate the elements of this list into a {@link String} with the given delimiter.
     *
     * @param delimiter Delimiter.
     * @return Concatenated value.
     */
    public String toString(String delimiter) {
        return String.join(delimiter, this);
    }

    /**
     * Stream the elements.
     *
     * @return Stream.
     */
    public Stream<String> stream() {
        Iterable<String> i = this;
        return StreamSupport.stream(i.spliterator(), false);
    }

    @Override
    public Iterator<String> iterator() {
        return new SLIterator(0, array.length);
    }

    /**
     * Remove an element from the list.
     *
     * @param element Element to remove.
     * @return Modified list.
     */
    public StringList remove(String element) {
        return minus(StringList.create(new String[] { element }));
    }

    /**
     * Remove elements from this list that are elements of another list.
     *
     * @param another Another list.
     * @return Modified list.
     */
    public StringList minus(StringList another) {
        if(another == null || another.isEmpty()) {
            return this;
        }
        if(another == this) {
            return EMPTY;
        }
        return StringList.create(stream().filter(s -> !another.contains(s)).collect(Collectors.toList()));
    }

    /**
     * Get a sub-list.
     *
     * @param index Index from which sublist to be started.
     * @return Sub-list.
     */
    public StringList subList(int index) {
        if(index < 0 || index >= size()) {
            return EMPTY;
        }
        return new SubStringList(this, index, array.length);
    }

    /**
     * Get a sub-list.
     *
     * @param start Starting index.
     * @param end Ending index (exclusive).
     * @return Sub-list.
     */
    public StringList subList(int start, int end) {
        if(end >= array.length) {
            end = array.length;
        }
        if(start < 0 || start >= end) {
            return EMPTY;
        }
        return new SubStringList(this, start, end);
    }

    /**
     * Concatenate another list to this list.
     *
     * @param another Another list.
     * @return Concatenated list.
     */
    public StringList concat(StringList another) {
        return concat(this, another);
    }

    /**
     * Concatenate another list to this list.
     *
     * @param another Another list.
     * @return Concatenated list.
     */
    public StringList concat(String[] another) {
        return concat(this.array, another);
    }

    /**
     * Concatenate a collection to this list.
     *
     * @param collection Collection.
     * @return Concatenated list.
     */
    public StringList concat(Collection<String> collection) {
        return concat(this, StringList.create(collection));
    }

    /**
     * Concatenate 2 lists..
     *
     * @param first First list (could be null).
     * @param second Second list (could be null).
     * @return Concatenated list.
     */
    public static StringList concat(StringList first, StringList second) {
        if(second == null || second.size() == 0) {
            return first == null ? EMPTY : first;
        }
        if(first == null || first.size() == 0) {
            return second;
        }
        String[] s = new String[first.size() + second.size()];
        int i = 0;
        for(String t: first) {
            s[i++] = t;
        }
        for(String t: second) {
            s[i++] = t;
        }
        return StringList.create(s);
    }

    /**
     * Concatenate an array of lists.
     *
     * @param lists Array of lists.
     * @return Concatenated list.
     */
    public static StringList concat(StringList... lists) {
        if(lists == null || lists.length == 0) {
            return EMPTY;
        }
        if(lists.length == 1) {
            return lists[0] == null ? EMPTY : lists[0];
        }
        if(lists.length == 2) {
            return concat(lists[0], lists[1]);
        }
        int len = 0;
        for(StringList item: lists) {
            if(item == null) {
                continue;
            }
            len += item.size();
        }
        String[] s = new String[len];
        int i = 0;
        for(StringList item: lists) {
            if(item == null) {
                continue;
            }
            for(String t: item) {
                s[i++] = t;
            }
        }
        return StringList.create(s);
    }

    /**
     * Concatenate an array of lists.
     *
     * @param lists Array of lists.
     * @return Concatenated list.
     */
    public static StringList concat(String[]... lists) {
        if(lists == null || lists.length == 0) {
            return EMPTY;
        }
        StringList[] slist = new StringList[lists.length];
        for(int i = 0; i < slist.length; i++) {
            slist[i] = StringList.create(lists[i]);
        }
        return concat(slist);
    }

    /**
     * Copy the elements of this list to a buffer.
     *
     * @param buffer Buffer to copy to
     * @return Number of elements copied. Zero will be returned if <code>buffer</code> is <code>null</code>.
     */
    public int copyTo(String[] buffer) {
        return copyTo(0, array.length, buffer);
    }

    /**
     * Copy the elements of this list to a buffer.
     *
     * @param startingIndex Starting index of the list from where copy begins
     * @param buffer Buffer to copy to
     * @return Number of elements copied. Zero will be returned if <code>buffer</code> is <code>null</code>.
     */
    public int copyTo(int startingIndex, String[] buffer) {
        return copyTo(startingIndex, array.length, buffer);
    }

    /**
     * Copy the elements of this list to a buffer.
     *
     * @param startingIndex Starting index of the list from where copy begins
     * @param count Number of elements to copy
     * @param buffer Buffer to copy to
     * @return Number of elements copied. Zero will be returned if <code>buffer</code> is <code>null</code>.
     */
    public int copyTo(int startingIndex, int count, String[] buffer) {
        return copyTo(startingIndex, count, buffer, array.length);
    }

    /**
     * Copy the elements of this list to a buffer.
     *
     * @param buffer Buffer to copy to
     * @param startingBufferIndex Starting index of the buffer from where copy begins
     * @return Number of elements copied. Zero will be returned if <code>buffer</code> is <code>null</code>.
     */
    public int copyTo(String[] buffer, int startingBufferIndex) {
        return copyTo(0, array.length, buffer, startingBufferIndex);
    }

    /**
     * Copy the elements of this list to a buffer.
     *
     * @param buffer Buffer to copy to
     * @param startingBufferIndex Starting index of the buffer from where copy begins
     * @param count Number of elements to copy
     * @return Number of elements copied. Zero will be returned if <code>buffer</code> is <code>null</code>.
     */
    public int copyTo(String[] buffer, int startingBufferIndex, int count) {
        return copyTo(0, count, buffer, startingBufferIndex);
    }

    /**
     * Copy the elements of this list to a buffer. Same as {@link #copyTo(int, int, String[], int)}.
     *
     * @param startingIndex Starting index of the list from where copy begins
     * @param buffer Buffer to copy to
     * @param startingBufferIndex Starting index of the buffer from where copy begins
     * @param count Number of elements to copy
     * @return Number of elements copied. Zero will be returned if <code>buffer</code> is <code>null</code>.
     */
    public int copyTo(int startingIndex, String[] buffer, int startingBufferIndex, int count) {
        return copyTo(startingIndex, count, buffer, startingBufferIndex);
    }

    /**
     * Copy the elements of this list to a buffer.
     *
     * @param startingIndex Starting index of the list from where copy begins
     * @param count Number of elements to copy
     * @param buffer Buffer to copy to
     * @param startingBufferIndex Starting index of the buffer from where copy begins
     * @return Number of elements copied. Zero will be returned if <code>buffer</code> is <code>null</code>.
     */
    public int copyTo(int startingIndex, int count, String[] buffer, int startingBufferIndex) {
        if(startingIndex < 0 || startingBufferIndex < 0 || count <= 0 || buffer == null) {
            return 0;
        }
        int c = 0;
        while(count > 0 && startingBufferIndex < buffer.length && startingIndex < array.length) {
            buffer[startingBufferIndex] = array[startingBufferIndex];
            ++startingBufferIndex;
            ++startingIndex;
            --count;
            ++c;
        }
        return c;
    }

    private class SLIterator implements Iterator<String> {

        private int start;
        private final int end;

        private SLIterator(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public boolean hasNext() {
            return start >= 0 && start < end;
        }

        @Override
        public String next() {
            try {
                if(start < end) {
                    return array[start++];
                }
            } catch(Throwable ignored) {
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class SubStringList extends StringList {

        private final int start, end;

        private SubStringList(StringList parent, int start, int end) {
            super(parent.array);
            this.start = start;
            this.end = end;
        }

        @Override
        public String get(int index) {
            int i = start + index;
            if(i < 0 || i >= end) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return array[i];
        }

        @Override
        public int size() {
            return end - start;
        }

        @Override
        public String[] array() {
            String[] s = new String[end - start];
            System.arraycopy(array, start, s, 0, s.length);
            return s;
        }

        @Override
        public int indexOf(String element, int from) {
            int i = StringUtility.indexOf(array, element, from + start);
            return i >= 0 ? (i - start) : -1;
        }

        @Override
        public int indexOf(String element, int from, int to) {
            int i = StringUtility.indexOf(array, element, from + start, to + start);
            return i >= 0 ? (i - start) : -1;
        }

        @Override
        public Iterator<String> iterator() {
            return new SLIterator(start, end);
        }

        @Override
        public StringList subList(int index) {
            return super.subList(start + index);
        }

        @Override
        public StringList subList(int start, int end) {
            return super.subList(this.start + start, this.start + end);
        }
    }
}