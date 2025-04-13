package network.aika.typedefs;

import network.aika.activations.Activation;
import network.aika.type.TypeRegistry;
import network.aika.neurons.Neuron;

public class NodeDefinition {

    public final NeuronDefinition neuron;

    public final ActivationDefinition activation;

    public NodeDefinition(TypeRegistry registry, String name) {
        neuron = new NeuronDefinition(registry, name + "Neuron");
        activation = new ActivationDefinition(registry, name + "Activation");

        neuron.setActivation(activation);
        activation.setNeuron(neuron);
    }

    public NodeDefinition addParent(NodeDefinition parent) {
        neuron.addParent(parent.neuron);
        activation.addParent(parent.activation);
        return this;
    }
}
