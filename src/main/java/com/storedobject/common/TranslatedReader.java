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
    /**
     * The underlying {@link Reader} providing the original input stream
     * before translation. This reader is used as the source for processing
     * in the translation logic defined in the {@link #translate()} method.
     */
    protected final Reader in;
    /**
     * A {@link BufferedWriter} used to write the translated output of the
     * input stream processed by this {@link TranslatedReader}. This writer
     * acts as the destination for the translated data after applying the
     * custom translation logic defined in the {@link #translate()} method.
     * It is initialized to output to an underlying stream managed by this class.
     */
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
