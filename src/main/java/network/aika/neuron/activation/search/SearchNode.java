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
package network.aika.neuron.activation.search;


import network.aika.Document;
import network.aika.Utils;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Activation.RecursiveDepthExceededException;
import network.aika.neuron.activation.Activation.OscillatingActivationsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static network.aika.neuron.Synapse.State.CURRENT;
import static network.aika.neuron.activation.search.Decision.EXCLUDED;
import static network.aika.neuron.activation.search.Decision.SELECTED;
import static network.aika.neuron.activation.search.Decision.UNKNOWN;


import java.util.*;

/**
 * The {@code SearchNode} class represents a node in the binary search tree that is used to find the optimal
 * interpretation for a given document. Each search node possess a refinement (simply a set of interpretation nodes).
 * The two options that this search node examines are that the refinement will either part of the final interpretation or not.
 * During each search step the activation values in all the neuron activations adjusted such that they reflect the interpretation of the current search path.
 * When the search reaches the maximum depth of the search tree and no further refinements exists, a weight is computed evaluating the current search path.
 * The search path with the highest weight is used to determine the final interpretation.
 * <p>
 * <p> Before the search is started a set of initial refinements is generated from the conflicts within the document.
 * In other words, if there are no conflicts in a given document, then no search is needed. In this case the final interpretation
 * will simply be the set of all interpretation nodes. The initial refinements are then expanded, meaning all interpretation nodes that are consistent
 * with this refinement are added to the refinement. The initial refinements are then propagated along the search path as refinement candidates.
 *
 * @author Lukas Molzberger
 */
public class SearchNode implements Comparable<SearchNode> {

    private static final Logger log = LoggerFactory.getLogger(SearchNode.class);

    public static int MAX_SEARCH_STEPS = Integer.MAX_VALUE;
    public static boolean OPTIMIZE_SEARCH = true;
    public static boolean COMPUTE_SOFT_MAX = false;

    private int id;

    private SearchNode parent;
    private Decision decision;

    private Activation act;
    private int level;

    public Branch selected = new Branch();
    public Branch excluded = new Branch();

    private double weightDelta;
    private double accumulatedWeight = 0.0;

    private Map<Activation, Option> modifiedActs = new TreeMap<>(Activation.ACTIVATION_ID_COMP);

    private Step step = Step.INIT;
    private Decision currentChildDecision = UNKNOWN;

    private long processVisited;
    private boolean bestPath;
    private int cachedCount = 1;
    private int cachedFactor = 1;

    private DebugState debugState;


    private enum Step {
        INIT,
        SELECT,
        POST_SELECT,
        EXCLUDE,
        POST_EXCLUDE,
        FINAL
    }


    public enum DebugState {
        CACHED,
        LIMITED,
        EXPLORE
    }


    public SearchNode(Document doc, Decision d, SearchNode p, int level) {
        id = doc.searchNodeIdCounter++;
        decision = d;
        parent = p;
        this.level = level;
    }


    public Branch getBranch(Decision d) {
        switch(d) {
            case SELECTED:
                return selected;
            case EXCLUDED:
                return excluded;
        }
        return null;
    }


    public SearchNode getAlternative() {
        return parent.getBranch(decision.getInverted()).child;
    }


    public void updateActivations(Document doc) throws OscillatingActivationsException {
        Activation act = getActivation();

        weightDelta = doc.getValueQueue().process(this);

        if (act != null && followPath()) {
            act.cachedSearchNode = this;

            if(COMPUTE_SOFT_MAX) {
                modifiedActs.values().forEach(o -> o.link());
            }
        }

        if (parent != null) {
            accumulatedWeight = weightDelta + parent.accumulatedWeight;
        }
    }


    public boolean followPath() {
        return getActivation().currentOption.searchNode == this && decision == getActivation().currentOption.getState().getPreferredDecision();
    }


    public int getId() {
        return id;
    }


    public Map<Activation, Option> getModifiedActivations() {
        return modifiedActs;
    }


    public double getAccumulatedWeight() {
        return accumulatedWeight;
    }


    public Activation getActivation() {
        return parent != null ? parent.act : null;
    }


    /**
     * Searches for the best interpretation for the given document.
     * <p>
     * This implementation of the algorithm is iterative to prevent stack overflow errors from happening.
     * Depending on the document the search tree might be getting very deep.
     *
     * @param doc
     * @param root
     */
    public static void search(Document doc, SearchNode root, long v, Long timeoutInMilliSeconds) throws TimeoutException, RecursiveDepthExceededException, OscillatingActivationsException {
        SearchNode sn = root;
        double returnWeight = 0.0;
        double returnWeightSum = 0.0;
        long startTime = System.currentTimeMillis();

        do {
            if (sn.processVisited != v) {
                sn.step = Step.INIT;
                sn.processVisited = v;
            }

            switch (sn.step) {
                case INIT:
                    if (sn.level >= doc.candidates.size()) {
                        checkTimeoutCondition(doc, timeoutInMilliSeconds, startTime);

                        returnWeight = sn.processResult(doc);
                        returnWeightSum = returnWeight;

                        sn.step = Step.FINAL;
                        sn = sn.parent;
                    } else {
                        sn.initStep(doc);
                        sn.step = Step.SELECT;
                    }
                    break;
                case SELECT:
                    if (sn.prepareStep(doc, SELECTED)) {
                        sn.step = Step.POST_SELECT;
                        sn = sn.selected.child; // Recursive Step
                    } else {
                        sn.step = Step.EXCLUDE;
                    }

                    break;
                case POST_SELECT:
                    sn.selected.postStep(returnWeight, returnWeightSum);

                    sn.step = Step.SELECT;
                    break;
                case EXCLUDE:
                    if (sn.prepareStep(doc, EXCLUDED)) {
                        sn.step = Step.POST_EXCLUDE;
                        sn = sn.excluded.child; // Recursive Step
                    } else {
                        sn.step = Step.FINAL;
                    }

                    break;
                case POST_EXCLUDE:
                    sn.excluded.postStep(returnWeight, returnWeightSum);

                    sn.step = Step.SELECT;
                    break;
                case FINAL:
                    returnWeight = sn.finalStep();
                    returnWeightSum = sn.getWeightSum();

                    sn = sn.parent;
                    break;
                default:
            }
        } while (sn != null);
    }



    public void setWeight(double w) {
        for (Option sc : modifiedActs.values()) {
            sc.setWeight(w);
        }
    }


    private static void checkTimeoutCondition(Document doc, Long timeoutInMilliSeconds, long startTime) throws TimeoutException {
        if (timeoutInMilliSeconds != null && System.currentTimeMillis() > startTime + timeoutInMilliSeconds) {
            throw new TimeoutException(doc, "Interpretation search took too long: " + (System.currentTimeMillis() - startTime) + "ms");
        }
    }


    public double getWeightSum() {
        return selected.weightSum + excluded.weightSum;
    }


    private void initStep(Document doc) throws RecursiveDepthExceededException {
        act = doc.candidates.get(level);

        if (OPTIMIZE_SEARCH) {
            Decision cd = getCachedDecision();

            if(cd != null && cd != UNKNOWN) {
                getBranch(cd).weightSum = act.alternativeCachedWeightSum;

                if (COMPUTE_SOFT_MAX) {
                    SearchNode asn = act.cachedSearchNode.getAlternative();
                    if (asn != null) {
                        asn.cachedCount++;
                    }
                }
            }
        }

        if (doc.searchStepCounter > MAX_SEARCH_STEPS) {
            dumpDebugState();
            throw new RuntimeException("Max search step exceeded.");
        }

        doc.searchStepCounter++;

        storeDebugInfos();
    }


    private Decision getCachedDecision() {
        return act.cachedDecision;
    }


    private boolean prepareStep(Document doc, Decision d) throws OscillatingActivationsException {
        Branch b = getBranch(d);

        if(b.visited) {
            return false;
        }
        b.visited = true;

        if (OPTIMIZE_SEARCH && getCachedDecision() == d.getInverted() &&
                (selected.searched || d == SELECTED)) {  // In case there is a tie between two cached conflicting activations.
            return false;
        }

        if(act != null) {
            act.countSearchVisits++;
        }

        SearchNode child = new SearchNode(doc, d, this, level + 1);

        if (b.prepareStep(doc, child)) return false;

        if (d == SELECTED && act.cachedDecision == UNKNOWN) {
            invalidateCachedDecisions();
        }

        act.debugDecisionCounts[d.ordinal()]++;

        return true;
    }


    private double finalStep() {
        Decision d = selected.weight >= excluded.weight ? SELECTED : EXCLUDED;

        if (selected.searched && excluded.searched) {
            act.cachedDecision = d;
            act.alternativeCachedWeightSum = getBranch(act.cachedDecision).weightSum;
        }

        Branch b = getBranch(d);
        SearchNode cn = b.child;
        if (cn != null && cn.bestPath) {
            act.bestChildNode = cn;
            bestPath = true;
        }

        if(!COMPUTE_SOFT_MAX) {
            if(!bestPath) {
                b.cleanup();
            }
            getBranch(d.getInverted()).cleanup();
        }

        return b.weight;
    }


    private void invalidateCachedDecisions() {
        act.getOutputLinks()
                .filter(l -> !l.isNegative(CURRENT))
                .forEach(l -> invalidateCachedDecision(l.getOutput()));
    }


    public static void invalidateCachedDecision(Activation act) {
        if (act != null && act.cachedDecision == EXCLUDED) {
            act.cachedDecision = UNKNOWN;

            SearchNode pn = act.cachedSearchNode.parent;
            if(pn != null) {
                pn.selected.repeat();
            }
        }

        act.getInputLinks()
                .filter(l -> l.isRecurrent() && l.isNegative(CURRENT))
                .map(l -> l.getInput())
                .filter(c -> c.cachedDecision == SELECTED)
                .forEach(c -> c.cachedDecision = UNKNOWN);
    }


    private double processResult(Document doc) {
        double accNW = accumulatedWeight;

        if (level > doc.selectedSearchNode.level || accNW > getSelectedAccumulatedWeight(doc)) {
            doc.selectedSearchNode = this;
            doc.storeFinalState();
            bestPath = true;
        } else {
            bestPath = false;
        }
/*
        if(COMPUTE_SOFT_MAX) {
            dumpDebugState();
            System.out.println(accumulatedWeight);
            System.out.println();
        }
*/
        return accumulatedWeight;
    }


    public static void computeCachedFactor(SearchNode sn) {
        while (sn != null) {
            switch (sn.currentChildDecision) {
                case UNKNOWN:
                    sn.currentChildDecision = SELECTED;
                    if (sn.selected.child != null) {
                        sn = sn.selected.child;
                        sn.computeCacheFactor();
                    }
                    break;
                case SELECTED:
                    sn.currentChildDecision = EXCLUDED;
                    if (sn.excluded.child != null) {
                        sn = sn.excluded.child;
                        sn.computeCacheFactor();
                    }
                    break;
                case EXCLUDED:
                    sn = sn.parent;
                    break;
            }
        }
    }


    private void computeCacheFactor() {
        cachedFactor = (parent != null ? parent.cachedFactor : 1) * cachedCount;

        for (Option sc : modifiedActs.values()) {
            sc.setCacheFactor(cachedFactor);
        }
    }


    private double getSelectedAccumulatedWeight(Document doc) {
        return doc.selectedSearchNode != null ? doc.selectedSearchNode.accumulatedWeight : -1.0;
    }


    public void changeState(Activation.Mode m) {
        modifiedActs
                .values()
                .forEach(sc -> sc.restoreState(m));
    }


    @Override
    public int compareTo(SearchNode sn) {
        return Integer.compare(id, sn.id);
    }


    public Decision getDecision() {
        return decision;
    }


    private void storeDebugInfos() {
        debugState = getDebugState();
        act.debugCounts[debugState.ordinal()]++;
    }


    private DebugState getDebugState() {
        if (!selected.searched || !excluded.searched) {
            return DebugState.LIMITED;
        } else if (getCachedDecision() != UNKNOWN) {
            return DebugState.CACHED;
        } else {
            return DebugState.EXPLORE;
        }
    }


    public void dumpDebugState() {
        SearchNode n = this;
        String weights = "";
        Decision decision = UNKNOWN;
        while (n != null && n.level >= 0) {
            log.info(
                    n.level + " " +
                            n.debugState +
                            " DECISION:" + decision +
                            weights +
                            " " + (n.act != null ? n.act.toString() : "") +
                            " MOD-ACTS:" + n.modifiedActs.size()
            );

            decision = n.decision;
            weights = " AW:" + Utils.round(n.accumulatedWeight) +
                    " DW:" + Utils.round(n.weightDelta);

            n = n.parent;
        }
    }


    public String toString() {
        return "id:" + id + " actId:" + (act != null ? act.getId() : "-") + " Decision:" + getDecision() + " curDec:" + currentChildDecision;
    }


    public static class TimeoutException extends RuntimeException {
        private Document doc;

        public TimeoutException(Document doc, String message) {
            super(message);
            this.doc = doc;
        }

        public Document getDocument() {
            return doc;
        }
    }
}
