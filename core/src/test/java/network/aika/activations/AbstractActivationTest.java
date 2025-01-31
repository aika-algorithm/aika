package network.aika.activations;

import network.aika.Document;
import network.aika.Model;
import network.aika.type.TypeRegistry;
import network.aika.neurons.Neuron;
import network.aika.typedefs.NodeDefinition;
import org.junit.jupiter.api.BeforeEach;

import static network.aika.neurons.RefType.NEURON_EXTERNAL;
import static org.mockito.Mockito.mock;

public abstract class AbstractActivationTest {

    TypeRegistry typeRegistry;
    NodeDefinition nodeDef;
    Model model;
    Neuron neuron;
    Document doc;

    @BeforeEach
    public void init() {
        typeRegistry = mock(TypeRegistry.class);
        nodeDef = new NodeDefinition(typeRegistry, "test");

        nodeDef.activation.initFlattenedType();
        nodeDef.neuron.initFlattenedType();

        doc = mock(Document.class);
        model = mock(Model.class);

        neuron = nodeDef.neuron.instantiate(model);
    }
}
