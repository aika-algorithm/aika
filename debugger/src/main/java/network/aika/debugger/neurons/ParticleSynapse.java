package network.aika.debugger.neurons;

import network.aika.debugger.AbstractParticleLink;
import network.aika.debugger.activations.ActivationGraphManager;
import network.aika.debugger.activations.layout.*;
import network.aika.debugger.activations.particles.ActivationParticle;
import network.aika.enums.direction.Direction;
import network.aika.elements.links.*;
import network.aika.elements.synapses.FeedbackSynapse;
import network.aika.elements.synapses.Synapse;
import org.graphstream.graph.Edge;
import org.graphstream.ui.geom.Vector3;
import org.miv.pherd.geom.Point3;

import static network.aika.debugger.AbstractGraphManager.STANDARD_DISTANCE_Y;
import static network.aika.debugger.TypeMapper.synapseTypeModifiers;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;

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
        String synapseTypeModifier = synapseTypeModifiers.get(s.getClass());
        if(synapseTypeModifier == null)
            synapseTypeModifier = "";

        if(s instanceof FeedbackSynapse)
            synapseTypeModifier += " arrow-shape: diamond;";

        edge.setAttribute("ui.style", synapseTypeModifier);
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

    public void calculateForce(Vector3 delta, Point3 pos, Direction dir, ActivationParticle other) {
        double targetDistance = getInitialYDistance();

        Point3 opos = other.getPosition();
        double dy = 0.0;

        if(dir == INPUT) {
            dy = (opos.y + targetDistance) - pos.y;
            dy = Math.max(0.0, dy);
        } else if(dir == OUTPUT) {
            dy = opos.y - (pos.y + targetDistance);
            dy = Math.min(0.0, dy);
        }

        delta.set(0.0, dy, 0.0);
    }


    public double getInitialYDistance() {
        return STANDARD_DISTANCE_Y;
    }

    @Override
    public void processLayout() {

    }
}