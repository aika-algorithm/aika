package network.aika.visitor.relations;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.visitor.DownVisitor;
import network.aika.visitor.UpVisitor;

public class UnboundUpVisitor extends UpVisitor<ConjunctiveActivation> {

    public UnboundUpVisitor(DownVisitor downVisitor, ConjunctiveActivation bindingSource) {
        super(downVisitor, bindingSource);
    }

    public boolean compatible(Synapse from, Synapse to) {
        return to.getRelation() == null;
    }

    @Override
    public void createLatentRelation(Link l) {
        // Nothing to do
    }
}
