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

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A static class that can be used to execute commands and tasks.
 * Uses a static instance of {@link java.util.concurrent.ExecutorService}.
 *
 * @author Syam
 */
public class Executor {

    private static final java.util.concurrent.ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * Submit a command for execution.
     *
     * @param command Command to execute.
     * @return {@link Future} to check the status.
     */
    public static Future<?> execute(Runnable command) {
        return executor.submit(command);
    }

    /**
     * Submit a command for execution.
     *
     * @param task Task to be carried out.
     * @param <V> Expected return value type.
     * @return {@link Future} to check the status.
     */
    public static <V> Future<V> execute(Callable<V> task) {
        return executor.submit(task);
    }
}
