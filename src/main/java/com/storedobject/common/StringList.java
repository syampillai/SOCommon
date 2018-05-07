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
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StringList implements Iterable<String> {

    private static final String[] empty_array = new String[] { };
    public static final StringList EMPTY = new StringList(empty_array);
    protected String[] array;

    public StringList(String list) {
        this(toArray(list));
    }

    public StringList(Collection<String> collection) {
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

    public StringList(String... array) {
        this.array = array == null || array.length == 0 ? empty_array : array;
    }

    public StringList(StringList... list) {
        array = concat(list).array;
    }

    private static String[] toArray(String list) {
        if(list == null || list.length() == 0) {
            return empty_array;
        }
        String s[] = StringUtility.trim(list.split(","));
        if(s.length == 1 && s[0].isEmpty()) {
            return empty_array;
        }
        return s;
    }

    public String get(int index) {
        return array[index];
    }

    public int size() {
        return array.length;
    }

    public String[] array() {
        return array.clone();
    }

    public int indexOf(String element) {
        return indexOf(element, 0);
    }

    public int indexOf(String element, int from) {
        return StringUtility.indexOf(array, element, from);
    }

    public int indexOf(String element, int from, int to) {
        return StringUtility.indexOf(array, element, from, to);
    }

    public int indexOf(Predicate<String> filter) {
        return indexOf(filter, 0, size());
    }

    public int indexOf(Predicate<String> filter, int from) {
        return indexOf(filter, from, size());
    }

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

    public int indexOf(Predicate<String> filter, boolean unique) {
        return indexOf(filter, unique, 0, size());
    }

    public int indexOf(Predicate<String> filter, boolean unique, int from) {
        return indexOf(filter, unique, from, size());
    }

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

    public boolean contains(String element) {
        return contains(element, 0);
    }

    public boolean contains(String element, int from) {
        return indexOf(element, from) >= 0;
    }

    public boolean contains(String element, int from, int to) {
        return indexOf(element, from, to) >= 0;
    }

    public boolean contains(Predicate<String> filter) {
        return contains(filter, 0);
    }

    public boolean contains(Predicate<String> filter, int from) {
        return indexOf(filter, from) >= 0;
    }

    public boolean contains(Predicate<String> filter, int from, int to) {
        return indexOf(filter, from, to) >= 0;
    }

    @Override
    public String toString() {
        return toString(",");
    }

    public String toString(String delimiter) {
        return String.join(delimiter, this);
    }

    public Stream<String> stream() {
        Iterable<String> i = () -> iterator();
        return StreamSupport.stream(i.spliterator(), false);
    }

    @Override
    public Iterator<String> iterator() {
        return new SLIterator(0, array.length);
    }

    public StringList remove(String element) {
        return minus(new StringList(new String[] { element }));
    }

    public StringList minus(StringList another) {
        if(another == null) {
            return this;
        }
        if(another == this) {
            return EMPTY;
        }
        ArrayList<String> a = new ArrayList<>();
        for(String s: this) {
            if(!another.contains(s)) {
                a.add(s);
            }
        }
        return new StringList(a);
    }

    public StringList subList(int index) {
        if(index < 0 || index >= size()) {
            return EMPTY;
        }
        return new SubStringList(this, index, array.length);
    }

    public StringList subList(int start, int end) {
        if(end >= array.length) {
            end = array.length;
        }
        if(start < 0 || start >= end) {
            return EMPTY;
        }
        return new SubStringList(this, start, end);
    }

    public StringList concat(StringList another) {
        return concat(this, another);
    }

    public StringList concat(String[] another) {
        return concat(this.array, another);
    }

    public StringList concat(Collection<String> collection) {
        return concat(this, new StringList(collection));
    }

    public static StringList concat(StringList first, StringList second) {
        if(second == null || second.size() == 0) {
            return first == null ? EMPTY : first;
        }
        if(first == null || first.size() == 0) {
            return second == null ? EMPTY : second;
        }
        String s[] = new String[first.size() + second.size()];
        int i = 0;
        for(String t: first) {
            s[i++] = t;
        }
        for(String t: second) {
            s[i++] = t;
        }
        return new StringList(s);
    }

    public static StringList concat(StringList... list) {
        if(list == null || list.length == 0) {
            return EMPTY;
        }
        if(list.length == 1) {
            return list[0] == null ? EMPTY : list[0];
        }
        if(list.length == 2) {
            return concat(list[0], list[1]);
        }
        int len = 0;
        for(StringList item: list) {
            if(item == null) {
                continue;
            }
            len += item.size();
        }
        String s[] = new String[len];
        int i = 0;
        for(StringList item: list) {
            if(item == null) {
                continue;
            }
            for(String t: item) {
                s[i++] = t;
            }
        }
        return new StringList(s);
    }

    public static StringList concat(String[]... list) {
        if(list == null || list.length == 0) {
            return EMPTY;
        }
        StringList[] slist = new StringList[list.length];
        for(int i = 0; i < slist.length; i++) {
            slist[i] = new StringList(list[i]);
        }
        return concat(slist);
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
            } catch(Throwable t) {
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class SubStringList extends StringList {

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
            String s[] = new String[end - start];
            for(int i = 0; i < s.length; i++) {
                s[i] = array[i + start];
            }
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
