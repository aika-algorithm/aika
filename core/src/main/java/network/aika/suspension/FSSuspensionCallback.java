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
package network.aika.suspension;

import network.aika.Model;
import network.aika.exceptions.MissingNeuronException;
import network.aika.suspension.SuspensionCallback;
import network.aika.utils.Writable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Lukas Molzberger
 */
public class FSSuspensionCallback implements SuspensionCallback {

    public static String MODEL = "model";
    public static String INDEX = "index";

    private AtomicLong currentId = new AtomicLong(0);

    private Map<LabelKey, Long> labels = Collections.synchronizedMap(new TreeMap<>());
    private Map<Long, long[]> index = Collections.synchronizedMap(new TreeMap<>());

    private Path path;
    private String modelLabel;

    private RandomAccessFile modelStore;

    public FSSuspensionCallback(Path path, String modelLabel) {
        this.path = path;
        this.modelLabel = modelLabel;
    }

    public void prepareNewModel() throws IOException {
        Files.createDirectories(path);
        File modelFile = getFile(MODEL);
        if(modelFile.exists())
            modelFile.delete();

        File indexFile = getFile(INDEX);
        if(indexFile.exists())
            indexFile.delete();
    }

    public void open() throws IOException {
        modelStore = new RandomAccessFile(getFile(MODEL), "rw");
    }

    public void close() throws IOException {
        modelStore.close();
    }

    @Override
    public Long getIdByLabel(String label, Long templateId) {
        return labels.get(new LabelKey(label, templateId));
    }

    @Override
    public void putLabel(String label, Long templateId, Long id) {
        labels.put(new LabelKey(label, templateId), id);
    }

    @Override
    public void removeLabel(String label, Long templateId) {
        if (label == null)
            return;

        labels.remove(new LabelKey(label, templateId));
    }

    @Override
    public long createId() {
        return currentId.addAndGet(1);
    }

    @Override
    public long getCurrentId() {
        return currentId.get();
    }

    @Override
    public synchronized void store(Long id, String label, Writable customData, byte[] data) throws IOException {
        modelStore.seek(modelStore.length());

        index.put(id, new long[]{modelStore.getFilePointer(), data.length});
        modelStore.write(data);
    }

    @Override
    public synchronized byte[] retrieve(Long id) throws IOException {
        long[] pos = index.get(id);
        if(pos == null)
            throw new MissingNeuronException(id, modelLabel);

        byte[] data = new byte[(int)pos[1]];

        modelStore.seek(pos[0]);
        modelStore.read(data);

        return data;
    }

    @Override
    public synchronized void remove(Long id) {
        index.remove(id);
    }

    @Override
    public Collection<Long> getAllIds() {
        return index.keySet();
    }

    @Override
    public void loadIndex(Model m) {
        try (FileInputStream fis = new FileInputStream(getFile(INDEX));
             ByteArrayInputStream bais = new ByteArrayInputStream(fis.readAllBytes());
             DataInputStream dis = new DataInputStream(bais)) {
            m.readFields(dis, m);
            readIndex(dis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveIndex(Model m) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (DataOutputStream dos = new DataOutputStream(baos);
             FileOutputStream fos = new FileOutputStream(getFile(INDEX))) {
            m.write(dos);
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
        currentId = new AtomicLong(in.readLong());

        labels.clear();
        while(in.readBoolean()) {
            String l = in.readUTF();
            Long templateId = in.readLong();
            Long id = in.readLong();
            labels.put(new LabelKey(l, templateId), id);
        }

        index.clear();
        while(in.readBoolean()) {
            Long id = in.readLong();
            long[] pos = new long[2];
            pos[0] = in.readLong();
            pos[1] = in.readInt();

            index.put(id, pos);
        }
    }

    private void writeIndex(DataOutput out) throws IOException {
        out.writeLong(currentId.get());

        for(Map.Entry<LabelKey, Long> me: labels.entrySet()) {
            out.writeBoolean(true);
            out.writeUTF(me.getKey().getLabel());
            out.writeLong(me.getKey().getTemplateId());
            out.writeLong(me.getValue());
        }
        out.writeBoolean(false);

        for(Map.Entry<Long, long[]> me: index.entrySet()) {
            out.writeBoolean(true);
            out.writeLong(me.getKey());
            out.writeLong(me.getValue()[0]);
            out.writeInt((int)me.getValue()[1]);
        }
        out.writeBoolean(false);
    }
}
