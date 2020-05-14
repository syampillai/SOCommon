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

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Generic Input/Output routines.
 * All 'get' methods return 'buffered' streams/readers/writers. 'Double buffering' is avoided.
 * Conversion between streams and reader/writer are with UTF-8 encoding.
 *
 * @author Syam
 */
public class IO {

    /**
     * Copy one stream to another. Both the streams will be closed.
     * @param input Source stream
     * @param output Destination stream
     * @throws IOException I/O exception
     */
    public static void copy(InputStream input, OutputStream output) throws IOException {
        try(InputStream in = get(input); OutputStream out = get(output)) {
            byte[] buf = new byte[2048];
            int r;
            while((r = in.read(buf)) != -1) {
                out.write(buf, 0, r);
            }
        }
    }

    /**
     * Copy one stream to another
     * @param input Source stream
     * @param output Destination stream
     * @param close Whether to close the streams or not
     * @throws IOException I/O exception
     */
    public static void copy(InputStream input, OutputStream output, boolean close) throws IOException {
        if(close) {
            copy(input, output);
        } else {
            copy(new NoCloseInputStream(input), new NoCloseOutputStream(output));
        }
    }

    private static class NoCloseInputStream extends FilterInputStream {

        protected NoCloseInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() {
        }
    }

    private static class NoCloseOutputStream extends FilterOutputStream {

        protected NoCloseOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void close() {
        }
    }

    /**
     * Copy from reader to writer. Both reader and writer will be closed.
     * @param reader Reader
     * @param writer Writer
     * @throws IOException I/O exception
     */
    public static void copy(Reader reader, Writer writer) throws IOException {
        int c;
        try(BufferedReader r = get(reader); BufferedWriter w = get(writer)) {
            while((c = r.read()) != -1) {
                w.write(c);
            }
        }
    }

    /**
     * Copy from reader to writer. Both reader and writer will be closed.
     * @param reader Reader
     * @param writer Writer
     * @param close Whether to close the reader/writer or not
     * @throws IOException I/O exception
     */
    public static void copy(Reader reader, Writer writer, boolean close) throws IOException {
        if(close) {
            copy(reader, writer);
        } else {
            copy(new NoCloseReader(reader), new NoCloseWriter(writer));
        }
    }

    private static class NoCloseReader extends FilterReader {

        protected NoCloseReader(Reader in) {
            super(in);
        }

        @Override
        public void close() {
        }
    }

    private static class NoCloseWriter extends FilterWriter {

        protected NoCloseWriter(Writer out) {
            super(out);
        }

        @Override
        public void close() {
        }
    }

    /**
     * Convert a stream to a buffered stream
     * @param in Stream to convert
     * @return Buffered stream
     */
    public static BufferedInputStream get(InputStream in) {
        if(in == null || in instanceof BufferedInputStream) {
            return (BufferedInputStream)in;
        }
        return new BufferedInputStream(in);
    }

    /**
     * Convert a stream to a buffered stream
     * @param out Stream to convert
     * @return Buffered stream
     */
    public static BufferedOutputStream get(OutputStream out) {
        if(out == null || out instanceof BufferedOutputStream) {
            return (BufferedOutputStream)out;
        }
        return new BufferedOutputStream(out);
    }

    /**
     * Convert a stream to a buffered UTF-8 reader
     * @param in Stream to convert
     * @return Buffered reader
     */
    public static BufferedReader getReader(InputStream in) {
        try {
            return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch(Throwable ignored) {
        }
        return null;
    }

    /**
     * Convert a stream to a buffered UTF-8 writer
     * @param out Stream to convert
     * @return Buffered writer
     */
    public static BufferedWriter getWriter(OutputStream out) {
        try {
            return new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        } catch(Throwable ignored) {
        }
        return null;
    }

    /**
     * Convert a reader to a buffered reader
     * @param in Reader to convert
     * @return Buffered reader
     */
    public static BufferedReader get(Reader in) {
        if(in == null || in instanceof BufferedReader) {
            return (BufferedReader)in;
        }
        return new BufferedReader(in);
    }

    /**
     * Convert a writer to a buffered writer
     * @param out Writer to convert
     * @return Buffered writer
     */
    public static BufferedWriter get(Writer out) {
        if(out == null || out instanceof BufferedWriter) {
            return (BufferedWriter)out;
        }
        return new BufferedWriter(out);
    }

    /**
     * Convert a stream to a buffered data stream
     * @param in Stream to convert
     * @return Buffered data stream
     */
    public static DataInputStream getData(InputStream in) {
        return new DataInputStream(get(in));
    }

    /**
     * Convert a stream to a buffered data stream
     * @param out Stream to convert
     * @return Buffered data stream
     */
    public static DataOutputStream getData(OutputStream out) {
        return new DataOutputStream(get(out));
    }

    /**
     * Create a buffered stream from the file
     * @param fileName Name of the file
     * @return Buffered stream
     * @throws IOException if I/O error occurs.
     */
    public static BufferedInputStream getInput(String fileName) throws IOException {
        return getInput(getPath(fileName));
    }

    /**
     * Get the path class for the file name passed.
     * @param fileName Name of the file (with directory separators if any)
     * @return Path
     */
    public static Path getPath(String fileName) {
        return FileSystems.getDefault().getPath(fileName);
    }

    /**
     * Create a buffered stream from the file
     * @param file File
     * @return Buffered stream
     * @throws IOException if I/O error occurs.
     */
    public static BufferedInputStream getInput(File file) throws IOException {
        return getInput(file.toPath());
    }

    /**
     * Create a buffered stream from the path
     * @param path Path
     * @return Buffered stream
     * @throws IOException if I/O error occurs.
     */
    public static BufferedInputStream getInput(Path path) throws IOException {
        return get(Files.newInputStream(path));
    }

    /**
     * Create a buffered stream to write for the file
     * @param fileName Name of the file
     * @return Buffered stream
     * @throws IOException if I/O error occurs.
     */
    public static BufferedOutputStream getOutput(String fileName) throws IOException {
        return getOutput(getPath(fileName));
    }

    /**
     * Create a buffered stream to write for the file
     * @param file File
     * @return Buffered stream
     * @throws IOException if I/O error occurs.
     */
    public static BufferedOutputStream getOutput(File file) throws IOException {
        return getOutput(file.toPath());
    }

    /**
     * Create a buffered stream to write for the file
     * @param path Path
     * @return Buffered stream
     * @throws IOException if I/O error occurs.
     */
    public static BufferedOutputStream getOutput(Path path) throws IOException {
        return get(Files.newOutputStream(path));
    }

    /**
     * Create a buffered data stream from the file
     * @param fileName Name of the file
     * @return Buffered stream
     * @throws IOException if I/O error occurs.
     */
    public static DataInputStream getDataInput(String fileName) throws IOException {
        return getDataInput(getPath(fileName));
    }

    /**
     * Create a buffered data stream from the file
     * @param file File
     * @return Buffered stream
     * @throws IOException if I/O error occurs.
     */
    public static DataInputStream getDataInput(File file) throws IOException {
        return getDataInput(file.toPath());
    }

    /**
     * Create a buffered data stream from the file
     * @param path Path
     * @return Buffered stream
     * @throws IOException if I/O error occurs.
     */
    public static DataInputStream getDataInput(Path path) throws IOException {
        return getData(Files.newInputStream(path));
    }

    /**
     * Create a buffered data stream to write for the file
     * @param fileName Name of the file
     * @return Buffered stream
     * @throws IOException if I/O error occurs.
     */
    public static DataOutputStream getDataOutput(String fileName) throws IOException {
        return getDataOutput(getPath(fileName));
    }

    /**
     * Create a buffered data stream to write for the file
     * @param file File
     * @return Buffered stream
     * @throws IOException if I/O error occurs.
     */
    public static DataOutputStream getDataOutput(File file) throws IOException {
        return getDataOutput(file.toPath());
    }

    /**
     * Create a buffered data stream to write for the path
     * @param path Path
     * @return Buffered stream
     * @throws IOException if I/O error occurs.
     */
    public static DataOutputStream getDataOutput(Path path) throws IOException {
        return getData(Files.newOutputStream(path));
    }

    /**
     * Create a buffered reader from the file
     * @param fileName Name of the file
     * @return Buffered reader
     * @throws IOException if I/O error occurs.
     */
    public static BufferedReader getReader(String fileName) throws IOException {
        return getReader(getPath(fileName));
    }

    /**
     * Create a buffered reader from the file
     * @param file File
     * @return Buffered reader
     * @throws IOException if I/O error occurs.
     */
    public static BufferedReader getReader(File file) throws IOException {
        return getReader(file.toPath());
    }

    /**
     * Create a buffered reader from the path
     * @param path Path
     * @return Buffered reader
     * @throws IOException if I/O error occurs.
     */
    public static BufferedReader getReader(Path path) throws IOException {
        return Files.newBufferedReader(path);
    }

    /**
     * Create a buffered writer to write for the file
     * @param fileName Name of the file
     * @return Buffered writer
     * @throws IOException if I/O error occurs.
     */
    public static BufferedWriter getWriter(String fileName) throws IOException {
        return getWriter(getPath(fileName));
    }

    /**
     * Create a buffered writer to write for the file
     * @param file File
     * @return Buffered writer
     * @throws IOException if I/O error occurs.
     */
    public static BufferedWriter getWriter(File file) throws IOException {
        return getWriter(file.toPath());
    }

    /**
     * Create a buffered writer to write for the path
     * @param path Path
     * @return Buffered writer
     * @throws IOException if I/O error occurs.
     */
    public static BufferedWriter getWriter(Path path) throws IOException {
        return Files.newBufferedWriter(path);
    }

    private static NullInputStream nullIn;
    private static NullOutputStream nullOut;

    private static class NullInputStream extends InputStream {

        @Override
        public int read() {
            return -1;
        }

        @Override
        public int read(byte[] b) {
            return -1;
        }

        @Override
        public int read(byte[] b, int off, int len) {
            return -1;
        }

        @Override
        public int available() {
            return 0;
        }

        @Override
        public void close() {
        }
    }

    private static class NullOutputStream extends OutputStream {

        @Override
        public void write(int b) {
        }

        @Override
        public void write(byte[] b) {
        }

        @Override
        public void write(byte[] b, int off, int len) {
        }

        @Override
        public void close() {
        }
    }

    /**
     * Create a null input stream. A read from this stream always returns EOF (-1).
     *
     * @return Null input stream
     */
    public static InputStream nullInput() {
        if(nullIn == null) {
            nullIn = new NullInputStream();
        }
        return nullIn;
    }

    /**
     * Create a null output stream. Anything written to this stream just disappears.
     *
     * @return Null output stream
     */
    public static OutputStream nullOutput() {
        if(nullOut == null) {
            nullOut = new NullOutputStream();
        }
        return nullOut;
    }

    /**
     * Connect an output stream to an input stream so that the input stream is closed when the output stream is closed.
     *
     * @param out Output stream
     * @param in Input stream to connect to
     * @return Modified output stream
     */
    public static OutputStream connect(OutputStream out, InputStream in) {
        return new FilterOutputStream(out) {
            public void close() {
                IO.close(out, in);
            }
        };
    }

    /**
     * Connect an input stream to an output stream so that the output stream is closed when the input stream is closed.
     *
     * @param in Input stream
     * @param out Output stream to connect to
     * @return Modified input stream
     */
    public static InputStream connect(InputStream in, OutputStream out) {
        return new FilterInputStream(in) {
            public void close() {
                IO.close(out, in);
            }
        };
    }

    /**
     * Create a Tee output stream that simultaneously writes content to 2 different output streams.
     *
     * @param first First output stream
     * @param second Second output stream
     * @return An output stream that will write to 2 simultaneous streams.
     */
    public static OutputStream tee(OutputStream first, OutputStream second) {
        return new TeeOutputStream(first, second);
    }

    /**
     * Create a read only byte buffer from the content of the input stream.
     *
     * @param in Input stream
     * @return A read only byte buffer containing the content of the input stream.
     * @throws IOException if IO can not be happen.
     */
    public static ByteBuffer readOnlyByteBuffer(InputStream in) throws IOException {
        return byteBuf(in, FileChannel.MapMode.READ_ONLY, "r");
    }

    /**
     * Create a read/write byte buffer from the content of the input stream.
     *
     * @param in Input stream
     * @return A read/write byte buffer containing the content of the input stream.
     * @throws IOException if buffer can not be created.
     */
    public static ByteBuffer byteBuffer(InputStream in) throws IOException {
        return byteBuf(in, FileChannel.MapMode.READ_WRITE, "rw");
    }

    private static ByteBuffer byteBuf(InputStream in, FileChannel.MapMode mode, String fileMode) throws IOException {
        File file = File.createTempFile("SOBuffer", "data");
        file.deleteOnExit();
        copy(get(in), new FileOutputStream(file));
        return new RandomAccessFile(file, fileMode).getChannel().map(mode, 0, file.length());
    }

    /**
     * Close some {@link Closeable} instances without throwing any {@link Exception}.
     *
     * @param closeables Closable instances to close.
     */
    public static void close(AutoCloseable... closeables) {
        if(closeables != null) {
            for(AutoCloseable closeable: closeables) {
                try {
                    if(closeable != null) {
                        closeable.close();
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }
}