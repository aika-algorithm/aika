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
import network.aika.elements.synapses.InputPatternSynapse;
import network.aika.elements.synapses.RelationInputSynapse;
import network.aika.elements.synapses.SamePatternSynapse;
import network.aika.enums.Scope;

import static network.aika.TestUtils.*;

/**
 *
 * @author Lukas Molzberger
 */
public class TestHelper {

    public static void initPatternTheCat(Model m, SameInhibitoryNeuron inhibNThe, SameInhibitoryNeuron inhibNCat, int variant) {
        PatternNeuron theIN = lookupToken(m, "the");
        PatternNeuron catIN = lookupToken(m, "cat");

        int relFrom = variant < 2 ? -5 : 1;
        int relTo = variant < 2 ? -1 : 5;

        LatentRelationNeuron relPT = TokenPositionRelationNeuron.lookupRelation(m, relFrom, relTo);

        BindingNeuron theBN = new BindingNeuron().init(m, "the (the cat)");
        new InputPatternSynapse()
                .setWeight(10.0)
                .init(theIN, theBN)
                .adjustBias();

        BindingNeuron catBN = new BindingNeuron().init(m, "cat (the cat)");
        new InputPatternSynapse()
                .setWeight(variant == 0  || variant == 2 ? 10.0 : 5.0)
                .init(catIN, catBN)
                .adjustBias();

        if(variant < 2) {
            new RelationInputSynapse()
                    .setWeight(5.0)
                    .init(relPT, catBN)
                    .adjustBias();
            new SamePatternSynapse()
                    .setWeight(variant == 1 || variant == 3 ? 10.0 : 5.0)
                    .init(theBN, catBN)
                    .adjustBias();
        } else {
            new RelationInputSynapse()
                    .setWeight(5.0)
                    .init(relPT, theBN)
                    .adjustBias();
            new SamePatternSynapse()
                    .setWeight(variant == 1 || variant == 3 ? 10.0 : 5.0)
                    .init(catBN, theBN)
                    .adjustBias();
        }

        PatternNeuron theCatP = initPatternLoop(m, "the cat", theBN, catBN);

        //addInhibitoryLoop(inhibNThe, false, theBN);
        //addInhibitoryLoop(new InhibitoryNeuron().init(m, "I-the (tc)"), true, theBN);

        setBias(theCatP, 3.0);

        setBias(theBN, 3.0);
        setBias(catBN, 3.0);
    }

    public static void initPatternBlackCat(Model m) {
        PatternNeuron blackIN = lookupToken(m, "black");
        PatternNeuron catIN = lookupToken(m, "cat");

        LatentRelationNeuron relPT = TokenPositionRelationNeuron.lookupRelation(m, -1, -1);

        BindingNeuron blackBN = new BindingNeuron().init(m, "black (black cat)");
        new InputPatternSynapse()
                .setWeight(10.0)
                .init(blackIN, blackBN)
                .adjustBias();

        BindingNeuron catBN = new BindingNeuron().init(m, "cat (black cat)");
        new InputPatternSynapse()
                .setWeight(20.0)
                .init(catIN, catBN)
                .adjustBias();

        new RelationInputSynapse()
                .setWeight(5.0)
                .init(relPT, catBN)
                .adjustBias();

        new SamePatternSynapse()
                .setWeight(5.0)
                .init(blackBN, catBN)
                .adjustBias();

        PatternNeuron blackCat = initPatternLoop(m, "black cat", blackBN, catBN);
        setBias(blackCat, 3.0);

        setBias(blackBN, 3.0);
        setBias(catBN, 3.0);
    }

    public static void initPatternTheDog(Model m, SameInhibitoryNeuron inhibNThe, SameInhibitoryNeuron inhibNDog, int variant) {
        PatternNeuron theIN = lookupToken(m, "the");
        PatternNeuron dogIN = lookupToken(m, "dog");

        int relFrom = variant < 2 ? -5 : 1;
        int relTo = variant < 2 ? -1 : 5;

        LatentRelationNeuron relPT = TokenPositionRelationNeuron.lookupRelation(m, relFrom, relTo);

        BindingNeuron theBN = new BindingNeuron().init(m, "the (the dog)");
        new InputPatternSynapse()
                .setWeight(10.0)
                .init(theIN, theBN)
                .adjustBias();

        BindingNeuron dogBN = new BindingNeuron().init(m, "dog (the dog)");
        new InputPatternSynapse()
                .setWeight(variant == 0  || variant == 2 ? 10.0 : 5.0)
                .init(dogIN, dogBN)
                .adjustBias();

        if(variant < 2) {
            new RelationInputSynapse()
                    .setWeight(5.0)
                    .init(relPT, dogBN)
                    .adjustBias();
            new SamePatternSynapse()
                    .setWeight(variant == 1 || variant == 3 ? 10.0 : 5.0)
                    .init(theBN, dogBN)
                    .adjustBias();
        } else {
            new RelationInputSynapse()
                    .setWeight(5.0)
                    .init(relPT, theBN)
                    .adjustBias();
            new SamePatternSynapse()
                    .setWeight(variant == 1 || variant == 3 ? 10.0 : 5.0)
                    .init(dogBN, theBN)
                    .adjustBias();
        }

        PatternNeuron theDogP = initPatternLoop(m, "the dog", theBN, dogBN);

        addInhibitoryLoop(inhibNThe, false, theBN);
        addInhibitoryLoop(new SameInhibitoryNeuron().init(m, "I-the (tg)"), true, theBN);

        setBias(theDogP, 3.0);

        setBias(theBN, 3.0);
        setBias(dogBN, 3.0);
    }
}
