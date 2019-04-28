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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Simple HTTP utility
 */
public class HTTP {

    private HttpURLConnection connection;
    private boolean allowHTTPErrors = false;

    public HTTP(String url) throws Exception {
        this(url, false);
    }

    public HTTP(String url, boolean post) throws Exception {
        this(new URL(url), post);
    }

    public HTTP(URL url) throws Exception {
        this(url, false);
    }

    public HTTP(URL url, boolean post) throws Exception {
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Connection", "close");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("charset", "utf-8");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setConnectTimeout(30*1000);
        connection.setReadTimeout(30*1000);
        connection.setRequestMethod(post ? "POST" : "GET");
    }

    public void setBasicAuthentication(String user, String password) {
        String a = user + ":" + password;
        a = Base64.getEncoder().encodeToString(a.getBytes(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", "Basic " + a);
    }

    public void setAllowHTTPErrors(boolean allow) {
        this.allowHTTPErrors = allow;
    }

    public InputStream getInputStream() throws Exception {
        getConnection();
        if(!allowHTTPErrors && connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new Exception(connection.getResponseMessage());
        }
        return connection.getInputStream();
    }

    public BufferedReader getReader() throws Exception {
        return IO.getReader(getInputStream());
    }

    public OutputStream getOutputStream() throws IOException {
        return connection.getOutputStream();
    }

    public BufferedWriter getWriter() throws Exception {
        return IO.getWriter(getOutputStream());
    }

    public void saveTo(OutputStream output) throws Exception {
        IO.copy(getInputStream(), output);
    }

    public static String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        return value;
    }

    public void post(Map<String, String> parameters) throws Exception {
        post(parameters.entrySet().stream().map(e -> e.getKey() + "=" + encode(e.getValue())).collect(Collectors.joining("&")));
    }

    public void post(String post) throws Exception {
        byte[] bytes = post.getBytes(StandardCharsets.UTF_8);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Length", "" + bytes.length);
        try(OutputStream w = getOutputStream()) {
            w.write(bytes);
        }
    }

    public String read() throws Exception {
        BufferedReader r = getReader();
        StringBuilder s = new StringBuilder();
        String line;
        while((line = r.readLine()) != null) {
            s.append(line).append('\n');
        }
        return s.toString();
    }

    public XML readXML() throws Exception {
        return new XML(getInputStream());
    }

    public JSON readJSON() throws Exception {
        return new JSON(getInputStream());
    }

    @SuppressWarnings("UnusedReturnValue")
    public HttpURLConnection getConnection() throws Exception {
        connection.connect();
        return connection;
    }

    public void setHostnameVerifier(HostnameVerifier verifier) {
        ((HttpsURLConnection)connection).setHostnameVerifier(verifier);
    }

    public void setSSLSocketFactory(SSLSocketFactory socketFactory) {
        ((HttpsURLConnection)connection).setSSLSocketFactory(socketFactory);
    }
}