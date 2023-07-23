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

import network.aika.fields.UpdateListener;
import network.aika.fields.AbstractFieldLink;
import network.aika.fields.FieldOutput;
import network.aika.fields.QueueField;
import network.aika.fields.ListenerFieldLink;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.Format;
import java.text.NumberFormat;


/**
 * @author Lukas Molzberger
 */
public class FieldOutputProperty<F extends FieldOutput> extends AbstractProperty implements UpdateListener<ListenerFieldLink> {

    protected F field;
    protected AbstractFieldLink listenerLink;

    private boolean showReference;

    protected Boolean isConnected;
    protected Boolean isPropagateUpdate;

    protected boolean withinUpdate;

    protected JLabel fieldLabel;
    protected JFormattedTextField currentValueField;

    public static FieldOutputProperty createFieldProperty(Container parent, FieldOutput f, boolean showReference, Boolean isConnected, Boolean isPropagateUpdates) {
        if(f instanceof QueueField) {
            return new QueueFieldProperty(parent, (QueueField) f, showReference, isConnected, isPropagateUpdates);
        } else {
            return new FieldOutputProperty(parent, f, showReference, isConnected, isPropagateUpdates);
        }
    }

    public FieldOutputProperty(Container parent, F f, boolean showReference, Boolean isConnected, Boolean isPropagateUpdate) {
        super(parent);
        this.showReference = showReference;
        this.isConnected = isConnected;
        this.isPropagateUpdate = isPropagateUpdate;

        Frame frame = (Frame) SwingUtilities.getWindowAncestor(parent);
        field = f;

        fieldLabel = new JLabel(f.getLabel());

        Format fieldFormatter = NumberFormat.getNumberInstance();
        currentValueField = new JFormattedTextField(fieldFormatter);
        fieldLabel.setLabelFor(currentValueField);

        setCurrentValue(f);

        currentValueField.setColumns(10);

        addMouseListener(frame, fieldLabel);

        registerListener();

        currentValueField.setEnabled(true);
    }

    @Override
    public void registerListener() {
        if(listenerLink == null) {
            listenerLink = new ListenerFieldLink(field, "fieldPropertyListener " + (field != null ? "(" + field.getLabel() + ")" : ""), this);
            field.addOutput(listenerLink);
            listenerLink.connect(true);
        }
    }

    @Override
    public void deregisterListener() {
        if(listenerLink != null) {
            field.removeOutput(listenerLink);
            listenerLink = null;
        }
    }

    @Override
    public void receiveUpdate(ListenerFieldLink fl, boolean nextRound, double u) {
        withinUpdate = true;
        currentValueField.setValue(field.getValue());
        withinUpdate = false;
    }

    public void addField(int pos, Insets insets) {
        addGridEntry(fieldLabel, 0, pos, 1, insets);
        addGridEntry(currentValueField, 1, pos, 1, insets);

        showReference(2, pos, insets);
        if(isConnected != null || isPropagateUpdate != null)
            showConnected(3, pos, insets);
    }

    protected void showReference(int xPos, int yPos, Insets insets) {
        if(showReference) {
            Object outRef = field.getReference();
            String outRefStr = outRef != null ? "" + outRef : "";
            if(outRefStr.length() > 80) {
                outRefStr = outRefStr.substring(0, 37) + "...";
            }
            JLabel jOutRef = new JLabel(outRefStr);
            addGridEntry(jOutRef, xPos, yPos, 1, insets);
        }
    }

    protected void showConnected(int xPos, int yPos, Insets insets) {
        String label = "";

        if(isConnected != null)
            label += isConnected ? "connected " : "unconnected ";

        if(isPropagateUpdate != null)
            label += isPropagateUpdate ? "prop" : "no prop";

        JLabel jOutRef = new JLabel(label);
        addGridEntry(jOutRef, xPos, yPos, 1, insets);
    }

    private void addMouseListener(Frame frame, JComponent c) {
        c.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                FieldReceiversDialog.showListenerDialog(frame, field);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    private void setCurrentValue(F f) {
        currentValueField.setValue(f.getValue());
    }

    public JLabel getLabel() {
        return fieldLabel;
    }

    public String toString() {
        return getClass().getSimpleName() + " " + parent.getName() + " " + fieldLabel.getText();
    }
}
