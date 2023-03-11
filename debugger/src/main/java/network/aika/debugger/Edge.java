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
package network.aika.debugger;

import network.aika.elements.Element;

/**
 * @author Lukas Molzberger
 */
public abstract class Edge<E extends Element> implements GraphicElement<E> {

    protected E edge;


/*
    protected static Map<Class<? extends Synapse>, String> synapseTypeModifiers = new HashMap<>();


    static {
        synapseTypeModifiers.put(InputPatternSynapse.class, "fill-color: rgb(0,150,00);");
        synapseTypeModifiers.put(RelationInputSynapse.class, "fill-color: rgb(50,230,50);");
        synapseTypeModifiers.put(NegativeFeedbackSynapse.class, "fill-color: rgb(185,0,0);");
        synapseTypeModifiers.put(SamePatternSynapse.class, "fill-color: rgb(50,200,120);");
        synapseTypeModifiers.put(PositiveFeedbackSynapse.class, "fill-color: rgb(120,200,50); ");
        synapseTypeModifiers.put(ReversePatternSynapse.class, "fill-color: rgb(120,200,50); ");

        synapseTypeModifiers.put(InhibitorySynapse.class, "fill-color: rgb(100,100,255);");

        synapseTypeModifiers.put(PatternCategorySynapse.class, "fill-color: rgb(100,0,200);");
        synapseTypeModifiers.put(BindingCategorySynapse.class, "fill-color: rgb(110,0,220);");
        synapseTypeModifiers.put(InhibitoryCategorySynapse.class, "fill-color: rgb(110,0,220);");

        synapseTypeModifiers.put(PatternSynapse.class, "fill-color: rgb(224, 34, 245);");

        synapseTypeModifiers.put(PatternCategoryInputSynapse.class, "fill-color: rgb(110,200,220); ");
        synapseTypeModifiers.put(BindingCategoryInputSynapse.class, "fill-color: rgb(110,200,220); ");
        synapseTypeModifiers.put(InhibitoryCategoryInputSynapse.class, "fill-color: rgb(110,200,220); ");
    }
*/

    public Edge(E edge) {
        this.edge = edge;
    }

    public E getElement() {
        return edge;
    }
}
