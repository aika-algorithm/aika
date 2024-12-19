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
package network.aika.fields.link;

import java.util.stream.Stream;

/**
 * @author Lukas Molzberger
 */
public interface FieldInputs<F extends FieldLink> {

    void addInput(F fl);

    void removeInput(F fl);

    Stream<F> getInputs();

    int size();

    default void connectInputs(boolean initialize) {
        getInputs().forEach(fl ->
                fl.connect(initialize)
        );
    }

    default void disconnectInputs(boolean deinitialize) {
        getInputs().forEach(fl ->
                fl.disconnect(deinitialize)
        );
    }

    default void disconnectAndUnlinkInputs(boolean deinitialize) {
        getInputs().forEach(fl -> {
            fl.disconnect(deinitialize);
            fl.unlinkInput();
        });
    }

    default void unlinkInputs() {
        getInputs().forEach(fl ->
                fl.unlinkInput()
        );
    }
}
