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

/**
 *
 * The suspension hook is used to suspend neurons to an external storage in order to reduce the memory footprint.
 *
 * !!! Important: When using the suspension hook, all references to a neuron need to occur through a
 * provider. Otherwise, the reference might be outdated.
 *
 * @author Lukas Molzberger
 */
public interface SuspensionCallback {

    void prepareNewModel() throws IOException;

    void open() throws IOException;

    void close() throws IOException;

    long createId();

    long getCurrentId();

    void store(Long id, byte[] data) throws IOException;

    void remove(Long id) throws IOException;

    byte[] retrieve(Long id) throws IOException;

    Long getIdByTokenId(int tokenId);

    void putTokenId(int tokenId, Long id);

    void removeTokenId(int tokenId);

    void loadIndex(Model m);

    void saveIndex(Model m) throws IOException;
}
