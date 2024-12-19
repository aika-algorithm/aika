package network.aika.bindingsignal;

import network.aika.Document;
import network.aika.activations.Activation;
import network.aika.activations.ActivationKey;
import network.aika.neurons.Neuron;

import java.util.*;
import java.util.stream.Stream;


public class BindingSignal {

    private final int tokenId;

    private final Document doc;

    private final NavigableMap<ActivationKey, Activation> activations = new TreeMap<>(
            Comparator.comparingLong(ActivationKey::neuronId)
                    .thenComparingInt(ActivationKey::actId)
    );

    public BindingSignal(int tokenId, Document doc) {
        this.tokenId = tokenId;
        this.doc = doc;
    }

    public int getTokenId() {
        return tokenId;
    }

    public Document getDocument() {
        return doc;
    }

    public void addActivation(Activation act) {
        activations.put(act.getKey(), act);
    }

    public Stream<Activation> getActivations(Neuron n) {
        return activations.subMap(
                        new ActivationKey(n.getId(), Integer.MIN_VALUE),
                        new ActivationKey(n.getId(), Integer.MAX_VALUE)
                )
                .values()
                .stream();
    }

    public Collection<Activation> getActivations() {
        return activations.values();
    }

    public String toString() {
        return "" + tokenId;
    }
}
