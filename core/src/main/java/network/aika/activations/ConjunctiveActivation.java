package network.aika.activations;

import network.aika.Document;
import network.aika.bindingsignal.BindingSignal;
import network.aika.neurons.Neuron;
import network.aika.neurons.Synapse;
import network.aika.typedefs.ActivationDefinition;
import network.aika.bindingsignal.BSType;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;


public class ConjunctiveActivation extends Activation {

    protected NavigableMap<Integer, Link> inputLinks = new TreeMap<>();

    public ConjunctiveActivation(
            ActivationDefinition t,
            Activation parent,
            Integer id,
            Neuron n,
            Document doc,
            Map<BSType, BindingSignal> bindingSignals
    ) {
        super(t, parent, id, n, doc, bindingSignals);
    }

    @Override
    public void linkIncoming(Activation excludedInputAct) {
        neuron
                .getInputSynapsesAsStream()
                .filter(s ->
                        s.isIncomingLinkingCandidate(getBindingSignals().keySet())
                )
                .forEach(s ->
                        linkIncoming(s, excludedInputAct)
                );
    }

    void linkIncoming(Synapse targetSyn, Activation excludedInputAct) {
        collectLinkingTargets(targetSyn.getInput(getModel())).stream()
                .filter(iAct -> iAct != excludedInputAct)
                .forEach(iAct ->
                        targetSyn.createLink(
                                iAct,
                                this
                        )
                );
    }

    @Override
    public void addInputLink(Link l) {
        Synapse syn = l.getSynapse();
        assert inputLinks.get(syn.getSynapseId()) == null;
        inputLinks.put(syn.getSynapseId(), l);
    }

    @Override
    public Link getInputLink(Activation iAct, int synapseId) {
        return inputLinks.get(synapseId);
    }

    @Override
    public Stream<Link> getInputLinks() {
        return inputLinks.values()
                .stream();
    }
}
