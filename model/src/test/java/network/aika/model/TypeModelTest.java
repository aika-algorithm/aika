package network.aika.model;

import network.aika.Config;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.RefType;
import network.aika.elements.synapses.Synapse;
import org.junit.jupiter.api.Test;

public class TypeModelTest {


    @Test
    public void testTypeModel() {
        TypeModel typeModel = new TypeModel(new Config());

        Neuron inputNeuron = typeModel
                .getPattern()
                .getNeuron()
                .instantiate(RefType.OTHER);

        Neuron outputNeuron = typeModel
                .getBinding()
                .getNeuron()
                .instantiate(RefType.OTHER);

        Synapse synapse = typeModel
                .getBinding()
                .getSynapse()
                .instantiate(inputNeuron, outputNeuron);
    }
}
