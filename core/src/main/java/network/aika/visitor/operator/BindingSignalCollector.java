package network.aika.visitor.operator;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.Transition;
import network.aika.enums.direction.Direction;
import network.aika.utils.BitUtils;
import network.aika.visitor.UpVisitor;

import java.util.HashMap;
import java.util.Map;

public class BindingSignalCollector implements Operator {

    private Transition forbidden;

    private Map<Transition, PatternActivation> bindingSignals = new HashMap<>(2);


    public BindingSignalCollector(Transition forbidden) {
        this.forbidden = forbidden;
    }

    public Map<Transition, PatternActivation> getBindingSignals() {
        return bindingSignals;
    }

    @Override
    public boolean checkForbiddenTransitions(Link l, Direction dir) {
        return l.getSynapse().getTransition() != forbidden;
    }

    @Override
    public boolean checkUp(Activation bsAct, int state, int depth) {
        if(!(bsAct instanceof PatternActivation))
            return false;

        for(Transition t: Transition.values()) {
            if(BitUtils.isSet(state, t))
                bindingSignals.put(t, (PatternActivation) bsAct);
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
