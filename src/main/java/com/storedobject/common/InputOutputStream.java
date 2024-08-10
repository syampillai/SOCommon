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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A class that combines an {@link InputStream} and an {@link OutputStream}. One thread may be writing to it and another
 * may be reading from it.
 *
 * @author Syam
 */

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A class that combines an {@link InputStream} and an {@link OutputStream}. One thread may be writing to it and another
 * may be reading from it.
 */
public class InputOutputStream {

    private final BlockingQueue<Byte> bufferQueue;
    private IStream reader;
    private OStream writer;
    private volatile boolean wEOF = false, rEOF = false;
    private boolean reusable;
    private static final long TIMEOUT_SECONDS = 50;  // Timeout duration

    /**
     * Constructor with a default buffer size of 8K.
     */
    public InputOutputStream() {
        this(8192);
    }

    /**
     * Constructor.
     *
     * @param bufferSize Buffer size
     */
    public InputOutputStream(int bufferSize) {
        if (bufferSize < 64) {
            bufferSize = 64;
        }
        bufferQueue = new ArrayBlockingQueue<>(bufferSize);
    }

    /**
     * Get the input stream for reading.
     *
     * @return Input stream.
     */
    public InputStream getInputStream() {
        if (reader == null) {
            reader = new IStream();
        }
        return reader;
    }

    /**
     * Get the output stream for writing to it.
     *
     * @return Output stream.
     */
    public OutputStream getOutputStream() {
        if (writer == null) {
            writer = new OStream();
        }
        return writer;
    }

    /**
     * Check whether this is reusable or not. (When set to 'reusable' mode, the buffer will be reset and it will possible
     * to start reading from and writing to it again.)
     *
     * @return True or false.
     */
    public final boolean isReusable() {
        return reusable;
    }

    /**
     * Set this in 'reusable' mode. (When set to 'reusable' mode, the buffer will be reset and it will possible
     * to start reading from and writing to it again.)
     *
     * @param reusable True or false
     */
    public void setReusable(boolean reusable) {
        this.reusable = reusable;
    }

    /**
     * Abort the streams. After this operation, this can not be reused too.
     */
    public void abort() {
        IO.close(reader, writer);
        reusable = false;
        rEOF = wEOF = true;
    }

    /**
     * Set an exception to the input stream so that someone will get it when they try to read something from it.
     *
     * @param e Exception to set.
     */
    public void setExternalException(Exception e) {
        reader.external = e;
    }

    private class IStream extends InputStream {

        private Exception external;

        @Override
        public int read() throws IOException {
            if (external != null) {
                throw new IOException(external);
            }
            try {
                if (rEOF) {
                    return -1; // End of stream
                }
                Byte b = bufferQueue.poll(TIMEOUT_SECONDS, TimeUnit.SECONDS);  // Wait with timeout
                if (b == null) {
                    throw new IOException("Read operation timed out");
                }
                return b & 0xFF;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Thread interrupted", e);
            }
        }

        @Override
        public int available() {
            return bufferQueue.size();
        }

        @Override
        public void close() {
            if (rEOF) {
                return;
            }
            if (reusable) {
                bufferQueue.clear();
                rEOF = wEOF = false;
            } else {
                rEOF = true;
            }
        }
    }

    private class OStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {
            if (wEOF) {
                throw new IOException("Stream already closed");
            }
            if (rEOF) {
                throw new IOException("No consumer");
            }
            try {
                boolean success = bufferQueue.offer((byte) b, TIMEOUT_SECONDS, TimeUnit.SECONDS);  // Wait with timeout
                if (!success) {
                    throw new IOException("Write operation timed out");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Thread interrupted", e);
            }
        }

        @Override
        public void close() {
            if (wEOF) {
                return;
            }
            if (reusable) {
                bufferQueue.clear();
                rEOF = wEOF = false;
            } else {
                wEOF = true;
            }
        }
    }
}