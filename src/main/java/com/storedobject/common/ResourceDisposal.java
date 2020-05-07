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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Resource disposal class. Any {@link AutoCloseable} resource can be statically register with this class so that it
 * will be closed when garbage collected.
 *
 * @author Syam
 */
public final class ResourceDisposal {

    private static final ReferenceQueue<ResourceHolder> referenceQueue = new ReferenceQueue<>();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final static Set<ResourceHolderReference> stack = new HashSet<>();
    private static boolean debug;
    private static Cleaner cleaner;

    /**
     * Register me so that I will get closed automatically when I am garbage collected.
     *
     * @param closeable Any closeable resource. <code>Null</code> values will be ignored.
     */
    public static void register(AutoCloseable closeable) {
        if(closeable != null) {
            ResourceHolder resourceHolder = new ResourceHolder(closeable);
            new ResourceHolderReference(resourceHolder, referenceQueue);
            if (cleaner == null) {
                createCleaner();
            }
        }
    }

    /**
     * Dump class names of the resources that are still not closed because their holders are not yet garbage collected.
     * This could be used for debugging purposes.
     */
    public static void dumpResources() {
        debug = true;
        System.gc();
        AtomicInteger i = new AtomicInteger(0);
        synchronized (stack) {
            stack.forEach(r -> {
                if(r != null) {
                    if(r.resource != null) {
                        System.err.println(((ResourceCleaner)r.resource).closeable.getClass());
                        i.incrementAndGet();
                    }
                }
            });
            System.err.println("Resources: " + i);
        }
    }

    private synchronized static void createCleaner() {
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
                    if(!debug) {
                        release(reference);
                    } else {
                        synchronized (stack) {
                            release(reference);
                        }
                    }
                } catch (InterruptedException ignored) {
                }
            }
        }

        private void release(ResourceHolderReference reference) {
            stack.remove(reference);
            reference.close();
            reference.clear();
        }
    }

    private static class ResourceHolderReference extends PhantomReference<ResourceHolder> {

        private AutoCloseable resource;

        public ResourceHolderReference(ResourceHolder referent, ReferenceQueue<? super ResourceHolder> q) {
            super(referent, q);
            resource = referent.getResource();
            if(resource == null) {
                throw new SORuntimeException("Resource is null");
            }
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
            resource = null;
        }
    }

    private static Timer timer;
    private static boolean gc;

    /**
     * Invoke JVM's garbage collector within the next 30 seconds.
     */
    public static void gc() {
        gc = true;
        if(timer == null) {
            createGCTimer();
        }
    }

    private synchronized static void createGCTimer() {
        if(timer != null) {
            return;
        }
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(gc) {
                    System.gc();
                    gc = false;
                }
            }
        };
        timer = new Timer("GC");
        timer.scheduleAtFixedRate(task, 1L, 30000L);
    }

    private static class ResourceHolder {

        private final AutoCloseable closeable;

        private ResourceHolder(AutoCloseable closeable) {
            this.closeable = closeable;
        }

        public AutoCloseable getResource() {
            return new ResourceCleaner(closeable);
        }
    }

    private static class ResourceCleaner implements AutoCloseable {

        private final AutoCloseable closeable;

        private ResourceCleaner(AutoCloseable closeable) {
            this.closeable = closeable;
        }

        @Override
        public void close() {
            try {
                closeable.close();
            } catch (Throwable ignored) {
            }
        }
    }
}