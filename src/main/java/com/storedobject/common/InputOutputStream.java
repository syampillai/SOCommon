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

public class InputOutputStream {

    private final byte[] buffer;
    private int wPointer = 0, rPointer = 0, generated = 0, consumed = 0;
    private boolean wEOF = false, rEOF = false;
    private IStream reader;
    private OStream writer;
    private boolean reusable;

    public InputOutputStream() {
        this(8192);
    }

    public InputOutputStream(int bufferSize) {
        if(bufferSize < 64) {
            bufferSize = 64;
        }
        buffer = new byte[bufferSize];
    }

    public InputStream getInputStream() {
        if(reader == null) {
            reader = new IStream();
        }
        return reader;
    }

    public OutputStream getOutputStream() {
        if(writer == null) {
            writer = new OStream();
        }
        return writer;
    }

    public boolean isReusable() {
        return reusable;
    }

    public void setReusable(boolean reusable) {
        this.reusable = reusable;
    }

    private class IStream extends InputStream {

        @Override
        public int read() throws IOException {
            if(rEOF) { // Reader was closed
                throw new IOException("Stream already closed");
            }
            while(generated == consumed) {
                if(wEOF) { // Writing was closed
                    return -1;
                }
                Thread.yield();
            }
            int c;
            synchronized (buffer) {
                c = buffer[rPointer] & 0xFF;
            }
            ++consumed;
            if(++rPointer == buffer.length) {
                rPointer = 0;
            }
            return c;
        }

        public int available() {
            return generated - consumed;
        }

        @Override
        public void close() {
            if(rEOF) {
                return;
            }
            if(wEOF && isReusable()) {
                rPointer = wPointer = generated = consumed = 0;
                rEOF = wEOF = false;
            } else {
                rEOF = true;
            }
        }
    }

    private class OStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {
            if(wEOF) { // Writer was closed
                throw new IOException("Stream already closed");
            }
            while(true) {
                if(rEOF) {
                    throw new IOException("No consumer");
                }
                if((generated - consumed) < buffer.length) {
                    break;
                }
                Thread.yield();
            }
            synchronized (buffer) {
                buffer[wPointer] = (byte)(0xFF & b);
            }
            ++generated;
            if(++wPointer == buffer.length) {
                wPointer = 0;
            }
        }

        @Override
        public void close() {
            if(wEOF) {
                return;
            }
            if(rEOF && isReusable()) {
                rPointer = wPointer = generated = consumed = 0;
                rEOF = wEOF = false;
            } else {
                wEOF = true;
            }
        }
    }
}