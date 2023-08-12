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
package network.aika.debugger.properties;

import network.aika.debugger.FieldObserver;
import network.aika.fields.Field;
import network.aika.fields.QueueInterceptor;

import javax.swing.*;
import java.awt.*;

/**
 * @author Lukas Molzberger
 */
public class QueueFieldProperty extends FieldOutputProperty<Field> implements FieldObserver {

    public QueueFieldProperty(Container parent, Field field, boolean showReference, Boolean isConnected, Boolean isPropagateUpdates) {
        super(parent, field, showReference, isConnected, isPropagateUpdates);

        currentValueField.addPropertyChangeListener("value", e -> {
            if(withinUpdate)
                return;

            Number v = (Number) currentValueField.getValue();
            if(v == null)
                return;

            field.setValue(v.doubleValue());
        });

        currentValueField.setEnabled(true);
    }

    @Override
    public void registerListener() {
        ((QueueInterceptor)field.getInterceptor()).addObserver(this);
    }

    @Override
    public void deregisterListener() {
        ((QueueInterceptor)field.getInterceptor()).removeObserver(this);
    }

    @Override
    public void receiveUpdate(double v) {
        SwingUtilities.invokeLater(() -> {
            withinUpdate = true;
            currentValueField.setValue(v);
            withinUpdate = false;
        });
    }
}
