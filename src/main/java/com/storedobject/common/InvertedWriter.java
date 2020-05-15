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
import java.nio.charset.StandardCharsets;

/**
 * Create a {@link Writer} such that whatever is written to it will appear in an associated
 * {@link Reader}! Beware: The associated {@link Reader} should be practically read from
 * a different thread.
 *
 * @author Syam
 */
public class InvertedWriter extends FilterWriter {

    private final Reader reader;

    /**
     * Constructor.
     */
    public InvertedWriter() {
        this(new InvertedOutputStream());
    }

    private InvertedWriter(InvertedOutputStream out) {
        super(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        reader = new InputStreamReader(out.getInputStream(), StandardCharsets.UTF_8);
    }

    /**
     * Get the associated reader.
     *
     * @return Reader.
     */
    public Reader getReader() {
        return reader;
    }
}
