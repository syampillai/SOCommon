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

import java.util.Objects;

public abstract class Range<T> {

    protected final T from, to;

    public Range(T from, T to) {
        this.from = clone(from);
        this.to = clone(to);
    }

    public T getFrom() {
        return clone(from);
    }

    public T getTo() {
        return clone(to);
    }

    protected T clone(T data) {
        return data;
    }

    @Override
    public final boolean equals(Object another) {
        if(another == null || !getClass().isAssignableFrom(another.getClass())) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Range<T> r = (Range<T>)another;
        if(this == another) {
            return true;
        }
        if((from == null && r.from != null) || (r.from == null && from != null)) {
            return false;
        }
        if((to == null && r.to != null) || (r.to == null && to != null)) {
            return false;
        }
        return same(from, r.from) && same(to, r.to);
    }

    public final boolean equals(T from, T to) {
        if((this.from == null && from != null) || (from == null && this.from != null)) {
            return false;
        }
        if((this.to == null && to != null) || (to == null && this.to != null)) {
            return false;
        }
        return same(this.from, from) && same(this.to, to);
    }

    protected boolean same(T one, T two) {
        return Objects.equals(one, two);
    }

    public boolean isValid() {
        if(from == null || to == null) {
            return false;
        }
        return value(from) < value(to) || same(from, to);
    }

    protected long value(@SuppressWarnings("unused") T data) {
        return 0L;
    }

    protected String toString(T data) {
        return data == null ? "" : data.toString();
    }

    @Override
    public String toString() {
        return toString(from) + " - " + toString(to);
    }
}
