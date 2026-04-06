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
 * A {@link java.io.FilterWriter} that translates its stream on-the-fly while writing.
 *
 * @author Syam
 */
public abstract class TranslatedWriter extends FilterWriter {

    private final InputOutputStream inout;
    /**
     * A {@link BufferedReader} used to read input data for translation. This stream serves as the
     * source for data that needs to be processed and written to the output writer.
     *
     * It reads the raw input from the underlying input-output stream and is used in conjunction
     * with the {@link TranslatedWriter#translate()} method to perform on-the-fly translation of
     * the input content.
     *
     * The lifecycle of this reader is managed internally, and it is expected to be closed automatically
     * once the translation process is complete.
     */
    protected final BufferedReader in;
    /**
     * The writer object to which the translated output is written. This writer acts as
     * the destination for the processed content after being translated by the
     * {@link TranslatedWriter#translate()} method.
     *
     * The lifecycle and closure of this writer are managed internally within the
     * {@link TranslatedWriter} class to ensure resources are properly released after
     * the translation process is completed.
     *
     * The translation process involves reading data from the {@link BufferedReader}
     * (variable {@code in}), processing it, and then writing the resulting output
     * to this writer.
     */
    protected final Writer out;

    /**
     * Creates a new translated writer.
     *
     * @param out Writer object providing the underlying stream.
     */
    protected TranslatedWriter(Writer out) {
        this(new InputOutputStream(), out);
    }

    private TranslatedWriter(InputOutputStream inout, Writer out) {
        super(Objects.requireNonNull(IO.getWriter(inout.getOutputStream())));
        this.inout = inout;
        this.in = IO.getReader(inout.getInputStream());
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
     * Translation should be done here. Original writer is available in the variable <code>out</code> and
     * the translated output should be written to it by reading the {@link BufferedReader} (variable <code>in</code>)
     * and translating it (i.e., read from 'in', translate and write to 'out')
     *
     * @throws Exception Throws any exception so that the ultimate reading program will get it.
     */
    protected abstract void translate() throws Exception;
}
