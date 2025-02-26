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
public class InputOutputStream {

    private final byte[] buffer;
    private int wPointer = 0, rPointer = 0;
    private volatile int generated = 0, consumed = 0;
    private boolean wEOF = false, rEOF = false, wWait = false, rWait = false;
    private IStream reader;
    private OStream writer;
    private boolean reusable;

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
        if(bufferSize < 64) {
            bufferSize = 64;
        }
        buffer = new byte[bufferSize];
    }

    /**
     * Get the input stream for reading.
     *
     * @return Input stream.
     */
    public InputStream getInputStream() {
        if(reader == null) {
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
        if(writer == null) {
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
            synchronized (buffer) {
                while (generated == consumed) {
                    if (external != null) {
                        throw new IOException(external);
                    }
                    if (rEOF) {
                        throw new IOException("Stream already closed");
                    }
                    if (wEOF || wWait) {
                        return -1;
                    }
                    try {
                        buffer.wait(5000); // Wait upto 5 seconds or until notified
                    } catch (InterruptedException ignored) {
                    }
                }
                int c = buffer[rPointer] & 0xFF;
                ++consumed;

                if (++rPointer == buffer.length) {
                    rPointer = 0;
                }
                buffer.notify(); // Notify the writer
                return c;
            }
        }

        public int available() {
            return generated - consumed;
        }

        @Override
        public void close() {
            try {
                if (rEOF) {
                    return;
                }
                if (wWait && reusable) {
                    rPointer = wPointer = generated = consumed = 0;
                    rWait = wWait = false;
                } else {
                    if (reusable) {
                        rWait = true;
                    } else {
                        rEOF = true;
                    }
                }
            } finally {
                synchronized (buffer) {
                    buffer.notify();
                }
            }
        }
    }

    private class OStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {
            synchronized (buffer) {
                while ((generated - consumed) >= buffer.length) {
                    if (wEOF) {
                        throw new IOException("Stream already closed");
                    }
                    if (rEOF) {
                        throw new IOException("No consumer");
                    }
                    try {
                        buffer.wait(5000); // Wait upto 5 seconds or until notified
                    } catch (InterruptedException ignored) {
                    }
                }

                buffer[wPointer] = (byte) (0xFF & b);
                ++generated;

                if (++wPointer == buffer.length) {
                    wPointer = 0;
                }
                buffer.notify(); // Notify the reader
            }
        }

        @Override
        public void close() {
            try {
                if (wEOF) {
                    return;
                }
                if (rWait && reusable) {
                    rPointer = wPointer = generated = consumed = 0;
                    rWait = wWait = false;
                } else {
                    if (reusable) {
                        wWait = true;
                    } else {
                        wEOF = true;
                    }
                }
            } finally {
                synchronized (buffer) {
                    buffer.notify();
                }
            }
        }
    }
}