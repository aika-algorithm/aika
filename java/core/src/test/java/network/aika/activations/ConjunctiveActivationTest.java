package network.aika.activations;


import network.aika.bindingsignal.BindingSignal;
import network.aika.neurons.ConjunctiveSynapse;
import network.aika.neurons.Neuron;
import network.aika.typedefs.EdgeDefinition;
import network.aika.typedefs.NodeDefinition;
import network.aika.typedefs.SynapseDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static network.aika.activations.TestBSTypes.A;
import static network.aika.activations.TestBSTypes.B;
import static network.aika.activations.TestUtils.getInputLink;
import static network.aika.bindingsignal.Transition.of;
import static network.aika.neurons.RefType.NEURON_EXTERNAL;


@ExtendWith(MockitoExtension.class)
public class ConjunctiveActivationTest extends AbstractActivationTest {

    Neuron inputNeuron;

    ConjunctiveSynapse synapse;

    @BeforeEach
    @Override
    public void init() {
        super.init();

        NodeDefinition inputNodeDef = new NodeDefinition(typeRegistry, "input");

        EdgeDefinition firstInputEdgeDef = new EdgeDefinition(typeRegistry, "test")
                .setInput(inputNodeDef)
                .setOutput(nodeDef);

        SynapseDefinition synapseDefinition = firstInputEdgeDef.synapse
                .setTransition(of(A, B));

        inputNodeDef.neuron.initFlattenedType();
        inputNodeDef.activation.initFlattenedType();
        firstInputEdgeDef.synapse.initFlattenedType();
        firstInputEdgeDef.link.initFlattenedType();

        inputNeuron = inputNodeDef.neuron.instantiate(model);
        synapse = (ConjunctiveSynapse) synapseDefinition.instantiate(inputNeuron, neuron);
    }

    @Test
    public void testLinkIncoming() {
        BindingSignal bs0 = new BindingSignal(0, doc);

        Activation iAct = inputNeuron.createActivation(null, doc, Map.of(A, bs0));
        Activation oAct = neuron.createActivation(null, doc, Map.of(B, bs0));

        Assertions.assertNull(getInputLink(oAct, 0));

        bs0.addActivation(iAct);

        Assertions.assertNull(getInputLink(oAct, 0));

        oAct.linkIncoming(null);

        Assertions.assertEquals(iAct, getInputLink(oAct, 0).getInput());

    }
}
