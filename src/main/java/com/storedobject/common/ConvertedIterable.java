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

/**
 * A decorator class that transforms elements of an iterable by applying a conversion function.
 * This class allows iteration over a collection of elements, converting each element from its
 * original type to a different target type during iteration.
 *
 * @param <FROM> the type of the elements in the original iterable
 * @param <TO> the type of the elements in the converted iterable
 *
 * @author Syam
 */
public class ConvertedIterable<FROM, TO> implements Iterable<TO> {

    private final Iterable<FROM> original;
    private final Function<FROM, TO> converter;

    /**
     * Constructs a ConvertedIterable instance by wrapping an existing iterable and applying a
     * conversion function to its elements.
     *
     * @param original The original iterable containing elements of type FROM.
     * @param converter The function used to convert elements from type FROM to type TO.
     */
    public ConvertedIterable(Iterable<FROM> original, Function<FROM, TO> converter) {
        this.original = original;
        this.converter = converter;
    }

    @Override
    public Iterator<TO> iterator() {
        return new Loop<>(original.iterator());
    }

    private class Loop<E> implements Iterator<E> {

        private final Iterator<FROM> loop;

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