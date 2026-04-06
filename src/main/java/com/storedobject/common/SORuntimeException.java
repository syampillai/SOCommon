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

/**
 * A custom runtime exception class that provides enhanced error details and user-friendly messages.
 * It implements the {@code EndUserMessage} interface to support messages targeted at end users
 * for better readability and clarity.
 *
 * <ul>
 *   <li>Supports chaining of exceptions and integrates with the {@code SOException} class for
 *       consistent end-user messaging.
 *   <li>Provides utility methods for extracting detailed stack traces and root cause analysis.
 *   <li>Overridden methods aim to produce structurally formatted messages derived from the
 *       exception hierarchy and root causes.
 * </ul>
 *
 * @author Syam
 */
public class SORuntimeException extends RuntimeException implements EndUserMessage {

    /**
     * Default constructor for the SORuntimeException class.
     * This constructor initializes a new instance of the exception
     * without any custom message or cause.
     */
    public SORuntimeException() {
        super();
    }

    /**
     * Constructs a new SORuntimeException instance with the specified detail message.
     *
     * @param message the detail message that explains the reason for the exception
     */
    public SORuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs a new SORuntimeException instance with the specified cause.
     *
     * @param cause the underlying cause of the exception, which can be null
     */
    public SORuntimeException(Throwable cause) {
        this(null, cause);
    }

    /**
     * Constructs a new SORuntimeException instance with the specified detail message and cause.
     *
     * @param message the detail message that provides additional context or explanation for the exception
     * @param cause   the underlying cause of the exception, typically another Throwable, which can be null
     */
    public SORuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getEndUserMessage() {
        String m = super.getMessage();
        Throwable cause = getCause();
        if(m == null || m.isEmpty()) {
            return cause instanceof SOException ? ((SOException) cause).getEndUserMessage() : "Error";
        }
        if(cause instanceof SOException) {
            m += " (" + ((SOException) cause).getEndUserMessage() + ")";
        }
        return m;
    }

    @Override
    public String getMessage() {
        String s;
        Throwable t = this;
        while(t.getCause() != null) {
            t = t.getCause();
        }
        String m = super.getMessage();
        if(t == this) {
            s = m;
            m = null;
        } else {
            s = t.getMessage();
        }
        String c = t.getClass().getName();
        if(s == null) {
            s = getCustomMessage();
        }
        if(m != null) {
            if(s == null) {
                s = m;
            } else {
                s = m + "\n" + s;
            }
        }
        c = c.substring(c.lastIndexOf('.')+1).replace('_', ' ');
        if(c.indexOf('$') > 0) {
            c = c.substring(c.indexOf('$') + 1);
        }
        if(c.equals("SORuntimeException") || c.equals("SOError")) {
            return s == null ? "Runtime Error" : s;
        }
        return s == null ? c : (c + " (" + s + ")");
    }

    /**
     * Retrieves a custom message associated with this exception.
     * This method can be overridden by subclasses to provide
     * specific custom messages based on the exception's context.
     *
     * @return the custom message as a string, or null if no custom message is provided
     */
    protected String getCustomMessage() {
        return null;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    /**
     * Retrieves the full or partial stack trace of this exception or its root cause as a string.
     * This method determines the trace based on whether this exception has a cause or not,
     * and delegates to the {@link #getTrace(Throwable, boolean)} method.
     *
     * @return a string representation of the stack trace, starting from either the root cause or this exception
     */
    public String getTrace() {
        return getTrace(getCause() == null ? this : getCause(), false);
    }

    /**
     * Retrieves a string representation of the stack trace for the given {@link Throwable}.
     * The stack trace may represent either the root cause or the specified exception
     * depending on the implementation.
     *
     * @param t the {@link Throwable} whose stack trace needs to be retrieved; cannot be null
     * @return a string representation of the stack trace for the specified {@link Throwable}
     */
    public static String getTrace(Throwable t) {
        return getTrace(t, false);
    }

    /**
     * Retrieves a string representation of the stack trace for a given {@link Throwable}.
     * The representation can include either the full stack trace with all causes or only the root cause,
     * based on the specified flag.
     *
     * @param t    the {@link Throwable} whose stack trace needs to be retrieved; must not be null
     * @param full a boolean flag indicating whether to include the full stack trace (true)
     *             or only the root cause (false)
     * @return a string representation of the stack trace, either for the full exception chain
     *         or just the root cause, depending on the {@code full} parameter
     */
    public static String getTrace(Throwable t, boolean full) {
        if(!full) {
            while(t.getCause() != null) {
                t = t.getCause();
            }
        }
        StringBuilder sb = new StringBuilder();
        while(t != null) {
            sb.append(t.getClass().getName()).append(": ");
            if(t.getMessage() != null) {
                sb.append(t.getMessage());
            }
            sb.append('\n');
            StackTraceElement[] ste = t.getStackTrace();
            for(StackTraceElement s: ste) {
                sb.append('\t').append(s).append('\n');
            }
            t = t.getCause();
            if(t != null) {
                sb.append("Caused by:\n");
            }
        }
        return sb.toString();
    }

    /**
     * Retrieves the stack trace of the specified thread as a string.
     * The stack trace is prefixed with a default title "Stack Trace".
     *
     * @param thread the {@link Thread} whose stack trace needs to be retrieved; must not be null
     * @return a string representation of the stack trace for the specified thread,
     *         prefixed with the default title
     */
    public static String getTrace(Thread thread) {
        return getTrace(thread, "Stack Trace");
    }

    /**
     * Retrieves the stack trace of the specified thread as a string.
     * The stack trace is prefixed with the given title.
     *
     * @param thread the {@link Thread} whose stack trace needs to be retrieved; must not be null
     * @param title  the title to display as the prefix of the stack trace; must not be null or empty
     * @return a string representation of the stack trace for the specified thread,
     *         prefixed with the given title
     */
    public static String getTrace(Thread thread, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append('\n');
        StackTraceElement[] ste = thread.getStackTrace();
        StackTraceElement s;
        for(int i = 1; i < ste.length; i++) {
            s = ste[i];
            sb.append('\t').append(s).append('\n');
        }
        return sb.toString();
    }
}