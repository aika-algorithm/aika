package network.aika;

import network.aika.debugger.AikaDebugger;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.excitatory.BindingNeuron;
import network.aika.neuron.excitatory.PatternNeuron;
import network.aika.text.Document;
import network.aika.text.TextModel;
import network.aika.text.TextReference;
import org.junit.jupiter.api.Test;

public class OscillationTest {

    @Test
    public void oscillationTest() {
        TextModel m = new TextModel();

        m.setN(912);

        Document doc = new Document("A ");
        doc.setConfig(
                new Config() {
                    public String getLabel(Activation act) {
                        return "X";
                    }
                }
                        .setAlpha(0.99)
                        .setLearnRate(-0.1)
                        .setEnableTraining(true)
        );

        PatternNeuron nA = m.getTemplates().SAME_PATTERN_TEMPLATE.instantiateTemplate(true);
        nA.setLabel("P-A");

        nA.setFrequency(53.0);
        nA.getSampleSpace().setN(299);
        nA.getSampleSpace().setLastPos(899l);

        BindingNeuron nPPA =  m.getTemplates().SAME_BINDING_TEMPLATE.instantiateTemplate(true);
        nPPA.setLabel("B-A");

        Synapse s = m.getTemplates().PRIMARY_INPUT_SYNAPSE_TEMPLATE.instantiateTemplate(nA, nPPA);

        s.setWeight(0.3);

        s.linkInput();
        s.linkOutput();

        AikaDebugger.createAndShowGUI(doc,m);

        doc.addInput(nA, new TextReference(doc, 0, 1));

        doc.process(m);

        System.out.println();
    }
}
