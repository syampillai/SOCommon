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

import java.io.BufferedWriter;
import java.io.FilterReader;
import java.io.Reader;
import java.util.Objects;

/**
 * A {@link java.io.FilterReader} that translates its stream on-the-fly.
 *
 * @author Syam
 */
public abstract class TranslatedReader extends FilterReader {

    private final InputOutputStream inout;
    protected final Reader in;
    protected final BufferedWriter out;

    /**
     * Creates a new translated reader.
     *
     * @param in Reader object providing the underlying stream.
     */
    protected TranslatedReader(Reader in) {
        this(new InputOutputStream(), in);
    }

    private TranslatedReader(InputOutputStream inout, Reader in) {
        super(Objects.requireNonNull(IO.getReader(inout.getInputStream())));
        this.inout = inout;
        this.in = in;
        this.out = IO.getWriter(inout.getOutputStream());
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
     * Translation should be done here. Original reader is available in the variable <code>in</code> and
     * the translated output should be written to the {@link BufferedWriter} (variable <code>out</code>).
     *
     * @throws Exception Throws any exception so that the ultimate reading program will get it.
     */
    protected abstract void translate() throws Exception;
}
