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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * A file-mapped data buffer with index-based access to data for writing and then, for reading.
 * Data is just byte arrays of arbitrary length.
 *
 * @author Syam
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileBuffer implements ResourceOwner {

    private final AutoCloseableList closeables = new AutoCloseableList();
    private File dataFile, indexFile;
    private ByteBuffer dataBuffer;
    private IntBuffer indexBuffer;
    private BufferedOutputStream dataOut;
    private DataOutputStream indexOut;
    private int size = 0, dataSize = 0;
    private byte created = -1;
    private boolean writable = false;
    private FileBuffer parent = null, child = null;

    /**
     * Constructor.
     */
    public FileBuffer() {
        ResourceDisposal.register(this);
    }

    /**
     * Get the "resource" owned by this buffer.
     *
     * @return The "resource" owned by this buffer.
     */
    @Override
    public AutoCloseable getResource() {
        return closeables;
    }

    /**
     * This method must be called before writing anything.
     */
    public void begin() {
        if(created > -1) {
            return;
        }
        try {
            dataFile = File.createTempFile("SOBuffer", "data");
            dataFile.deleteOnExit();
            dataOut = IO.getOutput(dataFile);
            closeables.add(dataOut);
            indexFile = File.createTempFile("SOBuffer", "index");
            indexFile.deleteOnExit();
            indexOut = IO.getDataOutput(indexFile);
            closeables.add(indexOut);
            created = 0;
        } catch(Exception e) {
            close();
        }
    }

    /**
     * This method must be called when writing is completed. After this, the buffer will be in read-only mode and
     * any attempt to write will cause errors.
     */
    public void end() {
        if(created != 0) {
            return;
        }
        RandomAccessFile file = null;
        try {
            dataOut.close();
            dataOut = null;
            indexOut.close();
            indexOut = null;
            file = new RandomAccessFile(dataFile, "r");
            dataBuffer = file.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, dataFile.length());
            file.close();
            file = new RandomAccessFile(indexFile, "r");
            indexBuffer = file.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, indexFile.length()).asIntBuffer();
            file.close();
            created = 1;
        } catch(Exception e) {
            if(file != null) {
                try {
                    file.close();
                } catch(IOException ignore) {
                }
            }
            close();
        }
    }

    /**
     * Write something at the current index. The data could be of any length. The first index is zero.
     *
     * @param data Data to write.
     * @throws Exception Throws if any error occurs.
     */
    public void write(byte[] data) throws Exception {
        indexOut.writeInt(dataSize);
        int len = data.length;
        if(len == 0 || len > 127) {
            dataOut.write(0);
            dataOut.write(ByteBuffer.allocate(4).putInt(len).array());
            dataSize += 5;
        } else {
            dataOut.write(len);
            dataSize++;
        }
        dataOut.write(data);
        ++size;
        dataSize += data.length;
    }

    /**
     * Close this buffer. No more reading or writing is possible after this.
     */
    public void close() {
        closeables.close();
        size = dataSize = 0;
        dataBuffer = null;
        indexBuffer = null;
        dataOut = null;
        indexOut = null;
        if(dataFile != null) {
            if(parent == null && child == null) {
                dataFile.delete();
            }
        }
        dataFile = null;
        if(indexFile != null) {
            indexFile.delete();
        }
        indexFile = null;
        if(parent != null) {
            parent.child = child;
        }
        if(child != null) {
            child.parent = parent;
        }
    }

    /**
     * Get the size of this buffer in terms of number of indices it holds. Each index may be pointing to
     * varying chunks of data.
     *
     * @return Number of indices written available in the buffer.
     */
    public int size() {
        return size;
    }

    /**
     * Read data from a specified index.
     *
     * @param index Index.
     * @return Data read.
     */
    public byte[] read(int index) {
        index = indexBuffer.get(index);
        int len = dataBuffer.get(index);
        ++index;
        if(len == 0) {
            dataBuffer.getInt(index);
            index += 4;
        }
        byte[] data = new byte[len];
        dataBuffer.position(index);
        dataBuffer.get(data);
        return data;
    }

    /**
     * Swap positions of 2 data chunks.
     *
     * @param firstIndex First index.
     * @param secondIndex Second index.
     */
    public void swap(int firstIndex, int secondIndex) {
        if(firstIndex == secondIndex) {
            return;
        }
        if(!writable) {
            setWritable();
        }
        int t = indexBuffer.get(firstIndex);
        indexBuffer.put(firstIndex, indexBuffer.get(secondIndex));
        indexBuffer.put(secondIndex, t);
    }

    /**
     * Sort the data according the comparator passed.
     *
     * @param comparator Comparator to compare the data chunks.
     * @return Sorter buffer.
     */
    public FileBuffer sort(Comparator<byte[]> comparator) {
        FileBuffer f = copy(null);
        if(f != null) {
            f.sort(comparator, 0, f.size - 1);
        }
        return f;
    }

    private void sort(Comparator<byte[]> comparator, int low, int high) {
        int i = low, j = high;
        byte[] pivot = read(low + ((high - low) >> 1));
        while(i <= j) {
            while(comparator.compare(read(i), pivot) < 0) {
                i++;
            }
            while(comparator.compare(read(j), pivot) > 0) {
                j--;
            }
            if(i <= j) {
                swap(i, j);
                i++;
                j--;
            }
        }
        if(low < j) {
            sort(comparator, low, j);
        }
        if(i < high) {
            sort(comparator, i, high);
        }
    }

    /**
     * Apply a filter to the buffer.
     *
     * @param filter Filter to be applied.
     * @return Filtered buffer (if the filter is <code>null</code> or if all the data chunks are satisfying the
     * filter, then, the same buffer will be returned).
     */
    public FileBuffer filter(Predicate<byte[]> filter) {
        if(filter == null) {
            return this;
        }
        FileBuffer f = copy(filter);
        if(f != null) {
            if(f.size == size) {
                f.indexFile.delete();
                return this;
            }
            chain(f);
        }
        return f;
    }

    private void chain(FileBuffer f) {
        if(child == null) {
            child = f;
            f.parent = this;
            return;
        }
        child.chain(f);
    }

    private FileBuffer copy(Predicate<byte[]> filter) {
        FileBuffer f = new FileBuffer();
        f.dataBuffer = dataBuffer.asReadOnlyBuffer();
        f.dataSize = dataSize;
        try {
            f.indexFile = File.createTempFile("SOBuffer", "index");
            f.indexFile.deleteOnExit();
            f.indexOut = IO.getDataOutput(f.indexFile);
            for(int i = 0; i < size; i++) {
                if(filter != null && !filter.test(read(i))) {
                    continue;
                }
                f.size++;
                f.indexOut.writeInt(indexBuffer.get(i));
            }
            f.indexOut.close();
            try (RandomAccessFile file = new RandomAccessFile(f.indexFile, "r")) {
                f.indexBuffer = file.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, f.indexFile.length()).asIntBuffer();
            }
            f.created = 1;
        } catch(Exception e) {
            f.created = -1;
        } finally {
            IO.close(f.indexOut);
            if(f.created != 1) {
                f.indexFile.delete();
                f = null;
            }
        }
        return f;
    }

    private synchronized void setWritable() {
        if(writable) {
            return;
        }
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(indexFile, "rw");
            indexBuffer = file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, indexFile.length()).asIntBuffer();
            file.close();
            writable = true;
        } catch(Exception e) {
            IO.close(file);
            close();
        }
    }
}