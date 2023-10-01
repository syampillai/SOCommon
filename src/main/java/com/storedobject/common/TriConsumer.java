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
* A functional interface that accepts 3 arguments of type T1, T2, T3 for performing an operation with no return value.
*
* @author Syam
**/
@FunctionalInterface
public interface TriConsumer<T1, T2, T3> {
    
    /**
    * A method that accepts 3 values for performing an operation on them without returing any result after the operation.
    *
    * @param t1 First parameter of type T1.
    * @param t2 Second parameter of type T2.
    * @param t3 Third parameter of type T3.
    **/
    void accept(T1 t1, T2 t2, T3 t3);
}
