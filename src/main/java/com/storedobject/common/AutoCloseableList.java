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
 * A list of {@link AutoCloseable} objects. This itself implements {@link AutoCloseable} and it closes all its
 * elements when the list itself is closed.
 *
 * @author Syam
 */
public class AutoCloseableList extends ArrayList<AutoCloseable> implements AutoCloseable {

    /**
     * Close all elements in this list. Any {@link Exception} thrown while closing any of its element will be ignored.
     */
    @Override
    public final void close() {
        forEach(IO::close);
        clear();
    }
}
