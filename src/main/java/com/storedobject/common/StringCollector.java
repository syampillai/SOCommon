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

public class StringCollector extends Thread {

    private BufferedReader in;
    private Exception exception = null;
    private StringBuilder s;

    public StringCollector(InputStream in) {
        this(IO.getReader(in));
    }

    public StringCollector(Reader in) {
        this.in = IO.get(in);
        start();
    }

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

    public Exception getException() {
        return exception;
    }

    public String getString() {
        while(in != null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        return s.toString();
    }
}