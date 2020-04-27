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

public class SORuntimeException extends RuntimeException implements EndUserMessage {

    public SORuntimeException() {
        super();
    }

    public SORuntimeException(String message) {
        super(message);
    }

    public SORuntimeException(Throwable cause) {
        this(null, cause);
    }

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

    protected String getCustomMessage() {
        return null;
    }

    public String toString() {
        return getMessage();
    }

    public String getTrace() {
        return getTrace(getCause() == null ? this : getCause(), false);
    }

    public static String getTrace(Throwable t) {
        return getTrace(t, false);
    }

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

    public static String getTrace(Thread thread) {
        return getTrace(thread, "Stack Trace");
    }

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