/*
 * Copyright (c) 2018-2024 Syam Pillai
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
 * Daemon thread.
 *
 * @author Syam
 */
public class DaemonThread extends Thread {

    public DaemonThread() {
        setDaemon(true);
    }

    public DaemonThread(Runnable target) {
        super(target);
        setDaemon(true);
    }

    public DaemonThread(ThreadGroup group, Runnable target) {
        super(group, target);
        setDaemon(true);
    }

    public DaemonThread(String name) {
        super(name);
        setDaemon(true);
    }

    public DaemonThread(ThreadGroup group, String name) {
        super(group, name);
        setDaemon(true);
    }

    public DaemonThread(Runnable target, String name) {
        super(target, name);
        setDaemon(true);
    }

    public DaemonThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        setDaemon(true);
    }

    public DaemonThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
        setDaemon(true);
    }

    public DaemonThread(ThreadGroup group, Runnable target, String name, long stackSize, boolean inheritThreadLocals) {
        super(group, target, name, stackSize, inheritThreadLocals);
        setDaemon(true);
    }
}
