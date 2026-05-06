/*
 * Copyright (c) 2018-2026 Syam Pillai
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

import java.io.PrintWriter;
import java.io.Writer;

/**
 * Interface for custom log writers.
 *
 * @author Syam
 */
public interface LogWriter {

    /**
     * Writes a debug message.
     * @param message the message to write
     */
    default void debug(String message) {
    }

    /**
     * Writes an error message.
     * @param message the error message
     */
    default void error(String message) {
    }

    /**
     * Writes an error with stack trace.
     * @param message the error message
     * @param throwable the exception
     */
    default void error(String message, Throwable throwable) {
        error(message);
    }

    /**
     * Flushes any buffered output.
     */
    default void flush() {
    }

    /**
     * Creates a new instance of a LogWriter that provides basic logging behavior
     * using standard output streams.
     *
     * @return a LogWriter instance that writes debug messages to standard output
     *         and error messages to standard error.
     */
    static LogWriter create() {
        return new LogWriter() {
            @Override
            public void debug(String message) {
                System.out.println(message);
            }

            @Override
            public void error(String message) {
                System.err.println(message);
            }

            @Override
            public void error(String message, Throwable throwable) {
                System.err.println(message);
                throwable.printStackTrace(System.err);
            }

            @Override
            public void flush() {
                System.out.flush();
                System.err.flush();
            }
        };
    }

    /**
     * Creates a new instance of a LogWriter that wraps the given {@link PrintWriter}.
     * The created LogWriter outputs log messages to the provided PrintWriter.
     *
     * @param writer the PrintWriter to which log messages will be written
     * @return a LogWriter instance that writes log messages using the given PrintWriter
     */
    static LogWriter create(PrintWriter writer) {
        return new PrintWriterLogWriter(writer);
    }

    /**
     * Creates a new instance of a LogWriter that wraps the provided Writer.
     * The created LogWriter outputs log messages to the given Writer.
     *
     * @param writer the Writer to which log messages will be written
     * @return a LogWriter instance that writes log messages using the provided Writer
     */
    static LogWriter create(Writer writer) {
        return new PrintWriterLogWriter(writer);
    }

    /**
     * Creates a new instance of a LogWriter that performs no operations.
     * This implementation is a no-op and does not produce any output.
     *
     * @return a LogWriter instance that discards all log messages and performs no actions
     */
    static LogWriter createNull() {
        return new LogWriter() {
        };
    }

    /**
     * A LogWriter implementation that writes log messages to a {@link PrintWriter}.
     * This class supports writing debug and error messages and flushing the underlying writer.
     *
     * @author Syam
     */
    public static class PrintWriterLogWriter implements LogWriter {

        private final PrintWriter writer;

        /**
         * Constructs a new PrintWriterLogWriter instance that writes log messages
         * to the provided PrintWriter.
         *
         * @param writer the PrintWriter to which log messages will be written
         */
        public PrintWriterLogWriter(PrintWriter writer) {
            this.writer = writer;
        }

        /**
         * Constructs a new PrintWriterLogWriter instance that writes log messages
         * to the provided Writer. The Writer is wrapped in a PrintWriter for convenience.
         *
         * @param writer the Writer to which log messages will be written
         */
        public PrintWriterLogWriter(Writer writer) {
            this.writer = new PrintWriter(writer);
        }

        @Override
        public void debug(String message) {
            writer.println(message);
        }

        @Override
        public void error(String message) {
            writer.println("[ERROR] " + message);
        }

        @Override
        public void error(String message, Throwable throwable) {
            writer.println("[ERROR] " + message);
            throwable.printStackTrace(writer);
        }

        @Override
        public void flush() {
            writer.flush();
        }
    }
}
