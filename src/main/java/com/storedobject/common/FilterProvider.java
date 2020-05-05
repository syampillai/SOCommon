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
 * Filter provider is used to define a provider of some sort of "filter condition". A typically example is a class
 * that provides a programmatically generated SQL condition.
 *
 * @author Syam
 */
@FunctionalInterface
public interface FilterProvider {

    /**
     * Get the filter condition string.
     *
     * @return The filter condition string.
     */
    String getFilterCondition();
}
