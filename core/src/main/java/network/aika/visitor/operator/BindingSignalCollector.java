package network.aika.visitor.operator;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.Transition;
import network.aika.enums.direction.Direction;
import network.aika.visitor.UpVisitor;

public class BindingSignalCollector implements Operator {

    private Transition bsType;
    private Transition forbidden;

    private PatternActivation bindingSignal;

    private int depth;


    public BindingSignalCollector(Transition bsType, Transition forbidden) {
        this.bsType = bsType;
        this.forbidden = forbidden;
    }

    public PatternActivation getBindingSignal() {
        return bindingSignal;
    }

    @Override
    public boolean checkForbiddenTransitions(Link l, Direction dir) {
        return l.getSynapse().getTransition() != forbidden;
    }

    @Override
    public boolean checkUp(Activation bsAct, int depth) {
        if(!(bsAct instanceof PatternActivation))
            return false;

        if(bindingSignal == null || depth > this.depth) {
            this.bindingSignal = (PatternActivation) bsAct;
            this.depth = depth;
        }

        return false;
    }

    @Override
    public void visitorCheck(UpVisitor v, Link lastLink, Activation act, int state) {
    }

    @Override
    public void relationCheck(Synapse relSyn, Activation toAct, Direction relDir) {
    }
}
