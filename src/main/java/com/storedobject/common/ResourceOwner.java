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
 * Defines an object that owns an auto-closeable resource. Such "resource owners" can be statically registered with the
 * {@link ResourceDisposal#register(ResourceOwner)} method so that the "resource" will be automatically closed when
 * the owner is garbage-collected. The "resource" should not have any reference to the "owner". Otherwise, the "owner"
 * will never get garbage-collected.
 */
public interface ResourceOwner {

    /**
     * Get the "resource" owned by this "resource owner".
     *
     * @return The "resource" owned by this "resource owner".
     */
    AutoCloseable getResource();
}
