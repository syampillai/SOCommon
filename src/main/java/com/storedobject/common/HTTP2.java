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
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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
 * <pre>
 *     Typical usage:
 *     String s = HTTP2.build("https://www.google.com").string();
 *     System.out.println("Google's default page's content is: " + s);
 *
 *     JSON json = HTT2.build("https://www.example.com?api=xxx").json(); // Get the JSON content
 * </pre>
 *
 * @author Syam
 */
public class HTTP2 {

    private static final HttpClient httpClient = newHHttpClientBuilder().build();
    private static final Map<String, String> DEFAULT_HEADERS = Map.of(
            "User-Agent", "SOClient/1.0",
            "Accept-Charset", "UTF-8",
            "Accept-Encoding", "gzip, deflate"
    );

    private static HttpClient.Builder newHHttpClientBuilder() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL);
    }

    private static InputStream stream(Builder b) throws Exception {
        b.response = b.httpClient().send(build(b.url, b.body, b.headers, b.requestCustomizer),
                HttpResponse.BodyHandlers.ofInputStream());
        return b.response.statusCode() == 200 ? new DecompressingInputStream(b.response) : null;
    }

    private static <T> CompletableFuture<T> async(Builder b, Function<InputStream, T> transformer) {
        return async(b).thenApply(r -> {
            b.response = r;
            if(r.statusCode() != 200) {
                return null;
            }
            try {
                return transformer.apply(new DecompressingInputStream(r));
            } catch (Exception e) {
                b.exception = e;
                return null;
            }
        });
    }

    private static CompletableFuture<HttpResponse<InputStream>> async(Builder b) {
        return b.httpClient().sendAsync(build(b.url, b.body, b.headers, b.requestCustomizer), HttpResponse.BodyHandlers.ofInputStream());
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
     * body, timeouts, and other configurations related to the HTTP client and requests.
     * The builder also supports synchronous and asynchronous operations for request execution.
     *
     * @author Syam
     */
    public static class Builder {

        String url;
        Map<String, String> headers;
        ChainedCustomizer requestCustomizer;
        String body;
        Exception exception;
        HttpResponse<InputStream> response; // HTTP2 will set this jst before reading the data
        HttpClient.Builder httpClientBuilder;

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
         * Adds a header to the request being built. If the headers map is null, it initializes
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
         * Configures the builder to accept any media type by setting the "Accept" header to "*/
        public Builder acceptAny() {
            return accept("*/*");
        }

        /**
         * Configures the SSLContext to be used by the HTTP client.
         *
         * @param sslContext the SSLContext instance to be used for secure HTTPS connections
         * @return the current Builder instance for method chaining
         */
        public Builder sslContext(SSLContext sslContext) {
            getClientBuilder().sslContext(sslContext);
            return this;
        }

        /**
         * Configures the SSL parameters to be used for HTTPS connections in the request.
         *
         * @param sslParameters the {@link SSLParameters} instance to configure SSL settings
         * @return the updated {@code Builder} instance for method chaining
         */
        public Builder sslParameters(SSLParameters sslParameters) {
            getClientBuilder().sslParameters(sslParameters);
            return this;
        }

        /**
         * Configures the HTTP client to use the specified {@link ProxySelector} for proxy settings.
         * The proxy selector determines the proxy to use for a given HTTP request.
         *
         * @param proxy the {@link ProxySelector} instance to configure proxy settings for the HTTP client
         * @return the current {@code Builder} instance for method chaining
         */
        public Builder proxy(ProxySelector proxy) {
            getClientBuilder().proxy(proxy);
            return this;
        }

        /**
         * Sets the local address for the HTTP client. This is used to specify the network
         * interface from which the connection will be made.
         *
         * @param address the {@link InetAddress} representing the local address or network
         *                interface to bind to.
         * @return the updated {@code Builder} instance for method chaining.
         */
        public Builder localAddress(InetAddress address) {
            getClientBuilder().localAddress(address);
            return this;
        }

        /**
         * Retrieves the HTTP response as an {@link HttpResponse} containing an {@link InputStream}.
         *
         * @return the HTTP response that wraps an {@link InputStream}, providing the body of the HTTP response.
         */
        public HttpResponse<InputStream> response() {
            return response;
        }

        /**
         * Converts the response content stream into a string representation.
         *
         * @return the string representation of the response content
         * @throws Exception if an error occurs while processing the stream
         */
        public String string() throws Exception {
            return StringUtility.toString(stream());
        }

        /**
         * Asynchronously retrieves the response body as a string.
         *
         * @return A CompletableFuture containing the response body as a String. The CompletableFuture
         *         can be completed normally with the response body or exceptionally if an error occurs.
         */
        public CompletableFuture<String> stringAsync() {
            return HTTP2.async(this, this::toString);
        }

        /**
         * Retrieves an InputStream representation of the response generated by the associated HTTP2 call.
         * In the event of an exception during the execution of the HTTP2 stream, this method returns null.
         *
         * @return an InputStream containing the HTTP2 response body, or null if an exception occurs
         */
        public InputStream stream() {
            try {
                return HTTP2.stream(this);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Converts the response content, set to an XML content type, into an XML object.
         *
         * @return an XML object representation of the response content
         */
        public XML xml() {
            return toXML(contentTypeXML().stream());
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
         * Converts the response content of type JSON into a JSON object.
         *
         * @return a JSON object representing the parsed response content
         */
        public JSON json() {
            return toJSON(contentTypeJSON().stream());
        }

        private JSON toJSON(InputStream in) {
            try {
                return new JSON(in);
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        public Exception getException() {
            return exception;
        }

        private XML toXML(InputStream in) {
            try {
                return new XML(in);
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        private String toString(InputStream in) {
            try {
                return StringUtility.toString(in);
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        /**
         * Retrieves or initializes the {@link HttpClient.Builder} instance for configuring
         * and building {@link HttpClient} objects. If the {@code httpClientBuilder} is null,
         * this method initializes it.
         * <p>Note: If this method is invoked a custom client builder is created that you can manipulate directly.
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
            if (httpClientBuilder != null) {
                return httpClientBuilder.build();
            }
            return HTTP2.httpClient;
        }

        private static class ChainedCustomizer extends ArrayList<Consumer<HttpRequest.Builder>>
                implements Consumer<HttpRequest.Builder> {

            @Override
            public void accept(HttpRequest.Builder builder) {
                forEach(c -> c.accept(builder));
            }
        }
    }

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
                return in.read(); // Normal read if not chunked
            }
            if (chunkSize == 0) {
                return -1; // End of stream
            }
            if (chunkSize == -1 || chunkPos >= chunkSize) {
                chunkSize = readChunkSize();
                if (chunkSize == 0) {
                    return -1; // End of chunked stream
                }
                chunkPos = 0;
                //Read and discard the trailing \r\n
                //noinspection ResultOfMethodCallIgnored
                in.read();
                //noinspection ResultOfMethodCallIgnored
                in.read();
            }
            chunkPos++;
            return in.read();
        }

        private int readChunkSize() throws IOException {
            StringBuilder hex = new StringBuilder();
            int b;
            while ((b = in.read()) != -1) {
                if (b == '\r') {
                    //noinspection ResultOfMethodCallIgnored
                    in.read();
                    break;
                }
                hex.append((char) b);
            }
            return Integer.parseInt(hex.toString(), 16);
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

    private static class DecompressingInputStream extends InputStream {

        private final InputStream decompressedStream;

        public DecompressingInputStream(HttpResponse<InputStream> response) throws IOException {
            InputStream in = new ChunkedInputStream(response);
            String contentEncoding = response.headers().firstValue("Content-Encoding").orElse(null);
            boolean isCompressed = contentEncoding.equalsIgnoreCase("gzip") || contentEncoding.equalsIgnoreCase("deflate");
            if (isCompressed) {
                if (contentEncoding.equalsIgnoreCase("gzip")) {
                    in = new GZIPInputStream(in);
                } else if (contentEncoding.equalsIgnoreCase("deflate")) {
                    in = new InflaterInputStream(in);
                }
            }
            this.decompressedStream = in;
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
