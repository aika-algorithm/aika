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

import network.aika.elements.neurons.*;
import network.aika.elements.neurons.relations.LatentRelationNeuron;
import network.aika.elements.neurons.relations.BeforeRelationNeuron;
import network.aika.elements.synapses.InputObjectSynapse;
import network.aika.elements.synapses.RelationInputSynapse;
import network.aika.elements.synapses.SameObjectSynapse;
import network.aika.meta.Dictionary;

import static network.aika.TestUtils.initPatternLoop;
import static network.aika.meta.NetworkMotifs.addOuterInhibitoryLoop;


/**
 *
 * @author Lukas Molzberger
 */
public class TestHelper {

    public static void initPatternTheCat(Model m, Dictionary dict, OuterInhibitoryNeuron inhibNThe, OuterInhibitoryNeuron inhibNCat, int variant) {
        PatternNeuron theIN = dict.lookupInputToken("the");
        PatternNeuron catIN = dict.lookupInputToken("cat");

        int relFrom = variant < 2 ? -5 : 1;
        int relTo = variant < 2 ? -1 : 5;

        LatentRelationNeuron relPT = BeforeRelationNeuron.createBeforeRelationNeuron(m, relFrom, relTo, "relPT");

        BindingNeuron theBN = new BindingNeuron(m).setLabel("the (the cat)");
        new InputObjectSynapse()
                .setWeight(10.0)
                .link(theIN, theBN)
                .adjustBias();

        BindingNeuron catBN = new BindingNeuron(m).setLabel("cat (the cat)");
        new InputObjectSynapse()
                .setWeight(variant == 0  || variant == 2 ? 10.0 : 5.0)
                .link(catIN, catBN)
                .adjustBias();

        if(variant < 2) {
            new RelationInputSynapse()
                    .setWeight(5.0)
                    .link(relPT, catBN)
                    .adjustBias();
            new SameObjectSynapse()
                    .setWeight(variant == 1 || variant == 3 ? 10.0 : 5.0)
                    .link(theBN, catBN)
                    .adjustBias();
        } else {
            new RelationInputSynapse()
                    .setWeight(5.0)
                    .link(relPT, theBN)
                    .adjustBias();
            new SameObjectSynapse()
                    .setWeight(variant == 1 || variant == 3 ? 10.0 : 5.0)
                    .link(catBN, theBN)
                    .adjustBias();
        }

        PatternNeuron theCatP = initPatternLoop(m, "the cat", theBN, catBN);

        //addInhibitoryLoop(inhibNThe, false, theBN);
        //addInhibitoryLoop(new InhibitoryNeuron().init(m, "I-the (tc)"), true, theBN);

        theCatP.setBias(3.0);

        theBN.setBias(3.0);
        catBN.setBias(3.0);
    }

    public static void initPatternBlackCat(Model m, Dictionary dict) {
        PatternNeuron blackIN = dict.lookupInputToken("black");
        PatternNeuron catIN = dict.lookupInputToken("cat");

        LatentRelationNeuron relPT = BeforeRelationNeuron.createBeforeRelationNeuron(m, -1, -1, "relPT");

        BindingNeuron blackBN = new BindingNeuron(m).setLabel("black (black cat)");
        new InputObjectSynapse()
                .setWeight(10.0)
                .link(blackIN, blackBN)
                .adjustBias();

        BindingNeuron catBN = new BindingNeuron(m).setLabel("cat (black cat)");
        new InputObjectSynapse()
                .setWeight(20.0)
                .link(catIN, catBN)
                .adjustBias();

        new RelationInputSynapse()
                .setWeight(5.0)
                .link(relPT, catBN)
                .adjustBias();

        new SameObjectSynapse()
                .setWeight(5.0)
                .link(blackBN, catBN)
                .adjustBias();

        PatternNeuron blackCat = initPatternLoop(m, "black cat", blackBN, catBN);
        blackCat.setBias(3.0);

        blackBN.setBias(3.0);
        catBN.setBias(3.0);
    }

    public static void initPatternTheDog(Model m, Dictionary dict, OuterInhibitoryNeuron inhibNThe, OuterInhibitoryNeuron inhibNDog, int variant) {
        PatternNeuron theIN = dict.lookupInputToken("the");
        PatternNeuron dogIN = dict.lookupInputToken("dog");

        int relFrom = variant < 2 ? -5 : 1;
        int relTo = variant < 2 ? -1 : 5;

        LatentRelationNeuron relPT = BeforeRelationNeuron.createBeforeRelationNeuron(m, relFrom, relTo, "relPT");

        BindingNeuron theBN = new BindingNeuron(m).setLabel("the (the dog)");
        new InputObjectSynapse()
                .setWeight(10.0)
                .link(theIN, theBN)
                .adjustBias();

        BindingNeuron dogBN = new BindingNeuron(m).setLabel("dog (the dog)");
        new InputObjectSynapse()
                .setWeight(variant == 0  || variant == 2 ? 10.0 : 5.0)
                .link(dogIN, dogBN)
                .adjustBias();

        if(variant < 2) {
            new RelationInputSynapse()
                    .setWeight(5.0)
                    .link(relPT, dogBN)
                    .adjustBias();
            new SameObjectSynapse()
                    .setWeight(variant == 1 || variant == 3 ? 10.0 : 5.0)
                    .link(theBN, dogBN)
                    .adjustBias();
        } else {
            new RelationInputSynapse()
                    .setWeight(5.0)
                    .link(relPT, theBN)
                    .adjustBias();
            new SameObjectSynapse()
                    .setWeight(variant == 1 || variant == 3 ? 10.0 : 5.0)
                    .link(dogBN, theBN)
                    .adjustBias();
        }

        PatternNeuron theDogP = initPatternLoop(m, "the dog", theBN, dogBN);

        addOuterInhibitoryLoop(theBN, inhibNThe, -10.0);
        addOuterInhibitoryLoop(theBN, new OuterInhibitoryNeuron(m).setLabel("I-the (tg)"), -10.0);

        theDogP.setBias(3.0);

        theBN.setBias(3.0);
        dogBN.setBias(3.0);
    }
}
