package network.aika.visitor.relations;

import network.aika.Thought;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.synapses.Synapse;
import network.aika.visitor.DownVisitor;
import network.aika.visitor.types.VisitorType;
import network.aika.visitor.operator.Operator;

import static network.aika.enums.direction.Direction.INPUT;

public class UnboundDownVisitor extends DownVisitor<ConjunctiveActivation> {

    public UnboundDownVisitor(Thought t, VisitorType type, Operator operator) {
        super(t, type, operator);
    }

    @Override
    public void up(ConjunctiveActivation bindingSource, int depth) {
            type.visit(
                    new UnboundUpVisitor(this, bindingSource),
                    bindingSource,
                    null,
                    depth
            );

            expandRelation(
                    bindingSource,
                    depth,
                    getOperator().getStartSynapse());
    }

    public void expandRelation(ConjunctiveActivation<?> origin, int depth, Synapse<?, ?, ?, ?, ?, ?> startSyn) {
        startSyn.getOutput()
                .getSynapsesWithRelations()
                .forEach(relSyn ->
                        checkRelation(origin, relSyn, INPUT, depth)
                );
    }
}
