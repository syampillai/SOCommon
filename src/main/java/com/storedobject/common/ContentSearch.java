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
* An interface that helps search for value(s) using a string pattern.
*
* @author Syam
**/
public interface ContentSearch {

    /**
     * Searches for value(s) that match the specified string pattern.
     *
     * @param pattern the string pattern used to perform the search.
     * @return an object representing the search result, or null if no match is found.
     */
    Object search(String pattern);

    /**
     * Continues the search for a value that matches the specified string pattern,
     * starting from the given object.
     *
     * @param pattern the string pattern used for the search.
     * @param from the object from which to continue the search.
     * @return an object representing the next search result, or null if no further match is found.
     */
    Object searchNext(String pattern, Object from);
}
