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
 * An implementation of a simple {@link ResourceOwner}.<br>
 *     Usage note: A reference to the instance of this class must be kept inside the class where the instance is used.
 *     Otherwise, the "resource" will be garbage-collected soon because this class instance itself will get
 *     garbage-collected.
 *
 * @author Syam
 */
public class ResourceHolder implements ResourceOwner {

    private AutoCloseable closeable;

    /**
     * Constructor.
     */
    public ResourceHolder() {
    }

    /**
     * Set the resource. This will immediately register this owner with {@link ResourceDisposal}.
     *
     * @param closeable Resource.
     */
    public void setResource(AutoCloseable closeable) {
        if(closeable == null) {
            return;
        }
        if(this.closeable != null) {
            try {
                this.closeable.close();
            } catch (Exception ignored) {
            }
        }
        this.closeable = closeable;
        ResourceDisposal.register(this);
    }

    /**
     * Get the resource.
     *
     * @return Resource.
     */
    @Override
    public final AutoCloseable getResource() {
        return closeable;
    }
}
