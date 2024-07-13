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

import network.aika.fielddefs.FieldDefinition;
import network.aika.fielddefs.ObjectDefinition;
import network.aika.fields.link.FieldLink;
import network.aika.fields.link.Inputs;
import network.aika.queue.ProcessingPhase;
import network.aika.queue.Queue;
import network.aika.utils.FieldWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Lukas Molzberger
 */
public class Field<O extends FieldObject, I extends Inputs<F>, F extends FieldLink> extends FieldOutputImpl<O> implements FieldInput<I, F>, FieldOutput<O>, FieldWritable {

    private FieldDefinition fieldDefinition;

    private boolean blocked;

    QueueInterceptor interceptor;

    private I inputs;

    public Field(I inputs) {
        this.inputs = inputs;
    }

    @Override
    public I getInputs() {
        return inputs;
    }

    public <F extends Field> F setQueued(Queue q, ProcessingPhase phase, boolean isNextRound) {
        interceptor = new QueueInterceptor(q, this, phase, isNextRound);
        return (F) this;
    }

    public Field setInitialValue(double initialValue) {
        value = initialValue;
        return this;
    }

    public void setFieldDefinition(FieldDefinition fieldDefinition) {
        this.fieldDefinition = fieldDefinition;
    }

    @Override
    public FieldDefinition getFieldDefinition() {
        return fieldDefinition;
    }

    public void setValue(double v) {
        triggerUpdate(v - value);
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public QueueInterceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(QueueInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void receiveUpdate(F fl, double u) {
        if(blocked)
            return;

        if(interceptor != null) {
            interceptor.receiveUpdate(u, false);
            return;
        }

        assert !withinUpdate;
        triggerUpdate(u);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(value);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        value = in.readDouble();
    }
}
