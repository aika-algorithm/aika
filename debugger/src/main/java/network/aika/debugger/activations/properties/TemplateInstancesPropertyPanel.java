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
package network.aika.debugger.activations.properties;

import network.aika.debugger.activations.properties.activations.ActivationPropertyPanel;
import network.aika.debugger.properties.AbstractPropertyPanel;
import network.aika.elements.activations.Activation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static network.aika.elements.activations.StateType.INNER_FEEDBACK;


/**
 * @author Lukas Molzberger
 */
public class TemplateInstancesPropertyPanel extends AbstractPropertyPanel {

    public TemplateInstancesPropertyPanel(Stream<? extends Activation> instances) {
        List<? extends Activation> sortedInstances = instances.collect(Collectors.toList());

        Collections.sort(sortedInstances, Comparator.comparingDouble(inst -> -inst.getNet(INNER_FEEDBACK).getValue()));
        sortedInstances.stream()
                .limit(10)
                .forEach(inst -> {
                    addEntry(ActivationPropertyPanel.create(inst));
                    addSeparator();
                }
        );

        addFinal();
    }

    public static TemplateInstancesPropertyPanel create(Activation act) {
        return new TemplateInstancesPropertyPanel(act.getTemplateInstances());
    }
}