package network.aika.activations;


import network.aika.Document;
import network.aika.bindingsignal.BindingSignal;
import network.aika.neurons.Neuron;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static network.aika.activations.TestBSTypes.A;
import static network.aika.activations.TestBSTypes.B;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
public class ActivationTest extends AbstractActivationTest {


    @Test
    public void testHasConflictingBindingSignals() {
        Document doc = mock(Document.class);
        Neuron n = mock(Neuron.class);

        BindingSignal bs0 = new BindingSignal(0, doc);
        BindingSignal bs1 = new BindingSignal(1, doc);

        Activation act = new ConjunctiveActivation(
                null,
                null,
                1,
                n,
                doc,
                Map.of(A, bs0)
        );

        assertFalse(
                act.hasConflictingBindingSignals(
                        Map.of(A, bs0)
                )
        );

        assertFalse(
                act.hasConflictingBindingSignals(
                        Map.of(
                                A, bs0,
                                B, bs1
                        )
                )
        );

        assertTrue(
                act.hasConflictingBindingSignals(
                        Map.of(
                                A, bs1,
                                B, bs0
                        )
                )
        );
    }

    @Test
    public void testHasNewBindingSignals() {
        Document doc = mock(Document.class);
        Neuron n = mock(Neuron.class);

        BindingSignal bs0 = new BindingSignal(0, doc);
        BindingSignal bs1 = new BindingSignal(1, doc);

        Activation act = new ConjunctiveActivation(
                null,
                null,
                1,
                n,
                doc,
                Map.of(A, bs0)
        );

        assertTrue(
                act.hasNewBindingSignals(
                        Map.of(
                                A, bs0,
                                B, bs1
                        )
                )
        );

        assertFalse(
                act.hasNewBindingSignals(
                        Map.of(A, bs0)
                )
        );
    }

    @Test
    public void testBranch() {
        BindingSignal bs0 = new BindingSignal(0, doc);
        BindingSignal bs1 = new BindingSignal(1, doc);

        Activation parentAct = new ConjunctiveActivation(
                null,
                null,
                1,
                neuron,
                doc,
                Map.of(A, bs0)
        );

        Activation childAct = parentAct.branch(
                Map.of(B, bs1)
        );

        assertEquals(parentAct, childAct.getParent());
        assertEquals(1, childAct.getBindingSignals().size());
        assertEquals(bs1, childAct.getBindingSignal(B));
    }

    @Test
    public void testCollectLinkingTargets() {

    }

    @Test
    public void testLinkOutgoing() {

    }

    @Test
    public void testPropagate() {

    }
}
