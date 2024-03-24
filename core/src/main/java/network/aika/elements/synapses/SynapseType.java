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
package network.aika.elements.synapses;

import network.aika.elements.NeuronType;
import network.aika.elements.activations.StateType;
import network.aika.enums.Trigger;
import network.aika.enums.Transition;
import network.aika.enums.direction.DirectionEnum;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 *
 * @author Lukas Molzberger
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SynapseType {

    NeuronType inputType();

    NeuronType outputType();

    Transition[] transition();

    Transition required();

    Trigger trigger() default Trigger.FIRED_INNER_FEEDBACK;

    StateType outputState() default StateType.PRE_FEEDBACK;

    boolean propagateRange() default true;

    DirectionEnum storedAt();

    boolean trainingAllowed() default true;
}
