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

@FunctionalInterface
public interface ETriFunction<T1, T2, T3, R, E extends Exception> {

    R apply(T1 t1, T2 t2, T3 t3) throws E;

    static <T1, T2, T3, R, E extends Exception> TriFunction<T1, T2, T3, R> wrap(ETriFunction<T1, T2, T3, R, E> function) {
        return (arg1, arg2, arg3) -> {
            try {
                return function.apply(arg1, arg2, arg3);
            } catch (Exception e) {
                throw new SORuntimeException(e);
            }
        };
    }
}