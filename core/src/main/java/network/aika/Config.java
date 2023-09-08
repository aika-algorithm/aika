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
package network.aika;

import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.Neuron;

/**
 *
 * @author Lukas Molzberger
 */
public class Config {
    private Double alpha = null; //0.99;

    private double annealStepSize = 0.05;

    private double learnRate;

    private double learnRateForAbstract;
    private boolean trainingEnabled;
    private boolean countingEnabled;
    private boolean metaInstantiationEnabled;

    private long neuronProviderRetention = 50;

    public double getLearnRate(boolean isAbstract) {
        return isAbstract ? learnRateForAbstract : learnRate;
    }


    public double getLearnRate() {
        return learnRate;
    }

    public Config setLearnRate(double learnRate) {
        this.learnRate = learnRate;
        return this;
    }

    public double getLearnRateForAbstract() {
        return learnRateForAbstract;
    }

    public Config setLearnRateForAbstract(double learnRate) {
        this.learnRateForAbstract = learnRateForAbstract;
        return this;
    }

    public double getAnnealStepSize() {
        return annealStepSize;
    }

    public Config setAnnealStepSize(double annealStepSize) {
        this.annealStepSize = annealStepSize;
        return this;
    }

    public Double getAlpha() {
        return alpha;
    }

    public Config setAlpha(Double alpha) {
        this.alpha = alpha;
        return this;
    }

    public boolean isTrainingEnabled() {
        return trainingEnabled;
    }

    public Config setTrainingEnabled(boolean trainingEnabled) {
        this.trainingEnabled = trainingEnabled;
        return this;
    }

    public Config setCountingEnabled(boolean countingEnabled) {
        this.countingEnabled = countingEnabled;
        return this;
    }

    public boolean isCountingEnabled() {
        return countingEnabled;
    }


    public boolean isMetaInstantiationEnabled() {
        return metaInstantiationEnabled;
    }

    public Config setMetaInstantiationEnabled(boolean metaInstantiationEnabled) {
        this.metaInstantiationEnabled = metaInstantiationEnabled;
        return this;
    }

    public long getNeuronProviderRetention() {
        return neuronProviderRetention;
    }

    public Config setNeuronProviderRetention(long neuronProviderRetention) {
        this.neuronProviderRetention = neuronProviderRetention;
        return this;
    }

    public String toString() {
        return "Alpha: " + alpha + "\n" +
                "LearnRate" + learnRate + "\n\n";
    }
}
