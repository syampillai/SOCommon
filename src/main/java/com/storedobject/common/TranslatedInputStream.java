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
    /**
     * The input stream containing the original data to be translated.
     * This stream serves as the source for translation operations
     * performed by the {@code translate()} method.
     *
     * <ul>
     *   <li>It is provided as a parameter during the construction of the containing class.</li>
     *   <li>Ensures the availability of unmodified input data before translation.</li>
     *   <li>Typically wrapped by a {@link FilterInputStream} for further processing.</li>
     * </ul>
     *
     * This field is declared as {@code protected} to allow access in
     * subclasses and {@code final} to ensure its reference cannot be changed.
     */
    protected final InputStream in;
    /**
     * The buffered output stream used to hold the translated output data.
     * This stream serves as the target for translation operations performed
     * by the {@code translate()} method.
     *
     * <ul>
     *   <li>It is a {@code BufferedOutputStream} to optimize write operations.</li>
     *   <li>The data written to this stream reflects the translated content
     *       based on the original input stream {@code in}.</li>
     *   <li>Declared as {@code protected} to allow subclasses to access this
     *       stream for customized translation behavior.</li>
     *   <li>Declared as {@code final} to ensure its reference cannot be modified
     *       after initialization.</li>
     * </ul>
     *
     * This field is initialized during the construction of the containing class
     * and is automatically managed, being closed along with the associated input
     * stream.
     */
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
