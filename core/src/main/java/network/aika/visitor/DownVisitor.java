package network.aika.visitor;

import network.aika.Thought;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.direction.Direction;
import network.aika.visitor.operator.Operator;
import network.aika.visitor.relations.BoundDownVisitor;
import network.aika.visitor.relations.BoundUpVisitor;
import network.aika.visitor.relations.UnboundUpVisitor;
import network.aika.visitor.types.VisitorType;

import static network.aika.utils.Utils.depthToSpace;

public abstract class DownVisitor<T extends ConjunctiveActivation> extends Visitor<T> {

    public DownVisitor(Thought t, VisitorType type, Operator operator) {
        this.v = t.getNewVisitorId();
        this.type = type;

        if(log.isDebugEnabled()) {
            log.debug("");
            log.debug(depthToSpace(0) + "Start:" + getClass().getSimpleName() + " " + operator.getClass().getSimpleName());
        }

        this.operator = operator;
    }

    public void checkRelation(T downBindingSource, Synapse relSyn, Direction relDir, int depth) {
        relSyn.getRelation()
                .evaluateLatentRelation((PatternActivation) downBindingSource, relDir.invert())
                .forEach(relTokenAct -> {
                            if (log.isDebugEnabled()) {
                                log.debug(
                                        depthToSpace(depth) + "U-TURN (rel) " +
                                                "downBS:" + downBindingSource.getClass().getSimpleName() + " " + downBindingSource.getId() + " " + downBindingSource.getLabel() + "  " +
                                                "upBS:" + relTokenAct.getClass().getSimpleName() + " " + relTokenAct.getId() + " " + relTokenAct.getLabel());
                            }

                            type.visit(
                                    new BoundUpVisitor(
                                            this,
                                            (PatternActivation) downBindingSource,
                                            relTokenAct,
                                            relSyn,
                                            relDir
                                    ),
                                    relTokenAct,
                                    null,
                                    depth
                            );
                        }
                );
    }

    public void next(Visitor v, Activation<?> act, int depth) {
        act.getInputLinks()
                .forEach(l ->
                        v.type.visit(v, l, depth)
                );
    }

    public void next(Visitor v, Link<?, ?, ?> l, int depth) {
        if(l.getInput() != null)
            v.type.visit(v, l.getInput(), l, depth);
    }

    public boolean isDown() {
        return true;
    }

    public boolean isUp() {
        return false;
    }

    public int getIndex() {
        return 0;
    }

    protected String getDir() {
        return "down";
    }
}
