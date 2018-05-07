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
            } catch(Exception e) {
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
            } catch (InterruptedException e) {
            }
        }
        return s.toString();
    }
}