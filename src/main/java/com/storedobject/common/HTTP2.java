/*
 * Copyright (c) 2018-2025 Syam Pillai
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

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * The HTTP2 class facilitates HTTP/2 client-server communication
 * with helper methods and a builder pattern for request customization.
 * It leverages Java's HttpClient for synchronous and asynchronous
 * HTTP calls and provides utilities for handling and parsing responses.
 * <p>Note: This utility class can transparently handle chunked and compressed (zipped or deflated) content as well.</p>
 * <p>Features:</p>
 * <ul>
 *   <li>Connection pooling optimization with custom client caching</li>
 *   <li>Automatic retry mechanism for transient failures</li>
 *   <li>Configurable debug logging with custom log destinations</li>
 *   <li>Proper handling of chunked transfer encoding</li>
 *   <li>Automatic decompression of gzip/deflate responses</li>
 *   <li>Virtual threads executor for async operations (Java 21+)</li>
 * </ul>
 * <pre>
 *     Typical usage:
 *     String s = HTTP2.build("https://www.google.com").string();
 *     System.out.println("Google's default page's content is: " + s);
 *
 *     JSON json = HTTP2.build("https://www.example.com?api=xxx").json(); // Get the JSON content
 *
 *     // With retry and custom logger
 *     HTTP2.setLogWriter(new PrintWriter(System.out));
 *     String data = HTTP2.builder("https://api.example.com/data")
 *         .retry(3, Duration.ofMillis(500))
 *         .debug()
 *         .string();
 * </pre>
 *
 * @author Syam
 */
public class HTTP2 {

    private static final HttpClient DEFAULT_HTTP_CLIENT = newHHttpClientBuilder().build();
    private static final Map<HttpClient.Builder, HttpClient> CUSTOM_CLIENTS = new WeakHashMap<>();
    private static final Map<String, String> DEFAULT_HEADERS = Map.of(
            "User-Agent", "SOClient/1.0",
            "Accept-Charset", "UTF-8",
            "Accept-Encoding", "gzip, deflate"
    );

    // Global log writer (can be overridden per request)
    private static volatile LogWriter globalLogWriter = LogWriter.create();
    private static volatile boolean globalDebugEnabled = false;

    private HTTP2() {
    }

    /**
     * Sets the global log writer for all HTTP2 debug output.
     * @param logWriter the log writer to use
     */
    public static void setLogWriter(LogWriter logWriter) {
        globalLogWriter = logWriter != null ? logWriter : LogWriter.createNull();
    }

    /**
     * Enables or disables global debug logging.
     * @param enabled true to enable debug logging globally
     */
    public static void setGlobalDebugEnabled(boolean enabled) {
        globalDebugEnabled = enabled;
    }

    /**
     * Checks if global debug is enabled.
     *
     * @return true if global debug is enabled, false otherwise
     */
    public static boolean isGlobalDebugEnabled() {
        return globalDebugEnabled;
    }

    private static HttpClient.Builder newHHttpClientBuilder() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .connectTimeout(Duration.ofSeconds(10));
    }

    private static HttpClient getClient(HttpClient.Builder builder) {
        if (builder == null) return DEFAULT_HTTP_CLIENT;
        return CUSTOM_CLIENTS.computeIfAbsent(builder, HttpClient.Builder::build);
    }

    private static InputStream stream(Builder b) throws Exception {
        b.error = null;
        long startTime = b.enableDebugLogging ? System.currentTimeMillis() : 0;

        b.response = b.httpClient().send(build(b.url, b.body, b.headers, b.requestCustomizer),
                HttpResponse.BodyHandlers.ofInputStream());

        if (b.enableDebugLogging && b.response != null) {
            long duration = System.currentTimeMillis() - startTime;
            b.logResponse(b.response, duration);
        }

        return new DecompressingInputStream(b.response);
    }

    private static <T> CompletableFuture<T> async(Builder b, Function<InputStream, T> transformer) {
        b.error = null;
        long startTime = b.enableDebugLogging ? System.currentTimeMillis() : 0;

        return async(b).thenApply(r -> {
            b.response = r;
            if (b.enableDebugLogging && b.response != null) {
                long duration = System.currentTimeMillis() - startTime;
                b.logResponse(b.response, duration);
            }
            try {
                return transformer.apply(new DecompressingInputStream(r));
            } catch (Exception e) {
                b.error(e);
                return null;
            }
        });
    }

    private static CompletableFuture<HttpResponse<InputStream>> async(Builder b) {
        return b.httpClient().sendAsync(build(b.url, b.body, b.headers, b.requestCustomizer),
                HttpResponse.BodyHandlers.ofInputStream());
    }

    private static HttpRequest build(String url, String body, Map<String, String> headers,
                                     Consumer<HttpRequest.Builder> requestCustomizer) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10));
        if(requestCustomizer != null) {
            requestCustomizer.accept(requestBuilder);
        }
        if (body != null) {
            requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
        } else {
            requestBuilder.GET();
        }
        if (headers == null) {
            DEFAULT_HEADERS.forEach(requestBuilder::header);
        } else {
            headers.forEach(requestBuilder::header);
            DEFAULT_HEADERS.entrySet().stream().filter(e -> !headers.containsKey(e.getKey()))
                    .forEach(e -> requestBuilder.header(e.getKey(), e.getValue()));
        }
        return requestBuilder.build();
    }

    /**
     * Checks if a service is available by sending a HEAD request.
     *
     * @param url the URL to check
     * @param timeout the timeout for the check
     * @return true if service responds with 200 OK within the timeout
     */
    public static boolean isServiceAvailable(String url, Duration timeout) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(timeout)
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<Void> response = DEFAULT_HTTP_CLIENT.send(request,
                    HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a service is available with default timeout of 5 seconds.
     *
     * @param url the URL to check
     * @return true if service responds with 200 OK within 5 seconds
     */
    public static boolean isServiceAvailable(String url) {
        return isServiceAvailable(url, Duration.ofSeconds(5));
    }

    /**
     * Creates a new instance of the {@code Builder} class, which provides methods
     * to configure and build an HTTP request.
     *
     * @return a new {@code Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new {@code Builder} instance initialized with the specified URL.
     *
     * @param url the URL to be set for the builder
     * @return a new {@code Builder} instance with the URL set
     */
    public static Builder builder(String url) {
        return builder().url(url);
    }

    /**
     * This class provides a builder pattern for constructing HTTP requests.
     * It allows customization of various aspects such as URL, headers,
     * body, timeouts, and other configurations related to the HTTP client and request.
     * The builder also supports synchronous and asynchronous operations for request execution.
     *
     * @author Syam
     */
    public static class Builder {

        private String url;
        private Map<String, String> headers;
        private ChainedCustomizer requestCustomizer;
        private String body;
        private Exception error;
        private Consumer<Exception> exceptionHandler;
        private HttpResponse<InputStream> response;
        private HttpClient.Builder httpClientBuilder;
        private int maxRetries = 0;
        private Duration retryDelay = Duration.ofMillis(100);
        private boolean enableDebugLogging = false;
        private LogWriter logWriter = null;

        private Builder() {
        }

        private void error(Exception e) {
            if(error == null) {
                error = e;
            }
            if(exceptionHandler != null) {
                exceptionHandler.accept(e);
            }
        }

        private LogWriter getLogWriter() {
            return logWriter != null ? logWriter : globalLogWriter;
        }

        private void debug(String message) {
            if (enableDebugLogging) {
                getLogWriter().debug(message);
            }
        }

        private void error(String message, Throwable throwable) {
            if (enableDebugLogging) {
                getLogWriter().error(message, throwable);
            }
        }

        private void logRequest(HttpRequest request) {
            if (!enableDebugLogging) return;
            StringBuilder sb = new StringBuilder();
            sb.append('[').append(DateTimeFormatter.ISO_INSTANT.format(Instant.now())).append("] ");
            sb.append(request.method()).append(' ').append(request.uri());
            sb.append(" - Headers: ");
            request.headers().map().forEach((k, v) ->
                    sb.append(k).append('=').append(v).append(';'));
            if (body != null && body.length() < 1000) {
                sb.append(" Body: ").append(body);
            } else if (body != null) {
                sb.append(" Body: [").append(body.length()).append(" bytes]");
            }
            debug(sb.toString());
        }

        private void logResponse(HttpResponse<?> response, long durationMs) {
            if (!enableDebugLogging) return;
            StringBuilder sb = new StringBuilder();
            sb.append('[').append(DateTimeFormatter.ISO_INSTANT.format(Instant.now())).append("] ");
            sb.append("Response: ").append(response.statusCode());
            sb.append(" (").append(durationMs).append(" ms)");
            response.headers().map().forEach((k, v) ->
                    sb.append(" | ").append(k).append('=').append(v));
            debug(sb.toString());
        }

        /**
         * Sets a custom log writer for this request (overrides global).
         *
         * @param logWriter the log writer to use
         * @return this builder instance
         */
        public Builder logWriter(LogWriter logWriter) {
            this.logWriter = logWriter;
            return this;
        }

        /**
         * Sets the maximum number of retry attempts for failed requests.
         *
         * @param maxRetries maximum retry attempts (0 = no retry)
         * @return this builder instance
         */
        public Builder retry(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        /**
         * Sets the retry policy with custom delay between attempts.
         *
         * @param maxRetries maximum retry attempts
         * @param retryDelay delay between retry attempts
         * @return this builder instance
         */
        public Builder retry(int maxRetries, Duration retryDelay) {
            this.maxRetries = maxRetries;
            this.retryDelay = retryDelay;
            return this;
        }

        /**
         * Enables debug logging for this request.
         * Uses global log writer if no custom one is set.
         *
         * @return this builder instance
         */
        public Builder debug() {
            this.enableDebugLogging = true;
            return this;
        }

        /**
         * Enables debug logging with a specific log writer for this request.
         *
         * @param logWriter the log writer to use for this request
         * @return this builder instance
         */
        public Builder debug(LogWriter logWriter) {
            this.enableDebugLogging = true;
            this.logWriter = logWriter;
            return this;
        }

        /**
         * Enables debug logging with a PrintWriter for this request.
         *
         * @param writer the writer to log to
         * @return this builder instance
         */
        public Builder debug(Writer writer) {
            return debug(LogWriter.create(writer));
        }

        /**
         * Sets the URL for the HTTP request.
         *
         * @param url the URL to be set for the request
         * @return the updated builder instance
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * Configures the HTTP client with a custom authenticator.
         *
         * @param authenticator the {@link Authenticator} instance to handle authentication for requests
         * @return the updated {@code Builder} instance for method chaining
         */
        public Builder authenticator(Authenticator authenticator) {
            getClientBuilder().authenticator(authenticator);
            return this;
        }

        /**
         * Sets the exception handler for handling exceptions. When an exception occurs, this handler will be invoked.
         *
         * @param exceptionHandler a consumer that defines how exceptions should be handled
         * @return the current Builder instance with the exception handler configured
         */
        public Builder exceptionHandler(Consumer<Exception> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        /**
         * Adds a customizer for the HTTP request being built. This method allows chaining multiple
         * customizers by combining them into a composite customizer if needed.
         *
         * @param requestCustomizer a {@link Consumer} implementation that applies customizations
         *                           to an {@link HttpRequest.Builder} instance.
         * @return the {@code Builder} instance, enabling method chaining.
         */
        public Builder requestCustomizer(Consumer<HttpRequest.Builder> requestCustomizer) {
            if(this.requestCustomizer == null) {
                this.requestCustomizer = new ChainedCustomizer();
            }
            this.requestCustomizer.add(requestCustomizer);
            return this;
        }

        /**
         * Sets the body content for the request and returns the builder instance for chaining.
         *
         * @param body the body content to be included in the request
         * @return the Builder instance for method chaining
         */
        public Builder body(String body) {
            this.body = body;
            return this;
        }

        /**
         * Sets the body content from a JSON object.
         *
         * @param json the JSON object to use as body
         * @return this builder instance
         */
        public Builder body(JSON json) {
            this.body = json.toString();
            contentTypeJSON();
            return this;
        }

        /**
         * Sets the body content from a Java object (converted to JSON).
         *
         * @param pojo the POJO to convert to JSON
         * @return this builder instance
         */
        public Builder body(Object pojo) {
            return body(new JSON(pojo));
        }

        /**
         * Sets the timeout duration for the HTTP request.
         *
         * @param timeout the timeout duration to be applied to the HTTP request
         * @return the Builder instance for method chaining
         */
        public Builder timeout(Duration timeout) {
            requestCustomizer(b -> b.timeout(timeout));
            return this;
        }

        /**
         * Adds a header to the request being built. If the header map is null, it initializes
         * the map and adds the key-value pair. Otherwise, it simply adds or updates the
         * key-value pair in the existing headers map.
         *
         * @param key the header name
         * @param value the header value
         * @return the current Builder instance for method chaining
         */
        public Builder header(String key, String value) {
            if(headers == null) {
                headers = new HashMap<>();
                headers.put(key, value);
            } else {
                headers.put(key, value);
            }
            return this;
        }

        /**
         * Sets a collection of headers for the request by iterating over the provided map
         * and adding each key-value pair as a header.
         *
         * @param headers a map containing the headers to be added, where the keys are the header
         *                names and the values are the corresponding header values
         * @return the current Builder instance for method chaining
         */
        public Builder headers(Map<String, String> headers) {
            headers.forEach(this::header);
            return this;
        }

        /**
         * Sets the Authorization header with a Bearer token.
         *
         * @param token the Bearer token to be used in the Authorization header
         * @return the Builder instance with the updated Authorization header
         */
        public Builder bearerToken(String token) {
            return header("Authorization", "Bearer " + token);
        }

        /**
         * Sets the "Content-Type" header for the request.
         *
         * @param contentType the value of the "Content-Type" header to be set
         * @return the current instance of the Builder for method chaining
         */
        public Builder contentType(String contentType) {
            return header("Content-Type", contentType);
        }

        /**
         * Sets the "Content-Type" header to "application/xml" and the "Accept" header to
         * "application/xml; charset=UTF-8" for the current request.
         *
         * @return this builder instance with updated headers, allowing method chaining
         */
        public Builder contentTypeXML() {
            return contentType("application/xml").accept("application/xml; charset=UTF-8");
        }

        /**
         * Sets the Content-Type header to "application/json" and the Accept header to
         * "application/json; charset=UTF-8".
         *
         * @return the updated Builder instance for method chaining
         */
        public Builder contentTypeJSON() {
            return contentType("application/json").accept("application/json; charset=UTF-8");
        }

        /**
         * Sets the Content-Type header to "application/x-www-form-urlencoded; charset=UTF-8".
         *
         * @return the Builder instance with the updated Content-Type header.
         */
        public Builder contentTypeForm() {
            return contentType("application/x-www-form-urlencoded; charset=UTF-8");
        }

        /**
         * Sets the "Accept" header to specify the media types that the client can process.
         *
         * @param accept the value of the "Accept" header, defining the desired media types.
         * @return the current Builder instance for method chaining.
         */
        public Builder accept(String accept) {
            return header("Accept", accept);
        }

        /**
         * Configures the builder to accept responses with a MIME type of "application/xml".
         *
         * @return the updated Builder instance configured to accept "application/xml" responses.
         */
        public Builder acceptXML() {
            return accept("application/xml");
        }

        /**
         * Sets the "Accept" header of the HTTP request to "application/json".
         *
         * @return the updated Builder instance
         */
        public Builder acceptJSON() {
            return accept("application/json");
        }

        /**
         * Configures the Builder to accept any type of content.
         *
         * @return the updated Builder instance configured to accept all media types
         */
        public Builder acceptAny() {
            return accept("*/*");
        }

        /**
         * Configures the SSLContext to be used by the HTTP client.
         *
         * @param sslContext the SSLContext instance to be used for secure HTTPS connections.
         *                   If null is passed it will be ignored.
         * @return the current Builder instance for method chaining
         */
        public Builder sslContext(SSLContext sslContext) {
            if(sslContext != null) {
                getClientBuilder().sslContext(sslContext);
            }
            return this;
        }

        /**
         * Configures the SSL parameters to be used for HTTPS connections in the request.
         *
         * @param sslParameters the {@link SSLParameters} instance to configure SSL settings
         *                   If null is passed it will be ignored.
         * @return the updated {@code Builder} instance for method chaining
         */
        public Builder sslParameters(SSLParameters sslParameters) {
            if(sslParameters != null) {
                getClientBuilder().sslParameters(sslParameters);
            }
            return this;
        }

        /**
         * Configures the HTTP client to use the specified {@link ProxySelector} for proxy settings.
         * The proxy selector determines the proxy to use for a given HTTP request.
         *
         * @param proxy the {@link ProxySelector} instance to configure proxy settings for the HTTP client
         *                   If null is passed it will be ignored.
         * @return the current {@code Builder} instance for method chaining
         */
        public Builder proxy(ProxySelector proxy) {
            if(proxy != null) {
                getClientBuilder().proxy(proxy);
            }
            return this;
        }

        /**
         * Sets the local address for the HTTP client. This is used to specify the network
         * interface from which the connection will be made.
         *
         * @param address the {@link InetAddress} representing the local address or network
         *                interface to bind to. If null is passed it will be ignored.
         * @return the updated {@code Builder} instance for method chaining.
         */
        public Builder localAddress(InetAddress address) {
            if(address != null) {
                getClientBuilder().localAddress(address);
            }
            return this;
        }

        /**
         * Retrieves the HTTP response as an {@link HttpResponse} containing the {@link InputStream}.
         * <p>Note: If you invoke this method before using any of the retrieval methods such as
         * {@link #string()}, {@link #xml()}, {@link #json()} or its async cousins, the result will be null.</p>
         * <p>Warning: The {@link InputStream} instance of this is not useful because it is already fully read by the
         * time this is available. However, the response can be used to check other details of the response.</p>
         *
         * @return the HTTP response that wraps an {@link InputStream}, providing the body of the HTTP response.
         */
        public HttpResponse<InputStream> response() {
            return response;
        }

        /**
         * Retrieves the HTTP status code from the response object.
         * If the response is null, it returns -1.
         * <p>Note: If you invoke this method before using any of the retrieval methods such as
         * {@link #string()}, {@link #xml()}, {@link #json()} or its async cousins, the result will be -1.</p>
         *
         * @return the HTTP status code of the response, or -1 if the response is null
         */
        public int statusCode() {
            return response == null ? -1 : response.statusCode();
        }

        /**
         * Retrieves and converts the response content stream into a string representation.
         *
         * @return the string representation of the response content
         */
        public String string() {
            Exception lastError = null;
            for (int attempt = 0; attempt <= maxRetries; attempt++) {
                if (attempt > 0) {
                    debug("[HTTP2] Retry attempt " + attempt + " for " + url);
                    try {
                        //noinspection BusyWait
                        Thread.sleep(retryDelay.toMillis());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                try {
                    if (enableDebugLogging) {
                        HttpRequest tempRequest = build(url, body, headers, requestCustomizer);
                        logRequest(tempRequest);
                    }
                    InputStream in = HTTP2.stream(this);
                    return toString(in);
                } catch (Exception e) {
                    lastError = e;
                    error("Attempt " + (attempt + 1) + " failed: " + e.getMessage(), e);
                    error(e);
                }
            }
            error(lastError);
            return null;
        }

        /**
         * Asynchronously retrieves the response body as a string.
         *
         * @return A CompletableFuture containing the response body as a String. The CompletableFuture
         *         can be completed normally with the response body or exceptionally if an error occurs.
         */
        public CompletableFuture<String> stringAsync() {
            if (enableDebugLogging) {
                HttpRequest tempRequest = build(url, body, headers, requestCustomizer);
                logRequest(tempRequest);
            }
            return HTTP2.async(this, this::toString);
        }

        /**
         * Retrieves the InputStream representation of the response generated by the associated HTTP2 call.
         * In the event of an exception during the execution of the HTTP2 stream, this method returns null.
         *
         * @return an InputStream containing the HTTP2 response body, or null if an exception occurs
         */
        public InputStream stream() {
            try {
                return HTTP2.stream(this);
            } catch (Exception e) {
                error(e);
                return null;
            }
        }

        /**
         * Asynchronously retrieves the HTTP response body as an InputStream.
         *
         * @return a CompletableFuture representing the asynchronous computation
         *         of the HTTP response body streamed as an InputStream.
         */
        public CompletableFuture<InputStream> streamAsync() {
            return async(this, Function.identity());
        }

        /**
         * Retrieves and converts the response content, set to an XML content type, into an XML object.
         *
         * @return an XML object representation of the response content
         */
        public XML xml() {
            return toXML(contentTypeXML().stream());
        }

        /**
         * Asynchronously retrieves the response and parses it as an XML object.
         * This method sets the Content-Type of the request to application/xml
         * and invokes the XML parsing logic.
         *
         * @return a CompletableFuture that completes with the parsed XML object
         *         representing the server's response, or completes exceptionally
         *         if an error occurs during the request or parsing.
         */
        public CompletableFuture<XML> xmlAsync() {
            return async(contentTypeXML(), this::toXML);
        }

        /**
         * Retrieves and converts the response content to a JSON object.
         *
         * @return a JSON object representing the parsed response content
         */
        public JSON json() {
            return toJSON(contentTypeJSON().stream());
        }

        /**
         * Asynchronously retrieves the response and parses it as a JSON object.
         *
         * @return a CompletableFuture that completes with the parsed JSON object
         */
        public CompletableFuture<JSON> jsonAsync() {
            return async(contentTypeJSON(), this::toJSON);
        }

        private JSON toJSON(InputStream in) {
            try {
                return new JSON(in);
            } catch (Exception e) {
                error(e);
                return null;
            }
        }

        /**
         * Retrieves the exception associated with the current context.
         *
         * @return the Exception object representing the error, or null if no exception is present.
         */
        public Exception getException() {
            return error;
        }

        private XML toXML(InputStream in) {
            try {
                return new XML(in);
            } catch (Exception e) {
                error(e);
                return null;
            }
        }

        private String toString(InputStream in) {
            try {
                return StringUtility.toString(in);
            } catch (Exception e) {
                error(e);
                return null;
            }
        }

        /**
         * Retrieves or initializes the {@link HttpClient.Builder} instance for configuring
         * and building {@link HttpClient} objects. If the {@code httpClientBuilder} is null,
         * this method initializes it.
         * <p>Note: If this method is invoked, a custom client builder is created that you can manipulate directly.
         * It will not affect any other connections of {@link HTTP2}.</p>
         * <p>Warning: Make sure that you don't call the {@link HttpClient.Builder#build()} method on the instance
         * returned by this. It will be automatically called internally when required.</p>
         *
         * @return the {@link HttpClient.Builder} instance, either existing or newly constructed.
         */
        public HttpClient.Builder getClientBuilder() {
            if(httpClientBuilder == null) {
                httpClientBuilder = newHHttpClientBuilder();
            }
            return httpClientBuilder;
        }

        private HttpClient httpClient() {
            return HTTP2.getClient(httpClientBuilder);
        }

        private static class ChainedCustomizer extends ArrayList<Consumer<HttpRequest.Builder>>
                implements Consumer<HttpRequest.Builder> {

            @Override
            public void accept(HttpRequest.Builder builder) {
                forEach(c -> c.accept(builder));
            }
        }
    }

    /**
     * Handles chunked transfer encoding properly.
     * Fixed version with proper CRLF validation.
     */
    private static class ChunkedInputStream extends InputStream {

        private final InputStream in;
        private final boolean isChunked;
        private int chunkSize = -1;
        private int chunkPos = 0;

        public ChunkedInputStream(HttpResponse<InputStream> response) {
            this.in = response.body();
            String transferEncoding = response.headers().firstValue("Transfer-Encoding").orElse(null);
            this.isChunked = "chunked".equalsIgnoreCase(transferEncoding);
        }

        @Override
        public int read() throws IOException {
            if (!isChunked) {
                return in.read();
            }
            if (chunkSize == 0) {
                return -1;
            }
            if (chunkSize == -1 || chunkPos >= chunkSize) {
                chunkSize = readChunkSize();
                if (chunkSize == 0) {
                    return -1;
                }
                chunkPos = 0;
                // Read and discard CR LF after chunk
                readCrLf();
            }
            chunkPos++;
            return in.read();
        }

        private int readChunkSize() throws IOException {
            StringBuilder hex = new StringBuilder();
            int b;
            while ((b = in.read()) != -1) {
                if (b == '\r') {
                    int next = in.read();
                    if (next != '\n') {
                        throw new IOException("Invalid chunk encoding: expected LF after CR");
                    }
                    break;
                }
                hex.append((char) b);
            }
            if (hex.length() == 0) {
                throw new IOException("Invalid chunk encoding: empty chunk size");
            }
            try {
                return Integer.parseInt(hex.toString(), 16);
            } catch (NumberFormatException e) {
                throw new IOException("Invalid chunk encoding: invalid hex number", e);
            }
        }

        private void readCrLf() throws IOException {
            int cr = in.read();
            int lf = in.read();
            if (cr != '\r' || lf != '\n') {
                throw new IOException("Invalid chunk encoding: expected CRLF after chunk data");
            }
        }

        @Override
        public int available() throws IOException {
            return isChunked ? 0 : in.available();
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }

    /**
     * Handles decompression of gzip and deflate encoded responses.
     * Fixed version with proper resource cleanup on error.
     */
    private static class DecompressingInputStream extends InputStream {

        private final InputStream decompressedStream;

        public DecompressingInputStream(HttpResponse<InputStream> response) throws IOException {
            InputStream in = new ChunkedInputStream(response);
            try {
                String contentEncoding = response.headers().firstValue("Content-Encoding").orElse(null);
                if (contentEncoding != null) {
                    if (contentEncoding.equalsIgnoreCase("gzip")) {
                        in = new GZIPInputStream(in);
                    } else if (contentEncoding.equalsIgnoreCase("deflate")) {
                        in = new InflaterInputStream(in);
                    }
                }
                this.decompressedStream = in;
            } catch (Exception e) {
                in.close();
                throw e;
            }
        }

        @Override
        public int read() throws IOException {
            return decompressedStream.read();
        }

        @Override
        public int available() throws IOException {
            return decompressedStream.available();
        }

        @Override
        public void close() throws IOException {
            decompressedStream.close();
        }
    }
}