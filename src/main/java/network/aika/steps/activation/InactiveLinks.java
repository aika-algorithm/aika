package network.aika.steps.activation;

import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.bindingsignal.BindingSignal;
import network.aika.steps.Step;

public class InactiveLinks extends Step<Activation> {

    private BindingSignal bindingSignal;

    public static void add(BindingSignal bs) {
        Step.add(new InactiveLinks(bs));
    }

    public InactiveLinks(BindingSignal bs) {
        super(bs.getActivation());
        bindingSignal = bs;
    }

    @Override
    public void process() {
        Activation act = getElement();
        Neuron n = act.getNeuron();

        n.addInactiveLinks(bindingSignal);
    }
}
