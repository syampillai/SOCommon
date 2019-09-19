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

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Set;

/**
 * Resource disposal class. {@link ResourceHolder}s can statically register with this class so that their resources
 * will be closed when garbage collected.
 *
 * @author Syam
 */
public final class ResourceDisposal {

    private static final ReferenceQueue<ResourceHolder> referenceQueue = new ReferenceQueue<>();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static Set<ResourceHolderReference> stack = new HashSet<>();
    private static Cleaner cleaner;

    public static synchronized void register(ResourceHolder resource) {
        new ResourceHolderReference(resource, referenceQueue);
        if(cleaner == null) {
            cleaner = new Cleaner();
        }
    }

    private static class Cleaner extends Thread {

        private Cleaner() {
            setDaemon(true);
            start();
        }

        @Override
        public void run() {
            //noinspection InfiniteLoopStatement
            while(true) {
                try {
                    ResourceHolderReference reference = (ResourceHolderReference) referenceQueue.remove();
                    stack.remove(reference);
                    reference.close();
                    reference.clear();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    private static class ResourceHolderReference extends PhantomReference<ResourceHolder> {

        private AutoCloseable resource;

        public ResourceHolderReference(ResourceHolder referent, ReferenceQueue<? super ResourceHolder> q) {
            super(referent, q);
            resource = referent.getResource();
            stack.add(this);
        }

        private void close() {
            AutoCloseable r = resource;
            if(r != null) {
                try {
                    r.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }
}