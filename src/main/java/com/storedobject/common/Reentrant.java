/*
 * Copyright (c) 2018-2020 Syam Pillai
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
 * An executable that allows to call its {@link #execute()} method again (even from another
 * {@link Thread}) while it is already executing.
 *
 * @author Syam
 */
@FunctionalInterface
public interface Reentrant extends Executable {

    /**
     * This method may return <code>false</code> if some class wants to disallow calling its {@link #execute()}
     * method by some other routine when it is in some specific state. It is up to the application's logic
     * to decide whether this state needs to be checked or not, because the class that implements this interface
     * already declares that its {@link #execute()} method is reentrant.
     *
     * @return Default implementation always returns <code>true</code>.
     */
    default boolean isRenetrant() {
        return true;
    }
}
