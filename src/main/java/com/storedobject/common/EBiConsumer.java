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

import java.util.function.BiConsumer;

@FunctionalInterface
public interface EBiConsumer<T1, T2, E extends Exception> {

    void accept(T1 t1, T2 t2) throws E;

    static <T1, T2, E extends Exception> BiConsumer<T1, T2> wrap(EBiConsumer<T1, T2, E> consumer) {
        return (arg1, arg2) -> {
            try {
                consumer.accept(arg1, arg2);
            } catch (Exception e) {
                throw new SORuntimeException(e);
            }
        };
    }
}