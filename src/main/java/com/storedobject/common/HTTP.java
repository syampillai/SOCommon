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
 * <p>Note: This is written before {@link java.net.http.HttpClient} was available in Java. So, it is better
 * to use that class and its related cousins instead of this class.</p>
 *
 * @author Syam
 */
@SuppressWarnings("UnusedReturnValue")
public class HTTP {

    private final static CookieManager cookieManager = new CookieManager();
    static {
        CookieHandler.setDefault(cookieManager);
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "host");
    }
    private static final String JSON_TYPE = "application/json";
    private final URL url;
    private String contentType = "application/x-www-form-urlencoded";
    private HttpURLConnection connection;
    private boolean allowHTTPErrors = false;
    private String method;
    private boolean ajaxMode = false;
    private boolean sni = true;
    private SSLSocketFactory socketFactory;
    private int readTimeout = 30000, connectTimeout = 30000;

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
        this.method = post ? "POST" : "GET";
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
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setRequestMethod(method);
            if(JSON_TYPE.equals(contentType)) {
                connection.setRequestProperty("Accept", contentType);
            }
            if(ajaxMode) {
                connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            }
        } catch(Exception e) {
            freeUp();
        }
    }

    /**
     * Set request method ("GET", "POST", etc.)
     *
     * @param method The method to set.
     */
    public void setMethod(String method) {
        if(this.method.equals(method)) {
            return;
        }
        this.method = method;
    }

    /**
     * Get the request method.
     *
     * @return Method.
     */
    public String getMethod() {
        return method;
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
        contentType = "application/json";
    }

    /**
     * Set content type.
     *
     * @param contentType Content type to set.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
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
        setRequestProperty("Authorization", "Basic " + a);
    }

    /**
     * Set basic authentication.
     *
     * @param key Bearer key.
     */
    public void setBearerAuthentication(String key) {
        setRequestProperty("Authorization", "Bearer " + key);
    }

    /**
     * Set a request property directly.
     *
     * @param propertyName Name of the property.
     * @param propertyValue Value of the property.
     */
    public void setRequestProperty(String propertyName, String propertyValue) {
        try {
            conn().setRequestProperty(propertyName, propertyValue);
        } catch(IOException ignored) {
        }
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
     * Get the input stream to read the response. If HTTP errors are not allowed,an exception will be raised
     * if the response is not HTTP OK. If HTTP errors are allowed, the "error stream" is returned.
     *
     * @return Stream to read the response.
     * @throws Exception If any error occurs.
     */
    public InputStream getInputStream() throws Exception {
        getConnection();
        if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            if(!allowHTTPErrors) {
                throw new Exception(connection.getResponseCode() + ": " + connection.getResponseMessage());
            }
            return connection.getErrorStream();
        }
        return connection.getInputStream();
    }

    /**
     * Get the response as a "reader".
     *
     * @return Get the response via a reader.
     * @throws Exception If any error occurs. (See {@link #getInputStream()}).
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
        if(JSON_TYPE.equals(contentType)) {
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
        freeUp();
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
        freeUp();
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
        freeUp();
        return json;
    }

    /**
     * Get the connection to manipulate it directly.
     *
     * @return Connection.
     * @throws Exception If any exception occurs.
     */
    public HttpURLConnection getConnection() throws Exception {
        String sniProperty = "jsse.enableSNIExtension";
        synchronized(cookieManager) {
            try {
                if(!sni) {
                    System.setProperty(sniProperty, "false");
                }
                conn().connect();
            } finally {
                System.clearProperty(sniProperty);
            }
        }
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

    /**
     * Disable SNI extension. By default, it's enabled.
     * <p>Note: This will disable SNI extension system-wide while connecting and it will be enabled again.</p>
     */
    public void disableSNIExtension() {
        this.sni = false;
    }

    /**
     * Set the read timeout for the connection. If the timeout is less than or equal to 0, the default timeout
     * of 30000 milliseconds will be used.
     *
     * @param readTimeout The read timeout in milliseconds.
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout <= 0 ? 30000 : readTimeout;
        if(connection != null) {
            connection.setReadTimeout(this.readTimeout);
        }
    }

    /**
     * Set the connection timeout for the HTTP request. If the timeout is less than or equal to 0,
     * the default timeout of 30000 milliseconds will be used.
     *
     * @param connectTimeout the connection timeout in milliseconds
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout <= 0 ? 30000 : connectTimeout;
        if(connection != null) {
            connection.setConnectTimeout(this.connectTimeout);
        }
    }
}