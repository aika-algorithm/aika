package network.aika.elements.synapses;

import network.aika.elements.Type;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.enums.LinkingMode;
import network.aika.enums.Transition;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SynapseType {

    public Type inputType();

    public Type outputType();

    public Transition transition();

    public Transition[] required() default {};

    public Transition[] forbidden() default {};

    public Transition[] requiredSecondary() default {};

    public Transition[] forbiddenSecondary() default {};

    public Class<? extends Neuron> up() default PatternNeuron.class;

    public LinkingMode linkingMode() default LinkingMode.REGULAR;
}
