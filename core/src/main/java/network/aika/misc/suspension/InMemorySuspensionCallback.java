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
package network.aika.misc.suspension;

import network.aika.Model;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Lukas Molzberger
 */
public class InMemorySuspensionCallback implements SuspensionCallback {

    private final AtomicInteger currentId = new AtomicInteger(0);

    private final Map<Long, byte[]> storage = new TreeMap<>();
    private final Map<Integer, Long> tokenIds = new TreeMap<>();

    @Override
    public void prepareNewModel() {

    }

    @Override
    public void open() throws IOException {

    }

    @Override
    public void close() {

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
    public void store(Long id, byte[] data) {
        storage.put(id, data);
    }

    @Override
    public void remove(Long id) {
        storage.remove(id);
    }

    @Override
    public byte[] retrieve(Long id) {
        return storage.get(id);
    }

    @Override
    public Long getIdByTokenId(int tokenId) {
        return tokenIds.get(tokenId);
    }

    @Override
    public void putTokenId(int tokenId, Long id) {
        tokenIds.put(tokenId, id);
    }

    @Override
    public void removeTokenId(int tokenId) {
        tokenIds.remove(tokenId);
    }

    @Override
    public void loadIndex(Model m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveIndex(Model m) {
        throw new UnsupportedOperationException();
    }
}
