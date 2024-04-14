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
package network.aika.elements.neurons.types;

/**
 *
 * @author Lukas Molzberger
 */
/*
public class PatternNeuron extends ConjunctiveNeuron {



    public PatternNeuron(NeuronProvider np) {
        super(np);

    }

    public PatternNeuron(Model m, RefType rt) {
        super(m, rt);

    }



    public static PatternNeuron create(Model m, String label) {
        return new PatternNeuron(m, NEURON_EXTERNAL)
                .setLabel(label)
                .setPersistent(true);
    }

    @Override
    public Neuron createCategoryNeuron() {
        return new PatternCategoryNeuron(getModel(), CATEGORY)
                .setLabel(getCategoryLabel(getLabel()));
    }

    @Override
    public PatternCategoryInputSynapse createCategoryInputSynapse() {
        return new PatternCategoryInputSynapse();
    }

    public NeuronStatistic getStatistic() {
        return statistic;
    }


    public static String getCategoryLabel(String placeholder) {
        return placeholder + CATEGORY_LABEL;
    }

    @Override
    public CategorySynapse createCategorySynapse() {
        return new PatternCategorySynapse();
    }

    @Override
    public PatternCategoryInputSynapse getCategoryInputSynapse() {
        return getInputSynapseByType(PatternCategoryInputSynapse.class);
    }

    @Override
    public PatternCategorySynapse getCategoryOutputSynapse() {
        return getOutputSynapseByType(PatternCategorySynapse.class);
    }

    @Override
    public void count(PatternActivation act) {
        Range absRange = act.getAbsoluteCharRange();
        statistic.count(absRange);

        setModified();

        PatternNeuron tn = getTemplate();
        if(tn != null)
            tn.getAverageCoveredSpace()
                    .count(absRange);
    }

    public void setFrequency(double f) {
        statistic.setFrequency(f);
        setModified();
    }
}
*/