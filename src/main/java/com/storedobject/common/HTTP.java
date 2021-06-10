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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple HTTP utility. Requests can be sent multiple times but before sending subsequent requests the method
 * {@link #done()} should be invoked to clean up the previous request.
 *
 * @author Syam
 */
public class HTTP {

    private final static CookieManager cookieManager = new CookieManager();
    static {
        System.setProperty("jsse.enableSNIExtension", "false"); // Workaround
        CookieHandler.setDefault(cookieManager);
    }
    private final URL url;
    private String contentType = "application/x-www-form-urlencoded";
    private HttpURLConnection connection;
    private boolean allowHTTPErrors = false;
    private boolean json = false;
    private final boolean post;
    private boolean ajaxMode = false;
    private SSLSocketFactory socketFactory;

    /**
     * Create a connection. (By default, GET method will be used).
     * Default content type is "application/x-www-form-urlencoded".
     *
     * @param url URL.
     * @throws MalformedURLException If URL is invalid.
     */
    public HTTP(String url) throws MalformedURLException {
        this(url, false);
    }

    /**
     * Create a connection.
     * Default content type is "application/x-www-form-urlencoded".
     *
     * @param url URL.
     * @param post Whether to use POST method or not.
     * @throws MalformedURLException If URL is invalid.
     */
    public HTTP(String url, boolean post) throws MalformedURLException {
        this(new URL(url), post);
    }

    /**
     * Create a connection. (By default, GET method will be used).
     * Default content type is "application/x-www-form-urlencoded".
     *
     * @param url URL.
     */
    public HTTP(URL url) {
        this(url, false);
    }

    /**
     * Create a connection.
     * Default content type is "application/x-www-form-urlencoded".
     *
     * @param url URL.
     * @param post Whether to use POST method or not.
     */
    public HTTP(URL url, boolean post) {
        this.url = url;
        this.post = post;
    }

    private HttpURLConnection conn() throws IOException {
        if(connection == null) {
            done();
        }
        return connection;
    }

    /**
     * Invoke this method specify that the current request/response is processed and the resources associated with it
     * are released. Further requests may be sent after this. If you don't want to do any further requests,
     * rather than calling this method, it is better to invoke {@link #close()}.
     */
    public void done() {
        freeUp();
        try {
            connection = (HttpURLConnection) url.openConnection();
            if(socketFactory != null) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(socketFactory);
            }
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("charset", "utf-8");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(30 * 1000);
            connection.setReadTimeout(30 * 1000);
            connection.setRequestMethod(post || json ? "POST" : "GET");
            if(ajaxMode) {
                connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            }
        } catch(Exception e) {
            freeUp();
        }
    }

    private void freeUp() {
        if(connection != null) {
            try {
                IO.close(getInputStream());
            } catch(Exception ignored) {
            }
            connection = null;
        }
    }

    /**
     * Set the connection in AJAX mode.
     */
    public void setAJAXMode() {
        ajaxMode = true;
    }

    /**
     * Set the content type to JSON ("application/json").
     */
    public void setJSONMode() {
        json = true;
        contentType = "application/json";
    }

    /**
     * Set basic authentication.
     *
     * @param user User.
     * @param password Password.
     */
    public void setBasicAuthentication(String user, String password) {
        String a = user + ":" + password;
        a = Base64.getEncoder().encodeToString(a.getBytes(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", "Basic " + a);
    }

    /**
     * Allow HTTP errors. If not allowed, an exception will be raised while raading the response.
     *
     * @param allow Whether to allow or not.
     */
    public void setAllowHTTPErrors(boolean allow) {
        this.allowHTTPErrors = allow;
    }

    /**
     * Get the input stream to read the response. (If HTTP errors are not allowed,an exception will be raised
     * if the response is not HTTP OK.
     *
     * @return Stream to read the response.
     * @throws Exception If any error occurs.
     */
    public InputStream getInputStream() throws Exception {
        getConnection();
        if(!allowHTTPErrors && connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new Exception(connection.getResponseMessage());
        }
        return connection.getInputStream();
    }

    /**
     * Get the response as a "reader".
     *
     * @return Get the response via a reader.
     * @throws Exception If any error occurs.
     */
    public BufferedReader getReader() throws Exception {
        return IO.getReader(getInputStream());
    }

    /**
     * Get the output stream to send the request directly.
     *
     * @return The output stream to which request can be written to.
     * @throws IOException If any IO exception occurs.
     */
    public OutputStream getOutputStream() throws IOException {
        return conn().getOutputStream();
    }

    /**
     * Get the output stream to send the request directly.
     *
     * @return The output stream as a "writer" to which request can be written to.
     * @throws IOException If any IO exception occurs.
     */
    public BufferedWriter getWriter() throws Exception {
        return IO.getWriter(getOutputStream());
    }

    /**
     * Save the response directly to an output stream.
     *
     * @param output Output stream to save the response body received.
     * @throws Exception If any exception occurs.
     */
    public void saveTo(OutputStream output) throws Exception {
        IO.copy(getInputStream(), output);
    }

    /**
     * Utility method to URL-encode some value.
     *
     * @param value Value to be encoded.
     * @return Encoded value.
     */
    public static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * Do a "post" request with the given parameters. If the JSON mode is on, the request will be sent as a
     * JSON request, otherwise as parameters.
     *
     * @param parameters Map containing parameters.
     * @throws Exception If any exception occurs.
     */
    public void post(Map<String, Object> parameters) throws Exception {
        if(json) {
            post(new JSON(parameters).toString());
        } else {
            post(parameters.entrySet().stream().map(e -> e.getKey() + "=" + encode(e.getValue().toString())).
                    collect(Collectors.joining("&")));
        }
    }

    /**
     * Do a "post" request with the given parameter.
     *
     * @param request Value to be posted.
     * @throws Exception If any exception occurs.
     */
    public void post(String request) throws Exception {
        byte[] bytes = request.getBytes(StandardCharsets.UTF_8);
        conn().setRequestMethod("POST");
        connection.setRequestProperty("Content-Length", "" + bytes.length);
        try(OutputStream w = getOutputStream()) {
            w.write(bytes);
        }
    }

    /**
     * Read the response as a string.
     *
     * @return Response as a string.
     * @throws Exception If any exception occurs.
     */
    public String read() throws Exception {
        BufferedReader r = getReader();
        StringBuilder s = new StringBuilder();
        String line;
        while((line = r.readLine()) != null) {
            s.append(line).append('\n');
        }
        done();
        return s.toString();
    }

    /**
     * Read the response as XML.
     *
     * @return Response as an XML.
     * @throws Exception If any exception occurs.
     */
    public XML readXML() throws Exception {
        XML xml = new XML(getInputStream());
        done();
        return xml;
    }

    /**
     * Read the response as a JSON.
     *
     * @return Response as a JSON.
     * @throws Exception If any exception occurs.
     */
    public JSON readJSON() throws Exception {
        JSON json = new JSON(getInputStream());
        done();
        return json;
    }

    /**
     * Get the connection to manipulate it directly.
     *
     * @return Connection.
     * @throws Exception If any exception occurs.
     */
    public HttpURLConnection getConnection() throws Exception {
        conn().connect();
        return connection;
    }

    /**
     * Close the connection and associated resources. After this, no more requests may be sent.
     */
    public void close() {
        if(connection != null) {
            connection.disconnect();
            connection = null;
        }
    }

    /**
     * Set the host verifier for this connection.
     *
     * @param verifier Host verifier.
     */
    public void setHostnameVerifier(HostnameVerifier verifier) {
        try {
            ((HttpsURLConnection)conn()).setHostnameVerifier(verifier);
        } catch(IOException ignored) {
        }
    }

    /**
     * Set custom SSL socket factory for this connection.
     *
     * @param socketFactory Factory to be set.
     */
    public void setSSLSocketFactory(SSLSocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }
}