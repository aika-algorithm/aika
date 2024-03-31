package network.aika.debugger.neurons;

import network.aika.debugger.AbstractParticleLink;
import network.aika.debugger.activations.ActivationGraphManager;
import network.aika.debugger.activations.layout.*;
import network.aika.elements.links.*;
import network.aika.elements.synapses.Synapse;
import org.graphstream.graph.Edge;

public class ParticleSynapse<S extends Synapse> extends AbstractParticleLink<S> {
    S link;

    public ParticleSynapse(S syn, Edge e, NeuronGraphManager gm) {
        super(syn, e, gm);
        this.link = syn;

        inputNode = graphManager.getNode(syn.getInput());
        outputNode = graphManager.getNode(syn.getOutput());

        inputParticle = graphManager.getParticle(inputNode);
        outputParticle= graphManager.getParticle(outputNode);

        inputParticle.addOutputParticleLink(this);
        outputParticle.addInputParticleLink(this);

        applyEdgeStyle(syn, e);
    }

    public void applyEdgeStyle(Synapse s, Edge edge) {
        edge.setAttribute("ui.style",  s.getSynapseType().getDebugStyle());
    }

    @Override
    public Long getInputId() {
        return link.getInput().getId();
    }

    @Override
    public Long getOutputId() {
        return link.getOutput().getId();
    }

    public S getLink() {
        return link;
    }

    public static ParticleLink create(Link l, Edge e, ActivationGraphManager gm) {
        return new ParticleLink(l, e, gm);
    }

    @Override
    public void processLayout() {

    }
}