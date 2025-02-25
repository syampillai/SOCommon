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
 * Resource disposal class. Any {@link ResourceOwner} can statically register with this class so that its
 * "resource" will be automatically closed when it is garbage-collected.
 *
 * @author Syam
 */
public final class ResourceDisposal {

    private static final ReferenceQueue<ResourceOwner> referenceQueue = new ReferenceQueue<>();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final static Set<ResourceOwnerReference> stack = new HashSet<>();
    private static boolean debug;
    private static Cleaner cleaner;

    /**
     * Register me so that my resource will get closed automatically when I am garbage collected.
     *
     * @param resourceOwner Owner of the resource. <code>Null</code> values will be ignored.
     */
    public static void register(ResourceOwner resourceOwner) {
        AutoCloseable resource;
        if(resourceOwner == null || (resource = resourceOwner.getResource()) == null) {
            throw new NullPointerException("Owner and resource can not be null");
        }
        new ResourceOwnerReference(resourceOwner, resource, referenceQueue);
        if (cleaner == null) {
            createCleaner();
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
                        System.err.println(r.resource.getClass());
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

    private static class Cleaner implements Runnable{

        private Cleaner() {
            Thread.ofVirtual().start(this);
        }

        @Override
        public void run() {
            while(true) {
                try {
                    ResourceOwnerReference reference = (ResourceOwnerReference) referenceQueue.remove();
                    if(!debug) {
                        release(reference);
                    } else {
                        synchronized (stack) {
                            release(reference);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                    return; // Exit now, we may have been interrupted for shutting down
                }
            }
        }

        private void release(ResourceOwnerReference reference) {
            stack.remove(reference);
            reference.close();
            reference.clear();
        }
    }

    private static class ResourceOwnerReference extends PhantomReference<ResourceOwner> {

        private AutoCloseable resource;

        public ResourceOwnerReference(ResourceOwner referent, AutoCloseable resource, ReferenceQueue<? super ResourceOwner> q) {
            super(referent, q);
            this.resource = resource;
            stack.add(this);
        }

        private void close() {
            IO.close(resource);
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
}