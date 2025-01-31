package network.aika.typedefs;

import network.aika.activations.Link;
import network.aika.type.TypeRegistry;
import network.aika.neurons.Synapse;

public class EdgeDefinition {

    public final SynapseDefinition synapse;

    public final LinkDefinition link;

    public EdgeDefinition(TypeRegistry registry, String name) {
        synapse = new SynapseDefinition(registry, name + "Synapse");
        link = new LinkDefinition(registry, name + "Link");

        synapse.setLink(link);
        link.setSynapse(synapse);
    }

    public EdgeDefinition addParent(EdgeDefinition parent) {
        synapse.addParent(parent.synapse);
        link.addParent(parent.link);
        return this;
    }

    public EdgeDefinition setInput(NodeDefinition node) {
        synapse.setInput(node.neuron);
        link.setInput(node.activation);
        return this;
    }

    public EdgeDefinition setOutput(NodeDefinition node) {
        synapse.setOutput(node.neuron);
        link.setOutput(node.activation);
        return this;
    }
}
