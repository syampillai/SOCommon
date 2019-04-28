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

import java.io.*;

/**
 * An output stream that simultaneously writes output to 2 different output streams. This may be
 * used for debugging content of output streams.
 *
 * @author Syam
 */
public class TeeOutputStream extends OutputStream {

    private OutputStream first, second;

    /**
     * Constructor with no output streams provided.
     */
    public TeeOutputStream() {
    }

    /**
     * Constructor.
     *
     * @param first First stream
     * @param second Second stream
     */
    public TeeOutputStream(OutputStream first, OutputStream second) {
        setFirstStream(first);
        setSecondStream(second);
    }

    /**
     * Set the first stream.
     *
     * @param first First stream
     */
    public void setFirstStream(OutputStream first) {
        this.first = first;
    }

    /**
     * Get the first stream.
     *
     * @return The first stream.
     */
    public OutputStream getFirstStream() {
        return first;
    }

    /**
     * Set the second stream.
     *
     * @param second Second stream
     */
    public void setSecondStream(OutputStream second) {
        this.second = second;
    }

    /**
     * Get the second stream.
     *
     * @return Second stream.
     */
    public OutputStream getSecondStream() {
        return second;
    }

    @Override
    public void write(int b) throws IOException {
        if(first != null) {
            first.write(b);
        }
        if(second != null) {
            second.write(b);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        if(first != null) {
            first.write(b);
        }
        if(second != null) {
            second.write(b);
        }
    }

    @Override
    public void write(byte[] b, int offset, int length) throws IOException {
        if(first != null) {
            first.write(b, offset, length);
        }
        if(second != null) {
            second.write(b, offset, length);
        }
    }

    @Override
    public void flush() throws IOException {
        if(first != null) {
            first.flush();
        }
        if(second != null) {
            second.flush();
        }
    }

    @Override
    public void close() throws IOException {
        IOException io = null;
        if(first != null) {
            try {
                first.close();
            } catch (IOException e) {
                io = e;
            }
        }
        if(second != null) {
            second.close();
        }
        if(io != null) {
            throw io;
        }
    }
}