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

public class SOException extends Exception {

    public SOException() {
        super();
    }

    public SOException(String message) {
        super(message);
    }

    public SOException(String message, Throwable cause) {
        super(message, cause);
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

    protected String getCustomMessage() {
        return null;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    public String getRootMessage() {
        return super.getMessage();
    }
}