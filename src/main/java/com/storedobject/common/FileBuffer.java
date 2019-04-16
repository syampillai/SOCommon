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

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileBuffer {

    private File dataFile, indexFile;
    private ByteBuffer dataBuffer;
    private IntBuffer indexBuffer;
    private BufferedOutputStream dataOut;
    private DataOutputStream indexOut;
    private int size = 0, dataSize = 0;
    private byte created = -1;
    private boolean writable = false;
    private FileBuffer parent = null, child = null;

    public void begin() {
        if(created > -1) {
            return;
        }
        try {
            dataFile = File.createTempFile("SOBuffer", "data");
            dataFile.deleteOnExit();
            dataOut = IO.getOutput(dataFile);
            indexFile = File.createTempFile("SOBuffer", "index");
            indexFile.deleteOnExit();
            indexOut = IO.getDataOutput(indexFile);
            created = 0;
        } catch(Exception e) {
            close();
        }
    }

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

    public void close() {
        size = dataSize = 0;
        dataBuffer = null;
        indexBuffer = null;
        if(dataOut != null) {
            try {
                dataOut.close();
            } catch(IOException ignored) {
            }
        }
        dataOut = null;
        if(indexOut != null) {
            try {
                indexOut.close();
            } catch(IOException ignored) {
            }
        }
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

    public int size() {
        return size;
    }

    @Override
    protected void finalize() {
        close();
    }

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
            try {
                f.indexOut.close();
            } catch (IOException ignored) {
            }
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
            if(file != null) {
                try {
                    file.close();
                } catch(IOException ignore) {
                }
            }
            close();
        }
    }
}