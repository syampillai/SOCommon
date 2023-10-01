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
/**
* A functional interface that implements TriFunction. In a TriFunction we specify the types of 3 arguments and a return value.
*
* @author Syam
**/
@FunctionalInterface
public interface TriFunction<T1, T2, T3, R> {
    /**
    * Accepts 3 arguments of the specified types T1, T2, T3 and returns a value of specified type R
    * 
    * @param t1 First argument passed of type T1
    * @param t2 Second argument passed of type T2
    * @param t3 Third argument passed of type T3
    **/
    R accept(T1 t1, T2 t2, T3 t3);
}
