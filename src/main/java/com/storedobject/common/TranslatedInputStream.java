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

import java.io.BufferedOutputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.Objects;

/**
 * A {@link java.io.FilterInputStream} that translates its stream on-the-fly.
 *
 * @author Syam
 */
public abstract class TranslatedInputStream extends FilterInputStream {

    private final InputOutputStream inout;
    protected final InputStream in;
    protected final BufferedOutputStream out;

    /**
     * Creates a new translated input stream.
     *
     * @param in a InputStream object providing the underlying stream.
     */
    protected TranslatedInputStream(InputStream in) {
        this(new InputOutputStream(), in);
    }

    private TranslatedInputStream(InputOutputStream inout, InputStream in) {
        super(Objects.requireNonNull(inout.getInputStream()));
        this.inout = inout;
        this.in = in;
        this.out = IO.get(inout.getOutputStream());
        Executor.execute(this::xlate);
    }

    private void xlate() {
        try {
            translate();
        } catch (Exception e) {
            inout.setExternalException(e);
        } finally {
            IO.close(in, out);
        }
    }

    /**
     * Translation should be done here. Original input stream is available in the variable <code>in</code> and
     * the translated output should be written to the {@link BufferedOutputStream} (variable <code>out</code>).
     *
     * @throws Exception Throws any exception so that the ultimate reading program will get it.
     */
    protected abstract void translate() throws Exception;
}
