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
import java.util.Objects;

/**
 * A {@link java.io.FilterOutputStream} that translates its stream on-the-fly while writing.
 *
 * @author Syam
 */
public abstract class TranslatedOutputStream extends FilterOutputStream {

    private final InputOutputStream inout;
    /**
     * A {@link BufferedInputStream} used to read input data that needs to be translated
     * before being written to the underlying output stream. This variable is intended to
     * serve as the source of data for the translation process.
     *
     * The {@link #translate()} method of the enclosing class should read data from this stream,
     * perform the translation, and write the results to the output stream.
     */
    protected final BufferedInputStream in;
    /**
     * The underlying output stream to which the translated data is written.
     * This stream serves as the destination for the data after it has been
     * translated by the {@link #translate()} method of the enclosing
     * {@code TranslatedOutputStream} class.
     *
     * The data processed from the {@link BufferedInputStream} variable
     * {@code in} is transformed and ultimately written to this stream.
     *
     * This field is final, ensuring that the reference to the output stream
     * cannot be reassigned after initialization.
     */
    protected final OutputStream out;

    /**
     * Creates a new translated output stream.
     *
     * @param out OutputStream object providing the underlying stream.
     */
    protected TranslatedOutputStream(OutputStream out) {
        this(new InputOutputStream(), out);
    }

    private TranslatedOutputStream(InputOutputStream inout, OutputStream out) {
        super(Objects.requireNonNull(IO.get(inout.getOutputStream())));
        this.inout = inout;
        this.in = IO.get(inout.getInputStream());
        this.out = out;
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
     * Translation should be done here. Original output stream is available in the variable <code>out</code> and
     * the translated output should be written to it by reading the {@link BufferedInputStream} (variable <code>in</code>)
     * and translating it (i.e., read from 'in', translate and write to 'out')
     *
     * @throws Exception Throws any exception so that the ultimate reading program will get it.
     */
    protected abstract void translate() throws Exception;
}
