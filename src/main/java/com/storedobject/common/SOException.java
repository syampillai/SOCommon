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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Represents a custom exception that integrates with the {@link EndUserMessage} interface
 * to provide user-friendly error messages, while also supporting detailed technical
 * error descriptions for debugging purposes.
 *
 * <p>This exception extends {@code Exception} and implements {@code EndUserMessage},
 * enabling it to generate both high-level, end-user-oriented messages and low-level,
 * developer-oriented diagnostic messages.</p>
 *
 * The class provides the following key functionalities:
 * <pre>
 * - Customizable messages retrievable via the {@code getMessage()} and {@code getEndUserMessage()} methods.
 * - Automatically includes the messages and stack traces of any nested causes in the output.
 * - Facilitates enhanced string formatting for exception details.
 * </pre>
 * The {@code getEndUserMessage()} method aims to return concise, user-friendly messages,
 * while the {@code getMessage()} method provides a comprehensive description, including
 * nested causes for diagnostic purposes.
 *
 * @author Syam
 */
public class SOException extends Exception implements EndUserMessage {

    /**
     * Default constructor for the SOException class.
     * Creates a new instance of SOException without any specific message or cause.
     */
    public SOException() {
        super();
    }

    /**
     * Constructs a new SOException with the specified detail message.
     *
     * @param message The detail message that describes the reason for the exception.
     */
    public SOException(String message) {
        super(message);
    }

    /**
     * Constructs a new SOException with the specified detail message and cause.
     *
     * @param message The detail message that explains the reason for the exception.
     * @param cause   The underlying cause of the exception, which can be used to provide additional context.
     */
    public SOException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getEndUserMessage() {
        Throwable cause = getCause();
        if(cause instanceof EndUserMessage) {
            return ((EndUserMessage) cause).getEndUserMessage();
        }
        String m = super.getMessage();
        return m == null || m.isEmpty() ? "Error" : m;
    }

    @Override
    public String getMessage() {
        String m = getMessageInt();
        Throwable cause = getCause();
        if(cause != null) {
            m += ", Caused by: " + toString(cause);
        }
        return m;
    }

    private static String toString(Throwable t) {
        if(t instanceof SOException) {
            return t.getMessage();
        }
        String m = t.getMessage();
        if(m == null) {
            m = t.getClass().getName();
        } else {
            m = t.getClass().getName() + "(" + m + ")";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        m += ", Stack: " + sw;
        Throwable cause = t.getCause();
        if(cause != null) {
            m += ", Caused by: " + toString(cause);
        }
        return m;
    }

    private String getMessageInt() {
        String c = getClass().getName(), s = super.getMessage();
        if(s == null) {
            s = getCustomMessage();
        }
        c = c.substring(c.lastIndexOf('.') + 1).replace('_', ' ');
        if(c.indexOf('$') > 0) {
            c = c.substring(c.indexOf('$') + 1);
        }
        if(c.equals("SOException")) {
            return s == null ? "Error" : s;
        }
        return s == null ? c : (c + " (" + s + ")");
    }

    /**
     * Retrieves a custom message associated with the exception.
     *
     * @return A string representing the custom message, or null if no custom message is set.
     */
    protected String getCustomMessage() {
        return null;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    /**
     * Retrieves the root message associated with the exception.
     * The root message provides the base-level explanation for why the exception was thrown.
     *
     * @return A string representing the root message, or null if no base message is available.
     */
    public String getRootMessage() {
        return super.getMessage();
    }
}