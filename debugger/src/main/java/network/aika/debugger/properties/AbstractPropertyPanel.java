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

import network.aika.elements.activations.Activation;
import network.aika.fields.FieldOutput;

import javax.swing.*;
import java.awt.*;

import static java.awt.GridBagConstraints.*;
import static network.aika.debugger.AbstractConsole.NOT_SET_STR;
import static network.aika.debugger.properties.FieldOutputProperty.createFieldProperty;

/**
 * @author Lukas Molzberger
 */
public class AbstractPropertyPanel extends JPanel {
    public static final int TITLE_SIZE = 16;
    public static final int SUB_TITLE_SIZE = 13;

    private int posCounter = 0;
    private Insets insets = new Insets(2, 4, 2, 4);

    private PropertiesHolder properties = new PropertiesHolder();

    public AbstractPropertyPanel() {
        super(new GridBagLayout());
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

    public void addTitle(String title) {
        addTitle(title, TITLE_SIZE);
    }

    public void addTitle(String title, int size) {
        new TitleProperty(this, title, new Font("non serif", Font.BOLD, size))
                .addField(posCounter, insets);

        posCounter++;
    }

    protected void addEntry(AbstractPropertyPanel bsEntry) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = HORIZONTAL;
        c.anchor = WEST;
        c.weightx = 0.1;
        c.gridx = 0;
        c.gridy = posCounter;
        c.insets = insets;
        add(bsEntry, c);
        posCounter++;
    }

    protected void addSeparator() {
        JSeparator s = new JSeparator();
        // set layout as vertical
        s.setOrientation(SwingConstants.HORIZONTAL);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = HORIZONTAL;
        c.anchor = WEST;
        c.weighty = 0.1;
        c.gridx = 0;
        c.gridy = posCounter;
        add(s, c);

        posCounter++;
    }

    public void addFinal() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = BOTH;
        c.anchor = WEST;
        c.weighty = 0.1;
        c.gridx = 0;
        c.gridy = posCounter;
        add(new JPanel(), c);
        posCounter++;
    }

    public void addConstant(String label, String value) {
        ConstantProperty property = new ConstantProperty(this, label, value);
        property.addField(posCounter, insets);

        properties.add(property);

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

    public static String getShortString(Activation act) {
        return  act != null ? act.toString() : NOT_SET_STR;
    }
}
