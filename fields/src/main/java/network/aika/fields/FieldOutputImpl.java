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
import network.aika.utils.StringUtils;
import network.aika.utils.ToleranceUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Lukas Molzberger
 */
public abstract class FieldOutputImpl implements FieldOutput {

    private static double MIN_TOLERANCE = 0.0000000001;

    private String label;
    private FieldObject reference;

    protected double value;

    private double updatedValue;

    protected boolean withinUpdate;

    protected Double tolerance;


    private Collection<AbstractFieldLink> receivers;

    public FieldOutputImpl(FieldObject reference, String label, Double tolerance) {
        this.reference = reference;
        this.label = label;

        initIO();
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public FieldObject getReference() {
        return reference;
    }

    public void setReference(FieldObject reference) {
        this.reference = reference;
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

    protected void initIO() {
        receivers = new ArrayList<>();
    }

    public void triggerUpdate(double u) {
        if(ToleranceUtils.belowTolerance(tolerance, u))
            return;

        withinUpdate = true;

        updatedValue = value + u;
        if(updatedValue > -MIN_TOLERANCE && updatedValue < MIN_TOLERANCE) {
            updatedValue = 0.0; // TODO: Find a better solution to this hack
        }

        propagateUpdate(u);
        value = updatedValue;

        withinUpdate = false;
    }

    public void disconnectAndUnlinkOutputs(boolean deinitialize) {
        synchronized (this.receivers) {
            receivers.forEach(fl -> {
                fl.disconnect(deinitialize);
                fl.unlinkOutput();
            });
        }
    }

    public Collection<AbstractFieldLink> getReceivers() {
        return receivers;
    }

    @Override
    public void addOutput(AbstractFieldLink fl) {
        synchronized (this.receivers) {
            this.receivers.add(fl);
        }
    }

    @Override
    public void removeOutput(AbstractFieldLink fl) {
        synchronized (this.receivers) {
            this.receivers.remove(fl);
        }
    }

    protected void propagateUpdate(double update) {
        AbstractFieldLink[] recs;

        synchronized (this.receivers) {
            recs = receivers.toArray(new AbstractFieldLink[0]);
        }

        for(int i = 0; i < recs.length; i++) {
            recs[i].receiveUpdate(update);
        }
    }

    @Override
    public String toString() {
        return getLabel() + ": " + getValueString();
    }

    public String getValueString() {
        return StringUtils.doubleToString(getValue());
    }
}
