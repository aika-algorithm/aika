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

import network.aika.enums.direction.Direction;
import network.aika.fields.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;
import static network.aika.debugger.properties.FieldOutputProperty.createFieldProperty;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;


/**
 * @author Lukas Molzberger
 */
public class FieldReceiversDialog extends JDialog
        implements ActionListener,
        PropertyChangeListener {

    private PropertiesHolder properties = new PropertiesHolder();

    private Insets insets = new Insets(5, 5, 5, 5);

    private int posCounter = 0;

    public static FieldReceiversDialog currentFieldReceiversDialog;

    public static void showListenerDialog(Frame frame, FieldOutput field) {
        if(currentFieldReceiversDialog != null)
            currentFieldReceiversDialog.dispose();

        currentFieldReceiversDialog = new FieldReceiversDialog(frame, field);
        currentFieldReceiversDialog.setVisible(true);
    }

    //Override addNotify
    public void addNotify(){
        super.addNotify();
        properties.register();
    }

    //Override removeNotify to clean up JNI
    public void removeNotify(){
        super.removeNotify();
        properties.deregister();
    }

    /** Creates the reusable dialog. */
    public FieldReceiversDialog(Frame aFrame, FieldOutput field) {
        super(aFrame, false);
        setLayout(new GridBagLayout());

        setSize(500, 800);

        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setTitle("Field Connections");

        addTitle("Current Field:");
        addConstant("Operator", field.getClass().getSimpleName());
        addField(field);

        if(field instanceof QueueInterceptor) {
            QueueInterceptor qsf = (QueueInterceptor) field;

            addConstant("Step:", qsf.getStep() != null ? qsf.getStep().toShortString() : "--");
        }

        posCounter++;

        if(field instanceof FieldInput) {
            FieldInput fi = (FieldInput) field;

            addTitle("Incoming Fields:");

            Collection<FieldLink> inputs = fi.getInputs();
            if(inputs != null) {
                inputs.stream()
                        .limit(100)
                        .forEach(fl ->
                                addFieldLink(fl, INPUT)
                        );
            }
        }

        addTitle("Outgoing Fields:");

        field.getReceivers()
                .stream()
                .filter(FieldLink.class::isInstance)
                .limit(100)
                .forEach(fl ->
                        addFieldLink((FieldLink) fl, OUTPUT)
                );

        addFinal();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        pack();
        setLocation(
                (Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2,
                (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public void addTitle(String title) {
        new TitleProperty(this, title, new Font("non serif", Font.BOLD, 18))
                .addField(posCounter, insets);

        posCounter++;
    }

    public void addConstant(String labelStr, String valueStr) {
        ConstantProperty property = new ConstantProperty(this, labelStr, valueStr);
        properties.add(property);
        property.addField(posCounter, insets);

        posCounter++;
    }

    public void addField(FieldOutput f) {
        if (f == null)
            return;

        FieldOutputProperty property = createFieldProperty(this, f, true, null);
        properties.add(property);
        property.addField(posCounter, insets);

        posCounter++;
    }

    public void addFieldLink(FieldLink fl, Direction dir) {
        if (fl == null)
            return;

        new FieldLinkProperty(this, fl, dir)
                    .addField(posCounter, insets);

        posCounter++;
    }

    protected void addFinal() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = HORIZONTAL;
        c.anchor = WEST;
        c.weighty = 0.1;
        c.gridx = 0;
        c.gridy = posCounter++;
        add(new JPanel(), c);
    }
}