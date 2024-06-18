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
package network.aika.elements.typedef;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.State;
import network.aika.elements.activations.StateType;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fielddefs.FieldObjectDefinition;
import network.aika.fields.SumField;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Lukas Molzberger
 */
public class StateTypeDefinition extends TypeDefinition<StateTypeDefinition, State> {

    private StateType type;

    private boolean isNextRound;

    private ActivationTypeDefinition activationType;


    public StateTypeDefinition(String name, StateType type) {
        super(name, State.class);
        this.type = type;
    }

    public StateType getType() {
        return type;
    }

    public StateTypeDefinition setActivationType(ActivationTypeDefinition activationType) {
        this.activationType = activationType;
        return this;
    }

    public ActivationTypeDefinition getActivationType() {
        return activationType;
    }

    public StateTypeDefinition setNextRound(boolean nextRound) {
        isNextRound = nextRound;

        return this;
    }

    public boolean isNextRound() {
        return isNextRound;
    }

    public State instantiate(Activation act) {
        try {
            State instance = clazz
                    .getConstructor(Activation.class)
                    .newInstance(act);

            instance.setTypeDefinition(this);
            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
