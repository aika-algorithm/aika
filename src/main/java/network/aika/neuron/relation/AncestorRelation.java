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
package network.aika.neuron.relation;

import network.aika.neuron.INeuron;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Position;

import java.util.*;
import java.util.stream.Stream;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class AncestorRelation extends Relation {

    public static AncestorRelation COMMON_ANCESTOR = new CommonAncestor();
    public static AncestorRelation IS_DESCENDANT_OF = new IsDescendantOf();
    public static AncestorRelation IS_ANCESTOR_OF = new IsAncestorOf();
    public static AncestorRelation NOT_DESCENDANT_OF = new NotDescendantOf();
    public static AncestorRelation NOT_ANCESTOR_OF = new NotAncestorOf();


    AncestorRelation() {}



    private static void collectCommonAncestor(Collection<Activation> results, INeuron n, Activation linkedAct, long v) {
        if(linkedAct.getVisitedId() == v) return;

        collectContains(results, n, linkedAct, v);

        linkedAct.getInputLinks()
                .filter(l -> l.isIdentity())
                .forEach(l -> collectCommonAncestor(results, n, l.getInput(), v));
    }


    private static void collectContains(Collection<Activation> results, INeuron n, Activation linkedAct, long v) {
        if(!linkedAct.checkVisited(v)) return;

        if(linkedAct.getINeuron() == n) {
            results.add(linkedAct);
        }

        linkedAct.getOutputLinks()
                .filter(l ->l.isIdentity())
                .forEach(l -> collectContains(results, n, l.getOutput(), v));
    }


    private static void collectContainedIn(Collection<Activation> results, INeuron n, Activation linkedAct, long v) {
        if(!linkedAct.checkVisited(v)) return;

        if(linkedAct.getINeuron() == n) {
            results.add(linkedAct);
        }

        linkedAct.getInputLinks()
                .filter(l -> l.isIdentity())
                .forEach(l -> collectContainedIn(results, n, l.getInput(), v));
    }


    @Override
    public void mapSlots(Map<Integer, Position> slots, Activation act) {
    }


    private static boolean contains(Activation actA, Activation actB, long v) {
        if(!actA.checkVisited(v)) return false;

        if(actA == actB) return true;

        return actA.getInputLinks()
                .filter(l -> l.isIdentity())
                .anyMatch(l -> contains(l.getInput(), actB, v));
    }


    private static boolean hasCommonAncestor(Activation act, Activation linkedAct) {
        long v = act.getNewVisitedId();
        markAncestors(linkedAct, v);
        return hasCommonAncestor(act, v, act.getNewVisitedId());
    }


    private static void markAncestors(Activation act, long v) {
        if(!act.checkVisited(v)) return;

        act.markedAncDesc = v;

        act.getInputLinks()
                .filter(l -> l.isIdentity())
                .forEach(l -> markAncestors(l.getInput(), v));
    }


    private static void markDescendants(Activation act, long v) {
        if(!act.checkVisited(v)) return;

        act.markedAncDesc = v;

        act.getOutputLinks()
                .filter(l -> l.isIdentity())
                .forEach(l -> markDescendants(l.getInput(), v));
    }


    private static boolean hasCommonAncestor(Activation act, long v1, long v2) {
        if(!act.checkVisited(v2)) return false;

        if(act.markedAncDesc == v1) return true;

        return act.getInputLinks()
                .filter(l -> l.isIdentity())
                .anyMatch(l -> hasCommonAncestor(l.getInput(), v1, v2));
    }


    @Override
    public boolean isExact() {
        return false;
    }


    public static class CommonAncestor extends AncestorRelation {
        public static int TYPE = 50;

        static {
            registerRelation(TYPE, () -> COMMON_ANCESTOR);
        }

        public CommonAncestor() {
        }

        @Override
        public int getType() {
            return TYPE;
        }

        @Override
        public Relation invert() {
            return new CommonAncestor();
        }

        @Override
        public boolean test(Activation act, Activation linkedAct, boolean allowUndefined) {
            return hasCommonAncestor(act, linkedAct);
        }

        @Override
        public Stream<Activation> getActivations(INeuron n, Activation linkedAct) {
            List<Activation> results = new ArrayList<>();
            collectCommonAncestor(results, n, linkedAct, linkedAct.getNewVisitedId());
            return results.stream();
        }

        public String toString() {
            return "COMMON-ANCESTOR";
        }
    }


    public static class IsDescendantOf extends AncestorRelation {
        public static int TYPE = 51;

        static {
            registerRelation(TYPE, () -> IS_DESCENDANT_OF);
        }

        public IsDescendantOf() {
        }

        @Override
        public int getType() {
            return TYPE;
        }

        @Override
        public Relation invert() {
            return new IsAncestorOf();
        }

        @Override
        public boolean test(Activation act, Activation linkedAct, boolean allowUndefined) {
            return contains(act, linkedAct, act.getNewVisitedId());
        }

        @Override
        public Stream<Activation> getActivations(INeuron n, Activation linkedAct) {
            List<Activation> results = new ArrayList<>();
            collectContains(results, n, linkedAct, linkedAct.getNewVisitedId());
            return results.stream();
        }

        public String toString() {
            return "DESCENDANT-OF";
        }
    }


    public static class IsAncestorOf extends AncestorRelation {
        public static int TYPE = 52;

        static {
            registerRelation(TYPE, () -> IS_ANCESTOR_OF);
        }

        public IsAncestorOf() {
        }

        @Override
        public int getType() {
            return TYPE;
        }

        @Override
        public Relation invert() {
            return new IsDescendantOf();
        }


        @Override
        public boolean test(Activation act, Activation linkedAct, boolean allowUndefined) {
            return contains(linkedAct, act, act.getNewVisitedId());
        }

        @Override
        public Stream<Activation> getActivations(INeuron n, Activation linkedAct) {
            List<Activation> results = new ArrayList<>();
            collectContainedIn(results, n, linkedAct, linkedAct.getNewVisitedId());
            return results.stream();
        }

        public String toString() {
            return "ANCESTOR-OF";
        }
    }


    public static class NotDescendantOf extends AncestorRelation {
        public static int TYPE = 53;

        static {
            registerRelation(TYPE, () -> NOT_DESCENDANT_OF);
        }

        public NotDescendantOf() {
        }

        @Override
        public int getType() {
            return TYPE;
        }

        @Override
        public Relation invert() {
            return new NotAncestorOf();
        }

        @Override
        public boolean test(Activation act, Activation linkedAct, boolean allowUndefined) {
            return !contains(act, linkedAct, act.getNewVisitedId());
        }

        @Override
        public Stream<Activation> getActivations(INeuron n, Activation linkedAct) {
            long v = linkedAct.getNewVisitedId();
            markDescendants(linkedAct, v);

            return n.getActivations(linkedAct.getDocument())
                    .filter(act -> act.markedAncDesc != v);
        }

        public String toString() {
            return "NOT-DESCENDANT-OF";
        }
    }


    public static class NotAncestorOf extends AncestorRelation {
        public static int TYPE = 54;

        static {
            registerRelation(TYPE, () -> NOT_ANCESTOR_OF);
        }

        public NotAncestorOf() {
        }

        @Override
        public int getType() {
            return TYPE;
        }

        @Override
        public Relation invert() {
            return new NotDescendantOf();
        }

        @Override
        public boolean test(Activation act, Activation linkedAct, boolean allowUndefined) {
            return !contains(linkedAct, act, act.getNewVisitedId());
        }

        @Override
        public Stream<Activation> getActivations(INeuron n, Activation linkedAct) {
            long v = linkedAct.getNewVisitedId();
            markAncestors(linkedAct, v);

            return n.getActivations(linkedAct.getDocument())
                    .filter(act -> act.markedAncDesc != v);
        }

        public String toString() {
            return "NOT-ANCESTOR-OF";
        }
    }
}