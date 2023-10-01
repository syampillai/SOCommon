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
import java.io.Writer;

/**
 * A {@link java.io.FilterWriter} that applies "string filling" (see {@link StringUtility#fill(String, StringFiller)}) to
 * its lines.
 *
 * @author Syam
 */
public class StringFillerWriter extends TranslatedWriter {

    private final StringFiller filler;

    /**
     * Creates a new filtered reader.
     *
     * @param in A Writer object providing the underlying stream.
     * @param filler {@link StringFiller}.
     */
    public StringFillerWriter(Writer in, StringFiller filler) {
        super(in);
        this.filler = filler;
    }

    @Override
    protected void translate() throws Exception {
        BufferedWriter writer = IO.get(out);
        String line;
        while ((line = in.readLine()) != null) {
            writer.write(StringUtility.fill(line, filler));
            writer.newLine();
        }
    }
}
