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
package network.aika.fields;

import network.aika.fields.link.AbstractFieldLink;
import network.aika.fields.link.ListenerFieldLink;

import java.util.Collection;

/**
 * @author Lukas Molzberger
 */
public interface FieldOutput {

    String getLabel();

    String getValueString();

    double getValue();

    double getUpdatedValue();

    void addOutput(AbstractFieldLink fl);

    void removeOutput(AbstractFieldLink fl);

    Collection<AbstractFieldLink> getReceivers();

    FieldObject getReference();

    void disconnectAndUnlinkOutputs(boolean deinitialize);

    boolean isWithinUpdate();

    default FieldOutput addListener(String listenerName, ReferencedUpdateListener fieldListener) {
        return addListener(listenerName, fieldListener, false);
    }

    default FieldOutput addListener(String listenerName, ReferencedUpdateListener fieldListener, boolean assumeInitialized) {
        ListenerFieldLink fl = new ListenerFieldLink(this, listenerName, fieldListener);
        addOutput(fl);
        if(!assumeInitialized)
            fl.connect();

        return this;
    }

    default boolean exceedsThreshold() {
        return getUpdatedValue() > 0.0;
    }
}
