package network.aika.network;

import network.aika.Document;
import network.aika.Model;
import network.aika.neuron.INeuron;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.relation.Relation;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.stream.Collectors;

import static network.aika.neuron.INeuron.Type.EXCITATORY;
import static network.aika.neuron.INeuron.Type.INPUT;
import static network.aika.neuron.Synapse.OUTPUT;
import static network.aika.neuron.relation.Relation.EQUALS;

public class AsymmetricSuppressionTest {


    @Test
    public void testAsymmetricSuppression() {
        Model m = new Model();

        Neuron inA = m.createNeuron("A", INPUT);

        Neuron inB = m.createNeuron("B", INPUT);

        Neuron outN = Neuron.init(m.createNeuron("OUT", EXCITATORY),
                10.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inA)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inB)
                        .setWeight(-100.0)
                        .setRecurrent(true),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );

        Document doc = new Document(m, "a");

        inA.addInput(doc, 0, 1);
        inB.addInput(doc, 0, 1);

        doc.process();

        System.out.println(doc.activationsToString());

        Assert.assertTrue(outN.getActivations(doc, true).collect(Collectors.toList()).isEmpty());
    }
}
