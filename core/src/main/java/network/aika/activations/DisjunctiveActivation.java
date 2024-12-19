package network.aika.activations;

import network.aika.Document;
import network.aika.bindingsignal.BindingSignal;
import network.aika.neurons.Neuron;
import network.aika.typedefs.ActivationDefinition;
import network.aika.bindingsignal.BSType;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;


public class DisjunctiveActivation extends Activation {

    protected NavigableMap<Integer, Link> inputLinks = new TreeMap<>();

    public DisjunctiveActivation(
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
    public void addInputLink(Link l) {
        Activation iAct = l.getInput();
        assert inputLinks.get(iAct.getId()) == null;
        inputLinks.put(iAct.getId(), l);
    }

    @Override
    public void linkIncoming(Activation excludedInputAct) {

    }

    @Override
    public Link getInputLink(Activation iAct, int synapseId) {
        return inputLinks.get(iAct.getId());
    }

    @Override
    public Stream<Link> getInputLinks() {
        return inputLinks.values()
                .stream();
    }

}
