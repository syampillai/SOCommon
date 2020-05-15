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
 * Create an {@link InputStream} such that whatever is read from this stream should be the same as whatever written to
 * an associated {@link OutputStream}! Beware: The associated {@link OutputStream} should be practically written to
 * from a different thread.
 *
 * @author Syam
 */
public class InvertedInputStream extends InputStream {

    private final InputOutputStream inout = new InputOutputStream();

    /**
     * Constructor.
     */
    public InvertedInputStream() {
    }

    @Override
    public int read() throws IOException {
        return inout.getInputStream().read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return super.read(b);
    }

    /**
     * Get the associated {@link OutputStream}.
     *
     * @return {@link OutputStream}.
     */
    public OutputStream getOutputStream() {
        return inout.getOutputStream();
    }

    @Override
    public void close() throws IOException {
        super.close();
        IO.close(inout.getInputStream(), inout.getOutputStream());
    }
}
