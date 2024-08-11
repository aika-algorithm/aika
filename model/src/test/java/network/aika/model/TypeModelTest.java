package network.aika.model;

import network.aika.Config;
import network.aika.Document;
import network.aika.Model;
import network.aika.Range;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.Synapse;
import network.aika.text.TextReference;
import org.junit.jupiter.api.Test;

import static network.aika.elements.neurons.RefType.NEURON_EXTERNAL;


public class TypeModelTest {


    @Test
    public void testTypeModel() {
        Model m = new Model();
        m.setConfig(new Config());

        TypeModel typeModel = new TypeModel(new Config());

        System.out.println(typeModel.dumpModel());

        Neuron inputNeuron = typeModel
                .getPattern()
                .getNeuron()
                .instantiate(m, NEURON_EXTERNAL)
                .setLabel("IN");

        Neuron outputNeuron = typeModel
                .getBinding()
                .getNeuron()
                .instantiate(m, NEURON_EXTERNAL)
                .setLabel("OUT")
                .setBias(1.0);

        Synapse synapse = typeModel
                .getBinding()
                .getSynapse()
                .instantiate(inputNeuron, outputNeuron)
                .setWeight(10.0);


        Document doc = new Document(m, "test");
        doc.addToken(
                inputNeuron,
                new TextReference(
                        new Range(0, 1),
                        new Range(0, 4)
                ),
                5.0
        );

        doc.disconnect();
    }
}
