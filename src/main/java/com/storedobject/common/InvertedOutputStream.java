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
 * Create an {@link java.io.OutputStream} such that whatever is written to it will appear in an associated
 * {@link InputStream}! Beware: The associated {@link InputStream} should be practically read from
 * a different thread.
 *
 * @author Syam
 */
public class InvertedOutputStream extends FilterOutputStream {

    private final InputOutputStream inout;

    /**
     * Constructor.
     */
    public InvertedOutputStream() {
        this(new InputOutputStream());
    }

    private InvertedOutputStream(InputOutputStream inout) {
        super(inout.getOutputStream());
        this.inout = inout;
    }

    /**
     * Get the associated input stream.
     *
     * @return Input stream.
     */
    public InputStream getInputStream() {
        return inout.getInputStream();
    }

    @Override
    public void close() throws IOException {
        super.close();
        IO.close(inout.getInputStream(), inout.getOutputStream());
    }
}
