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
package network.aika.fields.field;

import network.aika.fields.defs.FieldDefinition;
import network.aika.type.Obj;
import network.aika.queue.ProcessingPhase;
import network.aika.queue.Queue;
import network.aika.type.Type;
import network.aika.utils.FieldWritable;
import network.aika.utils.StringUtils;
import network.aika.utils.ToleranceUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Lukas Molzberger
 */
public class Field implements FieldInput, FieldOutput, FieldWritable {

    private final FieldDefinition fieldDefinition;

    private final short id;
    private final Obj object;

    protected double value;

    private double updatedValue;

    protected boolean withinUpdate;

    QueueInterceptor interceptor;

    public static boolean isTrue(FieldOutput f) {
        if(f == null)
            return false;

        return isTrue(f.getValue());
    }

    public static boolean isTrue(FieldOutput f, boolean updatedValue) {
        if(f == null)
            return false;

        return isTrue(
                updatedValue ?
                        f.getUpdatedValue() :
                        f.getValue()
        );
    }

    private static boolean isTrue(double v) {
        return v > 0.0;
    }


    public Field(Obj obj, FieldDefinition fd, short id) {
        this.object = obj;
        this.fieldDefinition = fd;
        this.id = id;
    }

    public short getId() {
        return id;
    }

    @Override
    public boolean isWithinUpdate() {
        return withinUpdate;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public double getUpdatedValue() {
        return withinUpdate ?
                updatedValue :
                value;
    }

    @Override
    public Obj getObject() {
        return object;
    }

    public Field setQueued(Queue q, ProcessingPhase phase, boolean isNextRound) {
        interceptor = new QueueInterceptor(q, this, phase, isNextRound);
        return this;
    }

    @Override
    public FieldDefinition getFieldDefinition() {
        return fieldDefinition;
    }

    protected Double getTolerance() {
        return fieldDefinition.getTolerance();
    }

    @Override
    public String getName() {
        return fieldDefinition.getName();
    }

    public QueueInterceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(QueueInterceptor interceptor) {
        this.interceptor = interceptor;
    }


    public void setValue(double v) {
        withinUpdate = true;
        updatedValue = v;
        propagateUpdate();
    }

    public void triggerUpdate(double u) {
        if(ToleranceUtils.belowTolerance(getTolerance(), u))
            return;

        withinUpdate = true;
        updatedValue = value + u;
/*        if(updatedValue > -MIN_TOLERANCE && updatedValue < MIN_TOLERANCE) {
            updatedValue = 0.0; // TODO: Find a better solution to this hack
        }
*/
        propagateUpdate();
    }

    private void propagateUpdate() {
        object
                .getType()
                .getFlattenedTypeOutputSide()
                .followLinks(this);

        value = updatedValue;
        withinUpdate = false;
    }

    public double getUpdate() {
        return updatedValue - value;
    }

    @Override
    public void receiveUpdate(double u) {
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

    @Override
    public String toString() {
        return getName() + ": " + getValueString();
    }

    public String getValueString() {
        return StringUtils.doubleToString(getValue());
    }
}
