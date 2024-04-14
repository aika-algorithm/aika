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
package network.aika.elements.typedef.model;


/**
 *
 * @author Lukas Molzberger
 */
public class TypeModel {

    StatesDef states = new StatesDef(this);

    NeuronDef neuron = new NeuronDef(this);

    ConjunctiveDef conjunctiveDef = new ConjunctiveDef(this);

    DisjunctiveDef disjunctiveDef = new DisjunctiveDef(this);

    CategoryDef categoryDef = new CategoryDef(this);
    BindingDef bindingDef = new BindingDef(this);
    PatternDef patternDef = new PatternDef(this);
    InhibitoryDef inhibitoryDef = new InhibitoryDef(this);

    public TypeModel() {
        states.init();
        neuron.init();
        conjunctiveDef.init();
        disjunctiveDef.init();
        bindingDef.init();
        patternDef.init();
        inhibitoryDef.init();
    }


    public StatesDef getStates() {
        return states;
    }

    public NeuronDef getNeuron() {
        return neuron;
    }

    public ConjunctiveDef getConjunctiveDef() {
        return conjunctiveDef;
    }

    public DisjunctiveDef getDisjunctiveDef() {
        return disjunctiveDef;
    }

    public CategoryDef getCategoryDef() {
        return categoryDef;
    }

    public BindingDef getBindingDef() {
        return bindingDef;
    }

    public PatternDef getPatternDef() {
        return patternDef;
    }

    public InhibitoryDef getInhibitoryDef() {
        return inhibitoryDef;
    }
}
