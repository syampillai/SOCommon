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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * The StringCollector class reads input data asynchronously from a given Reader or InputStream
 * and collects it into a single concatenated string. It is designed to be executed on a virtual thread.
 * This class also allows retrieval of any exception encountered during the reading process.
 *
 * StringCollector starts a virtual thread that reads and processes lines from the input source.
 * The collected string can be accessed after the reading process is completed, and users can
 * wait for the process to complete using the provided methods. Typically, you invoke the
 * {@link #toString()} method to the string being read.
 *
 * @author Syam
 */
public class StringCollector implements Runnable {

    private BufferedReader in;
    private Exception exception = null;
    private StringBuilder s;
    private final Thread collectorThread;

    /**
     * Creates a StringCollector that reads input data from the given InputStream asynchronously
     * and collects the data into a single concatenated string.
     *
     * @param in the InputStream to read input data from
     */
    public StringCollector(InputStream in) {
        this(IO.getReader(in));
    }

    /**
     * Constructs a new StringCollector that reads input data from the given Reader object.
     * The input is read asynchronously using a virtual thread and stored in a concatenated string.
     *
     * @param in the Reader from which the input data will be read
     */
    public StringCollector(Reader in) {
        this.in = IO.get(in);
        collectorThread = Thread.ofVirtual().start(this);
    }

    /**
     * Executes the main logic for collecting input data from the provided BufferedReader.
     * This method reads lines of text iteratively from the input source and concatenates them
     * into a single {@code StringBuilder} instance, separating lines with a newline character.
     * If an IOException occurs during the reading process, the exception is captured and
     * stored for later retrieval. The input stream is closed in the cleanup step to release
     * associated system resources.
     *
     * The method runs on the virtual thread started during the instantiation of the StringCollector,
     * facilitating asynchronous data collection.
     *
     * Any exception encountered can be accessed via the {@link #getException()} method, and the
     * concatenated string can be fetched through {@link #getString()}.
     */
    @Override
    public void run() {
        s = new StringBuilder();
        String line;
        try {
            while((line = in.readLine()) != null) {
                if(s.length() != 0) {
                    s.append('\n');
                }
                s.append(line);
            }
        } catch(IOException e) {
            exception = e;
        } finally {
            try {
                in.close();
            } catch(Exception ignored) {
            }
            in = null;
        }
    }

    /**
     * Retrieves the exception that occurred during the reading process, if any.
     * If no exception occurred, this method returns null.
     *
     * @return the exception encountered during the reading process, or null if no exception occurred.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Returns the concatenated string that has been collected from the input source.
     * This method waits for the internal thread to finish processing before returning
     * the result, ensuring that all input data has been read and appended to the string.
     *
     * @return The complete string collected from the input source.
     */
    public String getString() {
        while(in != null) {
            try {
                collectorThread.join();
            } catch (InterruptedException ignored) {
            }
        }
        return s.toString();
    }

    /**
     * Waits for the virtual thread, responsible for collecting input data, to complete execution.
     * This method blocks until the associated collector thread has finished processing or is interrupted.
     *
     * If the thread is interrupted while waiting, the interruption is silently ignored and the thread
     * continues to wait.
     */
    public void join() {
        try {
            collectorThread.join();
        } catch (InterruptedException ignored) {
        }
    }
}