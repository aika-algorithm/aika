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
package network.aika.model;


import network.aika.Config;
import network.aika.fielddefs.Type;
import network.aika.fielddefs.TypeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Lukas Molzberger
 */
public class TypeModel implements TypeRegistry {

    List<Type<?, ?>> types = new ArrayList<>();

    NeuronDef neuron = new NeuronDef(this);

    ConjunctiveDef conjunctive = new ConjunctiveDef(this, neuron);
    DisjunctiveDef disjunctive = new DisjunctiveDef(this, neuron);
    CategoryDef category = new CategoryDef(this, disjunctive);

    BindingDef binding = new BindingDef(this, conjunctive, category);
    PatternDef pattern = new PatternDef(this, conjunctive, category);
    InhibitoryDef inhibitory = new InhibitoryDef(this, disjunctive, category);

    public TypeModel(Config conf) {
        neuron.initNodes();
        conjunctive.initNodes();
        disjunctive.initNodes();
        category.initNodes();
        binding.initNodes();
        pattern.initNodes(conf);
        inhibitory.initNodes();

        neuron.initRelations();
        conjunctive.initRelations();
        disjunctive.initRelations();
        category.initRelations();
        binding.initRelations();
        pattern.initRelations(conf);
        inhibitory.initRelations();
    }

    public NeuronDef getNeuron() {
        return neuron;
    }

    public ConjunctiveDef getConjunctive() {
        return conjunctive;
    }

    public DisjunctiveDef getDisjunctive() {
        return disjunctive;
    }

    public CategoryDef getCategory() {
        return category;
    }

    public BindingDef getBinding() {
        return binding;
    }

    public PatternDef getPattern() {
        return pattern;
    }

    public InhibitoryDef getInhibitory() {
        return inhibitory;
    }

    @Override
    public void register(Type type) {
        types.add(type);
    }

    public String dumpModel() {
        StringBuilder sb = new StringBuilder();
        types.forEach(t -> {
            sb.append(t.getName() + "\n");
            sb.append("  class: " + t.getClazz().getSimpleName() + "\n");
            sb.append("  parents: " + t.getParents().stream()
                    .map(Type::getName)
                    .collect(Collectors.joining(", ")) +
                    "\n"
            );
            t.dumpType(sb);
            t.dumpFields(sb);
            sb.append("\n");
        });

        return sb.toString();
    }
}
