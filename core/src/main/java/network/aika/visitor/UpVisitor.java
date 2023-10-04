package network.aika.visitor;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;


public abstract class UpVisitor<T extends ConjunctiveActivation> extends Visitor<T> {

    private final T bindingSource;

    protected UpVisitor(DownVisitor<T> downVisitor, T bindingSource) {
        this.v = downVisitor.v;
        this.type = downVisitor.type;
        this.bindingSource = bindingSource;
        this.operator = downVisitor.operator;
    }

    public void next(Activation<?> act, Link lastLink, int depth) {
        check(lastLink, act);
        super.next(act, lastLink, depth);
    }

    public void check(Link lastLink, Activation act) {
        if(isUp())
            operator.check(this, lastLink, act);
    }

    public boolean compatible(Synapse from, Synapse to) {
        return bindingSource != null;
    }

    public void next(Visitor v, Activation<?> act, int depth) {
        act.getOutputLinks()
                .forEach(l ->
                        v.type.visit(v, l, depth)
                );
    }

    public void next(Visitor v, Link<?, ?, ?> l, int depth) {
        v.type.visit(v, l.getOutput(), l, depth);
    }

    public boolean isDown() {
        return false;
    }

    public boolean isUp() {
        return true;
    }

    public int getIndex() {
        return 1;
    }

    protected String getDir() {
        return "up";
    }

    public abstract void createLatentRelation(Link l);
}
