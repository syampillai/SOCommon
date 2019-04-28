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

import java.util.Iterator;
import java.util.function.Function;

public class ConvertedIterable<FROM, TO> implements Iterable<TO> {

    private Iterable<FROM> original;
    private Function<FROM, TO> converter;

    public ConvertedIterable(Iterable<FROM> original, Function<FROM, TO> converter) {
        this.original = original;
        this.converter = converter;
    }

    @Override
    public Iterator<TO> iterator() {
        return new Loop<>(original.iterator());
    }

    private class Loop<E> implements Iterator<E> {

        private Iterator<FROM> loop;

        private Loop(Iterator<FROM> loop) {
            this.loop = loop;
        }

        @Override
        public boolean hasNext() {
            return loop.hasNext();
        }

        @SuppressWarnings("unchecked")
        @Override
        public E next() {
            return (E)converter.apply(loop.next());
        }
    }
}