package network.aika.storage;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import network.aika.SuspensionHook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Lukas Molzberger
 */
public class FSSuspensionCallbackImpl implements SuspensionHook {

    public static String MODEL = "model";
    public static String INDEX = "index";

    private AtomicInteger currentId = new AtomicInteger(0);

    private Map<String, Integer> labels = Collections.synchronizedMap(new HashMap<>());
    private Map<Integer, long[]> index = Collections.synchronizedMap(new TreeMap<>());

    private Path path;
    private String modelLabel;

    private RandomAccessFile dataStore;


    public void open(Path path, String modelLabel, boolean create) throws IOException {
        this.path = path;
        this.modelLabel = modelLabel;
        if(create) {
            Files.createDirectories(path);
            File modelFile = getFile(MODEL);
            if(modelFile.exists())
                modelFile.delete();

            File indexFile = getFile(INDEX);
            if(indexFile.exists())
                indexFile.delete();

        } else {
            loadIndex();
        }
        dataStore = new RandomAccessFile(getFile(MODEL), "rw");
    }

    public void close() throws IOException {
        dataStore.close();
    }

    @Override
    public Integer getIdByLabel(String label) {
        return labels.get(label);
    }

    @Override
    public void putLabel(String label, Integer id) {
        labels.put(label, id);
    }

    @Override
    public void removeLabel(String label) {
        if (label == null)
            return;

        labels.remove(label);
    }

    @Override
    public int getNewId() {
        return currentId.addAndGet(1);
    }

    @Override
    public synchronized void store(Integer id, byte[] data) throws IOException {
        dataStore.seek(dataStore.length());

        index.put(id, new long[]{dataStore.getFilePointer(), data.length});
        dataStore.write(data);
    }

    @Override
    public synchronized byte[] retrieve(Integer id) throws IOException {
        long[] pos = index.get(id);
        if(pos == null)
            throw new MissingNodeException(String.format("Neuron with id %d is missing in model label %s", id, modelLabel));

        byte[] data = new byte[(int)pos[1]];

        dataStore.seek(pos[0]);
        dataStore.read(data);

        return data;
    }

    @Override
    public synchronized void remove(Integer id) {
        index.remove(id);
    }

    @Override
    public Iterable<Integer> getAllNodeIds() {
        return index.keySet();
    }

    @Override
    public void loadIndex() {
        try (FileInputStream fis = new FileInputStream(getFile(INDEX));
             ByteArrayInputStream bais = new ByteArrayInputStream(fis.readAllBytes());
             DataInputStream dis = new DataInputStream(bais)) {
            readIndex(dis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void storeIndex() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (DataOutputStream dos = new DataOutputStream(baos);
             FileOutputStream fos = new FileOutputStream(getFile(INDEX))) {
            writeIndex(dos);
            fos.write(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getFile(String prefix) {
        return new File(path.toFile(), prefix + "-" + modelLabel + ".dat");
    }

    private void readIndex(DataInput in) throws IOException {
        currentId = new AtomicInteger((int) in.readLong());

        labels.clear();
        while(in.readBoolean()) {
            String l = in.readUTF();
            Integer id = (int)in.readLong();
            labels.put(l, id);
        }

        index.clear();
        while(in.readBoolean()) {
            Integer id = (int)in.readLong();
            long[] pos = new long[2];
            pos[0] = in.readLong();
            pos[1] = in.readInt();

            index.put(id, pos);
        }
    }

    private void writeIndex(DataOutput out) throws IOException {
        out.writeLong(currentId.get());

        for(Map.Entry<String, Integer> me: labels.entrySet()) {
            out.writeBoolean(true);
            out.writeUTF(me.getKey());
            out.writeLong(me.getValue());
        }
        out.writeBoolean(false);

        for(Map.Entry<Integer, long[]> me: index.entrySet()) {
            out.writeBoolean(true);
            out.writeLong(me.getKey());
            out.writeLong(me.getValue()[0]);
            out.writeInt((int)me.getValue()[1]);
        }
        out.writeBoolean(false);
    }
}