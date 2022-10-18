/*
 * Copyright (c) 2018-2022 Syam Pillai
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
import java.io.Writer;
import java.util.Arrays;
import java.util.function.Function;

/**
 * CSV creator.
 *
 * @author Syam
 */
public abstract class CSV implements TextContentGenerator {

    private final String[] columns;
    private Function<Object, String> converter;
    private Writer writer;

    /**
     * Constructor.
     *
     * @param columnCount Number of columns.
     */
    public CSV(int columnCount) {
        columns = new String[columnCount];
        Arrays.fill(columns, "");
    }

    /**
     * Override this method for adding content to the CSV. Typically, you call {@link #setValue(int, Object)} (or its
     * variants - {@link #setValues(Object...)} or {@link #setValuesFrom(int, Object...)}) for each column value and
     * call {@link #writeRow()} to write out each row.
     */
    public abstract void generateContent() throws Exception;

    @Override
    public final void generateContent(Writer output) throws Exception {
        this.writer = output;
        generateContent();
    }

    /**
     * Set a value at a specific column in the current row.
     *
     * @param column Column,
     * @param value Value to set.
     */
    public final void setValue(int column, Object value) {
        if(converter == null) {
            converter = StringUtility::toString;
        }
        String v = converter.apply(value);
        if(v == null) {
            v = "";
        }
        if(v.indexOf('\"') >= 0) {
            columns[column] = "\"" + v.replace("\"", "\"\"") + "\"";
        } else if(v.indexOf(',') >= 0 || v.indexOf('\n') >= 0 || v.indexOf('\r') >= 0) {
            columns[column] = "\"" + v + "\"";
        } else {
            columns[column] = v;
        }
    }

    /**
     * Set values starting from a specific column in the current row.
     *
     * @param startingColumn Column,
     * @param values Values to set.
     */
    public final void setValuesFrom(int startingColumn, Object... values) {
        for(Object value: values) {
            setValue(startingColumn++, value);
        }
    }

    /**
     * Set values starting from the first column in the current row.
     *
     * @param values Values to set.
     */
    public final void setValues(Object... values) {
        setValuesFrom(0, values);
    }

    /**
     * Write out the current row.
     */
    public void writeRow() throws IOException {
        if(writer == null) {
            throw new IOException("Not generating");
        }
        for(int i = 0; i < columns.length; i++) {
            if(i > 0) {
                writer.write(',');
            }
            writer.write(columns[i]);
        }
        writer.write("\r\n");
    }

    /**
     * Set the value converter to map the column values to string.
     *
     * @param converter Converter. If no converter is set, {@link StringUtility#toString(Object)} is used as the
     *                  default converter.
     */
    public void setConverter(Function<Object, String> converter) {
        this.converter = converter;
    }

    /**
     * Get the value converter that maps the column values to string.
     *
     * @return Current converter. It could be <code>null</code>.
     */
    public Function<Object, String> getConverter() {
        return converter;
    }

    @Override
    public final String getContentType() {
        return "text/csv";
    }

    @Override
    public final String getFileExtension() {
        return "csv";
    }
}
